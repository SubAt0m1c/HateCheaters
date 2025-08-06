package com.github.subat0m1c.hatecheaters.utils.odinwrappers

import me.odinmain.utils.skyblock.PartyUtils

object Party {
    inline val isLeader get() = PartyUtils.isLeader()
}