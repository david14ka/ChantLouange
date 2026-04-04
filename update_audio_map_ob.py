import json
import os

def update_audio_map():
    ob_audio_path = "hymnallibrary_ob_audio.json"
    audio_map_path = "app/src/main/res/raw/audio_map.json"
    
    if not os.path.exists(ob_audio_path):
        print(f"Error: {ob_audio_path} not found.")
        return
    if not os.path.exists(audio_map_path):
        print(f"Error: {audio_map_path} not found.")
        return
        
    with open(ob_audio_path, "r", encoding="utf-8") as f:
        ob_entries = json.load(f)
        
    with open(audio_map_path, "r", encoding="utf-8") as f:
        audio_map = json.load(f)
        
    added_count = 0
    for entry in ob_entries:
        num = entry["number"].strip().lower()
        key = f"7_{num}"
        
        links = set(audio_map.get(key, []))
        
        if entry.get("piano_midi"):
            links.add(entry["piano_midi"])
        if entry.get("instru_midi"):
            links.add(entry["instru_midi"])
            
        if len(links) > len(audio_map.get(key, [])):
            audio_map[key] = sorted(list(links))
            added_count += 1
            
    # Sort keys for consistency
    sorted_map = dict(sorted(audio_map.items()))
    
    with open(audio_map_path, "w", encoding="utf-8") as f:
        json.dump(sorted_map, f, indent=2, ensure_ascii=False)
        
    print(f"Successfully updated {added_count} OB songs in audio_map.json.")

if __name__ == "__main__":
    update_audio_map()
