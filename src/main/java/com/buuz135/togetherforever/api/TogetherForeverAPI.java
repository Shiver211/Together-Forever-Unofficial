/*
 * This file is part of Hot or Not.
 *
 * Copyright 2018, Buuz135
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.buuz135.togetherforever.api;

import com.buuz135.togetherforever.api.data.DataManager;
import com.buuz135.togetherforever.api.data.FTBTogetherTeam;
import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.data.Universe;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * A collection of useful methods of the API
 */
public class TogetherForeverAPI {

    public static final String MOD_ID = "togetherforever";
    public static final String API_VERSION = "1.1";
    public static final String API_ID = MOD_ID + "api";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    private static TogetherForeverAPI ourInstance = new TogetherForeverAPI();

    private TogetherForeverAPI() {
    }

    public static TogetherForeverAPI getInstance() {
        return ourInstance;
    }

    /**
     * Gets the team of a player from the FTB Library team system
     *
     * @param playerUUID The UUID of the player to get the team from
     * @return The team of the player, null if the player isn't in a team
     */
    @Nullable
    public ITogetherTeam getPlayerTeam(UUID playerUUID) {
        if (!Universe.loaded()) return null;
        ForgePlayer player = Universe.get().getPlayer(playerUUID);
        if (player == null || !player.hasTeam()) return null;
        return new FTBTogetherTeam(player.team);
    }

    /**
     * Adds a player to a OfflineSyncRecovery
     *
     * @param recoveryClass      The class of the OfflineSyncRecovery
     * @param iPlayerInformation The PlayerInformation that need to be stored
     * @param compound           The recovery information that needs to be stored
     */
    public void addPlayerToOfflineRecovery(Class<? extends IOfflineSyncRecovery> recoveryClass, IPlayerInformation iPlayerInformation, NBTTagCompound compound) {
        DataManager manager = getDataManager(getWorld());
        if (manager == null) return;
        for (IOfflineSyncRecovery recovery : manager.getRecoveries()) {
            if (recovery.getClass().equals(recoveryClass)) {
                recovery.storeMissingPlayer(iPlayerInformation, compound);
                manager.markDirty();
                return;
            }
        }
        try {
            IOfflineSyncRecovery recovery = recoveryClass.newInstance();
            recovery.storeMissingPlayer(iPlayerInformation, compound);
            manager.getRecoveries().add(recovery);
            manager.markDirty();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the generic DataManager
     *
     * @param world A world of to get the DataManager, it can be any world
     * @return the DataManager
     */
    public DataManager getDataManager(World world) {
        if (world == null) return null;
        DataManager dataManager = (DataManager) world.getMapStorage().getOrLoadData(DataManager.class, DataManager.NAME);
        if (dataManager == null) {
            dataManager = new DataManager();
            world.getMapStorage().setData(DataManager.NAME, dataManager);
        }
        return dataManager;
    }

    /**
     * Gets the player entity
     *
     * @param string The name of the player
     * @return The EntityPlayerMP, null if the player is offline
     */
    @Nullable
    public EntityPlayerMP getPlayer(String string) {
        return FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(string);
    }

    /**
     * Gets the player entity
     *
     * @param uuid The UUID of the player
     * @return The EntityPlayerMP, null if the player is offline
     */
    @Nullable
    public EntityPlayerMP getPlayer(UUID uuid) {
        return FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(uuid);
    }

    /**
     * Gets the world overworld
     *
     * @return The overworld
     */
    public World getWorld() {
        if (DimensionManager.getWorld(0) == null || FMLCommonHandler.instance().getMinecraftServerInstance() == null)
            return null;
        return FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(0);
    }
}
