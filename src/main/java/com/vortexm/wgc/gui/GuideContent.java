package com.vortexm.wgc.gui;

import com.vortexm.wgc.WorldGuardComplater;
import com.vortexm.wgc.util.Lang;

import java.util.ArrayList;
import java.util.List;

/**
 * Static content for the in-game guide (/wgc guide). Each page is
 * {title, lore line 1, lore line 2, lore line 3}.
 */
final class GuideContent {

    private GuideContent() {
    }

    static List<String[]> pages(WorldGuardComplater plugin, String section) {
        Lang lang = plugin.lang();
        List<String[]> out = new ArrayList<>();
        switch (section) {
            case "commands" -> {
                out.add(page(lang, "guide-cmd-1-title", "guide-cmd-1-l1", "guide-cmd-1-l2", "guide-cmd-1-l3"));
                out.add(page(lang, "guide-cmd-2-title", "guide-cmd-2-l1", "guide-cmd-2-l2", "guide-cmd-2-l3"));
                out.add(page(lang, "guide-cmd-3-title", "guide-cmd-3-l1", "guide-cmd-3-l2", "guide-cmd-3-l3"));
                out.add(page(lang, "guide-cmd-4-title", "guide-cmd-4-l1", "guide-cmd-4-l2", "guide-cmd-4-l3"));
                out.add(page(lang, "guide-cmd-5-title", "guide-cmd-5-l1", "guide-cmd-5-l2", "guide-cmd-5-l3"));
            }
            case "flags" -> {
                out.add(page(lang, "guide-flag-1-title", "guide-flag-1-l1", "guide-flag-1-l2", "guide-flag-1-l3"));
                out.add(page(lang, "guide-flag-2-title", "guide-flag-2-l1", "guide-flag-2-l2", "guide-flag-2-l3"));
                out.add(page(lang, "guide-flag-3-title", "guide-flag-3-l1", "guide-flag-3-l2", "guide-flag-3-l3"));
                out.add(page(lang, "guide-flag-4-title", "guide-flag-4-l1", "guide-flag-4-l2", "guide-flag-4-l3"));
                out.add(page(lang, "guide-flag-5-title", "guide-flag-5-l1", "guide-flag-5-l2", "guide-flag-5-l3"));
            }
            case "claims" -> {
                out.add(page(lang, "guide-claim-1-title", "guide-claim-1-l1", "guide-claim-1-l2", "guide-claim-1-l3"));
                out.add(page(lang, "guide-claim-2-title", "guide-claim-2-l1", "guide-claim-2-l2", "guide-claim-2-l3"));
                out.add(page(lang, "guide-claim-3-title", "guide-claim-3-l1", "guide-claim-3-l2", "guide-claim-3-l3"));
                out.add(page(lang, "guide-claim-4-title", "guide-claim-4-l1", "guide-claim-4-l2", "guide-claim-4-l3"));
                out.add(page(lang, "guide-claim-5-title", "guide-claim-5-l1", "guide-claim-5-l2", "guide-claim-5-l3"));
            }
            case "permissions" -> {
                out.add(page(lang, "guide-perm-1-title", "guide-perm-1-l1", "guide-perm-1-l2", "guide-perm-1-l3"));
                out.add(page(lang, "guide-perm-2-title", "guide-perm-2-l1", "guide-perm-2-l2", "guide-perm-2-l3"));
                out.add(page(lang, "guide-perm-3-title", "guide-perm-3-l1", "guide-perm-3-l2", "guide-perm-3-l3"));
                out.add(page(lang, "guide-perm-4-title", "guide-perm-4-l1", "guide-perm-4-l2", "guide-perm-4-l3"));
                out.add(page(lang, "guide-perm-5-title", "guide-perm-5-l1", "guide-perm-5-l2", "guide-perm-5-l3"));
            }
            default -> {
                out.add(page(lang, "guide-tip-1-title", "guide-tip-1-l1", "guide-tip-1-l2", "guide-tip-1-l3"));
                out.add(page(lang, "guide-tip-2-title", "guide-tip-2-l1", "guide-tip-2-l2", "guide-tip-2-l3"));
                out.add(page(lang, "guide-tip-3-title", "guide-tip-3-l1", "guide-tip-3-l2", "guide-tip-3-l3"));
            }
        }
        return out;
    }

    private static String[] page(Lang lang, String titleKey, String l1, String l2, String l3) {
        return new String[]{
                lang.fmt(titleKey),
                lang.fmt(l1),
                lang.fmt(l2),
                lang.fmt(l3)
        };
    }
}
