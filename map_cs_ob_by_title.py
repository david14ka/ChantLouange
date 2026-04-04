"""
map_cs_ob_by_title.py
======================
Établit la correspondance CS (Crois Seulement, français) ↔ OB (Only Believe, anglais)
par TITRE uniquement (pas par numéro, car les numéros peuvent différer).

Stratégie en 2 passes :
  1. Dictionnaire manuel FR→EN pour les hymnes classiques connus
  2. rapidfuzz (token_sort_ratio) pour les hymnes restants

Produit :
  - cs_ob_mapping.json       : correspondance complète avec score de confiance
  - cs_ob_no_match.txt       : titres CS sans correspondance (à corriger manuellement)
  - unified_audio_map.json   : mis à jour avec les URLs OB pour chaque numéro CS matchée
"""

import json, re, os

try:
    from rapidfuzz import fuzz, process as rfprocess
    FUZZY_LIB = "rapidfuzz"
except ImportError:
    import difflib
    FUZZY_LIB = "difflib"

print(f"Bibliothèque fuzzy utilisée : {FUZZY_LIB}")

BASE = r"c:\Users\Josh\StudioProjects\ChantLouange"
CS_FILE       = os.path.join(BASE, r"app\src\main\java\com\davidkazad\chantlouange\datas\CS.java")
OB_FILE       = os.path.join(BASE, r"app\src\main\java\com\davidkazad\chantlouange\datas\OB.java")
OB_AUDIO_FILE = os.path.join(BASE, "hymnallibrary_ob_audio.json")
AUDIO_MAP_FILE= os.path.join(BASE, "unified_audio_map.json")
MAPPING_OUT   = os.path.join(BASE, "cs_ob_mapping.json")
NO_MATCH_OUT  = os.path.join(BASE, "cs_ob_no_match.txt")

