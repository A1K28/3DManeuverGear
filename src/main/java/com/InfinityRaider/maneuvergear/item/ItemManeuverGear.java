package com.InfinityRaider.maneuvergear.item;

import baubles.api.BaubleType;
import com.InfinityRaider.maneuvergear.handler.DartHandler;
import com.InfinityRaider.maneuvergear.network.MessageEquipManeuverGear;
import com.InfinityRaider.maneuvergear.network.MessageNotifyBaubleEquip;
import com.InfinityRaider.maneuvergear.physics.PhysicsEngine;
import com.InfinityRaider.maneuvergear.reference.Names;
import com.InfinityRaider.maneuvergear.reference.Reference;
import com.InfinityRaider.maneuvergear.render.IBaubleRenderer;
import com.InfinityRaider.maneuvergear.render.RenderManeuverGear;
import com.infinityraider.infinitylib.item.IItemWithRecipe;
import com.infinityraider.infinitylib.item.ItemBase;
import com.infinityraider.infinitylib.network.NetworkWrapper;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Tuple;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.ShapedOreRecipe;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@MethodsReturnNonnullByDefault
public class ItemManeuverGear extends ItemBase implements IBaubleRendered, IItemWithRecipe {
    public static int MAX_HOLSTERED_BLADES = 4;

    public ItemManeuverGear() {
        super(Names.Objects.MANEUVER_GEAR);
        this.setCreativeTab(CreativeTabs.COMBAT);
        this.setMaxStackSize(1);
    }

    @Override
    public List<String> getOreTags() {
        return Collections.emptyList();
    }

