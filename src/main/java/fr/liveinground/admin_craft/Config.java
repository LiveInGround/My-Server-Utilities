package fr.liveinground.admin_craft;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.*;
import java.util.stream.Collectors;

public class Config {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    static {
        BUILDER.push("spawnProtection");

        ENABLE_SPAWN_PROTECTION = BUILDER.comment("Enable/disable spawn protection")
                .define("enable", true);

        SP_OP_LEVEL = BUILDER.comment("OP level required to bypass protections")
                .defineInRange("bypassOPLevel", 1, 0, 4);

        SPAWN_PROTECTION_CENTER_X = BUILDER.comment("Center X coordinate of protection")
                .defineInRange("centerX", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);

        SPAWN_PROTECTION_CENTER_Z = BUILDER.comment("Center Z coordinate of protection")
                .defineInRange("centerZ", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);

        SPAWN_PROTECTION_RADIUS = BUILDER.comment("Protection radius")
                .defineInRange("radius", 16, 0, Integer.MAX_VALUE);

        ALLOW_PVP = BUILDER.comment("Allow PvP inside spawn protection")
                .define("enablePvP", false);

        ALLOW_EXPLOSION = BUILDER.comment("If set to false, explosions that might break blocks in the spawn protection won't deal any block damage")
                .define("allowExplosions", false);

        SP_EFFECTS = BUILDER.comment("Effects applied in spawn protection")
                .defineListAllowEmpty(
                        "effects",
                        List.of("minecraft:resistance", "minecraft:regeneration", "minecraft:saturation"),
                        Config::validateEffectName
                );

        ALLOWED_BLOCKS = BUILDER.comment("Blocks players are allowed to interact with")
                .defineListAllowEmpty(
                        "allowedBlocks",
                        List.of("minecraft:stone_button"),
                        Config::validateBlockName
                );

        BUILDER.pop();
    }

