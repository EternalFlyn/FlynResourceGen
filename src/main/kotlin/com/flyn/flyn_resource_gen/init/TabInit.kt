package com.flyn.flyn_resource_gen.init

import com.flyn.flyn_resource_gen.items.ResourceGenItem
import com.flyn.flyn_resource_gen.misc.allResourceGen
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.CreativeModeTabs
import thedarkcolour.kotlinforforge.forge.registerObject

object TabInit : Initializer<CreativeModeTab>(Registries.CREATIVE_MODE_TAB) {

    @Suppress("UNUSED")
    val MAIN_TAB by register.registerObject("main_tab") {
        CreativeModeTab.builder().run {
            withTabsBefore(CreativeModeTabs.COMBAT)
            title(Component.translatable("itempGroup.flyn_resource_gen"))
            icon {
                ItemInit.RESOURCE_GEN_BLOCK_ITEM.defaultInstance
            }
            displayItems { _, output ->
                val items = allResourceGen(ResourceGenItem::getItemStack)
                output.acceptAll(items)
            }
        }.build()
    }

}