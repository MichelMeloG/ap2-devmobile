import os
import json
import google.generativeai as genai
from fastapi import HTTPException

# Configuração do Gemini AI
GEMINI_API_KEY = os.getenv("GEMINI_API_KEY")

if GEMINI_API_KEY:
    genai.configure(api_key=GEMINI_API_KEY)

def gerar_pecas_com_ia(modelo_carro: str) -> list[dict]:
    """
    Gera uma lista de peças de modificação para um carro usando IA.
    Retorna uma lista de dicionários no formato:
    [{"nome": "...", "preco": 1200.50, "tipo": "Mecânica"}]
    """
    if not GEMINI_API_KEY:
        print("GEMINI_API_KEY não configurada. Usando mock offline.")
        return _gerar_mock_pecas(modelo_carro)

    prompt = f"""
Você é um especialista em tuning e modificações automotivas do Brasil.
Um usuário quer modificar um carro modelo: '{modelo_carro}'.
Gere 6 peças reais e compatíveis de alta performance ou estética para esse carro.
Retorne APENAS um array JSON válido sem markdown ou backticks. Cada objeto deve ter:
- "nome": string (O nome da peça específico para esse carro, ex: "Downpipe Inox 3 polegadas Golf GTI")
- "preco": float (Preço médio realista em reais BRL, sem simbolo, apenas numero, ex: 1500.0)
- "tipo": string (Deve ser exatamente "Mecânica" ou "Estética")
"""
    try:
        model = genai.GenerativeModel('gemini-1.5-flash')
        response = model.generate_content(prompt)
        
        texto = response.text.strip()
        if texto.startswith("```json"):
            texto = texto[7:]
        elif texto.startswith("```"):
            texto = texto[3:]
        if texto.endswith("```"):
            texto = texto[:-3]
        texto = texto.strip()
        
        pecas = json.loads(texto)
        return pecas
    except Exception as e:
        print(f"Erro ao gerar peças com IA: {e}")
        return _gerar_mock_pecas(modelo_carro)

def decodificar_vin_com_ia(vin: str) -> dict:
    if not GEMINI_API_KEY:
        return None
        
    prompt = f"""
Atue como um especialista em decodificação de chassis (VIN).
Decodifique o chassi '{vin}'. Este chassi provavelmente é brasileiro.
Tente extrair a marca, modelo específico, ano de fabricação, motor, número de portas, tipo de carroceria e tipo de combustível apenas baseado na estrutura e caracteres do chassi.
Retorne APENAS um objeto JSON válido sem markdown ou backticks com as seguintes chaves (use strings vazias se não souber):
- "marca": string
- "modelo": string
- "ano": string
- "motor": string
- "cilindros": string
- "potencia_hp": string
- "combustivel": string
- "carroceria": string
- "tracao": string
- "transmissao": string
- "portas": string
- "fabricante": string
- "pais_origem": string
"""
    try:
        model = genai.GenerativeModel('gemini-1.5-flash')
        response = model.generate_content(prompt)
        
        texto = response.text.strip()
        if texto.startswith("```json"):
            texto = texto[7:]
        elif texto.startswith("```"):
            texto = texto[3:]
        if texto.endswith("```"):
            texto = texto[:-3]
        texto = texto.strip()
        
        dados = json.loads(texto)
        return dados
    except Exception as e:
        print(f"Erro ao decodificar VIN com IA: {e}")
        return None

def _gerar_mock_pecas(modelo_carro: str) -> list[dict]:
    return [
        {"nome": f"Filtro Esportivo Cônico ({modelo_carro})", "preco": 350.0, "tipo": "Mecânica"},
        {"nome": f"Kit Molas Esportivas ({modelo_carro})", "preco": 950.0, "tipo": "Mecânica"},
        {"nome": f"Escapamento Esportivo ({modelo_carro})", "preco": 1200.0, "tipo": "Mecânica"},
        {"nome": "Kit Farol de Milha LED", "preco": 450.0, "tipo": "Estética"},
        {"nome": "Rodas de Liga Leve Aro 18", "preco": 3500.0, "tipo": "Estética"}
    ]
