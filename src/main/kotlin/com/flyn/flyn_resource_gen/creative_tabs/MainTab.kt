package com.flyn.flyn_resource_gen.creative_tabs

import com.flyn.flyn_resource_gen.FlynResourceGen
import com.flyn.flyn_resource_gen.blocks.ResourceGen
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.CreativeModeTabs
import thedarkcolour.kotlinforforge.forge.registerObject

object MainTab {

    val mainTab by FlynResourceGen.CREATIVE_MODE_TABS.registerObject("main_tab") {
        CreativeModeTab.builder().run {
            withTabsBefore(CreativeModeTabs.COMBAT)
            icon {
                ResourceGen.resourceGenBlockItem.defaultInstance
            }
            displayItems { _, output ->
                output.accept(ResourceGen.resourceGenBlockItem)
            }
        }.build()
    }

}