# ─────────────────────────────────────────────────────────────────────────────
# 1. Dictionnaire manuel FR → EN (titres nettoyés, sans ponctuation)
# ─────────────────────────────────────────────────────────────────────────────
MANUAL_DICT = {
    # CS titre nettoyé (FR)        : OB titre nettoyé (EN)
    "CROIS SEULEMENT"              : "ONLY BELIEVE",
    "GRACE ETONNANTE"              : "AMAZING GRACE",
    "ILS VIENNENT"                 : "THEY COME",
    "JE LAIME"                     : "I LOVE HIM",
    "MOMENT DE PRIERE"             : "SWEET HOUR OF PRAYER",
    "OH COMBIEN JAIME JESUS"       : "OH HOW I LOVE JESUS",
    "QUAND LES RACHETES SY ASSEMBLERONT": "WHEN THE REDEEMED GATHER",
    "OH JE DESIRE LE VOIR"         : "OH I WANT TO SEE HIM",
    "NOUS NOUS SENTONS SI BIEN"    : "FEELING SO MUCH BETTER",
    "APPRENDS MOI SEIGNEUR A ATTENDRE": "TEACH ME LORD TO WAIT",
    "ALLELUIA NOUS RESSUSCITERONS" : "HALLELUJAH WE SHALL RISE",
    "PORTER UNE COURONNE"          : "WEAR A CROWN",
    "MERVEILLEUSE GRACE"           : "MARVELOUS GRACE",
    "MONT SION"                    : "ZIONS HILL",
    "NOUS MARCHONS VERS SION"      : "WERE MARCHING TO ZION",
    "PURIFIE MOI"                  : "CLEANSE ME",
    "MES BREBIS CONNAISSENT MA VOIX": "MY SHEEP KNOW MY VOICE",
    "EN AVANT SOLDATS CHRETIENS"   : "ONWARD CHRISTIAN SOLDIER",
    "PARLONS DE JESUS"             : "LETS TALK ABOUT JESUS",
    "QUAND NOTRE SEIGNEUR REVIENDRA": "WHEN OUR LORD SHALL COME",
    "CEST LA BATAILLE"             : "THE FIGHT IS ON",
    "VICTOIRE EN JESUS"            : "VICTORY IN JESUS",
    "MA FOI REGARDE A TOI"         : "MY FAITH LOOKS UP TO THEE",
    "VIVANT PAR LA FOI"            : "LIVING BY FAITH",
    "JABANDONNE TOUT"              : "I SURRENDER ALL",
    "QUAND JATTEINDRAI CETTE CITE" : "WHEN I REACH THAT CITY",
    "PLUS TARD"                    : "FARTHER ALONG",
    "REMPLIS MOI DAMOUR"           : "FILL MY WAY WITH LOVE",
    "JAI RESOLU"                   : "I AM RESOLVED",
    "LIEUX PLUS HAUTS"             : "HIGHER GROUND",
    "DEBOUT SUR LES PROMESSES"     : "STANDING ON THE PROMISES",
    "DANS PEU DE TEMPS LE MATIN VIENDRA": "BY AND BY MORNING COMES",
    "ROCHER DAGES"                 : "ROCK OF AGES",
    "QUAND JE VERRAI LE SANG"      : "WHEN I SEE THE BLOOD",
    "LAMOUR MA SAUVE"              : "LOVE LIFTED ME",
    "PLUIES DE BENEDICTION"        : "SHOWERS OF BLESSING",
    "COMME TU VEUX SEIGNEUR"       : "HAVE THINE OWN WAY LORD",
    "ATTIRE MOI PLUS PRES"         : "DRAW ME NEARER",
    "JE SUIS MARQUE"               : "I AM MARKED",
    "NE SERAIT CE PAS MERVEILLEUX" : "WONT IT BE WONDERFUL",
    "SUR LES AILES DUNE COLOMBE"   : "WINGS OF A DOVE",
    "JAI ENVIE DY ALLER"           : "I FEEL LIKE TRAVELING ON",
    "VIENS DINER"                  : "COME AND DINE",
    "DIEU NOUS CONDUIT"            : "GOD LEADS US ALONG",
    "REPOSANT SUR LES BRAS ETERNELS": "LEANING ON LASTING ARMS",
    "LUMIERE"                      : "SUNLIGHT",
    "JE SAIS EN QUI JAI CRU"       : "I KNOW WHOM I BELIEVED",
    "APPORTANT LES FRUITS"         : "BRINGING IN THE SHEAVES",
    "UN MATIN DORE"                : "SOME GOLDEN DAYBREAK",
    "POURRONS NOUS A LA RIVIERE"   : "SHALL WE GATHER AT RIVER",
    "GLOIRE A SON NOM"             : "GLORY TO HIS NAME",
    "IL MA SORTI"                  : "HE BROUGHT ME OUT",
    "BIENTOT DANS LA DOUCEUR"      : "SWEET BY AND BY",
    "Y A UNE FONTAINE"             : "THERE IS A FOUNTAIN",
    "OH QUEL PRECIEUX AMOUR"       : "OH WHAT PRECIOUS LOVE",
    "JESUS NE FAILLIT"             : "JESUS NEVER FAILS",
    "HYMNE DE BATAILLE DE LA REPUBLIQUE": "BATTLE HYMN OF REPUBLIC",
    "AMEN"                         : "AMEN",
}

# ─────────────────────────────────────────────────────────────────────────────
# 2. Fonctions utilitaires
# ─────────────────────────────────────────────────────────────────────────────
_KEY_PAT = re.compile(
    r'\s*[\(\[]?\s*(?:Ab|Bb|Cb|Db|Eb|Fb|Gb|'
    r'A#|B#|C#|D#|E#|F#|G#|'
    r'Ab|Bb|C|D|E|F|G|A|B)'
    r'(?:b|#)?\s*[\)\]]?\s*$',
    re.IGNORECASE
)

def clean_title(t: str) -> str:
    """Retire numéro de chanson en tête, clé musicale en fin, ponctuation."""
    t = re.sub(r'^\d+[\.\s]+', '', t)         # "22. " ou "22."
    t = _KEY_PAT.sub('', t)                   # tonalité à la fin
    t = re.sub(r"[^A-Za-z0-9À-ÿ\s]", " ", t) # ponctuation → espace
    t = re.sub(r'\s+', ' ', t).strip().upper()
    # Normalisation de base (accents fréquents)
    replacements = {
        "É":"E","È":"E","Ê":"E","Ë":"E",
        "À":"A","Â":"A","Ä":"A","Á":"A",
        "Î":"I","Ï":"I","Í":"I",
        "Ô":"O","Ö":"O","Ó":"O",
        "Ù":"U","Û":"U","Ü":"U","Ú":"U",
        "Ç":"C","Ñ":"N",
        "'":"","'":"",
    }
    for src, dst in replacements.items():
        t = t.replace(src, dst)
    t = re.sub(r'\s+', ' ', t).strip()
    return t

