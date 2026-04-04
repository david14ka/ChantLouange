"""
update_cs_audio_from_ob.py
==========================
CS (Crois Seulement) is the French translation of OB (Only Believe).
Song number N in CS = Song number N in OB.

This script:
1. Reads hymnallibrary_ob_audio.json  -> OB audio by song number
2. Reads unified_audio_map.json       -> existing audio entries keyed by CS song number
3. For every CS song number (1-222+), if OB has audio for that number,
   adds the OB MIDI URLs to unified_audio_map.json under that key.
   Existing cantiquest entries are preserved; OB URLs are appended only
   if not already present.
"""

import json
import os

BASE = r"c:\Users\Josh\StudioProjects\ChantLouange"

OB_AUDIO_FILE    = os.path.join(BASE, "hymnallibrary_ob_audio.json")
AUDIO_MAP_FILE   = os.path.join(BASE, "unified_audio_map.json")
OUTPUT_FILE      = os.path.join(BASE, "unified_audio_map.json")

# ── 1. Load OB audio ─────────────────────────────────────────────────────────
with open(OB_AUDIO_FILE, encoding="utf-8") as f:
    ob_list = json.load(f)

# Build dict: song_number_str -> list_of_urls
ob_audio = {}
for entry in ob_list:
    num = str(entry["number"]).strip()
    urls = []
    for key in ("piano_midi", "instru_midi"):
        url = entry.get(key, "").strip()
        if url:
            urls.append(url)
    # deduplicate (piano and instru are often the same URL)
    seen = set()
    unique_urls = []
    for u in urls:
        if u not in seen:
            seen.add(u)
            unique_urls.append(u)
    ob_audio[num] = unique_urls

print(f"Loaded {len(ob_audio)} OB audio entries.")

# ── 2. Load existing audio map ────────────────────────────────────────────────
with open(AUDIO_MAP_FILE, encoding="utf-8") as f:
    audio_map = json.load(f)

print(f"Existing audio_map has {len(audio_map)} entries.")

# ── 3. CS song numbers: extract from CS.java ─────────────────────────────────
# CS songs go from 1 to ~222, same numbers as OB.
# We'll simply iterate over all numbers that exist in ob_audio and add them.

added   = 0
updated = 0
already = 0

for num_str, ob_urls in ob_audio.items():
    if not ob_urls:
        continue

    existing = audio_map.get(num_str, [])

    # Append OB URLs not already in existing list
    new_urls = [u for u in ob_urls if u not in existing]

    if new_urls:
        audio_map[num_str] = existing + new_urls
        if existing:
            updated += 1
        else:
            added += 1
    else:
        already += 1

print(f"\nResults:")
print(f"  New entries added    : {added}")
print(f"  Existing entries updated (OB URLs appended): {updated}")
print(f"  Already fully covered: {already}")
print(f"  Total entries in map : {len(audio_map)}")

# ── 4. Save ───────────────────────────────────────────────────────────────────
with open(OUTPUT_FILE, "w", encoding="utf-8") as f:
    json.dump(audio_map, f, ensure_ascii=False, indent=2)

print(f"\nSaved to: {OUTPUT_FILE}")
