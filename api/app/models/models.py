from sqlalchemy import Column, Integer, String, Float, ForeignKey, Table, DateTime
from sqlalchemy.orm import relationship
from datetime import datetime
from app.database.database import Base

# Tabela de associação N:M (Carro-Peça)
carro_peca_compatibilidade = Table(
    'carro_peca_compatibilidade',
    Base.metadata,
    Column('carro_id', Integer, ForeignKey('carros.id'), primary_key=True),
    Column('peca_id', Integer, ForeignKey('pecas.id'), primary_key=True)
)

class Carro(Base):
    __tablename__ = "carros"
    
    id = Column(Integer, primary_key=True, index=True)
    modelo = Column(String, unique=True, index=True)
    categoria = Column(String)
    
    # Relacionamentos
    pecas = relationship("Peca", secondary=carro_peca_compatibilidade, back_populates="carros")
    projetos = relationship("Projeto", back_populates="carro")

class Peca(Base):
    __tablename__ = "pecas"
    
    id = Column(Integer, primary_key=True, index=True)
    nome = Column(String, unique=True, index=True)
    preco = Column(Float)
    tipo = Column(String)
    ganho_hp = Column(Integer, default=0)
    descricao = Column(String, default="")
    
    # Relacionamentos
    carros = relationship("Carro", secondary=carro_peca_compatibilidade, back_populates="pecas")
    projetos = relationship("Projeto", secondary="projeto_peca", back_populates="pecas")

# Tabela de associação para Projeto-Peça
projeto_peca = Table(
    'projeto_peca',
    Base.metadata,
    Column('projeto_id', Integer, ForeignKey('projetos.id'), primary_key=True),
    Column('peca_id', Integer, ForeignKey('pecas.id'), primary_key=True)
)

class Projeto(Base):
    __tablename__ = "projetos"
    
    id = Column(Integer, primary_key=True, index=True)
    nome = Column(String, index=True)
    orcamento_maximo = Column(Float)
    custo_total_calculado = Column(Float, default=0.0)
    carro_id = Column(Integer, ForeignKey('carros.id'))
    criado_em = Column(DateTime, default=datetime.utcnow)
    
    # Relacionamentos
    carro = relationship("Carro", back_populates="projetos")
    pecas = relationship("Peca", secondary=projeto_peca, back_populates="projetos")
