import re

def final_fix():
    file_path = 'app/src/main/java/com/davidkazad/chantlouange/datas/CC.java'
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()

    # 1. Split remaining fused songs (safer regex)
    # Look for \nNumber. [UPPERCASE TITLE]\n
    # Song headers usually have at least 2 uppercase words or short title.
    # We'll use a broad one and then filter.
    pages = re.findall(r'pg\.add\(new Page\(\s*(\d+)\s*,\s*"(.*?)"\s*,\s*"(.*?)"\s*,\s*"(.*?)"\s*,\s*(\d+)\s*\)\);', content, re.DOTALL)
    
    new_pages = []
    for p_id, p_num, p_title, p_text, b_id in pages:
        # Check for embedded songs: \n[Number]. [TITLE]
        # Most hymn numbers in CC are between 1 and 650.
        # We look for \n followed by a number > 15 (to avoid verse 1, 2...)
        # followed by a dot, space, and then uppercase letters.
        
        # We split by headers like 309. JESUS REVIENT...
        # The header must be at the start of a line (\n)
        splits = re.split(r'\\n(\d+[A-Z]?\.\s*[A-ZÀ-Ÿ0-9\s\!\'\-,\?\(\)]{5,})(?=\\n|$)', p_text)
        
        if len(splits) > 1:
            new_pages.append({'num': p_num, 'title': p_title, 'text': splits[0]})
            i = 1
            while i < len(splits):
                header = splits[i].strip()
                body = splits[i+1].strip() if i+1 < len(splits) else ""
                i += 2
                
                # Parse header
                m = re.match(r'^(\d+[A-Z]?\.)\s*(.*)$', header)
                if m:
                    new_pages.append({'num': m.group(1) + " ", 'title': m.group(2).strip(), 'text': body})
                else:
                    new_pages[-1]['text'] += "\\n" + header + "\\n" + body
        else:
            new_pages.append({'num': p_num, 'title': p_title, 'text': p_text})

    # 2. Standardize Refrains & Clean Up
    for p in new_pages:
        t = p['text']
        
        # A) Standardize explicit refrain markers: /: refrain ... :/:
        # We use a non-greedy match and preserve content.
        # Handling the escaped \n as well.
        t = re.sub(r'(?i)(?:/:|:/:)\s*(refrain|ch[oœ]ur)\b\s*(.*?)\s*(?:/:|:/:)', r'Refrain : \2', t)
        
        # B) Handle lines starting with "Refrain" or "Chœur" that don't have the "Refrain :" format
        t = re.sub(r'(?i)(^|\\n)(refrain|ch[oœ]ur|chorus|coro)\s*[:.-]*\s*', r'\1Refrain : ', t)
        
        # C) Remove remaining /: and :/: (often used for repeats)
        # Replacing them with (bis) if they wrap a line, or just removing them as they can be noisy.
        # The user said standardizing they indicate refrain, so we've handled that.
        # Let's remove them for now as they are often improperly placed.
        t = t.replace('/:', '').replace(':/:', '')
        
        # D) Fix double "Refrain : Refrain :"
        t = re.sub(r'(?i)(Refrain\s*:\s*){2,}', r'Refrain : ', t)
        
        # E) Remove excess lines
        t = re.sub(r'\\n(\s*\\n){2,}', r'\\n\\n', t)
        
        p['text'] = t.strip()

    # 3. Assemble File
    start_match = re.search(r'protected List<Page> buildPages\(\) \{\s*List<Page> pg = new ArrayList<\>\(\);\s*', content)
    end_match = re.search(r'(\s*return pg;\s*\})', content)
    
    if start_match and end_match:
        prefix = content[:start_match.end()]
        suffix = content[end_match.start():]
        
        page_lines = []
        for i, p in enumerate(new_pages, 1):
            # We must escape " in the text if any were introduced, but they should be escaped already.
            # We also ensure the formatting is clean.
            page_lines.append(f'\t\tpg.add(new Page({i}, "{p["num"]}", "{p["title"]}", "{p["text"]}", 1));')
        
        new_content = prefix + "\n".join(page_lines) + suffix
        
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(new_content)
        print(f"DONE: Processed {len(new_pages)} pages.")
    else:
        print("ERROR: Could not find buildPages block.")

if __name__ == "__main__":
    final_fix()
    
# VERIFICATION PART 2 (Integrated)
import re
def verify_v2():
    file_path = 'app/src/main/java/com/davidkazad/chantlouange/datas/CC.java'
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    pages = re.findall(r'pg\.add\(new Page\(\s*(\d+)\s*,\s*"(.*?)"\s*,\s*"(.*?)"\s*,\s*"(.*?)"\s*,\s*\d+\s*\)\);', content, re.DOTALL)
    
    refrain_count = sum(1 for p in pages if 'Refrain :' in p[3])
    print(f"Songs with 'Refrain :' : {refrain_count}")
    
    fused = []
    for p_id, p_num, p_title, p_text in pages:
        # Check for song numbers inside text (large numbers > 15)
        m = re.search(r'\\n(\d{2,})\.', p_text)
        if m:
            num = int(m.group(1))
            if num > 20:
                fused.append(p_num)
    
    if fused:
        print(f"Possible fused songs remaining: {len(fused)} items ({fused[:10]}...)")
    else:
        print("No apparent fused songs remaining.")

verify_v2()
