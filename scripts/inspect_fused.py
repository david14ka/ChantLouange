import re

def inspect_fused():
    file_path = 'app/src/main/java/com/davidkazad/chantlouange/datas/CC.java'
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()

    # Look for the strings of 308, 349, 377, 526, 528, 552
    targets = ["308. ", "349. ", "377. ", "526. ", "528. ", "552. "]
    
    for t in targets:
        # Match pg.add(new Page(ID, "NUMBER. ", "TITLE", "CONTENT", 1));
        pattern = r'new Page\(\s*\d+\s*,\s*\"' + re.escape(t) + r'\",\s*\".*?\",\s*\"(.*?)\",\s*1\)'
        match = re.search(pattern, content, re.DOTALL)
        if match:
            print(f"--- CONTENT FOR {t} ---")
            # Only print a snippet and especially where the next number might be
            text = match.group(1)
            print(text.replace('\\n', '\n'))
            print("-" * 30)

if __name__ == "__main__":
    inspect_fused()
