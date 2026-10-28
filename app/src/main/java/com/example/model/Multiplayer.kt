package com.example.model

data class NetworkPlayer(
    val id: String,
    val name: String,
    val colorArgb: Long,
    var x: Float,
    var y: Float,
    var facingAngle: Float = 0f,
    var actionState: String = "Exploring",
    var heldItemName: String = "Iron Pickaxe",
    var pingMs: Int = 18
)

data class ChatMessage(
    val id: String,
    val senderName: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isSystem: Boolean = false
)

data class MultiplayerState(
    var isConnected: Boolean = true,
    var roomCode: String = "AETH-7429",
    var serverRegion: String = "Asia-East (Tokyo Relay)",
    var pingMs: Int = 22,
    var tickRate: Int = 60,
    val peers: MutableList<NetworkPlayer> = mutableListOf(),
    val chatMessages: MutableList<ChatMessage> = mutableListOf(),
    var isHost: Boolean = false
) {
    companion object {
        fun createDefault(): MultiplayerState {
            val peers = mutableListOf(
                NetworkPlayer("p_1", "Valkyrie_99", 0xFF38BDF8, 66f, 63f, 45f, "Mining Granite", "Iron Pickaxe", 18),
                NetworkPlayer("p_2", "ShadowRune", 0xFFA855F7, 60f, 68f, 180f, "Constructing Wall", "Wood Planks", 24),
                NetworkPlayer("p_3", "AuraKnight", 0xFFFBBF24, 69f, 61f, 270f, "Patrolling", "Solar Longbow", 16)
            )
            val chat = mutableListOf(
                ChatMessage("c_1", "System", "Connected to Realm AETH-7429 [Ultra Low-Latency Node]", System.currentTimeMillis() - 120000, true),
                ChatMessage("c_2", "Valkyrie_99", "Hey everyone! Found an iron vein south of the elder shrine!", System.currentTimeMillis() - 90000),
                ChatMessage("c_3", "ShadowRune", "Nice! I'm laying the foundation for our shared fortress.", System.currentTimeMillis() - 45000),
                ChatMessage("c_4", "AuraKnight", "I have spare mana draughts if anyone needs healing.", System.currentTimeMillis() - 15000)
            )
            return MultiplayerState(
                isConnected = true,
                roomCode = "AETH-7429",
                peers = peers,
                chatMessages = chat
            )
        }
    }
}
