package kr.sparkweb.multiplatform.processor

import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.javapoet.toJTypeName
import com.squareup.kotlinpoet.metadata.classinspectors.ElementsClassInspector
import com.squareup.kotlinpoet.metadata.specs.toTypeSpec
import kr.sparkweb.multiplatform.annotation.ProvideWithDagger
import dagger.Module
import dagger.Provides
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

class Processor : AbstractProcessor() {

    override fun process(
        elements: MutableSet<out TypeElement>?,
        roundEnv: RoundEnvironment
    ): Boolean {
        val modulePackageName = processingEnv.options["modulePackage"] ?: ""

        val providingClasses = mutableMapOf<ClassName, TypeSpec>()
        val moduleNames = mutableMapOf<String, ArrayList<ClassName>>()

        val classInspector = ElementsClassInspector.create(
            processingEnv.elementUtils,
            processingEnv.typeUtils
        )

        for (element in roundEnv.getElementsAnnotatedWith(ProvideWithDagger::class.java)) {
            val moduleName = element.getAnnotation(ProvideWithDagger::class.java).moduleName

            if (moduleName.isBlank()) {
                error("Module name cannot be blank")
                return false
            }

            // check annotated element is a class
            if (element.kind != ElementKind.CLASS) {
                error("Annotated type is not class : ${element.simpleName}")
                return false
            }

            val className = ClassName(
                processingEnv.elementUtils.getPackageOf(element).toString(),
                element.simpleName.toString()
            )
            val classSpec = (element as TypeElement).toTypeSpec(classInspector)

            // check theres only 1 constructor
            val primaryConstructorCount = if (classSpec.primaryConstructor != null) 1 else 0
            val constructors = classSpec.funSpecs.count { it.isConstructor }
            if (primaryConstructorCount + constructors != 1) {
                error("Can't decide how to instantiate annotated class : ${className.simpleName}")
                return false
            }

            if (providingClasses.containsKey(className)) {
                error("Duplicated providing classes : ${className.simpleName}")
                return false
            }

            providingClasses[className] = classSpec
            moduleNames[moduleName]?.add(className) ?: run {
                moduleNames[moduleName] = arrayListOf(className)
            }
        }

        // prevent overwriting by processing rounds
        if (providingClasses.isEmpty()) {
            return true
        }

        moduleNames.forEach { (moduleName, classNames) ->
            val classes = providingClasses.filter { classNames.contains(it.key) }
            JavaFile.builder(modulePackageName,
                com.squareup.javapoet.TypeSpec.classBuilder("${moduleName}Module")
                    .addAnnotation(Module::class.java)
                    .addModifiers(Modifier.PUBLIC)
                    .addMethods(classes.toMethods())
                    .build())
                .build()
                .writeTo(processingEnv.filer)
        }

        return true
    }

    override fun getSupportedAnnotationTypes() = mutableSetOf(
        ProvideWithDagger::class.java.canonicalName
    )

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }

    private fun Map<ClassName, TypeSpec>.toMethods() = map { (className, spec) ->
        className.generateProvidingMethod(spec)
    }

    private fun ClassName.generateProvidingMethod(spec: TypeSpec): MethodSpec {
        val constructor = spec.primaryConstructor
            ?: spec.funSpecs.first { it.isConstructor }

        val initStatement = run {
            with (StringBuilder("return new \$N(")) {
                val size = constructor.parameters.size
                for ((index, params) in constructor.parameters.withIndex()) {
                    append(params.name)
                    if (index != size - 1) {
                        append(", ")
                    }
                }
                append(")")
            }.toString()
        }

        return MethodSpec.methodBuilder("provide$simpleName")
            .addAnnotation(Provides::class.java)
            .addModifiers(Modifier.PUBLIC)
            .addParameters(constructor.parameters.map { it.toJavaParameterSpec() })
            .returns(this.toJTypeName())
            .addStatement(initStatement, simpleName)
            .build()
    }

    private fun ParameterSpec.toJavaParameterSpec() = com.squareup.javapoet.ParameterSpec
        .builder(type.toJTypeName(), name)
        .build()

    private fun error(message: String) {
        processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, message)
    }
}
