package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import com.example.game.GameEngine
import com.example.game.WorldGenerator
import com.example.model.*
import kotlin.math.*

@Composable
fun GameCanvas(
    gameEngine: GameEngine,
    player: PlayerState,
    placedBlocks: List<PlacedBlock>,
    resourceNodes: List<ResourceNode>,
    droppedItems: List<DroppedItem>,
    particles: List<Particle>,
    npcs: List<NPC>,
    multiplayer: MultiplayerState,
    modifier: Modifier = Modifier
) {
    val ambient = gameEngine.calculateAmbientLight()
    val ambientColor = Color(ambient.first)
    val darknessAlpha = ambient.second

    Canvas(modifier = modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val centerX = canvasWidth / 2f
        val centerY = canvasHeight / 2f

        val tileSize = (canvasWidth / 9.5f).coerceIn(48f, 96f)

        // Viewport bounds in world coordinates
        val minX = (player.x - (centerX / tileSize) - 2).toInt().coerceIn(0, WorldGenerator.WORLD_SIZE - 1)
        val maxX = (player.x + (centerX / tileSize) + 2).toInt().coerceIn(0, WorldGenerator.WORLD_SIZE - 1)
        val minY = (player.y - (centerY / tileSize) - 2).toInt().coerceIn(0, WorldGenerator.WORLD_SIZE - 1)
        val maxY = (player.y + (centerY / tileSize) + 2).toInt().coerceIn(0, WorldGenerator.WORLD_SIZE - 1)

        // Helper to convert world coordinate to screen offset
        fun worldToScreen(wx: Float, wy: Float): Offset {
            val sx = centerX + (wx - player.x) * tileSize
            val sy = centerY + (wy - player.y) * tileSize
            return Offset(sx, sy)
        }

        // 1. Draw Ground Terrain Tiles
        for (x in minX..maxX) {
            for (y in minY..maxY) {
                val tile = gameEngine.worldTiles[x][y]
                val screenPos = worldToScreen(x.toFloat(), y.toFloat())

                drawRect(
                    color = Color(tile.tileColorArgb),
                    topLeft = screenPos,
                    size = Size(tileSize + 1f, tileSize + 1f)
                )

                // Subtle grid lines
                drawRect(
                    color = Color.Black.copy(alpha = 0.06f),
                    topLeft = screenPos,
                    size = Size(tileSize + 1f, tileSize + 1f),
                    style = Stroke(width = 1f)
                )
            }
        }

        // 2. Draw Placed Structures & Blocks
        for (block in placedBlocks) {
            if (block.x in (minX - 1)..(maxX + 1) && block.y in (minY - 1)..(maxY + 1)) {
                val screenPos = worldToScreen(block.x.toFloat(), block.y.toFloat())
                drawPlacedBlock(block, screenPos, tileSize)
            }
        }

        // 3. Draw Resource Nodes
        for (node in resourceNodes) {
            if (node.x in (minX - 1f)..(maxX + 1f) && node.y in (minY - 1f)..(maxY + 1f)) {
                val screenPos = worldToScreen(node.x, node.y)
                drawResourceNode(node, screenPos, tileSize)
            }
        }

        // 4. Draw Dropped Items
        val timeNow = System.currentTimeMillis()
        for (drop in droppedItems) {
            if (drop.x in minX.toFloat()..maxX.toFloat() && drop.y in minY.toFloat()..maxY.toFloat()) {
                val screenPos = worldToScreen(drop.x, drop.y)
                val bob = sin((timeNow - drop.spawnTime) / 250.0).toFloat() * 4f
                drawCircle(
                    color = Color(0x66FFD700),
                    radius = tileSize * 0.35f,
                    center = screenPos + Offset(0f, bob)
                )
                drawCircle(
                    color = Color(0xFF00E5FF),
                    radius = tileSize * 0.2f,
                    center = screenPos + Offset(0f, bob)
                )
            }
        }

        // 5. Draw Dynamic NPCs
        for (npc in npcs) {
            if (npc.x in minX.toFloat()..maxX.toFloat() && npc.y in minY.toFloat()..maxY.toFloat()) {
                val screenPos = worldToScreen(npc.x, npc.y)
                drawNPC(npc, screenPos, tileSize)
            }
        }

        // 6. Draw Multiplayer Co-op Peers
        for (peer in multiplayer.peers) {
            if (peer.x in minX.toFloat()..maxX.toFloat() && peer.y in minY.toFloat()..maxY.toFloat()) {
                val screenPos = worldToScreen(peer.x, peer.y)
                drawNetworkPeer(peer, screenPos, tileSize)
            }
        }

        // 7. Draw Player Avatar (At Center)
        drawPlayerAvatar(player, Offset(centerX, centerY), tileSize)

        // 8. Draw Advanced Lighting & Day/Night Overlay
        if (darknessAlpha > 0.05f) {
            // Draw dark vignette overlay with radial cutouts around light sources
            val lightSources = gameEngine.collectLightSources(player.x.toInt(), player.y.toInt(), 12)

            // Draw full darkness overlay first
            drawRect(
                color = ambientColor.copy(alpha = darknessAlpha),
                size = size
            )

            // Draw glowing radial illumination cutouts for each light source
            for (light in lightSources) {
                val lScreen = worldToScreen(light.x, light.y)
                val lightRadiusPx = light.radius * tileSize

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(light.colorArgb).copy(alpha = 0.55f * light.intensity),
                            Color(light.colorArgb).copy(alpha = 0.25f * light.intensity),
                            Color.Transparent
                        ),
                        center = lScreen,
                        radius = lightRadiusPx
                    ),
                    radius = lightRadiusPx,
                    center = lScreen,
                    blendMode = BlendMode.Screen
                )
            }
        }

        // 9. Draw Dynamic Particles
        for (p in particles) {
            val screenPos = worldToScreen(p.x, p.y)
            val alpha = (p.life / p.maxLife).coerceIn(0f, 1f)
            drawCircle(
                color = Color(p.colorArgb).copy(alpha = alpha),
                radius = p.size,
                center = screenPos
            )
        }
    }
}

