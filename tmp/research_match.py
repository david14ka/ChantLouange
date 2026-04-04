import json
import re
import os
from difflib import SequenceMatcher

def normalize(s):
    if not s:
        return ""
    # Remove musical keys at the end (e.g., . Ab, . C, etc.)
    s = re.sub(r'\.\s*[A-G](b|#)?\s*$', '', s, flags=re.IGNORECASE)
    # Remove leading numbers (e.g., 1. , 1A. )
    s = re.sub(r'^\d+[A-Z]?\.\s*', '', s)
    # Lowercase
    s = s.lower()
    # Remove accents/non-ascii
    import unicodedata
    s = unicodedata.normalize('NFKD', s).encode('ASCII', 'ignore').decode('ASCII')
    # Remove punctuation
    s = re.sub(r'[^a-z0-9\s]', '', s)
    # Remove extra spaces
    s = re.sub(r'\s+', ' ', s).strip()
    return s

def extract_from_java(file_path, book_id):
    songs = []
    if not os.path.exists(file_path):
        print(f"File not found: {file_path}")
        return songs

    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
        
    # Match: new Page(number, "display", "title", "lyrics", ...)
    # The title is the 3rd argument.
    matches = re.finditer(r'new\s+Page\(\s*(\d+)\s*,\s*"[^"]*"\s*,\s*"([^"]*)"', content)
    for m in matches:
        number = m.group(1)
        title = m.group(2)
        songs.append({
            "book_id": book_id,
            "number": number,
            "title": title,
            "normalized_title": normalize(title)
        })
    return songs

def main():
    java_files = [
        ("app/src/main/java/com/davidkazad/chantlouange/datas/CC.java", 1),
        ("app/src/main/java/com/davidkazad/chantlouange/datas/CV.java", 2),
        ("app/src/main/java/com/davidkazad/chantlouange/datas/OB.java", 7),
        ("app/src/main/java/com/davidkazad/chantlouange/datas/CS.java", 8)
    ]

    app_songs = []
    for path, book_id in java_files:
        path = os.path.join("c:\\Users\\Josh\\StudioProjects\\ChantLouange", path)
        app_songs.extend(extract_from_java(path, book_id))

    print(f"Extracted {len(app_songs)} songs from Java files.")

    audio_db_path = "c:\\Users\\Josh\\StudioProjects\\ChantLouange\\cantiquest_songs_with_audio.json"
    with open(audio_db_path, 'r', encoding='latin-1') as f: # Try latin-1 due to encoding issues seen
        audio_db = json.load(f)

    for item in audio_db:
        item["normalized_title"] = normalize(item["title"])

    matches_found = []
    for app_song in app_songs:
        best_match = None
        best_score = 0
        
        # Optimization: first check for exact normalized match
        # (could use a dict for this)
        
        for audio_song in audio_db:
            score = SequenceMatcher(None, app_song["normalized_title"], audio_song["normalized_title"]).ratio()
            if score > best_score:
                best_score = score
                best_match = audio_song
            if score == 1.0:
                break
        
        if best_score > 0.85: # Threshold for matching
            matches_found.append({
                "app_song": app_song,
                "audio_match": best_match,
                "score": best_score
            })

    output_path = "c:\\Users\\Josh\\StudioProjects\\ChantLouange\\matches_report.json"
    with open(output_path, 'w', encoding='utf-8') as f:
        json.dump(matches_found, f, indent=2)

    print(f"Found {len(matches_found)} matches. Report saved to {output_path}")

if __name__ == "__main__":
    main()