    static {
        BUILDER.push("spawnOverride");

        ENABLE_SPAWN_OVERRIDE = BUILDER.comment("Should the world spawn be overridden?")
                .define("enabled", true);

        SPAWN_X = BUILDER.comment("Spawn X coordinate").defineInRange("x", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        SPAWN_Y = BUILDER.comment("Spawn Y coordinate").defineInRange("y", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        SPAWN_Z = BUILDER.comment("Spawn Z coordinate").defineInRange("z", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);

        BUILDER.pop();
    }

    static {
        BUILDER.push("muteSystem");
        
        MUTE_LEVEL = BUILDER.comment("The OP level required to use the /mute and /unmute commands").defineInRange("opLevel", 3, 0, 4);
        MUTE_FORBIDDEN_CMD = BUILDER.comment("The list of commands the players can't use while muted").defineListAllowEmpty("muteForbiddenCommands", List.of("msg", "tell", "teammsg", "w"), Config::validateString);
        MUTE_PREVENT_SIGN_PLACING = BUILDER.comment("Should the mod prevent muted players using signs ?").define("preventSigns", true);
        LOG_CANCELLED_EVENTS = BUILDER.comment("Should the mod log cancelled events to ops and console ?").define("logCancelledEvent", true);
        ALLOW_MESSAGES_TO_OPS = BUILDER.comment("Should the mod allow muted players to use commands to send messages to ops ?").define("allowMessagesToOps", true);

        BUILDER.pop();
    }

    static {
        BUILDER.push("noAltsSystem");

        ALT_LEVEL = BUILDER.comment("The OP level required to use the /alts command").defineInRange("opLevel", 3, 0, 4);

        BUILDER.pop();
    }

    static {
        BUILDER.push("quickSanctionSystem");

        ENABLE_SANC = BUILDER.comment("Should the /sanction command and the quick sanction system be enabled ? IMPORTANT NOTE: The sanctions can be configured in admin_craft_sanctions.toml").define("enable", true);
        SANC_LEVEL = BUILDER.comment("The OP level required to use the /sanction command").defineInRange("sanctionCommandLevel", 3, 0, 4);

        BUILDER.pop();
    }

    static {
        BUILDER.push("messages");

        SPAWN_PROTECTION_ENTER = BUILDER.comment("Message when entering spawn protection")
                .define("enter", "You are now in the spawn protection");

        SPAWN_PROTECTION_LEAVE = BUILDER.comment("Message when leaving spawn protection")
                .define("leave", "You are no more in the spawn protection");


            BUILDER.push("mute");
            MUTE_MESSAGE = BUILDER.comment("Message sent to players when they are muted").define("muteMessage", "You were muted by an operator. Reason: ");
            MUTE_MESSAGE_NO_REASON = BUILDER.comment("Message sent to players when they are muted without a specified reason").define("muteMessageNoReason", "You were muted by an operator.");
            MUTE_SUCCESS = BUILDER.comment("Message sent to the moderator once the player is successfully muted").define("muteSuccess", "%player% was muted: %reason%");
            MUTE_FAILED_ALREADY_MUTED = BUILDER.comment("Message sent to the moderator if the player is already muted").define("alreadyMuted", "%player% is already muted");
            MUTE_MESSAGE_CANCELLED = BUILDER.comment("Message sent to muted players when they attempt sending a message in chat").define("cancelChatMessage", "You can't send messages while muted!");
            CANCEL_LOG_FORMAT = BUILDER.comment("The log message sent to operators and console when a muted player's event is cancelled").define("logFormat", "[CANCELED] <%player% (muted)> <message>");

            UNMUTE_MESSAGE = BUILDER.comment("Message sent to players when they are unmuted").define("unMuteMessage", "You are now unmuted!");
            UNMUTE_SUCCESS = BUILDER.comment("Message sent to the moderator once the player is unmuted").define("unMuteSuccess", "%player% was unmuted");
            UNMUTE_FAILED_NOT_MUTED = BUILDER.comment("Message sent to the moderator if the player is not muted").define("notMuted", "%player% is not muted");
            BUILDER.pop();

        BUILDER.pop();
    }

    static final ModConfigSpec   SPEC = BUILDER.build();

    private static ModConfigSpec.BooleanValue ENABLE_SPAWN_PROTECTION;
    private static ModConfigSpec.IntValue SP_OP_LEVEL;
    private static ModConfigSpec.IntValue SPAWN_PROTECTION_CENTER_X;
    private static ModConfigSpec.IntValue SPAWN_PROTECTION_CENTER_Z;
    private static ModConfigSpec.IntValue SPAWN_PROTECTION_RADIUS;
    private static ModConfigSpec.BooleanValue ALLOW_PVP;
    private static ModConfigSpec.BooleanValue ALLOW_EXPLOSION;
    private static ModConfigSpec.ConfigValue<List<? extends String>> ALLOWED_BLOCKS;
    private static ModConfigSpec.ConfigValue<List<? extends String>> SP_EFFECTS;

    private static ModConfigSpec.BooleanValue ENABLE_SPAWN_OVERRIDE;
    private static ModConfigSpec.IntValue SPAWN_X;
    private static ModConfigSpec.IntValue SPAWN_Y;
    private static ModConfigSpec.IntValue SPAWN_Z;

    private static ModConfigSpec.ConfigValue<String> SPAWN_PROTECTION_ENTER;
    private static ModConfigSpec.ConfigValue<String> SPAWN_PROTECTION_LEAVE;
        private static ModConfigSpec.ConfigValue<String> MUTE_MESSAGE;
        private static ModConfigSpec.ConfigValue<String> MUTE_MESSAGE_NO_REASON;
        private static ModConfigSpec.ConfigValue<String> MUTE_SUCCESS;
        private static ModConfigSpec.ConfigValue<String> MUTE_FAILED_ALREADY_MUTED;
        private static ModConfigSpec.ConfigValue<String> UNMUTE_MESSAGE;
        private static ModConfigSpec.ConfigValue<String> UNMUTE_SUCCESS;
        private static ModConfigSpec.ConfigValue<String> UNMUTE_FAILED_NOT_MUTED;

        private static ModConfigSpec.ConfigValue<String> MUTE_MESSAGE_CANCELLED;
        private static ModConfigSpec.ConfigValue<String> CANCEL_LOG_FORMAT;
        private static ModConfigSpec.BooleanValue MUTE_PREVENT_SIGN_PLACING;
        private static ModConfigSpec.BooleanValue ALLOW_MESSAGES_TO_OPS;
        private static ModConfigSpec.BooleanValue LOG_CANCELLED_EVENTS;

    private static ModConfigSpec.IntValue MUTE_LEVEL;
    private static ModConfigSpec.ConfigValue<List<? extends String>> MUTE_FORBIDDEN_CMD;

    private static ModConfigSpec.IntValue ALT_LEVEL;

    private static ModConfigSpec.BooleanValue ENABLE_SANC;
    private static ModConfigSpec.IntValue SANC_LEVEL;

    public static boolean sp_enabled;
    public static int sp_op_level;
    public static int sp_center_x;
    public static int sp_center_z;
    public static int sp_radius;
    public static boolean sp_pvp_enabled;
    public static boolean sp_explosion_enabled;
    public static Set<Block> allowedBlocks;
    public static Set<MobEffect> sp_effects;

    public static boolean spawn_override;
    public static int spawn_x;
    public static int spawn_y;
    public static int spawn_z;

    public static String sp_enter_msg;
    public static String sp_leave_msg;
        public static String mute_message;
        public static String mute_message_no_reason;
        public static String mute_success;
        public static String mute_failed_already_muted;
        public static String unmute_message;
        public static String unmute_success;
        public static String unmute_failed_not_muted;
        public static String mute_message_cancelled;
        public static String cancel_log_format;
        public static boolean prevent_signs;
        public static boolean allow_to_ops_msg;
        public static boolean log_cancelled_events;

    public static int mute_level;
    public static Set<String> mute_forbidden_cmd;

    public static boolean enable_sanction_cmd;
    public static int sanction_level;

    public static int alt_level;

    private static boolean validateBlockName(final Object obj) {
        if (!(obj instanceof String blockName)) return false;

        ResourceLocation rl = ResourceLocation.tryParse(blockName);
        if (rl == null) return false;

        return BuiltInRegistries.BLOCK.containsKey(rl);
    }

    private static boolean validateEffectName(final Object obj) {
        if (!(obj instanceof String effectName)) return false;

        ResourceLocation rl = ResourceLocation.tryParse(effectName);
        if (rl == null) return false;

        return BuiltInRegistries.MOB_EFFECT.containsKey(rl);
    }

    private static boolean validateString(final Object obj) {
        return true;
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent.Loading event) {
        sp_enabled = ENABLE_SPAWN_PROTECTION.get();
        sp_op_level = SP_OP_LEVEL.get();
        sp_center_x = SPAWN_PROTECTION_CENTER_X.get();
        sp_center_z = SPAWN_PROTECTION_CENTER_Z.get();
        sp_radius = SPAWN_PROTECTION_RADIUS.get();
        sp_pvp_enabled = ALLOW_PVP.get();
        sp_explosion_enabled = ALLOW_EXPLOSION.get();
        allowedBlocks = ALLOWED_BLOCKS.get().stream()
                .map(blockName -> BuiltInRegistries.BLOCK.get(ResourceLocation.tryParse(blockName)))
                .collect(Collectors.toSet());
        sp_effects = SP_EFFECTS.get().stream()
                .map(effectName -> BuiltInRegistries.MOB_EFFECT.get(ResourceLocation.tryParse(effectName)))
                .collect(Collectors.toSet());

        spawn_override = ENABLE_SPAWN_OVERRIDE.get();
        spawn_x = SPAWN_X.get();
        spawn_y = SPAWN_Y.get();
        spawn_z = SPAWN_Z.get();

        sp_enter_msg = SPAWN_PROTECTION_ENTER.get();
        sp_leave_msg = SPAWN_PROTECTION_LEAVE.get();
            mute_message = MUTE_MESSAGE.get();
            mute_message_no_reason = MUTE_MESSAGE_NO_REASON.get();
            mute_success = MUTE_SUCCESS.get();
            mute_failed_already_muted = MUTE_FAILED_ALREADY_MUTED.get();
            unmute_message = UNMUTE_MESSAGE.get();
            unmute_success = UNMUTE_SUCCESS.get();
            unmute_failed_not_muted = UNMUTE_FAILED_NOT_MUTED.get();
            mute_message_cancelled = MUTE_MESSAGE_CANCELLED.get();
            cancel_log_format = CANCEL_LOG_FORMAT.get();
            log_cancelled_events = LOG_CANCELLED_EVENTS.get();

        mute_level = MUTE_LEVEL.get();
        mute_forbidden_cmd = new HashSet<>(MUTE_FORBIDDEN_CMD.get());
        prevent_signs = MUTE_PREVENT_SIGN_PLACING.get();
        allow_to_ops_msg = ALLOW_MESSAGES_TO_OPS.get();
        alt_level = ALT_LEVEL.get();
        enable_sanction_cmd = ENABLE_SANC.get();
        sanction_level = SANC_LEVEL.get();
    }

    @SubscribeEvent
    static void onReload(final ModConfigEvent.Reloading event) {
        sp_effects.clear();
        allowedBlocks.clear();
        mute_forbidden_cmd.clear();
        onLoad(null);
    }
}