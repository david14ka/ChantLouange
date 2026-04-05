import os
import re
import json
import unicodedata
import difflib

def normalize_title(title):
    if not title:
        return ""
    # Remove accents
    title = "".join(c for c in unicodedata.normalize('NFD', title) if unicodedata.category(c) != 'Mn')
    # Lowercase
    title = title.lower()
    # Remove keys/pitch (e.g., " c", " ab", " f", " g", " bb", " eb", " e") at the end
    title = re.sub(r'(\s+|[^\w])[a-g](b|#)?(\s+|$|[^\w])', ' ', title)
    # Replace special chars with space
    title = re.sub(r'[^a-z0-9]', ' ', title)
    # Remove common articles
    title = re.sub(r'^(l |d |le |la |les |un |une |the |a |an )', '', title)
    # Remove leading/trailing numbers
    title = re.sub(r'^\d+[a-z]?\s*', '', title)
    title = re.sub(r'^\d+\s*', '', title)
    title = re.sub(r'\s*\d+[a-z]?$', '', title)
    title = re.sub(r'\s*\d+$', '', title)
    # Collapse whitespace
    title = " ".join(title.split())
    return title

def find_references(text):
    refs = []
    # Match various book codes + number
    patterns = [
        (r'CS\s*\(?OB\)?\s*(\d+)', "8"), # CS usually 8
        (r'CS\s*(\d+)', "8"),
        (r'OB\s*(\d+)', "7"),
        (r'CV\s*(\d+)', "2"),
        (r'HL\s*(\d+)', "6"), # Assuming HL is book 6 (need to verify, but common)
        (r'NM\s*(\d+)', "3"), # Assuming NM is book 3
        (r'SSS\s*(\d+)', "5"),
    ]
    for p, book_id in patterns:
        matches = re.finditer(p, text, re.IGNORECASE)
        for m in matches:
            num = m.group(1)
            refs.append((book_id, num))
    return refs

BOOK_IDS = {"CC": "1", "CV": "2", "OB": "7", "CS": "8"}

def extract_songs(file_path):
    songs = []
    if not os.path.exists(file_path):
        return songs
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    # Support both quoted lyrics and null/missing lyrics
    pattern = r'pg\.add\(new Page\((\d+),\s*"([^"]+)",\s*"([^"]+)",\s*([\s\S]*?)",\s*(\d+)\s*\)\);'
    matches = re.finditer(pattern, content)
    for m in matches:
        number, id_prefix, title, lyrics, book_id = m.groups()
        id_str = id_prefix.strip().lower().replace(".", "").replace(" ", "")
        refs = find_references(title) + find_references(lyrics)
        songs.append({
            "number": number,
            "id": id_str,
            "title": title,
            "book_id": book_id,
            "key": f"{book_id}_{id_str}",
            "refs": list(set(refs))
        })
    return songs

# Paths
cc_path = r'c:\Users\Josh\StudioProjects\ChantLouange\app\src\main\java\com\davidkazad\chantlouange\datas\CC.java'
cs_path = r'c:\Users\Josh\StudioProjects\ChantLouange\app\src\main\java\com\davidkazad\chantlouange\datas\CS.java'
ob_path = r'c:\Users\Josh\StudioProjects\ChantLouange\app\src\main\java\com\davidkazad\chantlouange\datas\OB.java'
audio_map_path = r'c:\Users\Josh\StudioProjects\ChantLouange\app\src\main\res\raw\audio_map.json'

print("Loading data...")
cc_songs = extract_songs(cc_path)
cs_songs = extract_songs(cs_path)
ob_songs = extract_songs(ob_path)

cs_by_title = {normalize_title(s['title']): s for s in cs_songs}
ob_by_title = {normalize_title(s['title']): s for s in ob_songs}
cs_titles = list(cs_by_title.keys())
ob_titles = list(ob_by_title.keys())

with open(audio_map_path, 'r', encoding='utf-8') as f:
    audio_map = json.load(f)

updated_count = 0
total_links = 0

for cc in cc_songs:
    possible_keys = set()
    norm_cc = normalize_title(cc['title'])
    
    # 1. Reference-based matches
    for b_id, num in cc['refs']:
        possible_keys.add(f"{b_id}_{num}")
        # Transitive CS <-> OB
        if b_id == "8": possible_keys.add(f"7_{num}")
        if b_id == "7": possible_keys.add(f"8_{num}")

    # 2. Title-based matches
    if norm_cc:
        if norm_cc in cs_by_title:
            s = cs_by_title[norm_cc]
            possible_keys.add(s['key'])
            possible_keys.add(f"7_{s['number']}") # Try OB counterpart
        elif norm_cc in ob_by_title:
            s = ob_by_title[norm_cc]
            possible_keys.add(s['key'])
            possible_keys.add(f"8_{s['number']}") # Try CS counterpart
        else:
            # Fuzzy
            matches = difflib.get_close_matches(norm_cc, cs_titles, n=1, cutoff=0.92)
            if matches:
                s = cs_by_title[matches[0]]
                possible_keys.add(s['key'])
                possible_keys.add(f"7_{s['number']}")

    # Collect audio
    links = []
    for k in possible_keys:
        if k in audio_map:
            for link in audio_map[k]:
                if link not in links:
                    links.append(link)
    
    if links:
        target_key = cc['key']
        existing = audio_map.get(target_key, [])
        new_links = list(set(existing + links))
        if len(new_links) > len(existing):
            audio_map[target_key] = new_links
            updated_count += 1
            total_links += len(links)

print(f"Updated {updated_count} CC songs with {total_links} total audio links.")

with open(audio_map_path, 'w', encoding='utf-8') as f:
    json.dump(audio_map, f, indent=2, ensure_ascii=False)

print("Done.")
