package eu.darkbot.api.managers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface KeyBindsAPI {
    /**
     * Search {@link Character} associated with given {@link Shortcut} in-game.
     *
     * @param shortcut to be looked for
     * @return {@link Character} associated with {@link Shortcut} otherwise null
     */
    @Nullable
    Character getKeyBind(@NotNull Shortcut shortcut);

    /**
     * Search {@link Character} associated with given {@link Shortcut} and slotNumber in-game.
     *
     * @param shortcut   to be looked for
     * @param slotNumber between 1 - 10
     * @return {@link Character} associated with {@link Shortcut} otherwise null
     * @throws IllegalAccessException if {@code slotNumber < 1 || slotNumber > 10}
     */
    @Nullable
    Character getKeyBind(@NotNull Shortcut shortcut, int slotNumber) throws IllegalAccessException;

    enum Shortcut {
        DEFAULT_BAR(0),
        PREMIUM_BAR(10),
        JUMP_GATE(19),
        TOGGLE_CONFIG(20),
        ATTACK_LASER(21),
        ATTACK_ROCKET(22),
        ACTIVE_PET(23),
        PET_GUARD_MODE(24),
        PET_COMBO_REPAIR(25),
        LOGOUT(26),
        TOGGLE_WINDOWS(27),
        TOGGLE_MONITORING(28),
        ZOOM_IN(29),
        ZOOM_OUT(30),
        FOCUS_CHAT(31),
        TOGGLE_CATEGORY_BAR(32),
        TOGGLE_PRO_ACTION(34);

        private final int index;

        Shortcut(int index) {
            this.index = index;
        }

        public int getIndex() {
            return this.index;
        }

        public int getIndex(int slotNumber) throws IllegalAccessException {
            switch (this) {
                case DEFAULT_BAR:
                    if (slotNumber < 1 || slotNumber > 10)
                        throw new IllegalAccessException("Invalid slot number!");
                    return this.index + slotNumber - 1;

                case PREMIUM_BAR:
                    if (slotNumber < 1 || slotNumber > 10)
                        throw new IllegalAccessException("Invalid slot number!");
                    if (slotNumber == 10) return 33;
                    return this.index + slotNumber - 1;

                default:
                    return this.index;
            }
        }
    }
}