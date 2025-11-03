/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.descriptor

import kotlinx.rpc.descriptor.RpcTypeKrpc
import kotlinx.rpc.descriptor.RpcType
import kotlinx.rpc.descriptor.RpcTypeProvider
import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * RpcType provider for KRPC protocol that handles types with serialization annotations.
 */
@OptIn(InternalRpcApi::class)
@ExperimentalRpcApi
public class KrpcRpcTypeProvider : RpcTypeProvider {
    override fun createRpcType(
        kType: KType,
        annotations: List<Annotation>,
        metadata: Map<String, Any?>
    ): RpcType {
        // Extract serializers from metadata if present
        @Suppress("UNCHECKED_CAST")
        val serializers = metadata["serializers"] as? Map<KClass<KSerializer<Any?>>, KSerializer<Any?>> 
            ?: emptyMap()
        
        return RpcTypeKrpc(kType, annotations, serializers)
    }
    
    override fun canHandle(kType: KType, annotations: List<Annotation>): Boolean {
        // Handle types with @Serializable annotation
        return annotations.any { annotation ->
            // Check if the annotation is @Serializable
            // We check the string representation as a fallback for cross-platform compatibility
            annotation is Serializable || 
            annotation.toString().contains("kotlinx.serialization.Serializable")
        }
    }
}

/**
 * Registers the KRPC type provider when this module is loaded.
 * This should be called during module initialization.
 */
@OptIn(ExperimentalRpcApi::class)
public fun registerKrpcTypeProvider() {
    kotlinx.rpc.descriptor.RpcTypeProviderRegistry.register(KrpcRpcTypeProvider())
}
