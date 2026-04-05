import re

with open('app/src/main/java/com/davidkazad/chantlouange/datas/CC.java', 'r', encoding='utf-8') as f:
    content = f.read()

pages = re.findall(r'pg\.add\(new Page\(\s*(\d+)\s*,\s*"(.*?)"\s*,\s*"(.*?)"\s*,\s*"(.*?)"\s*,\s*\d+\s*\)\);', content, re.DOTALL)

expected_num = 1
for page in pages:
    idx, num_str, title, text = page
    num_match = re.search(r'\d+', num_str)
    if num_match:
        num = int(num_match.group())
        if num > expected_num:
            print(f"Missing number before: {num_str} {title}. Expected {expected_num}, got {num}")
            expected_num = num
        
        if num == expected_num:
            expected_num += 1

    # Check for refrains
    if '/:' in text or 'refrain' in text.lower():
        # Check standard format
        pass
    
    # Check for excess new lines
    if '\n\n\n' in text:
        print(f"Excess new lines in: {num_str} {title}")

print("Analysis complete.")
