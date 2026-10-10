param(
    [Parameter(Mandatory = $true)][string]$Url,
    [int]$Retries = 30,
    [int]$DelaySeconds = 4,
    [int]$InitialDelaySeconds = 10
)

# Give Tomcat a moment to notice the new WAR and redeploy it
Start-Sleep -Seconds $InitialDelaySeconds

for ($i = 1; $i -le $Retries; $i++) {
    try {
        $response = Invoke-WebRequest -UseBasicParsing -Uri $Url -TimeoutSec 5
        if ($response.StatusCode -eq 200 -and $response.Content -match '"status":"UP"') {
            Write-Host "Health check passed: $Url"
            exit 0
        }
    } catch {
        # application not ready yet
    }
    Write-Host "Waiting for application ($i/$Retries)..."
    Start-Sleep -Seconds $DelaySeconds
}

Write-Host "Health check FAILED: $Url"
exit 1
