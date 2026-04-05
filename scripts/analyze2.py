import re

with open('app/src/main/java/com/davidkazad/chantlouange/datas/CC.java', 'r', encoding='utf-8') as f:
    content = f.read()

pages = re.findall(r'pg\.add\(new Page\(\s*(\d+)\s*,\s*"(.*?)"\s*,\s*"(.*?)"\s*,\s*"(.*?)"\s*,\s*\d+\s*\)\);', content, re.DOTALL)

expected_num = 1
prev_page = None
missing = []
for page in pages:
    idx, num_str, title, text = page
    num_match = re.search(r'\d+', num_str)
    if num_match:
        num = int(num_match.group())
        while num > expected_num:
            if prev_page:
                prev_idx, prev_num_str, prev_title, prev_text = prev_page
                print(f"Missing {expected_num}. Likely fused in {prev_num_str} ({prev_title})")
                missing.append((expected_num, prev_page))
            expected_num += 1
        expected_num = num + 1
    prev_page = page

# Let's count refrains
refrains = []
excess_lines = []

for page in pages:
    idx, num_str, title, text = page
    # Check for excessive line breaks (3 or more consecutive \n)
    if re.search(r'\\n\\n\\n|\\n \\n \\n|\n\n\n', text) or "\\n\\n\\n" in text or "\\n \\n \\n" in text.replace('\r', ''):
        excess_lines.append(num_str)
    
    # Check for /: refrain or similar
    if re.search(r'/:.*?refrain|refrain.*?/:', text, re.IGNORECASE):
        refrains.append(num_str)

print("\n--- Missing Number Fusions ---")
for m in missing:
    num, p = m
    print(f"Look at id {p[0]} (Number {p[1]}) for missing song {num}")

print("\n--- Excess Lines ---")
print(f"Found in: {', '.join(excess_lines)}")

print("\n--- Refrains ---")
print(f"Found in: {', '.join(refrains)}")
