"""
build_audio_map.py
==================
Rebuilds app/src/main/res/raw/audio_map.json by merging:
  1. Existing yapper.fr MP3 links (kept as-is)
  2. Cantiquest MIDI links matched via book/number markers found in the song content
  3. Cantiquest MIDI links matched via normalized title (fallback)

Run from the project root:
    python build_audio_map.py
"""

import json
import re
import unicodedata
import sys
import os

# ---------------------------------------------------------------------------
# Helpers
# ---------------------------------------------------------------------------

def normalize(text):
    """Lowercase, strip accents, strip punctuation."""
    if not text:
        return ""
    nfd = unicodedata.normalize("NFD", text)
    stripped = "".join(c for c in nfd if unicodedata.category(c) != "Mn")
    stripped = stripped.lower()
    stripped = re.sub(r"[^\w\s]", "", stripped)
    return " ".join(stripped.split())


def extract_book_markers(text):
    """
    Extract references like 'CV 112', 'HeC 5', 'CGG 316' from raw song content.
    Returns list of (BOOK, NUMBER) tuples.
    """
    return re.findall(r"\b([A-Za-z]{2,5})\s+(\d+[a-z]?)\b", text or "")


# Book aliases: CC app abbreviation -> Cantiquest book code in the JSON
BOOK_ALIASES = {
    "CV":   "CV",
    "HC":   "HeC",
    "HEC":  "HeC",
    "HeC":  "HeC",
    "HEC1": "HeC",
    "CJ":   "CJ",
    "CGG":  "CGG1",
    "CGG1": "CGG1",
    "CGG2": "CGG2",
    "SST":  "SST",
    "VAM":  "VAM",
    "SSO":  "SSO",
    "CPE":  "CPE",
    "TEJ":  "TEJ",
    "ATG":  None,   # not in cantiquest — skip
    "NM":   None,
    "SAF":  None,
}


def midi_links(song):
    """Return list of non-null MIDI URLs for a cantiquest song entry."""
    links = []
    if song.get("piano_midi"):
        links.append(song["piano_midi"])
    if song.get("instru_midi"):
        links.append(song["instru_midi"])
    return links


# ---------------------------------------------------------------------------
# Load data
# ---------------------------------------------------------------------------

print("Loading data...", flush=True)

with open("cantiquest_songs_with_audio.json", encoding="utf-8") as f:
    cantiquest_data = json.load(f)

with open("app/src/main/res/raw/audio_map.json", encoding="utf-8") as f:
    existing_audio_map = json.load(f)

with open("GeneratedCC.json", encoding="utf-8") as f:
    cc_data = json.load(f)

print(f"  CC songs:         {len(cc_data['songs'])}", flush=True)
print(f"  Cantiquest songs: {len(cantiquest_data)}", flush=True)
print(f"  Existing map:     {len(existing_audio_map)} entries", flush=True)

# ---------------------------------------------------------------------------
# Index Cantiquest data
# ---------------------------------------------------------------------------

# By (BOOK, NUMBER) — canonical book code from JSON
cq_by_marker = {}
for song in cantiquest_data:
    key = (song["book"], str(song["number"]).strip())
    cq_by_marker[key] = song

# By normalized title
cq_by_title = {}
for song in cantiquest_data:
    nt = normalize(song["title"])
    cq_by_title.setdefault(nt, []).append(song)

print(f"  Marker index:     {len(cq_by_marker)} keys", flush=True)
print(f"  Title index:      {len(cq_by_title)} unique titles", flush=True)

# ---------------------------------------------------------------------------
# Build new audio map — start from existing entries
# ---------------------------------------------------------------------------

new_map = {}

# Seed with existing entries (keep all existing yapper MP3s and MIDI links)
for key, links in existing_audio_map.items():
    new_map[key] = list(links)   # copy

added_via_marker = 0
added_via_title  = 0
already_covered  = 0

for cc_song in cc_data["songs"]:
    raw_num  = cc_song["number"].strip().rstrip(".").strip()
    cc_num   = raw_num.lower()
    title    = cc_song.get("title", "")
    content  = cc_song.get("content", "")

    current_links = set(new_map.get(cc_num, []))
    original_count = len(current_links)

    # ---- Strategy 1: book/number markers embedded in the song content ----
    markers = extract_book_markers(content)
    for book_abbr, num in markers:
        cq_book = BOOK_ALIASES.get(book_abbr.upper()) or BOOK_ALIASES.get(book_abbr)
        if not cq_book:
            # Also try exact case from JSON
            cq_book = book_abbr  # try as-is
        for variant in [cq_book, cq_book.upper(), cq_book.lower()]:
            key = (variant, num)
            if key in cq_by_marker:
                for url in midi_links(cq_by_marker[key]):
                    current_links.add(url)
                break

    # ---- Strategy 2: direct match by CV number ----
    # If the CC song number is a plain integer, check if there's a CV MIDI for it
    if cc_num.isdigit():
        direct_key = ("CV", cc_num)
        if direct_key in cq_by_marker and not current_links:
            for url in midi_links(cq_by_marker[direct_key]):
                current_links.add(url)

    # ---- Strategy 3: title fuzzy match ----
    if not current_links:
        nt = normalize(title)
        if nt in cq_by_title:
            for cq_song in cq_by_title[nt]:
                for url in midi_links(cq_song):
                    current_links.add(url)
            if current_links:
                added_via_title += 1

    if current_links:
        new_map[cc_num] = sorted(current_links)   # sorted for diff stability
        if len(current_links) > original_count:
            if original_count == 0:
                added_via_marker += 1
        else:
            already_covered += 1

# ---------------------------------------------------------------------------
# Print summary
# ---------------------------------------------------------------------------

total_with_audio = len(new_map)
total_songs = len(cc_data["songs"])
print(f"\nResults:", flush=True)
print(f"  Songs with audio: {total_with_audio} / {total_songs}", flush=True)
print(f"  Already in map:   {already_covered}", flush=True)
print(f"  Added via marker: {added_via_marker}", flush=True)
print(f"  Added via title:  {added_via_title}", flush=True)

# Show some songs that STILL have no audio
no_audio = [s for s in cc_data["songs"] if s["number"].strip().rstrip(".").strip().lower() not in new_map]
print(f"  No audio found:   {len(no_audio)}", flush=True)
if no_audio:
    print("\n  First 20 songs without audio:", flush=True)
    for s in no_audio[:20]:
        print(f"    [{s['number']}] {s.get('title','?')[:60]}", flush=True)

# ---------------------------------------------------------------------------
# Save
# ---------------------------------------------------------------------------

out_path = "app/src/main/res/raw/audio_map.json"
with open(out_path, "w", encoding="utf-8") as f:
    json.dump(new_map, f, indent=2, ensure_ascii=False)

print(f"\nSaved to {out_path}  ({len(new_map)} entries)", flush=True)
