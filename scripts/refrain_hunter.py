import re

def hunt():
    file_path = 'app/src/main/java/com/davidkazad/chantlouange/datas/CC.java'
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # Let's find any variant of 'Refrain' or 'Choeur' and see the HEX
    # We look for words that start with 'ch' and end with 'r' or similar, 
    # or words that look like 'refrain'.
    pattern = re.compile(r'\b(ref|cho|choeur|chur|chour|coro|pambio)\w*\b', re.IGNORECASE)
    matches = pattern.findall(content)
    
    # Let's print unique matches to see the spelling/encoding
    unique_variants = set(matches)
    print(f"Variants found: {unique_variants}")

    # Let's see some samples
    samples = re.findall(r'.{0,30}\b(ref|cho|choeur|chur|chour|coro|pambio)\w*.{0,30}', content, re.IGNORECASE)
    for s in samples[:10]:
        print(f"Sample: {s}")

if __name__ == "__main__":
    hunt()
