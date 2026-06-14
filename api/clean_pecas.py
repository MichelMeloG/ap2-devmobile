import os
import sys
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
from app.models.models import Peca, Carro, Projeto

DATABASE_URL = "postgresql+psycopg2://postgres:Gearhead2026Password!@localhost/appmobile"
engine = create_engine(DATABASE_URL)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
db = SessionLocal()

print("Buscando e apagando pecas...")
# Since we might have foreign key constraints in projetos_pecas or carro_peca_compatibilidade, we need to be careful
db.execute("TRUNCATE TABLE pecas CASCADE")
db.commit()
print("Pecas apagadas com sucesso!")
