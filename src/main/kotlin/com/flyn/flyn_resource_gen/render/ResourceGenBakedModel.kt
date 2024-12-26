package com.flyn.flyn_resource_gen.render

import com.flyn.flyn_resource_gen.Config
import com.flyn.flyn_resource_gen.DEFAULT_ITEM_TRANSFORMS
import com.flyn.flyn_resource_gen.blocks.ResourceGenBlock.Companion.DEFAULT_ITEM
import com.flyn.flyn_resource_gen.blocks.ResourceGenBlock.Companion.DEFAULT_TIER
import com.flyn.flyn_resource_gen.init.BlockEntityInit
import com.flyn.flyn_resource_gen.misc.ResourceGenNbt
import com.flyn.flyn_resource_gen.misc.get
import com.flyn.flyn_resource_gen.misc.thisModTag
import com.google.common.cache.CacheBuilder
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.ItemOverrides
import net.minecraft.client.renderer.block.model.ItemTransforms
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.BlockAndTintGetter
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.Fluid
import net.minecraftforge.client.ChunkRenderTypeSet
import net.minecraftforge.client.model.BakedModelWrapper
import net.minecraftforge.client.model.IDynamicBakedModel
import net.minecraftforge.client.model.data.ModelData
import net.minecraftforge.client.model.data.ModelProperty
import java.util.*
import java.util.concurrent.TimeUnit

class ResourceGenBakedModel(
    private val frameModels: Map<Int, BakedModel?>,
    private val fluidModels: Map<Fluid, BakedModel?>,
    private val oppositeFluidModels: Map<Fluid, BakedModel?>,
    private val productModels: Map<Item, BakedModel?>
) : IDynamicBakedModel {

    override fun getQuads(
        state: BlockState?, side: Direction?, rand: RandomSource, extraData: ModelData, renderType: RenderType?
    ): List<BakedQuad> {
        val tier = extraData[TIER] ?: DEFAULT_TIER
        val product = extraData[PRODUCT] ?: DEFAULT_ITEM
        val key = getBakedKey(tier, product, side)
        // return the cache if exist
        BAKED_QUADS_CACHE.getIfPresent(key)?.run { return this }
        // construct the baked quads if the hash is not exist
        // define the utility function for add the quads with the baked model
        fun MutableList<BakedQuad>.addModel(model: BakedModel?) {
            if (model == null)
                return
            this.addAll(model.getQuads(state, side, rand, ModelData.EMPTY, renderType))
        }
        val result = mutableListOf<BakedQuad>()
        result.addModel(frameModels[tier])
        Config.canGenerateBlocks[product]?.let { type ->
            result.addModel(fluidModels[type.fluidL.fluid])
            result.addModel(oppositeFluidModels[type.fluidR.fluid])
            result.addModel(productModels[product])
        }
        BAKED_QUADS_CACHE.put(key, result)
        return result
    }

    private fun getBakedKey(tier: Int, item: Item, side: Direction?): BakedKey {
        return BakedKey(tier, item, side?.run { get3DDataValue() + 1 } ?: 0)
    }

    override fun useAmbientOcclusion(): Boolean = false

    override fun getParticleIcon(): TextureAtlasSprite {
        return Minecraft.getInstance().modelManager.missingModel.getParticleIcon(ModelData.EMPTY)
    }

    override fun getParticleIcon(data: ModelData): TextureAtlasSprite {
        val tier = data[TIER] ?: return particleIcon
        return frameModels[tier]?.getParticleIcon(ModelData.EMPTY) ?: particleIcon
    }

    override fun isGui3d(): Boolean = true

    override fun usesBlockLight(): Boolean = true

    override fun isCustomRenderer(): Boolean = false

    override fun getOverrides(): ItemOverrides = RESOURCE_GEN_ITEM_OVERRIDES

    override fun getModelData(
        level: BlockAndTintGetter, pos: BlockPos, state: BlockState, modelData: ModelData
    ): ModelData {
        return level.getBlockEntity(pos, BlockEntityInit.RESOURCE_GEN_BLOCK_ENTITY).map { blockEntity ->
            val builder = ModelData.builder()
            builder.with(TIER, blockEntity.tier)
            builder.with(PRODUCT, blockEntity.product)
            builder.build()
        }.orElse(ModelData.EMPTY)
    }

    override fun getTransforms(): ItemTransforms = DEFAULT_ITEM_TRANSFORMS

    override fun getRenderTypes(state: BlockState, rand: RandomSource, data: ModelData): ChunkRenderTypeSet {
        return ChunkRenderTypeSet.of(RenderType.cutout(), RenderType.translucent())
    }

    companion object {

        private val TIER = ModelProperty<Int>()
        private val PRODUCT = ModelProperty<Item>()

        private val BAKED_QUADS_CACHE =
            CacheBuilder.newBuilder().expireAfterAccess(15L, TimeUnit.MINUTES).build<BakedKey, List<BakedQuad>>()
        private val RESOLVE_MODEL_CACHE =
            CacheBuilder.newBuilder().expireAfterAccess(1L, TimeUnit.MINUTES).build<Int, BakedModel>()

        private val RESOURCE_GEN_ITEM_OVERRIDES = object : ItemOverrides() {

            override fun resolve(
                model: BakedModel, stack: ItemStack, level: ClientLevel?, entity: LivingEntity?, seed: Int
            ): BakedModel {
                val tier = stack.thisModTag.get(ResourceGenNbt.Tier).coerceAtLeast(1)
                val product = stack.thisModTag.get(ResourceGenNbt.Product)
                val hash = Objects.hash(tier, product)
                RESOLVE_MODEL_CACHE.getIfPresent(hash)?.run { return this }
                ResolveModel(model as ResourceGenBakedModel, tier, product).let {
                    RESOLVE_MODEL_CACHE.put(hash, it)
                    return it
                }
            }

        }

    }

    private class ResolveModel(
        model: ResourceGenBakedModel,
        val tier: Int,
        val product: Item
    ) : BakedModelWrapper<ResourceGenBakedModel>(model) {

        override fun getQuads(state: BlockState?, side: Direction?, rand: RandomSource): List<BakedQuad> {
            val data = ModelData.builder().apply {
                with(TIER, tier)
                with(PRODUCT, product)
            }.build()
            return originalModel.getQuads(state, side, rand, data, null)
        }

        override fun getRenderPasses(itemStack: ItemStack, fabulous: Boolean): MutableList<BakedModel> {
            return mutableListOf(this)
        }

        override fun applyTransform(
            transformType: ItemDisplayContext,
            poseStack: PoseStack,
            applyLeftHandTransform: Boolean
        ): BakedModel {
            if (transformType == ItemDisplayContext.NONE)
                return this
            DEFAULT_ITEM_TRANSFORMS.getTransform(transformType).apply(applyLeftHandTransform, poseStack)
            return this
        }

    }

    private data class BakedKey(val tier: Int, val product: Item, val side: Int)

}