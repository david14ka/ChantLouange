import re

def check_308_309():
    file_path = 'app/src/main/java/com/davidkazad/chantlouange/datas/CC.java'
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()

    # Search for Page 308. and 309.
    p308 = re.search(r'new Page\(\s*(\d+)\s*,\s*\"308\..*?\",\s*\"(.*?)\"', content)
    p309 = re.search(r'new Page\(\s*(\d+)\s*,\s*\"309\..*?\",\s*\"(.*?)\"', content)
    
    if p308: print(f"Page 308 object found! Table ID: {p308.group(1)}")
    if p309: print(f"Page 309 object found! Table ID: {p309.group(1)}")
    
    if p308 and p309:
        if p308.group(1) != p309.group(1):
            print("SUCCESS: 308 and 309 are separate pages now.")
        else:
            print("FAILURE: 308 and 309 share the same ID?")
    else:
        print(f"308: {p308}, 309: {p309}")

if __name__ == "__main__":
    check_308_309()
