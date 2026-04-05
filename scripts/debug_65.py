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
    s = re.sub(r'^\d+[a-z]?\s*', '', s)
    return s.strip()

def debug_65():
    with open('cantiquest_songs_with_audio.json', 'r', encoding='utf-8', errors='ignore') as f:
        audio_db = json.load(f)
    
    title = "DIEU TOUT PUISSANT"
    norm_title = clean_for_match(title)
    print(f"Norm Title: '{norm_title}'")
    
    for entry in audio_db:
        db_title = clean_for_match(entry.get('title', ''))
        score = difflib.SequenceMatcher(None, norm_title, db_title).ratio()
        if "dieu tout puissant" in db_title or score > 0.8:
            print(f"Match: {entry['book']} {entry['number']} | Title: {entry['title']} | Score: {score:.2f}")

if __name__ == "__main__":
    debug_65()
