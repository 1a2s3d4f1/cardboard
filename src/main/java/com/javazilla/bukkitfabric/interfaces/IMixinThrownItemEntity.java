package com.javazilla.bukkitfabric.interfaces;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface IMixinThrownItemEntity {

    public Item getDefaultItemPublic();

    public ItemStack getItemBF();

}
