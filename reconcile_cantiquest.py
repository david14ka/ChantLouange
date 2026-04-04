import json
import re
import os

def extract_markers(content):
    # Match patterns like CV 112, NM 56, SAF 239, etc.
    return re.findall(r'\b([A-Za-z]{2,4})\s+(\d+)\b', content)

def normalize_text(text):
    if not text: return ""
    # Lowercase, remove accents, remove punctuation
    import unicodedata
    text = "".join(c for c in unicodedata.normalize('NFD', text) if unicodedata.category(c) != 'Mn')
    text = text.lower()
    text = re.sub(r'[^\w\s]', '', text)
    return " ".join(text.split())

def main():
    print("Loading data...")
    try:
        with open('GeneratedCC.json', 'r', encoding='utf-8') as f:
            cc_data = json.load(f)
        
        with open('cantiquest_songs_with_audio.json', 'r', encoding='utf-8') as f:
            cantiquest_data = json.load(f)
            
        with open('app/src/main/java/com/davidkazad/chantlouange/utils/AudioMapper.java', 'r', encoding='utf-8') as f:
            java_content = f.read()
    except FileNotFoundError as e:
        print(f"Error: {e}")
        return

    # Extract existing CV map from Java
    existing_cv_map = {}
    base_url = "https://cantiques.yapper.fr/media/"
    # Extract lines like: CV_AUDIO_MAP.put("5", new String[]{"hymns/mp3/CV/CV_005-Jesus_Jesus_polyinstru.mp3", ...});
    matches = re.finditer(r'CV_AUDIO_MAP\.put\("([^"]+)",\s+new\s+String\[\]\{([^}]+)\}\);', java_content)
    for m in matches:
        num = m.group(1).lower()
        paths = [p.strip().strip('"') for p in m.group(2).split(',')]
        existing_cv_map[num] = [base_url + p for p in paths]

    # Index cantiquest data by book and number
    cq_by_marker = {}
    cq_by_title = {}
    for song in cantiquest_data:
        # Marker index
        key = (song['book'].upper(), song['number'])
        cq_by_marker[key] = song
        # Title index
        norm_title = normalize_text(song['title'])
        if norm_title not in cq_by_title:
            cq_by_title[norm_title] = []
        cq_by_title[norm_title].append(song)
        
    unified_mapping = {}
    
    # Book name normalization map (App -> Cantiquest)
    book_map = {
        'HC': 'HEC',
        'HEC': 'HEC',
        'CJ': 'CJ',
        'CV': 'CV',
        'CGG': 'CGG1',
        'SST': 'SST',
        'CPE': 'CPE',
        'TEJ': 'TEJ'
    }

    print("Reconciling...")
    matches_count = 0
    for song in cc_data['songs']:
        cc_number = song['number'].strip().rstrip('.').strip().lower() # e.g. "1a", "2"
        content = song['content']
        cc_title = normalize_text(song['title'])
        
        links = []
        
        # 1. Existing CV MP3s (if applicable)
        if cc_number in existing_cv_map:
            links.extend(existing_cv_map[cc_number])

        # 2. Extract markers from content
        markers = extract_markers(content)
        for book, num in markers:
            target_book = book_map.get(book.upper(), book.upper())
            if (target_book, num) in cq_by_marker:
                cq_song = cq_by_marker[(target_book, num)]
                if cq_song['piano_midi']: links.append(cq_song['piano_midi'])
                if cq_song['instru_midi']: links.append(cq_song['instru_midi'])

        # 3. Fuzzy title match (if still no links)
        if not links:
            if cc_title in cq_by_title:
                for cq_song in cq_by_title[cc_title]:
                    if cq_song['piano_midi']: links.append(cq_song['piano_midi'])
                    if cq_song['instru_midi']: links.append(cq_song['instru_midi'])

        if links:
            unified_mapping[cc_number] = list(set(links))
            matches_count += 1

    # Save consolidated mapping
    output_file = 'unified_audio_map.json'
    with open(output_file, 'w', encoding='utf-8') as f:
        json.dump(unified_mapping, f, indent=2, ensure_ascii=False)
    
    print(f"Reconciliation complete. Mapped {matches_count} songs out of {len(cc_data['songs'])}")
    print(f"Output saved to {output_file}")

if __name__ == "__main__":
    main()
