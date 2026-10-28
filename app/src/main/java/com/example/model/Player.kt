package com.example.model

data class Equipment(
    val weapon: Item? = null,
    val headArmor: Item? = null,
    val chestArmor: Item? = null,
    val accessory: Item? = null
)

data class PlayerStats(
    val attackPower: Int,
    val defense: Int,
    val harvestSpeed: Float,
    val moveSpeed: Float
)

data class PlayerState(
    var name: String = "Traveler",
    var x: Float = 64f,
    var y: Float = 64f,
    var level: Int = 1,
    var currentXp: Int = 0,
    var requiredXp: Int = 100,
    var hp: Int = 100,
    var maxHp: Int = 100,
    var stamina: Float = 100f,
    var maxStamina: Float = 100f,
    var gold: Int = 120,
    var aetherShards: Int = 15,
    val inventory: MutableList<ItemStack> = mutableListOf(),
    val hotbar: MutableList<ItemStack?> = MutableList(6) { null },
    var selectedHotbarIndex: Int = 0,
    var equipment: Equipment = Equipment(),
    var isSprinting: Boolean = false,
    var facingAngle: Float = 0f,
    var currentBiome: BiomeType = BiomeType.VERDANT_SHIRE
) {
    fun calculateStats(): PlayerStats {
        val baseAtk = 5 + (equipment.weapon?.attackPower ?: 0)
        val baseDef = (equipment.chestArmor?.defense ?: 0) + (equipment.headArmor?.defense ?: 0)
        val harvest = (equipment.weapon?.harvestSpeed ?: 1.0f)
        val speed = if (isSprinting && stamina > 0) 1.7f else 1.0f
        return PlayerStats(baseAtk, baseDef, harvest, speed)
    }

    fun addItem(item: Item, count: Int = 1): Boolean {
        // Try stack in hotbar
        for (i in hotbar.indices) {
            val slot = hotbar[i]
            if (slot != null && slot.item.id == item.id) {
                slot.count += count
                return true
            }
        }
        // Try stack in inventory
        for (stack in inventory) {
            if (stack.item.id == item.id) {
                stack.count += count
                return true
            }
        }
        // Try empty hotbar slot
        for (i in hotbar.indices) {
            if (hotbar[i] == null) {
                hotbar[i] = ItemStack(item, count)
                return true
            }
        }
        // Add to inventory
        if (inventory.size < 36) {
            inventory.add(ItemStack(item, count))
            return true
        }
        return false // Inventory full
    }

    fun removeItem(itemId: String, count: Int = 1): Boolean {
        var remainingToRemove = count
        // Remove from hotbar
        for (i in hotbar.indices) {
            val slot = hotbar[i]
            if (slot != null && slot.item.id == itemId) {
                if (slot.count > remainingToRemove) {
                    slot.count -= remainingToRemove
                    return true
                } else {
                    remainingToRemove -= slot.count
                    hotbar[i] = null
                    if (remainingToRemove == 0) return true
                }
            }
        }
        // Remove from inventory
        val iterator = inventory.iterator()
        while (iterator.hasNext()) {
            val stack = iterator.next()
            if (stack.item.id == itemId) {
                if (stack.count > remainingToRemove) {
                    stack.count -= remainingToRemove
                    return true
                } else {
                    remainingToRemove -= stack.count
                    iterator.remove()
                    if (remainingToRemove == 0) return true
                }
            }
        }
        return remainingToRemove == 0
    }

    fun getItemCount(itemId: String): Int {
        var total = 0
        for (slot in hotbar) {
            if (slot?.item?.id == itemId) total += slot.count
        }
        for (slot in inventory) {
            if (slot.item.id == itemId) total += slot.count
        }
        return total
    }

    fun getSelectedItem(): Item? {
        return hotbar.getOrNull(selectedHotbarIndex)?.item
    }

    fun gainXp(amount: Int): Boolean {
        currentXp += amount
        if (currentXp >= requiredXp) {
            currentXp -= requiredXp
            level++
            maxHp += 15
            hp = maxHp
            maxStamina += 10f
            stamina = maxStamina
            requiredXp = (requiredXp * 1.4f).toInt()
            return true // Leveled up
        }
        return false
    }
}
