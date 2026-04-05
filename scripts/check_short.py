import re

def check_short_pages():
    file_path = 'app/src/main/java/com/davidkazad/chantlouange/datas/CC.java'
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    pages = re.findall(r'pg\.add\(new Page\(\s*(\d+)\s*,\s*"(.*?)"\s*,\s*"(.*?)"\s*,\s*"(.*?)"\s*,\s*(\d+)\s*\)\);', content, re.DOTALL)
    
    short_ones = []
    for p_id, p_num, p_title, p_text, b_id in pages:
        if len(p_text) < 50:
            short_ones.append((p_num, p_title, p_text))
            
    print(f"Total short pages (< 50 chars): {len(short_ones)}")
    for s in short_ones[:20]:
        print(f"  {s[0]} ({s[1]}): {s[2]}")

if __name__ == "__main__":
    check_short_pages()
