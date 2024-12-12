package com.flyn.flyn_resource_gen.blocks

import net.minecraft.util.StringRepresentable
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.level.block.state.properties.IntegerProperty
import java.util.*

class ResourceGenBlock(properties: Properties) : Block(properties) {

    enum class GeneratorFluid : StringRepresentable {

        EMPTY, WATER, LAVA, MIXED;

        override fun getSerializedName() = this.name.lowercase(Locale.getDefault())

    }

    companion object {

        const val BLOCK_NAME = "resource_gen_block"

        val TIER = IntegerProperty.create("tier", 1, 5)
        val FLUID = EnumProperty.create("fluid", GeneratorFluid::class.java)

    }

    init {
        registerDefaultState(defaultBlockState().setValue(TIER, 1))
        registerDefaultState(defaultBlockState().setValue(FLUID, GeneratorFluid.EMPTY))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(TIER)
        builder.add(FLUID)
    }

}