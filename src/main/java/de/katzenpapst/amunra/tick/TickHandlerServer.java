package de.katzenpapst.amunra.tick;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.ShuttleTeleportHelper;
import de.katzenpapst.amunra.entity.spaceship.EntityShuttle;
import de.katzenpapst.amunra.mob.DamageSourceAR;
import de.katzenpapst.amunra.mothership.ISendPacketsOnEntry;
import de.katzenpapst.amunra.mothership.Mothership;
import de.katzenpapst.amunra.mothership.MothershipWorldData;
import de.katzenpapst.amunra.mothership.MothershipWorldProvider;
import de.katzenpapst.amunra.mothership.WorldProviderZeroG;
import de.katzenpapst.amunra.network.packet.PacketSimpleAR;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.prefab.entity.EntityAutoRocket;
import micdoodle8.mods.galacticraft.api.prefab.entity.EntitySpaceshipBase.EnumLaunchPhase;
import micdoodle8.mods.galacticraft.api.prefab.entity.EntitySpaceshipBase.RocketLaunchEvent;
import micdoodle8.mods.galacticraft.api.vector.BlockVec3;
import micdoodle8.mods.galacticraft.api.world.IOrbitDimension;
import micdoodle8.mods.galacticraft.core.blocks.BlockUnlitTorch;
import micdoodle8.mods.galacticraft.core.dimension.WorldProviderOrbit;
import micdoodle8.mods.galacticraft.core.oxygen.ThreadFindSeal;
import micdoodle8.mods.galacticraft.core.tile.TileEntityOxygenSealer;
import micdoodle8.mods.galacticraft.core.util.WorldUtil;
import micdoodle8.mods.galacticraft.core.wrappers.ScheduledBlockChange;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class TickHandlerServer {

    public static MothershipWorldData mothershipData;

    public static void restart() {
        mothershipData = null;
        /*
        if(FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            AmunRa.instance.setClientMothershipData(null);
        }*/
    }

    public TickHandlerServer() {
        // TODO Auto-generated constructor stub
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event)
    {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server == null) return;
        if (event.phase == TickEvent.Phase.START)
        {
            if (TickHandlerServer.mothershipData == null)
            {
                World world = server.worldServerForDimension(0);
                TickHandlerServer.mothershipData = (MothershipWorldData) world.mapStorage.loadData(MothershipWorldData.class, MothershipWorldData.saveDataID);
                //AmunRa.instance.mothershipRegistry = TickHandlerServer.mothershipData;
                if(TickHandlerServer.mothershipData == null) {
                    // TickHandlerServer.ssData = ProceduralGalaxy.instance.getSolarSystemManager();//new SolarSystemManager();
                    TickHandlerServer.mothershipData = new MothershipWorldData(MothershipWorldData.saveDataID);
                    //AmunRa.instance.mothershipRegistry = TickHandlerServer.mothershipData;
                    world.mapStorage.setData(MothershipWorldData.saveDataID, TickHandlerServer.mothershipData );
                }
            } else {
                //server.worldServerForDimension(0).getTotalWorldTime()
                // this works
                // tick all the motherships
                TickHandlerServer.mothershipData.tickAllMotherships();
            }
        }
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerLoggedInEvent event)
    {
        if(FMLCommonHandler.instance().getSide() != Side.SERVER) {
            return;
        }

        if (event.player instanceof EntityPlayerMP)
        {
            EntityPlayerMP thePlayer = (EntityPlayerMP) event.player;
            // Send list of motherships to player here

            NBTTagCompound msData = new NBTTagCompound();

            TickHandlerServer.mothershipData.writeToNBT(msData);

            AmunRa.packetPipeline.sendTo(new PacketSimpleAR(PacketSimpleAR.EnumSimplePacket.C_UPDATE_MOTHERSHIP_LIST, new Object[] {
                    msData
            }), thePlayer);

            if (event.player.worldObj.provider instanceof ISendPacketsOnEntry)
            {
                ((ISendPacketsOnEntry) event.player.worldObj.provider).sendPacketsToClient((EntityPlayerMP) event.player);
            }
        }


    }

    @SubscribeEvent
    public void onPlayerChangedDimensionEvent(PlayerChangedDimensionEvent event) {
        // this is somewhat of a hack
        //event.player
        if(event.player.worldObj.isRemote) {
            return;
        }
        MinecraftServer mcServer = FMLCommonHandler.instance().getMinecraftServerInstance();
        WorldServer ws = mcServer.worldServerForDimension(event.toDim);
        if(ws.provider instanceof ISendPacketsOnEntry && event.player instanceof EntityPlayerMP) {
            ((ISendPacketsOnEntry)ws.provider).sendPacketsToClient((EntityPlayerMP) event.player);
        }
        /*if(ws.provider instanceof MothershipWorldProvider) {

            //System.out.println("MUHKUH "+event.toDim+" isRemote: "+event.player.worldObj.isRemote);
            ChunkCoordinates spawn = event.player.getBedLocation(event.toDim);
            if(spawn == null) {
                System.out.println("Player has no spawn on "+event.toDim);
            } else {
                System.out.println("Player spawn on "+event.toDim+" is "+spawn.toString());
            }
        }*/
    }


    @SubscribeEvent
    public void onWorldTick(WorldTickEvent event)
    {
        if (event.phase == Phase.START)
        {
            final WorldServer world = (WorldServer) event.world;

            if (world.provider instanceof MothershipWorldProvider)
            {
                // ((MothershipWorldProvider)world.provider).update();
                final Object[] entityList = world.loadedEntityList.toArray();

                for (final Object o : entityList)
                {
                    if (o instanceof Entity)
                    {
                        final Entity e = (Entity) o;
                        // failsafe?
                        if (e.worldObj.provider instanceof MothershipWorldProvider)
                        {
                            if(e.posY < 0) {
                                CelestialBody parent = ((MothershipWorldProvider)e.worldObj.provider).getParent();
                                if(parent == null) {
                                    // jumped off mid-transit
                                    if(e instanceof EntityLivingBase) {
                                        ((EntityLivingBase)e).attackEntityFrom(DamageSourceAR.dsFallOffShip, 9001);
                                    } else {
                                        e.worldObj.removeEntity(e);
                                    }
                                } else {

                                    if(!parent.getReachable() || (parent.getTierRequirement() > AmunRa.instance.confMaxMothershipTier)) {
                                        // crash into
                                        if(e instanceof EntityLivingBase) {
                                            ((EntityLivingBase)e).attackEntityFrom(DamageSourceAR.getDSCrashIntoPlanet(parent), 9001);
                                        } else {
                                            e.worldObj.removeEntity(e);
                                        }
                                    } else {
                                        if(e instanceof EntityPlayerMP && e.ridingEntity instanceof EntityShuttle) {
                                            sendPlayerInShuttleToPlanet((EntityPlayerMP)e, (EntityShuttle)e.ridingEntity, world, parent.getDimensionID());
                                        } else if(e instanceof EntityShuttle && e.riddenByEntity instanceof EntityPlayerMP) {
                                            sendPlayerInShuttleToPlanet((EntityPlayerMP)e.riddenByEntity, (EntityShuttle)e, world, parent.getDimensionID());
                                        } else {
                                            // go there naked, as GC intended
                                            WorldUtil.transferEntityToDimension(e, parent.getDimensionID(), world, false, null);
                                        }
                                    }
                                }
                            }  else { // if(e.posY < 0) {
                                if(e instanceof EntityAutoRocket) {
                                    EntityAutoRocket rocket = (EntityAutoRocket)e;
                                    if(!(rocket instanceof EntityShuttle)) {
                                        // prevent them from launching, ever

                                        if(rocket.launchPhase == EnumLaunchPhase.IGNITED.ordinal()) {
                                            rocket.cancelLaunch();
                                        } else if(rocket.launchPhase == EnumLaunchPhase.LAUNCHED.ordinal()) {
                                            rocket.dropShipAsItem();
                                        }
                                    } else {
                                        Mothership ship = (Mothership) ((MothershipWorldProvider)e.worldObj.provider).getCelestialBody();
                                        if(ship.isInTransit()) {
                                            if(rocket.launchPhase == EnumLaunchPhase.IGNITED.ordinal()) {
                                                rocket.cancelLaunch();
                                            } else if(rocket.launchPhase == EnumLaunchPhase.LAUNCHED.ordinal()) {
                                                rocket.dropShipAsItem();
                                            }
                                        }
                                    }
                                }
                            }



                        } // if (e.worldObj.provider instanceof MothershipWorldProvider)
                    } // if (o instanceof Entity)
                } // for (final Object o : entityList)
            } // if (world.provider instanceof MothershipWorldProvider)
        } // (event.phase == Phase.START)
    }

    protected void sendPlayerInShuttleToPlanet(EntityPlayerMP player, EntityShuttle shuttle, World world, int dimensionID) {
        if(world.isRemote) {
            return;
        }
        // player.dismountEntity(shuttle);
        shuttle.riddenByEntity = null;
        player.ridingEntity = null;
        shuttle.setGCPlayerStats(player);
        shuttle.setDead();

        ShuttleTeleportHelper.transferEntityToDimension(player, dimensionID, (WorldServer) world);
    }

    @SubscribeEvent
    public void onClientDisconnectionFromServer(ClientDisconnectionFromServerEvent event) {
        System.out.println("onClientDisconnectionFromServer");
        //active = false;
    }
}
