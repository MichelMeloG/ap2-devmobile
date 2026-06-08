# 🚀 Guia de Início Rápido - Gearhead Builder

## ⚡ Quick Start (5 minutos)

### Backend API

```bash
# 1. Entrar na pasta api
cd api

# 2. Instalar dependências Python
pip install -r requirements.txt

# 3. Configurar variáveis de ambiente
# Criar arquivo .env (copiar do .env.example)
DATABASE_URL=postgresql://user:password@localhost:5432/gearhead_db

# 4. Executar servidor
python -m uvicorn app.main:app --reload

# ✅ API rodando em: http://localhost:8000
# 📖 Swagger docs: http://localhost:8000/docs
```

### Frontend Android

```bash
# 1. Abrir Android Studio
# Arquivo → Open → Selecionar a pasta app-mobile

# 2. Sincronizar Gradle
# Build → Sync Now (ou Ctrl+Shift+S)

# 3. Abrir emulador ou conectar dispositivo
# Device Manager → Create/Start emulator

# 4. Executar aplicativo
# Run → Run 'app' (Shift+F10) ou clique no Play Button
```

---

## 📋 Pré-requisitos

### Para Backend
- ✅ Python 3.11+
- ✅ PostgreSQL 13+
- ✅ pip

### Para Frontend
- ✅ Android Studio (versão 2024+)
- ✅ Android SDK API 35
- ✅ Emulador ou dispositivo físico (Android 8+)

---

## 🔧 Configuração Detalhada

### 1️⃣ Backend - Configuração PostgreSQL

#### Opção A: PostgreSQL Local
```bash
# Windows (usando PostgreSQL instalado)
# Conectar ao psql
psql -U postgres

# Criar banco de dados
CREATE DATABASE gearhead_db;
CREATE USER gearhead_user WITH PASSWORD 'sua_senha';
GRANT ALL PRIVILEGES ON DATABASE gearhead_db TO gearhead_user;

# Atualizar .env
DATABASE_URL=postgresql://gearhead_user:sua_senha@localhost:5432/gearhead_db
```

#### Opção B: PostgreSQL via Docker
```bash
docker run --name gearhead-postgres \
  -e POSTGRES_USER=gearhead_user \
  -e POSTGRES_PASSWORD=sua_senha \
  -e POSTGRES_DB=gearhead_db \
  -p 5432:5432 \
  -d postgres:15
```

### 2️⃣ Backend - Inicializar Dados

```bash
cd api

# Executar script de inicialização
python init_db.py

# Resultado esperado:
# ✅ Banco de dados inicializado com sucesso!
#    - 4 carros adicionados
#    - 10 peças adicionadas
```

### 3️⃣ Frontend - Configurar Conexão com API

#### Para Emulador
```java
// Já configurado em RetrofitClient.java
private static final String BASE_URL = "http://10.0.2.2:8000";
```

#### Para Dispositivo Físico
1. Descobrir IP local da máquina:
```bash
# Windows
ipconfig
# Procurar por "Endereço IPv4" (ex: 192.168.1.100)

# Mac/Linux
ifconfig
```

2. Editar [RetrofitClient.java](app-mobile/app/src/main/java/com/example/appmobile/api/RetrofitClient.java):
```java
private static final String BASE_URL = "http://192.168.1.100:8000";
```

3. Conectar dispositivo à mesma rede WiFi

---

## ✨ Fluxo de Teste Completo

### Passo 1: Verificar API
Abrir no navegador: `http://localhost:8000/docs`
- Visualizar todos os endpoints
- Clicar em "Try it out" para testar

### Passo 2: Executar Aplicativo Android
- Selecionar emulador/dispositivo
- Clicar em Run

### Passo 3: Navegar pelas Telas
1. **Dashboard** → Clique no botão amarelo (+)
2. **Cadastro Base** → Selecione carro, orçamento, nome
3. **Setup de Peças** → Selecione peças com CheckBox
4. **Detalhes Projeto** → Veja ProgressBar, clique Salvar
5. **Volta ao Dashboard** → Projeto criado!

