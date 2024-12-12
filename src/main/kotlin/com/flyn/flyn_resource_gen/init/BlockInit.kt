package com.flyn.flyn_resource_gen.init

import com.flyn.flyn_resource_gen.blocks.ResourceGenBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.material.MapColor
import net.minecraftforge.registries.ForgeRegistries
import thedarkcolour.kotlinforforge.forge.registerObject

object BlockInit : Initializer<Block>(ForgeRegistries.BLOCKS) {

    val RESOURCE_GEN_BLOCK by register.registerObject(ResourceGenBlock.BLOCK_NAME) {
        ResourceGenBlock(
            BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).apply {
                mapColor(MapColor.STONE)
                lightLevel { 10 }
            }
        )
    }

}