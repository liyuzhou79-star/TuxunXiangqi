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
no other changes were made. The remaining short cues (check, and
the guess-game ticks) are synthesized in code by this application
and are not third-party material.

A note on voice announcements: a human-voice "check!" call was
searched for across the open-source projects above and other
license-clear sources, but no recording with a clear license could
be verified (the candidate packs turned out to be synthesized
tones, confirmed by transcription and spectral checks). Rather
than ship a text-to-speech voice, the check cue stays a short
synthesized chime until a properly licensed human recording turns
up.
