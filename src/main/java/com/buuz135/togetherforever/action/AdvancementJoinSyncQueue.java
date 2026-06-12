package com.buuz135.togetherforever.action;

import com.buuz135.togetherforever.api.TogetherForeverAPI;
import com.buuz135.togetherforever.config.TogetherForeverConfig;
import net.minecraft.advancements.Advancement;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

public class AdvancementJoinSyncQueue {

    private final Queue<Task> tasks = new ArrayDeque<>();
    private final Set<UUID> activeTargets = new HashSet<>();

    public void enqueue(EntityPlayerMP target, EntityPlayerMP source, Iterable<Advancement> advancements) {
        UUID targetId = target.getUniqueID();
        if (activeTargets.add(targetId)) {
            target.sendMessage(new TextComponentString(TextFormatting.GOLD + "Advancement sync started."));
        }
        tasks.add(new Task(targetId, source.getUniqueID(), advancements.iterator()));
    }

    public void tick() {
        int remaining = TogetherForeverConfig.advancementJoinSyncPerTick;
        while (remaining > 0 && !tasks.isEmpty()) {
            Task task = tasks.peek();
            if (!task.processNextAdvancement()) {
                tasks.poll();
                if (!hasPendingTask(task.targetId)) {
                    finishTarget(task.targetId);
                }
                continue;
            }
            --remaining;
        }
    }

    private boolean hasPendingTask(UUID targetId) {
        for (Task task : tasks) {
            if (task.targetId.equals(targetId)) return true;
        }
        return false;
    }

    private void finishTarget(UUID targetId) {
        activeTargets.remove(targetId);
        EntityPlayerMP target = TogetherForeverAPI.getInstance().getPlayer(targetId);
        if (target != null) {
            target.sendMessage(new TextComponentString(TextFormatting.GREEN + "Advancement sync finished."));
        }
    }

    private static class Task {

        private final UUID targetId;
        private final UUID sourceId;
        private final Iterator<Advancement> advancements;

        private Task(UUID targetId, UUID sourceId, Iterator<Advancement> advancements) {
            this.targetId = targetId;
            this.sourceId = sourceId;
            this.advancements = advancements;
        }

        private boolean processNextAdvancement() {
            EntityPlayerMP target = TogetherForeverAPI.getInstance().getPlayer(targetId);
            EntityPlayerMP source = TogetherForeverAPI.getInstance().getPlayer(sourceId);
            if (target == null || source == null || !advancements.hasNext()) return false;
            Advancement advancement = advancements.next();
            for (String crit : source.getAdvancements().getProgress(advancement).getCompletedCriteria()) {
                target.getAdvancements().grantCriterion(advancement, crit);
            }
            return true;
        }
    }
}
