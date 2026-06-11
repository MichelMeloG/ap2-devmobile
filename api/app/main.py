from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.database.database import engine, Base
from app.models.models import Carro, Peca, Projeto
from app.routes import carros, pecas, projetos

# Cria as tabelas no banco de dados
Base.metadata.create_all(bind=engine)

# Inicializa a aplicação FastAPI
app = FastAPI(
    title="Gearhead Builder API",
    description="API para planejamento e orçamentação de modificações automotivas",
    version="1.0.0",
    docs_url="/docs",
    redoc_url="/redoc"
)

# CORS - Permite requisições do aplicativo Android
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # Em produção, configure com domínios específicos
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Incluir routers
app.include_router(carros.router)
app.include_router(pecas.router)
app.include_router(projetos.router)

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
