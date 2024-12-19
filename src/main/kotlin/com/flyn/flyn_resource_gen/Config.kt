package com.flyn.flyn_resource_gen

import com.flyn.flyn_resource_gen.misc.ResourceGenType
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.minecraftforge.common.ForgeConfigSpec
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.event.config.ModConfigEvent
import net.minecraftforge.registries.ForgeRegistries

@EventBusSubscriber(modid = FlynResourceGen.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
object Config {

    private val builder = ForgeConfigSpec.Builder()
    private val waterGeneratedBlocks = builder.run {
        comment("Generate block by water.")
        defineListAllowEmpty(
            "waterGenerateBlocks",
            listOf("minecraft:clay"),
            ::validateBlockName
        )
    }
    private val lavaGeneratedBlocks = builder.run {
        comment("Generate block by lava.")
        defineListAllowEmpty(
            "lavaGenerateBlocks",
            listOf("minecraft:obsidian"),
            ::validateBlockName
        )
    }
    private val mixedGeneratedBlocks = builder.run {
        comment("Generate block by mix water and lava.")
        defineListAllowEmpty(
            "mixedGenerateBlocks",
            listOf("minecraft:cobblestone"),
            ::validateBlockName
        )
    }
    val SPEC: ForgeConfigSpec = builder.build()

    lateinit var canGenerateBlocks: Map<Item, ResourceGenType>
        private set

    private fun validateBlockName(obj: Any): Boolean {
        return obj is String && ForgeRegistries.BLOCKS.containsKey(ResourceLocation(obj))
    }

    @SubscribeEvent
    fun onLoad(event: ModConfigEvent) {
        val canGenerateBlocks = mutableMapOf<Item, ResourceGenType>()
        canGenerateBlocks += waterGeneratedBlocks.labelGenerateProp(ResourceGenType.WATER)
        canGenerateBlocks += lavaGeneratedBlocks.labelGenerateProp(ResourceGenType.LAVA)
        canGenerateBlocks += mixedGeneratedBlocks.labelGenerateProp(ResourceGenType.MIXED)
        this.canGenerateBlocks = canGenerateBlocks
    }

    private fun ForgeConfigSpec.ConfigValue<MutableList<out String>>.labelGenerateProp(
        type: ResourceGenType
    ): Map<Item, ResourceGenType> {
        return get().mapNotNull {
            ForgeRegistries.BLOCKS
                .getValue(ResourceLocation(it))
                ?.takeUnless { block -> block.asItem() == Items.AIR }
        }.associate {
            it.asItem() to type
        }
    }

}