package com.flyn.flyn_resource_gen.init

import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.CreativeModeTabs
import net.minecraft.world.item.Item
import net.minecraftforge.registries.DeferredRegister
import thedarkcolour.kotlinforforge.forge.ObjectHolderDelegate
import thedarkcolour.kotlinforforge.forge.registerObject
import java.util.function.Supplier

object TabInit : Initializer<CreativeModeTab>(Registries.CREATIVE_MODE_TAB) {

    private val mainTabItems = mutableListOf<Supplier<out Item>>()

    fun <T: Item, U: T> DeferredRegister<T>.registerToMainTab(
        name: String,
        supplier: () -> U
    ): ObjectHolderDelegate<U> = this.registerObject(name, supplier).also {
        mainTabItems.add(it.registryObject)
    }

    @Suppress("UNUSED")
    val MAIN_TAB by register.registerObject("main_tab") {
        CreativeModeTab.builder().run {
            withTabsBefore(CreativeModeTabs.COMBAT)
            title(Component.translatable("itempGroup.flyn_resource_gen"))
            icon {
                ItemInit.RESOURCE_GEN_BLOCK_ITEM.defaultInstance
            }
            displayItems { _, output ->
                mainTabItems.forEach { output.accept(it.get()) }
            }
        }.build()
    }

}