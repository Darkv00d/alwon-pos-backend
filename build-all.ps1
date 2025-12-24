# Build All Alwon POS Backend Services
# Usage: .\build-all.ps1

Write-Host "======================================" -ForegroundColor Cyan
Write-Host "Building Alwon POS Backend Services" -ForegroundColor Cyan
Write-Host "======================================" -ForegroundColor Cyan
Write-Host ""

$services = @(
    "api-gateway",
    "session-service",
    "cart-service",
    "product-service",
    "payment-service",
    "camera-service",
    "access-service",
    "inventory-service",
    "websocket-server"
)

$total = $services.Count
$current = 0
$success = 0
$failed = 0

foreach ($service in $services) {
    $current++
    Write-Host "[$current/$total] Building $service..." -ForegroundColor Yellow
    
    Push-Location $service
    
    $output = mvn clean package -DskipTests 2>&1
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Success: $service built successfully" -ForegroundColor Green
        $success++
    }
    else {
        Write-Host "Failed: $service build failed" -ForegroundColor Red
        $failed++
    }
    
    Pop-Location
    Write-Host ""
}

Write-Host "======================================" -ForegroundColor Cyan
Write-Host "Build Summary:" -ForegroundColor Cyan
Write-Host "  Success: $success / $total" -ForegroundColor Green
Write-Host "  Failed:  $failed / $total" -ForegroundColor Red
Write-Host "======================================" -ForegroundColor Cyan

if ($failed -eq 0) {
    Write-Host "All services built successfully!" -ForegroundColor Green
    exit 0
}
else {
    Write-Host "Some services failed to build." -ForegroundColor Red
    exit 1
}
