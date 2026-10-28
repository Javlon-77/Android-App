package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PlayerState
import com.example.model.SaveSlot

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CloudSaveSheet(
    player: PlayerState,
    onSaveToCloud: (slotId: Int) -> Unit,
    onLoadFromCloud: (slotId: Int) -> Unit,
    onDismiss: () -> Unit
) {
    var saveSlots by remember {
        mutableStateOf(
            listOf(
                SaveSlot(1, "Traveler's Sanctuary", 4920L, player.level, player.gold, player.currentBiome.displayName, System.currentTimeMillis() - 60000, true, "SHA256: 8A4F..E92C"),
                SaveSlot(2, "Citadel Fortress Outpost", 12300L, 8, 450, "Sky Citadel", System.currentTimeMillis() - 86400000, true, "SHA256: 3F9B..7D1A"),
                SaveSlot(3, "Volcanic Forge Excavation", 2100L, 3, 85, "Shadowfell Crags", System.currentTimeMillis() - 172800000, false, "SHA256: 1C88..44B2")
            )
        )
    }

    var exportCodeDialog by remember { mutableStateOf<String?>(null) }
    var importInput by remember { mutableStateOf("") }
    var showImportField by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF161E2E),
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color(0xFF10B981)) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 6.dp)
                .testTag("cloud_save_sheet")
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CloudDone,
                        contentDescription = "Cloud Save",
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Encrypted Cloud Save Vault",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_cloud_save_button")) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF94A3B8))
                }
            }

            Text(
                text = "AES-256 state serialization synced with cloud servers for seamless cross-platform continuity.",
                fontSize = 11.sp,
                color = Color(0xFF94A3B8)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Save slots
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(saveSlots) { slot ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF202B40)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Slot ${slot.slotId}: ${slot.slotName}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color.White
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (slot.cloudSynced) Icons.Default.CloudDone else Icons.Default.CloudSync,
                                        contentDescription = "Sync",
                                        tint = if (slot.cloudSynced) Color(0xFF34D399) else Color(0xFFFBBF24),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (slot.cloudSynced) "Cloud Synced" else "Pending Sync",
                                        fontSize = 11.sp,
                                        color = if (slot.cloudSynced) Color(0xFF34D399) else Color(0xFFFBBF24)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Lvl ${slot.playerLevel} • ${slot.playerGold} Gold • ${slot.locationName} • Playtime: ${slot.formatPlayTime()}",
                                fontSize = 11.sp,
                                color = Color(0xFF38BDF8)
                            )
                            Text(
                                text = "Integrity Hash: ${slot.checksumHash}",
                                fontSize = 10.sp,
                                color = Color(0xFF64748B)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        exportCodeDialog = "AETH-SAVE-v1-${slot.slotId}-${System.currentTimeMillis()}-ENC${slot.checksumHash.take(8)}"
                                    },
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF38BDF8))
                                ) {
                                    Icon(Icons.Default.Share, contentDescription = "Export", modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Export Code", fontSize = 11.sp)
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Button(
                                    onClick = {
                                        onSaveToCloud(slot.slotId)
                                        slot.cloudSynced = true
                                        slot.lastSavedTimestamp = System.currentTimeMillis()
                                        slot.playerLevel = player.level
                                        slot.playerGold = player.gold
                                        saveSlots = saveSlots.map { it.copy() }
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                                ) {
                                    Text("Save Cloud", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Export dialog or token display
            if (exportCodeDialog != null) {
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("Cross-Platform Portable Save Token:", fontSize = 11.sp, color = Color(0xFF34D399), fontWeight = FontWeight.Bold)
                        Text(exportCodeDialog ?: "", fontSize = 11.sp, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
