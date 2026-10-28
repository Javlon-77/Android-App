package com.example.game

import com.example.model.*
import kotlin.math.sqrt

object WorldGenerator {
    const val WORLD_SIZE = 128

    fun generateWorld(): Pair<Array<Array<WorldTile>>, MutableList<ResourceNode>> {
        val tiles = Array(WORLD_SIZE) { x ->
            Array(WORLD_SIZE) { y ->
                val biome = determineBiome(x, y)
                val isWalkable = true
                val tileColor = getBiomeGroundColor(biome, x, y)
                WorldTile(x, y, biome, 1.0f, isWalkable, tileColor)
            }
        }

        val resourceNodes = mutableListOf<ResourceNode>()
        var nodeCounter = 0

        // Populate diverse resource nodes across biomes
        for (x in 4 until WORLD_SIZE - 4 step 3) {
            for (y in 4 until WORLD_SIZE - 4 step 3) {
                // Pseudo-random noise placement
                val hash = ((x * 73856093) xor (y * 19349663)) and 0x7FFFFFFF
                val roll = hash % 100

                // Keep spawn clearing at (64, 64)
                val distFromSpawn = sqrt(((x - 64) * (x - 64) + (y - 64) * (y - 64)).toFloat())
                if (distFromSpawn < 4.0f) continue

                val biome = tiles[x][y].biome

                when (biome) {
                    BiomeType.VERDANT_SHIRE -> {
                        if (roll < 35) {
                            resourceNodes.add(
                                ResourceNode("node_${nodeCounter++}", x.toFloat(), y.toFloat(), ResourceNodeType.OAK_TREE, 30, 30, ItemCatalog.WOOD, 2, 4)
                            )
                        } else if (roll < 55) {
                            resourceNodes.add(
                                ResourceNode("node_${nodeCounter++}", x.toFloat(), y.toFloat(), ResourceNodeType.STONE_ROCK, 45, 45, ItemCatalog.STONE, 1, 3)
                            )
                        }
                    }
                    BiomeType.CRYSTAL_CANYON -> {
                        if (roll < 40) {
                            resourceNodes.add(
                                ResourceNode("node_${nodeCounter++}", x.toFloat(), y.toFloat(), ResourceNodeType.CRYSTAL_NODE, 80, 80, ItemCatalog.AETHER_CRYSTAL, 1, 3)
                            )
                        } else if (roll < 60) {
                            resourceNodes.add(
                                ResourceNode("node_${nodeCounter++}", x.toFloat(), y.toFloat(), ResourceNodeType.IRON_VEIN, 60, 60, ItemCatalog.IRON_ORE, 1, 2)
                            )
                        }
                    }
                    BiomeType.WHISPERING_DESERT -> {
                        if (roll < 35) {
                            resourceNodes.add(
                                ResourceNode("node_${nodeCounter++}", x.toFloat(), y.toFloat(), ResourceNodeType.STONE_ROCK, 45, 45, ItemCatalog.STONE, 2, 4)
                            )
                        } else if (roll < 55) {
                            resourceNodes.add(
                                ResourceNode("node_${nodeCounter++}", x.toFloat(), y.toFloat(), ResourceNodeType.GOLD_VEIN, 90, 90, ItemCatalog.GOLD_NUGGET, 1, 2)
                            )
                        }
                    }
                    BiomeType.SHADOWFELL_CRAGS -> {
                        if (roll < 35) {
                            resourceNodes.add(
                                ResourceNode("node_${nodeCounter++}", x.toFloat(), y.toFloat(), ResourceNodeType.OBSIDIAN_SPIRE, 120, 120, ItemCatalog.OBSIDIAN_SHARD, 1, 3)
                            )
                        } else if (roll < 60) {
                            resourceNodes.add(
                                ResourceNode("node_${nodeCounter++}", x.toFloat(), y.toFloat(), ResourceNodeType.IRON_VEIN, 60, 60, ItemCatalog.IRON_ORE, 2, 3)
                            )
                        }
                    }
                    BiomeType.ENCHANTED_GROVE -> {
                        if (roll < 35) {
                            resourceNodes.add(
                                ResourceNode("node_${nodeCounter++}", x.toFloat(), y.toFloat(), ResourceNodeType.MANA_FLOWER, 20, 20, ItemCatalog.MANA_BLOOM, 1, 3)
                            )
                        } else if (roll < 60) {
                            resourceNodes.add(
                                ResourceNode("node_${nodeCounter++}", x.toFloat(), y.toFloat(), ResourceNodeType.OAK_TREE, 30, 30, ItemCatalog.WOOD, 2, 4)
                            )
                        }
                    }
                    BiomeType.SKY_CITADEL -> {
                        if (roll < 15) {
                            resourceNodes.add(
                                ResourceNode("node_${nodeCounter++}", x.toFloat(), y.toFloat(), ResourceNodeType.STONE_ROCK, 45, 45, ItemCatalog.STONE, 2, 4)
                            )
                        }
                    }
                }
            }
        }

        return Pair(tiles, resourceNodes)
    }

