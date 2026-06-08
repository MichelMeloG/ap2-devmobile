"""
Script para popular o banco de dados com carros e peças iniciais.
Execute: python init_db.py
"""

import os
import sys
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker

# Adiciona o path do app
sys.path.append(os.path.dirname(__file__))

from app.models.models import Base, Carro, Peca, carro_peca_compatibilidade
from app.database.database import DATABASE_URL

def init_database():
    """Inicializa o banco de dados com dados de exemplo."""
    
    # Cria as tabelas
    engine = create_engine(DATABASE_URL)
    Base.metadata.create_all(bind=engine)
    
    Session = sessionmaker(bind=engine)
    session = Session()
    
    # Verifica se já existem dados
    if session.query(Carro).count() > 0:
        print("Banco de dados já contém dados. Abortando inicialização.")
        session.close()
        return
    
    # Cria carros
    carros_data = [
        {"modelo": "Renault Clio K4M", "categoria": "Hatch"},
        {"modelo": "Honda Fit", "categoria": "Hatch"},
        {"modelo": "Volkswagen Gol", "categoria": "Hatch"},
        {"modelo": "Fiat 147", "categoria": "Hatch"},
    ]
    
    carros = [Carro(**data) for data in carros_data]
    session.add_all(carros)
    session.commit()
    
    # Cria peças
    pecas_data = [
        {"nome": "Swap Motor F4R 2.0", "preco": 8000.00, "tipo": "Mecânica"},
        {"nome": "Rodas TE37 Aro 15", "preco": 3500.00, "tipo": "Estética"},
        {"nome": "Remap Stage 2", "preco": 1500.00, "tipo": "Mecânica"},
        {"nome": "Suspensão Coilover", "preco": 2500.00, "tipo": "Mecânica"},
        {"nome": "Turbo KKK", "preco": 4500.00, "tipo": "Mecânica"},
        {"nome": "Intercooler", "preco": 1200.00, "tipo": "Mecânica"},
        {"nome": "Escape Inox", "preco": 800.00, "tipo": "Estética"},
        {"nome": "Pré-catalisador removido", "preco": 500.00, "tipo": "Mecânica"},
        {"nome": "Kit Buchas Poliuretano", "preco": 600.00, "tipo": "Mecânica"},
        {"nome": "Spoiler Carbono", "preco": 1800.00, "tipo": "Estética"},
    ]
    
    pecas = [Peca(**data) for data in pecas_data]
    session.add_all(pecas)
    session.commit()
    
    # Associa peças com carros (compatibilidade)
    clio = session.query(Carro).filter(Carro.modelo == "Renault Clio K4M").first()
    fit = session.query(Carro).filter(Carro.modelo == "Honda Fit").first()
    gol = session.query(Carro).filter(Carro.modelo == "Volkswagen Gol").first()
    
    # Clio compatível com todas as peças
    clio.pecas = pecas
    
    # Fit compatível com rodas, escape e spoiler
    fit.pecas = [p for p in pecas if p.tipo == "Estética"]
    
    # Gol compatível com suspensão, rodas e estética
    gol.pecas = [p for p in pecas if p.tipo in ["Estética", "Mecânica"] and "Swap" not in p.nome and "Turbo" not in p.nome]
    
    session.commit()
    
    print("✅ Banco de dados inicializado com sucesso!")
    print(f"   - {session.query(Carro).count()} carros adicionados")
    print(f"   - {session.query(Peca).count()} peças adicionadas")
    
    session.close()

if __name__ == "__main__":
    init_database()
