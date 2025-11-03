/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.descriptor

import kotlinx.rpc.annotations.Rpc
import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlin.reflect.KType

/**
 * Interface for providing custom RpcType implementations.
 * This allows different protocols (e.g., kRPC, gRPC) to define their own
 * type descriptors without coupling the core module to specific serialization libraries.
 * 
 * TODO KRPC-178: This is the foundation for the custom Service Descriptors approach.
 */
@ExperimentalRpcApi
public interface RpcTypeProvider {
    /**
     * Creates an RpcType instance for the given KType and annotations.
     * 
     * @param kType The Kotlin type to create a descriptor for
     * @param annotations Type-level annotations
     * @param metadata Additional protocol-specific metadata (e.g., serialization info)
     * @return An RpcType instance appropriate for this provider
     */
    public fun createRpcType(
        kType: KType,
        annotations: List<Annotation>,
        metadata: Map<String, Any?> = emptyMap()
    ): RpcType
    
    /**
     * Checks if this provider can handle the given type and annotations.
     * 
     * @param kType The Kotlin type to check
     * @param annotations Type-level annotations
     * @return true if this provider should be used for this type
     */
    public fun canHandle(
        kType: KType,
        annotations: List<Annotation>
    ): Boolean
}

/**
 * Registry for RpcType providers.
 * Allows registration of custom type providers for different protocols.
 */
@ExperimentalRpcApi
public object RpcTypeProviderRegistry {
    private val providers = mutableListOf<RpcTypeProvider>()
    private var defaultProvider: RpcTypeProvider = DefaultRpcTypeProvider
    
    /**
     * Registers a new RpcType provider.
     * Providers are checked in the order they were registered.
     */
    public fun register(provider: RpcTypeProvider) {
        providers.add(provider)
    }
    
    /**
     * Sets the default provider to use when no other provider can handle a type.
     */
    public fun setDefault(provider: RpcTypeProvider) {
        defaultProvider = provider
    }
    
    /**
     * Creates an RpcType for the given KType and annotations using the appropriate provider.
     */
    public fun createRpcType(
        kType: KType,
        annotations: List<Annotation>,
        metadata: Map<String, Any?> = emptyMap()
    ): RpcType {
        val provider = providers.firstOrNull { it.canHandle(kType, annotations) } ?: defaultProvider
        return provider.createRpcType(kType, annotations, metadata)
    }
    
    /**
     * Clears all registered providers (useful for testing).
     */
    internal fun clear() {
        providers.clear()
        defaultProvider = DefaultRpcTypeProvider
    }
}

/**
 * Default RpcType provider that creates RpcTypeDefault instances.
 */
@ExperimentalRpcApi
internal object DefaultRpcTypeProvider : RpcTypeProvider {
    override fun createRpcType(
        kType: KType,
        annotations: List<Annotation>,
        metadata: Map<String, Any?>
    ): RpcType {
        return RpcTypeDefault(kType, annotations)
    }
    
    override fun canHandle(kType: KType, annotations: List<Annotation>): Boolean = true
}
