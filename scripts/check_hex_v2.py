import re

def check_hex_v2():
    file_path = 'app/src/main/java/com/davidkazad/chantlouange/datas/CC.java'
    # Use latin-1 to see ALL bytes
    with open(file_path, 'r', encoding='latin-1') as f:
        content = f.read()

    # Look for "choeur" or "refrain"
    # In my previous grep it was: `cèlèbrent en Chœur`
    # Let's search for "Ch" then some non-ascii chars then "ur"
    matches = re.finditer(r'Ch.ur|Refrain|ch.ur', content, re.IGNORECASE)
    for m in matches:
        snippet = m.group(0)
        # print hex of snippet
        hex_vals = [hex(ord(c)) for c in snippet]
        print(f"Match: {snippet} | Hex: {hex_vals} | Context: {content[m.start()-10:m.end()+10]}")

if __name__ == "__main__":
    check_hex_v2()
