package com.flyn.flyn_resource_gen.init

import com.flyn.flyn_resource_gen.recipes.ResourceGenRecipe
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer
import net.minecraftforge.registries.ForgeRegistries
import thedarkcolour.kotlinforforge.forge.registerObject

object RecipeInit : Initializer<RecipeSerializer<*>>(ForgeRegistries.RECIPE_SERIALIZERS) {

    val RESOURCE_GEN by register.registerObject("crafting_resource_gen") {
        SimpleCraftingRecipeSerializer(::ResourceGenRecipe)
    }

}