package com.flyn.flyn_resource_gen.blocks

import com.flyn.flyn_resource_gen.FlynResourceGen
import com.flyn.flyn_resource_gen.block_entities.ResourceGenBlockEntity
import com.flyn.flyn_resource_gen.block_entities.getTickerHelper
import com.flyn.flyn_resource_gen.init.BlockEntityInit.RESOURCE_GEN_BLOCK_ENTITY
import com.flyn.flyn_resource_gen.init.BlockInit
import com.flyn.flyn_resource_gen.misc.ResourceGenNbt
import com.flyn.flyn_resource_gen.misc.get
import com.flyn.flyn_resource_gen.misc.put
import com.flyn.flyn_resource_gen.misc.thisModTag
import net.minecraft.client.renderer.BiomeColors
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.RegisterColorHandlersEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber

class ResourceGenBlock(properties: Properties) : Block(properties), EntityBlock,
    AdditionDropData<ResourceGenBlockEntity> {

    @EventBusSubscriber(modid = FlynResourceGen.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
    companion object {

        const val BLOCK_NAME = "resource_gen_block"
        const val DEFAULT_TIER = 1
        const val MAX_TIER = 6
        val EMPTY_CORE: Block = Blocks.AIR

        @SubscribeEvent
        fun registerWaterColor(event: RegisterColorHandlersEvent.Block) {
            event.register({ _, world, pos, index ->
                if (world == null || pos == null || index != 1)
                    return@register -1
                BiomeColors.getAverageWaterColor(world, pos)
            }, BlockInit.RESOURCE_GEN_BLOCK)
        }

    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? {
        return RESOURCE_GEN_BLOCK_ENTITY.create(pos, state)
    }

    override fun attack(state: BlockState, level: Level, pos: BlockPos, player: Player) {
        if (level.isClientSide)
            return
        // crouching to get the content, max to 1 stack
        if (player.isCrouching) {
            level.getBlockEntity(pos, RESOURCE_GEN_BLOCK_ENTITY).ifPresent {
                val stack = it.getProduct()
                player.inventory.add(stack)
            }
        }
    }

    override fun neighborChanged(
        state: BlockState, level: Level, pos: BlockPos, block: Block, fromPos: BlockPos, isMoving: Boolean
    ) {
        if (fromPos == pos.above()) {
            level.getBlockEntity(pos, RESOURCE_GEN_BLOCK_ENTITY).ifPresent {
                it.updateUpContainer()
            }
        }
    }

    override fun <T : BlockEntity?> getTicker(
        level: Level, state: BlockState, type: BlockEntityType<T>
    ): BlockEntityTicker<T>? = getTickerHelper(level)

    override fun setPlacedBy(
        level: Level, pos: BlockPos, state: BlockState, placer: LivingEntity?, stack: ItemStack
    ) {
        level.getBlockEntity(pos, RESOURCE_GEN_BLOCK_ENTITY).ifPresent { blockEntity ->
            blockEntity.tier = stack.thisModTag.get(ResourceGenNbt.Tier)
            blockEntity.core = stack.thisModTag.get(ResourceGenNbt.Core)
        }
    }

    override fun addDropData(stack: ItemStack, blockEntity: ResourceGenBlockEntity): ItemStack {
        stack.thisModTag.apply {
            put(ResourceGenNbt.Tier, blockEntity.tier)
            put(ResourceGenNbt.Core, blockEntity.core)
        }
         return stack
    }

}