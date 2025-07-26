package com.github.subat0m1c.hatecheaters.pvgui.v2

enum class PageEntries(val pageHandler: () -> PVPage) {
    Overview({ com.github.subat0m1c.hatecheaters.pvgui.v2.pages.Overview }),
    Profile({ com.github.subat0m1c.hatecheaters.pvgui.v2.pages.Profile }),
    Dungeons({ com.github.subat0m1c.hatecheaters.pvgui.v2.pages.Dungeons }),
    Inventory({ com.github.subat0m1c.hatecheaters.pvgui.v2.pages.Inventory }),
    Pets({ com.github.subat0m1c.hatecheaters.pvgui.v2.pages.Pets });

    inline val page get() = pageHandler()
}