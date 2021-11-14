package com.github.manolo8.darkbot.core.entities;

import com.github.manolo8.darkbot.Main;
import com.github.manolo8.darkbot.config.ConfigEntity;
import com.github.manolo8.darkbot.config.NpcInfo;
import com.github.manolo8.darkbot.core.manager.EffectManager;
import com.github.manolo8.darkbot.core.objects.facades.SettingsProxy;
import com.github.manolo8.darkbot.core.objects.facades.SlotBarsProxy;
import com.github.manolo8.darkbot.core.objects.slotbars.Item;
import eu.darkbot.api.game.items.SelectableItem;
import eu.darkbot.api.game.items.ItemCategory;
import eu.darkbot.api.managers.HeroItemsAPI;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import static com.github.manolo8.darkbot.Main.API;

public class Npc extends Ship implements eu.darkbot.api.game.entities.Npc {
    private static final NpcInfo INITIAL_NPC_INFO = new NpcInfo(); // Prevent NPE trying to obtain npc info.

    public NpcInfo npcInfo = INITIAL_NPC_INFO;
    public int npcId;
    public boolean ish;

    private eu.darkbot.api.config.types.NpcInfo apiNpcInfo;

    public Npc(int id) {
        super(id);
    }

    public Npc(int id, long address) {
        super(id);
        this.update(address);
    }

    @Override
    public void added(Main main) {
        super.added(main);
        this.apiNpcInfo = new NpcInfoImpl(main.pluginAPI.requireAPI(HeroItemsAPI.class),
                main.pluginAPI.requireInstance(SettingsProxy.class));
    }

    @Override
    public void update() {
        String oldName = playerInfo.username;
        super.update();

        npcId = API.readMemoryInt(API.readMemoryLong(address + 192) + 80);
        ish = hasEffect(EffectManager.Effect.NPC_ISH);

        if (!Objects.equals(oldName, playerInfo.username)) {
            npcInfo = ConfigEntity.INSTANCE.getOrCreateNpcInfo(playerInfo.username);
            npcInfo.npcId = npcId;
        }
    }

    @Override
    public int getNpcId() {
        return npcId;
    }

    @Override
    public eu.darkbot.api.config.types.NpcInfo getInfo() {
        return apiNpcInfo;
    }


    private class NpcInfoImpl implements eu.darkbot.api.config.types.NpcInfo {

        private final HeroItemsAPI items;
        private final SettingsProxy settingsProxy;

        public NpcInfoImpl(HeroItemsAPI items, SettingsProxy settingsProxy) {
            this.items = items;
            this.settingsProxy = settingsProxy;
        }

        @Override
        public boolean shouldKill() {
            return npcInfo.kill;
        }

        @Override
        public int getPriority() {
            return npcInfo.priority;
        }

        @Override
        public double getRadius() {
            return npcInfo.radius;
        }

        @Override
        public Optional<SelectableItem.Laser> getAmmo() {
            return findItemAssociatedWith(ItemCategory.LASERS, npcInfo.attackKey)
                    .map(item -> SelectableItem.Laser.of(item.id));
        }

        @Override
        public Optional<SelectableItem.Formation> getFormation() {
            return findItemAssociatedWith(ItemCategory.DRONE_FORMATIONS, npcInfo.attackFormation)
                    .map(item -> SelectableItem.Formation.of(item.id));
        }

        private Optional<Item> findItemAssociatedWith(ItemCategory category, Character c) {
            SettingsProxy.KeyBind k = settingsProxy.getAtChar(c);
            if (k == null || k.getType() == null) return Optional.empty();

            int slotNumber;
            try {
                slotNumber = Integer.parseInt(k.name().replaceAll("[^-?0-9]+", ""));
            } catch (NumberFormatException e) {
                return Optional.empty();
            }

            SlotBarsProxy proxy = (SlotBarsProxy) items;

            return proxy.categoryBar.get(category).items.stream()
                    .filter(Item::hasShortcut)
                    .filter(item -> item.getSlotBarType() == k.getType())
                    .filter(item -> item.containsSlotNumber(slotNumber))
                    .findAny();
        }

        @Override
        public boolean hasExtraFlag(Enum<?> flag) {
            return npcInfo.extra.has(getId(flag));
        }

        @Override
        public void setExtraFlag(Enum<?> flag, boolean active) {
            npcInfo.extra.set(getId(flag), active);
        }

        private String getId(Enum<?> flag) {
            return flag.getClass().getCanonicalName() + flag.name();
        }
    }
}
