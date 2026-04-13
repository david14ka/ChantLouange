"""Fetch raw HTML of a hymn page and dump it to inspect audio sources."""
import requests

url = "https://gccsatx.com/hymns/have-thine-own-way-lord/"
headers = {"User-Agent": "Mozilla/5.0 (compatible; HymnScraper/1.0)"}

r = requests.get(url, headers=headers, timeout=15)
html = r.text

# Search for audio-related content
import re

# 1) find all mp3/ogg/wav references
audio_refs = re.findall(r'["\']([^"\']*\.(mp3|ogg|wav|m4a|aac|flac|mid|midi)[^"\']*)["\']', html, re.I)
print("=== Audio file references ===")
for ref in audio_refs[:30]:
    print(ref[0])

print("\n=== <audio> tag context ===")
for m in re.finditer(r'<audio[^>]*>.*?</audio>', html, re.S | re.I):
    print(m.group()[:500])
    print("---")

print("\n=== Lines containing 'audio' ===")
for i, line in enumerate(html.splitlines()):
    if 'audio' in line.lower() or '.mp3' in line.lower() or '.wav' in line.lower() or 'soundcloud' in line.lower():
        print(f"[{i}] {line.strip()[:200]}")

# Also dump a section around any "src" with media
print("\n=== All src= values ===")
for m in re.finditer(r'src=["\']([^"\']+)["\']', html, re.I):
    val = m.group(1)
    if any(ext in val.lower() for ext in ['.mp3', '.ogg', '.wav', '.m4a', '.flac', '.mid', 'soundcloud', 'audio']):
        print(val)
