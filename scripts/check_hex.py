import re

def check_hex():
    file_path = 'app/src/main/java/com/davidkazad/chantlouange/datas/CC.java'
    with open(file_path, 'r', encoding='utf-8', errors='ignore') as f:
        content = f.read()

    # Look for Song 99 stanzas with "Choeur" or similar
    m = re.search(r'99\..*?\",\s*\"(.*?)\"', content, re.DOTALL)
    if m:
        text = m.group(1)
        # Find where "Choeur" or similar is
        # Actually in 99 it was "cèlèbrent en Chœur"
        # Let's find any word with \u0152 (œ) or non-ascii
        for i, char in enumerate(text):
            if ord(char) > 127:
                print(f"Char at {i}: {char} (Hex: {hex(ord(char))}) | Context: {text[max(0, i-10):i+10]}")

if __name__ == "__main__":
    check_hex()
