package de.katzenpapst.amunra.block;

import java.util.ArrayList;
import java.util.Random;

import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;

public class SubBlockBush extends SubBlock  implements IGrowable, IShearable {

	public SubBlockBush(String name, String texture) {
		super(name, texture);
		// TODO Auto-generated constructor stub
	}

	public SubBlockBush(String name, String texture, String tool,
			int harvestLevel) {
		super(name, texture, tool, harvestLevel);
		// TODO Auto-generated constructor stub
	}

	public SubBlockBush(String name, String texture, String tool,
			int harvestLevel, float hardness, float resistance) {
		super(name, texture, tool, harvestLevel, hardness, resistance);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean isShearable(ItemStack item, IBlockAccess world, int x,
			int y, int z) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ArrayList<ItemStack> onSheared(ItemStack item, IBlockAccess world,
			int x, int y, int z, int fortune) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean func_149851_a(World p_149851_1_, int p_149851_2_,
			int p_149851_3_, int p_149851_4_, boolean isWorldRemote) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean func_149852_a(World p_149852_1_, Random p_149852_2_,
			int p_149852_3_, int p_149852_4_, int p_149852_5_) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void func_149853_b(World p_149853_1_, Random p_149853_2_,
			int p_149853_3_, int p_149853_4_, int p_149853_5_) {
		// TODO Auto-generated method stub

	}

	public boolean canPlaceOn(BlockMetaPair blockToCheck, int meta) {
		return true;
	}

	public boolean canPlaceOn(Block blockToCheck, int metaToCheck, int meta) {
		// TODO Auto-generated method stub
		return true;
	}

}
