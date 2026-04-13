"""
1. Parse OB.java to extract (ob_num, ob_title) pairs
2. For each GCCSATX hymn URL, match the English title against ob_title values
3. Update audio_urls in cs_ob_mapping.json for matches found
"""

import json
import re
import time
import xml.etree.ElementTree as ET
from difflib import SequenceMatcher
from urllib.parse import urljoin

try:
    import requests
    from bs4 import BeautifulSoup
except ImportError:
    import subprocess, sys
    subprocess.check_call([sys.executable, "-m", "pip", "install", "requests", "beautifulsoup4"])
    import requests
    from bs4 import BeautifulSoup

OB_JAVA_FILE   = r"C:\Users\Josh\StudioProjects\ChantLouange\app\src\main\java\com\davidkazad\chantlouange\datas\OB.java"
SITEMAP_URL    = "https://gccsatx.com/hymns/sitemap.xml"
MAPPING_FILE   = r"C:\Users\Josh\StudioProjects\ChantLouange\cs_ob_mapping.json"
HEADERS        = {"User-Agent": "Mozilla/5.0 (compatible; HymnScraper/1.0)"}
FUZZY_THRESHOLD = 0.72


# ── helpers ────────────────────────────────────────────────────────────────────

def normalize(text: str) -> str:
    """Uppercase, strip punctuation, collapse spaces."""
    text = text.upper()
    text = re.sub(r"[^A-Z0-9 ]", " ", text)
    return re.sub(r"\s+", " ", text).strip()


def similarity(a: str, b: str) -> float:
    return SequenceMatcher(None, normalize(a), normalize(b)).ratio()


# ── step 1: parse OB.java ──────────────────────────────────────────────────────

def parse_ob_java(path: str) -> dict:
    """
    Returns {ob_num: ob_title} extracted from Page(...) declarations.
    Example line:  pg.add(new Page(2, "2.", "AMAZING GRACE (Ab)", ...
    """
    ob_map = {}
    pattern = re.compile(
        r'new\s+Page\s*\(\s*(\d+)\s*,\s*"[^"]*"\s*,\s*"([^"]+)"',
        re.S
    )
    with open(path, "r", encoding="utf-8") as f:
        content = f.read()
    for m in pattern.finditer(content):
        num   = int(m.group(1))
        title = m.group(2).strip()
        # strip key suffix like " (Ab)", " (Eb)", " ( C )"
        clean = re.sub(r'\s*\([^)]*\)\s*$', '', title).strip()
        ob_map[num] = {"raw": title, "clean": clean}
    print(f"Parsed {len(ob_map)} songs from OB.java")
    return ob_map


# ── step 2: build ob_num → mapping index lookup ──────────────────────────────

def build_ob_index(mapping: list) -> dict:
    """Returns {ob_num: index_in_mapping}"""
    idx = {}
    for i, entry in enumerate(mapping):
        ob_num = entry.get("ob_num")
        if ob_num is not None:
            idx[ob_num] = i
    return idx


# ── step 3: GCCSATX sitemap ───────────────────────────────────────────────────

def get_sitemap_urls() -> list:
    print(f"Fetching sitemap: {SITEMAP_URL}")
    r = requests.get(SITEMAP_URL, headers=HEADERS, timeout=15)
    r.raise_for_status()
    ns = {"sm": "http://www.sitemaps.org/schemas/sitemap/0.9"}
    root = ET.fromstring(r.text)
    urls = [loc.text.strip() for loc in root.findall(".//sm:loc", ns)]
    excluded = [
        "settings", "playlists", "members", "search", "guitar",
        "favorites", "projector", "gospel", "contact", "updates",
        "frequently", "help", "new-update", "sort", "editor",
        "template", "congregation", "heaven", "adoration",
        "lively", "quiet", "running", "vocals", "scripture"
    ]
    hymn_urls = [
        u for u in urls
        if re.match(r"https://gccsatx\.com/hymns/[^/]+/$", u)
        and not any(ex in u for ex in excluded)
    ]
    print(f"Found {len(hymn_urls)} hymn pages.")
    return hymn_urls


# ── step 4: fetch page, extract title + audio ─────────────────────────────────

