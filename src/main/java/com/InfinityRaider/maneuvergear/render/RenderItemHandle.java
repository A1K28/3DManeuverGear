package com.InfinityRaider.maneuvergear.render;

import com.InfinityRaider.maneuvergear.init.Items;
import com.InfinityRaider.maneuvergear.item.ItemManeuverGearHandle;
import com.InfinityRaider.maneuvergear.item.ItemResource;
import com.InfinityRaider.maneuvergear.render.model.ModelManeuverGearHandle;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderItemHandle implements IItemModelRenderer {
    private ModelManeuverGearHandle model = new ModelManeuverGearHandle();
    public static final RenderItemHandle instance = new RenderItemHandle();

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        if (type == ItemRenderType.ENTITY) {
            renderEntity(item);
            return;
        }
        if (type == ItemRenderType.EQUIPPED) {
            renderEquipped(item, (Entity) data[1]);
            return;
        }
        if (type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
            renderEquippedFirstPerson(item, (Entity) data[1]);
            return;
        }
        if (type == ItemRenderType.INVENTORY) {
            renderInventory(item);
        }
    }

    /**
     * Render item as entity in the
     *
     * @param stack:  the itemstack
     */
    protected void renderEntity(ItemStack stack) {
        float scale = 0.05F;
        float dx = 0.4F;
        float dy = -1.7F;
        float dz = 0.9F;
        float angleX = 180;
        float angleY = 135;
        float angleZ = -20;

        GL11.glRotatef(angleZ, -1.0F, 0, 1);
        GL11.glRotatef(angleX, 1, 0, 0);
        GL11.glRotatef(angleY, 0, 1, 0);
        GL11.glTranslatef(dx, dy, dz);
        GL11.glScalef(scale, scale, scale);

        renderModel(Minecraft.getMinecraft().thePlayer, stack, false);

        GL11.glScalef(1.0F / scale, 1.0F / scale, 1.0F / scale);
        GL11.glTranslatef(-dx, -dy, -dz);
        GL11.glRotatef(-angleY, 0, 1, 0);
        GL11.glRotatef(-angleX, 1, 0, 0);
        GL11.glRotatef(-angleZ, -1.0F, 0, 1);
    }

    /**
     * Render item held by an entity
     *
     * @param stack:  the itemstack
     * @param entity: the entity holding the stack
     */
    protected void renderEquipped(ItemStack stack, Entity entity) {
        float scale = 0.075F;
        float dx = 0.4F;
        float dy = -1.7F;
        float dz = 0.9F;
        float angleX = 180;
        float angleY = 135;
        float angleZ = -20;

        GL11.glRotatef(angleZ, -1.0F, 0, 1);
        GL11.glRotatef(angleX, 1, 0, 0);
        GL11.glRotatef(angleY, 0, 1, 0);
        GL11.glTranslatef(dx, dy, dz);
        GL11.glScalef(scale, scale, scale);

        renderModel(entity, stack, false);

        GL11.glScalef(1.0F / scale, 1.0F / scale, 1.0F / scale);
        GL11.glTranslatef(-dx, -dy, -dz);
        GL11.glRotatef(-angleY, 0, 1, 0);
        GL11.glRotatef(-angleX, 1, 0, 0);
        GL11.glRotatef(-angleZ, -1.0F, 0, 1);
    }

    /**
     * Render item held by an entity
     *
     * @param stack:  the itemstack
     * @param entity: the entity holding the stack
     */
    protected void renderEquippedFirstPerson(ItemStack stack, Entity entity) {
        if (entity == null) {
            return;
        }
        if (entity instanceof EntityPlayer) {
            float scale = 0.075F;
            float dx = 1F;
            float dy = 0F;
            float dz = 1F;
            float angleX = 95F;
            float angleY = 0F;
            float angleZ = -130F;

            GL11.glTranslatef(dx, dy, dz);
            GL11.glRotatef(angleX, 1, 0, 0);
            GL11.glRotatef(angleY, 0, 1, 0);
            GL11.glRotatef(angleZ, 0, 0, 1);
            GL11.glScalef(scale, scale, scale);

            renderModel(entity, stack, false);

            GL11.glScalef(1.0F / scale, 1.0F / scale, 1.0F / scale);
            GL11.glRotatef(-angleZ, 0, 0, 1);
            GL11.glRotatef(-angleY, 0, 1, 0);
            GL11.glRotatef(-angleX, 1, 0, 0);
            GL11.glTranslatef(-dx, -dy, -dz);
        }
    }

    /** Render item held by an entity
     * @param stack: the itemstack
     */
    protected void renderInventory(ItemStack stack) {
        float scale = 0.045F;
        float dx = 0.7F;
        float dy = -1.0F;
        float dz = 0.8F;
        float angleX = 135;
        float angleY = 100;
        float angleZ = -50;

        GL11.glRotatef(angleZ, -1.0F, 0, 1);
        GL11.glRotatef(angleX, 1, 0, 0);
        GL11.glRotatef(angleY, 0, 1, 0);
        GL11.glTranslatef(dx, dy, dz);
        GL11.glScalef(scale, scale, scale);

        renderModel(Minecraft.getMinecraft().thePlayer, stack, false);

        GL11.glScalef(1.0F / scale, 1.0F / scale, 1.0F / scale);
        GL11.glTranslatef(-dx, -dy, -dz);
        GL11.glRotatef(-angleY, 0, 1, 0);
        GL11.glRotatef(-angleX, 1, 0, 0);
        GL11.glRotatef(-angleZ, -1.0F, 0, 1);
    }

    @Override
    public final void renderModel(Entity entity, ItemStack stack, boolean left) {
        model.render(entity, 0, 0, 0, 0, 0, 1);

        if(stack != null && stack.getItem() != null && stack.getItem() instanceof ItemManeuverGearHandle) {
            ItemManeuverGearHandle handle = (ItemManeuverGearHandle) stack.getItem();
            if(handle.hasSwordBlade(stack, left)) {

                Tessellator tessellator = Tessellator.instance;
                Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationItemsTexture);
                IIcon icon = Items.itemResource.getIconFromDamage(ItemResource.EnumSubItems.SWORD_BLADE.ordinal());

                GL11.glPushMatrix();

                float scale = 20;
                float dx = -0.25F;
                float dy = 0.65F;
                float dz = -0.2F;
                float ax = 0;
                float ay = 90;
                float az = 225;

                GL11.glScalef(scale, scale, scale);
                GL11.glTranslatef(dx, dy, dz);
                GL11.glRotatef(ay, 0, 1, 0);
                GL11.glRotatef(ax, 1, 0, 0);
                GL11.glRotatef(az, 0, 0, 1);
                GL11.glScalef(1.5F, 1.5F, 0.8F);

                float X = -0.7F;
                float Y = -0.2F;
                float Z = 0.0375F;
                tessellator.addTranslation(X, Y, Z);

                ItemRenderer.renderItemIn2D(tessellator, icon.getMinU(), icon.getMinV(), icon.getMaxU(), icon.getMaxV(), icon.getIconWidth(), icon.getIconHeight(), 1.0F / ((float) 16));

                tessellator.addTranslation(-X, -Y, -Z);

                GL11.glScalef(1F / 1.5F, 1F / 1.5F, 1F / 0.8F);
                GL11.glRotatef(-az, 0, 0, 1);
                GL11.glRotatef(-ax, 1, 0, 0);
                GL11.glRotatef(-ay, 0, 1, 0);
                GL11.glTranslatef(-dx, -dy, -dz);
                GL11.glScalef(1F / scale, 1F / scale, 1F / scale);

                GL11.glPopMatrix();
            }
        }
    }
}
