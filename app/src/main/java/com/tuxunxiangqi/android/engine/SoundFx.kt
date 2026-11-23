package com.tuxunxiangqi.android.engine

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.MediaPlayer
import com.tuxunxiangqi.android.R
import com.tuxunxiangqi.android.data.SettingsStore
import kotlin.concurrent.thread
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin
import kotlin.random.Random

enum class SoundKind { MOVE, CAPTURE, CHECK, WIN, LOSE, DRAW, GOOD, BAD }

/** In-game sounds. Move, capture, win, lose and draw use real
 *  recorded clips bundled in res/raw (from the open-source
 *  ChessRoad project, GPL-3.0 — see THIRD_PARTY_NOTICES.md),
 *  played through MediaPlayer. The remaining cues (check, and
 *  the small guess-game ticks) fall back to the original
 *  code-synthesized PCM tones, which also serve as the fallback
 *  if a clip ever fails to play. Gated by the user's sound
 *  setting; every failure is swallowed so a sound problem can
 *  never break a game. */
object SoundFx {

    private const val RATE = 44100

    private fun rawFor(kind: SoundKind): Int? = when (kind) {
        SoundKind.MOVE -> R.raw.snd_move
        SoundKind.CAPTURE -> R.raw.snd_capture
        SoundKind.WIN -> R.raw.snd_win
        SoundKind.LOSE -> R.raw.snd_lose
        SoundKind.DRAW -> R.raw.snd_draw
        else -> null
    }

    fun play(context: Context, kind: SoundKind) {
        try {
            if (!SettingsStore(context).soundOn) return
            val raw = rawFor(kind)
            if (raw != null && playRaw(context, raw)) return
            playSynth(kind)
        } catch (_: Exception) {
        }
    }

    /** Live raw-clip players right now. Rapid-fire moves (fast
     *  spectacle speed, quick taps) must not pile MediaPlayers
     *  up: past the cap a cue is dropped silently instead —
     *  overlapping wood knocks are natural, a dozen are not. */
    private val activeRawPlayers = java.util.concurrent.atomic.AtomicInteger(0)
    private const val MAX_RAW_PLAYERS = 3

    /** Plays a bundled clip; returns false (so the caller falls
     *  back to the synth tone) if anything goes wrong. When the
     *  concurrency cap is hit it returns true on purpose: the
     *  cue counts as handled, so no synth pile-up follows. */
    private fun playRaw(context: Context, raw: Int): Boolean {
        if (activeRawPlayers.incrementAndGet() > MAX_RAW_PLAYERS) {
            activeRawPlayers.decrementAndGet()
            return true
        }
        return try {
            thread(isDaemon = true) {
                var player: MediaPlayer? = null
                val released = java.util.concurrent.atomic.AtomicBoolean(false)
                fun releaseOnce() {
                    if (released.compareAndSet(false, true)) {
                        try {
                            player?.release()
                        } catch (_: Exception) {
                        }
                        activeRawPlayers.decrementAndGet()
                    }
                }
                try {
                    player = MediaPlayer.create(context, raw)
                    if (player == null) {
                        activeRawPlayers.decrementAndGet()
                        return@thread
                    }
                    player.setOnCompletionListener { releaseOnce() }
                    player.setOnErrorListener { _, _, _ ->
                        releaseOnce()
                        true
                    }
                    player.start()
                } catch (_: Exception) {
                    releaseOnce()
                }
            }
            true
        } catch (_: Exception) {
            activeRawPlayers.decrementAndGet()
            false
        }
    }

    private fun playSynth(kind: SoundKind) {
        try {
            val samples = render(kind)
            thread(isDaemon = true) {
                var track: AudioTrack? = null
                try {
                    track = AudioTrack.Builder()
                        .setAudioAttributes(
                            AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_GAME)
                                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                .build()
                        )
                        .setAudioFormat(
                            AudioFormat.Builder()
                                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                                .setSampleRate(RATE)
                                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                                .build()
                        )
                        .setBufferSizeInBytes(samples.size * 2)
                        .setTransferMode(AudioTrack.MODE_STATIC)
                        .build()
                    track.write(samples, 0, samples.size)
                    track.play()
                    Thread.sleep(samples.size * 1000L / RATE + 150)
                } catch (_: Exception) {
                    // Sound is decoration; stay silent on any error.
                } finally {
                    try {
                        track?.release()
                    } catch (_: Exception) {
                    }
                }
            }
        } catch (_: Exception) {
        }
    }

    /** One tone segment: frequency, length, and whether it is a
     *  noise knock instead of a sine. */
    private data class Seg(val freq: Double, val ms: Int, val knock: Boolean = false, val vol: Double = 0.5)

    private fun render(kind: SoundKind): ShortArray {
        val segs: List<Seg> = when (kind) {
            SoundKind.MOVE -> listOf(Seg(170.0, 80, knock = true, vol = 0.7))
            SoundKind.CAPTURE -> listOf(
                Seg(130.0, 100, knock = true, vol = 0.85),
                Seg(0.0, 50),
                Seg(110.0, 90, knock = true, vol = 0.7)
            )
            SoundKind.CHECK -> listOf(
                Seg(990.0, 90, vol = 0.45),
                Seg(0.0, 60),
                Seg(990.0, 110, vol = 0.45)
            )
            SoundKind.WIN -> listOf(
                Seg(523.0, 120, vol = 0.5),
                Seg(659.0, 120, vol = 0.5),
                Seg(784.0, 200, vol = 0.55)
            )
            SoundKind.LOSE -> listOf(
                Seg(392.0, 140, vol = 0.5),
                Seg(330.0, 140, vol = 0.5),
                Seg(262.0, 220, vol = 0.5)
            )
            SoundKind.DRAW -> listOf(
                Seg(523.0, 130, vol = 0.5),
                Seg(523.0, 180, vol = 0.45)
            )
            SoundKind.GOOD -> listOf(
                Seg(660.0, 100, vol = 0.5),
                Seg(880.0, 160, vol = 0.5)
            )
            SoundKind.BAD -> listOf(Seg(220.0, 180, vol = 0.5))
        }
        val totalMs = segs.sumOf { it.ms }
        val out = ShortArray(totalMs * RATE / 1000)
        var offset = 0
        for (seg in segs) {
            val n = seg.ms * RATE / 1000
            val decay = 3.5 / (seg.ms / 1000.0) // exp envelope rate
            for (i in 0 until n) {
                val t = i.toDouble() / RATE
                val env = exp(-decay * t) * minOf(1.0, t / 0.006) // 6ms attack
                val v = if (seg.freq == 0.0) {
                    0.0
                } else if (seg.knock) {
                    // Low thump plus a decaying noise burst reads
                    // as a wooden piece hitting the board.
                    (sin(2 * PI * seg.freq * t) * 0.8 +
                        (Random.nextDouble() * 2 - 1) * 0.35) * env
                } else {
                    sin(2 * PI * seg.freq * t) * env
                }
                val idx = offset + i
                if (idx < out.size) {
                    out[idx] = (v * seg.vol * Short.MAX_VALUE)
                        .toInt().coerceIn(-32767, 32767).toShort()
                }
            }
            offset += n
        }
        return out
    }
}
