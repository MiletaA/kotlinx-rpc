@file:Suppress("detekt.MatchingDeclarationName")

/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.descriptor

import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.serialization.KSerializer
import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * KRPC-specific RpcType implementation that includes serialization information.
 * 
 * KRPC-178 RESOLUTION: This class remains in core for backward compatibility,
 * but the new [RpcTypeProvider] system should be used for custom descriptors.
 * Protocol-specific implementations can now register their own providers
 * using [RpcTypeProviderRegistry].
 * 
 * @see RpcTypeProvider for the extensible interface
 * @see RpcTypeProviderRegistry for registering custom providers
 */
@InternalRpcApi
public class RpcTypeKrpc(
    override val kType: KType,
    override val annotations: List<Annotation>,
    /**
     * Contains serializer instances from [kotlinx.serialization.Serializable.with] parameters from [annotations],
     * mapped by their [KClass].
     */
    public val serializers: Map<KClass<KSerializer<Any?>>, KSerializer<Any?>>,
) : RpcType {
    override fun toString(): String {
        return kType.toString()
    }
}
