/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.extension

import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.types.IrType

/**
 * Extension point for generating RpcType creation calls in the compiler plugin.
 * This allows different protocols to generate their own type descriptors.
 * 
 * Part of KRPC-178: General Approach for custom Service Descriptors
 */
internal interface RpcTypeProviderExtension {
    /**
     * Generates an IR call to create an RpcType instance for the given type.
     * 
     * @param type The IR type to create a descriptor for
     * @param ctx The RPC IR context
     * @return An IR expression that creates the appropriate RpcType instance
     */
    fun generateRpcTypeCall(type: IrType, ctx: RpcIrContext): IrExpression
    
    /**
     * Checks if this extension can handle the given type.
     * 
     * @param type The IR type to check
     * @return true if this extension should be used for this type
     */
    fun canHandle(type: IrType): Boolean
}

/**
 * Registry for RpcType provider extensions.
 * Allows registration of custom extensions for different protocols.
 */
internal object RpcTypeProviderExtensionRegistry {
    private val extensions = mutableListOf<RpcTypeProviderExtension>()
    private var defaultExtension: RpcTypeProviderExtension? = null
    
    /**
     * Registers a new extension.
     */
    fun register(extension: RpcTypeProviderExtension) {
        extensions.add(extension)
    }
    
    /**
     * Sets the default extension to use when no other extension can handle a type.
     */
    fun setDefault(extension: RpcTypeProviderExtension) {
        defaultExtension = extension
    }
    
    /**
     * Gets the appropriate extension for the given type.
     */
    fun getExtension(type: IrType): RpcTypeProviderExtension? {
        return extensions.firstOrNull { it.canHandle(type) } ?: defaultExtension
    }
    
    /**
     * Clears all registered extensions (useful for testing).
     */
    internal fun clear() {
        extensions.clear()
        defaultExtension = null
    }
}
