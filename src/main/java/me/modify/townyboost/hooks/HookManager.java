package me.modify.townyboost.hooks;

import lombok.Getter;
import me.modify.townyboost.hooks.discordsrv.DiscordSRVHook;
import me.modify.townyboost.hooks.papi.PAPIHook;

/**
 * Represents the management of all hooks the plugin uses.
 */
public class HookManager {

    /** DiscordSRV hook */
    @Getter private DiscordSRVHook discordSRVHook;

    /** PlaceholderAPI hook */
    @Getter private PAPIHook papiHook;

    /**
     * Initializes all hooks this object manages.
     */
    public HookManager() {
        this.discordSRVHook = new DiscordSRVHook();
        this.papiHook = new PAPIHook();
    }

    /**
     * Checks if hooks are present on the server, and marks them as hooked.
     */
    public void checkHooks() {
        discordSRVHook.check();
        papiHook.check();
    }

    /**
     * Registers the PlaceholderAPI expansion which adds placeholders controlled by the plugin.
     */
    public void registerPAPIExpansion() {
        papiHook.registerExpansion();
    }
}
