package com.xhlab.multiplatform.domain

import com.xhlab.multiplatform.util.MainCoroutineRule
import com.xhlab.multiplatform.util.MainCoroutineRule.Companion.runBlockingTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.yield
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import com.xhlab.multiplatform.util.Resource

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

    class TestMediatorUseCase : MediatorUseCase<String, String>() {
        override fun executeInternal(params: String): StateFlow<Resource<String>> {
            return MutableStateFlow(Resource.success(params))
        }

        override fun onExceptionWhileInvocation(e: Exception) = Unit
    }

    class TestFailingMediatorUseCase : MediatorUseCase<String, String>() {
        override fun executeInternal(params: String): StateFlow<Resource<String>> {
            throw RuntimeException()
        }

        override fun onExceptionWhileInvocation(e: Exception) = Unit
    }
}