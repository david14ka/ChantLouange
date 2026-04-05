import re

def check_refrain_errors():
    file_path = 'app/src/main/java/com/davidkazad/chantlouange/datas/CC.java'
    with open(file_path, 'r', encoding='utf-8', errors='ignore') as f:
        content = f.read()

    # Look for "choeur", "refrain" or "pambio" and print context
    pattern = re.compile(r'(?:refrain|ch.ur|pambio)', re.IGNORECASE)
    matches = list(pattern.finditer(content))
    
    # Let's see why they weren't caught as "Refrain :"
    for m in matches[:20]:
        context = content[max(0, m.start()-30) : min(len(content), m.end()+50)]
        print(f"Match: {m.group(0)} | Context: {repr(context)}")

if __name__ == "__main__":
    check_refrain_errors()
