package de.katzenpapst.amunra.mothership;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.prefab.entity.EntitySpaceshipBase;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.WorldProviderSpace;
import micdoodle8.mods.galacticraft.api.vector.BlockVec3;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.api.world.IExitHeight;
import micdoodle8.mods.galacticraft.api.world.IOrbitDimension;
import micdoodle8.mods.galacticraft.api.world.ISolarLevel;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.blocks.BlockSpinThruster;
import micdoodle8.mods.galacticraft.core.blocks.GCBlocks;
import micdoodle8.mods.galacticraft.core.client.SkyProviderOrbit;
import micdoodle8.mods.galacticraft.core.dimension.OrbitSpinSaveData;
import micdoodle8.mods.galacticraft.core.dimension.WorldProviderOrbit;
import micdoodle8.mods.galacticraft.core.entities.EntityLanderBase;
import micdoodle8.mods.galacticraft.core.entities.player.FreefallHandler;
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStatsClient;
import micdoodle8.mods.galacticraft.core.network.PacketSimple;
import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import micdoodle8.mods.galacticraft.core.util.GCLog;
import micdoodle8.mods.galacticraft.core.util.RedstoneUtil;
import micdoodle8.mods.galacticraft.core.world.gen.ChunkProviderOrbit;
import micdoodle8.mods.galacticraft.core.world.gen.WorldChunkManagerOrbit;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.client.IRenderHandler;

public abstract class WorldProviderZeroG  extends WorldProviderSpace implements ISolarLevel, IExitHeight {



    private static final float GFORCE = 9.81F / 400F; //gravity in metres per tick squared

    //private OrbitSpinSaveData savefile;
    //public boolean doSpinning = true;
    //private float angularVelocityRadians = 0F;
    //private float skyAngularVelocity = (float) (this.angularVelocityRadians * 180 / Math.PI);
    //private float angularVelocityTarget = 0F;
    //private float angularVelocityAccel = 0F;
    //private double spinCentreX;
    //private double spinCentreZ;
    //private float momentOfInertia;
    //private float massCentreX;
    /*private float massCentreZ;*/
    protected int ssBoundsMaxX;
    protected int ssBoundsMinX;
    protected int ssBoundsMaxY;
    protected int ssBoundsMinY;
    protected int ssBoundsMaxZ;
    protected int ssBoundsMinZ;
/*
    private LinkedList<BlockVec3> thrustersPlus = new LinkedList();
    private LinkedList<BlockVec3> thrustersMinus = new LinkedList();
    private BlockVec3 oneSSBlock;
    */
    //private HashSet<BlockVec3> stationBlocks = new HashSet();

    // private HashSet<BlockVec3> checked = new HashSet<BlockVec3>();

    private float artificialG;
    //Used to make continuous particles + thrust sounds at the spin thrusters in this dimension
    //If false, make particles + sounds occasionally in small bursts, just for fun (micro attitude changes)
    //see: BlockSpinThruster.randomDisplayTick()
    //public boolean thrustersFiring = false;
    private boolean dataNotLoaded = true;
    private List<Entity> loadedEntities = new LinkedList();
    private double pPrevMotionX = 0D;
    public double pPrevMotionY = 0D;
    private double pPrevMotionZ = 0D;
    private int pjumpticks = 0;
    private boolean pWasOnGround = false;

    @Override
    public Vector3 getFogColor()
    {
        return new Vector3(0, 0, 0);
    }

    @Override
    public Vector3 getSkyColor()
    {
        return new Vector3(0, 0, 0);
    }

    @Override
    public boolean canRainOrSnow()
    {
        return false;
    }

    @Override
    public boolean hasSunset()
    {
        return false;
    }

