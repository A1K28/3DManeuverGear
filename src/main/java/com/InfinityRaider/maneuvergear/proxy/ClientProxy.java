package com.InfinityRaider.maneuvergear.proxy;

import com.InfinityRaider.maneuvergear.handler.ArmSwingHandler;
import com.InfinityRaider.maneuvergear.handler.ConfigurationHandler;
import com.InfinityRaider.maneuvergear.handler.KeyInputHandler;
import com.InfinityRaider.maneuvergear.handler.MouseClickHandler;
import com.InfinityRaider.maneuvergear.physics.PhysicsEngine;
import com.InfinityRaider.maneuvergear.physics.PhysicsEngineClientLocal;
import com.InfinityRaider.maneuvergear.physics.PhysicsEngineDummy;
import com.InfinityRaider.maneuvergear.reference.Names;
import com.InfinityRaider.maneuvergear.reference.Reference;
import com.InfinityRaider.maneuvergear.render.*;
import com.InfinityRaider.maneuvergear.render.model.ModelPlayerCustomized;
import com.infinityraider.infinitylib.proxy.base.IClientProxyBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

@SuppressWarnings("unused")
@SideOnly(Side.CLIENT)
public class ClientProxy implements IClientProxyBase, IProxy {
    public static KeyBinding retractLeft = new KeyBinding(Reference.MOD_ID+"."+Names.Objects.KEY+"."+Names.Objects.RETRACT+Names.Objects.LEFT, Keyboard.KEY_Z, "key.categories.movement");
    public static KeyBinding retractRight = new KeyBinding(Reference.MOD_ID+"."+Names.Objects.KEY+"."+Names.Objects.RETRACT+Names.Objects.RIGHT, Keyboard.KEY_X, "key.categories.movement");

    @Override
    public PhysicsEngine createPhysicsEngine(EntityPlayer player) {
        if(player == null || !player.worldObj.isRemote) {
            return new PhysicsEngineDummy();
        }
        EntityPlayer local = getClientPlayer();
        if(local == null) {
            //This only happens during first startup of an SSP world
            return new PhysicsEngineClientLocal(player);
        }
        if(local.getUniqueID().equals(player.getUniqueID())) {
            //Happens during equipping of maneuver gear in an SSP or SMP world, a second SSP world startup or when a LAN player joins a host
            return new PhysicsEngineClientLocal(player);
        }
        else {
            //Happens when a LAN player joins an SSP world
            return new PhysicsEngineDummy();
        }
    }

    @Override
    public void spawnSteamParticles(EntityPlayer player) {
        World world = player.worldObj;
        double x = player.posX;
        double y = player.posY;
        double z = player.posZ;
        Vec3d lookVec = player.getLook(2);
        int nr = 10;
        for(int i=0;i<nr;i++) {
            Minecraft.getMinecraft().effectRenderer.spawnEffectParticle(EnumParticleTypes.CLOUD.getParticleID(), x, y, z, -(lookVec.xCoord * i) / (0.0F + nr), -(lookVec.yCoord * i) / (0.0F + nr), -(lookVec.zCoord * i) / (0.0F + nr));
        }
    }

    @Override
    public void initConfiguration(FMLPreInitializationEvent event) {
        IProxy.super.initConfiguration(event);
        ConfigurationHandler.getInstance().initClientConfigs(event);
    }

    @Override
    public void registerEventHandlers() {
        IProxy.super.registerEventHandlers();
        this.registerEventHandler(MouseClickHandler.getInstance());
        this.registerEventHandler(KeyInputHandler.getInstance());
        this.registerEventHandler(ArmSwingHandler.getInstance());
        this.registerEventHandler(RenderBauble.getInstance());
    }

    @Override
    public void registerKeyBindings() {
        ClientRegistry.registerKeyBinding(retractLeft);
        ClientRegistry.registerKeyBinding(retractRight);
    }

    @Override
    public void replacePlayerModel() {
        ModelPlayerCustomized.replaceOldModel();
    }
}
