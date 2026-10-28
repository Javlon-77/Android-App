package com.example.ui.dialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModStudioSheet(
    customMods: List<CustomMod>,
    onCreateMod: (mod: CustomMod) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Item Creator, 1: Block Creator, 2: Mod Manager

    // Item creation state
    var itemName by remember { mutableStateOf("Astral Blade") }
    var itemCategory by remember { mutableStateOf(ItemCategory.WEAPON) }
    var itemRarity by remember { mutableStateOf(ItemRarity.LEGENDARY) }
    var itemEmoji by remember { mutableStateOf("⚡") }
    var attackPower by remember { mutableFloatStateOf(65f) }
    var harvestSpeed by remember { mutableFloatStateOf(3.5f) }

    // Block creation state
    var blockName by remember { mutableStateOf("Neon Rune Obelisk") }
    var blockColorLong by remember { mutableLongStateOf(0xFF00FFCC) }
    var isLightEmitter by remember { mutableStateOf(true) }
    var lightRadius by remember { mutableFloatStateOf(5.5f) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF161E2E),
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color(0xFFA855F7)) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 6.dp)
                .testTag("mod_studio_sheet")
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Mod Studio",
                        tint = Color(0xFFA855F7),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Aetheria Mod Studio",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_mod_studio_button")) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF94A3B8))
                }
            }

            // Visual banner
            Image(
                painter = painterResource(id = R.drawable.img_mod_studio),
                contentDescription = "Arcane Mod Studio Workshop",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(95.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Tab bar
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color(0xFF202B40),
                contentColor = Color(0xFFA855F7)
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Item Creator", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Block Creator", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Mod Manager", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            when (selectedTab) {
                0 -> {
                    // Item Creator
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = itemName,
                            onValueChange = { itemName = it },
                            label = { Text("Item Name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFA855F7),
                                unfocusedBorderColor = Color(0xFF334155),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        // Emoji Selector
                        Text("Select Visual Sprite:", fontSize = 11.sp, color = Color(0xFF94A3B8))
                        val emojis = listOf("⚡", "🗡️", "🏹", "🔮", "💎", "🐉", "☄️", "🛡️", "🧪", "🪓", "🪐")
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(emojis) { em ->
                                FilterChip(
                                    selected = itemEmoji == em,
                                    onClick = { itemEmoji = em },
                                    label = { Text(em, fontSize = 16.sp) }
                                )
                            }
                        }

                        // Attack Power Slider
                        Text("Attack Damage: ${attackPower.toInt()}", fontSize = 12.sp, color = Color(0xFFF87171))
                        Slider(
                            value = attackPower,
                            onValueChange = { attackPower = it },
                            valueRange = 10f..150f,
                            colors = SliderDefaults.colors(thumbColor = Color(0xFFF87171), activeTrackColor = Color(0xFFF87171))
                        )

                        // Harvest Speed Slider
                        Text("Harvest Speed: ${String.format("%.1f", harvestSpeed)}x", fontSize = 12.sp, color = Color(0xFFFBBF24))
                        Slider(
                            value = harvestSpeed,
                            onValueChange = { harvestSpeed = it },
                            valueRange = 1f..6f,
                            colors = SliderDefaults.colors(thumbColor = Color(0xFFFBBF24), activeTrackColor = Color(0xFFFBBF24))
                        )

                        Button(
                            onClick = {
                                val newItem = Item(
                                    id = "mod_item_${System.currentTimeMillis()}",
                                    name = itemName,
                                    description = "Custom crafted in Mod Studio",
                                    category = itemCategory,
                                    rarity = itemRarity,
                                    iconEmoji = itemEmoji,
                                    baseValue = 150,
                                    attackPower = attackPower.toInt(),
                                    harvestSpeed = harvestSpeed
                                )
                                val mod = CustomMod(
                                    id = "mod_${System.currentTimeMillis()}",
                                    name = "$itemName Mod",
                                    author = "Player",
                                    version = "1.0",
                                    description = "Custom item inject",
                                    customItems = mutableListOf(newItem)
                                )
                                onCreateMod(mod)
                                onDismiss()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("inject_item_mod_button"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA855F7))
                        ) {
                            Text("✨ Inject Custom Item into Game", fontWeight = FontWeight.Bold)
                        }
                    }
                }
                1 -> {
                    // Block Creator
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = blockName,
                            onValueChange = { blockName = it },
                            label = { Text("Block Name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF00E5FF),
                                unfocusedBorderColor = Color(0xFF334155),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = isLightEmitter,
                                onCheckedChange = { isLightEmitter = it },
                                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF00E5FF))
                            )
                            Text("Emits Dynamic Point Illumination", fontSize = 12.sp, color = Color.White)
                        }

                        if (isLightEmitter) {
                            Text("Light Radius: ${String.format("%.1f", lightRadius)} tiles", fontSize = 12.sp, color = Color(0xFF00E5FF))
                            Slider(
                                value = lightRadius,
                                onValueChange = { lightRadius = it },
                                valueRange = 2f..8f,
                                colors = SliderDefaults.colors(thumbColor = Color(0xFF00E5FF), activeTrackColor = Color(0xFF00E5FF))
                            )
                        }

                        Button(
                            onClick = {
                                val blockDef = ModBlockDef(
                                    id = "mod_blk_${System.currentTimeMillis()}",
                                    name = blockName,
                                    colorArgb = blockColorLong,
                                    isLightEmitter = isLightEmitter,
                                    lightRadius = lightRadius
                                )
                                val blockItem = Item(
                                    id = "mod_item_blk_${System.currentTimeMillis()}",
                                    name = blockName,
                                    description = "Custom placeable block",
                                    category = ItemCategory.BUILDING,
                                    rarity = ItemRarity.EPIC,
                                    iconEmoji = "❇️",
                                    placeableBlockType = BlockType.MODDED_BLOCK
                                )
                                val mod = CustomMod(
                                    id = "mod_b_${System.currentTimeMillis()}",
                                    name = "$blockName Mod",
                                    author = "Player",
                                    version = "1.0",
                                    description = "Custom block inject",
                                    customItems = mutableListOf(blockItem),
                                    customBlocks = mutableListOf(blockDef)
                                )
                                onCreateMod(mod)
                                onDismiss()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("inject_block_mod_button"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                        ) {
                            Text("✨ Inject Custom Block into Game", fontWeight = FontWeight.Bold)
                        }
                    }
                }
                2 -> {
                    // Mod Manager
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 280.dp)
                            .padding(bottom = 16.dp)
                    ) {
                        items(customMods) { mod ->
                            Card(
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF202B40)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(10.dp))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(mod.name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                                        Text("By ${mod.author} • v${mod.version}", fontSize = 11.sp, color = Color(0xFF94A3B8))
                                        Text("${mod.customItems.size} Items, ${mod.customBlocks.size} Blocks", fontSize = 10.sp, color = Color(0xFF38BDF8))
                                    }
                                    Switch(
                                        checked = mod.isEnabled,
                                        onCheckedChange = { mod.isEnabled = it },
                                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF38BDF8))
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
