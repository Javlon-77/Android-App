package com.example.model

enum class DialogueActionType {
    NONE, ACCEPT_QUEST, OPEN_SHOP, GAIN_AFFINITY, GIVE_ITEM, FAST_TRAVEL
}

data class DialogueChoice(
    val text: String,
    val nextNodeId: String? = null,
    val requiredAffinity: Int = 0,
    val actionType: DialogueActionType = DialogueActionType.NONE,
    val actionParam: String = ""
)

data class DialogueNode(
    val id: String,
    val speakerText: String,
    val choices: List<DialogueChoice>
)

data class ShopItem(
    val item: Item,
    val buyPrice: Int,
    val sellPrice: Int,
    var stock: Int = 99
)

data class NPC(
    val id: String,
    val name: String,
    val title: String,
    val biome: BiomeType,
    val x: Float,
    val y: Float,
    val avatarEmoji: String,
    var affinity: Int = 20, // 0 to 100 hearts
    var mood: String = "Welcoming",
    val personality: String,
    val dialogueRootId: String,
    val dialogueNodes: Map<String, DialogueNode>,
    val offeredQuestIds: List<String>,
    val shopItems: List<ShopItem>
)

object NPCCatalog {
    val ELDER_RONALD = NPC(
        id = "npc_ronald",
        name = "Elder Ronald",
        title = "Village Elder & Lorekeeper",
        biome = BiomeType.VERDANT_SHIRE,
        x = 62f,
        y = 60f,
        avatarEmoji = "🧙‍♂️",
        affinity = 35,
        mood = "Wise & Gentle",
        personality = "Kind elder who has guided wanderers through the Aetherian wilds for decades.",
        dialogueRootId = "root",
        dialogueNodes = mapOf(
            "root" to DialogueNode(
                "root",
                "Greetings, young traveler. The ancient ley-lines whisper of great changes across Aetheria. Have you come seeking wisdom or guidance?",
                listOf(
                    DialogueChoice("What is this land of Aetheria?", "lore"),
                    DialogueChoice("Do you have any tasks for me?", "quest", actionType = DialogueActionType.ACCEPT_QUEST, actionParam = "qst_first_steps"),
                    DialogueChoice("Can you teach me about building and crafting?", "building_guide"),
                    DialogueChoice("Farewell, Elder.", null)
                )
            ),
            "lore" to DialogueNode(
                "lore",
                "Long ago, the Great Cataclysm fractured the realm into floating biomes held together by Aether Crystals. You have the power to rebuild civilization block by block!",
                listOf(
                    DialogueChoice("Fascinating. How do I harness this power?", "building_guide", actionType = DialogueActionType.GAIN_AFFINITY, actionParam = "5"),
                    DialogueChoice("Thank you, Elder.", "root")
                )
            ),
            "building_guide" to DialogueNode(
                "building_guide",
                "Gather wood and stone from the surroundings. Using your Build mode, you can place walls, glass, torches, and workshops anywhere you wish. Build yourself a stronghold against the night!",
                listOf(
                    DialogueChoice("I'll start gathering materials right away!", "root", actionType = DialogueActionType.GAIN_AFFINITY, actionParam = "5")
                )
            ),
            "quest" to DialogueNode(
                "quest",
                "Splendid! Take this blueprint. Gather 6 timber and 4 granite stone to establish your first outpost shelter. I shall reward you handsomely.",
                listOf(
                    DialogueChoice("Consider it done!", "root")
                )
            )
        ),
        offeredQuestIds = listOf("qst_first_steps", "qst_shelter"),
        shopItems = listOf(
            ShopItem(ItemCatalog.HEALTH_POTION, 15, 8),
            ShopItem(ItemCatalog.WOOD, 3, 1),
            ShopItem(ItemCatalog.STONE, 3, 1),
            ShopItem(ItemCatalog.TORCH, 5, 2)
        )
    )

