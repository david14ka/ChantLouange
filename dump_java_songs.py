import os
import re
import json

datas_dir = "app/src/main/java/com/davidkazad/chantlouange/datas"
songs = []

for fname in os.listdir(datas_dir):
    if fname.endswith(".java"):
        with open(os.path.join(datas_dir, fname), "r", encoding="utf-8") as f:
            content = f.read()
            m = re.search(r'super\(\s*(\d+)\s*,\s*"[^"]+"\s*,\s*"([^"]+)"', content)
            if not m: continue
            book_id = m.group(1)
            book_abbr = m.group(2)
            
            pages = re.findall(r'new Page\(\s*\d+\s*,\s*"([^"]+)"\s*,\s*"([^"]+)"\s*,\s*"(.*?)"\s*,\s*\d+\s*\)', content, re.DOTALL)
            for p in pages:
                songs.append({
                    "bookId": book_id,
                    "bookAbbr": book_abbr,
                    "number": p[0],
                    "title": p[1],
                    "content": p[2]
                })

print(f"Extracted {len(songs)} songs.")
with open("all_songs_dump.json", "w", encoding="utf-8") as f:
    json.dump(songs, f, indent=2, ensure_ascii=False)
