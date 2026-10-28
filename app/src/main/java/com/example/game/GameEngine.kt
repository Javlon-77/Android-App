package com.example.game

import com.example.model.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.*
import kotlin.random.Random

class GameEngine {
    private val _playerState = MutableStateFlow(PlayerState())
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    private val _placedBlocks = MutableStateFlow<List<PlacedBlock>>(emptyList())
    val placedBlocks: StateFlow<List<PlacedBlock>> = _placedBlocks.asStateFlow()

    private val _resourceNodes = MutableStateFlow<List<ResourceNode>>(emptyList())
    val resourceNodes: StateFlow<List<ResourceNode>> = _resourceNodes.asStateFlow()

    private val _droppedItems = MutableStateFlow<List<DroppedItem>>(emptyList())
    val droppedItems: StateFlow<List<DroppedItem>> = _droppedItems.asStateFlow()

    private val _particles = MutableStateFlow<List<Particle>>(emptyList())
    val particles: StateFlow<List<Particle>> = _particles.asStateFlow()

    private val _activeQuests = MutableStateFlow<List<Quest>>(QuestCatalog.ALL_QUESTS)
    val activeQuests: StateFlow<List<Quest>> = _activeQuests.asStateFlow()

    private val _npcs = MutableStateFlow<List<NPC>>(NPCCatalog.ALL_NPCS)
    val npcs: StateFlow<List<NPC>> = _npcs.asStateFlow()

    private val _economyState = MutableStateFlow(DynamicEconomyState.createDefault())
    val economyState: StateFlow<DynamicEconomyState> = _economyState.asStateFlow()

    private val _multiplayerState = MutableStateFlow(MultiplayerState.createDefault())
    val multiplayerState: StateFlow<MultiplayerState> = _multiplayerState.asStateFlow()

    private val _customMods = MutableStateFlow<List<CustomMod>>(listOf(ModCatalog.COSMIC_SAMPLE_MOD))
    val customMods: StateFlow<List<CustomMod>> = _customMods.asStateFlow()

    // Day / Night cycle (0.0 to 24.0 hours)
    private val _gameTimeHours = MutableStateFlow(10.5f) // Starts at 10:30 AM
    val gameTimeHours: StateFlow<Float> = _gameTimeHours.asStateFlow()

    // Performance & Settings
    var targetFps: Int = 60
    var particlesEnabled: Boolean = true
    var advancedLightingEnabled: Boolean = true
    var hapticFeedbackEnabled: Boolean = true

    // System banner alerts
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    // World grid
    lateinit var worldTiles: Array<Array<WorldTile>>
    private val solidBlocksMap = mutableMapOf<Pair<Int, Int>, PlacedBlock>()

    private var engineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var isRunning = false

    // Player velocity from joystick
    var joystickDx = 0f
    var joystickDy = 0f

    init {
        initializeWorld()
    }

    private fun initializeWorld() {
        val (tiles, nodes) = WorldGenerator.generateWorld()
        worldTiles = tiles
        _resourceNodes.value = nodes

        val initStructures = WorldGenerator.generateInitialStructures()
        _placedBlocks.value = initStructures
        solidBlocksMap.clear()
        for (b in initStructures) {
            if (b.type.isSolid) {
                solidBlocksMap[Pair(b.x, b.y)] = b
            }
        }

        // Setup starter inventory
        val p = _playerState.value
        p.hotbar[0] = ItemStack(ItemCatalog.WOOD_SWORD, 1)
        p.hotbar[1] = ItemStack(ItemCatalog.WOOD_PICKAXE, 1)
        p.hotbar[2] = ItemStack(ItemCatalog.WOOD_WALL, 24)
        p.hotbar[3] = ItemStack(ItemCatalog.TORCH, 8)
        p.hotbar[4] = ItemStack(ItemCatalog.HEALTH_POTION, 3)
        p.hotbar[5] = ItemStack(ItemCatalog.STONE_BRICK, 16)

        p.inventory.add(ItemStack(ItemCatalog.WOOD, 15))
        p.inventory.add(ItemStack(ItemCatalog.STONE, 10))
        p.inventory.add(ItemStack(ItemCatalog.AETHER_CRYSTAL, 3))
    }

