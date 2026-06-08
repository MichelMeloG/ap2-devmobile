# 🚗 Gearhead Builder - Aplicativo de Modificações Automotivas

## 📋 Visão Geral

**Gearhead Builder** é um aplicativo mobile inovador desenvolvido para entusiastas automotivos. O sistema atua como um planejador interativo para modificações automotivas, permitindo que usuários selecionem um veículo base, definam um objetivo (ex: Track Day, Daily, Estética) e recebam recomendações de peças compatíveis com cálculo automático de custos.

---

## 🏗️ Arquitetura do Projeto

```
ap2-devmobile/
├── app-mobile/          # Frontend Android Nativo
│   ├── app/
│   │   ├── src/main/
│   │   │   ├── java/com/example/appmobile/
│   │   │   │   ├── ui/              # Activities
│   │   │   │   ├── api/             # Cliente Retrofit
│   │   │   │   ├── models/          # Classes de Modelo
│   │   │   │   └── adapters/        # Adapters RecyclerView
│   │   │   ├── res/
│   │   │   │   └── layout/          # Layouts XML
│   │   │   └── AndroidManifest.xml
│   │   └── build.gradle.kts
│   ├── gradle/
│   └── settings.gradle.kts
│
├── api/                 # Backend FastAPI (Python)
│   ├── app/
│   │   ├── main.py                  # Entry point
│   │   ├── schemas.py               # Schemas Pydantic
│   │   ├── database/
│   │   │   └── database.py          # Configuração DB
│   │   ├── models/
│   │   │   └── models.py            # Modelos SQLAlchemy
│   │   └── routes/
│   │       ├── carros.py            # Endpoints de Carros
│   │       ├── pecas.py             # Endpoints de Peças
│   │       └── projetos.py          # Endpoints de Projetos
│   ├── requirements.txt
│   ├── Dockerfile
│   ├── init_db.py                   # Script de inicialização
│   └── README.md
│
└── README.md            # Este arquivo
```

---

## 🎯 Stack Tecnológico

### Frontend (Mobile)
- **Linguagem**: Java
- **Framework**: Android SDK (API 35)
- **Padrão Arquitetural**: MVVM (Model-View-ViewModel)
- **Comunicação**: Retrofit 2 + Gson
- **Componentes UI** (Requisitos AP2):
  - ✅ TextView (Títulos e rótulos)
  - ✅ EditText (Entrada de dados)
  - ✅ Button (Ações)
  - ✅ ImageView (Ícones)
  - ✅ Spinner (Seleção de carros)
  - ✅ CheckBox (Seleção de peças)
  - ✅ Switch (Modo agressivo/track)
  - ✅ RecyclerView (Listas)
  - ✅ CardView (Cards de projetos)
  - ✅ ProgressBar (Progresso do orçamento)

### Backend (API REST)
- **Linguagem**: Python 3.11
- **Framework**: FastAPI
- **Banco de Dados**: PostgreSQL
- **ORM**: SQLAlchemy
- **Validação**: Pydantic
- **Deploy**: Google Cloud Run
- **Documentação Automática**: Swagger/OpenAPI em `/docs`

### Infraestrutura
- **Container**: Docker
- **Cloud Platform**: Google Cloud Run
- **Banco de Dados**: PostgreSQL (Cloud SQL)

---

## 📱 Telas do Aplicativo (5 Activities)

### 1️⃣ **DashboardActivity** - Tela Principal
- **Componentes**: RecyclerView, CardView, FloatingActionButton
- **Funcionalidade**: 
  - Exibe lista de projetos criados
  - Cada projeto em um CardView com custo e orçamento
  - FAB para criar novo projeto
- **Endpoint**: `GET /projetos`

### 2️⃣ **CadastroBaseActivity** - Cadastro Base
- **Componentes**: Spinner, EditText, Button, TextView
- **Funcionalidade**:
  - Spinner para selecionar modelo do carro
  - EditText para orçamento máximo
  - EditText para nome do projeto
  - Validação obrigatória de campos
- **Endpoint**: `GET /carros`

### 3️⃣ **SetupPecasActivity** - Seleção de Peças
- **Componentes**: RecyclerView, CheckBox, Switch, Button
- **Funcionalidade**:
  - Lista de peças compatíveis com o carro selecionado
  - CheckBox para selecionar/desselecionar peças
  - Switch para modo "Agressivo/Track"
  - Cálculo dinâmico de custo
- **Endpoint**: `GET /pecas/{carro_id}`

