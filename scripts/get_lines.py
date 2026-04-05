import re
for line in open('app/src/main/java/com/davidkazad/chantlouange/datas/CC.java', encoding='utf-8'):
    if '"308. "' in line:
        print(line)
