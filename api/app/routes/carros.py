from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from app.database.database import get_db
from app.models.models import Carro, Peca, Projeto
from app.schemas import CarroCreate, Carro as CarroSchema, Peca as PecaSchema

router = APIRouter(prefix="/carros", tags=["carros"])

@router.get("", response_model=list[CarroSchema])
def listar_carros(db: Session = Depends(get_db)):
    """
    Retorna a lista de modelos base disponíveis.
    """
    carros = db.query(Carro).all()
    return carros

@router.post("", response_model=CarroSchema)
def criar_carro(carro: CarroCreate, db: Session = Depends(get_db)):
    """
    Cria um novo modelo de carro ou retorna se já existir.
    """
    existente = db.query(Carro).filter(Carro.modelo == carro.modelo).first()
    if existente:
        return existente
        
    db_carro = Carro(modelo=carro.modelo, categoria=carro.categoria)
    db.add(db_carro)
    db.commit()
    db.refresh(db_carro)
    return db_carro
