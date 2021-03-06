package de.katzenpapst.amunra.item;

import net.minecraft.block.Block;

import java.util.List;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.entity.spaceship.EntityShuttle;
import micdoodle8.mods.galacticraft.api.entity.IRocketType.EnumRocketType;
import micdoodle8.mods.galacticraft.api.item.IHoldableItem;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.blocks.GCBlocks;
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore;
import micdoodle8.mods.galacticraft.core.tile.TileEntityLandingPad;
import micdoodle8.mods.galacticraft.core.util.EnumColor;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

public class ItemShuttle extends Item implements IHoldableItem {


    public ItemShuttle(String assetName)
    {
        super();
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
        this.setMaxStackSize(1);
        this.setTextureName("arrow");
        this.setUnlocalizedName(assetName);
    }

    @Override
    public CreativeTabs getCreativeTab()
    {
        return AmunRa.instance.arTab;
    }

    public EntityShuttle spawnRocketEntity(ItemStack stack, World world, double centerX, double centerY, double centerZ)
    {
        final EntityShuttle spaceship = new EntityShuttle(world, centerX, centerY, centerZ, EnumRocketType.values()[stack.getItemDamage()]);

        spaceship.setPosition(spaceship.posX, spaceship.posY + spaceship.getOnPadYOffset(), spaceship.posZ);
        world.spawnEntityInWorld(spaceship);


        if (spaceship.rocketType.getPreFueled())
        {
            spaceship.fuelTank.fill(new FluidStack(GalacticraftCore.fluidFuel, 2000), true);
        }
        else if (stack.hasTagCompound() && stack.getTagCompound().hasKey("RocketFuel"))
        {
            spaceship.fuelTank.fill(new FluidStack(GalacticraftCore.fluidFuel, stack.getTagCompound().getInteger("RocketFuel")), true);
        }

        // TODO inventory

        return spaceship;
    }

    /**
     * itemstack, player, world, x, y, z, side, hitX, hitY, hitZ
     */
    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        boolean padFound = false;
        TileEntity tile = null;

        if (world.isRemote && player instanceof EntityPlayerSP)
        {
            // TODO FIX THIS, or figure out what it does
            ClientProxyCore.playerClientHandler.onBuild(8, (EntityPlayerSP) player);
            return false;
        }
        else
        {
            float centerX = -1;
            float centerY = -1;
            float centerZ = -1;

            for (int i = -1; i < 2; i++)
            {
                for (int j = -1; j < 2; j++)
                {
                    final net.minecraft.block.Block id = world.getBlock(x + i, y, z + j);
                    int meta = world.getBlockMetadata(x + i, y, z + j);

                    if (id == GCBlocks.landingPadFull && meta == 0)
                    {
                        padFound = true;
                        tile = world.getTileEntity(x + i, y, z + j);

                        centerX = x + i + 0.5F;
                        centerY = y + 0.4F;
                        centerZ = z + j + 0.5F;

                        break;
                    }
                }

                if (padFound) break;
            }

            if (padFound)
            {
                //Check whether there is already a rocket on the pad
                if (tile instanceof TileEntityLandingPad)
                {
                    if (((TileEntityLandingPad)tile).getDockedEntity() != null)
                        return false;
                }
                else
                {
                    return false;
                }

                final EntityShuttle spaceship = spawnRocketEntity(itemStack, world, centerX, centerY, centerZ);

            }
            else
            {
                centerX = x + 0.5F;
                centerY = y + 0.4F;
                centerZ = z + 0.5F;

                final EntityShuttle spaceship = spawnRocketEntity(itemStack, world, centerX, centerY, centerZ);

            }
            if (!player.capabilities.isCreativeMode)
            {
                itemStack.stackSize--;

                if (itemStack.stackSize <= 0)
                {
                    itemStack = null;
                }
            }
        }
        return true;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        // par3List.add(new ItemStack(par1, 1, 0));

        for (int i = 0; i < EnumRocketType.values().length; i++)
        {
            par3List.add(new ItemStack(par1, 1, i));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack par1ItemStack)
    {
        return ClientProxyCore.galacticraftItem;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack par1ItemStack, EntityPlayer player, List par2List, boolean b)
    {

        EnumRocketType type = EnumRocketType.values()[par1ItemStack.getItemDamage()];

        if (!type.getTooltip().isEmpty())
        {
            par2List.add(type.getTooltip());
        }

        if (type.getPreFueled())
        {
            par2List.add(EnumColor.RED + "\u00a7o" + GCCoreUtil.translate("gui.creativeOnly.desc"));
        }

        if (par1ItemStack.hasTagCompound() && par1ItemStack.getTagCompound().hasKey("RocketFuel"))
        {
            EntityShuttle rocket = new EntityShuttle(FMLClientHandler.instance().getWorldClient(), 0, 0, 0, EnumRocketType.values()[par1ItemStack.getItemDamage()]);
            par2List.add(GCCoreUtil.translate("gui.message.fuel.name") + ": " + par1ItemStack.getTagCompound().getInteger("RocketFuel") + " / " + rocket.fuelTank.getCapacity());
        }
    }

    @Override
    public boolean shouldHoldLeftHandUp(EntityPlayer player)
    {
        return true;
    }

    @Override
    public boolean shouldHoldRightHandUp(EntityPlayer player)
    {
        return true;
    }

    @Override
    public boolean shouldCrouch(EntityPlayer player)
    {
        return true;
    }
}