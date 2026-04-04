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

def parse_cc_java():
    cc_path = "app/src/main/java/com/davidkazad/chantlouange/datas/CC.java"
    songs = []
    if not os.path.exists(cc_path):
        return songs
        
    with open(cc_path, "r", encoding="utf-8") as f:
        content = f.read()
        
    # Match Page(id, indicator, title, metadata, bookId)
    pattern = r'new Page\s*\(\s*\d+\s*,\s*"([^"]+)"\s*,\s*"([^"]+)"\s*,\s*"(.*?)"\s*,\s*1\s*\)'
    matches = re.finditer(pattern, content, re.DOTALL)
    
    for m in matches:
        songs.append({
            "number": m.group(1).strip().rstrip("."),
            "title": m.group(2).strip(),
            "meta": m.group(3).strip(),
            "book": "CC"
        })
    return songs

def match_and_generate():
    # 1. Load OB matched results
    ob_results_path = "hymnallibrary_ob_audio.json"
    if not os.path.exists(ob_results_path):
        print("Error: OB audio results not found.")
        return
        
    with open(ob_results_path, "r", encoding="utf-8") as f:
        ob_results = json.load(f)
        
    ob_by_norm_title = {normalize(item['title']): item['piano_midi'] for item in ob_results}
    
    # 2. Also load raw library for direct matches
    raw_path = "hymnal_library_raw_partial.json"
    raw_by_norm = {}
    if os.path.exists(raw_path):
        with open(raw_path, "r", encoding="utf-8") as f:
            raw_data = json.load(f)
            for item in raw_data:
                raw_by_norm[normalize(item['title'])] = item['midi_url']

    cc_songs = parse_cc_java()
    matched_results = []
    
    print(f"Working on {len(cc_songs)} CC songs...")
    
    for song in cc_songs:
        nt = normalize(song['title'])
        meta = normalize(song['meta'])
        
        audio_url = None
        
        # Strategy A: Exact title match in OB
        if nt in ob_by_norm_title:
            audio_url = ob_by_norm_title[nt]
            
        # Strategy B: Exact title match in raw library
        elif nt in raw_by_norm:
            audio_url = raw_by_norm[nt]
            
        # Strategy C: Look for English title in metadata/quotes
        # Sometimes metadata contains "ENGLISH TITLE"
        # Example meta: "SSS 237 ; Amazing Grace" (approximate)
        # Search for any raw library title within the meta string
        if not audio_url:
            for rt_norm, url in raw_by_norm.items():
                if len(rt_norm) > 10 and rt_norm in meta:
                    audio_url = url
                    break
        
        # Strategy D: Fuzzy title match
        if not audio_url:
            scraped_titles = list(raw_by_norm.keys())
            matches = difflib.get_close_matches(nt, scraped_titles, n=1, cutoff=0.7)
            if matches:
                audio_url = raw_by_norm[matches[0]]
                
        if audio_url:
            song['piano_midi'] = audio_url
            song['instru_midi'] = audio_url
            matched_results.append(song)
            
    output_path = "hymnallibrary_cc_audio.json"
    with open(output_path, "w", encoding="utf-8") as f:
        json.dump(matched_results, f, indent=2, ensure_ascii=False)
        
    print(f"Matched {len(matched_results)} CC songs.")

if __name__ == "__main__":
    match_and_generate()
