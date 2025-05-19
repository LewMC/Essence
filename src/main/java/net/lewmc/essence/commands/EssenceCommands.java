package net.lewmc.essence.commands;

import net.lewmc.essence.utils.*;
import net.lewmc.essence.Essence;
import net.lewmc.foundry.Files;
import net.lewmc.foundry.command.FoundryCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Objects;

public class EssenceCommands extends FoundryCommand {
    private final Essence plugin;

    /**
     * Constructor for the EssenceCommands class.
     * @param plugin References to the main plugin class.
     */
    public EssenceCommands(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * Permission required to execute.
     * @return String - The permission string.
     */
    @Override
    protected String requiredPermission() {
        return null;
    }

    /**
     * /essence command handler.
     * @param cs Information about who sent the command - player or console.
     * @param command Information about what command was sent.
     * @param s Command label - not used here.
     * @param args The command's arguments.
     * @return boolean true/false - was the command accepted and processed or not?
     */
    @Override
    protected boolean onRun(CommandSender cs, Command command, String s, String[] args) {
        CommandUtil cmd = new CommandUtil(this.plugin, cs);
        if (cmd.isDisabled("essence")) { return cmd.disabled(); }

        MessageUtil msg = new MessageUtil(this.plugin, cs);

        if (args.length > 0) {
            if ("help".equals(args[0])) {
                HelpCommand helpCommand = new HelpCommand(this.plugin, msg, args, cs);
                return helpCommand.runHelpCommand();
            } else if ("reload".equals(args[0])) {
                return this.reloadCommand(cs, msg);
            } else if ("import".equals(args[0])) {
                return this.importCommand(args, msg, cs);
            }
        } else {
            msg.send("about", "version", new String[] { plugin.getDescription().getVersion() });
            msg.send("about", "description");
            msg.send("about", "author");
            if (!Objects.equals(this.plugin.getConfig().getString("language"), "en-GB")) {
                Files lang = new Files(this.plugin.config, this.plugin);
                lang.load("language/"+this.plugin.getConfig().getString("language")+".yml");
                msg.send("about", "authorLang", new String[] { lang.getString("meta.language"), lang.getString("meta.author") });
                lang.close();
            }
            msg.send("about", "issues");
            msg.send("about", "more");

            return true;
        }
        return true;
    }

    /**
     * Reloads Essence's configuration (experimental).
     * @param cs CommandSender - The entity that sent the command.
     * @param message MessageUtil - The message utility.
     * @return boolean - If the operation was successful.
     */
    private boolean reloadCommand(CommandSender cs, MessageUtil message) {
        PermissionHandler perms = new PermissionHandler(this.plugin, cs);
        if (perms.has("essence.admin.reload")) {
            this.plugin.reloadConfig();
            this.plugin.disabledCommands = this.plugin.getConfig().getStringList("disabled-commands");
            this.plugin.disabledCommandsFeedback = this.plugin.getConfig().getBoolean("disabled-commands-feedback");
            this.plugin.verbose = this.plugin.getConfig().getBoolean("verbose");
            this.plugin.chat_nameFormat = this.plugin.getConfig().getString("chat.name-format");
            this.plugin.chat_manage = this.plugin.getConfig().getBoolean("chat.enabled");
            this.plugin.chat_allowMessageFormatting = this.plugin.getConfig().getBoolean("chat.allow-message-formatting");
            this.plugin.economySymbol = this.plugin.getConfig().getString("economy.symbol");
            message.send("generic", "reload");
            return true;
        } else {
            return perms.not();
        }
    }

    /**
     * Imports homes, warps, and spawns from other plugins.
     * @param args String[] - List of command arguments.
     * @param message MessageUtil - The message utility.
     * @param cs CommandSender - The entity that sent the command.
     * @return boolean - If the operation was successful.
     */
    private boolean importCommand(String[] args, MessageUtil message, CommandSender cs) {
        PermissionHandler perms = new PermissionHandler(this.plugin, cs);
        if (perms.has("essence.admin.import")) {
            if (args.length > 1) {
                if (args[1].equalsIgnoreCase("essentials")) {
                    ImportUtil imp = new ImportUtil(this.plugin);
                    if (imp.essentialsWarps()) {
                        message.send("import", "importedwarps", new String[]{"Essentials"});
                    } else {
                        message.send("import", "unabletoimport", new String[]{"warps", "Essentials"});
                    }

                    if (imp.essentialsHomes()) {
                        message.send("import", "importedhomes", new String[]{"Essentials"});
                    } else {
                        message.send("import", "unabletoimport", new String[]{"homes", "Essentials"});
                    }

                    if (imp.essentialsSpawns()) {
                        message.send("import", "importedspawns", new String[]{"Essentials"});
                    } else {
                        message.send("import", "unabletoimport", new String[]{"spawns", "Essentials"});
                    }

                    message.send("generic", "done");
                    return true;
                } else {
                    message.send("import", "unsupported", new String[]{args[1]});
                    message.send("import", "list");
                    return true;
                }
            } else {
                message.send("import", "list");
                return true;
            }
        } else {
            return perms.not();
        }
    }
}