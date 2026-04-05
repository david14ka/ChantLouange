"""
Scrape PDF partition links for 'Chant de Victoire' from cantiquest.org
and build a pdf_map.json file keyed by "book_number" (e.g. "2_5" for CV hymn 5).

The book ID for CV in the app is 2 (see CV.java: super(2, "Chant de Victoire", ...)).
"""
import json
import re
import urllib.request
from html.parser import HTMLParser

BASE_URL = "https://www.cantiquest.org"
CV_PAGE  = "https://www.cantiquest.org/CV.htm"

class CVTableParser(HTMLParser):
    def __init__(self):
        super().__init__()
        self.rows = []
        self._in_tr = False
        self._cells = []
        self._current_cell = ""
        self._current_links = []
        self._in_td = False

    def handle_starttag(self, tag, attrs):
        attrs = dict(attrs)
        if tag == "tr":
            self._in_tr = True
            self._cells = []
        elif tag == "td" and self._in_tr:
            self._in_td = True
            self._current_cell = ""
            self._current_links = []
        elif tag == "a" and self._in_td:
            href = attrs.get("href", "")
            self._current_links.append(href)

    def handle_endtag(self, tag):
        if tag == "td" and self._in_td:
            self._cells.append({
                "text": self._current_cell.strip(),
                "links": self._current_links[:]
            })
            self._in_td = False
        elif tag == "tr" and self._in_tr:
            if self._cells:
                self.rows.append(self._cells[:])
            self._in_tr = False
            self._cells = []

    def handle_data(self, data):
        if self._in_td:
            self._current_cell += data

def build_absolute_url(href):
    if href.startswith("http"):
        return href
    return BASE_URL + "/" + href.lstrip("/")

def scrape_cv_pdfs():
    print(f"Fetching {CV_PAGE}...")
    req = urllib.request.Request(CV_PAGE, headers={"User-Agent": "Mozilla/5.0"})
    with urllib.request.urlopen(req, timeout=15) as resp:
        html = resp.read().decode("utf-8", errors="replace")

    parser = CVTableParser()
    parser.feed(html)

    pdf_map = {}
    results = []

    for row in parser.rows:
        if not row:
            continue

        # First cell typically contains the number
        num_text = row[0]["text"].strip() if row else ""
        # Try to extract a number
        m = re.match(r"^(\d+)", num_text)
        if not m:
            continue
        number = int(m.group(1))

        # Title is usually in the second cell
        title = row[1]["text"].strip() if len(row) > 1 else ""

        # Look for PDF links in any cell
        pdf_a4 = None
        pdf_a5 = None
        for cell in row:
            for link in cell["links"]:
                if not link:
                    continue
                lower = link.lower()
                if "a4-avecmusique.pdf" in lower or "a4.pdf" in lower:
                    pdf_a4 = build_absolute_url(link)
                elif "a5-avecmusique.pdf" in lower or "a5.pdf" in lower:
                    pdf_a5 = build_absolute_url(link)
                elif lower.endswith(".pdf"):
                    if pdf_a4 is None:
                        pdf_a4 = build_absolute_url(link)

        results.append({
            "number": number,
            "title": title,
            "pdf_a4": pdf_a4,
            "pdf_a5": pdf_a5,
        })

        # Key format: "bookId_hymnNumber" — CV book ID is 2
        key = f"2_{number}"
        if pdf_a4:
            pdf_map[key] = {"pdf_a4": pdf_a4, "pdf_a5": pdf_a5}

    print(f"Found {len(results)} hymns, {len(pdf_map)} with PDF links.")

    # Save the full details
    with open("cantiquest_cv_pdfs.json", "w", encoding="utf-8") as f:
        json.dump(results, f, ensure_ascii=False, indent=2)
    print("Saved full details to cantiquest_cv_pdfs.json")

    # Save the app-ready pdf_map
    with open("app/src/main/res/raw/pdf_map.json", "w", encoding="utf-8") as f:
        json.dump(pdf_map, f, ensure_ascii=False, indent=2)
    print("Saved app pdf_map to app/src/main/res/raw/pdf_map.json")

    # Print a few samples
    for r in results[:10]:
        print(r)

if __name__ == "__main__":
    scrape_cv_pdfs()
