import os
import re

def fix_java_syntax():
    path = 'app/src/main/java/com/davidkazad/chantlouange/datas/CC.java'
    if not os.path.exists(path):
        print("File not found")
        return

    with open(path, 'r', encoding='utf-8', errors='ignore') as f:
        content = f.read()

    # The error is likely \\\\" followed by some text, and then \\\\" again.
    # We want to replace these with \"
    # In Java: "string \"with quotes\""
    # My refactor likely produced "string \\\\"with quotes\\\\""
    
    # Replace the bad escapes for quotes
    new_content = content.replace('\\\\"', '\\"')
    
    # Replace bad escape for space: \ followed by space
    # Example: "Les temps sont dans Sa Main.\ Rendons"
    # Note: re.sub(r'\\ ', ' ', ...) but we need to be careful with double backslashes
    # Here we look for a literal backslash (single in string) followed by space.
    # In the file it looks like \ 
    new_content = new_content.replace('\\ ', ' ')

    # Sometimes it might be \\ 
    new_content = new_content.replace('\\\\ ', ' ')

    if content != new_content:
        with open(path, 'w', encoding='utf-8') as f:
            f.write(new_content)
        print("Corrections applied.")
    else:
        print("No corrections needed.")

if __name__ == "__main__":
    fix_java_syntax()
