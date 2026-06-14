import os
import json
import google.generativeai as genai

# Configuração do Gemini AI
GEMINI_API_KEY = os.getenv("GEMINI_API_KEY")

if GEMINI_API_KEY:
    genai.configure(api_key=GEMINI_API_KEY)


def gerar_pecas_com_ia(modelo_carro: str) -> list[dict]:
    """
    Gera uma lista de peças de modificação para um carro usando IA.
    Retorna uma lista de dicionários com: nome, preco, tipo, ganho_hp, descricao.
    """
    if not GEMINI_API_KEY:
        print("GEMINI_API_KEY não configurada. Usando mock offline.")
        return _gerar_mock_pecas(modelo_carro)

    prompt = f"""
Você é um especialista em tuning e modificações automotivas do Brasil.
Um usuário quer modificar um carro modelo: '{modelo_carro}'.
Gere 8 peças reais e compatíveis de alta performance ou estética para esse carro.
Retorne APENAS um array JSON válido sem markdown ou backticks. Cada objeto deve ter:
- "nome": string (O nome da peça específico para esse carro, ex: "Downpipe Inox 3 polegadas")
- "preco": float (Preço médio realista em reais BRL, apenas numero, ex: 1500.0)
- "tipo": string (Deve ser exatamente "Mecânica" ou "Estética")
- "ganho_hp": int (Estimativa realista de ganho de cavalos de potência com essa peça, 0 se for estética)
- "descricao": string (Breve descrição de 1 linha sobre o que a peça faz e por que é boa para esse carro)
"""
    try:
        model = genai.GenerativeModel('gemini-flash-latest')
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
        # Garante que todos os campos existem
        for p in pecas:
            p.setdefault("ganho_hp", 0)
            p.setdefault("descricao", "")
        return pecas
    except Exception as e:
        print(f"Erro ao gerar peças com IA: {e}")
        return _gerar_mock_pecas(modelo_carro)


def decodificar_vin_com_ia(vin: str) -> dict | None:
    """
    Usa o Gemini para decodificar um chassi (VIN) e retornar dados do veículo.
    """
    if not GEMINI_API_KEY:
        print("GEMINI_API_KEY não configurada para decodificação VIN.")
        return None
        
    prompt = f"""
Atue como um especialista em decodificação de chassis (VIN) automotivo.
Decodifique o chassi '{vin}'.
As 3 primeiras letras do chassi (WMI) identificam o fabricante e o país.
O 10º caractere identifica o ano-modelo.
Analise a estrutura completa do VIN e forneça o máximo de informações possíveis sobre esse veículo.
Se o VIN começar com '9', é um veículo fabricado no Brasil.
Se o VIN começar com '8', é um veículo fabricado na Argentina.

Retorne APENAS um objeto JSON válido sem markdown ou backticks com as seguintes chaves.
Use strings vazias "" para campos que não conseguir determinar com certeza:
- "marca": string (ex: "Honda", "Volkswagen")
- "modelo": string (ex: "Civic", "Golf GTI")
- "ano": string (ex: "2004")
- "motor": string (ex: "1.7L i-VTEC")
- "cilindros": string (ex: "4")
- "potencia_hp": string (ex: "115")
- "combustivel": string (ex: "Gasolina", "Flex", "Diesel")
- "carroceria": string (ex: "Sedan", "Hatchback", "SUV")
- "tracao": string (ex: "Dianteira (FWD)", "Traseira (RWD)", "Integral (AWD)")
- "transmissao": string (ex: "Manual 5 marchas", "Automático CVT")
- "portas": string (ex: "4", "2")
- "fabricante": string (ex: "Honda do Brasil")
- "pais_origem": string (ex: "Brasil", "Alemanha")
"""
    try:
        model = genai.GenerativeModel('gemini-flash-latest')
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


