package com.flyn.flyn_resource_gen.blocks

import com.flyn.flyn_resource_gen.FlynResourceGen
import com.flyn.flyn_resource_gen.init.BlockEntityInit
import com.flyn.flyn_resource_gen.init.BlockInit
import net.minecraft.client.renderer.BiomeColors
import net.minecraft.core.BlockPos
import net.minecraft.util.StringRepresentable
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.level.block.state.properties.IntegerProperty
import net.minecraft.world.phys.BlockHitResult
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.RegisterColorHandlersEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import java.util.*

class ResourceGenBlock(properties: Properties) : Block(properties), EntityBlock {

    enum class GeneratorFluid : StringRepresentable {

        EMPTY, WATER, LAVA;

        override fun getSerializedName() = this.name.lowercase(Locale.getDefault())

    }

    @EventBusSubscriber(modid = FlynResourceGen.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
    companion object {

        const val BLOCK_NAME = "resource_gen_block"

        val TIER = IntegerProperty.create("tier", 1, 5)
        val FLUID_L = EnumProperty.create("fluid_l", GeneratorFluid::class.java)
        val FLUID_R = EnumProperty.create("fluid_r", GeneratorFluid::class.java)

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
        registerDefaultState(defaultBlockState().setValue(FLUID_L, GeneratorFluid.EMPTY))
        registerDefaultState(defaultBlockState().setValue(FLUID_R, GeneratorFluid.EMPTY))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(TIER)
        builder.add(FLUID_L)
        builder.add(FLUID_R)
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? {
        return BlockEntityInit.RESOURCE_GEN_BLOCK_ENTITY.create(pos, state)
    }

    override fun use(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        blockHitResult: BlockHitResult
    ): InteractionResult {
        fun changeFluid(target: EnumProperty<GeneratorFluid>, fluid: GeneratorFluid) {
            level.setBlock(pos, state.setValue(target, fluid), 3)
            when (state.getValue(target)) {
                GeneratorFluid.EMPTY -> player.setItemInHand(hand, ItemStack(Items.BUCKET))
                GeneratorFluid.WATER -> player.setItemInHand(hand, ItemStack(Items.WATER_BUCKET))
                GeneratorFluid.LAVA -> player.setItemInHand(hand, ItemStack(Items.WATER_BUCKET))
                null -> return
            }
        }
        val fluidL = state.getValue(FLUID_L)
        val fluidR = state.getValue(FLUID_R)
        when (player.mainHandItem.item) {
            Items.BUCKET -> {
                if (fluidR != GeneratorFluid.EMPTY) {
                    changeFluid(FLUID_R, GeneratorFluid.EMPTY)
                } else if (fluidL != GeneratorFluid.EMPTY) {
                    changeFluid(FLUID_L, GeneratorFluid.EMPTY)
                }
            }
            Items.WATER_BUCKET -> {
                if (fluidL == GeneratorFluid.EMPTY) {
                    changeFluid(FLUID_L, GeneratorFluid.WATER)
                } else if (fluidR == GeneratorFluid.EMPTY) {
                    changeFluid(FLUID_R, GeneratorFluid.WATER)
                }
            }
            Items.LAVA_BUCKET -> {
                if (fluidL == GeneratorFluid.EMPTY) {
                    changeFluid(FLUID_L, GeneratorFluid.LAVA)
                } else if (fluidR == GeneratorFluid.EMPTY) {
                    changeFluid(FLUID_R, GeneratorFluid.LAVA)
                }
            }
            else -> return InteractionResult.PASS
        }
        return InteractionResult.sidedSuccess(level.isClientSide)
    }

}