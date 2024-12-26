package com.flyn.flyn_resource_gen.render

import com.flyn.flyn_resource_gen.Config
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonObject
import com.mojang.datafixers.util.Either
import net.minecraft.client.renderer.block.model.BlockModel
import net.minecraft.client.renderer.block.model.ItemOverrides
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.resources.model.*
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.inventory.InventoryMenu
import net.minecraft.world.item.Item
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.material.Fluids
import net.minecraftforge.client.model.geometry.IGeometryBakingContext
import net.minecraftforge.client.model.geometry.IGeometryLoader
import net.minecraftforge.client.model.geometry.IUnbakedGeometry
import java.util.function.Function

class ResourceGenUnbakedModel(
    private val property: UnbakedProperty
) : IUnbakedGeometry<ResourceGenUnbakedModel> {

    override fun bake(
        context: IGeometryBakingContext?,
        baker: ModelBaker,
        spriteGetter: Function<Material, TextureAtlasSprite>,
        modelState: ModelState,
        overrides: ItemOverrides?,
        modelLocation: ResourceLocation
    ): BakedModel {
        val frameModels = getTierFrame(baker, spriteGetter, modelState, modelLocation, property)
        val fluidModels = getFluid(baker, spriteGetter, modelState, modelLocation, property)
        val oppositeFluidModels = getFluid(baker, spriteGetter, BlockModelRotation.X0_Y180, modelLocation, property)
        val productModels = getProduct(baker, spriteGetter, modelState, modelLocation, property)
        return ResourceGenBakedModel(frameModels, fluidModels, oppositeFluidModels, productModels)
    }

    private fun ResourceLocation.toMaterial() = Material(InventoryMenu.BLOCK_ATLAS, this)

    private fun getTierFrame(
        baker: ModelBaker,
        spriteGetter: Function<Material, TextureAtlasSprite>,
        modelState: ModelState,
        modelLocation: ResourceLocation,
        property: UnbakedProperty
    ): Map<Int, BakedModel?> {
        var index = 0
        return property.tierTextures.associate { texture ->
            ++index to baker.getModel(property.frameLocation).apply {
                if (this is BlockModel) {
                    textureMap[MATERIAL] = Either.left(texture.toMaterial())
                    textureMap[PARTICLE] = Either.left(texture.toMaterial())
                }
            }.bake(baker, spriteGetter, modelState, modelLocation)
        }
    }

    private fun getFluid(
        baker: ModelBaker,
        spriteGetter: Function<Material, TextureAtlasSprite>,
        modelState: ModelState,
        modelLocation: ResourceLocation,
        property: UnbakedProperty
    ): Map<Fluid, BakedModel?> {
        return mapOf(
            Fluids.WATER to baker.getModel(property.waterLocation)
                .bake(baker, spriteGetter, modelState, modelLocation),
            Fluids.LAVA to baker.getModel(property.lavaLocation)
                .bake(baker, spriteGetter, modelState, modelLocation)
        )
    }

    private fun getProduct(
        baker: ModelBaker,
        spriteGetter: Function<Material, TextureAtlasSprite>,
        modelState: ModelState,
        modelLocation: ResourceLocation,
        property: UnbakedProperty
    ): Map<Item, BakedModel?> {
        return Config.canGenerateBlocks.keys.associate { item ->
            item to baker.getModel(property.productLocation).apply {
                if (this is BlockModel) {
                    val loc = ResourceLocation("block/${item}")
                    textureMap[MATERIAL] = Either.left(loc.toMaterial())
                }
            }.bake(baker, spriteGetter, modelState, modelLocation)
        }
    }

    companion object Loader : IGeometryLoader<ResourceGenUnbakedModel> {

        private const val MATERIAL = "material"
        private const val PARTICLE = "particle"

        override fun read(
            jsonObject: JsonObject?,
            deserializationContext: JsonDeserializationContext?
        ): ResourceGenUnbakedModel {
            var frameLocation = ""
            var waterLocation = ""
            var lavaLocation = ""
            var productLocation = ""
            var tierTextures = emptyList<String>()
            jsonObject!!.entrySet().forEach {
                when (it.key) {
                    "frame_model" -> {
                        frameLocation = it.value.asString
                    }
                    "fluid_model" -> {
                        waterLocation = it.value.asJsonObject["water"].asString
                        lavaLocation = it.value.asJsonObject["lava"].asString
                    }
                    "product_model" -> productLocation = it.value.asString
                    "tier_textures" -> {
                        tierTextures = it.value.asJsonArray.toList().map { element -> element.asString }
                    }
                }
            }
            return ResourceGenUnbakedModel(
                UnbakedProperty(frameLocation, waterLocation, lavaLocation, productLocation, tierTextures)
            )
        }

    }

    class UnbakedProperty(
        frameLocation: String,
        waterLocation: String,
        lavaLocation: String,
        productLocation: String,
        tierTextures: List<String>
    ) {
        val frameLocation = ResourceLocation(frameLocation)
        val waterLocation = ResourceLocation(waterLocation)
        val lavaLocation = ResourceLocation(lavaLocation)
        val productLocation = ResourceLocation(productLocation)
        val tierTextures = tierTextures.map { ResourceLocation(it) }
    }

}