private fun DrawScope.drawPlacedBlock(block: PlacedBlock, pos: Offset, tileSize: Float) {
    val blockColor = when (block.type) {
        BlockType.WOOD_WALL -> Color(0xFF8D6E63)
        BlockType.STONE_BRICK -> Color(0xFF78909C)
        BlockType.GLASS -> Color(0xAA80DEEA)
        BlockType.TORCH -> Color(0xFFFF9800)
        BlockType.LANTERN -> Color(0xFF00E5FF)
        BlockType.CHEST -> Color(0xFFD4A373)
        BlockType.TELEPORTER -> Color(0xFFA855F7)
        BlockType.TURRET -> Color(0xFF38BDF8)
        BlockType.WORKBENCH -> Color(0xFFBCAAA4)
        BlockType.FORGE -> Color(0xFFE53935)
        BlockType.ALCHEMY_TABLE -> Color(0xFF10B981)
        BlockType.MODDED_BLOCK -> Color(block.customColorArgb ?: 0xFF00FFCC)
        else -> Color.Gray
    }

    // Shadow
    drawRoundRect(
        color = Color.Black.copy(alpha = 0.25f),
        topLeft = pos + Offset(3f, 4f),
        size = Size(tileSize, tileSize),
        cornerRadius = CornerRadius(6f, 6f)
    )

    // Base block
    drawRoundRect(
        color = blockColor,
        topLeft = pos,
        size = Size(tileSize, tileSize),
        cornerRadius = CornerRadius(6f, 6f)
    )

    // 3D edge highlight
    drawRoundRect(
        color = Color.White.copy(alpha = 0.3f),
        topLeft = pos,
        size = Size(tileSize, tileSize * 0.25f),
        cornerRadius = CornerRadius(6f, 6f)
    )

    // Border
    drawRoundRect(
        color = Color.Black.copy(alpha = 0.4f),
        topLeft = pos,
        size = Size(tileSize, tileSize),
        cornerRadius = CornerRadius(6f, 6f),
        style = Stroke(width = 2f)
    )

    // Emitter center glow if it's a light source
    if (block.type == BlockType.TORCH || block.type == BlockType.LANTERN || block.type == BlockType.TELEPORTER) {
        drawCircle(
            color = Color.White.copy(alpha = 0.8f),
            radius = tileSize * 0.2f,
            center = pos + Offset(tileSize / 2f, tileSize / 2f)
        )
    }
}

