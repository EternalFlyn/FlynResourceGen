package com.flyn.flyn_resource_gen.init

import com.flyn.flyn_resource_gen.Config
import com.flyn.flyn_resource_gen.blocks.ResourceGenBlock
import com.flyn.flyn_resource_gen.misc.ResourceGenNbt
import com.flyn.flyn_resource_gen.misc.put
import com.flyn.flyn_resource_gen.misc.thisModTag
import com.flyn.flyn_resource_gen.setTag
import net.minecraft.core.registries.Registries
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.CreativeModeTabs
import net.minecraft.world.item.ItemStack
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
                val items = mutableListOf<ItemStack>()
                for (i in 1..ResourceGenBlock.MAX_TIER) {
                    items += Config.canGenerateBlocks.keys.map {
                        ItemStack(ItemInit.RESOURCE_GEN_BLOCK_ITEM, 1).setTag {
                            val data = CompoundTag().apply {
                                put(ResourceGenNbt.Tier, i)
                                put(ResourceGenNbt.Product, it)
                            }
                            thisModTag = data
                        }
                    }
                }
                output.acceptAll(items)
            }
        }.build()
    }

}