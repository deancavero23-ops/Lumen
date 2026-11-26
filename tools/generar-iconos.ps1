# Genera build/icon.png, build/icon.ico y renderer/icon.png desde la marca Lumen.
# Usa solo System.Drawing (Windows PowerShell 5.1), sin dependencias externas.
Add-Type -AssemblyName System.Drawing

function New-LumenBitmap([int]$size) {
  $bmp = New-Object System.Drawing.Bitmap($size, $size, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
  $g = [System.Drawing.Graphics]::FromImage($bmp)
  $g.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::HighQuality
  $g.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
  $g.Clear([System.Drawing.Color]::Transparent)
  $s = $size / 64.0

  function S([double]$v) { [single]($v * $s) }

  # Fondo redondeado con degradé rojo
  $rect = New-Object System.Drawing.RectangleF (S 2), (S 2), (S 60), (S 60)
  $path = New-Object System.Drawing.Drawing2D.GraphicsPath
  $r = S 14
  $path.AddArc($rect.X, $rect.Y, $r, $r, 180, 90)
  $path.AddArc($rect.Right - $r, $rect.Y, $r, $r, 270, 90)
  $path.AddArc($rect.Right - $r, $rect.Bottom - $r, $r, $r, 0, 90)
  $path.AddArc($rect.X, $rect.Bottom - $r, $r, $r, 90, 90)
  $path.CloseFigure()
  $brush = New-Object System.Drawing.Drawing2D.LinearGradientBrush($rect, [System.Drawing.Color]::FromArgb(255,255,90,82), [System.Drawing.Color]::FromArgb(255,143,29,25), 45)
  $g.FillPath($brush, $path)

  # Letra L negra (proporciones del SVG)
  $lp = New-Object System.Drawing.Drawing2D.GraphicsPath
  $pts = @(@(21,14),@(30,14),@(30,42),@(46,42),@(46,50),@(21,50))
  $gp = $pts | ForEach-Object { New-Object System.Drawing.PointF (S $_[0]), (S $_[1]) }
  $lp.AddPolygon($gp)
  $g.FillPath([System.Drawing.Brushes]::Black, $lp)

  # Chispa blanca arriba a la derecha
  $sp = New-Object System.Drawing.Drawing2D.GraphicsPath
  $pts2 = @(@(48,5),@(49,12),@(56,16),@(49,17),@(48,24),@(47,17),@(40,16),@(47,12))
  $gp2 = $pts2 | ForEach-Object { New-Object System.Drawing.PointF (S $_[0]), (S $_[1]) }
  $sp.AddPolygon($gp2)
  $g.FillPath([System.Drawing.Brushes]::White, $sp)

  $brush.Dispose(); $path.Dispose(); $lp.Dispose(); $sp.Dispose(); $g.Dispose()
  return $bmp
}

function Save-Png([System.Drawing.Bitmap]$bmp, [string]$path) {
  $bmp.Save($path, [System.Drawing.Imaging.ImageFormat]::Png)
}

function Save-Ico([string]$path) {
  $sizes = @(16,24,32,48,64,128,256)
  $images = @()
  foreach ($n in $sizes) {
    $b = New-LumenBitmap $n
    $ms = New-Object System.IO.MemoryStream
    $b.Save($ms, [System.Drawing.Imaging.ImageFormat]::Png)
    $images += ,@($n, $ms.ToArray())
    $b.Dispose(); $ms.Dispose()
  }
  $fs = [System.IO.File]::Create($path)
  $bw = New-Object System.IO.BinaryWriter($fs)
  $bw.Write([uint16]0); $bw.Write([uint16]1); $bw.Write([uint16]$images.Count)
  $offset = 6 + 16 * $images.Count
  foreach ($img in $images) {
    $n = [int]$img[0]; $data = [byte[]]$img[1]
    $bw.Write([byte]$(if ($n -eq 256) { 0 } else { $n }))
    $bw.Write([byte]$(if ($n -eq 256) { 0 } else { $n }))
    $bw.Write([byte]0); $bw.Write([byte]0)
    $bw.Write([uint16]1); $bw.Write([uint16]32)
    $bw.Write([uint32]$data.Length); $bw.Write([uint32]$offset)
    $offset += $data.Length
  }
  foreach ($img in $images) { $bw.Write([byte[]]$img[1]) }
  $bw.Close(); $fs.Close()
}

$root = Split-Path -Parent $PSScriptRoot
$big = New-LumenBitmap 256
Save-Png $big (Join-Path $root 'build\icon.png')
Save-Png $big (Join-Path $root 'renderer\icon.png')
$big.Dispose()
Save-Ico (Join-Path $root 'build\icon.ico')
'escrito: build/icon.png, build/icon.ico, renderer/icon.png'
