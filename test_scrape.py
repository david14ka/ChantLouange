import requests
import json

with open('cs_ob_mapping.json', 'r', encoding='utf-8') as f:
    data = json.load(f)

# Rather than fetching PDFs, let's just attempt to construct the PDF URL.
# From earlier, "Amazing Grace" had "https://www.hymnallibrary.org/media/midi_files/Amazing_Grace_file.mid"
# For Hymnal Library, the PDF link is typically:
# "https://www.hymnallibrary.org/media/pdf/[song_name].pdf"
# But sometimes it's sheet_music directory.
# Actually, their midi file URLs are in the `audio_urls` list of `cs_ob_mapping.json`.
# Let's inspect the existing audio_urls to see if we can deduce the pdf URLs.

import difflib

# As observed earlier, for "christ-returneth", the pdf was: 
# "https://www.hymnallibrary.org/media/hymns/sheet_music/christ_returneth.jpg"

import re

for item in data:
    pdf_urls = []
    
    # We will try a different approach. We use Google / Bing search to find hymnal library pages! No, we can just search!
    # Wait, let's use the site's REST API? Hymnal Library uses django maybe.

    break

print("Checking first 5 audio_urls:")
for item in data[:5]:
    print(item.get("cs_clean"), item.get("audio_urls"))
    
