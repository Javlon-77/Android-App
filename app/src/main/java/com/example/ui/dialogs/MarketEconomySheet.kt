package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
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
fun MarketEconomySheet(
    player: PlayerState,
    economy: DynamicEconomyState,
    onBuyItem: (item: Item, count: Int, price: Int) -> Unit,
    onSellItem: (item: Item, count: Int, price: Int) -> Unit,
    onDepositBank: (amount: Int) -> Unit,
    onWithdrawBank: (amount: Int) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Exchange, 1: Player Stall & Bank

    val exchangeItems = listOf(
        ItemCatalog.WOOD,
        ItemCatalog.STONE,
        ItemCatalog.IRON_ORE,
        ItemCatalog.IRON_INGOT,
        ItemCatalog.AETHER_CRYSTAL,
        ItemCatalog.MANA_BLOOM,
        ItemCatalog.OBSIDIAN_SHARD,
        ItemCatalog.GOLD_NUGGET
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF161E2E),
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color(0xFFFBBF24)) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 8.dp)
                .testTag("market_economy_sheet")
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = "Economy",
                        tint = Color(0xFFFBBF24),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Aetheria Commodity Exchange",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_market_button")) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF94A3B8))
                }
            }

            // Player balances
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "🪙 Purse: ${player.gold} Gold",
                    fontSize = 13.sp,
                    color = Color(0xFFFFD700),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "🏛️ Bank Vault: ${economy.bankSavings} Gold",
                    fontSize = 13.sp,
                    color = Color(0xFF38BDF8),
                    fontWeight = FontWeight.Bold
                )
            }

            // Tab row
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color(0xFF202B40),
                contentColor = Color(0xFF38BDF8)
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Global Market", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Stall & Vault", fontWeight = FontWeight.Bold) }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (selectedTab == 0) {
                // Commodity Exchange List
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 380.dp)
                        .padding(bottom = 16.dp)
                ) {
                    items(exchangeItems) { item ->
                        val price = economy.getMarketPrice(item)
                        val mult = economy.priceMultipliers.getOrDefault(item.id, 1.0f)
                        val isHighDemand = mult >= 1.05f
                        val playerHas = player.getItemCount(item.id)

                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF202B40)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(item.iconEmoji, fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(10.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color.White
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "$price Gold",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFFFD700)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(
                                            imageVector = if (isHighDemand) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                                            contentDescription = "Trend",
                                            tint = if (isHighDemand) Color(0xFF34D399) else Color(0xFFF87171),
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = String.format("%.0f%%", (mult - 1f) * 100),
                                            fontSize = 11.sp,
                                            color = if (isHighDemand) Color(0xFF34D399) else Color(0xFFF87171)
                                        )
                                    }
                                    Text("Owned: $playerHas", fontSize = 11.sp, color = Color(0xFF94A3B8))
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Button(
                                        onClick = { onBuyItem(item, 5, price * 5) },
                                        enabled = player.gold >= price * 5,
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                        modifier = Modifier.testTag("buy_btn_${item.id}")
                                    ) {
                                        Text("Buy 5", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Button(
                                        onClick = { onSellItem(item, 5, price * 5) },
                                        enabled = playerHas >= 5,
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                        modifier = Modifier.testTag("sell_btn_${item.id}")
                                    ) {
                                        Text("Sell 5", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Bank Vault & Player Stall
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF202B40)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "🏦 Citadel Bank Vault",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Secure savings safe from bandits. Generates 5% passive dividend.",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Button(
                                    onClick = { onDepositBank(50) },
                                    enabled = player.gold >= 50,
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                                ) {
                                    Text("Deposit 50g", fontSize = 11.sp)
                                }
                                Button(
                                    onClick = { onWithdrawBank(50) },
                                    enabled = economy.bankSavings >= 50,
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFBBF24), contentColor = Color.Black)
                                ) {
                                    Text("Withdraw 50g", fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF202B40)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "🏪 Your Outpost Merchant Stall",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Visitors and NPCs purchase goods from your stall. Total earned: ${economy.playerStallSalesEarned} Gold",
                                fontSize = 11.sp,
                                color = Color(0xFF34D399)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Active Stall Listings: 4 items (Oak Timber, Refined Iron, Vitality Potions, Lanterns)",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }
                }
            }
        }
    }
}
