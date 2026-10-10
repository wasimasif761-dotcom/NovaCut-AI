package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.ScreenDestination
import com.example.ui.screens.CloudSyncScreen
import com.example.ui.screens.EditorScreen
import com.example.ui.screens.ExportScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.StudioDarkBg
import com.example.viewmodel.EditorViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: EditorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = StudioDarkBg
                ) {
                    NovaCutApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun NovaCutApp(viewModel: EditorViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = androidx.compose.ui.platform.LocalContext.current

    Crossfade(
        targetState = uiState.currentScreen,
        label = "ScreenTransition"
    ) { screen ->
        when (screen) {
            ScreenDestination.HOME, ScreenDestination.TEMPLATES -> {
                HomeScreen(
                    projects = uiState.projects,
                    cloudUsedGb = uiState.cloudStorageUsedGb,
                    cloudTotalGb = uiState.cloudStorageTotalGb,
                    isCloudSyncing = uiState.isCloudSyncing,
                    onOpenProject = { viewModel.openProject(it) },
                    onCreateNewProject = { title, aspect -> viewModel.createNewProject(title, aspect) },
                    onImportVideosAndCreateProject = { uris ->
                        viewModel.createNewProjectFromUris(
                            context = context,
                            uris = uris,
                            title = "Project " + (uiState.projects.size + 1),
                            aspectRatio = com.example.model.AspectRatio.ASPECT_9_16
                        )
                    },
                    onDuplicateProject = { viewModel.duplicateProject(it) },
                    onDeleteProject = { viewModel.deleteProject(it) },
                    onOpenCloudSync = { viewModel.navigateTo(ScreenDestination.CLOUD_SYNC) }
                )
            }
            ScreenDestination.STUDIO_EDITOR, ScreenDestination.AI_MAGIC_HUB -> {
                EditorScreen(
                    uiState = uiState,
                    viewModel = viewModel,
                    onBackToHome = { viewModel.navigateTo(ScreenDestination.HOME) },
                    onGoToExport = { viewModel.navigateTo(ScreenDestination.EXPORT_RENDER) },
                    onGoToCloud = { viewModel.navigateTo(ScreenDestination.CLOUD_SYNC) }
                )
            }
            ScreenDestination.EXPORT_RENDER -> {
                ExportScreen(
                    project = uiState.currentProject,
                    renderStatus = uiState.renderStatus,
                    onStartRender = { profile -> viewModel.startExport(context, profile) },
                    onBack = {
                        if (uiState.currentProject != null) {
                            viewModel.navigateTo(ScreenDestination.STUDIO_EDITOR)
                        } else {
                            viewModel.navigateTo(ScreenDestination.HOME)
                        }
                    }
                )
            }
            ScreenDestination.CLOUD_SYNC -> {
                CloudSyncScreen(
                    usedGb = uiState.cloudStorageUsedGb,
                    totalGb = uiState.cloudStorageTotalGb,
                    isSyncing = uiState.isCloudSyncing,
                    onTriggerSync = { viewModel.triggerCloudSync() },
                    onBack = {
                        if (uiState.currentProject != null) {
                            viewModel.navigateTo(ScreenDestination.STUDIO_EDITOR)
                        } else {
                            viewModel.navigateTo(ScreenDestination.HOME)
                        }
                    }
                )
            }
        }
    }
}
