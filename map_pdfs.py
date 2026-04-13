import json
import requests
from bs4 import BeautifulSoup
import re
import urllib.parse
from concurrent.futures import ThreadPoolExecutor

with open('cs_ob_mapping.json', 'r', encoding='utf-8') as f:
    data = json.load(f)

base_url = "https://www.hymnallibrary.org"

def process_item(item):
    # Clear existing non-PDF links
    if item.get("partition_urls"):
        item["partition_urls"] = [url for url in item["partition_urls"] if url.lower().endswith('.pdf')]
        
    if item.get("partition_urls"): # already has a PDF URL
        return item
    
    ob_clean = item.get("ob_clean")
    if not ob_clean:
        return item
        
    # generate slug
    slug = re.sub(r'[^a-z0-9]+', '-', ob_clean.lower()).strip('-')
    
    # Try fetching the URL directly using the slug
    hymn_url = f"{base_url}/hymns/{slug}/"
    
    try:
        res = requests.get(hymn_url, timeout=10)
        if res.status_code == 200:
            soup = BeautifulSoup(res.text, "html.parser")
            part_urls = []
            
            for a in soup.find_all('a', href=True):
                href = a['href']
                low = href.lower()
                # Search for all .pdf links
                if low.endswith('.pdf') or ('.pdf' in low and 'download' in low):
                    if not href.startswith('http'):
                        href = base_url + href
                    part_urls.append(href)
            
            # Also check text for PDF
            for a in soup.find_all('a', string=re.compile(r'PDF', re.I)):
                href = a.get('href')
                if href:
                    if not href.startswith('http'):
                        href = base_url + href
                    part_urls.append(href)
            
            item["partition_urls"] = list(set(part_urls))
            if part_urls:
                print(f"Found via slug {slug}: {item['partition_urls']}")
                return item
    except:
        pass
        
    # If slug fails, search using site search API or search page
    search_url = f"{base_url}/hymns/?q={urllib.parse.quote_plus(ob_clean)}"
    try:
        res = requests.get(search_url, timeout=10)
        soup = BeautifulSoup(res.text, "html.parser")
        
        # In search results, find the best matching hymn link
        best_link = None
        for a in soup.find_all('a', href=True):
            href = a['href']
            if href.startswith('/hymns/') and href != '/hymns/' and href.count('/') == 3:
                hymn_slug = href.strip('/').split('/')[-1]
                slug_parts = slug.split('-')
                matches = sum(1 for p in slug_parts if p in hymn_slug and len(p) > 2)
                if matches >= min(len(slug_parts) - 1, 1):
                    best_link = href
                    break
        
        if best_link:
            hymn_url = base_url + best_link
            res = requests.get(hymn_url, timeout=10)
            soup = BeautifulSoup(res.text, "html.parser")
            part_urls = []
            
            for a in soup.find_all('a', href=True):
                href = a['href']
                low = href.lower()
                if low.endswith('.pdf') or ('.pdf' in low and 'download' in low):
                    if not href.startswith('http'):
                        href = base_url + href
                    part_urls.append(href)
            
            # Also check text for PDF
            for a in soup.find_all('a', string=re.compile(r'PDF', re.I)):
                href = a.get('href')
                if href:
                    if not href.startswith('http'):
                        href = base_url + href
                    part_urls.append(href)
                    
            item["partition_urls"] = list(set(part_urls))
            if part_urls:
                print(f"Found via search {best_link}: {item['partition_urls']}")
    except:
        pass

    return item

print(f"Total entries: {len(data)}")
with ThreadPoolExecutor(max_workers=10) as executor:
    results = list(executor.map(process_item, data[:10]))

with open('cs_ob_mapping_new.json', 'w', encoding='utf-8') as f:
    json.dump(results, f, indent=2, ensure_ascii=False)
    
print("Done!")