    @Override
    @ParametersAreNonnullByDefault
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        if(world.isRemote) {
            NetworkWrapper.getInstance().sendToServer(new MessageEquipManeuverGear(hand));
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    /**
     * Checks the number of blades left in the holster
     * @param stack the stack containing this
     * @param left check left or right holster
     * @return the amount of blades left in the holster
     */
    public int getBladeCount(ItemStack stack, boolean left) {
        if(!isValidManeuverGearStack(stack)) {
            return 0;
        }
        NBTTagCompound tag = stack.getTagCompound();
        if(tag == null) {
            return 0;
        }
        return left ? tag.getInteger(Names.NBT.LEFT) : tag.getInteger(Names.NBT.RIGHT);
    }

    /**
     * Tries to adds blades to the holster
     * @param stack the stack containing this
     * @param amount the amount to add
     * @param left add blades to left or right holster
     * @return the amount of blades left after adding to the holster
     */
    public int addBlades(ItemStack stack, int amount, boolean left) {
        if(!isValidManeuverGearStack(stack)) {
            return amount;
        }
        NBTTagCompound tag = stack.getTagCompound();
        if(tag == null) {
            tag = new NBTTagCompound();
            stack.setTagCompound(tag);
        }
        int current = getBladeCount(stack, left);
        if(left) {
            int maxToAdd = getMaxBlades() - current;
            if(amount >= maxToAdd) {
                tag.setInteger(Names.NBT.LEFT, getMaxBlades());
                return amount - maxToAdd;
            } else {
                tag.setInteger(Names.NBT.LEFT, current + amount);
                return 0;
            }
        } else {
            int maxToAdd = getMaxBlades() - current;
            if(amount >= maxToAdd) {
                tag.setInteger(Names.NBT.RIGHT, getMaxBlades());
                return amount - maxToAdd;
            } else {
                tag.setInteger(Names.NBT.RIGHT, current + amount);
                return 0;
            }
        }
    }

    /**
     * Tries to retrieve blades from the holster
     * @param stack the stack containing this
     * @param amount the amount to retrieve
     * @param left retrieve blades from left or right holster
     * @return the amount of blades retrieved from the holster
     */
    public int removeBlades(ItemStack stack, int amount, boolean left) {
        if(!isValidManeuverGearStack(stack)) {
            return 0;
        }
        NBTTagCompound tag = stack.getTagCompound();
        if(tag == null) {
            return 0;
        }
        int current = getBladeCount(stack, left);
        if(left) {
            if(amount >= current) {
                tag.setInteger(Names.NBT.LEFT, 0);
                return current;
            } else {
                tag.setInteger(Names.NBT.LEFT, current - amount);
                return amount;
            }
        } else {
            if(amount >= current) {
                tag.setInteger(Names.NBT.RIGHT, 0);
                return current;
            } else {
                tag.setInteger(Names.NBT.RIGHT, current - amount);
                return amount;
            }
        }

    }

    /**
     * @return maximum number of holstered blades per holster
     */
    public int getMaxBlades() {
        return MAX_HOLSTERED_BLADES;
    }

    /**
     * Checks if the stack is a valid stack containing Maneuver Gear
     * @param stack the stack to check
     * @return if the stack is valid
     */
    public boolean isValidManeuverGearStack(ItemStack stack) {
        return stack != null && stack.getItem() instanceof ItemManeuverGear;
    }

    @Override
    public BaubleType getBaubleType(ItemStack stack) {
        return BaubleType.BELT;
    }

    /** This handles the movement of the player */
    @Override
    public void onWornTick(ItemStack stack, EntityLivingBase entity) {
        if(entity==null || !(entity instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) entity;
        boolean remote = player.worldObj.isRemote;
        if(remote && stack!=null && stack.getItem()==this) {
            if(DartHandler.instance.isWearingGear(player) && (DartHandler.instance.getLeftDart(player)!=null || DartHandler.instance.getRightDart(player)!=null)) {
                PhysicsEngine engine = DartHandler.instance.getPhysicsEngine(player);
                engine.updateTick();
            }
        }
    }

    @Override
    public void onEquipped(ItemStack stack, EntityLivingBase entity) {
        if(entity==null || !(entity instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) entity;
        if(stack!=null && stack.getItem()==this) {
            DartHandler.instance.equipGear(player);
        }
        if(!player.worldObj.isRemote) {
            NetworkWrapper.getInstance().sendToAll(new MessageNotifyBaubleEquip(player, stack, true));
        }
    }

    @Override
    public void onUnequipped(ItemStack stack, EntityLivingBase entity) {
        if(entity==null || !(entity instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) entity;
        if(stack!=null && stack.getItem()==this) {
            DartHandler.instance.unEquipGear(player);
        }
        if(!player.worldObj.isRemote) {
            NetworkWrapper.getInstance().sendToAll(new MessageNotifyBaubleEquip(player, stack, false));
        }
    }

    @Override
    public boolean canEquip(ItemStack stack, EntityLivingBase entityL) {
        return true;
    }

    @Override
    public boolean canUnequip(ItemStack stack, EntityLivingBase entityLivingBase) {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IBaubleRenderer getRenderer(ItemStack stack) {
        return RenderManeuverGear.instance;
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("deprecation")
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
        if(stack != null) {
            list.add(I18n.translateToLocal("3DManeuverGear.ToolTip.belt"));
            list.add(I18n.translateToLocal("3DManeuverGear.ToolTip.leftBlades")+": "+this.getBladeCount(stack, true)+"/"+MAX_HOLSTERED_BLADES);
            list.add(I18n.translateToLocal("3DManeuverGear.ToolTip.rightBlades")+": "+this.getBladeCount(stack, false)+"/"+MAX_HOLSTERED_BLADES);
        }
    }

    @Override
    public List<IRecipe> getRecipes() {
        List<IRecipe> list = new ArrayList<>();
        list.add(new ShapedOreRecipe(this, "cnc", "lgl", "hbh",
                'c', ItemResource.EnumSubItems.CABLE_COIL.getStack(),
                'n', ItemResource.EnumSubItems.GAS_NOZZLE.getStack(),
                'l', ItemResource.EnumSubItems.GRAPPLE_LAUNCHER.getStack(),
                'g', ItemResource.EnumSubItems.GIRDLE.getStack(),
                'h', ItemResource.EnumSubItems.BLADE_HOLSTER_ASSEMBLY.getStack(),
                'b', ItemResource.EnumSubItems.BELT.getStack()));
        return list;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public List<Tuple<Integer, ModelResourceLocation>> getModelDefinitions() {
        List<Tuple<Integer, ModelResourceLocation>> list = new ArrayList<>();
        list.add(new Tuple<>(0, new ModelResourceLocation(Reference.MOD_ID.toLowerCase() + ":maneuverGear", "inventory")));
        return list;
    }
}
