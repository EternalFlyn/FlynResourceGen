package com.flyn.flyn_resource_gen

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraftforge.common.ForgeConfigSpec
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.event.config.ModConfigEvent
import net.minecraftforge.registries.ForgeRegistries

@EventBusSubscriber(modid = FlynResourceGen.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
object Config {

    private val builder = ForgeConfigSpec.Builder()
    private val _canGenerateItems = builder.run {
        comment("Can generate items.")
        defineListAllowEmpty(
            "canGenerateItems",
            listOf("minecraft:cobblestone"),
            ::validateItemName
        )
    }
    val SPEC = builder.build()

    lateinit var canGenerateItems: Set<Item>
        private set

    private fun validateItemName(obj: Any): Boolean {
        return obj is String && ForgeRegistries.ITEMS.containsKey(ResourceLocation(obj))
    }

    @SubscribeEvent
    fun onLoad(event: ModConfigEvent) {
        canGenerateItems = _canGenerateItems.get().mapNotNull {
            ForgeRegistries.ITEMS.getValue(ResourceLocation(it))
        }.toSet()
    }

}