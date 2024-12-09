package com.flyn.flyn_resource_gen.creative_tabs

import com.flyn.flyn_resource_gen.FlynResourceGen
import com.flyn.flyn_resource_gen.blocks.ResourceGen
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.CreativeModeTabs
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import thedarkcolour.kotlinforforge.forge.registerObject

@EventBusSubscriber(modid = FlynResourceGen.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
object MainTab {

    @Suppress("UNUSED")
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