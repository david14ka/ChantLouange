import re

def main():
    file_path = 'app/src/main/java/com/davidkazad/chantlouange/datas/CC.java'
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()

    pages = re.findall(r'pg\.add\(new Page\(\s*(\d+)\s*,\s*"(.*?)"\s*,\s*"(.*?)"\s*,\s*"(.*?)"\s*,\s*(\d+)\s*\)\);', content, re.DOTALL)
    
    new_pages = []
    
    for page in pages:
        idx, num, title, text, book_id = page
        
        # New regex: \n followed by number [A-Z]? dot space, then any uppercase/punctuation/space up to \n
        splits = re.split(r'\\n(\d+[A-Za-z]?\.\s*[^a-z\\]+?)(?=\\n)', text)
        if len(splits) > 1:
            new_pages.append({'num': num, 'title': title, 'text': splits[0].strip()})
            i = 1
            while i < len(splits):
                header = splits[i].strip()
                content_part = splits[i+1].strip()
                i += 2
                match = re.search(r'^(\d+[A-Za-z]?\.)\s+(.*)$', header)
                if match:
                    n_num = match.group(1) + " "
                    n_title = match.group(2).strip()
                    new_pages.append({'num': n_num, 'title': n_title, 'text': content_part})
                else:
                    new_pages[-1]['text'] += "\\n" + header + "\\n" + content_part
        else:
            new_pages.append({'num': num, 'title': title, 'text': text})

    for p in new_pages:
        t = p['text']
        t = t.strip()
        p['text'] = t

    start_match = re.search(r'protected List<Page> buildPages\(\) \{\s*List<Page> pg = new ArrayList<\>\(\);\s*', content)
    end_match = re.search(r'(\s*return pg;\s*\})', content)
    
    if start_match and end_match:
        prefix = content[:start_match.end()]
        suffix = content[end_match.start():]
        
        page_lines = []
        for i, p in enumerate(new_pages, 1):
            page_lines.append(f'\t\tpg.add(new Page({i}, "{p["num"]}", "{p["title"]}", "{p["text"]}", 1));')
        
        new_content = prefix + "\n".join(page_lines) + suffix
        
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(new_content)
        
        print(f"Successfully processed {len(new_pages)} pages.")

if __name__ == "__main__":
    main()
