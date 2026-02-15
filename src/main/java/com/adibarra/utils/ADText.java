package com.adibarra.utils;

import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unused")
public class ADText {

    private ADText() {
        throw new IllegalStateException("Utility class. Do not instantiate.");
    }

    public static final Set<String> TRUE_VALUES = new HashSet<>(Arrays.asList("true", "t", "yes", "on", "enable", "enabled"));
    public static final Set<String> FALSE_VALUES = new HashSet<>(Arrays.asList("false", "f", "no", "off", "disable", "disabled"));

    public static MutableText joinTextMutable(List<Text> list) {
        MutableText out = Text.empty();
        for (Text text : list) {
            out.append(text);
        }
        return out;
    }

    public static Text joinText(List<Text> list) {
        return joinTextMutable(list);
    }

    public static MutableText buildCmdLink(String base, String literal) {
        String cmd = "/" + base + " " + literal;
        return Text.literal(cmd)
            .setStyle(Style.EMPTY
                .withColor(Formatting.AQUA)
                .withClickEvent(new ClickEvent.RunCommand(cmd))
                .withHoverEvent(new HoverEvent.ShowText(Text.literal("Click to run " + cmd)))
            );
    }

    public static MutableText colorValue(String value) {
        value = value.toLowerCase();
        if (TRUE_VALUES.contains(value)) {
            return Text.literal(value).formatted(Formatting.GREEN);
        } else if (FALSE_VALUES.contains(value)) {
            return Text.literal(value).formatted(Formatting.RED);
        } else if (ADMisc.isDouble(value)) {
            return Text.literal(value).formatted(Formatting.BLUE);
        }
        return Text.literal(value).formatted(Formatting.GOLD);
    }
}
