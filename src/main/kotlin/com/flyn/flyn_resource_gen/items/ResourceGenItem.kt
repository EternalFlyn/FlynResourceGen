package com.flyn.flyn_resource_gen.items

import com.flyn.flyn_resource_gen.init.BlockInit
import com.flyn.flyn_resource_gen.misc.ResourceGenNbt
import com.flyn.flyn_resource_gen.misc.get
import com.flyn.flyn_resource_gen.misc.thisModTag
import net.minecraft.network.chat.Component
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

class ResourceGenItem : BlockItem(BlockInit.RESOURCE_GEN_BLOCK, Properties()) {

    override fun getName(stack: ItemStack): Component {
        val item = stack.thisModTag.get(ResourceGenNbt.Product)
            .takeUnless { it == Items.AIR }
            ?: run { return super.getName(stack)  }
        val tier = stack.thisModTag.get(ResourceGenNbt.Tier).coerceAtLeast(1)
        return Component.translatable(
            "item.flyn_resource_gen.resource_gen_block_variant",
            Component.translatable(item.descriptionId),
            tier
        )
    }

}