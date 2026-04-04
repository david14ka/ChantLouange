import json

with open("cantiquest_songs_with_audio.json", encoding="utf-8") as f:
    cantiquest_data = json.load(f)
    print("CV 112 matches:")
    print([d for d in cantiquest_data if d['book'] == 'CV' and d['number'] == '112'])

with open("GeneratedCC.json", encoding="utf-8") as f:
    cc_data = json.load(f)
    print("CC 4 matches:")
    s = [s for s in cc_data['songs'] if s['number'] == '4.'][0]
    print(repr(s['content']))
