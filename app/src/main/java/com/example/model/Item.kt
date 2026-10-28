package com.example.model

enum class ItemCategory {
    TOOL, WEAPON, ARMOR, MATERIAL, CONSUMABLE, BUILDING, MODDED
}

enum class ItemRarity {
    COMMON, UNCOMMON, RARE, EPIC, LEGENDARY
}

data class Item(
    val id: String,
    val name: String,
    val description: String,
    val category: ItemCategory,
    val rarity: ItemRarity = ItemRarity.COMMON,
    val iconEmoji: String,
    val baseValue: Int = 5,
    val attackPower: Int = 0,
    val defense: Int = 0,
    val harvestSpeed: Float = 1.0f,
    val healAmount: Int = 0,
    val staminaAmount: Int = 0,
    val placeableBlockType: BlockType? = null
)

data class ItemStack(
    val item: Item,
    var count: Int
)

enum class CraftingStation {
    HAND, WORKBENCH, FORGE, ALCHEMY_LAB
}

data class CraftingRecipe(
    val id: String,
    val name: String,
    val station: CraftingStation,
    val inputs: List<ItemStack>,
    val output: ItemStack,
    val category: String,
    val description: String = ""
)

object ItemCatalog {
    // Basic Materials
    val WOOD = Item("mat_wood", "Oak Timber", "Sturdy timber harvested from ancient trees", ItemCategory.MATERIAL, ItemRarity.COMMON, "🪵", 2)
    val STONE = Item("mat_stone", "Granite Stone", "Solid stone quarried from surface boulders", ItemCategory.MATERIAL, ItemRarity.COMMON, "🪨", 2)
    val IRON_ORE = Item("mat_iron_ore", "Iron Ore", "Raw iron ore found deep in crystal caverns", ItemCategory.MATERIAL, ItemRarity.UNCOMMON, "⛏️", 6)
    val IRON_INGOT = Item("mat_iron_ingot", "Refined Iron Ingot", "Smelted high-grade metal for forging", ItemCategory.MATERIAL, ItemRarity.UNCOMMON, "🔩", 12)
    val AETHER_CRYSTAL = Item("mat_crystal", "Aether Crystal", "Glowing blue crystal pulsating with arcane energy", ItemCategory.MATERIAL, ItemRarity.RARE, "💎", 25)
    val MANA_BLOOM = Item("mat_bloom", "Mana Bloom", "Rare luminescent blossom used in alchemical brews", ItemCategory.MATERIAL, ItemRarity.UNCOMMON, "🌸", 8)
    val OBSIDIAN_SHARD = Item("mat_obsidian", "Shadow Obsidian", "Volcanic glass forged in high heat", ItemCategory.MATERIAL, ItemRarity.RARE, "🖤", 20)
    val GOLD_NUGGET = Item("mat_gold_nugget", "Gold Nugget", "Pristine gold discovered in mountain veins", ItemCategory.MATERIAL, ItemRarity.RARE, "🪙", 30)

    // Tools
    val WOOD_PICKAXE = Item("tool_wood_pick", "Wooden Pickaxe", "Basic harvesting tool for soft rocks", ItemCategory.TOOL, ItemRarity.COMMON, "🪓", 8, harvestSpeed = 1.2f)
    val STONE_PICKAXE = Item("tool_stone_pick", "Stone Pickaxe", "Reliable pickaxe capable of mining iron ore", ItemCategory.TOOL, ItemRarity.UNCOMMON, "⛏️", 20, harvestSpeed = 1.8f)
    val IRON_PICKAXE = Item("tool_iron_pick", "Iron Pickaxe", "Heavy iron pickaxe for fast excavation", ItemCategory.TOOL, ItemRarity.RARE, "⛏️", 50, harvestSpeed = 2.6f)
    val ASTRAL_PICKAXE = Item("tool_astral_pick", "Astral Pickaxe", "Empowered with aether crystals; instant mining", ItemCategory.TOOL, ItemRarity.LEGENDARY, "✨", 200, harvestSpeed = 4.5f)

