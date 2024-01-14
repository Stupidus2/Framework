package de.Stupidus.commands;

import de.Stupidus.Framework.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
@CommandFW(name = "test")
public class TestCommand2 extends CommandFramework {
    @Override
    @Register()
    public void execute(CommandSender sender, String[] args, Command command, String cmd) {
        sender.sendMessage("Hi!");
    }
    @Register(subCommand = "testhelp", permission = "test")
    public void test(CommandSender sender, String[] args) {
        sender.sendMessage("Hi2!");
    }
}