    val CAPTAIN_KAELEN = NPC(
        id = "npc_kaelen",
        name = "Captain Kaelen",
        title = "Commander of Citadel Vanguard",
        biome = BiomeType.SKY_CITADEL,
        x = 75f,
        y = 30f,
        avatarEmoji = "⚔️",
        affinity = 25,
        mood = "Vigilant",
        personality = "Stern protector defending the Sky Citadel against shadow beasts and bandits.",
        dialogueRootId = "root",
        dialogueNodes = mapOf(
            "root" to DialogueNode(
                "root",
                "Halt citizen! The Vanguard is on high alert. If you carry a blade and have a steady heart, we have bounties to fulfill.",
                listOf(
                    DialogueChoice("I'm ready to defend the Citadel.", "bounty", actionType = DialogueActionType.ACCEPT_QUEST, actionParam = "qst_citadel_defense"),
                    DialogueChoice("Show me the garrison armory.", "shop", actionType = DialogueActionType.OPEN_SHOP),
                    DialogueChoice("At ease, Captain.", null)
                )
            ),
            "bounty" to DialogueNode(
                "bounty",
                "Rift beasts have been spotted near the Shadowfell borders. Slay them or mine reinforced obsidian to fortify our outpost walls!",
                listOf(
                    DialogueChoice("I accept the garrison contract.", "root", actionType = DialogueActionType.GAIN_AFFINITY, actionParam = "10")
                )
            ),
            "shop" to DialogueNode(
                "shop",
                "Only the finest forged gear for our defenders. Browse our weapons and armor.",
                listOf(
                    DialogueChoice("Return to conversation.", "root")
                )
            )
        ),
        offeredQuestIds = listOf("qst_citadel_defense", "qst_obsidian_hunt"),
        shopItems = listOf(
            ShopItem(ItemCatalog.IRON_LONGSWORD, 45, 25),
            ShopItem(ItemCatalog.IRON_CHESTPLATE, 80, 40),
            ShopItem(ItemCatalog.IRON_PICKAXE, 50, 25),
            ShopItem(ItemCatalog.DEFENSE_TURRET, 250, 120)
        )
    )

    val ARIA_ALCHEMIST = NPC(
        id = "npc_aria",
        name = "Aria",
        title = "Grand Herbalist & Alchemist",
        biome = BiomeType.ENCHANTED_GROVE,
        x = 25f,
        y = 90f,
        avatarEmoji = "🧝‍♀️",
        affinity = 30,
        mood = "Enchanted",
        personality = "Gentle botanist deeply connected to nature and the magical essence of the grove.",
        dialogueRootId = "root",
        dialogueNodes = mapOf(
            "root" to DialogueNode(
                "root",
                "Welcome to the Enchanted Grove, dear traveler. Can you smell the sweet scent of Mana Blooms? Nature here pulses with mystical vitality.",
                listOf(
                    DialogueChoice("What potions can you brew?", "brews"),
                    DialogueChoice("Need help gathering rare herbs?", "herb_quest", actionType = DialogueActionType.ACCEPT_QUEST, actionParam = "qst_mana_bloom"),
                    DialogueChoice("I would like to trade alchemical goods.", "shop", actionType = DialogueActionType.OPEN_SHOP),
                    DialogueChoice("May the spirits protect you.", null)
                )
            ),
            "brews" to DialogueNode(
                "brews",
                "With Mana Blooms and Aether Crystals, I concoct draughts that cure wounds in an instant and let your feet carry you like the wind!",
                listOf(
                    DialogueChoice("That sounds incredible!", "root", actionType = DialogueActionType.GAIN_AFFINITY, actionParam = "5")
                )
            ),
            "herb_quest" to DialogueNode(
                "herb_quest",
                "Oh wonderful! Bring me 5 Mana Blooms from the purple glades and I will brew you an elixir of vitality and share ancient recipes.",
                listOf(
                    DialogueChoice("I will harvest them with care.", "root")
                )
            ),
            "shop" to DialogueNode(
                "shop",
                "Here are my latest botanical concoctions.",
                listOf(
                    DialogueChoice("Back.", "root")
                )
            )
        ),
        offeredQuestIds = listOf("qst_mana_bloom", "qst_aether_nectar"),
        shopItems = listOf(
            ShopItem(ItemCatalog.HEALTH_POTION, 15, 8),
            ShopItem(ItemCatalog.STAMINA_ELIXIR, 18, 10),
            ShopItem(ItemCatalog.AETHER_NECTAR, 60, 30),
            ShopItem(ItemCatalog.MANA_BLOOM, 10, 5)
        )
    )

