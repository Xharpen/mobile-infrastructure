package com.xhlab.multiplatform.domain

import com.xhlab.multiplatform.util.MainCoroutineRule
import com.xhlab.multiplatform.util.MainCoroutineRule.Companion.runBlockingTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.yield
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import com.xhlab.multiplatform.util.Resource

@ExperimentalCoroutinesApi
class UseCaseTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var useCase: UseCase<String, String>

    private val parameter = ""

    @Test
    fun invokeUseCase() = mainCoroutineRule.runBlockingTest {
        useCase = TestUseCase()

        val result = useCase.invoke(it, parameter)
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
        useCase.invoke(it, parameter, mutableResult)
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

        val result = useCase.invoke(it, parameter)
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
        useCase.invoke(it, parameter, mutableResult)
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

    class TestUseCase : UseCase<String, String>() {
        override suspend fun execute(params: String): String {
            return params
        }

        override suspend fun onExceptionWhileInvocation(e: Exception) = Unit
    }

    class TestFailingUseCase : UseCase<String, String>() {
        override suspend fun execute(params: String): String {
            throw RuntimeException()
        }

        override suspend fun onExceptionWhileInvocation(e: Exception) = Unit
    }
}