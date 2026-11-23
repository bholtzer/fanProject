# Fan running sound

The Android resource `shared/src/androidMain/res/raw/fan_sound.wav` is derived
from **Table ventilator**, recorded by **Stephan**, 16 May 2007.

Source: https://commons.wikimedia.org/wiki/File:Table_ventilator.ogg

The author released this recording into the public domain through PDSounds.
The source page documents permission for commercial use and modification.

Changes: mono conversion, selection of a steady running section, DC removal,
half-second overlap for continuous looping, bass reduction, and soft peak limiting. No
synthetic hum is mixed in. The original Ogg is retained in this directory.

Regenerate by decoding `table_ventilator_original.ogg` to a mono, 44100 Hz,
16-bit WAV named `table_ventilator_original.wav`, then running
`generate_fan_sound.py` with Python. This replaces the former
synthetic recording.
