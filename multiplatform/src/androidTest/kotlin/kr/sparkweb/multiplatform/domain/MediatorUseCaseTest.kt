package kr.sparkweb.multiplatform.domain

import kr.sparkweb.multiplatform.util.MainCoroutineRule
import kr.sparkweb.multiplatform.util.MainCoroutineRule.Companion.runBlockingTest
import kr.sparkweb.multiplatform.util.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.yield
import kr.sparkweb.multiplatform.domain.MediatorUseCase
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class MediatorUseCaseTest {

    private val dispatcher = TestCoroutineDispatcher()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule(dispatcher)

    private lateinit var useCase: MediatorUseCase<String, String>

    private val parameter = "param1"
    private val parameter2 = "param2"

    @Test
    fun executeSuccessfully() = mainCoroutineRule.runBlockingTest {
        useCase = TestMediatorUseCase()

        val result = useCase.observe()
        useCase.execute(dispatcher, parameter)
        yield()
        assertEquals(
            Resource.success(results.last()),
            result.value
        )
    }

    @Test
    fun executeFailed() = mainCoroutineRule.runBlockingTest {
        useCase = TestFailingMediatorUseCase()

        val result = useCase.observe()
        useCase.execute(dispatcher, parameter)
        yield()
        assertEquals(
            Resource.Status.ERROR,
            result.value.status
        )
    }

    @Test
    fun executeMultiple() = mainCoroutineRule.runBlockingTest {
        useCase = TestMultipleMediatorUseCase()

        val result = useCase.observe()
        useCase.execute(dispatcher, parameter)
        yield()
        useCase.execute(dispatcher, parameter2)
        yield()

        assertEquals(
            Resource.success(parameter2),
            result.value
        )
    }

    class TestMediatorUseCase : ExceptionLoggingMediatorUseCase<String, String>() {
        override suspend fun executeInternal(params: String): Flow<Resource<String>> {
            return results.asFlow().transform { emit(Resource.success(it)) }
        }
    }

    class TestMultipleMediatorUseCase : ExceptionLoggingMediatorUseCase<String, String>() {
        override suspend fun executeInternal(params: String): Flow<Resource<String>> {
            return MutableStateFlow(Resource.success(params))
        }
    }

    class TestFailingMediatorUseCase : ExceptionLoggingMediatorUseCase<String, String>() {
        override suspend fun executeInternal(params: String): Flow<Resource<String>> {
            throw RuntimeException()
        }
    }

    abstract class ExceptionLoggingMediatorUseCase<Params, Result> : MediatorUseCase<Params, Result>() {
        override fun onException(exception: Throwable) {
            print(exception.toString())
        }
    }

    companion object {
        private val results = arrayOf("1", "2", "3")
    }
}
