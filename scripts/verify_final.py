import re

def verify_v3():
    file_path = 'app/src/main/java/com/davidkazad/chantlouange/datas/CC.java'
    with open(file_path, 'r', encoding='latin-1') as f:
        content = f.read()

    # Get all pages
    pages = re.findall(r'pg\.add\(new Page\(\s*(\d+)\s*,\s*"(.*?)"\s*,\s*"(.*?)"\s*,\s*"(.*?)"\s*,\s*\d+\s*\)\);', content, re.DOTALL)
    
    print(f"--- FINAL VERIFICATION ---")
    print(f"Total Songs: {len(pages)}")

    # 1. Refrain counts
    refrain_count = sum(1 for p in pages if 'Refrain :' in p[3])
    print(f"Songs with 'Refrain :' : {refrain_count}")

    # 2. Number sequence check
    expected = 1
    missing = []
    for p in pages:
        num_str = p[1].strip()
        m = re.search(r'(\d+)', num_str)
        if m:
            num = int(m.group(1))
            while num > expected:
                missing.append(expected)
                expected += 1
            if num == expected:
                expected += 1
    
    if missing:
        print(f"STILL MISSING NUMBERS: {missing}")
    else:
        print("Sequence CHECK: PASSED (All numbers 1 to end present)")

    # 3. Fused songs inside content (numbers > 30 following \\n)
    fused_found = []
    for p in pages:
        m = re.search(r'\\n(\d{2,})\.', p[3])
        if m:
            val = int(m.group(1))
            if val > 30:
                fused_found.append((p[1], val))
    
    if fused_found:
        print(f"SAMPLES OF REMAINING FUSED: {fused_found[:10]}")
    else:
        print("Fused songs inside text: NONE detected.")

    # 4. Triple newlines
    triples = sum(1 for p in pages if '\\n\\n\\n' in p[3])
    print(f"Songs with triple newlines: {triples}")

if __name__ == "__main__":
    verify_v3()
