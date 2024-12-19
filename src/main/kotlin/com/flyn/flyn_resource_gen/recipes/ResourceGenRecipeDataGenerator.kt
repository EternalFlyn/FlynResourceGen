package com.flyn.flyn_resource_gen.recipes

import com.flyn.flyn_resource_gen.Config
import com.flyn.flyn_resource_gen.FlynResourceGen
import com.flyn.flyn_resource_gen.addArray
import com.flyn.flyn_resource_gen.addObject
import com.flyn.flyn_resource_gen.blocks.ResourceGenBlock
import com.flyn.flyn_resource_gen.misc.ResourceGenType
import com.google.gson.JsonObject
import net.minecraft.data.PackOutput
import net.minecraft.data.recipes.FinishedRecipe
import net.minecraft.data.recipes.RecipeProvider
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraftforge.data.event.GatherDataEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.registries.ForgeRegistries
import java.util.function.Consumer

@EventBusSubscriber(modid = FlynResourceGen.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
class ResourceGenRecipeDataGenerator(output: PackOutput) : RecipeProvider(output) {

    companion object {

        @SubscribeEvent
        fun recipeRegister(event: GatherDataEvent) {
            event.generator.addProvider(
                event.includeServer(),
                ::ResourceGenRecipeDataGenerator
            )
        }

    }

    override fun buildRecipes(writer: Consumer<FinishedRecipe>) {
        Config.canGenerateBlocks.forEach { (item, type) ->
            when (type) {
                ResourceGenType.WATER -> writer.accept(GenRecipeBuilder.WaterGen(item))
                ResourceGenType.LAVA -> writer.accept(GenRecipeBuilder.LavaGen(item))
                ResourceGenType.MIXED -> writer.accept(GenRecipeBuilder.MixedGen(item))
            }
        }
    }

    private sealed class GenRecipeBuilder(
        val resourceGenTag: String,
        val result: Item,
        val fluidL: ResourceGenBlock.GeneratorFluid,
        val fluidR: ResourceGenBlock.GeneratorFluid,
        val pattern: String,
        val keys: JsonObject.() -> Unit
    ) : FinishedRecipe {

        companion object {

            protected fun JsonObject.define(
                char: Char, item: Item
            ) = addObject(char.toString()) {
                addProperty("item", ForgeRegistries.ITEMS.getKey(item)?.toString())
            }

        }

        override fun serializeRecipeData(json: JsonObject) {
            json.addArray("pattern") {
                add("---")
                add(pattern)
                add("---")
            }
            json.addObject("key") {
                keys
                addObject("-") {
                    addProperty("tag", "minecraft:logs")
                }
            }
            json.addObject("result") {
                addProperty("item", "flyn_resource_gen:resource_gen_block")
                addObject("nbt") {
                    addObject(FlynResourceGen.MOD_ID) {
                        addProperty("tier", 1)
                        addProperty("product", ForgeRegistries.ITEMS.getKey(result)?.toString())
                        addProperty("fluidL", fluidL.serializedName)
                        addProperty("fluidR", fluidR.serializedName)
                    }
                }
            }
            FlynResourceGen.LOGGER.info(json)
        }

        override fun getId(): ResourceLocation {
            val name = ForgeRegistries.ITEMS.getKey(result).toString().replace(":", "_")
            return ResourceLocation(FlynResourceGen.MOD_ID, "${resourceGenTag}_$name")
        }

        override fun getType(): RecipeSerializer<*> = RecipeSerializer.SHAPED_RECIPE

        override fun serializeAdvancement(): JsonObject? = null

        override fun getAdvancementId(): ResourceLocation? = null

        class WaterGen(result: Item) : GenRecipeBuilder(
            resourceGenTag = "water_gen",
            result = result,
            fluidL = ResourceGenBlock.GeneratorFluid.WATER,
            fluidR = ResourceGenBlock.GeneratorFluid.WATER,
            pattern = "WXW",
            keys = {
                define('W', Items.WATER_BUCKET)
                define('X', result)
            }
        )

        class LavaGen(result: Item) : GenRecipeBuilder(
            resourceGenTag = "lava_gen",
            result = result,
            fluidL = ResourceGenBlock.GeneratorFluid.LAVA,
            fluidR = ResourceGenBlock.GeneratorFluid.LAVA,
            pattern = "LXL",
            keys = {
                define('L', Items.LAVA_BUCKET)
                define('X', result)
            }
        )

        class MixedGen(result: Item) : GenRecipeBuilder(
            resourceGenTag = "mixed_gen",
            result = result,
            fluidL = ResourceGenBlock.GeneratorFluid.WATER,
            fluidR = ResourceGenBlock.GeneratorFluid.LAVA,
            pattern = "WXL",
            keys = {
                define('W', Items.WATER_BUCKET)
                define('X', result)
                define('L', Items.LAVA_BUCKET)
            }
        )

    }

}