    fun startLoop() {
        if (isRunning) return
        isRunning = true
        engineScope.launch {
            var lastTime = System.nanoTime()
            while (isRunning) {
                val now = System.nanoTime()
                val dt = ((now - lastTime) / 1_000_000_000f).coerceIn(0.001f, 0.05f)
                lastTime = now

                updateGame(dt)
                delay(16) // ~60 FPS update tick
            }
        }
    }

    fun stopLoop() {
        isRunning = false
    }

    private fun updateGame(dt: Float) {
        // 1. Advance Day/Night Cycle (24h in ~6 minutes = 0.066h per real second)
        val newTime = (_gameTimeHours.value + dt * 0.07f) % 24f
        _gameTimeHours.value = newTime

        // 2. Update Player Movement
        val player = _playerState.value
        if (joystickDx != 0f || joystickDy != 0f) {
            val stats = player.calculateStats()
            val speed = 3.8f * stats.moveSpeed * dt

            // Facing angle
            player.facingAngle = (atan2(joystickDy.toDouble(), joystickDx.toDouble()) * 180 / Math.PI).toFloat()

            // Sprint stamina drain
            if (player.isSprinting) {
                player.stamina = (player.stamina - 15f * dt).coerceAtLeast(0f)
                if (player.stamina <= 0f) player.isSprinting = false
            } else {
                player.stamina = (player.stamina + 10f * dt).coerceAtMost(player.maxStamina)
            }

            val targetX = (player.x + joystickDx * speed).coerceIn(2f, (WorldGenerator.WORLD_SIZE - 3).toFloat())
            val targetY = (player.y + joystickDy * speed).coerceIn(2f, (WorldGenerator.WORLD_SIZE - 3).toFloat())

            // Collision check against solid placed blocks
            val tileX = targetX.toInt()
            val tileY = targetY.toInt()
            if (!isTileSolid(tileX, tileY)) {
                player.x = targetX
                player.y = targetY
            } else if (!isTileSolid(tileX, player.y.toInt())) {
                player.x = targetX
            } else if (!isTileSolid(player.x.toInt(), tileY)) {
                player.y = targetY
            }

            // Update player current biome
            val curTileX = player.x.toInt().coerceIn(0, WorldGenerator.WORLD_SIZE - 1)
            val curTileY = player.y.toInt().coerceIn(0, WorldGenerator.WORLD_SIZE - 1)
            player.currentBiome = worldTiles[curTileX][curTileY].biome

            // Footstep dust particles
            if (particlesEnabled && Random.nextFloat() < 0.2f) {
                spawnParticle(player.x, player.y + 0.3f, 0xFF9E9E9E, ParticleType.DUST, 0.4f)
            }
        } else {
            // Stamina recovery when resting
            player.stamina = (player.stamina + 18f * dt).coerceAtMost(player.maxStamina)
        }

        // 3. Magnet & Pickup Dropped Items
        val dropped = _droppedItems.value.toMutableList()
        val iterator = dropped.iterator()
        var itemPicked = false
        while (iterator.hasNext()) {
            val d = iterator.next()
            val dist = sqrt((player.x - d.x) * (player.x - d.x) + (player.y - d.y) * (player.y - d.y))
            if (dist < 2.0f) {
                // Attract to player
                val angle = atan2((player.y - d.y).toDouble(), (player.x - d.x).toDouble())
                d.x += (cos(angle) * 5f * dt).toFloat()
                d.y += (sin(angle) * 5f * dt).toFloat()

                if (dist < 0.6f) {
                    player.addItem(d.item, d.count)
                    checkQuestProgress(ObjectiveType.GATHER_ITEM, d.item.id, d.count)
                    iterator.remove()
                    itemPicked = true
                    spawnParticle(d.x, d.y, 0xFFFFD700, ParticleType.SPARK, 0.6f)
                }
            }
        }
        if (itemPicked) {
            _droppedItems.value = dropped
        }

        // 4. Update Particle Physics
        if (particlesEnabled) {
            val curParticles = _particles.value.toMutableList()
            val pIter = curParticles.iterator()
            while (pIter.hasNext()) {
                val p = pIter.next()
                p.x += p.vx * dt
                p.y += p.vy * dt
                p.life -= dt
                if (p.life <= 0) {
                    pIter.remove()
                }
            }
            // Ambient biome particles
            if (curParticles.size < 40 && Random.nextFloat() < 0.3f) {
                spawnAmbientBiomeParticles(player)
            }
            _particles.value = curParticles
        }

        // 5. Simulate Co-op Peers Movement & Activity
        updateMultiplayerSimulation(dt)
    }

