package com.flyn.flyn_resource_gen.misc

import com.flyn.flyn_resource_gen.Config
import com.flyn.flyn_resource_gen.FlynResourceGen.MOD_ID
import com.flyn.flyn_resource_gen.blocks.ResourceGenBlock
import com.flyn.flyn_resource_gen.getItemId
import com.flyn.flyn_resource_gen.misc.ResourceGenNbt.*
import com.flyn.flyn_resource_gen.putItemId
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.StringRepresentable
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.material.Fluids
import java.util.*

enum class ResourceGenType(
    val fluidL: ResourceGenFluid,
    val fluidR: ResourceGenFluid
) {

    WATER(
        fluidL = ResourceGenFluid.WATER,
        fluidR = ResourceGenFluid.WATER
    ),
    LAVA(
        fluidL = ResourceGenFluid.LAVA,
        fluidR = ResourceGenFluid.LAVA
    ),
    MIXED(
        fluidL = ResourceGenFluid.WATER,
        fluidR = ResourceGenFluid.LAVA
    )

}

enum class ResourceGenFluid(val fluid: Fluid) : StringRepresentable {

    WATER(Fluids.WATER),
    LAVA(Fluids.LAVA);

    override fun getSerializedName() = this.name.lowercase(Locale.getDefault())
}

sealed class ResourceGenNbt<T>(val name: String) {

    data object Tier : ResourceGenNbt<Int>("tier")
    data object Count : ResourceGenNbt<Int>("count")
    data object Product : ResourceGenNbt<Item>("product")

}

var CompoundTag.thisModTag: CompoundTag
    get() = this.getCompound(MOD_ID)
    set(tag) {
        this.put(MOD_ID, tag)
    }

var ItemStack.thisModTag: CompoundTag
    get() = this.getOrCreateTagElement(MOD_ID)
    set(tag) {
        if (this.tag == null)
            this.tag = CompoundTag()
        this.tag!!.put(MOD_ID, tag)
    }

fun <T> CompoundTag.put(tag: ResourceGenNbt<T>, value: T) {
    when (value) {
        is Int -> this.putInt(tag.name, value)
        is Item -> this.putItemId(tag.name, value)
    }
}

@Suppress("UNCHECKED_CAST")
fun <T> CompoundTag.get(tag: ResourceGenNbt<T>): T {
    val result: Any = when (tag) {
        Tier, Count -> getInt(tag.name)
        Product -> getItemId(tag.name)
    }
    return result as T
}

fun <T> allResourceGen(block: (Item, Int) -> T): List<T> {
    val result = mutableListOf<T>()
    for (i in 1..ResourceGenBlock.MAX_TIER) {
        Config.canGenerateBlocks.keys.forEach {
            result.add(block(it, i))
        }
    }
    return result
}