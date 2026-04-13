import json
import os

def sync_audio_mappings():
    # Paths
    mapping_file = r'c:\Users\Josh\StudioProjects\ChantLouange\cs_ob_mapping.json'
    audio_map_file = r'c:\Users\Josh\StudioProjects\ChantLouange\app\src\main\res\raw\audio_map.json'

    # Load mapping data
    if not os.path.exists(mapping_file):
        print(f"Error: Mapping file not found at {mapping_file}")
        return

    with open(mapping_file, 'r', encoding='utf-8') as f:
        try:
            mappings = json.load(f)
        except json.JSONDecodeError as e:
            print(f"Error decoding mapping file: {e}")
            return

    # Load existing audio map
    if os.path.exists(audio_map_file):
        with open(audio_map_file, 'r', encoding='utf-8') as f:
            try:
                audio_map = json.load(f)
            except json.JSONDecodeError as e:
                print(f"Error decoding audio map file: {e}")
                audio_map = {}
    else:
        audio_map = {}

    # Book IDs
    BOOK_OB = 7
    BOOK_CS = 8

    updated_count = 0

    for entry in mappings:
        cs_num = entry.get('cs_num')
        ob_num = entry.get('ob_num')
        audio_urls = entry.get('audio_urls', [])

        if not audio_urls:
            continue

        # Update CS (Book 8)
        if cs_num is not None:
            key = f"{BOOK_CS}_{cs_num}"
            if key not in audio_map:
                audio_map[key] = []
            
            # Merge unique URLs
            for url in audio_urls:
                if url not in audio_map[key]:
                    audio_map[key].append(url)
                    updated_count += 1

        # Update OB (Book 7)
        if ob_num is not None:
            key = f"{BOOK_OB}_{ob_num}"
            if key not in audio_map:
                audio_map[key] = []
            
            # Merge unique URLs
            for url in audio_urls:
                if url not in audio_map[key]:
                    audio_map[key].append(url)
                    updated_count += 1

    # Save updated audio map
    with open(audio_map_file, 'w', encoding='utf-8') as f:
        json.dump(audio_map, f, indent=2, ensure_ascii=False)

    print(f"Synchronization complete. Added/Updated {updated_count} URL mappings.")

if __name__ == "__main__":
    sync_audio_mappings()