### 4️⃣ **DetalhesProjetoActivity** - Detalhes e Resumo
- **Componentes**: ProgressBar, RecyclerView, Button, TextView
- **Funcionalidade**:
  - Exibe peças selecionadas
  - ProgressBar mostrando % do orçamento gasto
  - Custo total vs orçamento
  - Botão "Salvar Projeto"
- **Endpoint**: `POST /projetos` ou `PUT /projetos/{id}`

### 5️⃣ **OficinaFinderActivity** - Localizador de Oficinas
- **Componentes**: ImageView, Button
- **Funcionalidade**:
  - ImageView com mapa estático
  - Intent Implícita (`ACTION_VIEW`) para abrir Google Maps
  - Busca por "oficinas mecânicas próximas"
- **Intent Implícita**: `geo:0,0?q=oficinas+mecânicas`

---

## 🔌 Especificação da API REST

**Base URL**: `http://10.0.2.2:8000` (emulador) ou `http://SEU_IP:8000` (dispositivo físico)

### Documentação Swagger
```
GET /docs
```

### Endpoints Implementados

#### 🚗 Carros
```
GET /carros
Descrição: Retorna lista de modelos base disponíveis
Response: [{"id": 1, "modelo": "Renault Clio K4M", "categoria": "Hatch"}, ...]
```

#### 🔧 Peças
```
GET /pecas/{carro_id}
Descrição: Retorna peças compatíveis com um carro
Response: [{"id": 3, "nome": "Rodas TE37", "preco": 3500.00, "tipo": "Estética"}, ...]

GET /pecas
Descrição: Retorna todas as peças
```

#### 📋 Projetos
```
POST /projetos
Payload: {
  "nome": "Track Day Setup",
  "orcamento_maximo": 15000.0,
  "carro_id": 1,
  "pecas_ids": [3, 7, 8]
}
Response: {"id": 1, "nome": "...", "custo_total_calculado": 7800.00}

GET /projetos
Descrição: Retorna todos os projetos

GET /projetos/{projeto_id}
Descrição: Retorna detalhes de um projeto específico

PUT /projetos/{projeto_id}
Descrição: Atualiza um projeto existente

DELETE /projetos/{projeto_id}
Descrição: Deleta um projeto
```

---

## 🗄️ Modelo de Dados (Relacional)

### Tabela: `carros`
```sql
CREATE TABLE carros (
    id SERIAL PRIMARY KEY,
    modelo VARCHAR(255) UNIQUE NOT NULL,
    categoria VARCHAR(100) NOT NULL
);
```

### Tabela: `pecas`
```sql
CREATE TABLE pecas (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(255) UNIQUE NOT NULL,
    preco DECIMAL(10, 2) NOT NULL,
    tipo VARCHAR(100) NOT NULL
);
```

### Tabela: `carro_peca_compatibilidade` (N:M)
```sql
CREATE TABLE carro_peca_compatibilidade (
    carro_id INTEGER PRIMARY KEY REFERENCES carros(id),
    peca_id INTEGER PRIMARY KEY REFERENCES pecas(id)
);
```

### Tabela: `projetos`
```sql
CREATE TABLE projetos (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    orcamento_maximo DECIMAL(10, 2) NOT NULL,
    custo_total_calculado DECIMAL(10, 2) DEFAULT 0.0,
    carro_id INTEGER REFERENCES carros(id),
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Tabela: `projeto_peca` (N:M)
```sql
CREATE TABLE projeto_peca (
    projeto_id INTEGER PRIMARY KEY REFERENCES projetos(id),
    peca_id INTEGER PRIMARY KEY REFERENCES pecas(id)
);
```

---

## 🚀 Como Executar

### Backend (FastAPI)

#### 1. Instalar dependências
```bash
cd api
pip install -r requirements.txt
```

#### 2. Configurar variáveis de ambiente
```bash
cp .env.example .env
# Editar .env com suas credenciais PostgreSQL
```

#### 3. Inicializar banco de dados
```bash
python init_db.py
```

#### 4. Executar servidor
```bash
python -m uvicorn app.main:app --reload
```

API disponível em: `http://localhost:8000`
Swagger: `http://localhost:8000/docs`

### Frontend (Android)

#### 1. Abrir no Android Studio
```bash
Arquivo → Abrir → Selecionar pasta app-mobile
```

#### 2. Configurar emulador/dispositivo
- Conectar dispositivo ou abrir emulador

