import requests
from bs4 import BeautifulSoup
import json
import time
from concurrent.futures import ThreadPoolExecutor, as_completed

BASE_URL = "https://www.hymnallibrary.org"
INDEX_URL = "https://www.hymnallibrary.org/hymns/hymns/index/hymns/?page={}"

def get_hymn_links(page_num):
    hymns = []
    print(f"Scraping index page {page_num}...")
    try:
        response = requests.get(INDEX_URL.format(page_num), timeout=20)
        soup = BeautifulSoup(response.text, 'html.parser')
        
        # In the index, hymn links are typically within <li> or <div> tags
        # and point to /hymns/slug/
        links = soup.find_all('a', href=True)
        for link in links:
            href = link['href']
            text = link.text.strip()
            
            # Match /hymns/something/ but ignore index and author links
            is_hymn_link = '/hymns/' in href and not any(x in href for x in ['/index/', '/authors/', '/scripture/', '/year/', '/midi/'])
            
            if is_hymn_link:
                # Clean up title
                title = text.replace('[[MIDI]:', '').replace(']', '').replace('[MIDI]:', '').strip()
                if title and len(title) > 2:
                    hymns.append({
                        "title": title,
                        "url": BASE_URL + href if href.startswith('/') else href
                    })
    except Exception as e:
        print(f"Error on index page {page_num}: {e}")
    return hymns

def extract_midi(hymn):
    try:
        resp = requests.get(hymn['url'], timeout=20)
        h_soup = BeautifulSoup(resp.text, 'html.parser')
        
        # Find the MIDI download link
        all_links = h_soup.find_all('a', href=True)
        for l in all_links:
            href = l['href']
            text = l.text.strip().lower()
            if ('download midi' in text or 'midi' in text) and href.endswith('.mid'):
                midi_link = href
                if not midi_link.startswith('http'):
                    # Handle relative paths or path starting with /
                    if midi_link.startswith('/'):
                        midi_link = BASE_URL + midi_link
                    else:
                        midi_link = BASE_URL + '/media/midi_files/' + midi_link.split('/')[-1]
                
                return {
                    "title": hymn['title'],
                    "midi_url": midi_link
                }
    except Exception as e:
        pass
    return None

def scrape_hymns():
    all_hymns_links = []
    
    # 1. Collect all hymn page links from the 36 index pages
    print("Step 1: Collecting hymn links from index...")
    with ThreadPoolExecutor(max_workers=5) as executor:
        futures = [executor.submit(get_hymn_links, p) for p in range(1, 37)]
        for i, future in enumerate(as_completed(futures)):
            all_hymns_links.extend(future.result())

    # De-duplicate links by URL
    unique_links_map = {h['url']: h for h in all_hymns_links}
    unique_links = list(unique_links_map.values())
    print(f"Found {len(unique_links)} unique hymns to visit.")

    if not unique_links:
        print("Error: No hymns found. Index scraping failed.")
        return

    # 2. Extract MIDI links from each hymn page
    print("Step 2: Extracting MIDI links from detail pages...")
    results = []
    with ThreadPoolExecutor(max_workers=10) as executor:
        futures = [executor.submit(extract_midi, h) for h in unique_links]
        
        count = 0
        for future in as_completed(futures):
            res = future.result()
            count += 1
            if res:
                results.append(res)
            
            if count % 50 == 0:
                print(f"Progress: {count}/{len(unique_links)} visited. Found {len(results)} MIDI so far.")
                with open("hymnal_library_raw_partial.json", "w", encoding="utf-8") as f:
                    json.dump(results, f, indent=2, ensure_ascii=False)

    with open("hymnal_library_raw.json", "w", encoding="utf-8") as f:
        json.dump(results, f, indent=2, ensure_ascii=False)
    
    print(f"Successfully saved {len(results)} MIDI links to hymnal_library_raw.json")

if __name__ == "__main__":
    scrape_hymns()
