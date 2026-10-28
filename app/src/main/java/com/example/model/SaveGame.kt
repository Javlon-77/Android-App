package com.example.model

data class SaveSlot(
    val slotId: Int,
    var slotName: String,
    var playTimeSeconds: Long,
    var playerLevel: Int,
    var playerGold: Int,
    var locationName: String,
    var lastSavedTimestamp: Long,
    var cloudSynced: Boolean = true,
    var checksumHash: String = ""
) {
    fun formatPlayTime(): String {
        val hours = playTimeSeconds / 3600
        val mins = (playTimeSeconds % 3600) / 60
        val secs = playTimeSeconds % 60
        return String.format("%02d:%02d:%02d", hours, mins, secs)
    }
}
