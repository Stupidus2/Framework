package de.Stupidus.Framework;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public abstract class CommandFramework implements CommandExecutor, Listener, TabCompleter {

    private final Register commandInfo;
    private CommandFW commandFW;
    private Map<String, Method> commandMethods;
    private Map<String, String> commandPermission;
    private Map<String, Integer> targetPlayer;
    private Map<String, Method> tab;

    public CommandFramework() {
        this.commandInfo = findRegisterAnnotation();
        this.commandMethods = findCommandMethods();
        this.commandFW = getClass().getAnnotation(CommandFW.class);
        this.commandPermission = findCommandPermissions();
        this.targetPlayer = findTargetPlayerMethods();
        this.tab = findCommandTabList();
        Objects.requireNonNull(commandInfo, ChatColor.RED + "Es ist ein Fehler in der Register Annotation aufgetreten!");
        Objects.requireNonNull(commandFW, ChatColor.RED + "Es ist ein Fehler in der CommandFW Annotation aufgetreten!");
        Objects.requireNonNull(commandMethods, ChatColor.RED + "Es ist ein Fehler in der CommandMethods Annotation aufgetreten!");
        Objects.requireNonNull(commandPermission, ChatColor.RED + "Es ist ein Fehler in Permissions aufgetreten!");
        Objects.requireNonNull(targetPlayer, ChatColor.RED + "Es ist ein Fehler in der TargetPlayer Annotation aufgetreten!");
        Objects.requireNonNull(tab, ChatColor.RED + "Es ist ein Fehler in der TabComplete Annotation aufgetreten!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String cmd, String[] args) {
        //Reload SubCommands and Permissions
        commandMethods.clear();
        commandMethods = findCommandMethods();
        commandPermission.clear();
        commandPermission = findCommandPermissions();
        if (commandInfo.subCommand().isEmpty() && commandFW.name().isEmpty()) {
            sender.sendMessage(ChatColor.RED + "Fehler aufgetreten!");
            return true;
        }

        if (args.length > 0) {

            String subCommand = args[0].toLowerCase();

            //Initializing subCommands
            if (commandMethods.containsKey(subCommand.toLowerCase())) {
                Method method = commandMethods.get(subCommand);
                if (method != null) {
                    String permission = commandPermission.get(subCommand);
                    if (!permission.isEmpty() && !sender.hasPermission(permission)) {
                        String permissionText = commandFW.errorPermission();
                        String permissionPlaceholer = permissionText.replace("{permission}", permission);
                        sender.sendMessage(ChatColor.RED + permissionPlaceholer);
                        return true;
                    }
                    //Checking if player is null for @TargetPlayer
                    Integer target = targetPlayer.get(subCommand);
                    if (target != null) {
                        if (Bukkit.getPlayer(args[target]) == null) {
                            sender.sendMessage(ChatColor.RED + commandFW.existPlayer());
                            return true;
                        }
                    }
                    try {

                        if (commandFW.player()) {
                            if (sender instanceof Player) {
                                method.invoke(this, sender, args);
                            } else {
                                sender.sendMessage(ChatColor.RED + (commandFW.errorPlayer()));
                            }
                        } else {
                            method.invoke(this, sender, args);
                        }
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println(ChatColor.RED + "Method is null!: Command Executer");
                }
            } else {
                sender.sendMessage(ChatColor.RED + commandFW.unknowCommand());
            }
        } else {

            //Initialising Command

            //Permission Check
            if (!commandFW.permission().isEmpty()) {
                if (!sender.hasPermission(commandFW.permission())) {
                    String permissionText = commandFW.errorPermission();
                    String permissionPlaceholer = permissionText.replace("{permission}", commandFW.permission());
                    sender.sendMessage(ChatColor.RED + permissionPlaceholer);
                    return true;
                }
            }

            //Player Check

            if (commandFW.player()) {
                if (sender instanceof Player) {
                    executeMethod((Player) sender, args, command, cmd);
                } else {
                    sender.sendMessage(ChatColor.RED + commandFW.errorPlayer());
                }
            } else {
                executeMethod(sender, args, command, cmd);
            }
        }
        return true;
    }

    private Register findRegisterAnnotation() {
        for (Method method : getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(Register.class)) {
                return method.getAnnotation(Register.class);
            }
        }
        return null;
    }

    private TabComplete findTabAnnotation() {
        for (Method method : getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(TabComplete.class)) {
                return method.getAnnotation(TabComplete.class);
            }
        }
        return null;
    }

    public Register getCommandInfo() {
        return commandInfo;
    }

    public CommandFW getCommandName() {
        return commandFW;
    }

    //Sub Command finder
    private Map<String, Method> findCommandMethods() {
        Map<String, Method> methods = new HashMap<>();
        for (Method method : getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(Register.class)) {
                Register annotation = method.getAnnotation(Register.class);
                String subCommand = annotation.subCommand().toLowerCase();
                if (subCommand.equals("{playerlist}")) {
                    for (Player players : Bukkit.getOnlinePlayers()) {
                        methods.put(players.getName().toLowerCase(), method);
                    }
                } else if (subCommand.equals("{worldlist}")) {
                    for (World worlds : Bukkit.getWorlds()) {
                        methods.put(worlds.getName().toLowerCase(), method);
                    }
                } else {
                    methods.put(subCommand, method);
                }
            }
        }
        return methods;
    }

    //TargetPlayer  finder
    private Map<String, Integer> findTargetPlayerMethods() {
        Map<String, Integer> methods = new HashMap<>();
        for (Method method : getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(TargetPlayer.class)) {

                Register annotationregister = method.getAnnotation(Register.class);
                String subCommand = annotationregister.subCommand().toLowerCase();

                TargetPlayer annotation = method.getAnnotation(TargetPlayer.class);
                Integer targetPlayer = annotation.targetPlayer();

                if (subCommand.equals("{playerlist}")) {
                    for (Player players : Bukkit.getOnlinePlayers()) {
                        methods.put(players.getName().toLowerCase(), targetPlayer);
                    }
                } else if (subCommand.equals("{worldlist}")) {
                    for (World worlds : Bukkit.getWorlds()) {
                        methods.put(worlds.getName().toLowerCase(), targetPlayer);
                    }
                } else {
                    methods.put(subCommand, targetPlayer);
                }
            }
        }
        return methods;
    }

    //Permission finder
    private Map<String, String> findCommandPermissions() {
        Map<String, String> permissions = new HashMap<>();
        for (Method method : getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(Register.class)) {
                Register annotation = method.getAnnotation(Register.class);
                String subCommand = annotation.subCommand().toLowerCase();
                String permission = annotation.permission().toLowerCase();
                if (subCommand.equals("{playerlist}")) {
                    for (Player players : Bukkit.getOnlinePlayers()) {
                        permissions.put(players.getName().toLowerCase(), permission);
                    }
                } else if (subCommand.equals("{worldlist}")) {
                    for (World worlds : Bukkit.getWorlds()) {
                        permissions.put(worlds.getName().toLowerCase(), permission);
                    }
                } else {
                    permissions.put(subCommand.toLowerCase(), permission);
                }
            }
        }
        return permissions;
    }

    //Tab list finder
    private Map<String, Method> findCommandTabList() {
        Map<String, Method> tabCompleteMap = new HashMap<>();
        for (Method method : getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(TabComplete.class)) {
                String subCommand = commandFW.name();
                tabCompleteMap.put(subCommand.toLowerCase(), method);
            }
        }
        return tabCompleteMap;
    }

    private void executeMethod(CommandSender sender, String[] args, Command command, String cmd) {
        if (!commandMethods.isEmpty() && args.length > 0) {
            String subCommand = args[0].toLowerCase();
            Method method = commandMethods.get(subCommand);
            if (method != null) {
                try {
                    method.invoke(this, sender, args);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            } else {
                execute(sender, args, command, cmd);
            }
        } else {
            execute(sender, args, command, cmd);
        }
    }

    public void execute(CommandSender sender, String[] args, Command command, String cmd) {
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        commandMethods.clear();
        commandMethods = findCommandMethods();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        commandMethods.clear();
        commandMethods = findCommandMethods();
    }

@Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        List<String> tabCompletee = new ArrayList<>();

        if (tab.containsKey(commandFW.name())) {
            Method method = tab.get(commandFW.name());
            if (args.length == 1) {
                if (sender.hasPermission(commandFW.permission())) {
                    String[] subCommand = commandMethods.keySet().toArray(new String[0]);
                    tabCompletee.addAll(Arrays.asList(subCommand));
                }
            } else if (args.length >= 2) {
                if (sender.hasPermission(commandFW.permission())) {
                    if (method != null) {
                        try {
                            method.invoke(this, sender, args, tabCompletee);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Method is null!: TabList");
                    }
                }
            }
        } else {

            if (args.length == 1) {
                if (sender.hasPermission(commandFW.permission())) {
                    String[] subCommand = commandMethods.keySet().toArray(new String[0]);
                    tabCompletee.addAll(Arrays.asList(subCommand));
                }
            }
        }
        return tabCompletee;
    }

    public List<String> tabCompleter(CommandSender sender, String[] args, List<String> tabCompleter) {
        return tabCompleter;
    }
}
