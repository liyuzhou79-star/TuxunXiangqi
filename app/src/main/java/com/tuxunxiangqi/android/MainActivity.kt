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
import com.tuxunxiangqi.android.ui.ReviewScreen

private sealed interface Screen {
    data object Home : Screen
    data class Board(
        val mode: BoardMode,
        val startCells: List<com.tuxunxiangqi.android.xiangqi.Piece?>? = null
    ) : Screen
    data object Records : Screen
    data object Guess : Screen
    data object Puzzles : Screen
    data object Guide : Screen
    data class Review(val record: GameRecord) : Screen
    data object Editor : Screen
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JiangTheme {
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
                            onGuide = { screen = Screen.Guide }
                        )
                        is Screen.Board -> BoardScreen(
                            mode = s.mode,
                            onBack = { screen = Screen.Home },
                            startCells = s.startCells
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
