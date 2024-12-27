package com.flyn.flyn_resource_gen.config

import com.flyn.flyn_resource_gen.FlynResourceGen
import com.flyn.flyn_resource_gen.config.ResourceGenProperty.Companion.joinPropertyToString
import com.flyn.flyn_resource_gen.misc.ResourceGenType
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraftforge.common.ForgeConfigSpec
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.event.config.ModConfigEvent

@EventBusSubscriber(modid = FlynResourceGen.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
object Config {

    private val builder = ForgeConfigSpec.Builder()
    private val waterGeneratedBlocks = builder.run {
        comment("Generate block by water.")
        defineListAllowEmpty(
            "waterGenerateBlocks",
            listOf(
                Blocks.CLAY to ResourceGenProperty(Items.CLAY, 100, ResourceGenType.WATER)
            ).map { (block, property) -> block.joinPropertyToString(property) },
            ResourceGenProperty::validate
        )
    }
    private val lavaGeneratedBlocks = builder.run {
        comment("Generate block by lava.")
        defineListAllowEmpty(
            "lavaGenerateBlocks",
            listOf(
                Blocks.OBSIDIAN to ResourceGenProperty(Items.OBSIDIAN, 100, ResourceGenType.LAVA)
            ).map { (block, property) -> block.joinPropertyToString(property) },
            ResourceGenProperty::validate
        )
    }
    private val mixedGeneratedBlocks = builder.run {
        comment("Generate block by mix water and lava.")
        defineListAllowEmpty(
            "mixedGenerateBlocks",
            listOf(
                Blocks.COBBLESTONE to ResourceGenProperty(Items.COBBLESTONE, 10, ResourceGenType.MIXED)
            ).map { (block, property) -> block.joinPropertyToString(property) },
            ResourceGenProperty::validate
        )
    }

    val SPEC: ForgeConfigSpec = builder.build()

    var generatorProperty = emptyMap<Block, ResourceGenProperty>()
        private set

    @SubscribeEvent
    fun onLoad(event: ModConfigEvent) {
        generatorProperty += waterGeneratedBlocks.get().mapNotNull {
            ResourceGenProperty.deserialize(it, ResourceGenType.WATER)
        }
        generatorProperty += lavaGeneratedBlocks.get().mapNotNull {
            ResourceGenProperty.deserialize(it, ResourceGenType.LAVA)
        }
        generatorProperty += mixedGeneratedBlocks.get().mapNotNull {
            ResourceGenProperty.deserialize(it, ResourceGenType.MIXED)
        }
    }

}