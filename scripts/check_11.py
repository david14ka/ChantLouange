import re

def check_11():
    file_path = 'app/src/main/java/com/davidkazad/chantlouange/datas/CC.java'
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()

    targets = ['71A. ', '85. ', '90A. ', '116B. ', '201. ', '252. ', '406. ', '409. ', '414. ', '417. ']
    for t in targets:
        m = re.search(r'new Page\(\s*\d+\s*,\s*\"' + re.escape(t) + r'\",\s*\".*?\",\s*\"(.*?)\"', content, re.DOTALL)
        if m:
            text = m.group(1).replace('\\n', '\n')
            fused_matches = re.finditer(r'\n(\d{2,})\.', text)
            for f in fused_matches:
                print(f"Song {t}: Found fused number {f.group(1)} at snippet: {text[f.start():f.start()+100].strip()}")

if __name__ == "__main__":
    check_11()
