from pydantic import BaseModel
from typing import List, Optional
from datetime import datetime

# === Schemas para Carros ===
class CarroBase(BaseModel):
    modelo: str
    categoria: str

class CarroCreate(CarroBase):
    pass

class Carro(CarroBase):
    id: int
    
    class Config:
        from_attributes = True

# === Schemas para Peças ===\r
class PecaBase(BaseModel):\r
    nome: str\r
    preco: float\r
    tipo: str\r
    ganho_hp: int = 0\r
    descricao: str = ""\r
\r
class PecaCreate(PecaBase):\r
    pass\r
\r
class Peca(PecaBase):\r
    id: int\r
    \r
    class Config:\r
        from_attributes = True

# === Schemas para Projetos ===
class ProjetoBase(BaseModel):
    nome: str
    orcamento_maximo: float
    carro_id: int
    pecas_ids: List[int] = []

class ProjetoCreate(ProjetoBase):
    pass

class ProjetoUpdate(BaseModel):
    nome: Optional[str] = None
    orcamento_maximo: Optional[float] = None
    pecas_ids: Optional[List[int]] = None

class Projeto(ProjetoBase):
    id: int
    custo_total_calculado: float
    criado_em: datetime
    
    class Config:
        from_attributes = True
