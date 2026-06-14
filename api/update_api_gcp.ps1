$ErrorActionPreference = "Stop"

# Pede a chave da API do Gemini para o usuário
$geminiKey = Read-Host -Prompt "Cole aqui a sua GEMINI_API_KEY (ou aperte Enter se já configurou no Cloud Run antes)"

$dbUrl = "postgresql+psycopg2://postgres:Gearhead2026Password!@/appmobile?host=/cloudsql/ap2-devmobile:us-central1:appmobile-db"

if ([string]::IsNullOrWhiteSpace($geminiKey)) {
    Write-Host "Atualizando a API no Cloud Run (mantendo as variaveis de ambiente atuais)..."
    gcloud run deploy appmobile-api --source . --region us-central1 --allow-unauthenticated --update-env-vars="DATABASE_URL=$dbUrl" --add-cloudsql-instances=ap2-devmobile:us-central1:appmobile-db --quiet
} else {
    Write-Host "Atualizando a API no Cloud Run com a nova GEMINI_API_KEY..."
    gcloud run deploy appmobile-api --source . --region us-central1 --allow-unauthenticated --update-env-vars="DATABASE_URL=$dbUrl,GEMINI_API_KEY=$geminiKey" --add-cloudsql-instances=ap2-devmobile:us-central1:appmobile-db --quiet
}

Write-Host "Pegando a URL do Cloud Run atualizada..."
$url = gcloud run services describe appmobile-api --region us-central1 --format="value(status.url)"
Write-Host "PRONTO! API atualizada com sucesso em: $url"
