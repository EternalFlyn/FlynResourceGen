package com.flyn.flyn_resource_gen.loot_function

import com.flyn.flyn_resource_gen.block_entities.ResourceGenBlockEntity
import com.flyn.flyn_resource_gen.blocks.ResourceGenBlock
import com.flyn.flyn_resource_gen.init.LootFunctionInit
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonObject
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.storage.loot.LootContext
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition

class SetGenPropertyFunction(conditionsIn: Array<out LootItemCondition>) : LootItemConditionalFunction(conditionsIn) {

    companion object Serializer : LootItemConditionalFunction.Serializer<SetGenPropertyFunction>() {

        override fun deserialize(
            obj: JsonObject, context: JsonDeserializationContext, conditionsIn: Array<out LootItemCondition>
        ): SetGenPropertyFunction {
            return SetGenPropertyFunction(conditionsIn)
        }

    }

    override fun getType(): LootItemFunctionType = LootFunctionInit.SET_GEN_PROPERTY

    override fun run(stack: ItemStack, context: LootContext): ItemStack {
        val state = context.getParamOrNull(LootContextParams.BLOCK_STATE)
            .takeIf { it?.block is ResourceGenBlock }
            ?: run { return stack }
        val blockEntity = context.getParamOrNull(LootContextParams.BLOCK_ENTITY)
            .takeIf { it is ResourceGenBlockEntity }
            ?: run { return stack }
        return (state.block as ResourceGenBlock).addDropData(stack, blockEntity as ResourceGenBlockEntity)
    }

}