    private fun isTileSolid(tx: Int, ty: Int): Boolean {
        return solidBlocksMap.containsKey(Pair(tx, ty))
    }

    fun spawnParticle(x: Float, y: Float, color: Long, type: ParticleType, life: Float = 0.8f) {
        val angle = Random.nextFloat() * 2 * Math.PI
        val speed = Random.nextFloat() * 1.5f + 0.3f
        val p = Particle(
            x = x,
            y = y,
            vx = (cos(angle) * speed).toFloat(),
            vy = (sin(angle) * speed).toFloat(),
            colorArgb = color,
            size = Random.nextFloat() * 6f + 3f,
            life = life,
            maxLife = life,
            type = type
        )
        val list = _particles.value.toMutableList()
        list.add(p)
        _particles.value = list
    }

    private fun spawnAmbientBiomeParticles(player: PlayerState) {
        val px = player.x + (Random.nextFloat() - 0.5f) * 14f
        val py = player.y + (Random.nextFloat() - 0.5f) * 14f
        when (player.currentBiome) {
            BiomeType.ENCHANTED_GROVE -> {
                spawnParticle(px, py, 0xFFA855F7, ParticleType.GLOW_SPORE, 2.5f)
            }
            BiomeType.SHADOWFELL_CRAGS -> {
                spawnParticle(px, py, 0xFFEF4444, ParticleType.EMBER, 1.8f)
            }
            BiomeType.CRYSTAL_CANYON -> {
                spawnParticle(px, py, 0xFF38BDF8, ParticleType.MAGIC_RUNE, 2.0f)
            }
            else -> {}
        }
    }

    private fun updateMultiplayerSimulation(dt: Float) {
        val mp = _multiplayerState.value
        for (peer in mp.peers) {
            // Gentle wandering around starting area
            if (Random.nextFloat() < 0.05f) {
                val wanderAngle = Random.nextFloat() * 2 * Math.PI
                peer.x = (peer.x + (cos(wanderAngle) * 0.4f).toFloat()).coerceIn(58f, 72f)
                peer.y = (peer.y + (sin(wanderAngle) * 0.4f).toFloat()).coerceIn(58f, 72f)
                peer.facingAngle = (wanderAngle * 180 / Math.PI).toFloat()
            }
        }
    }

    // Action: Mine / Attack
    fun performAction(): Boolean {
        val player = _playerState.value
        val stats = player.calculateStats()

        // 1. Check for nearby resource node within reach
        val nodes = _resourceNodes.value.toMutableList()
        var targetedNode: ResourceNode? = null
        var closestDist = 3.2f

        for (n in nodes) {
            val d = sqrt((player.x - n.x) * (player.x - n.x) + (player.y - n.y) * (player.y - n.y))
            if (d < closestDist) {
                closestDist = d
                targetedNode = n
            }
        }

        if (targetedNode != null) {
            val damage = (12 * stats.harvestSpeed).toInt().coerceAtLeast(5)
            targetedNode.health -= damage

            // Mining impact sparks
            spawnParticle(targetedNode.x, targetedNode.y, 0xFFFFEE55, ParticleType.SPARK, 0.5f)

            if (targetedNode.health <= 0) {
                nodes.remove(targetedNode)
                _resourceNodes.value = nodes

                val dropCount = Random.nextInt(targetedNode.dropMin, targetedNode.dropMax + 1)
                spawnDroppedItem(targetedNode.x, targetedNode.y, targetedNode.droppedItem, dropCount)
                player.gainXp(15)
                showToast("Harvested ${targetedNode.droppedItem.name} x$dropCount!")
            }
            return true
        }

        // 2. Check for nearby placed block to deconstruct/mine
        val bx = player.x.roundToInt()
        val by = player.y.roundToInt()
        val targetBlock = solidBlocksMap[Pair(bx, by)]
        if (targetBlock != null) {
            removePlacedBlock(targetBlock.x, targetBlock.y)
            showToast("Dismantled ${targetBlock.type.displayName}")
            return true
        }

        // Generic swing particle
        val frontX = player.x + cos(player.facingAngle * Math.PI / 180).toFloat() * 1.2f
        val frontY = player.y + sin(player.facingAngle * Math.PI / 180).toFloat() * 1.2f
        spawnParticle(frontX, frontY, 0xFF38BDF8, ParticleType.MAGIC_RUNE, 0.3f)
        return false
    }

