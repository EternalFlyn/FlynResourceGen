package com.flyn.flyn_resource_gen.init

import com.flyn.flyn_resource_gen.FlynResourceGen.MOD_ID
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.IForgeRegistry
import thedarkcolour.kotlinforforge.forge.MOD_BUS

open class Initializer<T> {

    protected val register: DeferredRegister<T>

    constructor(reg: IForgeRegistry<T>) {
        register = DeferredRegister.create(reg, MOD_ID)
    }

    constructor(key: ResourceKey<Registry<T>>) {
        register = DeferredRegister.create(key, MOD_ID)
    }

    fun register() = register.register(MOD_BUS)

}