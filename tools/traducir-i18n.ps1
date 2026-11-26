# Genera locales/it.json a partir de los textos es de los L('es','en') del renderer.
# Usa el endpoint público de traducción; el resultado se revisa y se guarda en el repo.
# Uso: powershell -ExecutionPolicy Bypass -File tools/traducir-i18n.ps1
$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $PSScriptRoot
$renderer = Join-Path $root 'renderer\js'

$textos = [Collections.Generic.List[string]]::new()
$vistos = [Collections.Generic.HashSet[string]]::new()
foreach ($f in Get-ChildItem $renderer -Filter *.js) {
  $c = Get-Content $f.FullName -Raw -Encoding UTF8
  foreach ($m in [regex]::Matches($c, 'L\(\s*(["''`])(?<s>(?:\\.|(?!\1).)*)\1', 'Singleline')) {
    $v = $m.Groups['s'].Value -replace "`r?`n", ' '
    if ($v.Length -ge 2 -and $vistos.Add($v)) { $textos.Add($v) }
  }
}

$dict = [ordered]@{}
# El traductor se equivoca con algunas palabras sueltas; se corrigen aquí.
$overrides = @{ 'Desgaste' = 'Usura'; 'Opcional' = 'Opzionale' }
$fallos = 0
for ($i = 0; $i -lt $textos.Count; $i += 20) {
  $batch = @($textos | Select-Object -Skip $i -First 20)
  $protegidos = [Collections.Generic.List[string]]::new()
  $proteger = {
    param($texto, $pattern)
    $res = ''
    $last = 0
    foreach ($m in [regex]::Matches($texto, $pattern)) {
      $res += $texto.Substring($last, $m.Index - $last)
      $n = $protegidos.Count
      $protegidos.Add($m.Value)
      $res += "[[${n}]]"
      $last = $m.Index + $m.Length
    }
    $res + $texto.Substring($last)
  }
  # Cada línea lleva su propia etiqueta para que el traductor no mezcle textos cortos.
  $preparados = for ($j = 0; $j -lt $batch.Count; $j++) {
    $s = & $proteger $batch[$j] '\$\{[^}]*\}'
    $s = & $proteger $s '</?[a-zA-Z][^>]*>'
    "##L${j}## " + $s
  }
  $q = $preparados -join "`n"
  try {
    $url = 'https://translate.googleapis.com/translate_a/single?client=gtx&sl=es&tl=it&dt=t&q=' + [uri]::EscapeDataString($q)
    $r = Invoke-RestMethod -Uri $url -TimeoutSec 30
    $out = ($r[0] | ForEach-Object { $_[0] }) -join ''
    # El traductor puede mover la etiqueta dentro de la frase o partir una línea en dos.
    $porToken = @{}
    foreach ($m in [regex]::Matches($out, '##L(\d+)## ?([\s\S]*?)(?=##L\d+##|$)')) {
      $porToken[[int]$m.Groups[1].Value] = ($m.Groups[2].Value -replace "`r?`n$", '')
    }
    if ($porToken.Count -ne $batch.Count) { throw "textos=$($porToken.Count) esperados=$($batch.Count)" }
    for ($j = 0; $j -lt $batch.Count; $j++) {
      $tr = [string]$porToken[$j]
      for ($k = 0; $k -lt $protegidos.Count; $k++) { $tr = $tr.Replace("[[${k}]]", $protegidos[$k]) }
      if ($overrides.ContainsKey($batch[$j])) { $tr = $overrides[$batch[$j]] }
      if ($tr) { $dict[$batch[$j]] = $tr }
    }
  } catch { $fallos++; "Fallo en lote $i : $($_.Exception.Message)" }
  if (($i % 100) -eq 0) { "procesados $i / $($textos.Count)" }
}

# Correcciones finales (el servicio de traducción no las devuelve bien sueltas).
foreach ($k in $overrides.Keys) { $dict[$k] = $overrides[$k] }

$locales = Join-Path $root 'locales'
New-Item -ItemType Directory -Force -Path $locales | Out-Null
$json = $dict | ConvertTo-Json -Depth 4
[IO.File]::WriteAllText((Join-Path $locales 'it.json'), $json, (New-Object Text.UTF8Encoding($false)))
"listo: $($dict.Count) traducciones; lotes fallidos: $fallos; total textos: $($textos.Count)"