PAGE_PAT = re.compile(
    r'new\s+Page\s*\(\s*(\d+)\s*,\s*"[^"]*"\s*,\s*"([^"]+)"',
    re.DOTALL
)

def parse_java(filepath):
    """Extrait (numéro, titre_brut, titre_nettoyé) depuis un fichier .java."""
    with open(filepath, encoding="utf-8") as f:
        content = f.read()
    songs = []
    for m in PAGE_PAT.finditer(content):
        num   = int(m.group(1))
        raw   = m.group(2).strip()
        clean = clean_title(raw)
        songs.append({"num": num, "raw": raw, "clean": clean})
    return songs

def fuzzy_best(query, choices_map, threshold=55):
    """
    choices_map : {clean_title: original_data}
    Retourne (best_key, score) ou (None, 0)
    """
    if FUZZY_LIB == "rapidfuzz":
        result = rfprocess.extractOne(
            query,
            list(choices_map.keys()),
            scorer=fuzz.token_sort_ratio,
            score_cutoff=threshold
        )
        if result:
            return result[0], result[1]
        return None, 0
    else:
        matches = difflib.get_close_matches(query, list(choices_map.keys()),
                                            n=1, cutoff=threshold/100)
        if matches:
            score = int(difflib.SequenceMatcher(None, query, matches[0]).ratio()*100)
            return matches[0], score
        return None, 0

# ─────────────────────────────────────────────────────────────────────────────
# 3. Parsing
# ─────────────────────────────────────────────────────────────────────────────
print("\n📖 Parsing CS.java ...")
cs_songs = parse_java(CS_FILE)
print(f"   {len(cs_songs)} chansons CS trouvées")

print("📖 Parsing OB.java ...")
ob_songs = parse_java(OB_FILE)
print(f"   {len(ob_songs)} chansons OB trouvées")

# Index OB : titre nettoyé → données
ob_by_clean = {s["clean"]: s for s in ob_songs}

# ─────────────────────────────────────────────────────────────────────────────
# 4. Chargement audio OB
# ─────────────────────────────────────────────────────────────────────────────
with open(OB_AUDIO_FILE, encoding="utf-8") as f:
    ob_audio_list = json.load(f)

ob_audio_by_num = {}
for entry in ob_audio_list:
    num = int(entry["number"])
    urls = []
    seen = set()
    for key in ("piano_midi", "instru_midi"):
        url = entry.get(key, "").strip()
        if url and url not in seen:
            urls.append(url)
            seen.add(url)
    ob_audio_by_num[num] = urls

print(f"🎵 {len(ob_audio_by_num)} entrées audio OB chargées")

# ─────────────────────────────────────────────────────────────────────────────
# 5. Correspondance CS ↔ OB
# ─────────────────────────────────────────────────────────────────────────────
mapping     = []   # résultat complet
no_match    = []   # CS sans correspondance
audio_pairs = []   # (cs_num, ob_num) pour les matchés avec audio

for cs in cs_songs:
    result = {
        "cs_num"   : cs["num"],
        "cs_title" : cs["raw"],
        "cs_clean" : cs["clean"],
        "ob_num"   : None,
        "ob_title" : None,
        "ob_clean" : None,
        "method"   : None,
        "score"    : 0,
        "audio_urls": [],
    }

    # Passe 1 : dictionnaire manuel
    ob_clean_match = MANUAL_DICT.get(cs["clean"])
    if ob_clean_match and ob_clean_match in ob_by_clean:
        ob = ob_by_clean[ob_clean_match]
        result.update({
            "ob_num"  : ob["num"],
            "ob_title": ob["raw"],
            "ob_clean": ob["clean"],
            "method"  : "manual_dict",
            "score"   : 100,
        })
    else:
        # Passe 2 : fuzzy
        best_key, score = fuzzy_best(cs["clean"], ob_by_clean, threshold=55)
        if best_key:
            ob = ob_by_clean[best_key]
            result.update({
                "ob_num"  : ob["num"],
                "ob_title": ob["raw"],
                "ob_clean": ob["clean"],
                "method"  : "fuzzy",
                "score"   : score,
            })

    if result["ob_num"] is not None:
        urls = ob_audio_by_num.get(result["ob_num"], [])
        result["audio_urls"] = urls
        audio_pairs.append((cs["num"], result["ob_num"], urls))
    else:
        no_match.append(cs)

    mapping.append(result)

