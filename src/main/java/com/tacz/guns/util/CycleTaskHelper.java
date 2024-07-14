package com.tacz.guns.util;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BooleanSupplier;

public final class CycleTaskHelper {
    private static final List<CycleTaskTicker> CYCLE_TASKS = new LinkedList<>();

    /**
     * Loops through the tasks according to the time interval provided. It will be called once immediately.
     *
     * @param task     A task that loops through a task will decide whether to continue the loop for the next time based on the boolean value returned.
     *                 If false is returned, the loop will not continue.
     * @param periodMs The interval in milliseconds between recurring calls.
     * @param cycles   Maximum number of cycles.
     */
    public static void addCycleTask(BooleanSupplier task, long periodMs, int cycles) {
        CycleTaskHelper.CycleTaskTicker ticker = new CycleTaskHelper.CycleTaskTicker(task, periodMs, cycles);
        if (ticker.tick()) {
            CYCLE_TASKS.add(ticker);
        }
    }

    public static void tick() {
        // Iterate, call and delete tasks that return false
        CYCLE_TASKS.removeIf(ticker -> !ticker.tick());
    }

    private static class CycleTaskTicker {
        private final BooleanSupplier task;
        private final float periodS;
        private final int cycles;
        private long timestamp = -1;
        private float compensation = 0;
        private int count = 0;

        private CycleTaskTicker(BooleanSupplier task, long periodMs, int cycles) {
            this.task = task;
            this.periodS = periodMs / 1000f;
            this.cycles = cycles;
        }

        private boolean tick() {
            if (timestamp == -1) {
                timestamp = System.currentTimeMillis();
                if (++count > cycles) {
                    return false;
                }
                return task.getAsBoolean();
            }
            float duration = (System.currentTimeMillis() - timestamp) / 1000f + compensation;
            if (duration > periodS) {
                compensation = duration;
                timestamp = System.currentTimeMillis();
                while (compensation > periodS) {
                    if (++count > cycles) {
                        return false;
                    }
                    if (!task.getAsBoolean()) {
                        return false;
                    }
                    compensation -= periodS;
                }
            }
            return true;
        }
    }
}
