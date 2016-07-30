package com.InfinityRaider.maneuvergear.proxy;

import com.InfinityRaider.maneuvergear.init.EntityRegistry;
import com.infinityraider.infinitylib.proxy.base.IServerProxyBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SuppressWarnings("unused")
@SideOnly(Side.SERVER)
public class ServerProxy implements IServerProxyBase, IProxy {
    @Override
    public void initEntities() {
        EntityRegistry.getInstance().serverInit();
    }
}
