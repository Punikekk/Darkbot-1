package eu.darkbot.api.managers;

import eu.darkbot.api.API;
import eu.darkbot.api.entities.Portal;
import eu.darkbot.api.objects.Locatable;
import eu.darkbot.api.objects.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MovementAPI extends API {
    /**
     * Tries to jump through portal.
     *
     * @param portal to jump through
     * @return true if jump button was clicked
     */
    boolean jumpPortal(@NotNull Portal portal);

    /**
     * @return true if hero is moving or destination path isn't empty.
     */
    boolean isMoving();

    /**
     * @return true if hero is out of map
     */
    boolean isOutOfMap();

    /**
     * @return current destination {@link Location}
     */
    @Nullable
    Location getDestination();

    /**
     * @return current hero location.
     * @see HeroAPI#getLocationInfo()
     */
    Location getCurrentLocation();

    /**
     * Will move random, prefers `preferred zones`
     * and omits `avoided zones`
     */
    void moveRandom();

    /**
     * Stops ship, removes destination path.
     *
     * @param currentLocation should stop at current location.
     */
    void stop(boolean currentLocation);

    /**
     * Checks if it is possible to move to your destination.
     *
     * @return true if it is possible to move there.
     */
    boolean canMove(double x, double y);

    default boolean canMove(@NotNull Locatable destination) {
        return canMove(destination.getX(), destination.getY());
    }

    /**
     * Sets location where to move.
     */
    void moveTo(double x, double y);

    default void moveTo(@NotNull Locatable destination) {
        moveTo(destination.getX(), destination.getY());
    }

    /**
     * Returns closest distance to destination,
     * calculates barriers and any other obstacles.
     */
    double getClosestDistance(double x, double y);

    default double getClosestDistance(@NotNull Locatable destination) {
        return getClosestDistance(destination.getX(), destination.getY());
    }

    /**
     * Calculates distance between two points,
     * includes barriers and any other obstacles.
     */
    double getDistanceBetween(double x, double y, double ox, double oy);

    default double getDistanceBetween(@NotNull Locatable loc, @NotNull Locatable otherLoc) {
        return getDistanceBetween(loc.getX(), loc.getY(), otherLoc.getX(), otherLoc.getY());
    }
}