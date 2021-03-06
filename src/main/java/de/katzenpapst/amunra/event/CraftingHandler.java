package de.katzenpapst.amunra.event;

import micdoodle8.mods.galacticraft.core.items.ItemBattery;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import de.katzenpapst.amunra.item.ARItems;
import de.katzenpapst.amunra.item.ItemAbstractRaygun;
import de.katzenpapst.amunra.item.ItemBaseBattery;
import de.katzenpapst.amunra.item.ItemCryogun;
import de.katzenpapst.amunra.item.ItemRaygun;

public class CraftingHandler {

public static CraftingHandler INSTANCE = new CraftingHandler();

	public CraftingHandler(){
	}

	@SubscribeEvent
	public void onCrafting(ItemCraftedEvent event){

		if(event.crafting.getItem() instanceof ItemRaygun) {
			handleRaygunCrafting(event, ARItems.raygun);
			return;
		}
		if(event.crafting.getItem() instanceof ItemCryogun) {
			handleRaygunCrafting(event, ARItems.cryogun);
			return;
		}
	}

	private void handleRaygunCrafting(ItemCraftedEvent event, ItemAbstractRaygun gun) {
		int indexGun = -1;
		int indexBattery = -1;

		for(int i=0;i<event.craftMatrix.getSizeInventory();i++) {
			ItemStack curItem = event.craftMatrix.getStackInSlot(i);
			if(curItem == null) continue;
			if(curItem.getItem() instanceof ItemAbstractRaygun) {
				indexGun = i;
			} else if(curItem.getItem() instanceof ItemBattery || curItem.getItem() instanceof ItemBaseBattery) {
				indexBattery = i;
			} /*else {
				return; // wrong recipe
			}*/
		}

		if(indexBattery != -1) {
			//float energyInOldBattery = ((ItemBattery)GCItems.battery).getElectricityStored(event.craftMatrix.getStackInSlot(indexBattery));
			ItemStack newBattery = event.craftMatrix.getStackInSlot(indexBattery);
			if(indexGun != -1) {
				ItemStack oldBattery = gun.getUsedBattery(event.craftMatrix.getStackInSlot(indexGun), true);
				event.player.inventory.addItemStackToInventory(oldBattery);
				/*
				// there is another gun in the ingredients, so this is recharging
				float energyInOldGun = gun.getElectricityStored(event.craftMatrix.getStackInSlot(indexGun));

				// new battery
				ItemStack newBattery = new ItemStack(GCItems.battery, 1);
				// give it the energy of the old gun
				((ItemBattery)GCItems.battery).setElectricity(newBattery, energyInOldGun);
				// return the battery to the player
				event.player.inventory.addItemStackToInventory(newBattery);
				*/
			} else {

			}
			// always set the energy of the battery from the ingredients to the finished gun
			//gun.setElectricity(event.crafting, energyInOldBattery);
			gun.setUsedBattery(event.crafting, newBattery);
		}


	}
}
