package com.flyn.flyn_resource_gen.recipes

import com.flyn.flyn_resource_gen.Config
import com.flyn.flyn_resource_gen.init.ItemInit
import com.flyn.flyn_resource_gen.init.RecipeInit
import com.flyn.flyn_resource_gen.misc.ResourceGenNbt
import com.flyn.flyn_resource_gen.misc.get
import com.flyn.flyn_resource_gen.misc.put
import com.flyn.flyn_resource_gen.misc.thisModTag
import com.flyn.flyn_resource_gen.setTag
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
        return Config.canGenerateBlocks[item]?.let { type ->
            this[3].`is`(type.fluidL.fluid.bucket) && this[5].`is`(type.fluidR.fluid.bucket)
        } ?: false
    }

    override fun matches(container: CraftingContainer, level: Level): Boolean {
        with(container.items) {
            if (!this[4].`is`(Tags.Items.GLASS))
                return false
            val core = this[7]
            // the core is the resource generator
            if (core.`is`(ItemInit.RESOURCE_GEN_BLOCK_ITEM)) {
                val tier = core.thisModTag.get(ResourceGenNbt.Tier)
                val product = core.thisModTag.get(ResourceGenNbt.Product)
                return isFrameMatch(tier) && isFluidMatch(product)
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
                tier = thisModTag.get(ResourceGenNbt.Tier) + 1
                product = thisModTag.get(ResourceGenNbt.Product)
            } else {
                product = item
            }
        }
        return ItemStack(ItemInit.RESOURCE_GEN_BLOCK_ITEM).setTag {
            val data = CompoundTag().apply {
                put(ResourceGenNbt.Tier, tier)
                put(ResourceGenNbt.Product, product)
            }
            thisModTag = data
        }
    }

    override fun canCraftInDimensions(width: Int, height: Int): Boolean {
        return width == 3 && height == 3
    }

    override fun getSerializer(): RecipeSerializer<*> = RecipeInit.RESOURCE_GEN

}