private fun DrawScope.drawResourceNode(node: ResourceNode, pos: Offset, tileSize: Float) {
    val center = pos + Offset(tileSize / 2f, tileSize / 2f)
    when (node.type) {
        ResourceNodeType.OAK_TREE -> {
            // Tree trunk
            drawRect(
                color = Color(0xFF5D4037),
                topLeft = center + Offset(-tileSize * 0.15f, 0f),
                size = Size(tileSize * 0.3f, tileSize * 0.45f)
            )
            // Foliage layers
            drawCircle(
                color = Color(0xFF1B5E20),
                radius = tileSize * 0.55f,
                center = center + Offset(0f, -tileSize * 0.25f)
            )
            drawCircle(
                color = Color(0xFF2E7D32),
                radius = tileSize * 0.45f,
                center = center + Offset(0f, -tileSize * 0.3f)
            )
        }
        ResourceNodeType.STONE_ROCK -> {
            drawCircle(
                color = Color(0xFF546E7A),
                radius = tileSize * 0.4f,
                center = center
            )
            drawCircle(
                color = Color(0xFF78909C),
                radius = tileSize * 0.28f,
                center = center + Offset(-tileSize * 0.1f, -tileSize * 0.1f)
            )
        }
        ResourceNodeType.IRON_VEIN -> {
            drawCircle(
                color = Color(0xFF455A64),
                radius = tileSize * 0.42f,
                center = center
            )
            // Iron specks
            drawCircle(
                color = Color(0xFFFFB74D),
                radius = tileSize * 0.12f,
                center = center + Offset(tileSize * 0.12f, -tileSize * 0.12f)
            )
            drawCircle(
                color = Color(0xFFFFB74D),
                radius = tileSize * 0.1f,
                center = center + Offset(-tileSize * 0.15f, tileSize * 0.1f)
            )
        }
        ResourceNodeType.CRYSTAL_NODE -> {
            // Glowing cyan crystal cluster
            drawCircle(
                color = Color(0x6600E5FF),
                radius = tileSize * 0.5f,
                center = center
            )
            val path = Path().apply {
                moveTo(center.x, center.y - tileSize * 0.5f)
                lineTo(center.x + tileSize * 0.35f, center.y + tileSize * 0.35f)
                lineTo(center.x - tileSize * 0.35f, center.y + tileSize * 0.35f)
                close()
            }
            drawPath(path, color = Color(0xFF38BDF8))
            drawCircle(color = Color.White, radius = tileSize * 0.1f, center = center)
        }
        ResourceNodeType.MANA_FLOWER -> {
            drawCircle(
                color = Color(0xFF9333EA),
                radius = tileSize * 0.35f,
                center = center
            )
            drawCircle(
                color = Color(0xFFC084FC),
                radius = tileSize * 0.2f,
                center = center
            )
            drawCircle(
                color = Color(0xFFFFE082),
                radius = tileSize * 0.08f,
                center = center
            )
        }
        ResourceNodeType.OBSIDIAN_SPIRE -> {
            drawCircle(
                color = Color(0xFF0F172A),
                radius = tileSize * 0.45f,
                center = center
            )
            drawCircle(
                color = Color(0xFFEF4444),
                radius = tileSize * 0.15f,
                center = center
            )
        }
        ResourceNodeType.GOLD_VEIN -> {
            drawCircle(
                color = Color(0xFF78716C),
                radius = tileSize * 0.42f,
                center = center
            )
            drawCircle(
                color = Color(0xFFFFD700),
                radius = tileSize * 0.18f,
                center = center
            )
        }
    }

    // Health bar if damaged
    if (node.health < node.maxHealth) {
        val barWidth = tileSize * 0.8f
        val barHeight = 5f
        val healthPct = (node.health.toFloat() / node.maxHealth).coerceIn(0f, 1f)
        val barTop = pos.y - 8f

        drawRect(
            color = Color(0x88000000),
            topLeft = Offset(center.x - barWidth / 2f, barTop),
            size = Size(barWidth, barHeight)
        )
        drawRect(
            color = Color(0xFF10B981),
            topLeft = Offset(center.x - barWidth / 2f, barTop),
            size = Size(barWidth * healthPct, barHeight)
        )
    }
}

