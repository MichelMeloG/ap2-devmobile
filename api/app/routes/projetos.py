from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from app.database.database import get_db
from app.models.models import Carro, Peca, Projeto
from app.schemas import ProjetoCreate, ProjetoUpdate, Projeto as ProjetoSchema

router = APIRouter(prefix="/projetos", tags=["projetos"])

@router.post("", response_model=ProjetoSchema)
def criar_projeto(projeto: ProjetoCreate, db: Session = Depends(get_db)):
    """
    Cria um novo projeto. Calcula o custo total das peças e verifica se cabe no orçamento.
    """
    # Verifica se o carro existe
    carro = db.query(Carro).filter(Carro.id == projeto.carro_id).first()
    if not carro:
        raise HTTPException(status_code=404, detail="Carro não encontrado")
    
    # Busca as peças selecionadas
    pecas = db.query(Peca).filter(Peca.id.in_(projeto.pecas_ids)).all()
    
    # Calcula o custo total
    custo_total = sum(peca.preco for peca in pecas)
    
    # Verifica se cabe no orçamento
    if custo_total > projeto.orcamento_maximo:
        raise HTTPException(
            status_code=400,
            detail=f"Orçamento insuficiente. Custo: R$ {custo_total:.2f}, Orçamento: R$ {projeto.orcamento_maximo:.2f}"
        )
    
    # Cria o projeto no banco
    db_projeto = Projeto(
        nome=projeto.nome,
        orcamento_maximo=projeto.orcamento_maximo,
        custo_total_calculado=custo_total,
        carro_id=projeto.carro_id
    )
    db_projeto.pecas = pecas
    
    db.add(db_projeto)
    db.commit()
    db.refresh(db_projeto)
    return db_projeto

@router.get("", response_model=list[ProjetoSchema])
def listar_projetos(db: Session = Depends(get_db)):
    """
    Retorna a lista de todos os projetos salvos.
    """
    projetos = db.query(Projeto).all()
    return projetos

@router.get("/{projeto_id}", response_model=ProjetoSchema)
def obter_projeto(projeto_id: int, db: Session = Depends(get_db)):
    """
    Retorna os detalhes de um projeto específico.
    """
    projeto = db.query(Projeto).filter(Projeto.id == projeto_id).first()
    if not projeto:
        raise HTTPException(status_code=404, detail="Projeto não encontrado")
    return projeto

@router.put("/{projeto_id}", response_model=ProjetoSchema)
def atualizar_projeto(projeto_id: int, projeto_update: ProjetoUpdate, db: Session = Depends(get_db)):
    """
    Atualiza um projeto existente.
    """
    projeto = db.query(Projeto).filter(Projeto.id == projeto_id).first()
    if not projeto:
        raise HTTPException(status_code=404, detail="Projeto não encontrado")
    
    # Atualiza campos simples
    if projeto_update.nome:
        projeto.nome = projeto_update.nome
    if projeto_update.orcamento_maximo:
        projeto.orcamento_maximo = projeto_update.orcamento_maximo
    
    # Atualiza peças se fornecidas
    if projeto_update.pecas_ids is not None:
        pecas = db.query(Peca).filter(Peca.id.in_(projeto_update.pecas_ids)).all()
        custo_total = sum(peca.preco for peca in pecas)
        
        if custo_total > projeto.orcamento_maximo:
            raise HTTPException(
                status_code=400,
                detail=f"Orçamento insuficiente. Custo: R$ {custo_total:.2f}, Orçamento: R$ {projeto.orcamento_maximo:.2f}"
            )
        
        projeto.pecas = pecas
        projeto.custo_total_calculado = custo_total
    
    db.commit()
    db.refresh(projeto)
    return projeto

@router.delete("/{projeto_id}")
def deletar_projeto(projeto_id: int, db: Session = Depends(get_db)):
    """
    Deleta um projeto do banco de dados.
    """
    projeto = db.query(Projeto).filter(Projeto.id == projeto_id).first()
    if not projeto:
        raise HTTPException(status_code=404, detail="Projeto não encontrado")
    
    db.delete(projeto)
    db.commit()
    return {"message": f"Projeto {projeto_id} deletado com sucesso"}
