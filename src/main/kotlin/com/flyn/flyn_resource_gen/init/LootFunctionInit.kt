package com.flyn.flyn_resource_gen.init

import com.flyn.flyn_resource_gen.loot_function.SetGenPropertyFunction
import net.minecraft.core.registries.Registries
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType
import thedarkcolour.kotlinforforge.forge.registerObject

object LootFunctionInit : Initializer<LootItemFunctionType>(Registries.LOOT_FUNCTION_TYPE) {

    val SET_GEN_PROPERTY by register.registerObject("set_gen_property") {
        LootItemFunctionType(SetGenPropertyFunction.Serializer)
    }

}