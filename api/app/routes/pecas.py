from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from app.database.database import get_db
from app.models.models import Carro, Peca, Projeto
from app.schemas import PecaCreate, Peca as PecaSchema
from app.services.ai_service import gerar_pecas_com_ia, gerar_resumo_projeto_com_ia

router = APIRouter(prefix="/pecas", tags=["pecas"])

@router.get("/{carro_id}", response_model=list[PecaSchema])
def listar_pecas_por_carro(carro_id: int, db: Session = Depends(get_db)):
    """
    Retorna a lista de peças compatíveis com um carro específico.
    Se não houver peças, usa IA para gerar peças dinamicamente.
    """
    carro = db.query(Carro).filter(Carro.id == carro_id).first()
    if not carro:
        raise HTTPException(status_code=404, detail="Carro não encontrado")
    
    if not carro.pecas:
        # Carro sem peças: usa IA para gerar novas e salvar no banco
        pecas_geradas = gerar_pecas_com_ia(carro.modelo)
        
        novas_pecas_db = []
        for p in pecas_geradas:
            db_peca = db.query(Peca).filter(Peca.nome == p["nome"]).first()
            if not db_peca:
                db_peca = Peca(
                    nome=p["nome"],
                    preco=p["preco"],
                    tipo=p["tipo"],
                    ganho_hp=p.get("ganho_hp", 0),
                    descricao=p.get("descricao", "")
                )
                db.add(db_peca)
            novas_pecas_db.append(db_peca)
            
        for p_db in novas_pecas_db:
            if p_db not in carro.pecas:
                carro.pecas.append(p_db)
                
        db.commit()
        
    return carro.pecas

@router.get("", response_model=list[PecaSchema])
def listar_todas_pecas(db: Session = Depends(get_db)):
    """
    Retorna todas as peças do catálogo.
    """
    pecas = db.query(Peca).all()
    return pecas

@router.post("", response_model=PecaSchema)
def criar_peca(peca: PecaCreate, db: Session = Depends(get_db)):
    """
    Cria uma nova peça (uso interno).
    """
    db_peca = Peca(
        nome=peca.nome,
        preco=peca.preco,
        tipo=peca.tipo,
        ganho_hp=peca.ganho_hp,
        descricao=peca.descricao
    )
    db.add(db_peca)
    db.commit()
    db.refresh(db_peca)
    return db_peca

@router.post("/resumo-ia")
def gerar_resumo_ia(dados: dict):
    """
    Gera um resumo inteligente do projeto usando IA.
    Recebe: {"modelo_carro": "...", "pecas": [{"nome": "...", "preco": ..., "ganho_hp": ...}]}
    """
    modelo = dados.get("modelo_carro", "Carro desconhecido")
    pecas = dados.get("pecas", [])
    resumo = gerar_resumo_projeto_com_ia(modelo, pecas)
    return {"resumo": resumo}
