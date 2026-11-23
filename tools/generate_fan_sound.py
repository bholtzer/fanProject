"""Build the Android fan loop from Stephan's public-domain table-fan recording.

Source: https://commons.wikimedia.org/wiki/File:Table_ventilator.ogg
Decode to tools/table_ventilator_original.wav (mono, 44100 Hz) before running.
"""
from array import array
from math import exp, pi, sqrt, tanh
from pathlib import Path
import sys
import wave


def rms(samples):
    return sqrt(sum(sample * sample for sample in samples) / len(samples))


root = Path(__file__).resolve().parents[1]
with wave.open(str(root / "tools/table_ventilator_original.wav"), "rb") as source:
    rate = source.getframerate()
    assert source.getnchannels() == 1 and source.getsampwidth() == 2
    audio = array("h")
    audio.frombytes(source.readframes(source.getnframes()))
    if sys.byteorder != "little":
        audio.byteswap()

# Select a steady ten-second interval, skipping motor startup and shutdown.
length = rate * 10
block = length // 40
candidates = []
for second in range(7, 29):
    start = second * rate
    clip = audio[start:start + length]
    levels = [rms(clip[index:index + block]) for index in range(0, length, block)]
    mean = sum(levels) / len(levels)
    if mean > .01 * 32768:
        variability = sqrt(sum((level - mean) ** 2 for level in levels) / len(levels))
        candidates.append((variability / mean, start))
_, start = min(candidates)

# Reduce low bass that phone speakers reproduce poorly. Let the filter settle
# before the loop starts so it does not introduce a transient at the join.
bass = 0.0
smoothing = 1 - exp(-2 * pi * 140 / rate)
clip = []
for sample in audio[start - rate // 2:start + length]:
    normalized = sample / 32768
    bass += smoothing * (normalized - bass)
    clip.append(normalized - bass * .84)
clip = clip[rate // 2:]

overlap = rate // 2
join = [
    tail + (head - tail) * index / overlap
    for index, (tail, head) in enumerate(zip(clip[-overlap:], clip[:overlap]))
]
loop = join + clip[overlap:-overlap]

# Soft-limit peaks while bringing the steady motor sound to an audible level.
low_gain, high_gain = 0.0, 32.0
for _ in range(14):
    gain = (low_gain + high_gain) / 2
    if rms([.85 * tanh(gain * sample) for sample in loop]) < .19:
        low_gain = gain
    else:
        high_gain = gain
loop = [.85 * tanh(low_gain * sample) for sample in loop]
assert .18 <= rms(loop) <= .19
assert abs(loop[0] - loop[-1]) < .1

pcm = array("h", (round(sample * 32767) for sample in loop))
if sys.byteorder != "little":
    pcm.byteswap()
with wave.open(str(root / "shared/src/androidMain/res/raw/fan_sound.wav"), "wb") as target:
    target.setparams((1, 2, rate, 0, "NONE", "not compressed"))
    target.writeframes(pcm.tobytes())
print(f"Fan loop: {len(loop) / rate:.1f}s, peak {max(map(abs, loop)):.3f}, RMS {rms(loop):.3f}")
