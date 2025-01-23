package com.flyn.flyn_resource_gen.block_entities

import com.flyn.flyn_resource_gen.config.Config
import com.flyn.flyn_resource_gen.config.ResourceGenProperty
import com.flyn.flyn_resource_gen.init.BlockEntityInit
import com.flyn.flyn_resource_gen.misc.ResourceGenNbt
import com.flyn.flyn_resource_gen.misc.get
import com.flyn.flyn_resource_gen.misc.put
import com.flyn.flyn_resource_gen.misc.thisModTag
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.ItemHandlerHelper
import net.minecraftforge.items.ItemStackHandler
import kotlin.math.min

class ResourceGenBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(
    BlockEntityInit.RESOURCE_GEN_BLOCK_ENTITY, pos, state
), TickableBlockEntity {

    var tier = ResourceGenProperty.DEFAULT_TIER
    var core = ResourceGenProperty.EMPTY_CORE
        set(value) {
            property = Config.generatorProperty[value]
            field = value
        }
    private val inventory = object: ItemStackHandler(1) {

        override fun getSlotLimit(slot: Int): Int {
            return ResourceGenProperty.getSlotLimit(tier)
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
    private var property: ResourceGenProperty? = null
    private var ticks = 0

    fun getProduct(): ItemStack {
        val stack = inventory.getStackInSlot(0)
        if (stack.isEmpty) {
            return ItemStack.EMPTY
        }
        val takeAmount = if (stack.count >= 64) 64 else stack.count
        stack.shrink(takeAmount)
        return ItemStack(property!!.product, takeAmount)
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
                    pushProduct()
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

    private fun growProduct() {
        val stack = inventory.getStackInSlot(0)
        val amount = ResourceGenProperty.getYield(tier)
        val maxCount = ResourceGenProperty.getSlotLimit(tier)
        if (stack.isEmpty) {
            inventory.setStackInSlot(0, ItemStack(property!!.product).apply { count = amount })
        } else {
            stack.count = min(stack.count + amount, maxCount)
        }
    }

    private fun pushProduct() {
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
        tier = data.get(ResourceGenNbt.Tier).coerceAtLeast(1)
        core = data.get(ResourceGenNbt.Core)
        if (property == null)
            return
        inventory.setStackInSlot(0, ItemStack(property!!.product, data.get(ResourceGenNbt.Count)))
    }

    override fun saveAdditional(nbt: CompoundTag) {
        super.saveAdditional(nbt)
        nbt.thisModTag = CompoundTag().apply {
            put(ResourceGenNbt.Tier, tier)
            put(ResourceGenNbt.Core, core)
            put(ResourceGenNbt.Count, inventory.getStackInSlot(0).count)
        }
    }

    override fun tick() {
        if (level == null || level!!.isClientSide || property == null)
            return
        if (++ticks < property!!.interval)
            return
        growProduct()
        pushProduct()
        ticks = 0
    }

    override fun <T : Any?> getCapability(cap: Capability<T>): LazyOptional<T> {
        return if (cap == ForgeCapabilities.ITEM_HANDLER) optional.cast()
        else super.getCapability(cap)
    }

    override fun invalidateCaps() {
        optional.invalidate()
    }

    override fun getUpdateTag(): CompoundTag {
        val nbt = super.getUpdateTag()
        nbt.thisModTag = CompoundTag().apply {
            put(ResourceGenNbt.Tier, tier)
            put(ResourceGenNbt.Core, core)
        }
        return nbt
    }

    override fun handleUpdateTag(nbt: CompoundTag) {
        val data = nbt.thisModTag
        tier = data.get(ResourceGenNbt.Tier).coerceAtLeast(1)
        core = data.get(ResourceGenNbt.Core)
    }

}