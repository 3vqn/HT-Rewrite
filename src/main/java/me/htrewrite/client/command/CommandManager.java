package me.htrewrite.client.command;

import static me.htrewrite.client.command.CommandReturnStatus.*;

import me.htrewrite.client.command.commands.FriendCommand;
import me.htrewrite.client.command.commands.HelpCommand;
import me.htrewrite.client.command.commands.ModuleCommand;
import me.htrewrite.client.command.commands.PrefixCommand;
import me.htrewrite.client.util.ConfigUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;

public class CommandManager {
    public static String prefix = "'";

    public final ConfigUtils configUtils;
    private ArrayList<Command> commands;

    public CommandManager() {
        configUtils = new ConfigUtils("config", "commands");
        Object object = configUtils.get("prefix");
        if(object == null) configUtils.set("prefix", prefix);
        else prefix = (String)object;

        commands = new ArrayList<Command>();
        commands.add(new FriendCommand());
        commands.add(new HelpCommand());
        commands.add(new ModuleCommand());
        // commands.add(new PrefixCommand());
    }

    public Command get(String alias) {
        for(Command command : commands)
            if(command.getAlias().equalsIgnoreCase(alias))
                return command;
        return null;
    }
    public CommandReturnStatus gotMessage(String message) {
        if(!message.startsWith(prefix)) return COMMAND_INVALID_SYNTAX;
        String[] args = message.split(" ");
        args[0] = args[0].replaceFirst(prefix, "");
        Command command = get(args[0]);
        if(command == null) return COMMAND_INVALID;
        args = ArrayUtils.remove(args, 0);
        command.call(args);
        return COMMAND_VALID;
    }

    public ConfigUtils getConfigUtils() { return configUtils; }
    public ArrayList<Command> getCommands() { return commands; }

    public void setPrefix(String newPrefix) {
        CommandManager.prefix = newPrefix;
        configUtils.set("prefix", newPrefix);
    }
}