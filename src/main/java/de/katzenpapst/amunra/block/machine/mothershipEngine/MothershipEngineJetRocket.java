package de.katzenpapst.amunra.block.machine.mothershipEngine;

import java.util.Map;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.GuiIds;
import de.katzenpapst.amunra.block.ARBlocks;
import de.katzenpapst.amunra.block.BlockMachineMeta;
import de.katzenpapst.amunra.item.ARItems;
import de.katzenpapst.amunra.item.ItemDamagePair;
import de.katzenpapst.amunra.item.MothershipFuel;
import de.katzenpapst.amunra.item.MothershipFuelRequirements;
import de.katzenpapst.amunra.tile.TileEntityIsotopeGenerator;
import de.katzenpapst.amunra.tile.TileEntityMothershipEngineJet;
import de.katzenpapst.amunra.world.CoordHelper;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class MothershipEngineJetRocket extends MothershipEngineJetBase {

    private IIcon iconFront = null;
    private IIcon iconBack = null;

    private String textureFront;
    private String textureBack;

    protected ItemDamagePair item = null;

    public MothershipEngineJetRocket(String name, String texture, String textureFront, String textureBack) {
        super(name, texture);
        this.textureFront = textureFront;
        this.textureBack = textureBack;
    }

    @Override
    public double getThrust(World w, int x, int y, int z, int meta) {
        return this.getMyTileEntity(w, x, y, z).getNumBoosters() * 100.0D;
    }

    @Override
    public double getSpeed(World world, int x, int y, int z, int meta) {
        return this.getMyTileEntity(world, x, y, z).getSpeed();
    }

    @Override
    public boolean canTravelDistance(World world, int x, int y, int z, int meta, double distance) {
        TileEntityMothershipEngineJet t = this.getMyTileEntity(world, x, y, z);
        return t.canTravelDistance(distance);
    }

    @Override
    public void registerBlockIcons(IIconRegister par1IconRegister)
    {
        super.registerBlockIcons(par1IconRegister);
        iconFront = par1IconRegister.registerIcon(textureFront);
        iconBack = par1IconRegister.registerIcon(textureBack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        int realMeta = ((BlockMachineMeta)this.parent).getRotationMeta(meta);
        // we have the front thingy at front.. but what is front?
        // east is the output
        // I think front is south
        ForgeDirection front = CoordHelper.rotateForgeDirection(ForgeDirection.SOUTH, realMeta);
        ForgeDirection back = CoordHelper.rotateForgeDirection(ForgeDirection.NORTH, realMeta);


        if(side == front.ordinal()) {
            return this.iconFront;
        }
        if(side == back.ordinal()) {
            return this.iconBack;
        }
        return this.blockIcon;
    }

    @Override
    public boolean onMachineActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ)
    {
        // do the isRemote thing here, too?
        entityPlayer.openGui(AmunRa.instance, GuiIds.GUI_MS_ROCKET_ENGINE, world, x, y, z);
        return true;
        // return false;
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata)
    {
        return new TileEntityMothershipEngineJet();
    }

    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderType()
    {
        return AmunRa.dummyRendererId;
    }

    @Override
    public Item getItem(World p_149694_1_, int p_149694_2_, int p_149694_3_, int p_149694_4_)
    {
        return item.getItem();
    }

    @Override
    public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_)
    {
        /**
         * Returns whether or not this bed block is the head of the bed.
         */
        return item.getItem();
    }

    @Override
    protected ItemDamagePair getItem() {
        if(item == null) {
            item = new ItemDamagePair(ARItems.jetItem, 0);
        }
        return item;
    }

    @Override
    public int damageDropped(int meta) {
        return item.getDamage();
    }

    @Override
    public void onBlockPlacedBy(World w, int x, int y, int z, EntityLivingBase user, ItemStack stack)
    {
        /*
        TileEntity leTile = w.getTileEntity(x, y, z);
        if(leTile instanceof TileEntityMothershipEngineJet) {
            ((TileEntityMothershipEngineJet)leTile).createMultiblock();
        }*/
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        TileEntity leTile = world.getTileEntity(x, y, z);
        if(leTile instanceof TileEntityMothershipEngineJet) {
            ((TileEntityMothershipEngineJet)leTile).scheduleUpdate();
            // world.markBlockForUpdate(x, y, z);
        }
    }

    /**
     * Called when a block is placed using its ItemBlock. Args: World, X, Y, Z, side, hitX, hitY, hitZ, block metadata
     *
     */
    @Override
    public int onBlockPlaced(World w, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int meta)
    {
        return meta;
    }

    @Override
    public boolean onUseWrench(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX,
            float hitY, float hitZ) {
        // TODO rotate the tile entity
        return false;
    }

    protected TileEntityMothershipEngineJet getMyTileEntity(World world, int x, int y, int z) {
        TileEntity t = world.getTileEntity(x, y, z);
        if(t == null || !(t instanceof TileEntityMothershipEngineJet)) {
            // TODO throw exception instead
            return null;
        }
        return (TileEntityMothershipEngineJet)t;
    }



    /**
     * Should consume the fuel needed for the transition, on client side also start any animation or something alike.
     * This will be called for all engines which are actually being used
     *
     * @param world
     * @param x
     * @param y
     * @param z
     * @param meta
     * @param distance
     */
    @Override
    public void beginTransit(World world, int x, int y, int z, int meta, double distance) {
        getMyTileEntity(world, x, y, z).beginTransit(distance);
    }


    /**
     * Will be called on all which return true from isInUse on transit end
     *
     * @param world
     * @param x
     * @param y
     * @param z
     * @param meta
     */
    @Override
    public void endTransit(World world, int x, int y, int z, int meta) {
        getMyTileEntity(world, x, y, z).endTransit();
    }

    /**
     * Should return whenever beginTransit has been called on this engine, and endTransit hasn't yet
     *
     * @param world
     * @param x
     * @param y
     * @param z
     * @param meta
     * @return
     */
    @Override
    public boolean isInUse(World world, int x, int y, int z, int meta) {
        return getMyTileEntity(world, x, y, z).isInUse();
    }

    @Override
    public boolean isEnabled(World world, int x, int y, int z, int meta) {
        TileEntityMothershipEngineJet myTile = getMyTileEntity(world, x, y, z);
        return !myTile.getDisabled(0) && !myTile.isObstructed();
    }

    @Override
    public MothershipFuelRequirements
            getFuelRequirements(World world, int x, int y, int z, int meta, double distance) {

        return getMyTileEntity(world, x, y, z).getFuelRequirements(distance);
    }
}
