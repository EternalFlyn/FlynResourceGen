package com.flyn.flyn_resource_gen.compat

import com.flyn.flyn_resource_gen.FlynResourceGen.MOD_ID
import com.flyn.flyn_resource_gen.init.ItemInit
import com.flyn.flyn_resource_gen.misc.thisModTag
import com.flyn.flyn_resource_gen.recipes.ResourceGenRecipe
import mezz.jei.api.IModPlugin
import mezz.jei.api.JeiPlugin
import mezz.jei.api.constants.RecipeTypes
import mezz.jei.api.constants.VanillaTypes
import mezz.jei.api.registration.IRecipeRegistration
import mezz.jei.api.registration.ISubtypeRegistration
import net.minecraft.resources.ResourceLocation

@JeiPlugin
class ResourceGenJeiPlugin : IModPlugin {

    override fun getPluginUid(): ResourceLocation {
        return ResourceLocation(MOD_ID, "jei_plugin")
    }

    override fun registerRecipes(registration: IRecipeRegistration) {
        registration.addRecipes(RecipeTypes.CRAFTING, ResourceGenRecipe.getRecipes())
    }

    override fun registerItemSubtypes(registration: ISubtypeRegistration) {
        registration.registerSubtypeInterpreter(
            VanillaTypes.ITEM_STACK, ItemInit.RESOURCE_GEN_BLOCK_ITEM
        ) { stack, _ -> stack.thisModTag.toString() }
    }

}