package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ShoppingCart
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
import com.example.model.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogueSheet(
    npc: NPC,
    onDismiss: () -> Unit,
    onAcceptQuest: (questId: String) -> Unit,
    onOpenShop: (npc: NPC) -> Unit,
    onAffinityGained: (amount: Int) -> Unit
) {
    var currentNodeId by remember(npc.id) { mutableStateOf(npc.dialogueRootId) }
    val currentNode = npc.dialogueNodes[currentNodeId] ?: npc.dialogueNodes[npc.dialogueRootId]

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF161E2E),
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color(0xFF38BDF8)) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .testTag("dialogue_sheet")
        ) {
            // Header with NPC avatar, name, title, and affinity hearts
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(Color(0xFF0284C7), Color(0xFFA855F7)))
                        )
                        .border(2.dp, Color(0xFF38BDF8), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = npc.avatarEmoji, fontSize = 28.sp)
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = npc.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0x3310B981)
                        ) {
                            Text(
                                text = npc.mood,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                fontSize = 11.sp,
                                color = Color(0xFF34D399),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Text(
                        text = npc.title,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8)
                    )

                    // Affinity Meter
                    Row(
                        modifier = Modifier.padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Affinity",
                            tint = Color(0xFFEC4899),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Affinity: ${npc.affinity}/100",
                            fontSize = 12.sp,
                            color = Color(0xFFF472B6),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_dialogue_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color(0xFF94A3B8)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Speaker speech bubble
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF202B40)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = currentNode?.speakerText ?: "...",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFFF1F5F9),
                    lineHeight = 22.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Your Response:",
                fontSize = 13.sp,
                color = Color(0xFF38BDF8),
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Choices list
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                items(currentNode?.choices ?: emptyList()) { choice ->
                    Button(
                        onClick = {
                            when (choice.actionType) {
                                DialogueActionType.ACCEPT_QUEST -> {
                                    if (choice.actionParam.isNotEmpty()) {
                                        onAcceptQuest(choice.actionParam)
                                    }
                                }
                                DialogueActionType.OPEN_SHOP -> {
                                    onOpenShop(npc)
                                }
                                DialogueActionType.GAIN_AFFINITY -> {
                                    val amount = choice.actionParam.toIntOrNull() ?: 5
                                    npc.affinity = (npc.affinity + amount).coerceAtMost(100)
                                    onAffinityGained(amount)
                                }
                                else -> {}
                            }

                            if (choice.nextNodeId != null) {
                                currentNodeId = choice.nextNodeId
                            } else {
                                onDismiss()
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0F172A),
                            contentColor = Color(0xFF38BDF8)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
                            .testTag("dialogue_choice_${choice.text.take(10)}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "💬  ${choice.text}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFFE2E8F0)
                            )
                            if (choice.actionType == DialogueActionType.OPEN_SHOP) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = "Shop",
                                    tint = Color(0xFFFBBF24),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