    private fun determineBiome(x: Int, y: Int): BiomeType {
        // Sky Citadel at North Center
        if (x in 55..85 && y in 18..40) {
            return BiomeType.SKY_CITADEL
        }
        // Center starting area is Verdant Shire
        if (x in 45..85 && y in 45..85) {
            return BiomeType.VERDANT_SHIRE
        }
        // North-West: Crystal Canyon
        if (x < 55 && y < 55) {
            return BiomeType.CRYSTAL_CANYON
        }
        // North-East: Whispering Desert
        if (x >= 75 && y < 60) {
            return BiomeType.WHISPERING_DESERT
        }
        // South-West: Enchanted Grove
        if (x < 60 && y >= 65) {
            return BiomeType.ENCHANTED_GROVE
        }
        // South-East: Shadowfell Crags
        return BiomeType.SHADOWFELL_CRAGS
    }

    private fun getBiomeGroundColor(biome: BiomeType, x: Int, y: Int): Long {
        val pattern = (x * 3 + y * 7) % 5
        return when (biome) {
            BiomeType.VERDANT_SHIRE -> {
                when (pattern) {
                    0 -> 0xFF2E7D32
                    1 -> 0xFF388E3C
                    2 -> 0xFF256628
                    else -> 0xFF2B7530
                }
            }
            BiomeType.CRYSTAL_CANYON -> {
                when (pattern) {
                    0 -> 0xFF0369A1
                    1 -> 0xFF0284C7
                    2 -> 0xFF075985
                    else -> 0xFF0C4A6E
                }
            }
            BiomeType.WHISPERING_DESERT -> {
                when (pattern) {
                    0 -> 0xFFD97706
                    1 -> 0xFFB45309
                    2 -> 0xFFF59E0B
                    else -> 0xFFCA8A04
                }
            }
            BiomeType.SHADOWFELL_CRAGS -> {
                when (pattern) {
                    0 -> 0xFF1C1917
                    1 -> 0xFF292524
                    2 -> 0xFF450A0A
                    else -> 0xFF18181B
                }
            }
            BiomeType.ENCHANTED_GROVE -> {
                when (pattern) {
                    0 -> 0xFF3B0764
                    1 -> 0xFF581C87
                    2 -> 0xFF6B21A8
                    else -> 0xFF4A044E
                }
            }
            BiomeType.SKY_CITADEL -> {
                when (pattern) {
                    0 -> 0xFF334155
                    1 -> 0xFF475569
                    2 -> 0xFF1E293B
                    else -> 0xFF3B4A60
                }
            }
        }
    }

    fun generateInitialStructures(): List<PlacedBlock> {
        val initial = mutableListOf<PlacedBlock>()
        var idCounter = 0

        // Elder Ronald's Sanctuary in Shire at (62, 58)
        initial.add(PlacedBlock("init_${idCounter++}", 61, 58, BlockType.STONE_BRICK))
        initial.add(PlacedBlock("init_${idCounter++}", 62, 58, BlockType.WORKBENCH))
        initial.add(PlacedBlock("init_${idCounter++}", 63, 58, BlockType.STONE_BRICK))
        initial.add(PlacedBlock("init_${idCounter++}", 61, 57, BlockType.TORCH, lightRadius = 4f))
        initial.add(PlacedBlock("init_${idCounter++}", 63, 57, BlockType.TORCH, lightRadius = 4f))
        initial.add(PlacedBlock("init_${idCounter++}", 62, 59, BlockType.CHEST))

        // Sky Citadel Fountain / Waystone at (75, 28)
        initial.add(PlacedBlock("init_${idCounter++}", 75, 28, BlockType.TELEPORTER, lightRadius = 5f))
        initial.add(PlacedBlock("init_${idCounter++}", 74, 28, BlockType.LANTERN, lightRadius = 4.5f))
        initial.add(PlacedBlock("init_${idCounter++}", 76, 28, BlockType.LANTERN, lightRadius = 4.5f))

        // Gorim's Forge in Shadowfell at (95, 93)
        initial.add(PlacedBlock("init_${idCounter++}", 95, 93, BlockType.FORGE, lightRadius = 4f))
        initial.add(PlacedBlock("init_${idCounter++}", 94, 93, BlockType.STONE_BRICK))
        initial.add(PlacedBlock("init_${idCounter++}", 96, 93, BlockType.STONE_BRICK))

        // Aria's Alchemical Lab in Enchanted Grove at (25, 88)
        initial.add(PlacedBlock("init_${idCounter++}", 25, 88, BlockType.ALCHEMY_TABLE, lightRadius = 3.5f))
        initial.add(PlacedBlock("init_${idCounter++}", 26, 88, BlockType.LANTERN, lightRadius = 4f))

        return initial
    }
}
