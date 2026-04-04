import json
import re
import os
import unicodedata

def normalize(text):
    if not text: return ""
    text = re.sub(r"\s+\([A-G][b#]?\)\s*$", "", text, flags=re.IGNORECASE)
    nfd = unicodedata.normalize("NFD", text)
    s = "".join(c for c in nfd if unicodedata.category(c) != "Mn")
    res = " ".join(re.sub(r"[^\w\s]", "", s.lower()).split())
    return res

def update_cc_audio():
    audio_map_path = "app/src/main/res/raw/audio_map.json"
    if os.path.exists(audio_map_path):
        with open(audio_map_path, "r", encoding="utf-8") as f:
            audio_map = json.load(f)
    else:
        audio_map = {}

    # 1. Load CC songs
    cc_path = "app/src/main/java/com/davidkazad/chantlouange/datas/CC.java"
    with open(cc_path, "r", encoding="utf-8") as f:
        cc_content = f.read()
    
    cc_songs = []
    pattern = r'new Page\s*\(\s*\d+\s*,\s*"([^"]+)"\s*,\s*"([^"]+)"\s*,\s*"(.*?)"\s*,\s*1\s*\)'
    for m in re.finditer(pattern, cc_content, re.DOTALL):
        cc_songs.append({
            "number": m.group(1).strip().rstrip("."),
            "title": m.group(2).strip(),
            "meta": m.group(3).strip(),
            "norm": normalize(m.group(2).strip())
        })

    # 2. Load OB matched results (source of truth from English library)
    ob_path = "hymnallibrary_ob_audio.json"
    ob_matches = {}
    if os.path.exists(ob_path):
        with open(ob_path, "r", encoding="utf-8") as f:
            ob_data = json.load(f)
            for item in ob_data:
                ob_matches[normalize(item['title'])] = item['piano_midi']

    # 3. Load direct CC matches
    cc_match_path = "hymnallibrary_cc_audio.json"
    cc_direct_matches = {}
    if os.path.exists(cc_match_path):
        with open(cc_match_path, "r", encoding="utf-8") as f:
            cc_data = json.load(f)
            for item in cc_data:
                cc_direct_matches[item['number']] = item['piano_midi']

    updated_count = 0
    # 4. Apply updates
    for song in cc_songs:
        key = f"1_{song['number']}"
        existing = set(audio_map.get(key, []))
        
        audio_url = None
        
        # Priority 1: Direct match (already found)
        if song['number'] in cc_direct_matches:
            audio_url = cc_direct_matches[song['number']]
        
        # Priority 2: Title match in OB results
        elif song['norm'] in ob_matches:
            audio_url = ob_matches[song['norm']]
            
        if audio_url:
            if audio_url not in existing:
                existing.add(audio_url)
                audio_map[key] = sorted(list(existing))
                updated_count += 1
                
    # 5. Save updated map
    with open(audio_map_path, "w", encoding="utf-8") as f:
        json.dump(audio_map, f, indent=2, ensure_ascii=False)
    
    print(f"Updated {updated_count} CC audio entries in audio_map.json.")

if __name__ == "__main__":
    update_cc_audio()
