package com.flyn.flyn_resource_gen.creative_tabs

import com.flyn.flyn_resource_gen.FlynResourceGen
import com.flyn.flyn_resource_gen.blocks.ResourceGen
import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.CreativeModeTabs
import net.minecraft.world.item.Item
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.registries.DeferredRegister
import thedarkcolour.kotlinforforge.forge.ObjectHolderDelegate
import thedarkcolour.kotlinforforge.forge.registerObject
import java.util.function.Supplier

@EventBusSubscriber(modid = FlynResourceGen.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
object MainTab {

    private val tabItems = mutableListOf<Supplier<out Item>>()

    @Suppress("UNUSED")
    val mainTab by FlynResourceGen.CREATIVE_MODE_TABS.registerObject("main_tab") {
        CreativeModeTab.builder().run {
            withTabsBefore(CreativeModeTabs.COMBAT)
            title(Component.translatable("itempGroup.flyn_resource_gen"))
            icon {
                ResourceGen.resourceGenBlockItem.defaultInstance
            }
            displayItems { _, output ->
                tabItems.forEach { output.accept(it.get()) }
            }
        }.build()
    }

    fun <T: Item, U: T> DeferredRegister<T>.registerToTab(
        name: String,
        supplier: () -> U
    ): ObjectHolderDelegate<U> = this.registerObject(name, supplier).also {
        tabItems.add(it.registryObject)
    }

}