    // Weapons
    val WOOD_SWORD = Item("wep_wood_sword", "Training Blade", "Basic wooden sword for self-defense", ItemCategory.WEAPON, ItemRarity.COMMON, "🗡️", 10, attackPower = 8)
    val IRON_LONGSWORD = Item("wep_iron_sword", "Iron Broadsword", "Sharpened blade crafted by city blacksmiths", ItemCategory.WEAPON, ItemRarity.UNCOMMON, "⚔️", 45, attackPower = 20)
    val CRYSTAL_BLADE = Item("wep_crystal_blade", "Aether Edge", "Channels crystalline pulses of pure energy", ItemCategory.WEAPON, ItemRarity.RARE, "💠", 120, attackPower = 38)
    val SUN_BOW = Item("wep_sun_bow", "Solar Longbow", "Fires searing arrows of concentrated dawn light", ItemCategory.WEAPON, ItemRarity.EPIC, "🏹", 180, attackPower = 52)
    val VOID_STAFF = Item("wep_void_staff", "Staff of the Shadowfell", "Unleashes cosmic shockwaves that obliterate foes", ItemCategory.WEAPON, ItemRarity.LEGENDARY, "🔮", 350, attackPower = 80)

    // Armor
    val LEATHER_TUNIC = Item("arm_leather", "Leather Brigandine", "Supple armor offering nimble protection", ItemCategory.ARMOR, ItemRarity.COMMON, "🥋", 25, defense = 6)
    val IRON_CHESTPLATE = Item("arm_iron", "Knight's Iron Cuirass", "Heavy forged plate absorbing heavy blows", ItemCategory.ARMOR, ItemRarity.UNCOMMON, "🛡️", 80, defense = 18)
    val OBSIDIAN_AEGIS = Item("arm_obsidian", "Obsidian Warplate", "Imbued with dragonfire resistance", ItemCategory.ARMOR, ItemRarity.EPIC, "🦺", 240, defense = 36)

    // Consumables
    val HEALTH_POTION = Item("con_health", "Vitality Draught", "Restores 40 Health immediately", ItemCategory.CONSUMABLE, ItemRarity.COMMON, "🧪", 15, healAmount = 40)
    val STAMINA_ELIXIR = Item("con_stamina", "Windstride Tonic", "Restores 50 Stamina and temporarily boosts sprint speed", ItemCategory.CONSUMABLE, ItemRarity.UNCOMMON, "⚡", 18, staminaAmount = 50)
    val AETHER_NECTAR = Item("con_nectar", "Aether Nectar", "Fully restores Health and Stamina with glowing aura", ItemCategory.CONSUMABLE, ItemRarity.RARE, "🍷", 60, healAmount = 100, staminaAmount = 100)

    // Building Blocks
    val WOOD_WALL = Item("bld_wood_wall", "Wood Planks", "Solid wooden wall and floor building material", ItemCategory.BUILDING, ItemRarity.COMMON, "🧱", 3, placeableBlockType = BlockType.WOOD_WALL)
    val STONE_BRICK = Item("bld_stone_brick", "Stone Bricks", "Fortified stone blocks for castle walls and towers", ItemCategory.BUILDING, ItemRarity.COMMON, "🧱", 5, placeableBlockType = BlockType.STONE_BRICK)
    val GLASS_WINDOW = Item("bld_glass", "Crystal Glass Pane", "Transparent crystalline window block", ItemCategory.BUILDING, ItemRarity.UNCOMMON, "🪟", 8, placeableBlockType = BlockType.GLASS)
    val TORCH = Item("bld_torch", "Luminous Torch", "Illuminates dark caves and structures with warm light", ItemCategory.BUILDING, ItemRarity.COMMON, "🔥", 4, placeableBlockType = BlockType.TORCH)
    val LANTERN = Item("bld_lantern", "Arcane Lantern", "High-intensity cyan point light source", ItemCategory.BUILDING, ItemRarity.UNCOMMON, "🏮", 15, placeableBlockType = BlockType.LANTERN)
    val CHEST = Item("bld_chest", "Storage Chest", "Safely stores up to 30 stacks of inventory items", ItemCategory.BUILDING, ItemRarity.UNCOMMON, "📦", 20, placeableBlockType = BlockType.CHEST)
    val TELEPORTER = Item("bld_teleporter", "Waystone Teleporter", "Fast travel portal linking your builds across the map", ItemCategory.BUILDING, ItemRarity.RARE, "🌀", 150, placeableBlockType = BlockType.TELEPORTER)
    val DEFENSE_TURRET = Item("bld_turret", "Aether Crystal Sentry", "Automated defense crystal that wards off hostile monsters", ItemCategory.BUILDING, ItemRarity.EPIC, "⚡", 250, placeableBlockType = BlockType.TURRET)