# ─────────────────────────────────────────────────────────────────────────────
# 6. Statistiques
# ─────────────────────────────────────────────────────────────────────────────
matched       = [r for r in mapping if r["ob_num"] is not None]
by_dict       = [r for r in matched if r["method"] == "manual_dict"]
by_fuzzy      = [r for r in matched if r["method"] == "fuzzy"]
with_audio    = [r for r in matched if r["audio_urls"]]

print(f"\n{'='*55}")
print(f"  Total CS               : {len(cs_songs)}")
print(f"  Matchés (dict manuel)  : {len(by_dict)}")
print(f"  Matchés (fuzzy)        : {len(by_fuzzy)}")
print(f"  Sans correspondance    : {len(no_match)}")
print(f"  Avec audio OB          : {len(with_audio)}")
print(f"{'='*55}")

# ─────────────────────────────────────────────────────────────────────────────
# 7. Sauvegarde cs_ob_mapping.json
# ─────────────────────────────────────────────────────────────────────────────
with open(MAPPING_OUT, "w", encoding="utf-8") as f:
    json.dump(mapping, f, ensure_ascii=False, indent=2)
print(f"\n✅ Mapping sauvegardé → {MAPPING_OUT}")

# ─────────────────────────────────────────────────────────────────────────────
# 8. Sauvegarde cs_ob_no_match.txt
# ─────────────────────────────────────────────────────────────────────────────
with open(NO_MATCH_OUT, "w", encoding="utf-8") as f:
    f.write(f"Chansons CS sans correspondance OB ({len(no_match)})\n")
    f.write("="*55 + "\n")
    for s in no_match:
        f.write(f"  CS #{s['num']:3d}  {s['raw']}\n")
print(f"✅ Sans correspondance   → {NO_MATCH_OUT}")

# ─────────────────────────────────────────────────────────────────────────────
# 9. Mise à jour unified_audio_map.json
#    (on ne réécrase PAS les URLs déjà présentes)
# ─────────────────────────────────────────────────────────────────────────────
with open(AUDIO_MAP_FILE, encoding="utf-8") as f:
    audio_map = json.load(f)

# Reconstruire à partir de zéro pour cette clé
# (on garde tout ce qui existait, on ajoute les OB URLs si absentes)
added = updated = already = 0
for cs_num, ob_num, urls in audio_pairs:
    key = str(cs_num)
    existing = audio_map.get(key, [])
    new_urls = [u for u in urls if u not in existing]
    if new_urls:
        audio_map[key] = existing + new_urls
        if existing:
            updated += 1
        else:
            added += 1
    else:
        already += 1

print(f"\n🗺  audio_map : {added} nouvelles entrées, {updated} mises à jour, {already} déjà couvertes")

with open(AUDIO_MAP_FILE, "w", encoding="utf-8") as f:
    json.dump(audio_map, f, ensure_ascii=False, indent=2)
print(f"✅ unified_audio_map.json mis à jour ({len(audio_map)} entrées)")

# ─────────────────────────────────────────────────────────────────────────────
# 10. Aperçu des correspondances fuzzy (pour vérification)
# ─────────────────────────────────────────────────────────────────────────────
if by_fuzzy:
    print(f"\n🔍 Correspondances fuzzy (score, CS → OB) :")
    for r in sorted(by_fuzzy, key=lambda x: x["score"]):
        flag = "⚠️ " if r["score"] < 70 else "   "
        print(f"  {flag}[{int(r['score']):3d}] CS#{r['cs_num']:3d} '{r['cs_title']}'"
              f"\n         -> OB#{r['ob_num']:3d} '{r['ob_title']}'")

