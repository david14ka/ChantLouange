import re

def verify():
    file_path = 'app/src/main/java/com/davidkazad/chantlouange/datas/CC.java'
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    pages = re.findall(r'pg\.add\(new Page\(\s*(\d+)\s*,\s*"(.*?)"\s*,\s*"(.*?)"\s*,\s*"(.*?)"\s*,\s*(\d+)\s*\)\);', content, re.DOTALL)
    
    refrain_count = 0
    for p_id, p_num, p_title, p_text, b_id in pages:
        if 'Refrain :' in p_text:
            if refrain_count < 5:
                print(f"Found Refrain in Song {p_num}: {p_text[:100]}...")
            refrain_count += 1
            
    print(f"\nTotal songs with 'Refrain :' found: {refrain_count}")

if __name__ == "__main__":
    verify()
