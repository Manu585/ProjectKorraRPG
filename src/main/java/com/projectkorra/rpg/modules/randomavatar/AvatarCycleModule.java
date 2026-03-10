package com.projectkorra.rpg.modules.randomavatar;

import com.projectkorra.rpg.modules.Module;
import com.projectkorra.rpg.modules.randomavatar.commands.AvatarCommand;
import com.projectkorra.rpg.modules.randomavatar.listeners.AvatarListener;
import com.projectkorra.rpg.modules.randomavatar.manager.AvatarManager;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class AvatarCycleModule extends Module {

    private AvatarManager avatarManager;
    private AvatarListener avatarListener;
    private BukkitTask avatarCheckTask;

    public AvatarCycleModule(Plugin plugin) {
        super(plugin, "RandomAvatar");
    }

    @Override
    public void enable() {
        this.avatarManager = new AvatarManager(plugin);
        this.avatarListener = new AvatarListener(avatarManager);

        new AvatarCommand(avatarManager);

        registerListeners(this.avatarListener);

        avatarManager.refreshRecentPlayersAsync();

        avatarCheckTask = Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            plugin.getLogger().info("Avatar selection: Checking for new avatars.");
            avatarManager.checkAvatars();
        }, 100L, 20L * 60); // Every 60 seconds
    }

    @Override
    public void disable() {
        if (avatarCheckTask != null) {
            avatarCheckTask.cancel();
            avatarCheckTask = null;
        }

        if (this.avatarListener != null) {
            HandlerList.unregisterAll(this.avatarListener);
            this.avatarListener = null;
        }

        this.avatarManager = null;
    }

    public AvatarManager getAvatarManager() {
        return avatarManager;
    }

}
