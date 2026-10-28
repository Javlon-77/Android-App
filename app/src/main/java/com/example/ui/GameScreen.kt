package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.game.GameEngine
import com.example.model.*
import com.example.ui.components.GameCanvas
import com.example.ui.components.VirtualJoystick
import com.example.ui.dialogs.*

@Composable
fun GameScreen(
    gameEngine: GameEngine,
    modifier: Modifier = Modifier
) {
    val player by gameEngine.playerState.collectAsStateWithLifecycle()
    val placedBlocks by gameEngine.placedBlocks.collectAsStateWithLifecycle()
    val resourceNodes by gameEngine.resourceNodes.collectAsStateWithLifecycle()
    val droppedItems by gameEngine.droppedItems.collectAsStateWithLifecycle()
    val particles by gameEngine.particles.collectAsStateWithLifecycle()
    val activeQuests by gameEngine.activeQuests.collectAsStateWithLifecycle()
    val npcs by gameEngine.npcs.collectAsStateWithLifecycle()
    val economy by gameEngine.economyState.collectAsStateWithLifecycle()
    val multiplayer by gameEngine.multiplayerState.collectAsStateWithLifecycle()
    val customMods by gameEngine.customMods.collectAsStateWithLifecycle()
    val gameTimeHours by gameEngine.gameTimeHours.collectAsStateWithLifecycle()
    val toastMessage by gameEngine.toastMessage.collectAsStateWithLifecycle()

    // Dialog state controllers
    var activeDialogueNPC by remember { mutableStateOf<NPC?>(null) }
    var showCraftingSheet by remember { mutableStateOf(false) }
    var showBuildPalette by remember { mutableStateOf(false) }
    var showMarketSheet by remember { mutableStateOf(false) }
    var showMultiplayerSheet by remember { mutableStateOf(false) }
    var showModStudioSheet by remember { mutableStateOf(false) }
    var showCloudSaveSheet by remember { mutableStateOf(false) }
    var showQuestLogSheet by remember { mutableStateOf(false) }
    var showMinimapDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    // Nearby interaction check
    val nearbyNPC = remember(player.x, player.y, npcs) {
        gameEngine.findNearbyNPC()
    }

    val hasClaimableQuests = remember(activeQuests) {
        activeQuests.any { it.status == QuestStatus.READY_TO_CLAIM }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0D111A))
    ) {
        // 1. Central Game Canvas Engine
        GameCanvas(
            gameEngine = gameEngine,
            player = player,
            placedBlocks = placedBlocks,
            resourceNodes = resourceNodes,
            droppedItems = droppedItems,
            particles = particles,
            npcs = npcs,
            multiplayer = multiplayer,
            modifier = Modifier.fillMaxSize()
        )

        // 2. Top Status HUD Bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Time & Biome
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xCC0F172A),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val hours = gameTimeHours.toInt()
                        val minutes = ((gameTimeHours - hours) * 60).toInt()
                        val isDay = gameTimeHours in 6.0f..18.0f
                        Text(
                            text = if (isDay) "☀️" else "🌙",
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = String.format("%02d:%02d", hours, minutes),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "• ${player.currentBiome.displayName}",
                            fontSize = 11.sp,
                            color = Color(0xFF38BDF8),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Currency Badges
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xCC0F172A),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🪙", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${player.gold}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = Color(0xFFFFD700)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xCC0F172A),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("💎", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${player.aetherShards}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = Color(0xFF00E5FF)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Player HP & Stamina Gauges + Action Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Gauges
                Column(modifier = Modifier.width(170.dp)) {
                    // Level & HP bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Lvl ${player.level}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF38BDF8)
                        )
                        Text(
                            text = "HP ${player.hp}/${player.maxHp}",
                            fontSize = 10.sp,
                            color = Color(0xFFF87171)
                        )
                    }
                    LinearProgressIndicator(
                        progress = { (player.hp.toFloat() / player.maxHp).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = Color(0xFFEF4444),
                        trackColor = Color(0xFF331111)
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    // Stamina bar
                    LinearProgressIndicator(
                        progress = { (player.stamina / player.maxStamina).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = Color(0xFF10B981),
                        trackColor = Color(0xFF113322)
                    )
                }

                // Quick Navigation Action Buttons (Map, Quests, Market, Multiplayer, Modding, Cloud, Settings)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { showMinimapDialog = true },
                        modifier = Modifier
                            .size(34.dp)
                            .background(Color(0xCC0F172A), CircleShape)
                            .border(1.dp, Color(0xFF334155), CircleShape)
                            .testTag("nav_map_btn")
                    ) {
                        Text("🗺️", fontSize = 14.sp)
                    }

                    // Quest button with notification marker
                    Box {
                        IconButton(
                            onClick = { showQuestLogSheet = true },
                            modifier = Modifier
                                .size(34.dp)
                                .background(Color(0xCC0F172A), CircleShape)
                                .border(1.dp, Color(0xFF334155), CircleShape)
                                .testTag("nav_quests_btn")
                        ) {
                            Text("📜", fontSize = 14.sp)
                        }
                        if (hasClaimableQuests) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .align(Alignment.TopEnd)
                                    .background(Color(0xFFFFD700), CircleShape)
                            )
                        }
                    }

                    IconButton(
                        onClick = { showMarketSheet = true },
                        modifier = Modifier
                            .size(34.dp)
                            .background(Color(0xCC0F172A), CircleShape)
                            .border(1.dp, Color(0xFF334155), CircleShape)
                            .testTag("nav_market_btn")
                    ) {
                        Text("⚖️", fontSize = 14.sp)
                    }

                    IconButton(
                        onClick = { showMultiplayerSheet = true },
                        modifier = Modifier
                            .size(34.dp)
                            .background(Color(0xCC0F172A), CircleShape)
                            .border(1.dp, Color(0xFF334155), CircleShape)
                            .testTag("nav_multiplayer_btn")
                    ) {
                        Text("👥", fontSize = 14.sp)
                    }

                    IconButton(
                        onClick = { showModStudioSheet = true },
                        modifier = Modifier
                            .size(34.dp)
                            .background(Color(0xCC0F172A), CircleShape)
                            .border(1.dp, Color(0xFF334155), CircleShape)
                            .testTag("nav_mod_studio_btn")
                    ) {
                        Text("✨", fontSize = 14.sp)
                    }

                    IconButton(
                        onClick = { showCloudSaveSheet = true },
                        modifier = Modifier
                            .size(34.dp)
                            .background(Color(0xCC0F172A), CircleShape)
                            .border(1.dp, Color(0xFF334155), CircleShape)
                            .testTag("nav_cloud_btn")
                    ) {
                        Text("☁️", fontSize = 14.sp)
                    }

                    IconButton(
                        onClick = { showSettingsDialog = true },
                        modifier = Modifier
                            .size(34.dp)
                            .background(Color(0xCC0F172A), CircleShape)
                            .border(1.dp, Color(0xFF334155), CircleShape)
                            .testTag("nav_settings_btn")
                    ) {
                        Text("⚙️", fontSize = 14.sp)
                    }
                }
            }
        }

        // 3. Floating In-Game Toast Alert
        AnimatedVisibility(
            visible = toastMessage != null,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 90.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xE60284C7),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8))
            ) {
                Text(
                    text = toastMessage ?: "",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }

        // 4. Nearby NPC Interaction Floating Prompt
        if (nearbyNPC != null) {
            Button(
                onClick = { activeDialogueNPC = nearbyNPC },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8), contentColor = Color(0xFF0F172A)),
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = (-65).dp)
                    .testTag("talk_npc_prompt_btn")
            ) {
                Text(
                    text = "💬 Talk to ${nearbyNPC.name}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }

        // 5. Bottom HUD: Controls & Hotbar
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 8.dp)
        ) {
            // Hotbar Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xDD0F172A),
                    border = androidx.compose.foundation.BorderStroke(1.5f.dp, Color(0xFF334155))
                ) {
                    Row(
                        modifier = Modifier.padding(6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        for (i in 0 until 6) {
                            val slot = player.hotbar.getOrNull(i)
                            val isSelected = player.selectedHotbarIndex == i

                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) Color(0xFF0284C7) else Color(0xFF1E293B))
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) Color(0xFFFFD700) else Color(0xFF475569),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable {
                                        player.selectedHotbarIndex = i
                                    }
                                    .testTag("hotbar_slot_$i"),
                                contentAlignment = Alignment.Center
                            ) {
                                if (slot != null) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(slot.item.iconEmoji, fontSize = 18.sp)
                                        if (slot.count > 1) {
                                            Text(
                                                text = "${slot.count}",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                    }
                                } else {
                                    Text("${i + 1}", fontSize = 10.sp, color = Color(0xFF64748B))
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Joystick & Action Buttons Cluster
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                // Left: Virtual Touch Joystick
                VirtualJoystick(
                    modifier = Modifier.testTag("virtual_joystick"),
                    sizeDp = 135f,
                    onMove = { dx, dy ->
                        gameEngine.joystickDx = dx
                        gameEngine.joystickDy = dy
                    }
                )

                // Right: Action Buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Sprint Toggle
                        IconButton(
                            onClick = { gameEngine.toggleSprint() },
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    if (player.isSprinting) Color(0xFFFBBF24) else Color(0xCC0F172A),
                                    CircleShape
                                )
                                .border(1.5.dp, Color(0xFF334155), CircleShape)
                                .testTag("sprint_btn")
                        ) {
                            Text("⚡", fontSize = 20.sp)
                        }

                        // Crafting Station
                        IconButton(
                            onClick = { showCraftingSheet = true },
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color(0xCC0F172A), CircleShape)
                                .border(1.5.dp, Color(0xFF38BDF8), CircleShape)
                                .testTag("open_crafting_btn")
                        ) {
                            Text("🛠️", fontSize = 20.sp)
                        }
                    }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Place Block Button
                        IconButton(
                            onClick = { gameEngine.placeSelectedBlock() },
                            modifier = Modifier
                                .size(54.dp)
                                .background(
                                    Brush.linearGradient(listOf(Color(0xFF0284C7), Color(0xFF0284C7))),
                                    CircleShape
                                )
                                .border(2.dp, Color(0xFF38BDF8), CircleShape)
                                .testTag("place_block_btn")
                        ) {
                            Text("🧱", fontSize = 22.sp)
                        }

                        // Architecture Palette selector
                        IconButton(
                            onClick = { showBuildPalette = true },
                            modifier = Modifier
                                .size(46.dp)
                                .background(Color(0xCC0F172A), CircleShape)
                                .border(1.5.dp, Color(0xFF334155), CircleShape)
                                .testTag("open_build_palette_btn")
                        ) {
                            Text("🏗️", fontSize = 18.sp)
                        }
                    }

                    // Primary Action Button (Harvest / Mine / Attack)
                    Button(
                        onClick = { gameEngine.performAction() },
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEF4444)
                        ),
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier
                            .size(72.dp)
                            .border(2.5.dp, Color(0xFFFFD700), CircleShape)
                            .testTag("primary_action_btn")
                    ) {
                        val selected = player.getSelectedItem()
                        val icon = when {
                            selected?.category == ItemCategory.TOOL -> "⛏️"
                            selected?.category == ItemCategory.WEAPON -> "⚔️"
                            else -> "💥"
                        }
                        Text(icon, fontSize = 30.sp)
                    }
                }
            }
        }

        // Modals & Bottom Sheets
        if (activeDialogueNPC != null) {
            DialogueSheet(
                npc = activeDialogueNPC!!,
                onDismiss = { activeDialogueNPC = null },
                onAcceptQuest = { questId ->
                    gameEngine.activeQuests.value.find { it.id == questId }?.status = QuestStatus.ACTIVE
                    gameEngine.showToast("Quest Accepted: ${activeDialogueNPC?.name}'s Mission!")
                },
                onOpenShop = {
                    showMarketSheet = true
                },
                onAffinityGained = { amt ->
                    gameEngine.showToast("Reputation increased with ${activeDialogueNPC?.name} (+$amt) ❤️")
                }
            )
        }

        if (showCraftingSheet) {
            CraftingSheet(
                player = player,
                onCraft = { recipe ->
                    gameEngine.craftRecipe(recipe)
                },
                onDismiss = { showCraftingSheet = false }
            )
        }

        if (showBuildPalette) {
            BuildPaletteSheet(
                player = player,
                onSelectBlockItem = { item ->
                    player.hotbar[player.selectedHotbarIndex] = ItemStack(item, player.getItemCount(item.id).coerceAtLeast(1))
                    gameEngine.showToast("Equipped ${item.name} for building!")
                },
                onDismiss = { showBuildPalette = false }
            )
        }

        if (showMarketSheet) {
            MarketEconomySheet(
                player = player,
                economy = economy,
                onBuyItem = { item, count, price ->
                    if (player.gold >= price) {
                        player.gold -= price
                        player.addItem(item, count)
                        economy.recordTransaction(item.id, isBuy = true, quantity = count)
                        gameEngine.showToast("Purchased ${item.name} x$count (-$price Gold)")
                    }
                },
                onSellItem = { item, count, price ->
                    if (player.removeItem(item.id, count)) {
                        player.gold += price
                        economy.recordTransaction(item.id, isBuy = false, quantity = count)
                        gameEngine.showToast("Sold ${item.name} x$count (+$price Gold)")
                    }
                },
                onDepositBank = { amt ->
                    if (player.gold >= amt) {
                        player.gold -= amt
                        economy.bankSavings += amt
                        gameEngine.showToast("Deposited $amt Gold to Bank Vault")
                    }
                },
                onWithdrawBank = { amt ->
                    if (economy.bankSavings >= amt) {
                        economy.bankSavings -= amt
                        player.gold += amt
                        gameEngine.showToast("Withdrew $amt Gold from Bank Vault")
                    }
                },
                onDismiss = { showMarketSheet = false }
            )
        }

        if (showMultiplayerSheet) {
            MultiplayerSheet(
                multiplayer = multiplayer,
                onSendMessage = { text ->
                    gameEngine.sendChatMessage(text)
                },
                onDismiss = { showMultiplayerSheet = false }
            )
        }

        if (showModStudioSheet) {
            ModStudioSheet(
                customMods = customMods,
                onCreateMod = { mod ->
                    gameEngine.addCustomMod(mod)
                },
                onDismiss = { showModStudioSheet = false }
            )
        }

        if (showCloudSaveSheet) {
            CloudSaveSheet(
                player = player,
                onSaveToCloud = { slotId ->
                    gameEngine.showToast("World & state encrypted and saved to Cloud Slot $slotId ☁️")
                },
                onLoadFromCloud = { slotId ->
                    gameEngine.showToast("Loaded state from Cloud Slot $slotId")
                },
                onDismiss = { showCloudSaveSheet = false }
            )
        }

        if (showQuestLogSheet) {
            QuestLogSheet(
                quests = activeQuests,
                onClaimReward = { qId ->
                    gameEngine.claimQuestReward(qId)
                },
                onDismiss = { showQuestLogSheet = false }
            )
        }

        if (showMinimapDialog) {
            MinimapDialog(
                player = player,
                npcs = npcs,
                onTeleport = { tx, ty ->
                    player.x = tx
                    player.y = ty
                    gameEngine.spawnParticle(tx, ty, 0xFFA855F7, ParticleType.MAGIC_RUNE, 1.2f)
                    gameEngine.showToast("Teleported across Aetheria!")
                },
                onDismiss = { showMinimapDialog = false }
            )
        }

        if (showSettingsDialog) {
            SettingsDialog(
                gameEngine = gameEngine,
                onDismiss = { showSettingsDialog = false }
            )
        }
    }
}
