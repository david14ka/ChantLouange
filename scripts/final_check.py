import re

def final_check():
    file_path = 'app/src/main/java/com/davidkazad/chantlouange/datas/CC.java'
    with open(file_path, 'r', encoding='utf-8', errors='ignore') as f:
        content = f.read()

    m309 = re.search(r'\"309\..*?\",\s*\".*?\",\s*\"(.*?)\"', content, re.DOTALL)
    if m309:
        print("--- SONG 309 CONTENT ---")
        print(m309.group(1).replace('\\n', '\n'))
        
    m86 = re.search(r'\"86\..*?\",\s*\".*?\",\s*\"(.*?)\"', content, re.DOTALL)
    if m86:
        print("\n--- SONG 86 CONTENT ---")
        print(m86.group(1).replace('\\n', '\n'))

if __name__ == "__main__":
    final_check()
