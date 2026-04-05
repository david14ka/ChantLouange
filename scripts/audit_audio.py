import json
import re
import difflib

def extract_songs():
    file_path = 'app/src/main/java/com/davidkazad/chantlouange/datas/CC.java'
    with open(file_path, 'r', encoding='utf-8', errors='ignore') as f:
        content = f.read()

    # Match Page(id, num, title, text, 1)
    pages = re.findall(r'pg\.add\(new Page\(\s*\d+\s*,\s*\"(.*?)\"\s*,\s*\"(.*?)\"\s*,\s*\"(.*?)(?<!\\)\"\s*,\s*1\)\);', content, re.DOTALL)
    
    songs = []
    for num_str, title, text in pages:
        # Number usually "1.", "1A. ", "2. ", etc.
        m = re.search(r'(\d+)([A-Z]?)', num_str)
        if m:
            base_num = m.group(1)
            suffix = m.group(2).lower() if m.group(2) else ""
            song_id = f"{base_num}{suffix}"
            songs.append({'id': song_id, 'title': title.strip(), 'text': text})
    return songs

def extract_audio_map():
    file_path = 'app/src/main/res/raw/audio_map.json'
    with open(file_path, 'r', encoding='utf-8') as f:
        data = json.load(f)
    # Book 1 is CC
    cc_map = {k[2:]: v for k, v in data.items() if k.startswith('1_')}
    return cc_map

def audit():
    songs = extract_songs()
    audio_map = extract_audio_map()
    
    mismatches = []
    for s in songs:
        sid = s['id']
        title = s['title']
        urls = audio_map.get(sid, [])
        
        if not urls:
            # Check for generic number key (e.g. 1_1 instead of 1_1a)
            # but usually the key is the exact number.
            continue
            
        # Check first URL for title match
        first_url = urls[0]
        # Filename usually ends in -Title.mid
        filename = first_url.split('/')[-1].replace('_', ' ').replace('-', ' ').replace('.mid', '').replace('.mp3', '')
        # Try to find a match
        score = difflib.SequenceMatcher(None, title.lower(), filename.lower()).ratio()
        
        if score < 0.4:
            # Could be a different title in original language?
            # Or a book reference (e.g. CJ 187)
            # Let's check the text of 1A. for references.
            # "O CHRIST, ETERNEL" text: "SAF 239; SSS 237 ; NM 118 ; ..."
            # In audio_map: "CJ_187-mono-piano-O_Christ_eternel_Rocher.mid"
            # Filename "O Christ eternel Rocher" vs "O CHRIST, ETERNEL"
            # This is a match! Just different title length.
            # But let's check for REAL mismatches.
            pass

        # Let's just print a sample of 20 to see.
        mismatches.append((sid, title, filename, score))

    # Sort by score ascending to see the worst
    mismatches.sort(key=lambda x: x[3])
    print(f"Total songs audited: {len(songs)}")
    print(f"Top 20 potential mismatches:")
    for sid, title, filename, score in mismatches[:20]:
        print(f"ID: {sid} | Title: {title} | Audio: {filename} | Score: {score:.2f}")

if __name__ == "__main__":
    audit()
