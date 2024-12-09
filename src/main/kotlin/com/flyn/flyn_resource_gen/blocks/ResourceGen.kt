package com.flyn.flyn_resource_gen.blocks

import com.flyn.flyn_resource_gen.FlynResourceGen
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.material.MapColor
import thedarkcolour.kotlinforforge.forge.registerObject

object ResourceGen {

    val resourceGenBlock by FlynResourceGen.BLOCKS.registerObject("resource_gen") {
        Block(
            BlockBehaviour.Properties.of().apply {
                mapColor(MapColor.STONE)
            }
        )
    }

    val resourceGenBlockItem by FlynResourceGen.ITEMS.registerObject("resource_gen") {
        BlockItem(
            resourceGenBlock,
            Item.Properties()
        )
    }

}