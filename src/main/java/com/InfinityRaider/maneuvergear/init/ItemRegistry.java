package com.InfinityRaider.maneuvergear.init;

import com.InfinityRaider.maneuvergear.handler.ConfigurationHandler;
import com.InfinityRaider.maneuvergear.item.*;
import com.InfinityRaider.maneuvergear.reference.Names;
import com.InfinityRaider.maneuvergear.render.item.ItemRendererRegistry;
import com.InfinityRaider.maneuvergear.utility.LogHelper;
import com.InfinityRaider.maneuvergear.utility.RegisterHelper;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.Tuple;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

public class ItemRegistry {
    private static final ItemRegistry INSTANCE = new ItemRegistry();

    public static ItemRegistry getInstance() {
        return INSTANCE;
    }

    public Item itemManeuverGear;
    public Item itemManeuverGearHandle;
    public Item itemResource;
    public Item itemFallBoots;
    public Item itemRecord;

    private List<Item> items;
    private boolean init;

    private ItemRegistry() {
        this.items = new ArrayList<>();
        this.init = false;
    }

    public List<Item> getItems() {
        return items;
    }

    public void init() {
        if(init) {
            return;
        }

        itemManeuverGear = new ItemManeuverGear();
        RegisterHelper.registerItem(itemManeuverGear, Names.Objects.MANEUVER_GEAR);
        items.add(itemManeuverGear);

        itemManeuverGearHandle = new ItemManeuverGearHandle();
        RegisterHelper.registerItem(itemManeuverGearHandle, Names.Objects.MANEUVER_HANDLE);
        items.add(itemManeuverGearHandle);

        itemResource = new ItemResource();
        RegisterHelper.registerItem(itemResource, Names.Objects.RESOURCE);
        items.add(itemResource);

        if(!ConfigurationHandler.getInstance().disableFallBoots) {
            itemFallBoots = new ItemFallBoots();
            RegisterHelper.registerItem(itemFallBoots, Names.Objects.BOOTS);
            items.add(itemFallBoots);
        }

        if(!ConfigurationHandler.getInstance().disableMusicDisc) {
            itemRecord = new ItemRecord("GurenNoYumiya");
            RegisterHelper.registerItem(itemRecord, Names.Objects.RECORD);
            items.add(itemRecord);
        }

        init = true;
    }

    public void initRecipes() {
        if(init) {
            getItems().stream().filter(item -> item instanceof IItemWithRecipe).forEach(
                    item -> ((IItemWithRecipe) item).getRecipes().forEach(GameRegistry::addRecipe));
        }
    }

    @SideOnly(Side.CLIENT)
    public void registerRenderers() {
        for(Item item : items) {
            if(item instanceof IItemWithModel) {
                for (Tuple<Integer, ModelResourceLocation> entry : ((IItemWithModel) item).getModelDefinitions()) {
                    ModelLoader.setCustomModelResourceLocation(item, entry.getFirst(), entry.getSecond());
                }
            }
            if(item instanceof ICustomRenderedItem) {
                ItemRendererRegistry.getInstance().registerCustomItemRenderer((ICustomRenderedItem<? extends Item>) item);
            }
        }

        for (ICustomRenderedItem item : ItemRendererRegistry.getInstance().getRegisteredItems()) {
            LogHelper.debug("Registered custom renderer for " + item.getItemModelResourceLocation());
        }
    }
}
