package com.example.model

data class ModBlockDef(
    val id: String,
    val name: String,
    val colorArgb: Long = 0xFF00E5FF,
    val hardness: Float = 50f,
    val isLightEmitter: Boolean = true,
    val lightRadius: Float = 4.5f,
    val lightColorArgb: Long = 0xFF00E5FF
)

data class CustomMod(
    val id: String,
    val name: String,
    val author: String,
    val version: String,
    val description: String,
    var isEnabled: Boolean = true,
    val customItems: MutableList<Item> = mutableListOf(),
    val customBlocks: MutableList<ModBlockDef> = mutableListOf(),
    val customQuests: MutableList<Quest> = mutableListOf()
)

object ModCatalog {
    val COSMIC_SAMPLE_MOD = CustomMod(
        id = "mod_cosmic_expansion",
        name = "Cosmic Aurora Expansion",
        author = "Zephyr & RealmArchitect",
        version = "1.4.0",
        description = "Adds high-tier plasma gear, glowing neon crystalline blocks, and stellar expedition quests.",
        isEnabled = true,
        customItems = mutableListOf(
            Item(
                id = "mod_item_plasma_blade",
                name = "Hyperion Plasma Blade",
                description = "Forged in the heart of a cosmic supernova. Slices through any material with ease.",
                category = ItemCategory.WEAPON,
                rarity = ItemRarity.LEGENDARY,
                iconEmoji = "❇️",
                baseValue = 500,
                attackPower = 95,
                harvestSpeed = 5.0f
            ),
            Item(
                id = "mod_item_quantum_core",
                name = "Quantum Warp Matrix",
                description = "Instantaneous dimensional teleportation catalyst.",
                category = ItemCategory.CONSUMABLE,
                rarity = ItemRarity.EPIC,
                iconEmoji = "⚛️",
                baseValue = 180,
                healAmount = 100,
                staminaAmount = 100
            )
        ),
        customBlocks = mutableListOf(
            ModBlockDef(
                id = "mod_blk_aurora_core",
                name = "Aurora Beacon Block",
                colorArgb = 0xFF00FFCC,
                hardness = 100f,
                isLightEmitter = true,
                lightRadius = 6.0f,
                lightColorArgb = 0xFF00FFCC
            )
        ),
        customQuests = mutableListOf(
            Quest(
                id = "qst_cosmic_discovery",
                title = "Cosmic Awakening",
                description = "Wield the Hyperion Plasma Blade and harvest rare aether nodes across the continent.",
                category = QuestCategory.MODDING,
                giverNpcId = "npc_zephyr",
                objectives = listOf(
                    QuestObjective("obj_cosmic_crystal", "Harvest 3 Aether Geodes", ObjectiveType.GATHER_ITEM, "mat_crystal", 0, 3)
                ),
                rewardGold = 300,
                rewardXp = 400,
                rewardAetherShards = 25
            )
        )
    )
}
