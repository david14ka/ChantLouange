import re

def check_85():
    file_path = 'app/src/main/java/com/davidkazad/chantlouange/datas/CC.java'
    with open(file_path, 'r', encoding='utf-8', errors='ignore') as f:
        content = f.read()

    m85 = re.search(r'\"85\..*?\",\s*\".*?\",\s*\"(.*?)\"', content, re.DOTALL)
    if m85:
        print("--- SONG 85 CONTENT ---")
        print(repr(m85.group(1)))

if __name__ == "__main__":
    check_85()
