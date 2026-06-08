# Gearhead Builder API

API FastAPI para planejamento e orçamentação de modificações automotivas.

## Instalação

```bash
pip install -r requirements.txt
```

## Executar Localmente

```bash
python -m uvicorn app.main:app --reload
```

A API estará disponível em: `http://localhost:8000`
Documentação Swagger: `http://localhost:8000/docs`

## Deploy no Google Cloud Run

```bash
# Autenticar no Google Cloud
gcloud auth login

# Criar um repositório Artifact Registry
gcloud artifacts repositories create gearhead-api --repository-format=docker --location=us-central1

# Construir e fazer push da imagem Docker
gcloud builds submit --tag us-central1-docker.pkg.dev/[PROJECT_ID]/gearhead-api/gearhead-api

# Fazer deploy no Cloud Run
gcloud run deploy gearhead-api --image us-central1-docker.pkg.dev/[PROJECT_ID]/gearhead-api/gearhead-api --platform managed --region us-central1
```

## Endpoints Disponíveis

- `GET /carros` - Lista de carros
- `GET /pecas/{carro_id}` - Peças compatíveis com um carro
- `POST /projetos` - Criar novo projeto
- `GET /projetos` - Listar projetos
- `PUT /projetos/{id}` - Atualizar projeto
- `DELETE /projetos/{id}` - Deletar projeto
