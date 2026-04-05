import re

def main():
    file_path = 'app/src/main/java/com/davidkazad/chantlouange/datas/CC.java'
    
    with open(file_path, 'r', encoding='utf-8', errors='ignore') as f:
        content = f.read()

    # 1. Extract all Page definitions
    page_matches = re.findall(r'pg\.add\(new Page\(\s*\d+\s*,\s*\"(.*?)\"\s*,\s*\"(.*?)\"\s*,\s*\"(.*?)(?<!\\)\"\s*,\s*1\)\);', content, re.DOTALL)
    print(f"Loaded {len(page_matches)} pages.")

    new_pages = []
    for num, title, text in page_matches:
        # A) Check for fused songs
        # Permissive regex for headers: 
        # \n + numbers + optional suffix A-Z + dot + space + any text until the next \n
        header_pattern = r'\\n(\d{2,}[A-Z]?\.\s*(?:\([a-z]\)\s*)?[^\\]+?)(?=\\n|$)'
        
        splits = re.split(header_pattern, text)
        if len(splits) > 1:
            new_pages.append({'num': num, 'title': title, 'text': splits[0]})
            i = 1
            while i < len(splits):
                h = splits[i].strip()
                b = splits[i+1].strip() if i+1 < len(splits) else ""
                i += 2
                
                # Check if it's really a header (large num, mostly uppercase title)
                m = re.match(r'^(\d+[A-Z]?\.)\s*(.*)$', h)
                if m:
                    n_num = m.group(1) + " "
                    n_title = m.group(2).strip()
                    new_pages.append({'num': n_num, 'title': n_title, 'text': b})
                else:
                    new_pages[-1]['text'] += "\\n" + h + "\\n" + b
        else:
            new_pages.append({'num': num, 'title': title, 'text': text})

    print(f"Pages after split: {len(new_pages)}")

    # 2. Refrains & Formatting
    for p in new_pages:
        t = p['text']
        
        # A) Standardize Refrains
        # Target: /: refrain ... :/: or similar
        # Pattern: markers + optional newline + "refrain/choeur" + optional punctuation + content + markers
        t = re.sub(r'(?i)(?::/|/:|:/:|:|/)+\s*(?:\\n)*\s*(refrain|ch.ur|chorus|coro|pambio|pembio)\b\s*[:.-]*\s*(.*?)\s*(?::/|/:|:/:|:|/)+', r'Refrain : \2', t)
        
        # B) Lines starting with "Refrain" or "Chœur"
        t = re.sub(r'(?i)(^|\\n)(refrain|ch.ur|chorus|coro|pambio|pembio)\s*[:.-]*\s*', r'\1Refrain : ', t)
        
        # C) Remove remaining markers that are NOT refrains (often used for repeats like :...:)
        # User said standardizing refrain, so we only promote explicitly labeled ones.
        # Simple repeats :...: will be cleaned up.
        t = t.replace('/:', '').replace(':/:', '')
        # Handle the common :...: variant
        t = re.sub(r'(^|\\n)\s*:\s*(.*?)\s*:\s*(\\n|$)', r'\1\2\3', t)
        
        # D) Clean up
        t = re.sub(r'(?i)(Refrain\s*:\s*){2,}', r'Refrain : ', t)
        t = re.sub(r'(\\n\s*){3,}', r'\\n\\n', t)
        
        p['text'] = t.strip()

    # 3. Save
    page_lines = []
    for i, p in enumerate(new_pages, 1):
        # Escape quotes in title/text to be safe
        t_clean = p['text'].replace('"', '\\"')
        page_lines.append(f'\t\tpg.add(new Page({i}, "{p["num"]}", "{p["title"]}", "{t_clean}", 1));')
        
    start_match = re.search(r'protected List<Page> buildPages\(\) \{\s*List<Page> pg = new ArrayList<\>\(\);\s*', content)
    end_match = re.search(r'(\s*return pg;\s*\})', content)
    
    if start_match and end_match:
        prefix = content[:start_match.end()]
        suffix = content[end_match.start():]
        final_file = prefix + "\n".join(page_lines) + suffix
        
        # Write back - we'll try utf-8
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(final_file)
        print("SUCCESS: File saved.")
    else:
        print("ERROR: markers not found.")

if __name__ == "__main__":
    main()
