$playerUrl = 'https://cantiques.yapper.fr/player/?filter=CV'
$html = (Invoke-WebRequest -Uri $playerUrl -UseBasicParsing).Content

# Pattern: <a href="#" data-url="/CV/CV_005.html" data-src="hymns/mp3/CV/CV_005-..."
$pattern = 'data-url="(/CV/CV_([^"]+)\.html)"\s+data-src="([^"]+)"'
$matches_list = [regex]::Matches($html, $pattern)

Write-Host "Found $($matches_list.Count) audio entries"

$grouped = @{}
foreach ($m in $matches_list) {
    $num = $m.Groups[2].Value  # e.g. "005", "147a"
    $src = $m.Groups[3].Value  # e.g. "hymns/mp3/CV/CV_005-..."
    
    # Normalize number: "005" -> "5", "147a" -> "147a"
    $num = $num -replace '^0+(\d+[a-z]?)$', '$1'
    
    if (-not $grouped.ContainsKey($num)) {
        $grouped[$num] = [System.Collections.Generic.List[string]]::new()
    }
    $grouped[$num].Add($src)
}

Write-Host "Songs with audio: $($grouped.Count)"

# Sort keys numerically
$sortedKeys = $grouped.Keys | Sort-Object { 
    if ($_ -match '^(\d+)') { [int]$Matches[1] } else { 9999 }
}

# Generate Java
$lines = [System.Collections.Generic.List[string]]::new()
$lines.Add("package com.davidkazad.chantlouange.utils;")
$lines.Add("")
$lines.Add("import java.util.ArrayList;")
$lines.Add("import java.util.HashMap;")
$lines.Add("import java.util.List;")
$lines.Add("import java.util.Map;")
$lines.Add("")
$lines.Add("/**")
$lines.Add(" * Maps CV song numbers to their streaming audio URLs.")
$lines.Add(" * Source: https://cantiques.yapper.fr/player/?filter=CV")
$lines.Add(" */")
$lines.Add("public class AudioMapper {")
$lines.Add("")
$lines.Add("    private static final String BASE_URL = `"https://cantiques.yapper.fr/media/`";")
$lines.Add("    public static final int CV_BOOK_ID = 2;")
$lines.Add("")
$lines.Add("    private static final Map<String, String[]> CV_AUDIO_MAP = new HashMap<>();")
$lines.Add("")
$lines.Add("    static {")

foreach ($key in $sortedKeys) {
    $srcs = $grouped[$key]
    $quoted = ($srcs | ForEach-Object { "`"$_`"" }) -join ", "
    $lines.Add("        CV_AUDIO_MAP.put(`"$key`", new String[]{$quoted});")
}

$lines.Add("    }")
$lines.Add("")
$lines.Add("    public static List<String> getAudioUrls(int bookId, String number) {")
$lines.Add("        List<String> result = new ArrayList<>();")
$lines.Add("        if (bookId != CV_BOOK_ID) return result;")
$lines.Add("        String key = number.trim().replaceAll(`"\\\\.\\\\s*$`", `"`").trim();")
$lines.Add("        String[] srcs = CV_AUDIO_MAP.get(key);")
$lines.Add("        if (srcs != null) {")
$lines.Add("            for (String src : srcs) result.add(BASE_URL + src);")
$lines.Add("        }")
$lines.Add("        return result;")
$lines.Add("    }")
$lines.Add("")
$lines.Add("    public static boolean hasAudio(int bookId, String number) {")
$lines.Add("        return !getAudioUrls(bookId, number).isEmpty();")
$lines.Add("    }")
$lines.Add("}")

$outputDir = 'C:\Users\Josh\StudioProjects\ChantLouange\app\src\main\java\com\davidkazad\chantlouange\utils'
if (-not (Test-Path $outputDir)) { New-Item -ItemType Directory -Path $outputDir | Out-Null }
$outputPath = "$outputDir\AudioMapper.java"
Set-Content -Path $outputPath -Value ($lines -join "`n") -Encoding UTF8
Write-Host "Generated: $outputPath"
