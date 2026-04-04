import json
import re
import unicodedata

def normalize(text):
    if not text: return ""
    nfd = unicodedata.normalize("NFD", text)
    s = "".join(c for c in nfd if unicodedata.category(c) != "Mn")
    return " ".join(re.sub(r"[^\w\s]", "", s.lower()).split())

def extract_book_markers(text):
    return re.findall(r"\b([A-Za-z]{2,5})\s+(\d+[a-z]?)\b", text or "")

# Load data
with open("cantiquest_songs_with_audio.json", "r", encoding="utf-8") as f:
    cantiquest_data = json.load(f)

with open("all_songs_dump.json", "r", encoding="utf-8") as f:
    all_songs = json.load(f)

with open("app/src/main/res/raw/audio_map.json", "r", encoding="utf-8") as f:
    existing_map = json.load(f)

# The existing map was exclusively for CV (bookId = 2) without prefixes.
new_map = {}
for k, v in existing_map.items():
    if "_" not in k: # If it doesn't already have a prefix
        new_map[f"2_{k}"] = v
    else:
        new_map[k] = v

cq_by_marker = {}
cq_by_title = {}

BOOK_ALIASES = {
    "CV": "CV", 
    "HC": "HeC", "HEC": "HeC", 
    "CJ": "CJ", "CGG": "CGG1", "CGG1": "CGG1", "CGG2": "CGG2",
    "SST": "SST", "VAM": "VAM", "SSO": "SSO", "CPE": "CPE", "TEJ": "TEJ"
}

for song in cantiquest_data:
    b = song["book"]
    # We want to map it directly
    cq_by_marker[(b, song["number"].lower())] = song
    
    nt = normalize(song["title"])
    if nt.lower() not in ["titre", "titres"]:
        cq_by_title.setdefault(nt, []).append(song)

for song in all_songs:
    book_id = song["bookId"]
    raw_num = song["number"].strip().rstrip(".").strip().lower()
    content = song["content"]
    title = song["title"]
    
    key = f"{book_id}_{raw_num}"
    current_links = set(new_map.get(key, []))
    original_len = len(current_links)
    
    # 1. Exact Book match to Cantiquest
    book_abbr = song["bookAbbr"].upper()
    target_cq = BOOK_ALIASES.get(book_abbr)
    if target_cq and (target_cq, raw_num) in cq_by_marker:
        cq = cq_by_marker[(target_cq, raw_num)]
        if cq.get("piano_midi"): current_links.add(cq["piano_midi"])
        if cq.get("instru_midi"): current_links.add(cq["instru_midi"])

    # 2. Markers in content
    markers = extract_book_markers(content)
    for b_mkr, n_mkr in markers:
        t_cq = BOOK_ALIASES.get(b_mkr.upper())
        if t_cq and (t_cq, n_mkr.lower()) in cq_by_marker:
            cq = cq_by_marker[(t_cq, n_mkr.lower())]
            if cq.get("piano_midi"): current_links.add(cq["piano_midi"])
            if cq.get("instru_midi"): current_links.add(cq["instru_midi"])

    # 3. Fuzzy title match
    if not current_links:
        nt = normalize(title)
        if nt in cq_by_title:
            for cq in cq_by_title[nt]:
                if cq.get("piano_midi"): current_links.add(cq["piano_midi"])
                if cq.get("instru_midi"): current_links.add(cq["instru_midi"])
                
    if current_links:
        new_map[key] = sorted(list(current_links))

with open("app/src/main/res/raw/audio_map.json", "w", encoding="utf-8") as f:
    json.dump(new_map, f, indent=2, ensure_ascii=False)

print(f"Generated comprehensive map with {len(new_map)} mapped songs.")
