from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.database.database import engine, Base
from app.models.models import Carro, Peca, Projeto
from app.routes import carros, pecas, projetos, external
from sqlalchemy import text

# Cria as tabelas no banco de dados
Base.metadata.create_all(bind=engine)

# Inicializa a aplicação FastAPI
app = FastAPI(
    title="Gearhead Builder API",
    description="API para planejamento e orçamentação de modificações automotivas",
    version="2.0.0",
    docs_url="/docs",
    redoc_url="/redoc"
)

# CORS - Permite requisições do aplicativo Android
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Migração: adiciona novas colunas na primeira requisição
@app.on_event("startup")
def run_migrations():
    try:
        with engine.connect() as conn:
            conn.execute(text("ALTER TABLE pecas ADD COLUMN IF NOT EXISTS ganho_hp INTEGER DEFAULT 0"))
            conn.execute(text("ALTER TABLE pecas ADD COLUMN IF NOT EXISTS descricao VARCHAR DEFAULT ''"))
            conn.commit()
            print("✅ Migração de banco de dados concluída.")
    except Exception as e:
        print(f"⚠️ Migração ignorada: {e}")

# Incluir routers
app.include_router(carros.router)
app.include_router(pecas.router)
app.include_router(projetos.router)
app.include_router(external.router)

@app.get("/")
def root():
    """
    Endpoint raiz para verificar se a API está rodando.
    """
    return {
        "message": "Bem-vindo ao Gearhead Builder API",
        "docs": "/docs",
        "endpoints": {
            "carros": "/carros",
            "pecas": "/pecas",
            "projetos": "/projetos"
        }
    }

@app.get("/health")
def health_check():
    """
    Endpoint de health check para verificar se a API está saudável.
    """
    return {"status": "OK"}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
