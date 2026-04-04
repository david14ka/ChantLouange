import json

data = json.load(open('cantiquest_songs_with_audio.json', encoding='utf-8'))
cv_songs = [s for s in data if s['book'] == 'CV']
print(f"CV songs total: {len(cv_songs)}")
for s in cv_songs[:20]:
    piano = s.get('piano_midi', 'NONE')
    if piano:
        piano = piano[:60]
    else:
        piano = 'NONE'
    print(f"{s['number']}: {s['title']} | piano={piano}")

# Check how many have MIDI
with_midi = [s for s in data if s.get('piano_midi') or s.get('instru_midi')]
print(f"\nTotal songs with MIDI: {len(with_midi)} / {len(data)}")
