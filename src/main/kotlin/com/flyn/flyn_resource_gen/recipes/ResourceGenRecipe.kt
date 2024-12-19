package com.flyn.flyn_resource_gen.recipes

import com.flyn.flyn_resource_gen.*
import com.flyn.flyn_resource_gen.FlynResourceGen.MOD_ID
import com.flyn.flyn_resource_gen.init.ItemInit
import com.flyn.flyn_resource_gen.init.RecipeInit
import com.flyn.flyn_resource_gen.misc.ResourceGenType
import net.minecraft.core.RegistryAccess
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.ItemTags
import net.minecraft.world.inventory.CraftingContainer
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.CraftingBookCategory
import net.minecraft.world.item.crafting.CustomRecipe
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.level.Level
import net.minecraftforge.common.Tags

class ResourceGenRecipe(
    location: ResourceLocation, category: CraftingBookCategory
) : CustomRecipe(location, category) {

    private fun List<ItemStack>.isFrameMatch(predicate: (ItemStack) -> Boolean): Boolean {
        return predicate(get(0)) && predicate(get(1)) && predicate(get(2))
                && predicate(get(6)) && predicate(get(8))
    }

    private fun List<ItemStack>.isFrameMatch(tier: Int): Boolean {
        return when (tier) {
            0 -> isFrameMatch { it.`is`(ItemTags.LOGS) }
            1 -> isFrameMatch { it.`is`(Items.COBBLESTONE) }
            2 -> isFrameMatch { it.`is`(Items.IRON_BLOCK) }
            3 -> isFrameMatch { it.`is`(Items.GOLD_BLOCK) }
            4 -> isFrameMatch { it.`is`(Items.DIAMOND_BLOCK) }
            5 -> isFrameMatch { it.`is`(Items.NETHERITE_BLOCK) }
            else -> false
        }
    }

    private fun List<ItemStack>.isFluidMatch(item: Item): Boolean {
        return when (Config.canGenerateBlocks[item]) {
            ResourceGenType.WATER -> {
                this[3].`is`(Items.WATER_BUCKET) && this[5].`is`(Items.WATER_BUCKET)
            }
            ResourceGenType.LAVA -> {
                this[3].`is`(Items.LAVA_BUCKET) && this[5].`is`(Items.LAVA_BUCKET)
            }
            ResourceGenType.MIXED -> {
                this[3].`is`(Items.WATER_BUCKET) && this[5].`is`(Items.LAVA_BUCKET)
            }
            null -> false
        }
    }

    override fun matches(container: CraftingContainer, level: Level): Boolean {
        with(container.items) {
            if (!this[4].`is`(Tags.Items.GLASS))
                return false
            val core = this[7]
            // the core is the resource generator
            if (core.`is`(ItemInit.RESOURCE_GEN_BLOCK_ITEM)) {
                try {
                    val tier = core.tag!!.getCompound(MOD_ID).getInt("tier")
                    val product = core.tag!!.getCompound(MOD_ID).getItemId("product")
                    return isFrameMatch(tier) && isFluidMatch(product)
                } catch (e: Exception) {
                    return false
                }
            }
            // the core can be generated
            if (Config.canGenerateBlocks.containsKey(core.item)) {
                return isFrameMatch(0) && isFluidMatch(core.item)
            }
            return false
        }
    }

    override fun assemble(container: CraftingContainer, access: RegistryAccess): ItemStack {
        var tier = 1
        val product: Item
        with (container.items[7]) {
            if (`is`(ItemInit.RESOURCE_GEN_BLOCK_ITEM)) {
                tier = tag!!.getCompound(MOD_ID).getInt("tier") + 1
                product = tag!!.getCompound(MOD_ID).getItemId("product")
            } else {
                product = item
            }
        }
        return ItemStack(ItemInit.RESOURCE_GEN_BLOCK_ITEM).setTag {
            val data = CompoundTag().apply {
                putInt("tier", tier)
                putItemId("product", product)
            }
            put(MOD_ID, data)
        }
    }

    override fun canCraftInDimensions(width: Int, height: Int): Boolean {
        return width == 3 && height == 3
    }

    override fun getSerializer(): RecipeSerializer<*> = RecipeInit.RESOURCE_GEN

}