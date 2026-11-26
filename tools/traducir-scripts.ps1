# Genera scripts/it-mensajes.json: traducciones italianas de los avisos de PowerShell.
# common.ps1 los usa para añadir el campo "it" a progreso y registros.
$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $PSScriptRoot
$scripts = Join-Path $root 'scripts'

$textos = [Collections.Generic.List[string]]::new()
$vistos = [Collections.Generic.HashSet[string]]::new()
foreach ($f in Get-ChildItem $scripts -Filter *.ps1) {
  $c = Get-Content $f.FullName -Raw -Encoding UTF8
  foreach ($linea in ($c -split "`n")) {
    if ($linea -notmatch 'Send-(Progress|Log)') { continue }
    foreach ($m in [regex]::Matches($linea, "'([^'\r\n]{5,160})'")) {
      $v = $m.Groups[1].Value
      if ($v -match '[\|{}\\]' -or $v -match '^\s*$') { continue }
      if ($v -match '( could | the | was | with | permission|Looking|Reading|Removing|Installing)') { continue }
      if ($v -notmatch '[áéíóúñ¿¡]' -and $v -notmatch '\b(el|la|los|las|de|que|no|con|para|por|al|una|un|en|su|sin|ya|más)\b') { continue }
      if ($vistos.Add($v)) { $textos.Add($v) }
    }
  }
}

$dict = [ordered]@{}
$fallos = 0
for ($i = 0; $i -lt $textos.Count; $i += 20) {
  $batch = @($textos | Select-Object -Skip $i -First 20)
  $q = $batch -join "`n"
  try {
    $url = 'https://translate.googleapis.com/translate_a/single?client=gtx&sl=es&tl=it&dt=t&q=' + [uri]::EscapeDataString($q)
    $r = Invoke-RestMethod -Uri $url -TimeoutSec 30
    $lineas = (($r[0] | ForEach-Object { $_[0] }) -join '') -split "`n"
    if ($lineas.Count -ne $batch.Count) { throw "líneas=$($lineas.Count) esperadas=$($batch.Count)" }
    for ($j = 0; $j -lt $batch.Count; $j++) {
      if ($lineas[$j] -and $lineas[$j] -ne $batch[$j]) { $dict[$batch[$j]] = $lineas[$j] }
    }
  } catch { $fallos++ }
}
$json = $dict | ConvertTo-Json -Depth 4
[IO.File]::WriteAllText((Join-Path $scripts 'it-mensajes.json'), $json, (New-Object Text.UTF8Encoding($false)))
"listo: $($dict.Count) traducciones; lotes fallidos: $fallos; total: $($textos.Count)"
