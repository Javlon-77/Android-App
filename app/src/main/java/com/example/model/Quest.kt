package com.example.model

enum class QuestCategory(val displayName: String, val badgeEmoji: String) {
    STORY("Main Story", "📜"),
    BUILDING("Architecture", "🏗️"),
    CRAFTING("Crafting & Smithing", "🔨"),
    EXPLORATION("Exploration", "🧭"),
    BOUNTY("Vanguard Bounty", "⚔️"),
    MODDING("Modding Studio", "✨")
}

enum class QuestStatus {
    NOT_STARTED, ACTIVE, READY_TO_CLAIM, COMPLETED
}

enum class ObjectiveType {
    GATHER_ITEM, MINE_RESOURCE, BUILD_BLOCK, TALK_NPC, CRAFT_ITEM, CREATE_MOD
}

data class QuestObjective(
    val id: String,
    val description: String,
    val type: ObjectiveType,
    val targetId: String? = null,
    var currentCount: Int = 0,
    val requiredCount: Int = 1,
    var isCompleted: Boolean = false
) {
    fun progress(amount: Int = 1) {
        currentCount = (currentCount + amount).coerceAtMost(requiredCount)
        if (currentCount >= requiredCount) {
            isCompleted = true
        }
    }
}

data class Quest(
    val id: String,
    val title: String,
    val description: String,
    val category: QuestCategory,
    val giverNpcId: String,
    val objectives: List<QuestObjective>,
    val rewardGold: Int,
    val rewardXp: Int,
    val rewardAetherShards: Int = 0,
    val rewardItems: List<ItemStack> = emptyList(),
    var status: QuestStatus = QuestStatus.NOT_STARTED
) {
    fun checkCompletion(): Boolean {
        return objectives.all { it.isCompleted }
    }
}

object QuestCatalog {
    val QUEST_FIRST_STEPS = Quest(
        id = "qst_first_steps",
        title = "Awakening in the Shire",
        description = "Meet Elder Ronald in the Verdant Shire and gather basic timber to sustain yourself in the wilderness.",
        category = QuestCategory.STORY,
        giverNpcId = "npc_ronald",
        objectives = listOf(
            QuestObjective("obj_timber", "Harvest 4 Oak Timber from Shire trees", ObjectiveType.GATHER_ITEM, "mat_wood", 0, 4)
        ),
        rewardGold = 50,
        rewardXp = 80,
        rewardAetherShards = 5,
        rewardItems = listOf(ItemStack(ItemCatalog.WOOD_PICKAXE, 1), ItemStack(ItemCatalog.HEALTH_POTION, 2))
    )

    val QUEST_SHELTER = Quest(
        id = "qst_shelter",
        title = "Architect of Aetheria",
        description = "Place building blocks in the world to establish your first outpost base, and illuminate it with a torch.",
        category = QuestCategory.BUILDING,
        giverNpcId = "npc_ronald",
        objectives = listOf(
            QuestObjective("obj_build_blocks", "Place 6 Building Blocks (Walls/Bricks)", ObjectiveType.BUILD_BLOCK, null, 0, 6),
            QuestObjective("obj_place_torch", "Place 1 Luminous Torch for lighting", ObjectiveType.BUILD_BLOCK, "bld_torch", 0, 1)
        ),
        rewardGold = 80,
        rewardXp = 120,
        rewardAetherShards = 8,
        rewardItems = listOf(ItemStack(ItemCatalog.STONE_BRICK, 12), ItemStack(ItemCatalog.LANTERN, 1))
    )

