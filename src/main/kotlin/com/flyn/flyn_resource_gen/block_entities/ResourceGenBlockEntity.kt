package com.flyn.flyn_resource_gen.block_entities

import com.flyn.flyn_resource_gen.blocks.ResourceGenBlock
import com.flyn.flyn_resource_gen.init.BlockEntityInit
import com.flyn.flyn_resource_gen.misc.ResourceGenNbt
import com.flyn.flyn_resource_gen.misc.get
import com.flyn.flyn_resource_gen.misc.put
import com.flyn.flyn_resource_gen.misc.thisModTag
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.ItemHandlerHelper
import net.minecraftforge.items.ItemStackHandler
import kotlin.math.min

class ResourceGenBlockEntity(pos: BlockPos, private val state: BlockState) : BlockEntity(
    BlockEntityInit.RESOURCE_GEN_BLOCK_ENTITY, pos, state
), TickableBlockEntity {

    companion object {

        private val GEN_PROPERTY = List(5) { index ->
            (1 shl index) to (64 shl index shl index)
        }

    }

    val tier
        get() = state.getValue(ResourceGenBlock.TIER)
    var product = Items.AIR
    private val inventory = object: ItemStackHandler(1) {

        override fun getSlotLimit(slot: Int): Int {
            return GEN_PROPERTY[tier - 1].second
        }

        override fun onContentsChanged(slot: Int) {
            super.onContentsChanged(slot)
            this@ResourceGenBlockEntity.setChanged()
        }

        override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
            return stack
        }

        override fun isItemValid(slot: Int, stack: ItemStack): Boolean {
            return false
        }

    }
    private val optional = LazyOptional.of { inventory }
    private var upContainer: LazyOptional<IItemHandler>? = null
    private var ticks = 0

    fun getProduct(): ItemStack {
        val stack = inventory.getStackInSlot(0)
        if (stack.isEmpty) {
            return ItemStack.EMPTY
        }
        val takeAmount = if (stack.count >= 64) 64 else stack.count
        stack.shrink(takeAmount)
        return ItemStack(product).apply {
            count = takeAmount
        }
    }

    fun updateUpContainer() {
        if (level == null || !level!!.isLoaded(worldPosition.above()))
            return
        level!!.getBlockEntity(worldPosition.above())?.let { blockEntity ->
            val optional = blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, Direction.DOWN)
            if (optional.isPresent) {
                if (upContainer != optional) {
                    upContainer = optional
                    upContainer!!.addListener { updateUpContainer() }
                }
                return
            }
        }
        upContainer = LazyOptional.empty()
    }

    private fun getUpContainer(): LazyOptional<IItemHandler> {
        if (upContainer == null)
            updateUpContainer()
        return upContainer!!
    }

    private fun growFromTier() {
        val stack = inventory.getStackInSlot(0)
        val (amount, maxCount) = GEN_PROPERTY[tier - 1]
        if (stack.isEmpty) {
            inventory.setStackInSlot(0, ItemStack(product).apply { count = amount })
        } else {
            stack.count = min(stack.count + amount, maxCount)
        }
    }

    private fun push() {
        val stack = inventory.getStackInSlot(0)
        getUpContainer().map {
            ItemHandlerHelper.insertItem(it, stack, false)
        }.orElse(stack).let {
            inventory.setStackInSlot(0, it)
        }
    }

    override fun load(nbt: CompoundTag) {
        super.load(nbt)
        val data = nbt.thisModTag
        product = data.get(ResourceGenNbt.Product)
        inventory.setStackInSlot(0, ItemStack(product).apply {
            count = data.get(ResourceGenNbt.Count)
        })
    }

    override fun saveAdditional(nbt: CompoundTag) {
        super.saveAdditional(nbt)
        nbt.thisModTag = CompoundTag().apply {
            put(ResourceGenNbt.Product, product)
            inventory.getStackInSlot(0).run {
                put(ResourceGenNbt.Count, count)
            }
        }
    }

    override fun tick() {
        if (level == null || level!!.isClientSide)
            return
        if (product == Items.AIR || ticks++ % 10 != 0)
            return
        growFromTier()
        push()
    }

    override fun <T : Any?> getCapability(cap: Capability<T>): LazyOptional<T> {
        return if (cap == ForgeCapabilities.ITEM_HANDLER) optional.cast()
        else super.getCapability(cap)
    }

    override fun invalidateCaps() {
        optional.invalidate()
    }

}