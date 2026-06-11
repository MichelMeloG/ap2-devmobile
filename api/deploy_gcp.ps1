$ErrorActionPreference = "Stop"
$newPath = [Environment]::GetEnvironmentVariable("Path", [EnvironmentVariableTarget]::User) + ";" + [Environment]::GetEnvironmentVariable("Path", [EnvironmentVariableTarget]::Machine)
$env:Path = $newPath

Write-Host "Criando instancia do Cloud SQL (isso pode levar 5 a 10 minutos)..."
gcloud sql instances create appmobile-db --database-version=POSTGRES_15 --tier=db-f1-micro --region=us-central1 --quiet

Write-Host "Definindo senha para o usuario postgres..."
gcloud sql users set-password postgres --instance=appmobile-db --password=Gearhead2026Password! --quiet

Write-Host "Criando banco de dados appmobile..."
gcloud sql databases create appmobile --instance=appmobile-db --quiet

Write-Host "Fazendo o deploy da API no Cloud Run..."
$dbUrl = "postgresql+psycopg2://postgres:Gearhead2026Password!@/appmobile?host=/cloudsql/ap2-devmobile:us-central1:appmobile-db"
gcloud run deploy appmobile-api --source . --region us-central1 --allow-unauthenticated --set-env-vars="DATABASE_URL=$dbUrl" --add-cloudsql-instances=ap2-devmobile:us-central1:appmobile-db --quiet

Write-Host "Pegando a URL do Cloud Run..."
$url = gcloud run services describe appmobile-api --region us-central1 --format="value(status.url)"
Write-Host "CLOUD_RUN_URL=$url"
