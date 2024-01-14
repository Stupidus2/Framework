package de.Stupidus.initilizer;

import de.Stupidus.Framework.CommandFramework;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections8.Reflections;

import java.lang.reflect.InvocationTargetException;

public class CommandFrameworkInitializer {
    public CommandFrameworkInitializer(JavaPlugin plugin) {
        String packageName = getClass().getPackage().getName();

        for (Class<? extends CommandFramework> clazz : new Reflections("de.Stupidus.commands").getSubTypesOf(CommandFramework.class)) {
            try {
                CommandFramework commandPlugin = clazz.getDeclaredConstructor().newInstance();
                plugin.getCommand(commandPlugin.getCommandName().name()).setExecutor(commandPlugin);
                plugin.getServer().getPluginManager().registerEvents(commandPlugin, plugin);
                plugin.getCommand(commandPlugin.getCommandName().name()).setTabCompleter(commandPlugin);

            } catch (InstantiationException | NoSuchMethodException | InvocationTargetException |
                     IllegalAccessException e) {
                Throwable cause = e.getCause();
                if (cause != null) {
                    cause.printStackTrace();
                }
            }
        }
    }

}
