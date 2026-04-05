import re

def verify_content():
    file_path = 'app/src/main/java/com/davidkazad/chantlouange/datas/CC.java'
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()

    pages = re.findall(r'pg\.add\(new Page\(\s*(\d+)\s*,\s*"(.*?)"\s*,\s*"(.*?)"\s*,\s*"(.*?)"\s*,\s*\d+\s*\)\);', content, re.DOTALL)
    
    # Check for empty or very short content
    for p_id, p_num, p_title, p_text in pages:
        if len(p_text) < 10:
            print(f"ALARM: Song {p_num} ({p_title}) has abnormally short content: '{p_text}'")
        
        # Check if the split left some numbers in the text
        if re.search(r'\\n\d+\.', p_text):
            print(f"STILL FUSED: Song {p_num} ({p_title}) seems to have another song starting inside.")

    # Search for common refrain markers to see if any were missed or messed up
    # Since I supposedly removed them, I should look for the result of my "fix"
    for p_id, p_num, p_title, p_text in pages:
        if 'Refrain :' in p_text:
            # OK, just counting
            pass
        elif re.search(r'ch[oœ]ur|refrain|/:', p_text, re.IGNORECASE):
            print(f"NEEDS FIX: Song {p_num} ({p_title}) still has raw refrain markers: {p_text[:100]}...")

if __name__ == "__main__":
    verify_content()
