package de.katzenpapst.amunra.proxy;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import de.katzenpapst.amunra.proxy.ARSidedProxy.ParticleType;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class ARSidedProxy {

    public enum ParticleType {
        PT_MOTHERSHIP_JET_FLAME,
        PT_MOTHERSHIP_JET_SMOKE
    }

    public void preInit(FMLPreInitializationEvent event)
    {

    }

    public void init(FMLInitializationEvent event)
    {
    }


    public void postInit(FMLPostInitializationEvent event)
    {

    }

    public void spawnParticles(ParticleType type, World world, Vector3 pos, Vector3 motion) {
        // noop
    }

    public void playTileEntitySound(TileEntity tile, ResourceLocation resource) {
        // noop
    }
}
