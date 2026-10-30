/*
 * LibertyBans-fabric
 * Copyright © 2026 Anand Beh
 *
 * LibertyBans-fabric is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * LibertyBans-fabric is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with LibertyBans-fabric. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU Lesser General Public License.
 */

package space.arim.libertybans.env.fabric;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;

final class MinecraftAudiences {

    private MinecraftAudiences() {
    }

    static Audience audience(CommandSourceStack source) {
        return new CommandSourceAudience(source);
    }

    static Audience audience(ServerPlayer player) {
        return new PlayerAudience(player);
    }

    static net.minecraft.network.chat.Component asNative(Component message) {
        JsonElement serialized = GsonComponentSerializer.gson().serializeToTree(message);
        try {
            return decode(serialized);
        } catch (RuntimeException ex) {
            try {
                // Vanilla refuses click events such as open_file when decoding; drop them and retry
                return decode(stripRejectedClickEvents(serialized.deepCopy()));
            } catch (RuntimeException rejected) {
                return net.minecraft.network.chat.Component.literal(
                        PlainTextComponentSerializer.plainText().serialize(message));
            }
        }
    }

    private static net.minecraft.network.chat.Component decode(JsonElement serialized) {
        Pair<net.minecraft.network.chat.Component, JsonElement> decoded =
                ComponentSerialization.CODEC.decode(
                                RegistryContext.PROVIDER.createSerializationContext(JsonOps.INSTANCE), serialized)
                        .result()
                        .orElseThrow(() -> new IllegalStateException("Unable to convert component " + serialized));
        return decoded.getFirst();
    }

    private static JsonElement stripRejectedClickEvents(JsonElement element) {
        if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            JsonElement clickEvent = object.get("clickEvent");
            if (clickEvent != null && clickEvent.isJsonObject()) {
                JsonElement action = clickEvent.getAsJsonObject().get("action");
                if (action != null && "open_file".equals(action.getAsString())) {
                    object.remove("clickEvent");
                }
            }
            for (String key : new ArrayList<>(object.keySet())) {
                object.add(key, stripRejectedClickEvents(object.get(key)));
            }
        } else if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            for (int i = 0; i < array.size(); i++) {
                array.set(i, stripRejectedClickEvents(array.get(i)));
            }
        }
        return element;
    }

    private static final class RegistryContext {

        static final HolderLookup.Provider PROVIDER =
                RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
    }

    private static final class CommandSourceAudience implements Audience {

        private final CommandSourceStack source;

        private CommandSourceAudience(CommandSourceStack source) {
            this.source = source;
        }

        @Override
        public void sendMessage(Component message) {
            source.sendSystemMessage(asNative(message));
        }
    }

    private static final class PlayerAudience implements Audience {

        private final ServerPlayer player;

        private PlayerAudience(ServerPlayer player) {
            this.player = player;
        }

        @Override
        public void sendMessage(Component message) {
            player.sendSystemMessage(asNative(message));
        }
    }
}
