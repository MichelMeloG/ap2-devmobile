from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from app.database.database import get_db
from app.models.models import Carro, Peca, Projeto
from app.schemas import PecaCreate, Peca as PecaSchema

router = APIRouter(prefix="/pecas", tags=["pecas"])

@router.get("/{carro_id}", response_model=list[PecaSchema])
def listar_pecas_por_carro(carro_id: int, db: Session = Depends(get_db)):
    """
    Retorna a lista de peças compatíveis com um carro específico.
    """
    carro = db.query(Carro).filter(Carro.id == carro_id).first()
    if not carro:
        raise HTTPException(status_code=404, detail="Carro não encontrado")
    
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
    db_peca = Peca(nome=peca.nome, preco=peca.preco, tipo=peca.tipo)
    db.add(db_peca)
    db.commit()
    db.refresh(db_peca)
    return db_peca
