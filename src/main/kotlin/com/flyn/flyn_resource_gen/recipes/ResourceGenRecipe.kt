package com.flyn.flyn_resource_gen.recipes

import com.flyn.flyn_resource_gen.Config
import com.flyn.flyn_resource_gen.FlynResourceGen.MOD_ID
import com.flyn.flyn_resource_gen.init.ItemInit
import com.flyn.flyn_resource_gen.init.RecipeInit
import com.flyn.flyn_resource_gen.items.ResourceGenItem
import com.flyn.flyn_resource_gen.misc.*
import net.minecraft.core.NonNullList
import net.minecraft.core.RegistryAccess
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.ItemTags
import net.minecraft.world.inventory.CraftingContainer
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.CraftingBookCategory
import net.minecraft.world.item.crafting.CraftingRecipe
import net.minecraft.world.item.crafting.CustomRecipe
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.ShapedRecipe
import net.minecraft.world.level.Level
import net.minecraftforge.common.Tags

class ResourceGenRecipe(
    location: ResourceLocation, category: CraftingBookCategory
) : CustomRecipe(location, category) {

    companion object {

        private val FRAME_POS = arrayOf(0, 1, 2, 6, 8)
        private val GLASS_POS = 4
        private val FLUID_POS_L = 3
        private val FLUID_POS_R = 5
        private val CORE_POS = 7

        // for JEI
        fun getRecipes(): List<CraftingRecipe> {
            return allResourceGen { product, tier ->
                genRecipe(product, tier)
            }.filterNotNull()
        }

        // for JEI
        private fun genRecipe(product: Item, tier: Int): ShapedRecipe? {
            val type = Config.canGenerateBlocks[product] ?: return null
            val ingredients = NonNullList.create<Ingredient>()
            val frameMaterial = getFrameMaterial(tier)
            for (pos in 0..8) {
                when (pos) {
                    in FRAME_POS -> ingredients.add(frameMaterial)
                    FLUID_POS_L -> ingredients.add(Ingredient.of(type.fluidL.fluid.bucket))
                    GLASS_POS -> ingredients.add(Ingredient.of(Items.GLASS))
                    FLUID_POS_R -> ingredients.add(Ingredient.of(type.fluidR.fluid.bucket))
                    CORE_POS -> ingredients.add(getCoreMaterial(product, tier))
                }
            }
            return ShapedRecipe(
                ResourceLocation(MOD_ID, "${product}_gen_tier_$tier"),
                "", CraftingBookCategory.REDSTONE, 3, 3, ingredients,
                ResourceGenItem.getItemStack(product, tier)
            )
        }

        private fun getFrameMaterial(tier: Int): Ingredient {
            return when (tier) {
                2 -> Ingredient.of(Items.COBBLESTONE)
                3 -> Ingredient.of(Items.IRON_BLOCK)
                4 -> Ingredient.of(Items.GOLD_BLOCK)
                5 -> Ingredient.of(Items.DIAMOND_BLOCK)
                6 -> Ingredient.of(Items.NETHERITE_BLOCK)
                else -> Ingredient.of(ItemTags.LOGS)
            }
        }

        private fun getCoreMaterial(product: Item, tier: Int): Ingredient {
            return if (tier <= 1) Ingredient.of(product)
            else Ingredient.of(ResourceGenItem.getItemStack(product, tier - 1))
        }

    }

    private fun List<ItemStack>.isFrameMatch(predicate: (ItemStack) -> Boolean): Boolean {
        for (i in FRAME_POS) {
            if (!predicate(get(i)))
                return false
        }
        return true
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
            this[FLUID_POS_L].`is`(type.fluidL.fluid.bucket) && this[FLUID_POS_R].`is`(type.fluidR.fluid.bucket)
        } ?: false
    }

    override fun matches(container: CraftingContainer, level: Level): Boolean {
        with(container.items) {
            if (!this[GLASS_POS].`is`(Tags.Items.GLASS))
                return false
            val core = this[CORE_POS]
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
        with (container.items[CORE_POS]) {
            if (`is`(ItemInit.RESOURCE_GEN_BLOCK_ITEM)) {
                tier = thisModTag.get(ResourceGenNbt.Tier) + 1
                product = thisModTag.get(ResourceGenNbt.Product)
            } else {
                product = item
            }
        }
        return ResourceGenItem.getItemStack(product, tier)
    }

    override fun canCraftInDimensions(width: Int, height: Int): Boolean {
        return width == 3 && height == 3
    }

    override fun getSerializer(): RecipeSerializer<*> = RecipeInit.RESOURCE_GEN

}