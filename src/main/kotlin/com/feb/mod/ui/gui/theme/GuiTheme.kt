package com.feb.mod.ui.gui.theme

import com.feb.mod.ui.gui.particles.ParticleManager

data class GuiTheme(
    val particleStyle: ParticleManager.Style,
    val background: Int,
    val sidebar: Int,
    val addonPanel: Int,
    val divider: Int,
    val buttonIdle: Int,
    val buttonHover: Int,
    val buttonActive: Int,
    val buttonActiveBorder: Int,
)

object GuiThemes {
    val WINTER = GuiTheme(
        particleStyle      = ParticleManager.Style.WINTER,
        background         = 0x991E3A5F.toInt(),
        sidebar            = 0xCC15283F.toInt(),
        addonPanel         = 0xBB172E4A.toInt(),
        divider            = 0x60FFFFFF.toInt(),
        buttonIdle         = 0xFF1E3A5F.toInt(),
        buttonHover        = 0xFF2E4A6F.toInt(),
        buttonActive       = 0xFF2E5A8F.toInt(),
        buttonActiveBorder = 0xFF4E7AAF.toInt(),
    )

    val SPRING = GuiTheme(
        particleStyle      = ParticleManager.Style.SPRING,
        background         = 0x991E5F2A.toInt(),
        sidebar            = 0xCC15401C.toInt(),
        addonPanel         = 0xBB1A4A20.toInt(),
        divider            = 0x60AAFFAA.toInt(),
        buttonIdle         = 0xFF1A3D20.toInt(),
        buttonHover        = 0xFF2A5230.toInt(),
        buttonActive       = 0xFF2A6B35.toInt(),
        buttonActiveBorder = 0xFF4AAF60.toInt(),
    )

    val SUMMER = GuiTheme(
        particleStyle      = ParticleManager.Style.SUMMER,
        background         = 0x995F4A1E.toInt(),
        sidebar            = 0xCC3F3010.toInt(),
        addonPanel         = 0xBB4A3812.toInt(),
        divider            = 0x60FFEE88.toInt(),
        buttonIdle         = 0xFF3D2E0A.toInt(),
        buttonHover        = 0xFF5C4510.toInt(),
        buttonActive       = 0xFF7A5C10.toInt(),
        buttonActiveBorder = 0xFFCB9A20.toInt(),
    )

    val AUTUMN = GuiTheme(
        particleStyle      = ParticleManager.Style.AUTUMN,
        background         = 0x995F2E1E.toInt(),
        sidebar            = 0xCC3F1E0F.toInt(),
        addonPanel         = 0xBB4A2010.toInt(),
        divider            = 0x60FFAA55.toInt(),
        buttonIdle         = 0xFF3D1A08.toInt(),
        buttonHover        = 0xFF5C2A10.toInt(),
        buttonActive       = 0xFF7A3510.toInt(),
        buttonActiveBorder = 0xFFCB6020.toInt(),
    )

    fun fromStyle(style: ParticleManager.Style) = when (style) {
        ParticleManager.Style.WINTER -> WINTER
        ParticleManager.Style.SPRING -> SPRING
        ParticleManager.Style.SUMMER -> SUMMER
        ParticleManager.Style.AUTUMN -> AUTUMN
    }
}