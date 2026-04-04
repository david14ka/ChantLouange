import json
import re
import unicodedata
import difflib
import os

def normalize(text):
    if not text: return ""
    # Strip (KEY) like (C), (Ab), (Eb) etc.
    text = re.sub(r"\s+\([A-G][b#]?\)\s*$", "", text, flags=re.IGNORECASE)
    # Remove accents
    nfd = unicodedata.normalize("NFD", text)
    s = "".join(c for c in nfd if unicodedata.category(c) != "Mn")
    # Lowercase and remove punctuation
    res = " ".join(re.sub(r"[^\w\s]", "", s.lower()).split())
    return res

def parse_ob_java():
    ob_path = "app/src/main/java/com/davidkazad/chantlouange/datas/OB.java"
    songs = []
    if not os.path.exists(ob_path):
        print(f"Error: {ob_path} not found.")
        return songs
        
    with open(ob_path, "r", encoding="utf-8") as f:
        content = f.read()
        
    # Pattern: new Page(id, "number", "title", "content", bookId)
    pattern = r'new Page\s*\(\s*\d+\s*,\s*"([^"]+)"\s*,\s*"([^"]+)"\s*,\s*"(.*?)"\s*,\s*7\s*\)'
    matches = re.finditer(pattern, content, re.DOTALL)
    
    for m in matches:
        num = m.group(1).strip().rstrip(".")
        title = m.group(2).strip()
        songs.append({
            "number": num,
            "title": title,
            "book": "OB"
        })
    return songs

def match_and_generate():
    scraped_path = "hymnal_library_raw_partial.json"
    if not os.path.exists(scraped_path):
        scraped_path = "hymnal_library_raw.json"
        
    if not os.path.exists(scraped_path):
        print("Error: No scraped data found.")
        return
        
    with open(scraped_path, "r", encoding="utf-8") as f:
        scraped_data = json.load(f)
    
    scraped_titles = []
    scraped_by_norm = {}
    for item in scraped_data:
        nt = normalize(item['title'])
        if nt and nt not in scraped_by_norm:
            scraped_titles.append(nt)
            scraped_by_norm[nt] = item['midi_url']
            
    ob_songs = parse_ob_java()
    
    matched_results = []
    for song in ob_songs:
        nt = normalize(song['title'])
        
        # 1. Exact match
        if nt in scraped_by_norm:
            song['piano_midi'] = scraped_by_norm[nt]
            song['instru_midi'] = scraped_by_norm[nt]
            matched_results.append(song)
            continue
            
        # 2. Substring match (if one is a subset of the other)
        found_sub = False
        for st in scraped_titles:
            if nt in st or st in nt:
                # To avoid false positives, check if the smaller one is at least 50% of the larger one
                smaller = min(len(nt), len(st))
                larger = max(len(nt), len(st))
                if smaller > 5 or smaller/larger > 0.4:
                    song['piano_midi'] = scraped_by_norm[st]
                    song['instru_midi'] = scraped_by_norm[st]
                    matched_results.append(song)
                    found_sub = True
                    break
        if found_sub: continue

        # 3. Fuzzy match with lower cutoff
        matches = difflib.get_close_matches(nt, scraped_titles, n=1, cutoff=0.6)
        if matches:
            best_match = matches[0]
            song['piano_midi'] = scraped_by_norm[best_match]
            song['instru_midi'] = scraped_by_norm[best_match]
            matched_results.append(song)
            
    output_path = "hymnallibrary_ob_audio.json"
    actual_output = []
    for m in matched_results:
        actual_output.append({
            "title": m['title'],
            "number": m['number'],
            "book": m['book'],
            "piano_midi": m['piano_midi'],
            "instru_midi": m['instru_midi']
        })
        
    with open(output_path, "w", encoding="utf-8") as f:
        json.dump(actual_output, f, indent=2, ensure_ascii=False)
        
    print(f"Successfully matched {len(matched_results)} out of {len(ob_songs)} songs.")
    print(f"Output saved to {output_path}")

if __name__ == "__main__":
    match_and_generate()