    // Action: Place Block from Selected Hotbar Slot
    fun placeSelectedBlock(): Boolean {
        val player = _playerState.value
        val selectedItem = player.getSelectedItem() ?: return false
        val blockType = selectedItem.placeableBlockType ?: return false

        // Place 1.2 tiles in front of player
        val frontX = (player.x + cos(player.facingAngle * Math.PI / 180) * 1.4f).roundToInt()
        val frontY = (player.y + sin(player.facingAngle * Math.PI / 180) * 1.4f).roundToInt()

        if (frontX !in 1 until WorldGenerator.WORLD_SIZE - 1 || frontY !in 1 until WorldGenerator.WORLD_SIZE - 1) {
            return false
        }

        // Check if tile already occupied
        if (solidBlocksMap.containsKey(Pair(frontX, frontY))) {
            showToast("Tile is already occupied!")
            return false
        }

        val placed = PlacedBlock(
            id = "blk_${System.currentTimeMillis()}_${Random.nextInt(1000)}",
            x = frontX,
            y = frontY,
            type = blockType,
            placedBy = player.name,
            lightRadius = blockType.lightEmission
        )

        val list = _placedBlocks.value.toMutableList()
        list.add(placed)
        _placedBlocks.value = list

        if (blockType.isSolid) {
            solidBlocksMap[Pair(frontX, frontY)] = placed
        }

        // Consume 1 item
        player.removeItem(selectedItem.id, 1)
        player.gainXp(10)

        // Block placement particle
        spawnParticle(frontX.toFloat(), frontY.toFloat(), 0xFF64B5F6, ParticleType.DUST, 0.6f)
        showToast("Constructed ${blockType.displayName}!")

        checkQuestProgress(ObjectiveType.BUILD_BLOCK, selectedItem.id, 1)
        return true
    }

    fun removePlacedBlock(x: Int, y: Int): Boolean {
        val list = _placedBlocks.value.toMutableList()
        val target = list.find { it.x == x && it.y == y } ?: return false
        list.remove(target)
        _placedBlocks.value = list
        solidBlocksMap.remove(Pair(x, y))
        spawnParticle(x.toFloat(), y.toFloat(), 0xFFEF4444, ParticleType.SPARK, 0.5f)
        return true
    }

    private fun spawnDroppedItem(x: Float, y: Float, item: Item, count: Int) {
        val d = DroppedItem("drop_${System.currentTimeMillis()}_${Random.nextInt(100)}", x, y, item, count)
        val list = _droppedItems.value.toMutableList()
        list.add(d)
        _droppedItems.value = list
    }

    // Check NPC interaction
    fun findNearbyNPC(): NPC? {
        val player = _playerState.value
        for (npc in _npcs.value) {
            val dist = sqrt((player.x - npc.x) * (player.x - npc.x) + (player.y - npc.y) * (player.y - npc.y))
            if (dist < 2.8f) return npc
        }
        return null
    }

    fun craftRecipe(recipe: CraftingRecipe): Boolean {
        val player = _playerState.value
        // Verify ingredients
        for (input in recipe.inputs) {
            if (player.getItemCount(input.item.id) < input.count) {
                showToast("Missing materials for ${recipe.name}")
                return false
            }
        }
        // Deduct materials
        for (input in recipe.inputs) {
            player.removeItem(input.item.id, input.count)
        }
        // Add output item
        player.addItem(recipe.output.item, recipe.output.count)
        player.gainXp(25)

        // Trigger particles
        spawnParticle(player.x, player.y, 0xFF38BDF8, ParticleType.SPARK, 0.8f)
        showToast("Crafted ${recipe.output.item.name} x${recipe.output.count}!")

        checkQuestProgress(ObjectiveType.CRAFT_ITEM, recipe.id, 1)
        return true
    }

