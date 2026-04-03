$baseUrl = 'https://cantiques.yapper.fr'
$playerUrl = 'https://cantiques.yapper.fr/player/?filter=CV'
$outputDir = 'C:\Users\Josh\StudioProjects\ChantLouange\audio_CV'

if (-not (Test-Path $outputDir)) {
    New-Item -ItemType Directory -Path $outputDir | Out-Null
    Write-Host "Dossier cree: $outputDir"
}

Write-Host "Recuperation de la liste des audios..."
$response = Invoke-WebRequest -Uri $playerUrl -UseBasicParsing
$html = $response.Content

$pattern = 'data-src="([^"]+)"'
$matches_list = [regex]::Matches($html, $pattern)

Write-Host "Trouve $($matches_list.Count) fichiers audio"

$audioFiles = @()
foreach ($m in $matches_list) {
    $audioFiles += $m.Groups[1].Value
}

Write-Host ""
Write-Host "Liste des fichiers:"
$audioFiles | ForEach-Object { Write-Host "  - $_" }

Write-Host ""
Write-Host "Demarrage du telechargement..."
$total = $audioFiles.Count
$count = 0

foreach ($src in $audioFiles) {
    $count++
    $fileUrl = "$baseUrl/media/$src"
    $fileName = $src -replace '/', '_'
    $outputPath = Join-Path $outputDir $fileName
    
    if (Test-Path $outputPath) {
        Write-Host "[$count/$total] Deja telecharge: $fileName"
        continue
    }
    
    Write-Host "[$count/$total] Telechargement: $fileName"
    try {
        Invoke-WebRequest -Uri $fileUrl -OutFile $outputPath -UseBasicParsing
        $sizeKB = [math]::Round((Get-Item $outputPath).Length / 1024)
        Write-Host "  OK ($sizeKB KB)"
    } catch {
        Write-Host "  ERREUR: $_"
    }
}

Write-Host ""
Write-Host "Termine! $count fichiers traites dans $outputDir"