#### 3. Sincronizar Gradle
- Build → Sync Now

#### 4. Executar aplicativo
- Run → Run 'app'

---

## 📦 Requisitos da AP2 - Checklist

- ✅ **Aplicativo Android compilando sem erros**
- ✅ **5 Activities/Telas com navegação via Intents explícitas**
- ✅ **Comunicação contínua com API REST (Retrofit)**
- ✅ **RecyclerView com Adapter customizado**
- ✅ **Componentes UI**: TextView, EditText, Button, ImageView, Spinner, CheckBox, Switch, RecyclerView, CardView, ProgressBar
- ✅ **API REST em FastAPI com documentação Swagger automática** (`/docs`)
- ✅ **Banco de dados PostgreSQL com estrutura relacional**
- ✅ **CRUD completo** (Create, Read, Update, Delete)
- ✅ **Intent Implícita** (ACTION_VIEW para Google Maps)
- ✅ **Lógica de validação e cálculo de orçamento**
- ✅ **Padrão MVVM no Android**
- ✅ **Deploy pronto para Google Cloud Run**

---

## 🐳 Deploy no Google Cloud Run

### Pré-requisitos
- Conta Google Cloud
- gcloud CLI instalada
- Docker instalado

### Passos

```bash
# 1. Autenticar
gcloud auth login

# 2. Configurar projeto
gcloud config set project [SEU_PROJECT_ID]

# 3. Criar repositório
gcloud artifacts repositories create gearhead-api --repository-format=docker --location=us-central1

# 4. Construir imagem
cd api
gcloud builds submit --tag us-central1-docker.pkg.dev/[PROJECT_ID]/gearhead-api/gearhead-api

# 5. Deploy
gcloud run deploy gearhead-api \
  --image us-central1-docker.pkg.dev/[PROJECT_ID]/gearhead-api/gearhead-api \
  --platform managed \
  --region us-central1 \
  --set-env-vars DATABASE_URL=postgresql://user:pass@cloudsql-ip/db
```

**URL da API após deploy**: `https://gearhead-api-[hash].a.run.app`
**Swagger**: `https://gearhead-api-[hash].a.run.app/docs`

---

## 📸 Fluxo de Uso

```
Dashboard (Listar Projetos)
    ↓ [FAB "Novo Projeto"]
Cadastro Base (Selecionar Carro + Orçamento)
    ↓ [Próximo]
Setup de Peças (Selecionar Peças com CheckBox)
    ↓ [Concluir]
Detalhes Projeto (ProgressBar + Salvar)
    ↓ [Salvar]
Dashboard (Projeto Criado)
    ↓ [Clique no Card]
Detalhes do Projeto (Ver Peças)
    ↓ [Menu]
Oficina Finder (Buscar Oficinas no Maps)
```

---

## 🛠️ Troubleshooting

### Erro de Conexão com API
**Problema**: `java.net.ConnectException`
**Solução**: Verificar se servidor FastAPI está rodando em `http://10.0.2.2:8000`

### Erro de Permissão de Internet
**Problema**: `android.permission.INTERNET`
**Solução**: Permissão já adicionada no `AndroidManifest.xml`

### Erro de Compilação Gradle
**Problema**: `Gradle build failed`
**Solução**: 
```bash
./gradlew clean
./gradlew build --refresh-dependencies
```

---

## 👨‍💻 Tecnologias Utilizadas

| Camada | Tecnologia | Versão |
|--------|------------|--------|
| **Frontend** | Android SDK | 35 |
| | Retrofit | 2.9.0 |
| | Gson | 2.10.1 |
| **Backend** | FastAPI | 0.104.1 |
| | SQLAlchemy | 2.0.23 |
| | Pydantic | 2.5.0 |
| **Banco Dados** | PostgreSQL | 13+ |
| **DevOps** | Docker | Latest |
| | Google Cloud Run | Managed |

---

## 📝 Licença

Projeto desenvolvido como Avaliação Prática 2 (AP2) - Desenvolvimento Mobile.

---

## 👥 Autores

- Desenvolvido com foco em requisitos da disciplina de Desenvolvimento Mobile

---

## 🔗 Links Importantes

- **Documentação Swagger (após deploy)**: `/docs`
- **Google Cloud**: https://console.cloud.google.com
- **Android Studio**: https://developer.android.com/studio
- **FastAPI Docs**: https://fastapi.tiangolo.com

---

**Última atualização**: 2026-06-08
**Status**: ✅ Pronto para uso
