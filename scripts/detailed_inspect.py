import re

def detailed_inspect():
    file_path = 'app/src/main/java/com/davidkazad/chantlouange/datas/CC.java'
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()

    # Song 557 (Still Fused)
    m557 = re.search(r'new Page\(\s*\d+\s*,\s*\"557\..*?\",\s*\".*?\",\s*\"(.*?)\"', content, re.DOTALL)
    if m557:
        print("--- SONG 557 CONTENT ---")
        print(m557.group(1).replace('\\n', '\n'))
        print("-" * 30)

    # Song 99 (Refrain Fix)
    m99 = re.search(r'new Page\(\s*\d+\s*,\s*\"99\..*?\",\s*\".*?\",\s*\"(.*?)\"', content, re.DOTALL)
    if m99:
        print("--- SONG 99 CONTENT ---")
        print(m99.group(1).replace('\\n', '\n'))
        print("-" * 30)

if __name__ == "__main__":
    detailed_inspect()
