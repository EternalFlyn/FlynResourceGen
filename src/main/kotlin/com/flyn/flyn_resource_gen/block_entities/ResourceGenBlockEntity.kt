package com.flyn.flyn_resource_gen.block_entities

import com.flyn.flyn_resource_gen.FlynResourceGen
import com.flyn.flyn_resource_gen.init.BlockEntityInit
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState

class ResourceGenBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(
    BlockEntityInit.RESOURCE_GEN_BLOCK_ENTITY, pos, state
) {

    override fun load(nbt: CompoundTag) {
        super.load(nbt)
        val data = nbt.getCompound(FlynResourceGen.MOD_ID)

    }

    override fun saveAdditional(nbt: CompoundTag) {
        super.saveAdditional(nbt)
        val data = CompoundTag()
        nbt.put(FlynResourceGen.MOD_ID, data)
    }

}