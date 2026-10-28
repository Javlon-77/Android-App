package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuildPaletteSheet(
    player: PlayerState,
    onSelectBlockItem: (item: Item) -> Unit,
    onDismiss: () -> Unit
) {
    val buildableItems = listOf(
        ItemCatalog.WOOD_WALL,
        ItemCatalog.STONE_BRICK,
        ItemCatalog.GLASS_WINDOW,
        ItemCatalog.TORCH,
        ItemCatalog.LANTERN,
        ItemCatalog.CHEST,
        ItemCatalog.TELEPORTER,
        ItemCatalog.DEFENSE_TURRET
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF161E2E),
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color(0xFF38BDF8)) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .testTag("build_palette_sheet")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "🏗️ Freeform Architecture Palette",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Tap a block to equip it to your primary builder hotbar slot",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8)
                    )
                }
                IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_build_palette_button")) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF94A3B8))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                items(buildableItems) { item ->
                    val count = player.getItemCount(item.id)
                    val hasMaterial = count > 0

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (hasMaterial) Color(0xFF202B40) else Color(0xFF0F172A)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = if (hasMaterial) Color(0xFF0284C7) else Color(0xFF334155),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                onSelectBlockItem(item)
                                onDismiss()
                            }
                            .testTag("build_item_${item.id}")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(item.iconEmoji, fontSize = 32.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = item.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Carried: $count",
                                fontSize = 11.sp,
                                color = if (hasMaterial) Color(0xFF34D399) else Color(0xFFEF4444),
                                fontWeight = FontWeight.SemiBold
                            )
                            if (item.placeableBlockType?.lightEmission ?: 0f > 0f) {
                                Text(
                                    text = "💡 Glow: ${item.placeableBlockType?.lightEmission}m",
                                    fontSize = 10.sp,
                                    color = Color(0xFFFBBF24)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
