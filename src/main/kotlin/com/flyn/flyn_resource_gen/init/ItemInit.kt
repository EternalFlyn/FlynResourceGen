package com.flyn.flyn_resource_gen.init

import com.flyn.flyn_resource_gen.blocks.ResourceGenBlock
import com.flyn.flyn_resource_gen.init.TabInit.registerToMainTab
import com.flyn.flyn_resource_gen.items.ResourceGenItem
import net.minecraft.world.item.Item
import net.minecraftforge.registries.ForgeRegistries

object ItemInit : Initializer<Item>(ForgeRegistries.ITEMS) {

    val RESOURCE_GEN_BLOCK_ITEM by register.registerToMainTab(ResourceGenBlock.BLOCK_NAME) { ResourceGenItem() }

}