package com.flyn.flyn_resource_gen

import com.flyn.flyn_resource_gen.config.Config
import com.flyn.flyn_resource_gen.init.*
import com.flyn.flyn_resource_gen.render.ResourceGenUnbakedModel
import net.minecraftforge.client.event.ModelEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.config.ModConfig
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent
import org.apache.logging.log4j.LogManager
import thedarkcolour.kotlinforforge.forge.LOADING_CONTEXT
import thedarkcolour.kotlinforforge.forge.MOD_BUS
import thedarkcolour.kotlinforforge.forge.runForDist

@Mod(FlynResourceGen.MOD_ID)
@EventBusSubscriber(modid = FlynResourceGen.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
object FlynResourceGen {

    const val MOD_ID = "flyn_resource_gen"
    val LOGGER = LogManager.getLogger(MOD_ID)

    init {
        BlockInit.register()
        ItemInit.register()
        TabInit.register()
        BlockEntityInit.register()
        RecipeInit.register()
        LootFunctionInit.register()

        runForDist(
            clientTarget = {
                MOD_BUS.addListener(::onClientSetup)
                MOD_BUS.addListener(::onModelRegistry)
            }, serverTarget = {
                MOD_BUS.addListener(::onServerSetup)
            }
        )

        LOADING_CONTEXT.registerConfig(ModConfig.Type.COMMON, Config.SPEC)
    }

    @SubscribeEvent
    @Suppress("UNUSED_PARAMETER")
    fun onSetup(event: FMLCommonSetupEvent) {
        LOGGER.info("onSetup")
    }

    @SubscribeEvent
    @Suppress("UNUSED_PARAMETER")
    fun onLoadComplete(event: FMLLoadCompleteEvent) {
        LOGGER.info("Items: ${Config.generatorProperty}")
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onClientSetup(event: FMLClientSetupEvent) {
        // Some client setup code
        LOGGER.info("Initializing client...")
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onServerSetup(event: FMLDedicatedServerSetupEvent) {
        LOGGER.info("Server starting...")
    }

    private fun onModelRegistry(event : ModelEvent.RegisterGeometryLoaders) {
        event.register("resource_gen_block", ResourceGenUnbakedModel.Loader)
    }

}