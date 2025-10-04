package fr.liveinground.admin_craft.mutes;

import fr.liveinground.admin_craft.Config;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.ServerOpList;
import net.minecraft.server.players.ServerOpListEntry;

import fr.liveinground.admin_craft.AdminCraft;
import fr.liveinground.admin_craft.PlaceHolderSystem;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;

public class Utils {
    public static List<ServerPlayer> getOnlineOperators() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();

        List<ServerPlayer> onlinePlayers = server.getPlayerList().getPlayers();

        ServerOpList opList = server.getPlayerList().getOps();

        return onlinePlayers.stream()
                .filter(player -> {
                    ServerOpListEntry entry = opList.get(player.getGameProfile());
                    return entry != null && entry.getLevel() >= 1; // niveau OP ≥ 1
                })
                .collect(Collectors.toList());
    }

    public static void logCancelledMessage(ServerPlayer player, String message) {
        if (Config.log_cancelled_events) {
            final String logMessage = PlaceHolderSystem.replacePlaceholders(Config.cancel_log_format, Map.of("player", player.getDisplayName().getString(), "message", message));
            AdminCraft.LOGGER.info(logMessage);
            for (ServerPlayer p: getOnlineOperators()) {
                p.sendSystemMessage(Component.literal(logMessage));
            }
        }
    }
}
