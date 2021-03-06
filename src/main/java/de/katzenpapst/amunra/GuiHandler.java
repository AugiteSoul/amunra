package de.katzenpapst.amunra;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.relauncher.Side;
import de.katzenpapst.amunra.client.gui.GuiAtomBattery;
import de.katzenpapst.amunra.client.gui.GuiMothershipSelection;
import de.katzenpapst.amunra.client.gui.GuiMothershipSettings;
import de.katzenpapst.amunra.client.gui.GuiRocketEngine;
import de.katzenpapst.amunra.inventory.ContainerAtomBattery;
import de.katzenpapst.amunra.inventory.ContainerMothershipSettings;
import de.katzenpapst.amunra.inventory.ContainerRocketEngine;
import de.katzenpapst.amunra.tile.TileEntityIsotopeGenerator;
import de.katzenpapst.amunra.tile.TileEntityMothershipController;
import de.katzenpapst.amunra.tile.TileEntityMothershipEngineJet;
import de.katzenpapst.amunra.tile.TileEntityMothershipSettings;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class GuiHandler implements IGuiHandler {



    public GuiHandler() {
        // TODO Auto-generated constructor stub
    }


    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {

        if(FMLCommonHandler.instance().getEffectiveSide() != Side.SERVER) {
            return null;
        }

        TileEntity tile = world.getTileEntity(x, y, z);

        switch(ID) {
        case GuiIds.GUI_ATOMBATTERY:
            return new ContainerAtomBattery(player.inventory, (TileEntityIsotopeGenerator)tile);
        case GuiIds.GUI_MS_ROCKET_ENGINE:
            return new ContainerRocketEngine(player.inventory, (TileEntityMothershipEngineJet)tile);
        case GuiIds.GUI_MS_SETTINGS:
            return new ContainerMothershipSettings(player.inventory, (TileEntityMothershipSettings)tile);
        }

        return null;
    }

    @Override
    // @SideOnly(Side.CLIENT)
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if(FMLCommonHandler.instance().getEffectiveSide() != Side.CLIENT) {
            return null;
        }

        TileEntity tile = world.getTileEntity(x, y, z);

        switch(ID) {
        case GuiIds.GUI_ATOMBATTERY:
            return new GuiAtomBattery(player.inventory, (TileEntityIsotopeGenerator)tile);
        case GuiIds.GUI_MOTHERSHIPCONTROLLER:
            List<CelestialBody> possibleCelestialBodies = new ArrayList<CelestialBody>();
            return new GuiMothershipSelection(possibleCelestialBodies, (TileEntityMothershipController)tile, world);
        case GuiIds.GUI_MS_ROCKET_ENGINE:
            return new GuiRocketEngine(player.inventory, (TileEntityMothershipEngineJet)tile);
        case GuiIds.GUI_MS_SETTINGS:
            return new GuiMothershipSettings(player.inventory, (TileEntityMothershipSettings)tile);
        }

        return null;
    }

}
