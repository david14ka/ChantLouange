import re

def main():
    file_path = 'app/src/main/java/com/davidkazad/chantlouange/datas/CC.java'
    with open(file_path, 'r', encoding='utf-8', errors='ignore') as f:
        content = f.read()

    # Get all pages
    page_matches = re.findall(r'pg\.add\(new Page\(\s*\d+\s*,\s*\"(.*?)\"\s*,\s*\"(.*?)\"\s*,\s*\"(.*?)(?<!\\)\"\s*,\s*1\)\);', content, re.DOTALL)
    
    new_pages = []
    # 1. Splitting (already mostly done, but we'll re-verify for the 16 splits)
    for num, title, text in page_matches:
        header_pattern = r'\\n(\d{2,}[A-Z]?\.\s*(?:\([a-z]\)\s*)?[^\\]+?)(?=\\n|$)'
        splits = re.split(header_pattern, text)
        if len(splits) > 1:
            new_pages.append({'num': num, 'title': title, 'text': splits[0]})
            i = 1
            while i < len(splits):
                h, b = splits[i].strip(), splits[i+1].strip() if i+1 < len(splits) else ""
                i += 2
                m = re.match(r'^(\d+[A-Z]?\.)\s*(.*)$', h)
                if m:
                    new_pages.append({'num': m.group(1) + " ", 'title': m.group(2).strip(), 'text': b})
                else:
                    new_pages[-1]['text'] += "\\n" + h + "\\n" + b
        else:
            new_pages.append({'num': num, 'title': title, 'text': text})

    # 2. Refrains & Encoding Fixes
    refrains_added = 0
    for p in new_pages:
        t = p['text']
        
        # Capture Refrain pattern: optional markers + word + optional markers
        # We must avoid matching "pécheur"
        # The œ character is often corrupted, so we use . or .. to match it.
        # Pattern: Starts line, optional colon/slash, followed by "Refrain" or "Chœur" (or "Chur" or "Churs")
        ref_regex = r'(?i)(?:\\n|^)\s*(?:[:/]+)?\s*(refrain|ch[o\W]{1,3}ur|chorus|coro|pambio|pembio)s?\b\s*[:.-]*\s*'
        
        # We only want to replace if it's followed by some content (not just a word in a sentence)
        # We'll use re.finditer to be safer
        new_text = t
        offset = 0
        for match in re.finditer(ref_regex, t):
            word = match.group(0).lower()
            if 'péch' in word or 'pech' in word: continue
            
            # Replace with standard "Refrain : "
            prefix = "" if match.start() == 0 else "\\n"
            replacement = prefix + "Refrain : "
            
            # Check the context to ensure it's a header
            # (Usually followed by text on the same or next line)
            new_text = new_text[:match.start()+offset] + replacement + new_text[match.end()+offset:]
            offset += len(replacement) - (match.end() - match.start())
            refrains_added += 1

        t = new_text
        # Clean up markers /: and :/:
        t = t.replace('/:', '').replace(':/:', '')
        
        # Clean up repeated Refrain labels
        t = re.sub(r'(?i)(Refrain\s*:\s*){2,}', r'Refrain : ', t)
        t = re.sub(r'(\\n\s*){3,}', r'\\n\\n', t)
        
        p['text'] = t.strip()

    print(f"Refrain headers standardized: {refrains_added}")

    # 3. Save
    page_lines = []
    for i, p in enumerate(new_pages, 1):
        t_clean = p['text'].replace('"', '\\"') # ensure quotes in content are escaped
        page_lines.append(f'\t\tpg.add(new Page({i}, "{p["num"]}", "{p["title"]}", "{t_clean}", 1));')
        
    start_match = re.search(r'protected List<Page> buildPages\(\) \{\s*List<Page> pg = new ArrayList<\>\(\);\s*', content)
    end_match = re.search(r'(\s*return pg;\s*\})', content)
    
    if start_match and end_match:
        prefix = content[:start_match.end()]
        suffix = content[end_match.start():]
        final_file = prefix + "\n".join(page_lines) + suffix
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(final_file)
        print(f"SUCCESS: File saved. Total Songs: {len(new_pages)}")
    else:
        print("ERROR: markers not found.")

if __name__ == "__main__":
    main()
