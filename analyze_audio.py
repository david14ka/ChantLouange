import json

# Load the cantiquest scrape data
with open('cantiquest_songs_with_audio.json', encoding='utf-8') as f:
    cantiquest_data = json.load(f)

# Load the current audio map
with open('app/src/main/res/raw/audio_map.json', encoding='utf-8') as f:
    audio_map = json.load(f)

print(f"=== CANTIQUEST SCRAPE SUMMARY ===")
books = {}
with_piano = {}
with_instru = {}
for s in cantiquest_data:
    book = s['book']
    books[book] = books.get(book, 0) + 1
    if s.get('piano_midi'):
        with_piano[book] = with_piano.get(book, 0) + 1
    if s.get('instru_midi'):
        with_instru[book] = with_instru.get(book, 0) + 1

for book in sorted(books.keys()):
    print(f"  {book}: {books[book]} songs, piano_midi={with_piano.get(book,0)}, instru_midi={with_instru.get(book,0)}")

print(f"\n=== CURRENT AUDIO MAP ===")
print(f"  Total entries: {len(audio_map)}")
keys_numeric = [k for k in audio_map if k.isdigit()]
keys_alpha = [k for k in audio_map if not k.isdigit()]
print(f"  Numeric keys: {len(keys_numeric)}")
print(f"  Alphanumeric keys: {len(keys_alpha)}")
print(f"  Alphanumeric examples: {sorted(keys_alpha)[:10]}")

print(f"\n=== CV SONGS WITH AUDIO IN CANTIQUEST ===")
cv_songs = [s for s in cantiquest_data if s['book'] == 'CV']
cv_with_midi = [s for s in cv_songs if s.get('piano_midi') or s.get('instru_midi')]
print(f"  CV songs total: {len(cv_songs)}")
print(f"  CV songs with MIDI: {len(cv_with_midi)}")
# Show a sample
for s in cv_with_midi[:10]:
    mid = s.get('piano_midi', '')[:70] if s.get('piano_midi') else ''
    print(f"    CV {s['number']}: {s['title'][:40]} | {mid}")
