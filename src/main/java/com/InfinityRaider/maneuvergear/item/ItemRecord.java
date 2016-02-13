package com.InfinityRaider.maneuvergear.item;

import com.InfinityRaider.maneuvergear.reference.Reference;
import net.minecraft.util.ResourceLocation;

public class ItemRecord extends net.minecraft.item.ItemRecord {
    private final static String name = "GurenNoYumiya";

    public ItemRecord() {
        super(Reference.MOD_ID.toLowerCase()+":"+name);
    }

    @Override
    public ResourceLocation getRecordResource(String name) {
        return new ResourceLocation(Reference.MOD_ID.toLowerCase()+":records."+ItemRecord.name);
    }
}
