package de.katzenpapst.amunra.mothership;

import net.minecraft.entity.player.EntityPlayerMP;

public interface ISendPacketsOnEntry {
    public void sendPacketsToClient(EntityPlayerMP player);
}
