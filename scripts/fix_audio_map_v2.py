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
    # Remove punctuation
    s = re.sub(r'[^a-z0-9\s]', '', s)
    # Remove "1A", "1B" etc from start if present
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
    'CCS': ['HeC', 'VAM'],
    'NM': ['NM'],
    'HL': ['HL'],
    'CH': ['CH'],
    'IH': ['IH'],
}

def main():
    songs = extract_songs()
    
    with open('cantiquest_songs_with_audio.json', 'r', encoding='utf-8', errors='ignore') as f:
        audio_db = json.load(f)
        
    map_path = 'app/src/main/res/raw/audio_map.json'
    with open(map_path, 'r', encoding='utf-8') as f:
        audio_map = json.load(f)

    new_cc_map = {}
    updates_count = 0
    total_found = 0

    for s in songs:
        sid = s['id']
        key = f"1_{sid}"
        title = s['title']
        norm_title = clean_for_match(title)
        refs = s['refs']
        
        best_links = []
        best_score = 0
        found_method = ""

        # 1. Strategy A: Reference Match
        for ref_book, ref_num in refs:
            possible_books = BOOK_MAP.get(ref_book.upper(), [ref_book.upper()])
            for entry in audio_db:
                if entry.get('book', '').upper() in possible_books and str(entry.get('number')) == ref_num:
                    # Double check title similarity to avoid wrong book match
                    db_title = clean_for_match(entry.get('title', ''))
                    score = difflib.SequenceMatcher(None, norm_title, db_title).ratio()
                    if score > 0.6: # Relaxed because numbers match
                        links = []
                        if entry.get('instru_midi'): links.append(entry['instru_midi'])
                        if entry.get('piano_midi'): links.append(entry['piano_midi'])
                        if links:
                            best_links = links
                            found_method = f"Ref {ref_book} {ref_num}"
                            break
            if best_links: break

        # 2. Strategy B: Title Fuzzy Match (if no ref match)
        if not best_links:
            for entry in audio_db:
                db_title = clean_for_match(entry.get('title', ''))
                if not db_title: continue
                
                score = difflib.SequenceMatcher(None, norm_title, db_title).ratio()
                if score > 0.85 and score > best_score:
                    links = []
                    if entry.get('instru_midi'): links.append(entry['instru_midi'])
                    if entry.get('piano_midi'): links.append(entry['piano_midi'])
                    if links:
                        best_score = score
                        best_links = links
                        found_method = f"Title ({score:.2f})"

        # 3. Decision
        current_urls = audio_map.get(key, [])
        
        # If we found a good match and it's different from current (or current is broken/missing)
        is_correction = False
        if best_links:
            if not current_urls:
                is_correction = True
            else:
                # Check if current is one of those generic number ones like 464.mid
                is_generic = any(re.search(r'/\d+\.mid$', url) for url in current_urls)
                if is_generic or (found_method.startswith("Ref") and best_links[0] != current_urls[0]):
                    is_correction = True

            if is_correction:
                new_cc_map[key] = best_links
                updates_count += 1
                total_found += 1
            else:
                new_cc_map[key] = current_urls
                total_found += 1
        else:
            if current_urls:
                new_cc_map[key] = current_urls
                total_found += 1

    # Apply changes
    final_map = {k: v for k, v in audio_map.items() if not k.startswith('1_')}
    final_map.update(new_cc_map)
    sorted_map = dict(sorted(final_map.items(), key=lambda item: (int(item[0].split('_')[0]), item[0].split('_')[1])))

    with open(map_path, 'w', encoding='utf-8') as f:
        json.dump(sorted_map, f, indent=2)

    print(f"Total CC Songs: {len(songs)}")
    print(f"Total entries in map: {total_found}")
    print(f"Updates/Corrections applied: {updates_count}")

if __name__ == "__main__":
    main()