    val GORIM_IRONBREAKER = NPC(
        id = "npc_gorim",
        name = "Gorim Ironbreaker",
        title = "High Guild Master Smith",
        biome = BiomeType.SHADOWFELL_CRAGS,
        x = 95f,
        y = 95f,
        avatarEmoji = "🧔",
        affinity = 20,
        mood = "Boisterous",
        personality = "Dwarven master metalsmith who can turn raw ore and dragon obsidian into indestructible armor.",
        dialogueRootId = "root",
        dialogueNodes = mapOf(
            "root" to DialogueNode(
                "root",
                "By the anvil of the mountain! Come closer to the fire. If you aren't here to hammer iron, then stay out of my sparks!",
                listOf(
                    DialogueChoice("Teach me the secrets of master forging.", "forge_secrets"),
                    DialogueChoice("Need high-grade ores from the deep veins?", "ore_quest", actionType = DialogueActionType.ACCEPT_QUEST, actionParam = "qst_iron_forge"),
                    DialogueChoice("Inspect master weapons & armors.", "shop", actionType = DialogueActionType.OPEN_SHOP),
                    DialogueChoice("Keep the forge blazing, Gorim.", null)
                )
            ),
            "forge_secrets" to DialogueNode(
                "forge_secrets",
                "Raw ore is useless without heat. Smelt your iron in a forge, then fuse it with Aether crystals to awaken the metal's resonance!",
                listOf(
                    DialogueChoice("A true master's wisdom.", "root", actionType = DialogueActionType.GAIN_AFFINITY, actionParam = "5")
                )
            ),
            "ore_quest" to DialogueNode(
                "ore_quest",
                "My crucible is hungry! Bring me 6 Iron Ore and 2 Obsidian Shards, and I'll craft you an Iron Broadsword on the house!",
                listOf(
                    DialogueChoice("I'll strike the veins!", "root")
                )
            ),
            "shop" to DialogueNode(
                "shop",
                "Heavy forged steel and refined ingots. Everything stamped with the Ironbreaker seal.",
                listOf(
                    DialogueChoice("Back.", "root")
                )
            )
        ),
        offeredQuestIds = listOf("qst_iron_forge"),
        shopItems = listOf(
            ShopItem(ItemCatalog.IRON_INGOT, 15, 10),
            ShopItem(ItemCatalog.STONE_PICKAXE, 20, 10),
            ShopItem(ItemCatalog.IRON_PICKAXE, 50, 25),
            ShopItem(ItemCatalog.CRYSTAL_BLADE, 120, 60)
        )
    )

