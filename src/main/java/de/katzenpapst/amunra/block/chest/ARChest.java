package de.katzenpapst.amunra.block.chest;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.tile.TileEntityARChest;
import micdoodle8.mods.galacticraft.planets.mars.blocks.BlockTier2TreasureChest;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class ARChest extends BlockContainer {

	String blockIconName;
	String smallChestTexture;
	String largeChestTexture;
	private final Random random = new Random();

	private static ResourceLocation normalTexture;
	private static ResourceLocation doubleTexture;



	public ARChest(String assetName, String blockIconName, String smallChestTexture, String largeChestTexture) {
		super(Material.rock);
        this.setHardness(2.5F);
        this.setResistance(100.0F);
        this.setStepSound(Block.soundTypeStone);
        this.setBlockName(assetName);

        this.blockIconName = blockIconName;
        this.smallChestTexture = smallChestTexture;
        this.largeChestTexture = largeChestTexture;

        normalTexture = new ResourceLocation(this.smallChestTexture);
        doubleTexture = new ResourceLocation(this.largeChestTexture);

	}

	public ResourceLocation getNormalTexure() {
		return normalTexture;
	}

	public ResourceLocation getDoubleTexture() {
		return doubleTexture;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int p_149915_2_) {
		return new TileEntityARChest();
	}

	@Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister par1IconRegister)
    {
        this.blockIcon = par1IconRegister.registerIcon(this.blockIconName);
    }

	/*
	@Override
    public float getBlockHardness(World par1World, int par2, int par3, int par4)
    {
		// I think this makes it indestructible
        return -1.0F;
    }*/

	@SideOnly(Side.CLIENT)
    @Override
    public CreativeTabs getCreativeTabToDisplayOn()
    {
        return AmunRa.arTab;
    }

	@Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    /**
     * The type of render function that is called for this block
     */
    @Override
	public int getRenderType()
    {
    	return AmunRa.chestRendererId;
    	// http://www.minecraftforum.net/forums/archive/tutorials/931732-forge-micros-chest-tutorial
    	// micdoodle8.mods.galacticraft.core.blocks.BlockT1TreasureChest.BlockT1TreasureChest(String)
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
    }

    /**
     * Called whenever the block is added into the world. Args: world, x, y, z
     */
    @Override
	public void onBlockAdded(World world, int x, int y, int z)
    {
    	super.onBlockAdded(world, x, y, z);
        this.unifyAdjacentChests(world, x, y, z);
        Block var5 = world.getBlock(x, y, z - 1);
        Block var6 = world.getBlock(x, y, z + 1);
        Block var7 = world.getBlock(x - 1, y, z);
        Block var8 = world.getBlock(x + 1, y, z);

        if (var5 == this)
        {
            this.unifyAdjacentChests(world, x, y, z - 1);
        }

        if (var6 == this)
        {
            this.unifyAdjacentChests(world, x, y, z + 1);
        }

        if (var7 == this)
        {
            this.unifyAdjacentChests(world, x - 1, y, z);
        }

        if (var8 == this)
        {
            this.unifyAdjacentChests(world, x + 1, y, z);
        }
    }

    public void unifyAdjacentChests(World world, int x, int y, int z)
    {
        if (!world.isRemote)
        {
            Block var5 = world.getBlock(x, y, z - 1);
            Block var6 = world.getBlock(x, y, z + 1);
            Block var7 = world.getBlock(x - 1, y, z);
            Block var8 = world.getBlock(x + 1, y, z);
            Block var10;
            Block var11;
            byte rotationOrSo;
            int otherBlockMeta;

            if (var5 != this && var6 != this)
            {
                if (var7 != this && var8 != this)
                {
                    rotationOrSo = 3;

                    if (var5.func_149730_j() && !var6.func_149730_j())
                    {
                        rotationOrSo = 3;
                    }

                    if (var6.func_149730_j() && !var5.func_149730_j())
                    {
                        rotationOrSo = 2;
                    }

                    if (var7.func_149730_j() && !var8.func_149730_j())
                    {
                        rotationOrSo = 5;
                    }

                    if (var8.func_149730_j() && !var7.func_149730_j())
                    {
                        rotationOrSo = 4;
                    }
                }
                else
                {
                    var10 = world.getBlock(var7 == this ? x - 1 : x + 1, y, z - 1);
                    var11 = world.getBlock(var7 == this ? x - 1 : x + 1, y, z + 1);
                    rotationOrSo = 3;
                    if (var7 == this)
                    {
                        otherBlockMeta = world.getBlockMetadata(x - 1, y, z);
                    }
                    else
                    {
                        otherBlockMeta = world.getBlockMetadata(x + 1, y, z);
                    }

                    if (otherBlockMeta == 2)
                    {
                        rotationOrSo = 2;
                    }
                    // func_149730_j = isOpaque
                    if ((var5.func_149730_j() || var10.func_149730_j()) && !var6.func_149730_j() && !var11.func_149730_j())
                    {
                        rotationOrSo = 3;
                    }

                    if ((var6.func_149730_j() || var11.func_149730_j()) && !var5.func_149730_j() && !var10.func_149730_j())
                    {
                        rotationOrSo = 2;
                    }
                }
            }
            else
            {
                var10 = world.getBlock(x - 1, y, var5 == this ? z - 1 : z + 1);
                var11 = world.getBlock(x + 1, y, var5 == this ? z - 1 : z + 1);
                rotationOrSo = 5;
                if (var5 == this)
                {
                    otherBlockMeta = world.getBlockMetadata(x, y, z - 1);
                }
                else
                {
                    otherBlockMeta = world.getBlockMetadata(x, y, z + 1);
                }

                if (otherBlockMeta == 4)
                {
                    rotationOrSo = 4;
                }

                if ((var7.func_149730_j() || var10.func_149730_j()) && !var8.func_149730_j() && !var11.func_149730_j())
                {
                    rotationOrSo = 5;
                }

                if ((var8.func_149730_j() || var11.func_149730_j()) && !var7.func_149730_j() && !var10.func_149730_j())
                {
                    rotationOrSo = 4;
                }
            }

            world.setBlockMetadataWithNotify(x, y, z, rotationOrSo, 3);
        }
    }

    @Override
    public boolean canPlaceBlockAt(World par1World, int par2, int par3, int par4)
    {
        int var5 = 0;

        if (par1World.getBlock(par2 - 1, par3, par4) == this)
        {
            ++var5;
        }

        if (par1World.getBlock(par2 + 1, par3, par4) == this)
        {
            ++var5;
        }

        if (par1World.getBlock(par2, par3, par4 - 1) == this)
        {
            ++var5;
        }

        if (par1World.getBlock(par2, par3, par4 + 1) == this)
        {
            ++var5;
        }

        return var5 <= 1 && (this.isThereANeighborChest(par1World, par2 - 1, par3, par4) ? false : !this.isThereANeighborChest(par1World, par2 + 1, par3, par4) && !this.isThereANeighborChest(par1World, par2, par3, par4 - 1) && !this.isThereANeighborChest(par1World, par2, par3, par4 + 1));
    }

    /**
     * Checks the neighbor blocks to see if there is a chest there. Args: world,
     * x, y, z
     */
    private boolean isThereANeighborChest(World par1World, int x, int y, int z)
    {
        return par1World.getBlock(x, y, z) == this && (par1World.getBlock(x - 1, y, z) == this ? true : par1World.getBlock(x + 1, y, z) == this ? true : par1World.getBlock(x, y, z - 1) == this ? true : par1World.getBlock(x, y, z + 1) == this);
    }

    @Override
    public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, Block par5)
    {
        super.onNeighborBlockChange(par1World, par2, par3, par4, par5);
        final TileEntityARChest var6 = (TileEntityARChest) par1World.getTileEntity(par2, par3, par4);

        if (var6 != null)
        {
            var6.updateContainingBlockInfo();
        }
    }

    @Override
    public void breakBlock(World par1World, int par2, int par3, int par4, Block par5, int par6)
    {
        final TileEntityARChest var7 = (TileEntityARChest) par1World.getTileEntity(par2, par3, par4);

        if (var7 != null)
        {
            for (int var8 = 0; var8 < var7.getSizeInventory(); ++var8)
            {
                final ItemStack var9 = var7.getStackInSlot(var8);

                if (var9 != null)
                {
                    final float var10 = this.random.nextFloat() * 0.8F + 0.1F;
                    final float var11 = this.random.nextFloat() * 0.8F + 0.1F;
                    EntityItem var14;

                    for (final float var12 = this.random.nextFloat() * 0.8F + 0.1F; var9.stackSize > 0; par1World.spawnEntityInWorld(var14))
                    {
                        int var13 = this.random.nextInt(21) + 10;

                        if (var13 > var9.stackSize)
                        {
                            var13 = var9.stackSize;
                        }

                        var9.stackSize -= var13;
                        var14 = new EntityItem(par1World, par2 + var10, par3 + var11, par4 + var12, new ItemStack(var9.getItem(), var13, var9.getItemDamage()));
                        final float var15 = 0.05F;
                        var14.motionX = (float) this.random.nextGaussian() * var15;
                        var14.motionY = (float) this.random.nextGaussian() * var15 + 0.2F;
                        var14.motionZ = (float) this.random.nextGaussian() * var15;

                        if (var9.hasTagCompound())
                        {
                            var14.getEntityItem().setTagCompound((NBTTagCompound) var9.getTagCompound().copy());
                        }
                    }
                }
            }
        }

        super.breakBlock(par1World, par2, par3, par4, par5, par6);
    }

    /**
     * Called upon block activation (right click on the block.)
     */
    @Override
    public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9)
    {
        Object var10 = par1World.getTileEntity(par2, par3, par4);

        if (var10 == null)
        {
            return true;
        }
        else if (par1World.isSideSolid(par2, par3 + 1, par4, ForgeDirection.DOWN))
        {
            return true;
        }
        else if (BlockTier2TreasureChest.isOcelotBlockingChest(par1World, par2, par3, par4))
        {
            return true;
        }
        else if (par1World.getBlock(par2 - 1, par3, par4) == this && (par1World.isSideSolid(par2 - 1, par3 + 1, par4, ForgeDirection.DOWN) || BlockTier2TreasureChest.isOcelotBlockingChest(par1World, par2 - 1, par3, par4)))
        {
            return true;
        }
        else if (par1World.getBlock(par2 + 1, par3, par4) == this && (par1World.isSideSolid(par2 + 1, par3 + 1, par4, ForgeDirection.DOWN) || BlockTier2TreasureChest.isOcelotBlockingChest(par1World, par2 + 1, par3, par4)))
        {
            return true;
        }
        else if (par1World.getBlock(par2, par3, par4 - 1) == this && (par1World.isSideSolid(par2, par3 + 1, par4 - 1, ForgeDirection.DOWN) || BlockTier2TreasureChest.isOcelotBlockingChest(par1World, par2, par3, par4 - 1)))
        {
            return true;
        }
        else if (par1World.getBlock(par2, par3, par4 + 1) == this && (par1World.isSideSolid(par2, par3 + 1, par4 + 1, ForgeDirection.DOWN) || BlockTier2TreasureChest.isOcelotBlockingChest(par1World, par2, par3, par4 + 1)))
        {
            return true;
        }
        else
        {
            if (par1World.getBlock(par2 - 1, par3, par4) == this)
            {
                var10 = new InventoryLargeChest("container.chestDouble", (TileEntityARChest) par1World.getTileEntity(par2 - 1, par3, par4), (IInventory) var10);
            }

            if (par1World.getBlock(par2 + 1, par3, par4) == this)
            {
                var10 = new InventoryLargeChest("container.chestDouble", (IInventory) var10, (TileEntityARChest) par1World.getTileEntity(par2 + 1, par3, par4));
            }

            if (par1World.getBlock(par2, par3, par4 - 1) == this)
            {
                var10 = new InventoryLargeChest("container.chestDouble", (TileEntityARChest) par1World.getTileEntity(par2, par3, par4 - 1), (IInventory) var10);
            }

            if (par1World.getBlock(par2, par3, par4 + 1) == this)
            {
                var10 = new InventoryLargeChest("container.chestDouble", (IInventory) var10, (TileEntityARChest) par1World.getTileEntity(par2, par3, par4 + 1));
            }

            if (par1World.isRemote)
            {
                return true;
            }
            else
            {
                par5EntityPlayer.displayGUIChest((IInventory) var10);
                return true;
            }
        }
    }

}
