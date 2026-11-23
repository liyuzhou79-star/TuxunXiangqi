# Third-party notices

## Pikafish

The chess engine is [Pikafish](https://github.com/official-pikafish/Pikafish),
a free and strong UCI xiangqi engine derived from Stockfish.
Copyright (C) the Pikafish developers. Licensed under GPL-3.0.

The engine binary is compiled from the unmodified upstream source in
CI with the Android NDK. The bundled neural network file
`pikafish.nnue` is the official default network distributed by the
Pikafish project (official-pikafish/Networks).

## Piece and river calligraphy — Bakudai （莫大毛筆體）

The brush-calligraphy typeface used for the piece glyphs and the
楚河漢界 river text is **Bakudai Bold** （莫大毛筆體）, an open-source
Traditional Chinese brush font by max32002, derived from the Kouzan
brush font （衡山毛筆フォント) by 青柳衡山.
Source: https://github.com/max32002/bakudaifont
Licensed under the SIL Open Font License 1.1 (full text in
`licenses/OFL-1.1-Bakudai.txt`).

It is bundled as a subset (`app/src/main/res/font/bakudai_bold.ttf`,
about 11 KB) containing only the glyphs the board renders.
One glyph, 卒， is taken from **NaikaiFont Bold** （內海字體） by the
same author (https://github.com/max32002/naikaifont, also SIL OFL
1.1), because Bakudai's own 卒 outline is drawn in a running-brush
form that reads ambiguously at piece size. Both fonts permit this
use and combination under OFL 1.1.

## Board wood texture

The walnut grain seen on the board face and frame is the photograph
"Texture de noyer" (walnut wood) by **Cyril5555**, from Wikimedia
Commons: https://commons.wikimedia.org/wiki/File:Texture_de_noyer.jpg
Licensed under Creative Commons Attribution-ShareAlike 3.0 Unported
(CC BY-SA 3.0). The bundled copy
(`app/src/main/res/drawable-nodpi/board_wood.jpg`) is modified:
resized and recompressed, and it is colour-tinted at draw time.
Modified versions of the photograph are likewise shared under
CC BY-SA 3.0.

## In-game sound clips — ChessRoad

The recorded clips for move, capture, win, lose and draw
(`app/src/main/res/raw/snd_*.mp3`) come from **ChessRoad Lite**
（棋路）, an open-source xiangqi application by 何兆雲 (hezhaoyun):
https://github.com/hezhaoyun/chessroad-lite
Licensed under GPL-3.0, the same license as this application.
The clips were trimmed of trailing silence and loudness-normalised;
no other changes were made. The remaining short cues (the
guess-game ticks) are synthesized in code by this application
and are not third-party material.

## Checkmate voice shout — chinese-chess-fish-android

The human voice clip played when a game ends by checkmate or
stalemate (`app/src/main/res/raw/snd_checkmate_voice.mp3`) comes
from **chinese-chess-fish-android** by zfdang:
https://github.com/zfdang/chinese-chess-fish-android
Licensed under the MIT License. The clip was verified to be a
genuine human recording (speech transcription plus spectral /
formant analysis) before inclusion; it was trimmed of silence,
loudness-normalised and re-encoded, with no change to the voice
itself.

## Check voice call — WatchChess

The human voice clip played whenever a move gives check
(`app/src/main/res/raw/snd_check_voice.mp3`) comes from
**WatchChess** by yingwang:
https://github.com/yingwang/WatchChess
Licensed under GPL-3.0, the same license as this application.
The clip was verified to be a genuine human recording before
inclusion: speech transcription (Whisper, two model sizes)
reads it as an unambiguous "将军" (check), its fundamental
frequency wanders naturally (roughly 120–195 Hz, adult male),
and its spectrum shows the irregular harmonic / formant
structure of a real shouted voice — unlike the flat synthesized
tones rejected in earlier searches. It was loudness-normalised
and re-encoded, with no change to the voice itself.

## Baked piece faces (in-house assets)

The fourteen piece-face images (`piece_red_*.webp` /
`piece_black_*.webp` in `res/drawable-nodpi`) were produced
in-house by an offline baking script; no third-party artwork
is included. Each face composites materials already listed
above: the CC BY-SA 3.0 walnut photograph (brightened and
re-toned toward boxwood, rotated per piece) for the grain,
and the Bakudai Bold calligraphy typeface (OFL) for the
glyph. The side wall, engraved rings, bevel light and glyph
emboss are drawn by the script itself. At runtime the app
only pastes the baked image and draws one live shadow, so
the pieces keep a finished-art look without per-frame
procedural drawing.
