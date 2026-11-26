# Prepara Lumen para publicarlo: reemplaza los marcadores TU-USUARIO/tuusuario
# por tu usuario real de GitHub en todos los archivos del proyecto.
# Uso:
#   powershell -ExecutionPolicy Bypass -File tools/preparar-publicacion.ps1 -Usuario TU_USUARIO
param([Parameter(Mandatory = $true)][string]$Usuario)

$ErrorActionPreference = 'Stop'
if ($Usuario -notmatch '^[A-Za-z0-9](?:[A-Za-z0-9-]{0,37}[A-Za-z0-9])?$') {
  throw "Usuario de GitHub no válido: '$Usuario' (solo letras, números y guiones; máximo 39 caracteres)."
}

$root = Split-Path -Parent $PSScriptRoot
$lower = $Usuario.ToLowerInvariant()
$cambios = 0
$archivos = 0

Get-ChildItem -LiteralPath $root -Recurse -File |
  Where-Object {
    $_.FullName -notmatch '\\node_modules\\' -and
    $_.FullName -notmatch '\\dist\\' -and
    $_.Extension -in @('.json', '.js', '.html', '.md', '.txt', '.xml', '.yml', '.yaml', '.nsh', '.svg')
  } |
  ForEach-Object {
    $t = Get-Content -LiteralPath $_.FullName -Raw -Encoding UTF8
    $n = $t.Replace('TU-USUARIO', $Usuario).Replace('tuusuario', $lower)
    if ($n -ne $t) {
      [IO.File]::WriteAllText($_.FullName, $n, (New-Object Text.UTF8Encoding($false)))
      $archivos++
      $cambios += ([regex]::Matches($t, 'TU-USUARIO|tuusuario')).Count
    }
  }

"Listo: $cambios marcadores reemplazados en $archivos archivos."
"Usuario: $Usuario"
"Web: https://$Usuario.github.io/Lumen/"
"Repo: https://github.com/$Usuario/Lumen"
"AVISO: sustituye tambien docs/google911daa092a5d70db.html por el archivo de verificacion que te de Google Search Console."