    val QUEST_CITADEL_DEFENSE = Quest(
        id = "qst_citadel_defense",
        title = "Citadel Vanguard Call",
        description = "Report to Captain Kaelen and help fortify the high fortress perimeter by quarrying stone and iron.",
        category = QuestCategory.BOUNTY,
        giverNpcId = "npc_kaelen",
        objectives = listOf(
            QuestObjective("obj_quarry_stone", "Quarry 8 Granite Stone boulders", ObjectiveType.GATHER_ITEM, "mat_stone", 0, 8),
            QuestObjective("obj_mine_iron", "Mine 4 Iron Ore from rock veins", ObjectiveType.GATHER_ITEM, "mat_iron_ore", 0, 4)
        ),
        rewardGold = 120,
        rewardXp = 160,
        rewardAetherShards = 12,
        rewardItems = listOf(ItemStack(ItemCatalog.IRON_LONGSWORD, 1), ItemStack(ItemCatalog.STAMINA_ELIXIR, 2))
    )

    val QUEST_MANA_BLOOM = Quest(
        id = "qst_mana_bloom",
        title = "Essence of the Grove",
        description = "Help Aria harvest vibrant Mana Blooms in the twilight canopies of the Enchanted Grove.",
        category = QuestCategory.EXPLORATION,
        giverNpcId = "npc_aria",
        objectives = listOf(
            QuestObjective("obj_mana_bloom", "Collect 5 Mana Blooms", ObjectiveType.GATHER_ITEM, "mat_bloom", 0, 5)
        ),
        rewardGold = 90,
        rewardXp = 140,
        rewardAetherShards = 10,
        rewardItems = listOf(ItemStack(ItemCatalog.AETHER_NECTAR, 1), ItemStack(ItemCatalog.HEALTH_POTION, 3))
    )

    val QUEST_IRON_FORGE = Quest(
        id = "qst_iron_forge",
        title = "Trial of the Master Smith",
        description = "Smelt raw metal into refined ingots at Gorim's Forge in the volcanic Shadowfell Crags.",
        category = QuestCategory.CRAFTING,
        giverNpcId = "npc_gorim",
        objectives = listOf(
            QuestObjective("obj_smelt_ingots", "Craft 3 Refined Iron Ingots at a Forge", ObjectiveType.CRAFT_ITEM, "rcp_iron_ingot", 0, 3)
        ),
        rewardGold = 150,
        rewardXp = 200,
        rewardAetherShards = 15,
        rewardItems = listOf(ItemStack(ItemCatalog.IRON_CHESTPLATE, 1))
    )

    val QUEST_CARAVAN_TRADE = Quest(
        id = "qst_caravan_trade",
        title = "Caravan Silk & Sand",
        description = "Trade high-value building commodities with Lyra's desert outpost caravan.",
        category = QuestCategory.EXPLORATION,
        giverNpcId = "npc_lyra",
        objectives = listOf(
            QuestObjective("obj_trade_supplies", "Deliver 8 Timber and 3 Iron Ingots to Lyra", ObjectiveType.GATHER_ITEM, "mat_wood", 0, 8)
        ),
        rewardGold = 200,
        rewardXp = 250,
        rewardAetherShards = 20,
        rewardItems = listOf(ItemStack(ItemCatalog.SUN_BOW, 1))
    )

    val QUEST_MOD_CREATOR = Quest(
        id = "qst_mod_creator",
        title = "Genesis Modder",
        description = "Use the built-in Mod Studio to create and enable a custom mod for the game world.",
        category = QuestCategory.MODDING,
        giverNpcId = "npc_zephyr",
        objectives = listOf(
            QuestObjective("obj_make_mod", "Create and activate a custom mod item or block in Mod Studio", ObjectiveType.CREATE_MOD, null, 0, 1)
        ),
        rewardGold = 250,
        rewardXp = 350,
        rewardAetherShards = 30,
        rewardItems = listOf(ItemStack(ItemCatalog.ASTRAL_PICKAXE, 1))
    )

    val ALL_QUESTS = listOf(
        QUEST_FIRST_STEPS,
        QUEST_SHELTER,
        QUEST_CITADEL_DEFENSE,
        QUEST_MANA_BLOOM,
        QUEST_IRON_FORGE,
        QUEST_CARAVAN_TRADE,
        QUEST_MOD_CREATOR
    )

    fun findById(id: String): Quest? = ALL_QUESTS.find { it.id == id }
}
