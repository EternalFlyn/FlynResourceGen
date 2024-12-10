package com.flyn.flyn_resource_gen.blocks

import com.flyn.flyn_resource_gen.FlynResourceGen
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.material.MapColor
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import thedarkcolour.kotlinforforge.forge.registerObject

@EventBusSubscriber(modid = FlynResourceGen.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
object ResourceGen {

    const val BLOCK_NAME = "resource_gen_block"

    val resourceGenBlock by FlynResourceGen.BLOCKS.registerObject(BLOCK_NAME) {
        Block(
            BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).apply {
                mapColor(MapColor.STONE)
                lightLevel { 10 }
            }
        )
    }

    val resourceGenBlockItem by FlynResourceGen.ITEMS.registerObject(BLOCK_NAME) {
        BlockItem(
            resourceGenBlock,
            Item.Properties()
        )
    }

}