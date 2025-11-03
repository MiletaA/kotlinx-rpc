package kotlinx.rpc.descriptor

import kotlinx.coroutines.flow.Flow
import kotlinx.rpc.annotations.Rpc
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@Rpc
interface NewsServiceForFlowTest {
    fun stream(): Flow<String>
    suspend fun greet(name: String): String
}

class IsFlowIntegrationTest {
    @Test
    fun stream_isFlow_and_returnType_is_Flow() {
        val descriptor = serviceDescriptorOf<NewsServiceForFlowTest>()
        val stream = descriptor.callables["stream"] ?: error("stream not found")

        assertEquals(typeOf<Flow<String>>().toString(), stream.returnType.kType.toString())
        assertTrue(stream.returnsFlow)
        assertTrue(stream.isNonSuspendFunction)
    }

    @Test
    fun greet_notFlow_and_returnType_is_String() {
        val descriptor = serviceDescriptorOf<NewsServiceForFlowTest>()
        val greet = descriptor.callables["greet"] ?: error("greet not found")

        assertEquals(typeOf<String>().toString(), greet.returnType.kType.toString())
        assertFalse(greet.returnsFlow)
        // greet is suspend now
        kotlin.test.assertFalse(greet.isNonSuspendFunction)
    }
}
