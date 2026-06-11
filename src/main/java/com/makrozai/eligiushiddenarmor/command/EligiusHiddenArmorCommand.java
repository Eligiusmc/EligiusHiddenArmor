package com.makrozai.eligiushiddenarmor.command;

import com.makrozai.eligiushiddenarmor.EligiusHiddenArmor;
import com.makrozai.eligiushiddenarmor.command.util.AbstractCommand;
import com.makrozai.eligiushiddenarmor.command.util.CommandStatus;
import com.makrozai.eligiushiddenarmor.handler.MessageHandler;
import com.makrozai.eligiushiddenarmor.domain.service.ArmorHideService;
import com.makrozai.eligiushiddenarmor.util.ConfigHolder;
import com.makrozai.eligiushiddenarmor.util.PermissionUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Command dispatcher handling all `/eha` commands and subcommands.
 * Parses player input, verifies permissions, and delegates actions to the
 * {@link ArmorHideService}.
 */
public class EligiusHiddenArmorCommand extends AbstractCommand implements ConfigHolder {
    ArmorHideService EligiusHiddenArmorManager;

    private boolean defaultPermissionToggle;
    private boolean defaultPermissionToggleOther;

    public EligiusHiddenArmorCommand(EligiusHiddenArmor plugin, String command) {
        super(plugin, command);
        plugin.addConfigHolder(this);
        this.EligiusHiddenArmorManager = plugin.getArmorHideService();
    }

    @Override
    public CommandStatus execute(CommandSender sender, Command command, String[] arguments) throws Exception {
        if((arguments.length < 1) || (arguments[0].equalsIgnoreCase("help"))) {
            help(sender);
            return CommandStatus.SUCCESS;
        }

        MessageHandler messageHandler = plugin.getMessageHandler();

        String subcommand = arguments[0].toLowerCase();

        switch (subcommand) {
            case "reload":
                if (!hasSubPermission(sender, "reload")) {
                    messageHandler.message(sender, "no_permission");
                    break;
                }
                plugin.saveDefaultConfig();
                plugin.reloadConfig();
                messageHandler.reloadLocales();
                messageHandler.message(sender, "reloaded", true);
                return CommandStatus.SUCCESS;
            case "toggle":
            case "hide":
            case "show":
                if (!toggleArmor(sender, arguments)) {
                    messageHandler.message(sender, "no_permission");
                    break;
                }
                return CommandStatus.SUCCESS;
        }

        messageHandler.message(sender, "command_invalid");
        return CommandStatus.INVALID_USAGE;
    }

    /**
     * Internal handler for toggle logic when players use `/eha toggle [piece] [player]`.
     *
     * @param sender    The command issuer.
     * @param arguments The command arguments provided.
     * @return boolean  True if executed properly, false if permission was denied.
     */
    private boolean toggleArmor(CommandSender sender, String[] arguments) {
        if (!hasSubPermission(sender, "toggle") && !defaultPermissionToggle) return false;
        MessageHandler messageHandler = plugin.getMessageHandler();
        Player player;
        
        String pieceArg = "all";
        String playerArg = null;
        
        if (arguments.length >= 2) {
            String possiblePiece = arguments[1].toLowerCase();
            if (possiblePiece.equals("all") || com.makrozai.eligiushiddenarmor.domain.model.ArmorPiece.fromName(possiblePiece) != null) {
                pieceArg = possiblePiece;
                if (arguments.length >= 3) {
                    playerArg = arguments[2];
                }
            } else {
                playerArg = arguments[1];
            }
        }
        
        if (playerArg != null) {
            if (!hasSubPermission(sender, "toggle.other") && !defaultPermissionToggleOther) {
                messageHandler.message(sender, "no_permission");
                return true;
            }
            player = Bukkit.getPlayer(playerArg);

            if (player == null) {
                messageHandler.message(sender, "player_not_found");
                return true;
            }
        } else {
            if (sender instanceof ConsoleCommandSender) {
                messageHandler.message(sender, "player_only");
                return true;
            } else {
                player = (Player) sender;
            }
        }

        String action = arguments[0].toLowerCase();
        
        com.makrozai.eligiushiddenarmor.domain.model.ArmorPiece piece = null;
        if (!pieceArg.equals("all")) {
            piece = com.makrozai.eligiushiddenarmor.domain.model.ArmorPiece.fromName(pieceArg);
        }

        switch (action) {
            case "toggle":
                if (piece != null) EligiusHiddenArmorManager.togglePiece(player, piece, true);
                else EligiusHiddenArmorManager.togglePlayer(player, true);
                break;
            case "hide":
                if (piece != null) EligiusHiddenArmorManager.hidePiece(player, piece, true);
                else EligiusHiddenArmorManager.enablePlayer(player, true);
                break;
            case "show":
                if (piece != null) EligiusHiddenArmorManager.showPiece(player, piece, true);
                else EligiusHiddenArmorManager.disablePlayer(player, true);
                break;
        }

        if (plugin.getConfig().getBoolean("sounds.toggle", true)) {
            try {
                player.playSound(player.getLocation(), org.bukkit.Sound.ITEM_ARMOR_EQUIP_GENERIC, 1.0f, 1.0f);
            } catch (Exception ignored) {}
        }

        if (!player.equals(sender)) {
            Map<String, String> placeholderMap = new HashMap<>();
            boolean hidden = piece != null ? EligiusHiddenArmorManager.isPieceHidden(player, piece) : EligiusHiddenArmorManager.isEnabled(player);
            placeholderMap.put("visibility", messageHandler.getLocalizedLegacyMessage(hidden ? "visibility_hidden_word" : "visibility_shown_word"));
            placeholderMap.put("player", player.getName());
            
            if (piece != null) {
                placeholderMap.put("piece", messageHandler.getLocalizedLegacyMessage("piece_" + piece.name().toLowerCase()));
                messageHandler.message(sender, "armor_piece_visibility_other", false, placeholderMap);
            } else {
                messageHandler.message(sender, "armor_visibility_other", false, placeholderMap);
            }
        }
        return true;
    }

    private void help(CommandSender sender){
        MessageHandler messageHandler = plugin.getMessageHandler();
        messageHandler.message(sender, "help_header", false);

        if(PermissionUtil.canUse(sender ,"eligiushiddenarmor.toggle") || defaultPermissionToggle)
            messageHandler.message(sender, "help_toggle", false);

        if(PermissionUtil.canUse(sender ,"eligiushiddenarmor.toggle.other") || (defaultPermissionToggle && defaultPermissionToggleOther))
            messageHandler.message(sender, "help_toggle_other", false);

        if(PermissionUtil.canUse(sender, "eligiushiddenarmor.reload"))
            messageHandler.message(sender, "help_reload", false);

        messageHandler.message(sender, "help_footer", false);
    }

    @Override
    public void loadConfig(FileConfiguration config) {
        this.defaultPermissionToggle = config.getBoolean("default-permissions.toggle", true);
        this.defaultPermissionToggleOther = config.getBoolean("default-permissions.toggle-other", false);
    }
}
