package eu.darkbot.api.managers;

import eu.darkbot.api.API;
import eu.darkbot.api.entities.Pet;
import eu.darkbot.api.objects.Gui;
import eu.darkbot.api.objects.LocationInfo;
import eu.darkbot.api.utils.ItemNotEquippedException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * API to manage hero's pet.
 */
public interface PetAPI extends Gui, Pet, API {

    /**
     * @return true if pet by module was marked to be enabled.
     */
    boolean isEnabled();

    /**
     * Will change enabled flag for pet.
     * If was set to {@code true} bot will try to enable the pet,
     * only if user configured bot to use the pet.
     */
    void setEnabled(boolean enabled);

    /**
     * @return true if pet is active.
     */
    boolean isActive();

    /**
     * @return true if pet is repaired.
     */
    boolean isRepaired();
    // try repair?

    /**
     * @param gearId to be checked
     * @return true if given gear is available.
     */
    boolean isGearAvailable(int gearId);

    default boolean isGearAvailable(@NotNull Pet.Gear gear) {
        return isGearAvailable(gear.getId());
    }

    /**
     * @param gearId to be set
     * @throws ItemNotEquippedException if given gear is not equipped or doesn't exists
     */
    void setGear(int gearId) throws ItemNotEquippedException;

    default void setGear(@NotNull Pet.Gear gear) throws ItemNotEquippedException {
        setGear(gear.getId());
    }

    /**
     * @return {@link LocationInfo} of pet locator target
     * or else null if theres none.
     */
    @Nullable
    LocationInfo getLocatorNpcLoc();

    /**
     * Checks if pet has the given {@link Pet.Cooldown},
     * which can make gears unavailable temporally.
     *
     * @param cooldownId to be checked
     * @return true if pet has given {@code cooldownId}
     * @see Pet.Cooldown
     */
    boolean hasCooldown(int cooldownId);

    default boolean hasCooldown(@NotNull Pet.Cooldown cooldown) {
        return hasCooldown(cooldown.getId());
    }

    /**
     * @param gear to be checked
     * @return true if given gear is currently cooling down
     */
    default boolean hasCooldown(@NotNull Pet.Gear gear) {
        Pet.Cooldown cooldown = gear.getCooldown();
        return cooldown != null && hasCooldown(cooldown);
    }

    int getFuel();
    int getMaxFuel();

    int getHeat();
    int getMaxHeat();

    @Override
    default int getHull() {
        return 0;
    }

    @Override
    default int getMaxHull() {
        return 0;
    }

    @Override
    default boolean hullDecreasedIn(int time) {
        return false;
    }

    @Override
    default boolean hullIncreasedIn(int time) {
        return false;
    }
}