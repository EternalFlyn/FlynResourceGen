package com.flyn.flyn_resource_gen.block_entities

import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker

fun interface TickableBlockEntity {
    fun tick()
}

fun <T : BlockEntity?> getTickerHelper(level: Level): BlockEntityTicker<T>? {
    return if (level.isClientSide) null
    else BlockEntityTicker { _, _, _, blockEntity ->
        if (blockEntity is TickableBlockEntity) {
            blockEntity.tick()
        }
    }
}