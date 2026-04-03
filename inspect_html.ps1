$html = (Invoke-WebRequest -Uri 'https://cantiques.yapper.fr/player/?filter=CV' -UseBasicParsing).Content
# Find the playlist section
$idx = $html.IndexOf('id="playlist"')
$html.Substring($idx, 3000)
