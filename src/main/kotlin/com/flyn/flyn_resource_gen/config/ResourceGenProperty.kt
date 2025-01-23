package com.flyn.flyn_resource_gen.config

import com.flyn.flyn_resource_gen.misc.ResourceGenType
import net.minecraft.ResourceLocationException
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraftforge.registries.ForgeRegistries

data class ResourceGenProperty(
    val product: Item,
    val interval: Int,
    val type: ResourceGenType,
) {

    companion object {

        const val DEFAULT_TIER = 1
        const val MAX_TIER = 6
        val TIER_RANGE = DEFAULT_TIER..MAX_TIER
        val EMPTY_CORE: Block = Blocks.AIR
        private val YIELD = Array(MAX_TIER) { index -> 1 shl index }
        private val SLOT_LIMIT = Array(MAX_TIER) { index -> 64 shl index shl index }

        private fun getBlock(name: String): Block? {
            try {
                val loc = ResourceLocation(name)
                return ForgeRegistries.BLOCKS.getValue(loc)
                    .takeIf { it?.asItem() is BlockItem && it.asItem() != Items.AIR }
            } catch (exp: ResourceLocationException) {
                return null
            }
        }

        private fun getItem(name: String): Item? {
            try {
                val loc = ResourceLocation(name)
                return ForgeRegistries.ITEMS.getValue(loc)
                    .takeUnless { it == Items.AIR }
            } catch (exp: ResourceLocationException) {
                return null
            }
        }

        fun validate(obj: Any): Boolean {
            if (obj !is String)
                return false
            obj.split(",").map { it.trim() }.run {
                if (size !in 2..3)
                    return false
                getBlock(this[0]) ?: return false
                this[1].toIntOrNull() ?: return false
                if (size == 3)
                    getItem(this[2]) ?: return false
            }
            return true
        }

        fun deserialize(obj: Any, type: ResourceGenType): Pair<Block, ResourceGenProperty> {
            (obj as String).split(",").map { it.trim() }.run {
                val block = getBlock(this[0])!!
                val interval = this[1].toInt().coerceAtLeast(10)
                return if (size < 3) {
                    block to ResourceGenProperty(block.asItem(), interval, type)
                } else {
                    val replace = getItem(this[2])!!
                    block to ResourceGenProperty(replace, interval, type)
                }
            }
        }

        fun Block.joinPropertyToString(property: ResourceGenProperty): String {
            val block = ForgeRegistries.BLOCKS.getKey(this)
            return if (this.asItem() == property.product)
                "$block, ${property.interval}"
            else {
                val replace = ForgeRegistries.ITEMS.getKey(property.product)
                "$block, ${property.interval}, $replace"
            }
        }

        fun getYield(tier: Int): Int {
            return if (tier in TIER_RANGE) YIELD[tier - 1]
            else YIELD[0]
        }

        fun getSlotLimit(tier: Int): Int {
            return if (tier in TIER_RANGE) SLOT_LIMIT[tier - 1]
            else SLOT_LIMIT[0]
        }

    }

}
