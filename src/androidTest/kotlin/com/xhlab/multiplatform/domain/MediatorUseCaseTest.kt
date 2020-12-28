package com.xhlab.multiplatform.domain

import com.xhlab.multiplatform.util.MainCoroutineRule
import com.xhlab.multiplatform.util.MainCoroutineRule.Companion.runBlockingTest
import com.xhlab.multiplatform.util.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.yield
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class MediatorUseCaseTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var useCase: MediatorUseCase<String, String>

    private val parameter = "param1"
    private val parameter2 = "param2"

    @Test
    fun executeSuccessfully() = mainCoroutineRule.runBlockingTest {
        useCase = TestMediatorUseCase()

        val result = useCase.observe()
        useCase.execute(it, parameter)
        yield()
        assertEquals(
            Resource.success(parameter),
            result.value
        )
    }

    @Test
    fun executeFailed() = mainCoroutineRule.runBlockingTest {
        useCase = TestFailingMediatorUseCase()

        val result = useCase.observe()
        useCase.execute(it, parameter)
        yield()
        assertEquals(
            Resource.Status.ERROR,
            result.value.status
        )
    }

    @Test
    fun executeMultiple() = mainCoroutineRule.runBlockingTest {
        useCase = TestMediatorUseCase()

        val result = useCase.observe()
        useCase.execute(it, parameter)
        yield()
        useCase.execute(it, parameter2)
        yield()
        assertEquals(
            Resource.success(parameter2),
            result.value
        )
    }

    class TestMediatorUseCase : ExceptionLoggingMediatorUseCase<String, String>() {
        override suspend fun executeInternal(coroutineScope: CoroutineScope, params: String): Flow<Resource<String>> {
            return MutableStateFlow(Resource.success(params))
        }
    }

    class TestFailingMediatorUseCase : ExceptionLoggingMediatorUseCase<String, String>() {
        override suspend fun executeInternal(coroutineScope: CoroutineScope, params: String): Flow<Resource<String>> {
            throw RuntimeException()
        }
    }

    abstract class ExceptionLoggingMediatorUseCase<Params, Result> : MediatorUseCase<Params, Result>() {
        override fun onException(exception: Throwable) {
            print(exception.toString())
        }
    }
}