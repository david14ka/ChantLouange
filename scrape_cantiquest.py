import requests
from bs4 import BeautifulSoup
import json
import os
import re

def scrape_book(book_name, url):
    print(f"Scraping {book_name} from {url}...")
    headers = {
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36'
    }
    try:
        response = requests.get(url, headers=headers, timeout=120)
        response.encoding = 'iso-8859-1'
        response.raise_for_status()
    except Exception as e:
        print(f"Failed to fetch {url}: {e}")
        return []

    soup = BeautifulSoup(response.text, 'html.parser')
    songs = []
    
    # Find the main table. Usually it's the one with most rows.
    tables = soup.find_all('table')
    if not tables:
        print(f"No table found for {book_name}")
        return []
    
    # Heuristic: table with most rows
    main_table = max(tables, key=lambda t: len(t.find_all('tr')))
    
    rows = main_table.find_all('tr')
    for row in rows:
        cells = row.find_all('td')
        if len(cells) < 3:
            continue
            
        # Try to find Number and Title
        # Structure varies, but usually 1st col is Num, 2nd is Title
        num_text = cells[0].get_text(strip=True)
        # Remove trailing dot if exists
        num_match = re.search(r'(\d+)', num_text)
        if not num_match:
            continue
        number = num_match.group(1)
        
        title = cells[1].get_text(strip=True)
        if not title or title.lower() in ('titre', 'titres'):
            continue

        song_data = {
            "title": title,
            "number": number,
            "book": book_name,
            "piano_midi": None,
            "instru_midi": None
        }

        # Look for MIDI links in the row
        for link in row.find_all('a', href=True):
            href = link['href']
            text = link.get_text(strip=True).lower()
            
            # Pattern check: usually has .mid and mono-piano / polyinstru
            if '.mid' in href.lower():
                full_midi_url = f"https://www.cantiquest.org/{href}" if not href.startswith('http') else href
                if 'mono-piano' in href.lower() or 'piano' in text:
                    song_data['piano_midi'] = full_midi_url
                elif 'polyinstru' in href.lower() or 'instru' in text:
                    song_data['instru_midi'] = full_midi_url

        songs.append(song_data)
        
    print(f"Found {len(songs)} songs in {book_name}")
    return songs

def main():
    base_url = "https://www.cantiquest.org"
    recueils = {
        "CV": f"{base_url}/CV.htm",
        "HeC": f"{base_url}/HeC.htm",
        "CJ": f"{base_url}/CJ.htm",
        "SST": f"{base_url}/SST.htm",
        "CpE": f"{base_url}/CpE.htm",
        "CGG1": f"{base_url}/CGG1.htm",
        "CGG2": f"{base_url}/CGG2.htm",
        "TeJ": f"{base_url}/Trésors_enfance.htm",
        "VAM": f"{base_url}/VAM.htm",
        "CtqCa": f"{base_url}/CtqCa.htm",
        "Divers": f"{base_url}/Divers.htm",
        "SST": f"{base_url}/Spiritual_Songs_Traduits.htm",
        "SSO": f"{base_url}/Spiritual_Songs_Original.htm"
    }

    all_songs = []
    for book, url in recueils.items():
        all_songs.extend(scrape_book(book, url))

    # Also check English Spiritual Songs if interested, but the user asked for French site index.
    
    output_file = "cantiquest_songs_with_audio.json"
    with open(output_file, 'w', encoding='utf-8') as f:
        json.dump(all_songs, f, indent=2, ensure_ascii=False)
    
    print(f"Saved {len(all_songs)} songs to {output_file}")

if __name__ == "__main__":
    main()
