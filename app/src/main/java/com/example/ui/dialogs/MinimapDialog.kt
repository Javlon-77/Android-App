package com.example.ui.dialogs

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.game.WorldGenerator
import com.example.model.NPC
import com.example.model.PlayerState

@Composable
fun MinimapDialog(
    player: PlayerState,
    npcs: List<NPC>,
    onTeleport: (x: Float, y: Float) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF161E2E)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .border(1.dp, Color(0xFF38BDF8), RoundedCornerShape(16.dp))
                .testTag("minimap_dialog")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Map,
                            contentDescription = "Map",
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Realm Cartography",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_map_button")) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF94A3B8))
                    }
                }

                Text(
                    text = "Player: (${player.x.toInt()}, ${player.y.toInt()}) • Biome: ${player.currentBiome.displayName}",
                    fontSize = 11.sp,
                    color = Color(0xFF38BDF8)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Tactical World Map Canvas
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                        .background(Color(0xFF0F172A), RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val mapW = size.width
                        val mapH = size.height

                        fun worldToMap(wx: Float, wy: Float): Offset {
                            val mx = (wx / WorldGenerator.WORLD_SIZE.toFloat()) * mapW
                            val my = (wy / WorldGenerator.WORLD_SIZE.toFloat()) * mapH
                            return Offset(mx, my)
                        }

                        // Draw Biome zones
                        // North Sky Citadel
                        drawRect(Color(0xFF334155), Offset(mapW * 0.42f, mapH * 0.14f), Size(mapW * 0.24f, mapH * 0.18f))
                        // NW Crystal Canyon
                        drawRect(Color(0xFF0369A1), Offset(0f, 0f), Size(mapW * 0.42f, mapH * 0.42f))
                        // NE Whispering Desert
                        drawRect(Color(0xFFD97706), Offset(mapW * 0.60f, 0f), Size(mapW * 0.40f, mapH * 0.46f))
                        // Center Verdant Shire
                        drawRect(Color(0xFF2E7D32), Offset(mapW * 0.35f, mapH * 0.35f), Size(mapW * 0.32f, mapH * 0.32f))
                        // SW Enchanted Grove
                        drawRect(Color(0xFF581C87), Offset(0f, mapH * 0.50f), Size(mapW * 0.46f, mapH * 0.50f))
                        // SE Shadowfell Crags
                        drawRect(Color(0xFF450A0A), Offset(mapW * 0.60f, mapH * 0.58f), Size(mapW * 0.40f, mapH * 0.42f))

                        // Draw NPC dots
                        for (npc in npcs) {
                            val nPos = worldToMap(npc.x, npc.y)
                            drawCircle(Color(0xFFFFD700), radius = 6f, center = nPos)
                            drawCircle(Color.White, radius = 3f, center = nPos)
                        }

                        // Draw Player Crosshair
                        val pPos = worldToMap(player.x, player.y)
                        drawCircle(Color(0xFF00E5FF), radius = 8f, center = pPos)
                        drawCircle(Color.White, radius = 4f, center = pPos)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Waystone Fast Travel Portals:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Fast Travel Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Button(
                        onClick = {
                            onTeleport(64f, 64f)
                            onDismiss()
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Shire", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            onTeleport(75f, 30f)
                            onDismiss()
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Citadel", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            onTeleport(25f, 90f)
                            onDismiss()
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF581C87)),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Grove", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            onTeleport(95f, 95f)
                            onDismiss()
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF991B1B)),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Forge", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
