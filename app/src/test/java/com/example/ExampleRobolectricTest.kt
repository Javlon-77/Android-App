package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.game.GameEngine
import com.example.model.ItemCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Aetheria", appName)
  }

  @Test
  fun `verify game engine initialization`() {
    val engine = GameEngine()
    val player = engine.playerState.value
    assertNotNull(player)
    assertTrue(player.hp > 0)
    assertTrue(player.inventory.isNotEmpty() || player.hotbar.any { it != null })
    assertEquals(ItemCatalog.WOOD_SWORD.id, player.hotbar[0]?.item?.id)
  }
}
