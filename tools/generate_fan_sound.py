"""Build the bundled loop from Stephan's public-domain table-fan recording.

Source: https://commons.wikimedia.org/wiki/File:Table_ventilator.ogg
Decode to tools/table_ventilator_original.wav (mono, 44100 Hz) before running.
Requires numpy. The original recording is retained alongside this script.
"""
from pathlib import Path
import wave
import numpy as np

root = Path(__file__).resolve().parents[1]
with wave.open(str(root / 'tools/table_ventilator_original.wav'), 'rb') as source:
    rate = source.getframerate()
    assert source.getnchannels() == 1 and source.getsampwidth() == 2
    audio = np.frombuffer(source.readframes(source.getnframes()), dtype='<i2').astype(float) / 32768

# Find a steady ten-second section, excluding power-on/off and speed changes.
length = rate * 10
candidates = []
for second in range(7, 29):
    clip = audio[second * rate:second * rate + length]
    blocks = clip.reshape(40, -1)
    rms = np.sqrt(np.mean(blocks ** 2, axis=1))
    if rms.mean() > .01:
        candidates.append((rms.std() / rms.mean(), second))
_, start = min(candidates)
clip = audio[start * rate:start * rate + length].copy()
clip -= clip.mean()
# Overlap the tail and head. The resulting boundaries follow adjacent source samples.
overlap = rate // 2
fade = np.linspace(0, 1, overlap, endpoint=False)
join = clip[-overlap:] * (1 - fade) + clip[:overlap] * fade
loop = np.concatenate((join, clip[overlap:-overlap]))
loop *= min(.85 / np.max(np.abs(loop)), .18 / np.sqrt(np.mean(loop ** 2)))
output = root / 'shared/src/androidMain/res/raw/fan_sound.wav'
with wave.open(str(output), 'wb') as target:
    target.setparams((1, 2, rate, 0, 'NONE', 'not compressed'))
    target.writeframes((loop * 32767).astype('<i2').tobytes())
assert np.max(np.abs(loop)) < 1
assert abs(loop[0] - loop[-1]) < .1
print(f'Real fan loop: section {start}-{start + 10}s, duration {len(loop)/rate:.1f}s, peak {np.max(np.abs(loop)):.3f}, RMS {np.sqrt(np.mean(loop ** 2)):.3f}, boundary jump {abs(loop[0]-loop[-1]):.5f}')
