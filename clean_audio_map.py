import json
import os

def clean_audio_map():
    # Path
    audio_map_file = r'c:\Users\Josh\StudioProjects\ChantLouange\app\src\main\res\raw\audio_map.json'

    if not os.path.exists(audio_map_file):
        print(f"Error: Audio map file not found at {audio_map_file}")
        return

    # Load audio map
    with open(audio_map_file, 'r', encoding='utf-8') as f:
        try:
            audio_map = json.load(f)
        except json.JSONDecodeError as e:
            print(f"Error decoding audio map file: {e}")
            return

    cleaned_count = 0
    removed_mid_total = 0

    for key, urls in audio_map.items():
        # Check if there's at least one mp3
        has_mp3 = any(url.lower().endswith('.mp3') for url in urls)
        
        if has_mp3:
            # Filter out mid/midi links
            new_urls = [url for url in urls if not (url.lower().endswith('.mid') or url.lower().endswith('.midi'))]
            
            if len(new_urls) < len(urls):
                removed_mid_total += (len(urls) - len(new_urls))
                audio_map[key] = new_urls
                cleaned_count += 1

    # Save cleaned audio map
    with open(audio_map_file, 'w', encoding='utf-8') as f:
        json.dump(audio_map, f, indent=2, ensure_ascii=False)

    print(f"Cleanup complete.")
    print(f"Modified {cleaned_count} entries.")
    print(f"Removed total of {removed_mid_total} .mid/.midi links.")

if __name__ == "__main__":
    clean_audio_map()
