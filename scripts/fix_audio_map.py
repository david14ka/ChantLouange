import json
import re
import difflib

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
            
            # Find references like "VAM 123" or "CJ 456"
            # Common patterns: CCC 123, VAM 123, CJ 123, etc.
            refs = re.findall(r'\b([A-Z]{2,3})\s+(\d+)\b', text)
            songs.append({
                'id': song_id, 
                'title': title.strip(), 
                'text': text, 
                'refs': refs
            })
    return songs

def clean_title(t):
    return re.sub(r'[^a-zA-Z0-9]', '', t).lower()

def score_match(t1, t2):
    return difflib.SequenceMatcher(None, clean_title(t1), clean_title(t2)).ratio()

def main():
    songs = extract_songs()
    
    # Load Cantiquest DB
    with open('cantiquest_songs_with_audio.json', 'r', encoding='utf-8', errors='ignore') as f:
        audio_db = json.load(f)
        
    # Load Current Map
    map_path = 'app/src/main/res/raw/audio_map.json'
    with open(map_path, 'r', encoding='utf-8') as f:
        audio_map = json.load(f)

    # Book ID 1 is CC
    new_cc_map = {}
    corrections_made = 0
    total_found = 0

    for s in songs:
        sid = s['id']
        key = f"1_{sid}"
        title = s['title']
        refs = s['refs']
        
        best_links = []
        found_source = ""

        # 1. Try to find by reference (e.g. VAM 123)
        for book, num in refs:
            # Normalize book names to Cantiquest database
            # SAF/SSS -> SSO? NM -> ? HP -> ? 
            # Let's just try direct match in DB
            for entry in audio_db:
                if entry['book'].upper() == book.upper() and str(entry['number']) == num:
                    links = []
                    if entry.get('instru_midi'): links.append(entry['instru_midi'])
                    if entry.get('piano_midi'): links.append(entry['piano_midi'])
                    if links:
                        best_links = links
                        found_source = f"{book} {num}"
                        break
            if best_links: break

        # 2. If not found by reference, try by title fuzzy match
        if not best_links:
            top_score = 0
            for entry in audio_db:
                e_title = entry.get('title', '')
                score = score_match(title, e_title)
                if score > 0.9 and score > top_score:
                    links = []
                    if entry.get('instru_midi'): links.append(entry['instru_midi'])
                    if entry.get('piano_midi'): links.append(entry['piano_midi'])
                    if links:
                        best_links = links
                        top_score = score
                        found_source = f"Title Match ({score:.2f})"

        # 3. If still not found, check existing map for validation
        if not best_links:
            # If current map has urls, keep them if they look okay, otherwise flag
            current_urls = audio_map.get(key, [])
            if current_urls:
                # Validate current
                # (Simple check: is there a number mismatch in the URL?)
                best_links = current_urls
                found_source = "Kept Current"

        if best_links:
            new_cc_map[key] = best_links
            total_found += 1
            if found_source.startswith("Title") or found_source != "Kept Current":
                corrections_made += 1

    # Update audio_map.json
    # Keep other books (2_, 3_, 4_)
    final_map = {k: v for k, v in audio_map.items() if not k.startswith('1_')}
    final_map.update(new_cc_map)
    # Sort keys for cleanliness
    sorted_map = dict(sorted(final_map.items(), key=lambda item: (item[0].split('_')[0], item[0].split('_')[1])))

    with open(map_path, 'w', encoding='utf-8') as f:
        json.dump(sorted_map, f, indent=2)

    print(f"Total CC Songs: {len(songs)}")
    print(f"Audio found: {total_found}")
    print(f"Proposed corrections: {corrections_made}")

if __name__ == "__main__":
    main()
