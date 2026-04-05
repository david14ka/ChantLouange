import re

def main():
    file_path = 'app/src/main/java/com/davidkazad/chantlouange/datas/CC.java'
    
    # Read with latin-1 to be absolutely safe with all bytes
    with open(file_path, 'r', encoding='latin-1') as f:
        content = f.read()

    char_count_before = len(content)

    # 1. Extract all existing Page definitions
    # Pattern: pg.add(new Page(ID, "NUM", "TITLE", "CONTENT", 1));
    # We use a broad match for the content that handles escaped quotes \"
    page_matches = re.findall(r'pg\.add\(new Page\(\s*\d+\s*,\s*\"(.*?)\"\s*,\s*\"(.*?)\"\s*,\s*\"(.*?)(?<!\\)\"\s*,\s*1\)\);', content, re.DOTALL)
    
    print(f"Initial pages found: {len(page_matches)}")
    
    new_pages = []
    for num, title, text in page_matches:
        # 2. Split fused songs
        # Regex for headers like: \n309. JESUS REVIENT...
        # We allow (a), (b) suffixes and common punctuation.
        # We look for a number > 15 to avoid verse markers in text.
        header_pattern = r'\\n(\d{2,}[A-Z]?\.\s*(?:\([a-z]\)\s*)?[A-ZÄ-ÿ\d\s\!\'\-,\?\(\)\.\:]{5,})(?=\\n|$)'
        
        splits = re.split(header_pattern, text)
        
        if len(splits) > 1:
            # First part belongs to the current page
            new_pages.append({'num': num, 'title': title, 'text': splits[0]})
            
            # Subsequent parts are [Header, Body, Header, Body...]
            i = 1
            while i < len(splits):
                h = splits[i].strip()
                b = splits[i+1].strip() if i+1 < len(splits) else ""
                i += 2
                
                # Parse header into number and title
                # Example: "309. JESUS REVIENT"
                h_match = re.match(r'^(\d+[A-Z]?\.)\s*(.*)$', h)
                if h_match:
                    new_pages.append({'num': h_match.group(1) + " ", 'title': h_match.group(2).strip(), 'text': b})
                else:
                    # Fallback
                    new_pages[-1]['text'] += "\\n" + h + "\\n" + b
        else:
            new_pages.append({'num': num, 'title': title, 'text': text})

    print(f"Total pages after splitting: {len(new_pages)}")

    # 3. Standardize Refrains
    for p in new_pages:
        t = p['text']
        
        # A) Explicit marked refrains: /: Refrain ... :/: 
        # (Handling the variants of Chœur/Choir)
        # We use . for the potentially corrupted œ
        t = re.sub(r'(?i)(?:/:|:/:)\s*(refrain|ch.ur|chorus|coro|pambio)\b\s*[:.-]*\s*(.*?)\s*(?:/:|:/:)', r'Refrain : \2', t)
        
        # B) Stanza headers: Refrain: or Chœur:
        t = re.sub(r'(?i)(^|\\n)(refrain|ch.ur|chorus|coro|pambio)\s*[:.-]*\s*', r'\1Refrain : ', t)
        
        # C) Remove remaining markers /: and :/:
        t = t.replace('/:', '').replace(':/:', '')
        
        # D) Clean up double "Refrain : Refrain :"
        t = re.sub(r'(?i)(Refrain\s*:\s*){2,}', r'Refrain : ', t)
        
        # E) Fix triple newlines
        t = re.sub(r'(\\n\s*){3,}', r'\\n\\n', t)
        
        p['text'] = t.strip()

    # 4. Re-generate buildPages content
    page_lines = []
    for i, p in enumerate(new_pages, 1):
        # Escape any unescaped quotes (though they should be fine)
        # Ensure the string is clean for Java
        page_lines.append(f'\t\tpg.add(new Page({i}, "{p["num"]}", "{p["title"]}", "{p["text"]}", 1));')
    
    # Extract prefix and suffix from original content
    start_match = re.search(r'protected List<Page> buildPages\(\) \{\s*List<Page> pg = new ArrayList<\>\(\);\s*', content)
    end_match = re.search(r'(\s*return pg;\s*\})', content)
    
    if start_match and end_match:
        prefix = content[:start_match.end()]
        suffix = content[end_match.start():]
        
        final_content = prefix + "\n".join(page_lines) + suffix
        
        with open(file_path, 'w', encoding='latin-1') as f:
            f.write(final_content)
        
        print(f"SUCCESS: File updated. Final char count: {len(final_content)}")
    else:
        print("ERROR: Could not find buildPages markers.")

if __name__ == "__main__":
    main()
