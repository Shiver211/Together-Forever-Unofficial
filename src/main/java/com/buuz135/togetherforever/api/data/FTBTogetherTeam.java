package com.buuz135.togetherforever.api.data;

import com.buuz135.togetherforever.api.IPlayerInformation;
import com.buuz135.togetherforever.api.ITogetherTeam;
import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.data.ForgeTeam;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * An {@link ITogetherTeam} adapter backed by a FTB Library {@link ForgeTeam}.
 * <p>
 * Team membership is fully owned by FTB Utilities / FTB Library, so all mutation and
 * NBT serialization methods are no-ops here. This wrapper only exposes the read-only
 * view that the sync engine needs: the members, the team name and the owner.
 */
public class FTBTogetherTeam implements ITogetherTeam {

    private final ForgeTeam team;

    public FTBTogetherTeam(ForgeTeam team) {
        this.team = team;
    }

    public ForgeTeam getForgeTeam() {
        return team;
    }

    @Override
    public void addPlayer(IPlayerInformation playerInformation) {
        // Membership is managed by FTB, nothing to do here.
    }

    @Override
    public void removePlayer(IPlayerInformation playerInformation) {
        // Membership is managed by FTB, nothing to do here.
    }

    @Override
    public void removePlayer(UUID playerUUID) {
        // Membership is managed by FTB, nothing to do here.
    }

    @Override
    public Collection<IPlayerInformation> getPlayers() {
        List<IPlayerInformation> players = new ArrayList<>();
        for (ForgePlayer member : team.getMembers()) {
            DefaultPlayerInformation information = new DefaultPlayerInformation();
            information.setUUID(member.getId());
            information.setName(member.getName());
            players.add(information);
        }
        return players;
    }

    @Override
    public NBTTagCompound getNBTTag() {
        // FTB persists its own teams, nothing for us to store.
        return new NBTTagCompound();
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        // FTB persists its own teams, nothing for us to read.
    }

    @Override
    public String getTeamName() {
        return team.getId();
    }

    @Override
    public UUID getOwner() {
        ForgePlayer owner = team.getOwner();
        return owner == null ? null : owner.getId();
    }
}
