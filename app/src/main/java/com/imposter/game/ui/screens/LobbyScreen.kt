package com.imposter.game.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imposter.game.data.AvatarStore
import com.imposter.game.ui.components.GradientBackground
import com.imposter.game.ui.components.PlayerAvatar
import com.imposter.game.ui.components.PrimaryButton
import com.imposter.game.ui.theme.Accent
import com.imposter.game.ui.theme.AccentBright
import com.imposter.game.ui.theme.BgCard
import com.imposter.game.ui.theme.BgMid
import com.imposter.game.ui.theme.TextMuted
import com.imposter.game.ui.theme.TextSecondary
import com.imposter.game.viewmodel.GameUiState
import java.io.File

@Composable
fun LobbyScreen(
    state: GameUiState,
    onAddPlayer: () -> Unit,
    onRemovePlayer: (Int) -> Unit,
    onRename: (Int, String) -> Unit,
    onSetAvatar: (Int, String?) -> Unit,
    onOpenSettings: () -> Unit,
    onBack: () -> Unit,
    onStart: () -> Unit,
) {
    val context = LocalContext.current
    var pendingPlayerId by remember { mutableStateOf<Int?>(null) }
    var pendingFile by remember { mutableStateOf<File?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
    ) { success ->
        val id = pendingPlayerId
        val file = pendingFile
        if (success && id != null && file != null) {
            val path = AvatarStore.saveFromCapture(context, id, file)
            if (path != null) {
                onSetAvatar(id, path)
            } else {
                Toast.makeText(context, "Foto konnte nicht gespeichert werden", Toast.LENGTH_SHORT).show()
            }
        } else if (file != null) {
            file.delete()
        }
        pendingPlayerId = null
        pendingFile = null
    }

    GradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconCircleButton(icon = Icons.Filled.ArrowBack, onClick = onBack)
                Spacer(Modifier.weight(1f))
                Text(
                    text = "Spieler",
                    color = Color.White,
                    fontSize = 22.sp,
                )
                Spacer(Modifier.weight(1f))
                IconCircleButton(icon = Icons.Filled.Settings, onClick = onOpenSettings)
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "${state.players.size} Spieler · Imposter ${imposterText(state)}",
                color = TextSecondary,
                fontSize = 15.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(20.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 12.dp),
            ) {
                items(state.players, key = { it.id }) { player ->
                    PlayerRow(
                        name = player.name,
                        score = player.score,
                        avatarPath = player.avatarPath,
                        canRemove = state.players.size > 3,
                        onRename = { onRename(player.id, it) },
                        onRemove = { onRemovePlayer(player.id) },
                        onCamera = {
                            val (uri, file) = AvatarStore.createCaptureTarget(context)
                            pendingPlayerId = player.id
                            pendingFile = file
                            try {
                                cameraLauncher.launch(uri)
                            } catch (_: Throwable) {
                                Toast.makeText(context, "Keine Kamera-App gefunden", Toast.LENGTH_SHORT).show()
                                pendingPlayerId = null
                                pendingFile = null
                                file.delete()
                            }
                        },
                        onClearPhoto = { onSetAvatar(player.id, null) },
                    )
                }
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(BgCard.copy(alpha = 0.5f))
                            .clickable(enabled = state.players.size < 16) { onAddPlayer() },
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.Add,
                                contentDescription = null,
                                tint = AccentBright,
                            )
                            Spacer(Modifier.size(8.dp))
                            Text(
                                text = if (state.players.size < 16) "Spieler hinzufügen" else "Maximal 16 Spieler",
                                color = AccentBright,
                                fontSize = 16.sp,
                            )
                        }
                    }
                }
            }

            if (state.players.size < 3) {
                Text(
                    text = "Mindestens 3 Spieler zum Starten",
                    color = TextMuted,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    textAlign = TextAlign.Center,
                )
            }

            PrimaryButton(
                text = "Runde starten",
                onClick = onStart,
                enabled = state.players.size >= 3,
                leading = {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                    )
                },
            )
            Spacer(Modifier.height(8.dp))
        }
    }
}

private fun imposterText(state: GameUiState): String {
    val s = state.settings
    return when (s.imposterMode) {
        com.imposter.game.model.ImposterCountMode.FIXED -> s.fixedImposters.toString()
        com.imposter.game.model.ImposterCountMode.RANDOM -> "Zufall ${s.randomMin}–${s.randomMax}"
        com.imposter.game.model.ImposterCountMode.AUTO -> "Auto"
    }
}

@Composable
private fun PlayerRow(
    name: String,
    score: Int,
    avatarPath: String?,
    canRemove: Boolean,
    onRename: (String) -> Unit,
    onRemove: () -> Unit,
    onCamera: () -> Unit,
    onClearPhoto: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(BgCard)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AvatarWithCameraBadge(
            avatarPath = avatarPath,
            onCamera = onCamera,
            onLongPress = onClearPhoto,
        )
        Spacer(Modifier.size(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            BasicTextField(
                value = name,
                onValueChange = onRename,
                textStyle = LocalTextStyle.current.copy(color = Color.White, fontSize = 17.sp),
                cursorBrush = androidx.compose.ui.graphics.SolidColor(AccentBright),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            if (score > 0) {
                Text(
                    text = "$score Pkt",
                    color = TextMuted,
                    fontSize = 13.sp,
                )
            }
        }
        if (canRemove) {
            IconCircleButton(icon = Icons.Filled.Close, onClick = onRemove, size = 36.dp)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AvatarWithCameraBadge(
    avatarPath: String?,
    onCamera: () -> Unit,
    onLongPress: () -> Unit,
) {
    Box(modifier = Modifier.size(52.dp)) {
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(48.dp)
                .clip(CircleShape)
                .combinedClickable(
                    onLongClick = onLongPress,
                    onClick = onCamera,
                ),
        ) {
            PlayerAvatar(
                avatarPath = avatarPath,
                size = 48.dp,
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(22.dp)
                .clip(CircleShape)
                .background(AccentBright)
                .clickable { onCamera() },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.PhotoCamera,
                contentDescription = null,
                tint = BgMid,
                modifier = Modifier.size(14.dp),
            )
        }
    }
}

@Composable
fun IconCircleButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    size: androidx.compose.ui.unit.Dp = 44.dp,
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.08f))
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = Color.White)
    }
}
