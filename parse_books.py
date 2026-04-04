import os
import re

datas_dir = "app/src/main/java/com/davidkazad/chantlouange/datas"
books = {}
for fname in os.listdir(datas_dir):
    if fname.endswith(".java"):
        with open(os.path.join(datas_dir, fname), "r", encoding="utf-8") as f:
            content = f.read()
            m = re.search(r'super\(\s*(\d+)\s*,\s*"[^"]+"\s*,\s*"([^"]+)"', content)
            if m:
                books[m.group(1)] = m.group(2)
            else:
                # Some might use variables or have no abbreviation
                pass

print(books)
