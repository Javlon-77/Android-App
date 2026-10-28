package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CraftingSheet(
    player: PlayerState,
    onCraft: (recipe: CraftingRecipe) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedStation by remember { mutableStateOf(CraftingStation.HAND) }
    val filteredRecipes = remember(selectedStation) {
        ItemCatalog.RECIPES.filter { it.station == selectedStation }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF161E2E),
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color(0xFF38BDF8)) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .testTag("crafting_sheet")
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = "Crafting",
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Aetheria Crafting Matrix",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_crafting_button")) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF94A3B8))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Station selector chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val stations = listOf(
                    CraftingStation.HAND to "👋 Field Hand",
                    CraftingStation.WORKBENCH to "🛠️ Workbench",
                    CraftingStation.FORGE to "🔥 Magma Forge",
                    CraftingStation.ALCHEMY_LAB to "🧪 Alchemy Lab"
                )
                items(stations) { (station, label) ->
                    val isSelected = selectedStation == station
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedStation = station },
                        label = { Text(label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF0284C7),
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFF202B40),
                            labelColor = Color(0xFF94A3B8)
                        ),
                        modifier = Modifier.testTag("station_chip_${station.name}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Recipes list
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp)
                    .padding(bottom = 16.dp)
            ) {
                items(filteredRecipes) { recipe ->
                    val canCraft = recipe.inputs.all { player.getItemCount(it.item.id) >= it.count }

                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (canCraft) Color(0xFF1E293B) else Color(0xFF0F172A)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = if (canCraft) Color(0xFF38BDF8).copy(alpha = 0.5f) else Color(0xFF334155),
                                shape = RoundedCornerShape(14.dp)
                            )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Output icon
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .background(Color(0xFF202B40), RoundedCornerShape(10.dp))
                                    .border(1.dp, Color(0xFF475569), RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(recipe.output.item.iconEmoji, fontSize = 22.sp)
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            // Recipe details & ingredients
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${recipe.output.item.name} x${recipe.output.count}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "• ${recipe.category}",
                                        fontSize = 11.sp,
                                        color = Color(0xFF38BDF8)
                                    )
                                }

                                if (recipe.output.item.attackPower > 0) {
                                    Text("⚔️ +${recipe.output.item.attackPower} Attack", fontSize = 11.sp, color = Color(0xFFF87171))
                                } else if (recipe.output.item.defense > 0) {
                                    Text("🛡️ +${recipe.output.item.defense} Defense", fontSize = 11.sp, color = Color(0xFF60A5FA))
                                } else if (recipe.output.item.harvestSpeed > 1f) {
                                    Text("⚡ ${recipe.output.item.harvestSpeed}x Mining Speed", fontSize = 11.sp, color = Color(0xFFFBBF24))
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                // Ingredients checklist
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    for (input in recipe.inputs) {
                                        val playerHas = player.getItemCount(input.item.id)
                                        val hasEnough = playerHas >= input.count
                                        Text(
                                            text = "${input.item.iconEmoji} $playerHas/${input.count}",
                                            fontSize = 11.sp,
                                            color = if (hasEnough) Color(0xFF34D399) else Color(0xFFEF4444),
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Craft button
                            Button(
                                onClick = { onCraft(recipe) },
                                enabled = canCraft,
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF0284C7),
                                    disabledContainerColor = Color(0xFF334155)
                                ),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("craft_btn_${recipe.id}")
                            ) {
                                Text(
                                    text = if (canCraft) "Craft" else "Locked",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
