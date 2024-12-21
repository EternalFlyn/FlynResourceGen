package com.flyn.flyn_resource_gen.blocks

import com.flyn.flyn_resource_gen.Config
import com.flyn.flyn_resource_gen.FlynResourceGen
import com.flyn.flyn_resource_gen.block_entities.ResourceGenBlockEntity
import com.flyn.flyn_resource_gen.block_entities.getTickerHelper
import com.flyn.flyn_resource_gen.init.BlockEntityInit.RESOURCE_GEN_BLOCK_ENTITY
import com.flyn.flyn_resource_gen.init.BlockInit
import com.flyn.flyn_resource_gen.misc.*
import net.minecraft.client.renderer.BiomeColors
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.level.block.state.properties.IntegerProperty
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.RegisterColorHandlersEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber

class ResourceGenBlock(properties: Properties) : Block(properties), EntityBlock,
    AdditionDropData<ResourceGenBlockEntity> {

    @EventBusSubscriber(modid = FlynResourceGen.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
    companion object {

        const val BLOCK_NAME = "resource_gen_block"
        const val MAX_TIER = 6

        val TIER = IntegerProperty.create("tier", 1, MAX_TIER)
        val FLUID_L = EnumProperty.create("fluid_l", ResourceGenFluid::class.java)
        val FLUID_R = EnumProperty.create("fluid_r", ResourceGenFluid::class.java)

        @SubscribeEvent
        fun registerWaterColor(event: RegisterColorHandlersEvent.Block) {
            event.blockColors.register({ _, world, pos, _ ->
                if (world == null || pos == null) return@register 0xFFFFFF
                world.getBlockTint(pos, BiomeColors.WATER_COLOR_RESOLVER)
            }, BlockInit.RESOURCE_GEN_BLOCK)
        }

    }

    init {
        registerDefaultState(defaultBlockState().setValue(TIER, 1))
        registerDefaultState(defaultBlockState().setValue(FLUID_L, ResourceGenFluid.EMPTY))
        registerDefaultState(defaultBlockState().setValue(FLUID_R, ResourceGenFluid.EMPTY))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(TIER)
        builder.add(FLUID_L)
        builder.add(FLUID_R)
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
                FlynResourceGen.LOGGER.info("take $stack")
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

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
        var result = defaultBlockState()
        with(context.itemInHand.thisModTag) {
            get(ResourceGenNbt.Tier)
                .coerceAtLeast(1)
                .let { result = result.setValue(TIER, it) }
            get(ResourceGenNbt.Product)
                .let { Config.canGenerateBlocks[it] }
                ?.run {
                    result = result.setValue(FLUID_L, fluidL)
                    result = result.setValue(FLUID_R, fluidR)
                }
        }
        return result
    }

    override fun setPlacedBy(
        level: Level, pos: BlockPos, state: BlockState, placer: LivingEntity?, stack: ItemStack
    ) {
        level.getBlockEntity(pos, RESOURCE_GEN_BLOCK_ENTITY).ifPresent { blockEntity ->
            val product = stack.thisModTag.get(ResourceGenNbt.Product)
            blockEntity.product = product
        }
    }

    override fun addDropData(stack: ItemStack, blockEntity: ResourceGenBlockEntity): ItemStack {
        stack.thisModTag.apply {
            put(ResourceGenNbt.Tier, blockEntity.tier)
            put(ResourceGenNbt.Product, blockEntity.product)
        }
         return stack
    }

}