    val LYRA_MERCHANT = NPC(
        id = "npc_lyra",
        name = "Lyra the Trader",
        title = "Leader of the Dune Caravan",
        biome = BiomeType.WHISPERING_DESERT,
        x = 90f,
        y = 35f,
        avatarEmoji = "🧕",
        affinity = 40,
        mood = "Charismatic",
        personality = "Shrewd and charming caravan master who runs the dynamic cross-biome market economy.",
        dialogueRootId = "root",
        dialogueNodes = mapOf(
            "root" to DialogueNode(
                "root",
                "Ah, an esteemed visitor! In the dunes, everything has a price, and everything has an opportunity. Looking to buy, sell, or trade contracts?",
                listOf(
                    DialogueChoice("How does the market economy work here?", "market_lore"),
                    DialogueChoice("Do you have caravan transport quests?", "trade_quest", actionType = DialogueActionType.ACCEPT_QUEST, actionParam = "qst_caravan_trade"),
                    DialogueChoice("Open the Caravan Exchange.", "shop", actionType = DialogueActionType.OPEN_SHOP),
                    DialogueChoice("May fortune follow your caravans.", null)
                )
            ),
            "market_lore" to DialogueNode(
                "market_lore",
                "Prices fluctuate based on supply and demand! If players dump stacks of timber, timber prices plummet. Stock up on rare crystals when low, and sell high!",
                listOf(
                    DialogueChoice("I'll keep a sharp eye on market rates.", "root", actionType = DialogueActionType.GAIN_AFFINITY, actionParam = "10")
                )
            ),
            "trade_quest" to DialogueNode(
                "trade_quest",
                "My pack animals need supplies. Deliver 8 Wood Timber and 5 Refined Iron Ingots to my trading stall for 150 gold coins and 10 Aether Shards!",
                listOf(
                    DialogueChoice("Deal accepted!", "root")
                )
            ),
            "shop" to DialogueNode(
                "shop",
                "Exotic goods from every corner of the floating continent.",
                listOf(
                    DialogueChoice("Back.", "root")
                )
            )
        ),
        offeredQuestIds = listOf("qst_caravan_trade"),
        shopItems = listOf(
            ShopItem(ItemCatalog.GOLD_NUGGET, 40, 30),
            ShopItem(ItemCatalog.AETHER_CRYSTAL, 30, 20),
            ShopItem(ItemCatalog.SUN_BOW, 180, 90),
            ShopItem(ItemCatalog.TELEPORTER, 150, 75)
        )
    )

    val ZEPHYR_MAGE = NPC(
        id = "npc_zephyr",
        name = "Zephyr",
        title = "Master of Runic Modding",
        biome = BiomeType.CRYSTAL_CANYON,
        x = 35f,
        y = 35f,
        avatarEmoji = "🧙",
        affinity = 30,
        mood = "Curious & Inventive",
        personality = "Eccentric arcane architect who studies reality alteration, scripting, and custom modding.",
        dialogueRootId = "root",
        dialogueNodes = mapOf(
            "root" to DialogueNode(
                "root",
                "Greetings! You feel the crystalline resonance, do you not? In this canyon, reality is malleable. With our Mod Studio, you can author your own items, blocks, and quests!",
                listOf(
                    DialogueChoice("Tell me about the Mod Studio.", "modding_info"),
                    DialogueChoice("Do you have a modding challenge for me?", "mod_quest", actionType = DialogueActionType.ACCEPT_QUEST, actionParam = "qst_mod_creator"),
                    DialogueChoice("Trade arcane relics.", "shop", actionType = DialogueActionType.OPEN_SHOP),
                    DialogueChoice("Farewell, Zephyr.", null)
                )
            ),
            "modding_info" to DialogueNode(
                "modding_info",
                "Access the Mod Studio from the main interface anytime. You can script new custom items, design vibrant blocks with custom light emissions, and create your own NPCs!",
                listOf(
                    DialogueChoice("That is unlimited power!", "root", actionType = DialogueActionType.GAIN_AFFINITY, actionParam = "10")
                )
            ),
            "mod_quest" to DialogueNode(
                "mod_quest",
                "Create your very first custom item or block in the Mod Studio and activate the mod to earn legendary status and 200 Gold!",
                listOf(
                    DialogueChoice("I will become a creator of worlds!", "root")
                )
            ),
            "shop" to DialogueNode(
                "shop",
                "High tier aetherial artifacts and catalysts.",
                listOf(
                    DialogueChoice("Back.", "root")
                )
            )
        ),
        offeredQuestIds = listOf("qst_mod_creator"),
        shopItems = listOf(
            ShopItem(ItemCatalog.AETHER_CRYSTAL, 28, 20),
            ShopItem(ItemCatalog.LANTERN, 15, 8),
            ShopItem(ItemCatalog.VOID_STAFF, 350, 180),
            ShopItem(ItemCatalog.ASTRAL_PICKAXE, 200, 100)
        )
    )

    val ALL_NPCS = listOf(
        ELDER_RONALD,
        CAPTAIN_KAELEN,
        ARIA_ALCHEMIST,
        GORIM_IRONBREAKER,
        LYRA_MERCHANT,
        ZEPHYR_MAGE
    )

    fun findById(id: String): NPC? = ALL_NPCS.find { it.id == id }
}
