package kr.sparkweb.multiplatform.domain

import kr.sparkweb.multiplatform.util.MainCoroutineRule
import kr.sparkweb.multiplatform.util.MainCoroutineRule.Companion.runBlockingTest
import kr.sparkweb.multiplatform.util.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.yield
import kr.sparkweb.multiplatform.domain.UseCase
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class UseCaseTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var useCase: UseCase<String, String>

    private val parameter = ""

    @Test
    fun invokeUseCase() = mainCoroutineRule.runBlockingTest {
        useCase = TestUseCase()

        val result = useCase.invoke(it.coroutineContext, parameter)
        yield()
        assertEquals(
            Resource.success(parameter),
            result.value
        )
    }

    @Test
    fun invokeUseCaseWithMutableLiveData() = mainCoroutineRule.runBlockingTest {
        useCase = TestUseCase()

        val mutableResult = MutableStateFlow<Resource<String>>(Resource.loading(null))
        useCase.invoke(it.coroutineContext, parameter, mutableResult)
        yield()
        assertEquals(
            Resource.success(parameter),
            mutableResult.value
        )
    }

    @Test
    fun invokeUseCaseInstant() = mainCoroutineRule.runBlockingTest {
        useCase = TestUseCase()

        val result = useCase.invokeInstant(parameter)
        assertEquals(
            Resource.success(parameter),
            result
        )
    }

    @Test
    fun invokeUseCaseFailed() = mainCoroutineRule.runBlockingTest {
        useCase = TestFailingUseCase()

        val result = useCase.invoke(it.coroutineContext, parameter)
        yield()
        assertEquals(
            Resource.Status.ERROR,
            result.value.status
        )
    }

    @Test
    fun invokeUseCaseWithMutableLiveDataFailed() = mainCoroutineRule.runBlockingTest {
        useCase = TestFailingUseCase()

        val mutableResult = MutableStateFlow<Resource<String>>(Resource.loading(null))
        useCase.invoke(it.coroutineContext, parameter, mutableResult)
        yield()
        assertEquals(
            Resource.Status.ERROR,
            mutableResult.value.status
        )
    }

    @Test
    fun invokeUseCaseInstantFailed() = mainCoroutineRule.runBlockingTest {
        useCase = TestFailingUseCase()

        val result = useCase.invokeInstant(parameter)
        assertEquals(
            Resource.Status.ERROR,
            result.status
        )
    }

    class TestUseCase : ExceptionLoggingUseCase<String, String>() {
        override suspend fun execute(params: String): String {
            return params
        }
    }

    class TestFailingUseCase : ExceptionLoggingUseCase<String, String>() {
        override suspend fun execute(params: String): String {
            throw RuntimeException()
        }
    }

    abstract class ExceptionLoggingUseCase<Params, Result> : UseCase<Params, Result>() {
        override fun onException(exception: Throwable) {
            print(exception.toString())
        }
    }
}
