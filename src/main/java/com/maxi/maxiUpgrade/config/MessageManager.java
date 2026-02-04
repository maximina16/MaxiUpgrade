package com.maxi.maxiUpgrade.config;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public final class MessageManager {

    private final FileConfiguration messages;

    public MessageManager(FileConfiguration messages) {
        this.messages = messages;
    }

    public void send(CommandSender sender, String path, String... replace) {
        String msg = messages.getString(path);
        if (msg == null) return;

        for (int i = 0; i + 1 < replace.length; i += 2) {
            msg = msg.replace(replace[i], replace[i + 1]);
        }

        sender.sendMessage(msg.replace("&", "§"));
    }
}
