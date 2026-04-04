import json
import re
import os
import difflib

def build_sss_map():
    sss_to_title = {}
    dump_path = "all_songs_dump.json"
    if not os.path.exists(dump_path):
        return sss_to_title
        
    with open(dump_path, "r", encoding="utf-8") as f:
        data = json.load(f)
        
    # Book 9 (Bb) is likely English hymns
    for song in data:
        if song.get('bookId') != '9': continue
        
        content = song.get('content', '')
        title = song.get('title', '')
        
        # Search for SSS ### in title or content
        m = re.search(r'SSS\s*(\d+)', title + " " + content)
        if m:
            sss_num = m.group(1)
            # Find the actual english title. Often it's the first line in quotes.
            content_lines = content.split('\\n')
            english_title = None
            for line in content_lines:
                if '"' in line:
                    english_title = line.split('"')[1]
                    break
            if not english_title and len(title) > 5 and not title.startswith('CH. '):
                english_title = title
            
            if english_title:
                sss_to_title[sss_num] = english_title
                
    return sss_to_title

def match_cc_via_sss():
    sss_map = build_sss_map()
    print(f"Built SSS map with {len(sss_map)} entries.")
    
    # Load raw library
    with open("hymnal_library_raw_partial.json", "r", encoding="utf-8") as f:
        raw_data = json.load(f)
        raw_by_title = {item['title'].lower(): item['midi_url'] for item in raw_data}

    # Parse CC.java for codes
    cc_path = "app/src/main/java/com/davidkazad/chantlouange/datas/CC.java"
    with open(cc_path, "r", encoding="utf-8") as f:
        cc_content = f.read()
    
    pattern = r'new Page\s*\(\s*\d+\s*,\s*"([^"]+)"\s*,\s*"([^"]+)"\s*,\s*"(.*?)"\s*,\s*1\s*\)'
    matches = []
    
    for m in re.finditer(pattern, cc_content, re.DOTALL):
        number = m.group(1).strip().rstrip(".")
        title = m.group(2).strip()
        meta = m.group(3)
        
        # Search for SSS index in meta
        sm = re.search(r'SSS\s*(\d+)', meta)
        if sm:
            sss_num = sm.group(1)
            if sss_num in sss_map:
                eng_title = sss_map[sss_num]
                # Look up this eng_title in raw_by_title
                audio_url = None
                if eng_title.lower() in raw_by_title:
                    audio_url = raw_by_title[eng_title.lower()]
                else:
                    # Fuzzy match
                    f_matches = difflib.get_close_matches(eng_title.lower(), list(raw_by_title.keys()), n=1, cutoff=0.75)
                    if f_matches:
                        audio_url = raw_by_title[f_matches[0]]
                
                if audio_url:
                    matches.append({
                        "number": number,
                        "title": title,
                        "english": eng_title,
                        "piano_midi": audio_url,
                        "instru_midi": audio_url
                    })
                    
    with open("hymnallibrary_cc_audio_sss.json", "w", encoding="utf-8") as f:
        json.dump(matches, f, indent=2, ensure_ascii=False)
        
    print(f"Matched {len(matches)} CC songs via SSS mapping.")

if __name__ == "__main__":
    match_cc_via_sss()
