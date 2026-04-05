import re

def investigate_fails():
    file_path = 'app/src/main/java/com/davidkazad/chantlouange/datas/CC.java'
    with open(file_path, 'r', encoding='latin-1') as f:
        content = f.read()

    # Investigation 1: Why 71A didn't split?
    # Match 71A Page
    m71a = re.search(r'pg\.add\(new Page\(\s*\d+\s*,\s*\"71A\..*?\",\s*\".*?\",\s*\"(.*?)\"', content, re.DOTALL)
    if m71a:
        text = m71a.group(1)
        print(f"--- 71A TEXT (first 200 chars) ---")
        print(repr(text[:200]))
        # Test my regex locally on this string
        header_pattern = r'\\n(\d{2,}[A-Z]?\.\s*(?:\([a-z]\)\s*)?[A-ZÄ-ÿ\d\s\!\'\-,\?\(\)\.\:]{5,})(?=\\n|$)'
        splits = re.split(header_pattern, text)
        print(f"Splits count: {len(splits)}")

    # Investigation 2: Why no Refrains?
    # Let's search for "refrain" or "choeur" in any form and print the bytes
    matches = re.finditer(r'ref|cho|ch.ur', content, re.IGNORECASE)
    print("\n--- REFRAIN SAMPLES (HEX) ---")
    count = 0
    for m in matches:
        snippet = content[m.start()-10 : m.end()+10]
        hex_vals = [hex(ord(c)) for c in snippet]
        print(f"Snippet: {repr(snippet)} | Hex: {hex_vals}")
        count += 1
        if count > 10: break

if __name__ == "__main__":
    investigate_fails()
