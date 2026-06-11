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


# ========== NHTSA — Decodificação de VIN ==========

@router.get("/vin/{vin}")
async def consultar_vin(vin: str):
    """
    Decodifica um VIN (chassi) usando a API gratuita do NHTSA (governo americano).
    Retorna marca, modelo, ano, motor, combustível e tipo de carroceria.
    """
    if len(vin) != 17:
        raise HTTPException(status_code=400, detail="O VIN deve ter exatamente 17 caracteres")
    
    async with httpx.AsyncClient() as client:
        response = await client.get(
            f"{NHTSA_BASE}/decodevinvalues/{vin}",
            params={"format": "json"},
            timeout=15.0
        )
    
    if response.status_code != 200:
        raise HTTPException(status_code=response.status_code, detail="Erro ao consultar NHTSA")
    
    data = response.json()
    
    if not data.get("Results") or len(data["Results"]) == 0:
        raise HTTPException(status_code=404, detail="VIN não encontrado")
    
    result = data["Results"][0]
    
    # Filtra apenas os campos relevantes
    return {
        "vin": vin.upper(),
        "marca": result.get("Make", ""),
        "modelo": result.get("Model", ""),
        "ano": result.get("ModelYear", ""),
        "motor": result.get("EngineModel", "") or result.get("DisplacementL", "") + "L" if result.get("DisplacementL") else "",
        "cilindros": result.get("EngineCylinders", ""),
        "potencia_hp": result.get("EngineHP", ""),
        "combustivel": result.get("FuelTypePrimary", ""),
        "carroceria": result.get("BodyClass", ""),
        "tracao": result.get("DriveType", ""),
        "transmissao": result.get("TransmissionStyle", ""),
        "portas": result.get("Doors", ""),
        "fabricante": result.get("Manufacturer", ""),
        "pais_origem": result.get("PlantCountry", ""),
    }
