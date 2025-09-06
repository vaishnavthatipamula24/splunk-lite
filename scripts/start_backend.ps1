# Robust backend start script
Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

Write-Output "Working dir: $(Get-Location)"

# Kill any process listening on 8080
$lines = netstat -ano | Select-String ":8080" -SimpleMatch
if ($lines) {
  foreach ($l in $lines) {
    $parts = ($l -split '\s+') | Where-Object { $_ -ne '' }
    $pid = $parts[-1]
    Write-Output "Found PID $pid using port 8080; attempting taskkill..."
    cmd /c "taskkill /PID $pid /F" | Write-Output
  }
} else {
  Write-Output 'No process found on 8080'
}

# Prepare log
$log = Join-Path (Get-Location) 'backend.log'
if (Test-Path $log) { Remove-Item $log -Force }

Write-Output "Starting backend in background (mvn -DskipTests spring-boot:run), logs -> $log"
Start-Process -FilePath 'cmd.exe' -ArgumentList '/c','mvn -DskipTests spring-boot:run > backend.log 2>&1' -WorkingDirectory (Get-Location) -WindowStyle Hidden

# Wait for startup (60s)
$started = $false
for ($i=0; $i -lt 60; $i++) {
  Start-Sleep -Seconds 1
  if (Test-Path $log) {
    $tail = Get-Content $log -Tail 200 -Raw -ErrorAction SilentlyContinue
    if ($tail -and $tail -match 'Started App') {
      Write-Output "APP_STARTED at ${i}s"
      $started = $true
      break
    }
  }
}

if (-not $started) {
  Write-Output "App did not indicate startup within 60s; showing tail of log (if present):"
  if (Test-Path $log) { Get-Content $log -Tail 200 }
  exit 2
}

# Call debug endpoint
try {
  Write-Output "Calling http://localhost:8080/api/debug/logs?size=10"
  $r = Invoke-RestMethod -Uri http://localhost:8080/api/debug/logs?size=10 -Method Get -TimeoutSec 10
  Write-Output 'DEBUG_RESPONSE_JSON:'
  $r | ConvertTo-Json -Depth 5
} catch {
  Write-Output "HTTP ERROR: $($_.Exception.Message)"
  Write-Output "Tail of backend.log:"; Get-Content $log -Tail 200
  exit 3
}
