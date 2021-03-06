package de.katzenpapst.amunra.event;

import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.util.OxygenUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import de.katzenpapst.amunra.mob.DamageSourceAR;
import de.katzenpapst.amunra.mob.entity.IEntityNonOxygenBreather;

public class EventHandlerAR {

    @SubscribeEvent
    public void entityLivingEvent(LivingUpdateEvent event)
    {
        EntityLivingBase entityLiving = event.entityLiving;
        if(!(entityLiving instanceof IEntityNonOxygenBreather)) {
            return;
        }

        if (entityLiving.ticksExisted % 100 == 0)
        {
            CelestialBody body;
            boolean isInSealedArea = OxygenUtil.isAABBInBreathableAirBlock(entityLiving);

            // entityLiving.worldObj.provider
            if (entityLiving.worldObj.provider instanceof IGalacticraftWorldProvider) {
                body = ((IGalacticraftWorldProvider)entityLiving.worldObj.provider).getCelestialBody();
            } else {
                body = GalacticraftCore.planetOverworld;
            }
            if(!((IEntityNonOxygenBreather)entityLiving).canBreatheIn(body.atmosphere, isInSealedArea)) {
                // should I add these events about suffocation that GC does?
                entityLiving.attackEntityFrom(DamageSourceAR.dsSuffocate, 1);
            }
        }
    }



    // event.
    /*
		if (event.crafting.getItem() == mainClass.roymondBucket){
			System.out.println("Desired item has been crafted.");
			for (int i=0; i< event.player.inventory.mainInventory.length; i++){
				System.out.println(event.player.inventory.mainInventory[i]);
				//Prints each slot out on a line.
			}

			if (event.player.inventory.hasItem(Items.bucket)){
				System.out.println("Player has Item. Cleared to remove it");
			}

	/*
	@SubscribeEvent
	public void onPlayerItemCrafted(PlayerEvent.ItemCraftedEvent event)
	{
	}*/
}
