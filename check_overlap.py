import re

def get_titles(file_path):
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    # Looking for new Page(number, "indicator", "TITLE", ...)
    return set(re.findall(r'new Page\(\d+,\s*\"[^\"]*\",\s*\"([^\"]+)\"', content))

ob_titles = get_titles('app/src/main/java/com/davidkazad/chantlouange/datas/OB.java')
cc_titles = get_titles('app/src/main/java/com/davidkazad/chantlouange/datas/CC.java')

common = ob_titles & cc_titles
print(f"OB Titles: {len(ob_titles)}")
print(f"CC Titles: {len(cc_titles)}")
print(f"Common Titles: {len(common)}")
if common:
    print("Samples:", list(common)[:10])