def gerar_resumo_projeto_com_ia(modelo_carro: str, pecas: list[dict]) -> str:
    """
    Gera um resumo inteligente do projeto de modificação usando IA.
    """
    if not GEMINI_API_KEY:
        return "Configure a GEMINI_API_KEY para receber análises inteligentes da IA."

    pecas_texto = "\n".join([
        f"- {p.get('nome', 'Peça')}: R$ {p.get('preco', 0):.2f} (+{p.get('ganho_hp', 0)} hp)"
        for p in pecas
    ])
    ganho_total = sum(p.get("ganho_hp", 0) for p in pecas)
    custo_total = sum(p.get("preco", 0) for p in pecas)

    prompt = f"""
Atue como um mecânico chefe e especialista em tuning automotivo brasileiro.
Um cliente montou um projeto para um {modelo_carro} com as seguintes peças:

{pecas_texto}

Custo total: R$ {custo_total:.2f}
Ganho total estimado: +{ganho_total} hp

Escreva um resumo curto e super empolgante (máximo 3 a 4 frases) analisando APENAS as peças escolhidas neste projeto.
Seja criativo e fale com propriedade. 
Inclua:
1. Uma opinião profissional sobre a qualidade e compatibilidade da escolha das peças em conjunto.
2. Como o carro vai se comportar com esse setup específico (ronco, aceleração, visual, dinâmica, etc).

IMPORTANTE: NÃO dê sugestões de próximas peças, não ofereça dicas de próximos passos e não sugira o que fazer depois. Apenas comente o que o usuário já escolheu.
Use emojis para deixar o texto vivo! Responda apenas o texto do resumo, sem formatação markdown.
"""
    try:
        model = genai.GenerativeModel('gemini-flash-latest')
        response = model.generate_content(prompt)
        return response.text.strip()
    except Exception as e:
        print(f"Erro ao gerar resumo com IA: {e}")
        return f"Projeto com {len(pecas)} peças selecionadas. Ganho estimado de +{ganho_total} hp. Custo total: R$ {custo_total:.2f}."


def _gerar_mock_pecas(modelo_carro: str) -> list[dict]:
    return [
        {"nome": f"Filtro Esportivo Cônico ({modelo_carro})", "preco": 350.0, "tipo": "Mecânica", "ganho_hp": 5, "descricao": "Aumenta o fluxo de ar para o motor, melhorando a respiração"},
        {"nome": f"Kit Molas Esportivas ({modelo_carro})", "preco": 950.0, "tipo": "Mecânica", "ganho_hp": 0, "descricao": "Rebaixa o centro de gravidade em 35mm para melhor estabilidade"},
        {"nome": f"Escapamento Esportivo Inox ({modelo_carro})", "preco": 1800.0, "tipo": "Mecânica", "ganho_hp": 8, "descricao": "Escape com menor restrição e som esportivo marcante"},
        {"nome": f"Downpipe 3 polegadas ({modelo_carro})", "preco": 1200.0, "tipo": "Mecânica", "ganho_hp": 15, "descricao": "Reduz a contrapressão dos gases de escape drasticamente"},
        {"nome": f"Chip de Potência Stage 1 ({modelo_carro})", "preco": 2500.0, "tipo": "Mecânica", "ganho_hp": 30, "descricao": "Remapeamento da ECU para mais potência e torque"},
        {"nome": "Rodas de Liga Leve Aro 18", "preco": 3500.0, "tipo": "Estética", "ganho_hp": 0, "descricao": "Rodas leves que melhoram a aparência e reduzem peso não-suspendido"},
        {"nome": "Envelopamento Preto Fosco Completo", "preco": 4500.0, "tipo": "Estética", "ganho_hp": 0, "descricao": "Visual agressivo com proteção da pintura original"},
        {"nome": "Kit Farol de Milha LED", "preco": 450.0, "tipo": "Estética", "ganho_hp": 0, "descricao": "Iluminação LED de alta potência com visual moderno"},
    ]
