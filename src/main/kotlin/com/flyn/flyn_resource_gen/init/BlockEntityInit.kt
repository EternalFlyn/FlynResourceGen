package com.flyn.flyn_resource_gen.init

import com.flyn.flyn_resource_gen.block_entities.ResourceGenBlockEntity
import com.flyn.flyn_resource_gen.blocks.ResourceGenBlock
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraftforge.registries.ForgeRegistries
import thedarkcolour.kotlinforforge.forge.registerObject

object BlockEntityInit : Initializer<BlockEntityType<*>>(ForgeRegistries.BLOCK_ENTITY_TYPES) {

    val RESOURCE_GEN_BLOCK_ENTITY: BlockEntityType<ResourceGenBlockEntity>
            by register.registerObject(ResourceGenBlock.BLOCK_NAME) {
                BlockEntityType.Builder.of(::ResourceGenBlockEntity, BlockInit.RESOURCE_GEN_BLOCK).build(null)
            }

}