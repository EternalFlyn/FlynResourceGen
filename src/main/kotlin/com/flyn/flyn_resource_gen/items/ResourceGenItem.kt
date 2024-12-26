package com.flyn.flyn_resource_gen.items

import com.flyn.flyn_resource_gen.FlynResourceGen
import com.flyn.flyn_resource_gen.init.BlockInit
import com.flyn.flyn_resource_gen.init.ItemInit
import com.flyn.flyn_resource_gen.misc.ResourceGenNbt
import com.flyn.flyn_resource_gen.misc.get
import com.flyn.flyn_resource_gen.misc.thisModTag
import net.minecraft.network.chat.Component
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.RegisterColorHandlersEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber

class ResourceGenItem : BlockItem(BlockInit.RESOURCE_GEN_BLOCK, Properties()) {

    @EventBusSubscriber(modid = FlynResourceGen.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
    companion object {

        private const val PLAINS_WATER_COLOR = 4159204

        @SubscribeEvent
        fun registerWaterColor(event: RegisterColorHandlersEvent.Item) {
            event.register({ stack, index ->
                return@register if (index == 1) PLAINS_WATER_COLOR
                else -1
            }, ItemInit.RESOURCE_GEN_BLOCK_ITEM)
        }

    }

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