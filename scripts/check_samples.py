import json

def check_samples():
    with open('app/src/main/res/raw/audio_map.json', 'r', encoding='utf-8') as f:
        data = json.load(f)
    
    samples = ["1_1a", "1_14", "1_65", "1_309", "1_86"]
    for s in samples:
        print(f"{s}: {data.get(s, 'Not Found')}")

if __name__ == "__main__":
    check_samples()
