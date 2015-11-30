package de.katzenpapst.amunra.client.renderer;

import net.minecraft.block.Block;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.model.ModelLargeChest;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLLog;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.block.chest.ARChest;
import de.katzenpapst.amunra.tile.TileEntityARChest;

public class RendererTileEntityChest extends TileEntitySpecialRenderer {


    private static final ResourceLocation doubleTexture = new ResourceLocation("textures/entity/chest/normal_double.png");
    private static final ResourceLocation normalTexture = new ResourceLocation("textures/entity/chest/normal.png");
    private ModelChest chestModelNormal = new ModelChest();
    private ModelChest chestModelDouble = new ModelLargeChest();



    public RendererTileEntityChest()
    {
    	AmunRa.chestRendererId = RenderingRegistry.getNextAvailableRenderId();
    }

    public void renderTileEntityAt(TileEntityARChest tileEnt, double x, double y, double z, float angle)
    {
    	Block block = tileEnt.getBlockType();
        int i;
        if (!tileEnt.hasWorldObj())
        {
            i = 0;
        }
        else
        {

            i = tileEnt.getBlockMetadata();

            if (block instanceof ARChest && i == 0)
            {
                try
                {
                	((ARChest)block).unifyAdjacentChests(tileEnt.getWorldObj(), tileEnt.xCoord, tileEnt.yCoord, tileEnt.zCoord);
                }
                catch (ClassCastException e)
                {
                    FMLLog.severe("Attempted to render a chest at %d,  %d, %d that was not a chest", tileEnt.xCoord, tileEnt.yCoord, tileEnt.zCoord);
                    return;
                }
                i = tileEnt.getBlockMetadata();
            }

            tileEnt.checkForAdjacentChests();
        }

        if (tileEnt.adjacentChestZNeg == null && tileEnt.adjacentChestXNeg == null)
        {
            ModelChest modelchest;

            if (tileEnt.adjacentChestXPos == null && tileEnt.adjacentChestZPos == null)
            {
                modelchest = this.chestModelNormal;


                this.bindTexture(((ARChest)block).getNormalTexure());

            }
            else
            {
                modelchest = this.chestModelDouble;


                this.bindTexture(((ARChest)block).getDoubleTexture());

            }

            GL11.glPushMatrix();
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glTranslatef((float)x, (float)y + 1.0F, (float)z + 1.0F);
            GL11.glScalef(1.0F, -1.0F, -1.0F);
            GL11.glTranslatef(0.5F, 0.5F, 0.5F);
            short short1 = 0;

            if (i == 2)
            {
                short1 = 180;
            }

            if (i == 3)
            {
                short1 = 0;
            }

            if (i == 4)
            {
                short1 = 90;
            }

            if (i == 5)
            {
                short1 = -90;
            }

            if (i == 2 && tileEnt.adjacentChestXPos != null)
            {
                GL11.glTranslatef(1.0F, 0.0F, 0.0F);
            }

            if (i == 5 && tileEnt.adjacentChestZPos != null)
            {
                GL11.glTranslatef(0.0F, 0.0F, -1.0F);
            }

            GL11.glRotatef(short1, 0.0F, 1.0F, 0.0F);
            GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
            float f1 = tileEnt.prevLidAngle + (tileEnt.lidAngle - tileEnt.prevLidAngle) * angle;
            float f2;

            if (tileEnt.adjacentChestZNeg != null)
            {
                f2 = tileEnt.adjacentChestZNeg.prevLidAngle + (tileEnt.adjacentChestZNeg.lidAngle - tileEnt.adjacentChestZNeg.prevLidAngle) * angle;

                if (f2 > f1)
                {
                    f1 = f2;
                }
            }

            if (tileEnt.adjacentChestXNeg != null)
            {
                f2 = tileEnt.adjacentChestXNeg.prevLidAngle + (tileEnt.adjacentChestXNeg.lidAngle - tileEnt.adjacentChestXNeg.prevLidAngle) * angle;

                if (f2 > f1)
                {
                    f1 = f2;
                }
            }

            f1 = 1.0F - f1;
            f1 = 1.0F - f1 * f1 * f1;
            modelchest.chestLid.rotateAngleX = -(f1 * (float)Math.PI / 2.0F);
            modelchest.renderAll();
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            GL11.glPopMatrix();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    @Override
	public void renderTileEntityAt(TileEntity tileEnt, double x, double y, double z, float angle)
    {
        this.renderTileEntityAt((TileEntityARChest)tileEnt, x, y, z, angle);
    }

}
