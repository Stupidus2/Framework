package de.Stupidus.commands;

import de.Stupidus.Framework.*;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

@CommandFW(name = "heal", player = true, permission = "healmain", unknowCommand = "Diesen Spieler gibt es nicht!")
public class ExampleCommand extends CommandFramework {
    @Override
    @Register()
    public void execute(CommandSender sender, String[] args, Command command, String cmd) {
        Player player = (Player) sender;
        player.setHealth(20);
        player.sendMessage("healt");
    }
    @Register(subCommand = "{playerlist}", permission = "test")
    @TargetPlayer(targetPlayer = 0)
    public void otherHeal(CommandSender sender, String[] args) {
        Player target = Bukkit.getPlayer(args[0]);
        target.setHealth(20);
        target.sendMessage("Du wurdest healt!");
        sender.sendMessage(" Healt anderen spieler");
    }
    @Register(subCommand = "lul")
    public void lul(CommandSender sender, String[] args) {
        if(args.length > 1) {
            if (args[1].equals("hi")) {
                sender.sendMessage("Hi!");
            }
        }
    }

    @Override
    public List<String> tabCompleter(CommandSender sender, String[] args, List<String> tabCompleter) {
        return tabCompleter;
    }
}