    val ALL_ITEMS = listOf(
        WOOD, STONE, IRON_ORE, IRON_INGOT, AETHER_CRYSTAL, MANA_BLOOM, OBSIDIAN_SHARD, GOLD_NUGGET,
        WOOD_PICKAXE, STONE_PICKAXE, IRON_PICKAXE, ASTRAL_PICKAXE,
        WOOD_SWORD, IRON_LONGSWORD, CRYSTAL_BLADE, SUN_BOW, VOID_STAFF,
        LEATHER_TUNIC, IRON_CHESTPLATE, OBSIDIAN_AEGIS,
        HEALTH_POTION, STAMINA_ELIXIR, AETHER_NECTAR,
        WOOD_WALL, STONE_BRICK, GLASS_WINDOW, TORCH, LANTERN, CHEST, TELEPORTER, DEFENSE_TURRET
    )

    val RECIPES = listOf(
        // Hand crafting
        CraftingRecipe("rcp_wood_pick", "Wooden Pickaxe", CraftingStation.HAND, listOf(ItemStack(WOOD, 4)), ItemStack(WOOD_PICKAXE, 1), "Tools", "A quick tool carved from branches"),
        CraftingRecipe("rcp_wood_sword", "Training Blade", CraftingStation.HAND, listOf(ItemStack(WOOD, 5)), ItemStack(WOOD_SWORD, 1), "Weapons", "Simple wooden blade"),
        CraftingRecipe("rcp_torch", "Luminous Torch x4", CraftingStation.HAND, listOf(ItemStack(WOOD, 1), ItemStack(STONE, 1)), ItemStack(TORCH, 4), "Building", "Provides bright illumination"),
        CraftingRecipe("rcp_wood_wall", "Wood Planks x4", CraftingStation.HAND, listOf(ItemStack(WOOD, 2)), ItemStack(WOOD_WALL, 4), "Building", "Basic building material"),

        // Workbench crafting
        CraftingRecipe("rcp_stone_pick", "Stone Pickaxe", CraftingStation.WORKBENCH, listOf(ItemStack(WOOD, 3), ItemStack(STONE, 6)), ItemStack(STONE_PICKAXE, 1), "Tools", "Durable mining tool"),
        CraftingRecipe("rcp_stone_brick", "Stone Bricks x4", CraftingStation.WORKBENCH, listOf(ItemStack(STONE, 4)), ItemStack(STONE_BRICK, 4), "Building", "Durable masonry"),
        CraftingRecipe("rcp_glass", "Crystal Glass x2", CraftingStation.WORKBENCH, listOf(ItemStack(STONE, 2), ItemStack(AETHER_CRYSTAL, 1)), ItemStack(GLASS_WINDOW, 2), "Building", "Clear windows"),
        CraftingRecipe("rcp_chest", "Storage Chest", CraftingStation.WORKBENCH, listOf(ItemStack(WOOD, 10), ItemStack(IRON_INGOT, 1)), ItemStack(CHEST, 1), "Building", "Item vault"),
        CraftingRecipe("rcp_lantern", "Arcane Lantern", CraftingStation.WORKBENCH, listOf(ItemStack(IRON_INGOT, 2), ItemStack(AETHER_CRYSTAL, 1)), ItemStack(LANTERN, 1), "Building", "Bright mana light"),

        // Forge crafting
        CraftingRecipe("rcp_iron_ingot", "Refined Iron Ingot", CraftingStation.FORGE, listOf(ItemStack(IRON_ORE, 2)), ItemStack(IRON_INGOT, 1), "Refining", "Purify ore into metal"),
        CraftingRecipe("rcp_iron_pick", "Iron Pickaxe", CraftingStation.FORGE, listOf(ItemStack(WOOD, 4), ItemStack(IRON_INGOT, 4)), ItemStack(IRON_PICKAXE, 1), "Tools", "Fast mining"),
        CraftingRecipe("rcp_iron_sword", "Iron Broadsword", CraftingStation.FORGE, listOf(ItemStack(WOOD, 2), ItemStack(IRON_INGOT, 5)), ItemStack(IRON_LONGSWORD, 1), "Weapons", "Heavy melee blade"),
        CraftingRecipe("rcp_iron_armor", "Knight's Iron Cuirass", CraftingStation.FORGE, listOf(ItemStack(IRON_INGOT, 8), ItemStack(LEATHER_TUNIC, 1)), ItemStack(IRON_CHESTPLATE, 1), "Armor", "Solid metal armor"),
        CraftingRecipe("rcp_crystal_blade", "Aether Edge", CraftingStation.FORGE, listOf(ItemStack(IRON_INGOT, 5), ItemStack(AETHER_CRYSTAL, 4)), ItemStack(CRYSTAL_BLADE, 1), "Weapons", "Energy weapon"),
        CraftingRecipe("rcp_astral_pick", "Astral Pickaxe", CraftingStation.FORGE, listOf(ItemStack(IRON_PICKAXE, 1), ItemStack(AETHER_CRYSTAL, 8), ItemStack(OBSIDIAN_SHARD, 4)), ItemStack(ASTRAL_PICKAXE, 1), "Tools", "Supreme mining tool"),
        CraftingRecipe("rcp_obsidian_armor", "Obsidian Warplate", CraftingStation.FORGE, listOf(ItemStack(OBSIDIAN_SHARD, 12), ItemStack(IRON_CHESTPLATE, 1)), ItemStack(OBSIDIAN_AEGIS, 1), "Armor", "Elite volcanic armor"),

        // Alchemy Lab
        CraftingRecipe("rcp_health_pot", "Vitality Draught x2", CraftingStation.ALCHEMY_LAB, listOf(ItemStack(MANA_BLOOM, 2)), ItemStack(HEALTH_POTION, 2), "Potions", "Instant health brew"),
        CraftingRecipe("rcp_stamina_pot", "Windstride Tonic x2", CraftingStation.ALCHEMY_LAB, listOf(ItemStack(MANA_BLOOM, 1), ItemStack(AETHER_CRYSTAL, 1)), ItemStack(STAMINA_ELIXIR, 2), "Potions", "Stamina and agility brew"),
        CraftingRecipe("rcp_aether_nectar", "Aether Nectar", CraftingStation.ALCHEMY_LAB, listOf(ItemStack(MANA_BLOOM, 3), ItemStack(AETHER_CRYSTAL, 3), ItemStack(GOLD_NUGGET, 1)), ItemStack(AETHER_NECTAR, 1), "Potions", "Full restoration elixir"),
        CraftingRecipe("rcp_teleporter", "Waystone Teleporter", CraftingStation.ALCHEMY_LAB, listOf(ItemStack(STONE_BRICK, 10), ItemStack(AETHER_CRYSTAL, 6), ItemStack(GOLD_NUGGET, 2)), ItemStack(TELEPORTER, 1), "Tech", "Fast travel gateway"),
        CraftingRecipe("rcp_turret", "Aether Crystal Sentry", CraftingStation.ALCHEMY_LAB, listOf(ItemStack(IRON_INGOT, 6), ItemStack(AETHER_CRYSTAL, 5), ItemStack(OBSIDIAN_SHARD, 3)), ItemStack(DEFENSE_TURRET, 1), "Tech", "Automated base protection")
    )

    fun findItemById(id: String): Item? {
        return ALL_ITEMS.find { it.id == id }
    }
}
