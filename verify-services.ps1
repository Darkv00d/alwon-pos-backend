# Verify All Services Health
# Usage: .\verify-services.ps1

Write-Host "======================================" -ForegroundColor Cyan
Write-Host "Verifying Alwon POS Services" -ForegroundColor Cyan
Write-Host "======================================" -ForegroundColor Cyan
Write-Host ""

$services = @(
    @{Name = "API Gateway"; Port = 8080; Path = "/actuator/health" },
    @{Name = "Session Service"; Port = 8081; Path = "/health" },
    @{Name = "Cart Service"; Port = 8082; Path = "/health" },
    @{Name = "Product Service"; Port = 8083; Path = "/health" },
    @{Name = "Payment Service"; Port = 8084; Path = "/health" },
    @{Name = "Camera Service"; Port = 8085; Path = "/health" },
    @{Name = "Access Service"; Port = 8086; Path = "/health" },
    @{Name = "Inventory Service"; Port = 8087; Path = "/health" },
    @{Name = "WebSocket Server"; Port = 8090; Path = "/actuator/health" }
)

$total = $services.Count
$online = 0
$offline = 0

foreach ($service in $services) {
    $url = "http://localhost:$($service.Port)$($service.Path)"
    
    try {
        $response = Invoke-WebRequest -Uri $url -TimeoutSec 2 -UseBasicParsing -ErrorAction Stop
        
        if ($response.StatusCode -eq 200) {
            Write-Host "✓ $($service.Name) (Port $($service.Port)) - ONLINE" -ForegroundColor Green
            $online++
        }
        else {
            Write-Host "✗ $($service.Name) (Port $($service.Port)) - ERROR" -ForegroundColor Red
            $offline++
        }
    }
    catch {
        Write-Host "✗ $($service.Name) (Port $($service.Port)) - OFFLINE" -ForegroundColor Red
        $offline++
    }
}

Write-Host ""
Write-Host "======================================" -ForegroundColor Cyan
Write-Host "Status Summary:" -ForegroundColor Cyan
Write-Host "  Online:  $online / $total" -ForegroundColor Green
Write-Host "  Offline: $offline / $total" -ForegroundColor Red
Write-Host "======================================" -ForegroundColor Cyan

if ($offline -eq 0) {
    Write-Host "All services are running! 🎉" -ForegroundColor Green
}
else {
    Write-Host "$offline service(s) are offline." -ForegroundColor Yellow
}
