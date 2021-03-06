package com.github.manolo8.darkbot.core.manager;

import com.github.manolo8.darkbot.config.ConfigEntity;
import com.github.manolo8.darkbot.core.BotInstaller;
import com.github.manolo8.darkbot.core.itf.Manager;
import com.github.manolo8.darkbot.core.objects.swf.IntArray;
import com.github.manolo8.darkbot.core.utils.ByteUtils;
import com.github.manolo8.darkbot.utils.LogUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

import static com.github.manolo8.darkbot.Main.API;

public class RepairManager implements Manager {
    private boolean writtenToLog = true;
    private long guiAddress, mainAddress, userDataAddress, repairAddress;

    private String killerName;
    private final IntArray repairOptions = IntArray.ofArray(true);

    private final Map<String, OutputStream> streams = new HashMap<>();

    @Override
    public void install(BotInstaller botInstaller) {
        botInstaller.guiManagerAddress.add(value -> {
            guiAddress = value;
            repairAddress = 0;
        });
        botInstaller.mainAddress.add(value -> mainAddress = value);
        botInstaller.heroInfoAddress.add(value -> userDataAddress = value);
    }

    public void tick() {
        if (isDead()) writtenToLog = false;
        else {
            if (!writtenToLog) writeKiller();
            writtenToLog = true;
            return;
        }

        if (repairAddress == 0) updateRepairAddr();

        killerName = API.readMemoryString(API.readMemoryLong(repairAddress + 0x68));
        repairOptions.update(API.readMemoryLong(repairAddress + 0x58));
    }

    private void updateRepairAddr() {
        long[] values = API.queryMemory(ByteUtils.getBytes(guiAddress, mainAddress), 1);
        if (values.length == 1) repairAddress = values[0] - 0x38;
    }

    public String getKillerName() {
        return killerName;
    }

    public boolean isDead() {
        return API.readMemoryBoolean(userDataAddress + 0x4C);
    }

    public boolean canRespawn(int option) {
        for (int i = 0; i < repairOptions.getSize(); i++) {
            if (repairOptions.get(i) == option) return true;
        }
        return false;
    }

    public int[] getRespawnOptionsIds() {
        int[] options = new int[repairOptions.getSize()];
        for (int i = 0; i < repairOptions.getSize(); i++) {
            options[i] = repairOptions.get(i);
        }
        return options;
    }

    private void writeKiller() {
        String killerMessage = killerName == null || killerName.isEmpty()
                ? "You were destroyed by a radiation/mine/unknown"
                : "You have been destroyed by: " + killerName;
        System.out.println(killerMessage);

        if (ConfigEntity.INSTANCE.getConfig().MISCELLANEOUS.LOG_DEATHS)
            writeToFile(LogUtils.START_TIME + "death", formatLogMessage(killerMessage));
    }

    private String formatLogMessage(String message) {
        return String.format("[%s] %-" + message.length() + "s" + System.lineSeparator(),
                LocalDateTime.now().format(LogUtils.LOG_DATE),
                message);
    }
    private void writeToFile(String name, String message) {
        try {
            OutputStream os = getOrCreateStream(name);
            if (os == null) return;

            os.write(message.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private OutputStream getOrCreateStream(String name) {
        return this.streams.computeIfAbsent(name, LogUtils::createLogFile);
    }

}