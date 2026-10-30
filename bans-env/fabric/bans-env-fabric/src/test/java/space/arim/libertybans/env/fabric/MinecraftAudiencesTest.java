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

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MinecraftAudiencesTest {

    @BeforeAll
    public static void bootstrap() {
        net.minecraft.SharedConstants.tryDetectVersion();
        net.minecraft.server.Bootstrap.bootStrap();
    }

    @Test
    public void textWithStyle() {
        Component message = Component.text("Hello")
                .color(NamedTextColor.RED)
                .decorate(TextDecoration.BOLD)
                .clickEvent(ClickEvent.runCommand("/lb reload"))
                .hoverEvent(HoverEvent.showText(Component.text("Hover")));

        net.minecraft.network.chat.Component nativeMessage = MinecraftAudiences.asNative(message);

        assertEquals("Hello", nativeMessage.getString());
        assertNotNull(nativeMessage.getStyle().getColor());
        assertEquals(0xFF5555, nativeMessage.getStyle().getColor().getValue());
        assertTrue(nativeMessage.getStyle().isBold());
        assertNotNull(nativeMessage.getStyle().getClickEvent());
        assertNotNull(nativeMessage.getStyle().getHoverEvent());
    }

    @Test
    public void translatableWithChildren() {
        Component message = Component.translatable("block.minecraft.stone")
                .append(Component.text(" suffix", NamedTextColor.BLUE));

        net.minecraft.network.chat.Component nativeMessage = MinecraftAudiences.asNative(message);

        TranslatableContents contents = assertInstanceOf(TranslatableContents.class, nativeMessage.getContents());
        assertEquals("block.minecraft.stone", contents.getKey());
        assertEquals(1, nativeMessage.getSiblings().size());
        assertNull(nativeMessage.getStyle().getColor());
    }

    @Test
    public void openFileClickEventIsStripped() {
        Component message = Component.text("Denial")
                .clickEvent(ClickEvent.openFile("playerdata.txt"));

        net.minecraft.network.chat.Component nativeMessage = MinecraftAudiences.asNative(message);

        assertEquals("Denial", nativeMessage.getString());
        assertNull(nativeMessage.getStyle().getClickEvent());
    }
}
