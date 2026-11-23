package com.tuxunxiangqi.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.tuxunxiangqi.android.ui.BoardMode
import com.tuxunxiangqi.android.ui.BoardScreen
import com.tuxunxiangqi.android.ui.HomeScreen
import com.tuxunxiangqi.android.ui.JiangTheme
import com.tuxunxiangqi.android.data.GameRecord
import com.tuxunxiangqi.android.ui.RecordsScreen
import com.tuxunxiangqi.android.ui.GuessScreen
import com.tuxunxiangqi.android.ui.GuideScreen
import com.tuxunxiangqi.android.ui.PuzzleScreen
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.tuxunxiangqi.android.data.SettingsStore
import com.tuxunxiangqi.android.engine.PikafishEngine
import com.tuxunxiangqi.android.ui.ReviewScreen
import com.tuxunxiangqi.android.ui.SettingsScreen
import com.tuxunxiangqi.android.ui.StatsScreen

private sealed interface Screen {
    data object Home : Screen
    data class Board(
        val mode: BoardMode,
        val startCells: List<com.tuxunxiangqi.android.xiangqi.Piece?>? = null,
        val resume: com.tuxunxiangqi.android.data.ResumeGame? = null,
        val startTurnRed: Boolean = true,
        val bookPrefixUci: List<String> = emptyList()
    ) : Screen
    data object Records : Screen
    data object Guess : Screen
    data object Puzzles : Screen
    data object Openings : Screen
    data object Guide : Screen
    data class Review(val record: GameRecord) : Screen
    data object Editor : Screen
    data object Settings : Screen
    data object Stats : Screen
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var themeMode by remember { mutableStateOf(SettingsStore(this).themeMode) }
            val warmContext = LocalContext.current
            LaunchedEffect(Unit) {
                // Pre-warm: copy the NNUE file and boot the engine
                // once so the first game starts without the wait.
                val warm = PikafishEngine(warmContext)
                warm.start({ warm.close() }, { warm.close() })
            }
            val dark = when (themeMode) {
                "dark" -> true
                "light" -> false
                else -> isSystemInDarkTheme()
            }
            JiangTheme(darkTheme = dark) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var screen by remember { mutableStateOf<Screen>(Screen.Home) }
                    BackHandler(enabled = screen != Screen.Home) {
                        screen = Screen.Home
                    }
                    when (val s = screen) {
                        Screen.Home -> HomeScreen(
                            onVsAi = { screen = Screen.Board(BoardMode.AI) },
                            onVsFriend = { screen = Screen.Board(BoardMode.PVP) },
                            onRecords = { screen = Screen.Records },
                            onEditor = { screen = Screen.Editor },
                            onSpectacle = { screen = Screen.Board(BoardMode.SPECTACLE) },
                            onGuess = { screen = Screen.Guess },
                            onPuzzles = { screen = Screen.Puzzles },
                            onOpenings = { screen = Screen.Openings },
                            onGuide = { screen = Screen.Guide },
                            onSettings = { screen = Screen.Settings },
                            onResume = { game ->
                                screen = Screen.Board(
                                    mode = if (game.modeName == "AI") {
                                        BoardMode.AI
                                    } else {
                                        BoardMode.PVP
                                    },
                                    resume = game
                                )
                            }
                        )
                        is Screen.Board -> BoardScreen(
                            mode = s.mode,
                            onBack = { screen = Screen.Home },
                            startCells = s.startCells,
                            startTurn = if (s.startTurnRed) {
                                com.tuxunxiangqi.android.xiangqi.Side.RED
                            } else {
                                com.tuxunxiangqi.android.xiangqi.Side.BLACK
                            },
                            bookPrefix = s.bookPrefixUci,
                            resume = s.resume,
                            onReviewRecord = { id ->
                                val rec = com.tuxunxiangqi.android.data.RecordStore(
                                    applicationContext
                                ).loadAll().firstOrNull { it.id == id }
                                if (rec != null) screen = Screen.Review(rec)
                            }
                        )
                        Screen.Records -> RecordsScreen(
                            onBack = { screen = Screen.Home },
                            onOpenReview = { record -> screen = Screen.Review(record) }
                        )
                        Screen.Guess -> GuessScreen(
                            onBack = { screen = Screen.Home }
                        )
                        Screen.Puzzles -> PuzzleScreen(
                            onBack = { screen = Screen.Home }
                        )
                        Screen.Openings -> com.tuxunxiangqi.android.ui.OpeningScreen(
                            onBack = { screen = Screen.Home },
                            onStartGame = { cells, redTurn, m, history ->
                                screen = Screen.Board(
                                    mode = m,
                                    startCells = cells,
                                    startTurnRed = redTurn,
                                    bookPrefixUci = history
                                )
                            }
                        )
                        Screen.Settings -> SettingsScreen(
                            onBack = { screen = Screen.Home },
                            onOpenStats = { screen = Screen.Stats },
                            onThemeChanged = { themeMode = it }
                        )
                        Screen.Stats -> StatsScreen(
                            onBack = { screen = Screen.Settings }
                        )
                        Screen.Guide -> GuideScreen(
                            onBack = { screen = Screen.Home }
                        )
                        is Screen.Review -> ReviewScreen(
                            record = s.record,
                            onBack = { screen = Screen.Records }
                        )
                        Screen.Editor -> com.tuxunxiangqi.android.ui.EditorScreen(
                            onBack = { screen = Screen.Home },
                            onStart = { cells, m ->
                                screen = Screen.Board(m, cells)
                            }
                        )
                    }
                }
            }
        }
    }
}
