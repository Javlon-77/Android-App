package com.example.model

enum class BiomeType(
    val displayName: String,
    val description: String,
    val primaryColorArgb: Long,
    val secondaryColorArgb: Long,
    val ambientLightMultiplier: Float
) {
    VERDANT_SHIRE("Verdant Shire", "Lush emerald meadows, ancient oak woods, and gentle running streams", 0xFF2E7D32, 0xFF4CAF50, 1.0f),
    CRYSTAL_CANYON("Crystal Canyon", "Luminescent canyons towering with floating aether crystals", 0xFF0284C7, 0xFF38BDF8, 0.95f),
    WHISPERING_DESERT("Whispering Desert", "Shifting golden sand dunes hiding sunken marble temples", 0xFFD97706, 0xFFFBBF24, 1.1f),
    SHADOWFELL_CRAGS("Shadowfell Crags", "Volcanic obsidian peaks glowing with rivers of liquid fire", 0xFF450A0A, 0xFF991B1B, 0.75f),
    ENCHANTED_GROVE("Enchanted Grove", "Twilight violet canopies glowing with bioluminescent spores", 0xFF581C87, 0xFF9333EA, 0.85f),
    SKY_CITADEL("Sky Citadel", "Grand floating white stone metropolis with central fountains and trade plazas", 0xFF1E293B, 0xFF64748B, 1.05f)
}

enum class BlockType(
    val displayName: String,
    val iconEmoji: String,
    val maxDurability: Int,
    val isSolid: Boolean,
    val lightEmission: Float = 0f,
    val lightColorArgb: Long = 0xFFFFEE88
) {
    AIR("Empty Space", "⬜", 0, false),
    WOOD_WALL("Wood Plank", "🪵", 50, true),
    STONE_BRICK("Stone Brick", "🧱", 120, true),
    GLASS("Crystal Glass", "🪟", 30, true),
    TORCH("Torch", "🔥", 20, false, lightEmission = 3.5f, lightColorArgb = 0xFFFF9800),
    LANTERN("Arcane Lantern", "🏮", 40, false, lightEmission = 5.0f, lightColorArgb = 0xFF00E5FF),
    CHEST("Chest", "📦", 80, true),
    TELEPORTER("Teleporter", "🌀", 150, false, lightEmission = 4.0f, lightColorArgb = 0xFFA855F7),
    TURRET("Defense Turret", "⚡", 200, true, lightEmission = 2.5f, lightColorArgb = 0xFF38BDF8),
    WORKBENCH("Workbench", "🛠️", 60, true),
    FORGE("Blacksmith Forge", "🔥", 100, true, lightEmission = 3.0f, lightColorArgb = 0xFFEF4444),
    ALCHEMY_TABLE("Alchemy Table", "🧪", 60, true, lightEmission = 2.0f, lightColorArgb = 0xFF10B981),
    MODDED_BLOCK("Custom Block", "✨", 100, true)
}

data class PlacedBlock(
    val id: String,
    val x: Int,
    val y: Int,
    val type: BlockType,
    val customColorArgb: Long? = null,
    var durability: Int = type.maxDurability,
    val placedBy: String = "Player",
    val lightRadius: Float = type.lightEmission,
    val customModId: String? = null
)

enum class ResourceNodeType(
    val displayName: String,
    val iconEmoji: String,
    val maxHealth: Int,
    val requiredToolTier: Int
) {
    OAK_TREE("Ancient Oak", "🌳", 30, 0),
    STONE_ROCK("Granite Boulder", "🪨", 45, 1),
    IRON_VEIN("Iron Ore Deposit", "⛏️", 60, 2),
    CRYSTAL_NODE("Aether Geode", "💎", 80, 2),
    MANA_FLOWER("Mana Bloom Patch", "🌸", 20, 0),
    OBSIDIAN_SPIRE("Obsidian Spire", "🖤", 120, 3),
    GOLD_VEIN("Auric Gold Vein", "🪙", 90, 2)
}

data class ResourceNode(
    val id: String,
    val x: Float,
    val y: Float,
    val type: ResourceNodeType,
    var health: Int,
    val maxHealth: Int,
    val droppedItem: Item,
    val dropMin: Int = 1,
    val dropMax: Int = 3
)

data class DroppedItem(
    val id: String,
    var x: Float,
    var y: Float,
    val item: Item,
    val count: Int,
    val spawnTime: Long = System.currentTimeMillis()
)

enum class ParticleType {
    EMBER, SPARK, GLOW_SPORE, DUST, MAGIC_RUNE, WATER_DROP
}

data class Particle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val colorArgb: Long,
    var size: Float,
    var life: Float,
    val maxLife: Float,
    val type: ParticleType
)

data class LightSource(
    val x: Float,
    val y: Float,
    val radius: Float,
    val intensity: Float,
    val colorArgb: Long
)

data class WorldTile(
    val x: Int,
    val y: Int,
    val biome: BiomeType,
    val elevation: Float,
    val isWalkable: Boolean,
    val tileColorArgb: Long
)