---

## 🐛 Problemas Comuns e Soluções

### ❌ Erro: "Falha ao conectar com a API"

**Causa**: API não está rodando
```bash
# Solução:
cd api
python -m uvicorn app.main:app --reload
```

### ❌ Erro: "Falha na conexão com banco de dados"

**Causa**: PostgreSQL não está rodando
```bash
# Solução:
# Windows - iniciar PostgreSQL
# Mac - brew services start postgresql
# Docker - docker start gearhead-postgres
```

### ❌ Erro: "Cannot resolve symbol 'Retrofit'"

**Causa**: Gradle não sincronizou as dependências
```bash
# Solução:
# Build → Clean Project
# Build → Sync Now
# File → Invalidate Caches → Restart
```

### ❌ Erro: "Permissão recusada (INTERNET)"

**Causa**: Falta de permissão no AndroidManifest.xml
```bash
# Solução:
# Já adicionada, mas verificar:
# app/src/main/AndroidManifest.xml
# <uses-permission android:name="android.permission.INTERNET" />
```

---

## 📊 Estrutura de Pastas (Resumida)

```
├── api/
│   ├── app/
│   │   ├── main.py           ← Inicia aqui
│   │   ├── models/models.py  ← Tabelas do banco
│   │   ├── routes/           ← Endpoints
│   │   └── database/         ← Conexão DB
│   ├── requirements.txt       ← Dependências
│   ├── init_db.py            ← Popular dados
│   └── Dockerfile            ← Deploy
│
└── app-mobile/
    └── app/
        ├── src/main/
        │   ├── java/
        │   │   └── com/example/appmobile/
        │   │       ├── ui/               ← 5 Activities
        │   │       ├── api/              ← Retrofit
        │   │       ├── models/           ← Classes Java
        │   │       └── adapters/         ← RecyclerView
        │   └── res/layout/               ← 5 Telas XML
        └── build.gradle.kts              ← Dependências
```

---

## 🎯 Checklist para Submissão AP2

- [ ] Backend funcionando em http://localhost:8000
- [ ] Swagger acessível em http://localhost:8000/docs
- [ ] 5 Activities compilando sem erro
- [ ] RecyclerView exibindo projetos
- [ ] Spinner mostrando carros
- [ ] CheckBox para peças funcionando
- [ ] ProgressBar calculando orçamento
- [ ] Switch Modo Agressivo presente
- [ ] Intent Implícita abrindo Google Maps
- [ ] Integração Retrofit com sucesso
- [ ] Banco de dados populado
- [ ] README.md descrevendo tudo
- [ ] .gitignore criado

---

## 📚 Documentação Completa

Consultar [README.md](README.md) para:
- Arquitetura completa
- Especificação de endpoints
- Modelo de dados relacional
- Deploy no Google Cloud Run
- Troubleshooting avançado

---

## 💡 Próximos Passos (Após Funcionar)

1. **Deploy no Cloud Run**
   ```bash
   cd api
   gcloud builds submit --tag us-central1-docker.pkg.dev/[PROJECT]/gearhead-api/gearhead-api
   ```

2. **Apontar Android para API Cloud**
   ```java
   private static final String BASE_URL = "https://gearhead-api-xyz.a.run.app";
   ```

3. **Implementar Autenticação** (Firebase)
4. **Adicionar Fotos de Carros** (ImageView com Glide)
5. **Notificações Push** (FCM)

---

## 🎉 Dúvidas?

1. Verificar logs no logcat (Android Studio)
2. Verificar console do FastAPI
3. Consultar status no Swagger (`/docs`)
4. Testar endpoints manualmente com curl:

```bash
# Listar carros
curl http://localhost:8000/carros

# Listar projetos
curl http://localhost:8000/projetos

# Criar projeto
curl -X POST http://localhost:8000/projetos \
  -H "Content-Type: application/json" \
  -d '{"nome":"Track","orcamento_maximo":15000,"carro_id":1,"pecas_ids":[1,2,3]}'
```

---

**Boa sorte! 🏁**
