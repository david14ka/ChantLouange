import json
import re
import difflib
import unicodedata

def normalize(s):
    if not s: return ""
    return ''.join(c for c in unicodedata.normalize('NFD', s)
                  if unicodedata.category(c) != 'Mn').lower().strip()

def clean_for_match(s):
    s = normalize(s)
    s = re.sub(r'[^a-z0-9\s]', '', s)
    # Remove numbers like "1A", "1B" etc from start if present
    s = re.sub(r'^\d+[a-z]?\s*', '', s)
    return s.strip()

def extract_songs():
    file_path = 'app/src/main/java/com/davidkazad/chantlouange/datas/CC.java'
    with open(file_path, 'r', encoding='utf-8', errors='ignore') as f:
        content = f.read()

    pages = re.findall(r'pg\.add\(new Page\(\s*\d+\s*,\s*\"(.*?)\"\s*,\s*\"(.*?)\"\s*,\s*\"(.*?)(?<!\\)\"\s*,\s*1\)\);', content, re.DOTALL)
    
    songs = []
    for num_str, title, text in pages:
        m = re.search(r'(\d+)([A-Z]?)', num_str)
        if m:
            base_num = m.group(1)
            suffix = m.group(2).lower() if m.group(2) else ""
            song_id = f"{base_num}{suffix}"
            
            # Find references
            refs = re.findall(r'\b([A-Z]{2,3})\s+(\d+)\b', text)
            songs.append({
                'id': song_id, 
                'title': title.strip(), 
                'text': text, 
                'refs': refs
            })
    return songs

# Mapping of book abbreviations in CC to Cantiquest DB
BOOK_MAP = {
    'SSS': ['SSO', 'SSS'],
    'SAF': ['SSO', 'SSS', 'CJ'],
    'VAM': ['VAM'],
    'CJ': ['CJ'],
    'CV': ['CV'],
    'HEC': ['HeC'],
    'CCS': ['HeC', 'VAM', 'CJ'],
    'NM': ['NM'],
    'HL': ['HL'],
    'CH': ['CH'],
    'IH': ['IH'],
}

def is_similar(t1, t2):
    t1 = clean_for_match(t1)
    t2 = clean_for_match(t2)
    if not t1 or not t2: return False
    
    # 1. Exact match
    if t1 == t2: return True
    # 2. One starts with the other
    if len(t1) > 5 and len(t2) > 5:
        if t1.startswith(t2) or t2.startswith(t1): return True
    # 3. Fuzzy match
    score = difflib.SequenceMatcher(None, t1, t2).ratio()
    return score > 0.85

def main():
    songs = extract_songs()
    with open('cantiquest_songs_with_audio.json', 'r', encoding='utf-8', errors='ignore') as f:
        audio_db = json.load(f)
    with open('app/src/main/res/raw/audio_map.json', 'r', encoding='utf-8') as f:
        audio_map = json.load(f)

    new_cc_map = {}
    updates = []

    for s in songs:
        sid = s['id']
        key = f"1_{sid}"
        title = s['title']
        refs = s['refs']
        
        found_links = None
        method = ""

        # Strategy 1: Check references in text
        for ref_book, ref_num in refs:
            possible_books = BOOK_MAP.get(ref_book.upper(), [ref_book.upper()])
            for entry in audio_db:
                if entry.get('book', '').upper() in possible_books and str(entry.get('number')) == ref_num:
                    if is_similar(title, entry.get('title', '')):
                        found_links = []
                        if entry.get('instru_midi'): found_links.append(entry['instru_midi'])
                        if entry.get('piano_midi'): found_links.append(entry['piano_midi'])
                        if found_links:
                            method = f"Ref {ref_book} {ref_num}"
                            break
            if found_links: break

        # Strategy 2: High score title match
        if not found_links:
            best_score = 0
            for entry in audio_db:
                db_t = entry.get('title', '')
                if not db_t: continue
                # Custom similarity
                t1 = clean_for_match(title)
                t2 = clean_for_match(db_t)
                score = 0
                if t1 == t2: score = 1.0
                elif len(t1) > 5 and (t1.startswith(t2) or t2.startswith(t1)): score = 0.95
                else: score = difflib.SequenceMatcher(None, t1, t2).ratio()
                
                if score > 0.85 and score > best_score:
                    links = []
                    if entry.get('instru_midi'): links.append(entry['instru_midi'])
                    if entry.get('piano_midi'): links.append(entry['piano_midi'])
                    if links:
                        best_score = score
                        found_links = links
                        method = f"Title ({score:.2f})"

        current_urls = audio_map.get(key, [])
        is_generic = current_urls and any(re.search(r'/\d+\.mid$', url) for url in current_urls)
        
        if found_links:
            # If current is generic 464.mid OR we have a very strong ref match different from current
            if not current_urls or is_generic or (method.startswith("Ref") and found_links != current_urls):
                new_cc_map[key] = found_links
                updates.append(f"{sid}: '{title}' -> {method}")
            else:
                new_cc_map[key] = current_urls
        elif current_urls:
            new_cc_map[key] = current_urls

    # Re-assemble
    final_map = {k: v for k, v in audio_map.items() if not k.startswith('1_')}
    final_map.update(new_cc_map)
    sorted_map = dict(sorted(final_map.items(), key=lambda x: (int(x[0].split('_')[0]), x[0].split('_')[1])))

    with open('app/src/main/res/raw/audio_map.json', 'w', encoding='utf-8') as f:
        json.dump(sorted_map, f, indent=2)

    print(f"Applied {len(updates)} updates.")
    for u in updates[:20]:
        print(f"  {u}")
    if len(updates) > 20: print(f"  ... and {len(updates)-20} more.")

if __name__ == "__main__":
    main()
