import json
import re

def main():
    with open('cantiquest_songs_with_audio.json', 'r', encoding='utf-8', errors='ignore') as f:
        db = json.load(f)
    
    clean_db = []
    removed = 0
    for entry in db:
        title = entry.get('title', '')
        # If title is just digits or very short
        if re.match(r'^\d+$', title) or len(title) < 3:
            removed += 1
            continue
        # Also check for the specific long number string
        if len(title) > 50 and re.match(r'^\d+$', title):
            removed += 1
            continue
        clean_db.append(entry)
        
    print(f"Removed {removed} corrupted entries. Original: {len(db)}, Clean: {len(clean_db)}")
    
    with open('cantiquest_db_clean.json', 'w', encoding='utf-8') as f:
        json.dump(clean_db, f, indent=2)

if __name__ == "__main__":
    main()
