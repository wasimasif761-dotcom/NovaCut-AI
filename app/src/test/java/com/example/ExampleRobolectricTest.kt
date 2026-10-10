package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.ProjectRepository
import com.example.model.AspectRatio
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
    assertEquals("NovaCut AI", appName)
  }

  @Test
  fun `create and query project repository`() {
    val initialProjects = ProjectRepository.projects.value
    assertTrue(initialProjects.isNotEmpty())

    val newProj = ProjectRepository.createNewProject("Test Reel", AspectRatio.ASPECT_9_16)
    assertNotNull(newProj)
    assertEquals("Test Reel", newProj.title)
    assertEquals(AspectRatio.ASPECT_9_16, newProj.aspectRatio)

    val queried = ProjectRepository.getProjectById(newProj.id)
    assertNotNull(queried)
    assertEquals("Test Reel", queried?.title)
  }
}
