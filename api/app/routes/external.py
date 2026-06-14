import os
import httpx
from fastapi import APIRouter, HTTPException

router = APIRouter(prefix="/api/external", tags=["external"])

API_NINJAS_KEY = os.getenv("API_NINJAS_KEY", "")
API_NINJAS_BASE = "https://api.api-ninjas.com/v1"
NHTSA_BASE = "https://vpic.nhtsa.dot.gov/api/vehicles"


# ========== API Ninjas — Marcas, Modelos, Trims ==========

@router.get("/marcas")
async def listar_marcas():
    """
    Retorna lista de marcas de veículos via API Ninjas.
    """
    if not API_NINJAS_KEY:
        raise HTTPException(status_code=500, detail="API_NINJAS_KEY não configurada")
    
    async with httpx.AsyncClient() as client:
        response = await client.get(
            f"{API_NINJAS_BASE}/carmakes",
            headers={"X-Api-Key": API_NINJAS_KEY},
            timeout=10.0
        )
    
    if response.status_code != 200:
        raise HTTPException(status_code=response.status_code, detail="Erro ao consultar marcas")
    
    return response.json()


@router.get("/modelos")
async def listar_modelos(marca: str):
    """
    Retorna lista de modelos de uma marca via API Ninjas.
    """
    if not API_NINJAS_KEY:
        raise HTTPException(status_code=500, detail="API_NINJAS_KEY não configurada")
    
    async with httpx.AsyncClient() as client:
        response = await client.get(
            f"{API_NINJAS_BASE}/carmodels",
            params={"make": marca},
            headers={"X-Api-Key": API_NINJAS_KEY},
            timeout=10.0
        )
    
    if response.status_code != 200:
        raise HTTPException(status_code=response.status_code, detail="Erro ao consultar modelos")
    
    return response.json()


@router.get("/trims")
async def listar_trims(marca: str, modelo: str):
    """
    Retorna trims/specs de um veículo via API Ninjas.
    """
    if not API_NINJAS_KEY:
        raise HTTPException(status_code=500, detail="API_NINJAS_KEY não configurada")
    
    async with httpx.AsyncClient() as client:
        response = await client.get(
            f"{API_NINJAS_BASE}/cartrims",
            params={"make": marca, "model": modelo, "limit": 10},
            headers={"X-Api-Key": API_NINJAS_KEY},
            timeout=10.0
        )
    
    if response.status_code != 200:
        raise HTTPException(status_code=response.status_code, detail="Erro ao consultar trims")
    
    return response.json()


# ========== API Ninjas — Decodificação de VIN ==========

@router.get("/vin/{vin}")
async def consultar_vin(vin: str):
    """
    Decodifica um VIN (chassi) usando a API Ninjas.
    Retorna marca, modelo, ano, motor, combustível e tipo de carroceria.
    """
    if len(vin) != 17:
        raise HTTPException(status_code=400, detail="O VIN deve ter exatamente 17 caracteres")
    
    if not API_NINJAS_KEY:
        raise HTTPException(status_code=500, detail="API_NINJAS_KEY não configurada")
    
    async with httpx.AsyncClient() as client:
        response = await client.get(
            f"{API_NINJAS_BASE}/vinlookup",
            params={"vin": vin},
            headers={"X-Api-Key": API_NINJAS_KEY},
            timeout=15.0
        )
    
    if response.status_code != 200:
        raise HTTPException(status_code=response.status_code, detail="Erro ao consultar API Ninjas")
    
    data = response.json()
    
    if isinstance(data, list):
        if len(data) == 0:
            raise HTTPException(status_code=404, detail="VIN não encontrado")
        data = data[0]
    
    if not data or "vin" not in data:
        raise HTTPException(status_code=404, detail="VIN não encontrado")
    
    # Filtra apenas os campos relevantes
    return {
        "vin": data.get("vin", vin).upper(),
        "marca": data.get("make", data.get("manufacturer", "")),
        "modelo": data.get("model", ""),
        "ano": str(data.get("year", "")),
        "motor": str(data.get("engine", "")) + "L" if data.get("engine") else "",
        "cilindros": str(data.get("cylinders", "")),
        "potencia_hp": "",
        "combustivel": data.get("fuel_type", ""),
        "carroceria": data.get("body_class", ""),
        "tracao": data.get("drive_type", ""),
        "transmissao": "",
        "portas": str(data.get("doors", "")),
        "fabricante": data.get("manufacturer", ""),
        "pais_origem": data.get("country", ""),
    }