private fun DrawScope.drawNPC(npc: NPC, pos: Offset, tileSize: Float) {
    val center = pos + Offset(tileSize / 2f, tileSize / 2f)

    // NPC Shadow
    drawCircle(
        color = Color.Black.copy(alpha = 0.3f),
        radius = tileSize * 0.42f,
        center = center + Offset(0f, tileSize * 0.15f)
    )

    // Base body
    drawCircle(
        color = Color(0xFF6366F1),
        radius = tileSize * 0.36f,
        center = center
    )
    drawCircle(
        color = Color(0xFFE0E7FF),
        radius = tileSize * 0.28f,
        center = center
    )

    // Quest indicator marker floating above head
    val markerY = center.y - tileSize * 0.75f
    drawCircle(
        color = Color(0xFFFFD700),
        radius = tileSize * 0.18f,
        center = Offset(center.x, markerY)
    )
    drawCircle(
        color = Color(0xFF1E293B),
        radius = tileSize * 0.08f,
        center = Offset(center.x, markerY)
    )
}

private fun DrawScope.drawNetworkPeer(peer: NetworkPlayer, pos: Offset, tileSize: Float) {
    val center = pos + Offset(tileSize / 2f, tileSize / 2f)

    // Co-op player circle
    drawCircle(
        color = Color(peer.colorArgb).copy(alpha = 0.35f),
        radius = tileSize * 0.45f,
        center = center
    )
    drawCircle(
        color = Color(peer.colorArgb),
        radius = tileSize * 0.32f,
        center = center
    )
    drawCircle(
        color = Color.White,
        radius = tileSize * 0.15f,
        center = center
    )

    // Direction indicator
    val angleRad = peer.facingAngle * Math.PI / 180.0
    val pointer = center + Offset((cos(angleRad) * tileSize * 0.38f).toFloat(), (sin(angleRad) * tileSize * 0.38f).toFloat())
    drawCircle(
        color = Color(0xFFFFD700),
        radius = tileSize * 0.08f,
        center = pointer
    )
}

private fun DrawScope.drawPlayerAvatar(player: PlayerState, center: Offset, tileSize: Float) {
    // Shadow
    drawCircle(
        color = Color.Black.copy(alpha = 0.35f),
        radius = tileSize * 0.42f,
        center = center + Offset(0f, tileSize * 0.15f)
    )

    // Player body
    drawCircle(
        color = Color(0xFF0284C7),
        radius = tileSize * 0.38f,
        center = center
    )
    drawCircle(
        color = Color(0xFF38BDF8),
        radius = tileSize * 0.30f,
        center = center
    )

    // Equipped weapon or tool accent
    val angleRad = player.facingAngle * Math.PI / 180.0
    val handOffset = Offset((cos(angleRad) * tileSize * 0.42f).toFloat(), (sin(angleRad) * tileSize * 0.42f).toFloat())

    drawCircle(
        color = Color(0xFFFFD700),
        radius = tileSize * 0.14f,
        center = center + handOffset
    )

    // Inner core
    drawCircle(
        color = Color.White,
        radius = tileSize * 0.14f,
        center = center
    )
}