    fun checkQuestProgress(type: ObjectiveType, targetId: String?, amount: Int = 1) {
        val quests = _activeQuests.value.map { it.copy() }
        for (q in quests) {
            if (q.status == QuestStatus.NOT_STARTED || q.status == QuestStatus.ACTIVE) {
                for (obj in q.objectives) {
                    if (obj.type == type) {
                        if (obj.targetId == null || obj.targetId == targetId) {
                            obj.progress(amount)
                        }
                    }
                }
                if (q.checkCompletion() && q.status != QuestStatus.COMPLETED) {
                    q.status = QuestStatus.READY_TO_CLAIM
                    showToast("Quest Completed: ${q.title}! Claim rewards.")
                }
            }
        }
        _activeQuests.value = quests
    }

    fun claimQuestReward(questId: String) {
        val quests = _activeQuests.value.map { it.copy() }
        val q = quests.find { it.id == questId } ?: return
        if (q.status == QuestStatus.READY_TO_CLAIM) {
            q.status = QuestStatus.COMPLETED
            val p = _playerState.value
            p.gold += q.rewardGold
            p.aetherShards += q.rewardAetherShards
            p.gainXp(q.rewardXp)
            for (rewardItem in q.rewardItems) {
                p.addItem(rewardItem.item, rewardItem.count)
            }
            _activeQuests.value = quests
            showToast("Rewards Claimed: +${q.rewardGold} Gold, +${q.rewardXp} XP!")
        }
    }

    fun showToast(msg: String) {
        _toastMessage.value = msg
        engineScope.launch {
            delay(3000)
            if (_toastMessage.value == msg) {
                _toastMessage.value = null
            }
        }
    }

    fun toggleSprint() {
        val p = _playerState.value
        if (p.stamina > 10f) {
            p.isSprinting = !p.isSprinting
        }
    }

    fun sendChatMessage(text: String) {
        if (text.isBlank()) return
        val mp = _multiplayerState.value
        val msg = ChatMessage("c_${System.currentTimeMillis()}", _playerState.value.name, text)
        mp.chatMessages.add(msg)
        _multiplayerState.value = mp.copy()
    }

    fun addCustomMod(mod: CustomMod) {
        val mods = _customMods.value.toMutableList()
        mods.add(mod)
        _customMods.value = mods

        // Register custom items in player catalog
        for (item in mod.customItems) {
            _playerState.value.addItem(item, 1)
        }
        checkQuestProgress(ObjectiveType.CREATE_MOD, null, 1)
        showToast("Mod '${mod.name}' installed and active!")
    }

    fun calculateAmbientLight(): Pair<Long, Float> {
        val hour = _gameTimeHours.value
        // Dawn: 5.0 to 7.0
        // Day: 7.0 to 18.0
        // Dusk: 18.0 to 20.5
        // Night: 20.5 to 5.0
        return when {
            hour in 7.0f..17.5f -> Pair(0xFF000000, 0.0f) // Bright daylight
            hour in 17.5f..19.5f -> {
                val t = (hour - 17.5f) / 2.0f
                Pair(0xFFB45309, t * 0.45f) // Warm sunset amber
            }
            hour in 19.5f..20.5f -> {
                val t = (hour - 19.5f)
                Pair(0xFF1E1B4B, 0.45f + t * 0.35f) // Deep twilight
            }
            hour in 5.0f..7.0f -> {
                val t = (7.0f - hour) / 2.0f
                Pair(0xFFD97706, t * 0.4f) // Soft morning glow
            }
            else -> Pair(0xFF020617, 0.78f) // Deep midnight darkness
        }
    }

    fun collectLightSources(centerTileX: Int, centerTileY: Int, radius: Int = 14): List<LightSource> {
        val lights = mutableListOf<LightSource>()
        // Player lantern aura
        val player = _playerState.value
        lights.add(LightSource(player.x, player.y, 2.5f, 0.9f, 0xFFFFDD88))

        // Placed light blocks
        for (b in _placedBlocks.value) {
            if (b.type.lightEmission > 0 && abs(b.x - centerTileX) <= radius && abs(b.y - centerTileY) <= radius) {
                lights.add(LightSource(b.x.toFloat(), b.y.toFloat(), b.type.lightEmission, 0.95f, b.type.lightColorArgb))
            }
        }
        return lights
    }
}