def extract_hymn_data(url: str) -> dict:
    try:
        r = requests.get(url, headers=HEADERS, timeout=15)
        if r.status_code != 200:
            return {"title": "", "url": url, "audio_urls": []}
        html  = r.text
        soup  = BeautifulSoup(html, "html.parser")

        # title
        h1 = soup.find("h1")
        title = h1.get_text(strip=True) if h1 else ""
        if not title:
            t = soup.find("title")
            title = t.get_text(strip=True).split("-")[0].strip() if t else ""

        # audio – raw regex on HTML
        audio_links = []
        for ref in re.findall(
            r'["\']([^"\']*\.(?:mp3|ogg|wav|m4a|aac|flac|mid|midi)[^"\']*)["\']',
            html, re.I
        ):
            full = ref if ref.startswith("http") else urljoin(url, ref)
            audio_links.append(full)
        for a in soup.find_all("a", href=True):
            href = a["href"]
            if re.search(r"\.(mp3|ogg|wav|m4a|mid|midi)(\?.*)?$", href, re.I):
                full = href if href.startswith("http") else urljoin(url, href)
                audio_links.append(full)
        # dedupe
        seen, unique = set(), []
        for link in audio_links:
            if link not in seen:
                seen.add(link); unique.append(link)
        return {"title": title, "url": url, "audio_urls": unique}
    except Exception as e:
        return {"title": "", "url": url, "audio_urls": [], "error": str(e)}


# ── step 5: fuzzy match GCCS title against ob titles ─────────────────────────

def match_against_ob(gccs_title: str, ob_map: dict):
    """
    Returns (ob_num, score) of best match above threshold, or None.
    Compares normalized GCCS title against ob_map.clean values.
    """
    best_num   = None
    best_score = 0.0
    for ob_num, ob_info in ob_map.items():
        score = similarity(gccs_title, ob_info["clean"])
        if score > best_score:
            best_score = score
            best_num   = ob_num
    if best_score >= FUZZY_THRESHOLD:
        return best_num, best_score
    return None


# ── main ──────────────────────────────────────────────────────────────────────

def main():
    ob_map  = parse_ob_java(OB_JAVA_FILE)

    with open(MAPPING_FILE, "r", encoding="utf-8") as f:
        mapping = json.load(f)
    print(f"Loaded {len(mapping)} entries from mapping file.\n")

    ob_index = build_ob_index(mapping)   # ob_num -> index in mapping[]

    hymn_urls     = get_sitemap_urls()
    updated_count = 0
    matched_count = 0
    results_log   = []

    for i, url in enumerate(hymn_urls):
        slug       = url.rstrip("/").split("/")[-1]
        slug_title = slug.replace("-", " ").title()

        # quick slug-level pre-screen
        pre = match_against_ob(slug_title, ob_map)
        if pre is None:
            print(f"[{i+1}/{len(hymn_urls)}] '{slug_title}' -> no OB match (skip)")
            continue

        # fetch page
        data  = extract_hymn_data(url)
        time.sleep(0.25)

        page_title = data["title"] if data["title"] else slug_title
        match = match_against_ob(page_title, ob_map)
        if match is None:
            match = pre   # fall back to slug match

        ob_num, score = match
        ob_info = ob_map[ob_num]

        # find the corresponding mapping entry via ob_num
        map_idx = ob_index.get(ob_num)
        if map_idx is None:
            print(f"[{i+1}/{len(hymn_urls)}] '{page_title}' -> OB#{ob_num} not in mapping (skip)")
            continue

        entry   = mapping[map_idx]
        matched_count += 1
        audio_added   = []

        if data["audio_urls"]:
            existing  = set(entry.get("audio_urls") or [])
            new_urls  = [u for u in data["audio_urls"] if u not in existing]
            if new_urls:
                if not entry.get("audio_urls"):
                    entry["audio_urls"] = []
                entry["audio_urls"] = data["audio_urls"] + [
                    u for u in entry["audio_urls"] if u not in set(data["audio_urls"])
                ]
                audio_added  = new_urls
                updated_count += 1

        audio_info = (
            f"+{len(audio_added)} new: {audio_added}" if audio_added
            else (f"already present ({len(data['audio_urls'])})" if data["audio_urls"]
                  else "NO AUDIO on page")
        )
        print(f"[{i+1}/{len(hymn_urls)}] '{page_title}' -> OB#{ob_num} '{ob_info['clean']}' "
              f"(cs#{entry.get('cs_num')}, score={score:.2f}) | {audio_info}")

        results_log.append({
            "gccs_url"   : url,
            "gccs_title" : page_title,
            "ob_num"     : ob_num,
            "ob_title"   : ob_info["raw"],
            "cs_num"     : entry.get("cs_num"),
            "score"      : round(score, 4),
            "audio_found": data["audio_urls"],
            "audio_added": audio_added,
        })

    # save
    with open(MAPPING_FILE, "w", encoding="utf-8") as f:
        json.dump(mapping, f, indent=2, ensure_ascii=False)

    log_path = r"C:\Users\Josh\StudioProjects\ChantLouange\gccsatx_match_log.json"
    with open(log_path, "w", encoding="utf-8") as f:
        json.dump(results_log, f, indent=2, ensure_ascii=False)

    print(f"\n{'='*60}")
    print(f"Done. {matched_count} OB matches out of {len(hymn_urls)} GCCS hymns scraped.")
    print(f"{updated_count} mapping entries updated with new audio URLs.")
    print(f"Match log -> {log_path}")


if __name__ == "__main__":
    main()
