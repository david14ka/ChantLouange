import json
import unicodedata

def normalize(s):
    return ''.join(c for c in unicodedata.normalize('NFD', s)
                  if unicodedata.category(c) != 'Mn').lower()

def search_db():
    with open('cantiquest_songs_with_audio.json', 'r', encoding='utf-8', errors='ignore') as f:
        db = json.load(f)
    
    target = normalize("Dieu tout puissant")
    for s in db:
        title = normalize(s.get('title', ''))
        if target in title:
            print(f"{s['book']} {s['number']}: {s['title']}")
            print(f"Piano: {s.get('piano_midi')}")
            print(f"Instru: {s.get('instru_midi')}")

if __name__ == "__main__":
    search_db()