    @Override
    public boolean shouldForceRespawn()
    {
        return !ConfigManagerCore.forceOverworldRespawn;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public float getStarBrightness(float par1)
    {
        final float var2 = this.worldObj.getCelestialAngle(par1);
        float var3 = 1.0F - (MathHelper.cos(var2 * (float) Math.PI * 2.0F) * 2.0F + 0.25F);

        if (var3 < 0.0F)
        {
            var3 = 0.0F;
        }

        if (var3 > 1.0F)
        {
            var3 = 1.0F;
        }

        return var3 * var3 * 0.5F + 0.3F;
    }

    @Override
    public boolean isSkyColored()
    {
        return false;
    }

    @Override
    public double getHorizon()
    {
        return 44.0D;
    }

    @Override
    public int getAverageGroundLevel()
    {
        return 64;
    }

    @Override
    public boolean canCoordinateBeSpawn(int var1, int var2)
    {
        return true;
    }

    //Overriding only in case the Galacticraft API is not up-to-date
    //(with up-to-date API this makes zero difference)
    @Override
    public boolean isSurfaceWorld()
    {
        return (this.worldObj == null) ? false : this.worldObj.isRemote;
    }

    //Overriding only in case the Galacticraft API is not up-to-date
    //(with up-to-date API this makes zero difference)
    @Override
    public boolean canRespawnHere()
    {
        return false;
    }

    //Overriding only in case the Galacticraft API is not up-to-date
    //(with up-to-date API this makes zero difference)
    @Override
    public int getRespawnDimension(EntityPlayerMP player)
    {
        return this.shouldForceRespawn() ? this.dimensionId : 0;
    }

    @Override
    public float getGravity()
    {
        return 0.075F;//0.073F;
    }

    @Override
    public boolean hasBreathableAtmosphere()
    {
        return false;
    }

    @Override
    public double getMeteorFrequency()
    {
        return 0;
    }

    @Override
    public double getFuelUsageMultiplier()
    {
        return 0.5D;
    }

    @Override
    public double getYCoordinateToTeleport()
    {
        return 1200;
    }

    @Override
    public boolean canSpaceshipTierPass(int tier)
    {
        return tier > 0;
    }

    @Override
    public float getFallDamageModifier()
    {
        return 0.4F;
    }

    @Override
    public float getSoundVolReductionAmount()
    {
        return 50.0F;
    }

    @SideOnly(Side.CLIENT)
    public void preVanillaMotion(EntityPlayerSP p)
    {
        FreefallHandler.setupFreefallPre(p);
        this.pWasOnGround = p.onGround;
    }

    @SideOnly(Side.CLIENT)
    public void postVanillaMotion(EntityPlayerSP p)
    {
        GCPlayerStatsClient stats = GCPlayerStatsClient.get(p);
        boolean freefall = stats.inFreefall;
        if (freefall) p.ySize = 0F;  //Undo the sneak height adjust
        freefall = this.testFreefall(p, freefall);
        stats.inFreefall = freefall;
        stats.inFreefallFirstCheck = true;

        boolean doGravity = true;

        if (freefall)
        {
            doGravity = false;
            this.pjumpticks = 0;

            //Reverse effects of deceleration
            p.motionX /= 0.91F;
            p.motionZ /= 0.91F;
            p.motionY /= 0.9800000190734863D;

            //Do freefall motion
            if (!p.capabilities.isCreativeMode)
            {
                FreefallHandler.freefallMotion(p);
            }
            else
            {
                //Half the normal acceleration in Creative mode
                double dx = p.motionX - this.pPrevMotionX;
                double dy = p.motionY - this.pPrevMotionY;
                double dz = p.motionZ - this.pPrevMotionZ;
                p.motionX -= dx / 2;
                p.motionY -= dy / 2;
                p.motionZ -= dz / 2;

                if (p.motionX > 1.2F)
                {
                    p.motionX = 1.2F;
                }
                if (p.motionX < -1.2F)
                {
                    p.motionX = -1.2F;
                }
                if (p.motionY > 0.7F)
                {
                    p.motionY = 0.7F;
                }
                if (p.motionY < -0.7F)
                {
                    p.motionY = -0.7F;
                }
                if (p.motionZ > 1.2F)
                {
                    p.motionZ = 1.2F;
                }
                if (p.motionZ < -1.2F)
                {
                    p.motionZ = -1.2F;
                }
            }
            //TODO: Think about endless drift?
            //Player may run out of oxygen - that will kill the player eventually if can't get back to SS
            //Could auto-kill + respawn the player if floats too far away (config option whether to lose items or not)
            //But we want players to be able to enjoy the view of the spinning space station from the outside
            //Arm and leg movements could start tumbling the player?
        }
        else
        //Not freefall - within arm's length of something or jumping
        {
            double dy = p.motionY - this.pPrevMotionY;
            //if (p.motionY < 0 && this.pPrevMotionY >= 0) p.posY -= p.motionY;
            //if (p.motionY != 0) p.motionY = this.pPrevMotionY;
            if (p.movementInput.jump)
            {
                if (p.onGround || this.pWasOnGround)
                {
                    this.pjumpticks = 20;
                    p.motionY -= 0.015D;
                    p.onGround = false;
                    p.posY -= 0.1D;
                    p.boundingBox.offset(0, -0.1D, 0);
                }
                else
                {
                    p.motionY += 0.015D;
                    if (this.pjumpticks == 0)
                    {
                        p.motionY -= dy;
                    }
                }
            }
            else if (p.movementInput.sneak)
            {
                if (!p.onGround)
                {
                    p.motionY -= 0.015D;
                    if (!FreefallHandler.sneakLast )
                    {
                        p.boundingBox.offset(0D, 0.0268D, 0D);
                        FreefallHandler.sneakLast = true;
                    }
                }
                this.pjumpticks = 0;
            }
            else if (FreefallHandler.sneakLast)
            {
                FreefallHandler.sneakLast = false;
                p.boundingBox.offset(0D, -0.0268D, 0D);
            }

            if (this.pjumpticks > 0)
            {
                this.pjumpticks--;
                p.motionY -= dy;
                if (this.pjumpticks >= 17)
                {
                    p.motionY += 0.03D;
                }
            }
        }


        this.pPrevMotionX = p.motionX;
        this.pPrevMotionY = p.motionY;
        this.pPrevMotionZ = p.motionZ;
    }

    @SideOnly(Side.CLIENT)
    private boolean testFreefall(EntityPlayerSP p, boolean flag)
    {
        if (this.pjumpticks > 0 || (this.pWasOnGround && p.movementInput.jump))
        {
            return false;
        }

        if (p.ridingEntity != null)
        {
            Entity e = p.ridingEntity;
            if (e instanceof EntitySpaceshipBase)
                return ((EntitySpaceshipBase)e).getLaunched();
            if (e instanceof EntityLanderBase)
                return false;
            //TODO: should check whether lander has landed (whatever that means)
            //TODO: could check other ridden entities - every entity should have its own freefall check :(
        }

        //This is an "on the ground" check
        if (!flag)
        {
            return false;
        }
        else
        {
            float rY = p.rotationYaw % 360F;
            double zreach = 0D;
            double xreach = 0D;
            if (rY < 80F || rY > 280F) zreach = 0.2D;
            if (rY < 170F && rY > 10F) xreach = 0.2D;
            if (rY < 260F && rY > 100F) zreach = -0.2D;
            if (rY < 350F && rY > 190F) xreach = -0.2D;
            AxisAlignedBB playerReach = p.boundingBox.addCoord(xreach, 0, zreach);

            if (playerReach.maxX >= this.ssBoundsMinX && playerReach.minX <= this.ssBoundsMaxX && playerReach.maxY >= this.ssBoundsMinY && playerReach.minY <= this.ssBoundsMaxY && playerReach.maxZ >= this.ssBoundsMinZ && playerReach.minZ <= this.ssBoundsMaxZ)
            //Player is somewhere within the space station boundaries
            {
                //Check if the player's bounding box is in the same block coordinates as any non-vacuum block (including torches etc)
                //If so, it's assumed the player has something close enough to grab onto, so is not in freefall
                //Note: breatheable air here means the player is definitely not in freefall
                int xm = MathHelper.floor_double(playerReach.minX);
                int xx = MathHelper.floor_double(playerReach.maxX);
                int ym = MathHelper.floor_double(playerReach.minY);
                int yy = MathHelper.floor_double(playerReach.maxY);
                int zm = MathHelper.floor_double(playerReach.minZ);
                int zz = MathHelper.floor_double(playerReach.maxZ);
                for (int x = xm; x <= xx; x++)
                {
                    for (int y = ym; y <= yy; y++)
                    {
                        for (int z = zm; z <= zz; z++)
                        {
                            //Blocks.air is hard vacuum - we want to check for that, here
                            Block b = this.worldObj.getBlock(x, y, z);
                            if (Blocks.air != b && GCBlocks.brightAir != b)
                            {
                                return false;
                            }
                        }
                    }
                }
            }
        }



        return true;
    }

    public void readFromNBT(NBTTagCompound nbt)
    {

        this.ssBoundsMinX = nbt.getInteger("boundsMinX");
        this.ssBoundsMaxX = nbt.getInteger("boundsMaxX");
        this.ssBoundsMinZ = nbt.getInteger("boundsMinZ");
        this.ssBoundsMaxZ = nbt.getInteger("boundsMaxZ");
        this.ssBoundsMinY = nbt.getInteger("boundsMinY");
        this.ssBoundsMaxY = nbt.getInteger("boundsMaxY");
    }

    public void writeToNBT(NBTTagCompound nbt)
    {
        nbt.setInteger("boundsMinX", ssBoundsMinX);
        nbt.setInteger("boundsMinY", ssBoundsMinY);
        nbt.setInteger("boundsMinZ", ssBoundsMinZ);
        nbt.setInteger("boundsMaxX", ssBoundsMaxX);
        nbt.setInteger("boundsMaxY", ssBoundsMaxY);
        nbt.setInteger("boundsMaxZ", ssBoundsMaxZ);
    }


    @Override
    public float getThermalLevelModifier()
    {
        return 0;
    }

    @Override
    public float getWindLevel()
    {
        return 0.1F;
    }

}
