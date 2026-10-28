package com.example.ui.dialogs

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Quest
import com.example.model.QuestStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestLogSheet(
    quests: List<Quest>,
    onClaimReward: (questId: String) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF161E2E),
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color(0xFF38BDF8)) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 6.dp)
                .testTag("quest_log_sheet")
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = "Quests",
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Aetheria Chronicles & Quests",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_quest_log_button")) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF94A3B8))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp)
                    .padding(bottom = 16.dp)
            ) {
                items(quests) { quest ->
                    val isReady = quest.status == QuestStatus.READY_TO_CLAIM
                    val isCompleted = quest.status == QuestStatus.COMPLETED

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isReady) Color(0xFF1E293B) else Color(0xFF202B40)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = when {
                                    isReady -> Color(0xFFFFD700)
                                    isCompleted -> Color(0xFF10B981)
                                    else -> Color(0xFF334155)
                                },
                                shape = RoundedCornerShape(12.dp)
                            )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(quest.category.badgeEmoji, fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = quest.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = Color.White
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = when {
                                        isCompleted -> Color(0x3310B981)
                                        isReady -> Color(0x33FBBF24)
                                        else -> Color(0x330284C7)
                                    }
                                ) {
                                    Text(
                                        text = when {
                                            isCompleted -> "Completed"
                                            isReady -> "Claimable!"
                                            else -> "In Progress"
                                        },
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when {
                                            isCompleted -> Color(0xFF34D399)
                                            isReady -> Color(0xFFFFD700)
                                            else -> Color(0xFF38BDF8)
                                        }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = quest.description,
                                fontSize = 12.sp,
                                color = Color(0xFF94A3B8)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Objectives list
                            for (obj in quest.objectives) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Done",
                                        tint = if (obj.isCompleted) Color(0xFF34D399) else Color(0xFF64748B),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${obj.description} (${obj.currentCount}/${obj.requiredCount})",
                                        fontSize = 11.sp,
                                        color = if (obj.isCompleted) Color(0xFFE2E8F0) else Color(0xFF94A3B8),
                                        fontWeight = if (obj.isCompleted) FontWeight.SemiBold else FontWeight.Normal
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Rewards footer
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Rewards: +${quest.rewardGold} Gold, +${quest.rewardXp} XP",
                                    fontSize = 11.sp,
                                    color = Color(0xFFFFD700),
                                    fontWeight = FontWeight.Bold
                                )

                                if (isReady) {
                                    Button(
                                        onClick = { onClaimReward(quest.id) },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700), contentColor = Color.Black),
                                        modifier = Modifier.testTag("claim_quest_${quest.id}")
                                    ) {
                                        Text("Claim Reward", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
