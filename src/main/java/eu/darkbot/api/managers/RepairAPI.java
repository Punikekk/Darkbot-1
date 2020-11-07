package eu.darkbot.api.managers;

import eu.darkbot.api.API;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

public interface RepairAPI extends API {

    int getDeathsAmount();

    boolean isDestroyed();

    /**
     * Tries to repair ship with given repair option
     * @throws IllegalStateException if ship is already repaired
     */
    void tryRevive(int repairOption) throws IllegalStateException;

    @Nullable
    int[] getAvailableRepairOptions();

    @Nullable
    String getLastDestroyerName();

    @Nullable
    LocalDateTime getLastDeathTime();
}