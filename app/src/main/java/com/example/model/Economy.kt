package com.example.model

data class MarketListing(
    val id: String,
    val sellerName: String,
    val isPlayerListing: Boolean,
    val item: Item,
    var count: Int,
    val unitPrice: Int,
    val timestamp: Long = System.currentTimeMillis()
)

data class DynamicEconomyState(
    val marketListings: MutableList<MarketListing> = mutableListOf(),
    val priceMultipliers: MutableMap<String, Float> = mutableMapOf(),
    var bankSavings: Int = 0,
    var playerStallSalesEarned: Int = 0,
    var lastMarketShiftTimestamp: Long = System.currentTimeMillis()
) {
    fun getMarketPrice(item: Item): Int {
        val multiplier = priceMultipliers.getOrDefault(item.id, 1.0f)
        return (item.baseValue * multiplier).toInt().coerceAtLeast(1)
    }

    fun recordTransaction(itemId: String, isBuy: Boolean, quantity: Int) {
        val current = priceMultipliers.getOrDefault(itemId, 1.0f)
        // Buying increases demand/price, selling increases supply/decreases price
        val delta = if (isBuy) (0.02f * quantity) else (-0.02f * quantity)
        priceMultipliers[itemId] = (current + delta).coerceIn(0.5f, 2.5f)
    }

    companion object {
        fun createDefault(): DynamicEconomyState {
            val listings = mutableListOf(
                MarketListing("lst_1", "Caravan Traders", false, ItemCatalog.WOOD, 40, 2),
                MarketListing("lst_2", "Dwarf Guild", false, ItemCatalog.IRON_INGOT, 15, 14),
                MarketListing("lst_3", "Herbalist Guild", false, ItemCatalog.MANA_BLOOM, 20, 9),
                MarketListing("lst_4", "Citadel Armory", false, ItemCatalog.IRON_LONGSWORD, 3, 50),
                MarketListing("lst_5", "Arcane Enclave", false, ItemCatalog.AETHER_CRYSTAL, 8, 30),
                MarketListing("lst_6", "Desert Excavators", false, ItemCatalog.GOLD_NUGGET, 10, 35)
            )
            val multipliers = mutableMapOf(
                ItemCatalog.WOOD.id to 1.0f,
                ItemCatalog.STONE.id to 1.0f,
                ItemCatalog.IRON_ORE.id to 1.15f,
                ItemCatalog.IRON_INGOT.id to 1.1f,
                ItemCatalog.AETHER_CRYSTAL.id to 1.25f,
                ItemCatalog.MANA_BLOOM.id to 0.95f,
                ItemCatalog.OBSIDIAN_SHARD.id to 1.3f
            )
            return DynamicEconomyState(listings, multipliers, bankSavings = 50)
        }
    }
}
