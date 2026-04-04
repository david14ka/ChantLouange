import json
import os

BASE = r"c:\Users\Josh\StudioProjects\ChantLouange"
UNIFIED_MAP_FILE = os.path.join(BASE, "unified_audio_map.json")
FINAL_MAP_FILE = os.path.join(BASE, r"app\src\main\res\raw\audio_map.json")

def update_final_audio_map():
    # 1. Load unified map
    if not os.path.exists(UNIFIED_MAP_FILE):
        print(f"Error: {UNIFIED_MAP_FILE} not found.")
        return
        
    with open(UNIFIED_MAP_FILE, 'r', encoding='utf-8') as f:
        unified_map = json.load(f)
    
    # 2. Load final map
    if not os.path.exists(FINAL_MAP_FILE):
        print(f"Error: {FINAL_MAP_FILE} not found.")
        return
        
    with open(FINAL_MAP_FILE, 'r', encoding='utf-8') as f:
        final_map = json.load(f)

    # 3. Update final_map with CS entries (Book ID 8)
    # The unified_map keys correspond to CS song numbers/ids.
    
    added_count = 0
    updated_count = 0
    
    for cs_key, urls in unified_map.items():
        # Destination key format: "8_NUMBER"
        # cs_key can be "1", "2", "22a", etc.
        final_key = f"8_{cs_key}"
        
        if final_key in final_map:
            # Update existing: merge URLs and deduplicate
            existing_urls = final_map[final_key]
            # Ensure it's a list
            if not isinstance(existing_urls, list):
                existing_urls = [existing_urls] if existing_urls else []
                
            merged = existing_urls + [u for u in urls if u not in existing_urls]
            final_map[final_key] = merged
            updated_count += 1
        else:
            # Add new entry
            final_map[final_key] = urls
            added_count += 1
            
    # 4. Save final map
    with open(FINAL_MAP_FILE, 'w', encoding='utf-8') as f:
        json.dump(final_map, f, ensure_ascii=False, indent=2)
        
    print(f"Update complete for Crois Seulement (Book 8):")
    print(f" - New entries added: {added_count}")
    print(f" - Entries updated: {updated_count}")
    print(f" - Total entries in audio_map.json: {len(final_map)}")

if __name__ == "__main__":
    update_final_audio_map()
