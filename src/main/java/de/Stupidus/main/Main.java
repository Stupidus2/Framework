package de.Stupidus.main;

import de.Stupidus.initilizer.CommandFrameworkInitializer;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        CommandFrameworkInitializer commandFrameworkInitializer = new CommandFrameworkInitializer(this);
    }
}
