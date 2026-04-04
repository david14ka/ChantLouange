import json
import re
import unicodedata
import difflib
import os

def normalize(text):
    if not text: return ""
    nfd = unicodedata.normalize("NFD", text)
    s = "".join(c for c in nfd if unicodedata.category(c) != "Mn")
    return " ".join(re.sub(r"[^\w\s]", "", s.lower()).split())

# 1. Load cantiquest songs
with open("cantiquest_songs_with_audio.json", "r", encoding="utf-8") as f:
    cantiquest_data = json.load(f)

# Built list of normalized titles and their cantiquest song objects
cq_titles = []
cq_by_title = {}
for song in cantiquest_data:
    nt = normalize(song["title"])
    if nt not in cq_by_title:
        cq_titles.append(nt)
        cq_by_title[nt] = []
    cq_by_title[nt].append(song)

# 2. Re-extract songs only from CC.java, CV.java, OB.java, CS.java
datas_dir = "app/src/main/java/com/davidkazad/chantlouange/datas"
target_files = ["CC.java", "CV.java", "OB.java", "CS.java"]
app_songs = []

for fname in target_files:
    path = os.path.join(datas_dir, fname)
    if os.path.exists(path):
        with open(path, "r", encoding="utf-8") as f:
            content = f.read()
            m = re.search(r'super\(\s*(\d+)\s*,\s*"[^"]+"\s*,\s*"([^"]+)"', content)
            if not m: continue
            book_id = m.group(1)
            pages = re.findall(r'new Page\(\s*\d+\s*,\s*"([^"]+)"\s*,\s*"(.*?)"\s*,\s*"(.*?)"\s*,\s*\d+\s*\)', content, re.DOTALL)
            for p in pages:
                app_songs.append({
                    "bookId": book_id,
                    "number": p[0],
                    "title": p[1]
                })

print(f"Extracted {len(app_songs)} songs from {target_files}.")

# 3. Load existing audio_map.json
audio_map_path = "app/src/main/res/raw/audio_map.json"
if os.path.exists(audio_map_path):
    with open(audio_map_path, "r", encoding="utf-8") as f:
        audio_map = json.load(f)
else:
    audio_map = {}

# 4. Do fuzzy matching and update audio_map
mapped_count = 0
fuzzy_mapped = 0

for song in app_songs:
    book_id = song["bookId"]
    raw_num = song["number"].strip().rstrip(".").strip().lower()
    title = song["title"]
    
    key = f"{book_id}_{raw_num}"
    current_links = set(audio_map.get(key, []))
    
    nt = normalize(title)
    
    # Ensure cutoff is low enough for good fuzzy matching. 0.8 is usually good for titles.
    matches = difflib.get_close_matches(nt, cq_titles, n=1, cutoff=0.7)
    if matches:
        best_match = matches[0]
        if best_match != nt:
            fuzzy_mapped += 1
            print(f"Fuzzy match found: '{nt}' -> '{best_match}'")
            
        for cq in cq_by_title[best_match]:
            if cq.get("piano_midi"): current_links.add(cq["piano_midi"])
            if cq.get("instru_midi"): current_links.add(cq["instru_midi"])
            
    if current_links:
        audio_map[key] = sorted(list(current_links))
        mapped_count += 1

# 5. Save the updated map
with open(audio_map_path, "w", encoding="utf-8") as f:
    json.dump(audio_map, f, indent=2, ensure_ascii=False)

print(f"Total songs parsed: {len(app_songs)}")
print(f"Total entries in audio_map updated/verified from these 4 books: {mapped_count}")
print(f"Total fuzzy matches found: {fuzzy_mapped}")
