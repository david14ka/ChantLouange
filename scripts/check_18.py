import re

def check_18():
    file_path = 'app/src/main/java/com/davidkazad/chantlouange/datas/CC.java'
    with open(file_path, 'r', encoding='utf-8', errors='ignore') as f:
        content = f.read()

    m18 = re.search(r'\"18\..*?\",\s*\".*?\",\s*\"(.*?)\"', content, re.DOTALL)
    if m18:
        print("--- SONG 18 CONTENT ---")
        print(repr(m18.group(1)))

if __name__ == "__main__":
    check_18()
