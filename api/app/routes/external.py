import os
import httpx
from fastapi import APIRouter, HTTPException

router = APIRouter(prefix="/api/external", tags=["external"])

NHTSA_BASE = "https://vpic.nhtsa.dot.gov/api/vehicles"


# ========== Mock Data API — Marcas, Modelos, Trims ==========

@router.get("/marcas")
async def listar_marcas():
    """
    Retorna lista de marcas de veículos (mock).
    """
    marcas = ["Acura", "Audi", "BMW", "Chevrolet", "Dodge", "Ferrari", "Fiat", "Ford", "Honda", "Hyundai", "Jeep", "Kia", "Lamborghini", "Mazda", "Mercedes-Benz", "Nissan", "Peugeot", "Porsche", "Renault", "Subaru", "Toyota", "Volkswagen", "Volvo"]
    return [{"make": m} for m in marcas]

@router.get("/modelos")
async def listar_modelos(marca: str):
    """
    Retorna lista de modelos de uma marca (mock).
    """
    if marca.lower() == "honda":
        modelos = ["Civic", "Accord", "Fit", "HR-V", "CR-V", "S2000"]
    elif marca.lower() == "volkswagen":
        modelos = ["Golf", "Jetta", "Polo", "Passat", "Tiguan", "Up!"]
    elif marca.lower() == "ford":
        modelos = ["Mustang", "Focus", "Fiesta", "Fusion", "Ranger"]
    else:
        modelos = ["Modelo A", "Modelo B", "Modelo C"]
    return [{"model": m} for m in modelos]

@router.get("/trims")
async def listar_trims(marca: str, modelo: str):
    """
    Retorna trims/specs de um veículo (mock).
    """
    return [{
        "make": marca,
        "model": modelo,
        "trim": f"{modelo} Base",
        "year": "2024",
        "body_type": "Sedan/Hatch",
        "engine_type": "4-Cylinder",
        "drive_type": "FWD"
    }]


# ========== Decodificação de VIN (NHTSA + Offline Fallback) ==========

def decodificar_ano_vin(digito: str) -> str:
    mapa_anos = {
        'A': '2010', 'B': '2011', 'C': '2012', 'D': '2013', 'E': '2014',
        'F': '2015', 'G': '2016', 'H': '2017', 'J': '2018', 'K': '2019',
        'L': '2020', 'M': '2021', 'N': '2022', 'P': '2023', 'R': '2024',
        'S': '2025', 'T': '2026', 'V': '1997', 'W': '1998', 'X': '1999',
        'Y': '2000', '1': '2001', '2': '2002', '3': '2003', '4': '2004',
        '5': '2005', '6': '2006', '7': '2007', '8': '2008', '9': '2009',
    }
    return mapa_anos.get(digito.upper(), "Ano Desconhecido")

def decodificar_wmi_vin(wmi: str) -> str:
    wmi = wmi.upper()
    mapa_wmi = {
        '93H': 'Honda', '9BG': 'Chevrolet', '9BW': 'Volkswagen', '9BD': 'Fiat',
        '8AP': 'Fiat', '935': 'Citroën/Peugeot', '936': 'Peugeot/Citroën',
        '93U': 'Audi', '9BF': 'Ford', '9BM': 'Mercedes-Benz', '9BR': 'Toyota',
        '93R': 'Toyota', '8AW': 'Volkswagen', '8AF': 'Ford', '3VW': 'Volkswagen',
        '9C2': 'Honda Motos', '9CD': 'Yamaha Motos'
    }
    return mapa_wmi.get(wmi, "Marca Desconhecida")

@router.get("/vin/{vin}")
async def consultar_vin(vin: str):
    """
    Decodifica um VIN (chassi) tentando usar a API do NHTSA primeiro.
    Se não encontrar (como no caso de carros brasileiros), usa um fallback offline.
    """
    vin = vin.upper()
    if len(vin) != 17:
        raise HTTPException(status_code=400, detail="O VIN deve ter exatamente 17 caracteres")
    
    # Tentativa 1: NHTSA (Governo Americano - Gratuito)
    try:
        async with httpx.AsyncClient() as client:
            response = await client.get(
                f"{NHTSA_BASE}/decodevinvalues/{vin}",
                params={"format": "json"},
                timeout=10.0
            )
            if response.status_code == 200:
                data = response.json()
                if data.get("Results") and len(data["Results"]) > 0:
                    result = data["Results"][0]
                    # Se o NHTSA tiver o Make, usamos ele
                    if result.get("Make"):
                        return {
                            "vin": vin,
                            "marca": result.get("Make", "").capitalize(),
                            "modelo": result.get("Model", "Desconhecido").capitalize(),
                            "ano": result.get("ModelYear", decodificar_ano_vin(vin[9])),
                            "motor": result.get("EngineModel", "") or (result.get("DisplacementL", "") + "L" if result.get("DisplacementL") else "1.0/1.6/2.0"),
                            "cilindros": result.get("EngineCylinders", "4"),
                            "potencia_hp": result.get("EngineHP", ""),
                            "combustivel": result.get("FuelTypePrimary", "Flex/Gasolina"),
                            "carroceria": result.get("BodyClass", "Hatch/Sedan"),
                            "tracao": result.get("DriveType", "FWD"),
                            "transmissao": result.get("TransmissionStyle", "Manual/Auto"),
                            "portas": result.get("Doors", "4"),
                            "fabricante": result.get("Manufacturer", ""),
                            "pais_origem": result.get("PlantCountry", ""),
                        }
    except Exception:
        pass # Falhou NHTSA, prossegue para o fallback local
    
    # Tentativa 2: Usar a IA (Gemini) para decodificar o VIN
    from app.services.ai_service import decodificar_vin_com_ia
    dados_ia = decodificar_vin_com_ia(vin)
    if dados_ia and dados_ia.get("marca") and dados_ia.get("modelo"):
        # Garante que o VIN seja o requisitado
        dados_ia["vin"] = vin
        return dados_ia
    
    # Fallback Offline (Para carros BR que falham no NHTSA e sem API Key)
    wmi = vin[0:3]
    ano_digito = vin[9]
    
    marca = decodificar_wmi_vin(wmi)
    ano = decodificar_ano_vin(ano_digito)
    
    return {
        "vin": vin,
        "marca": marca,
        "modelo": "",
        "ano": ano,
        "motor": "",
        "cilindros": "",
        "potencia_hp": "",
        "combustivel": "",
        "carroceria": "",
        "tracao": "",
        "transmissao": "",
        "portas": "",
        "fabricante": marca,
        "pais_origem": "Brasil" if vin.startswith('9') else "",
    }
