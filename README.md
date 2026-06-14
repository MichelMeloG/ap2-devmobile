# ⚙️ GearUp - O Seu Garagem Virtual & Tuning Studio

Bem-vindo ao **GearUp**, o aplicativo definitivo para entusiastas automotivos que desejam planejar, gerenciar e tunar seus projetos de carros dos sonhos! 

O GearUp não é apenas um bloco de notas; é um estúdio de modificação completo, alimentado por inteligência artificial, projetado para te ajudar a transformar ideias em projetos concretos sem estourar o orçamento.

---

## 🏎️ Como Funciona? (O Fluxo do App)

O GearUp foi pensado para ser incrivelmente intuitivo. Abaixo, você confere o passo a passo completo da experiência do usuário:

### 1. 🏠 O Dashboard: A Sua Garagem
Logo ao abrir o aplicativo, você é recebido pelo seu **Dashboard** (Meus Projetos). Aqui você encontra todos os seus projetos de tunagem atuais listados.
A partir do Dashboard você tem as principais ferramentas na ponta dos dedos: pode visualizar os custos de cada projeto ativo, buscar por oficinas próximas ou iniciar um novo projeto através da nossa Consulta por Chassi.

![Dashboard](docs/screenshots/print_4.jpg)

### 2. 📝 Criando um Novo Projeto
Você tem duas formas fantásticas de iniciar o seu projeto:

**A. Inserção Manual (Novo Projeto):**
Defina o nome da sua build, a marca, o modelo e o seu limite de gastos (orçamento). O app vai te ajudar a manter o controle financeiro do seu projeto, seja ele um *Projeto GT-R* de 300 mil reais ou um carro para o dia a dia!
![Novo Projeto Manual](docs/screenshots/print_5.jpg)

**B. Consulta Inteligente (VIN):**
Está de olho em um carro específico na vida real? Basta inserir o chassi do carro (VIN), e a nossa IA fará uma varredura para extrair e preencher automaticamente o motor, ano, tração e as especificações de fábrica exatas do veículo!
![Consulta VIN](docs/screenshots/print_1.jpg)

### 3. 🛠️ O Tuning Studio: Escolhendo Peças
Após criar o projeto, a diversão começa! Você é levado para a loja virtual de peças personalizadas para o seu modelo.
- **Categorias Inteligentes**: Alterne rapidamente entre peças de **Mecânica** (performance) e **Estética** (visual).
- **Catálogo Dinâmico por Veículo**: O banco de dados se adapta perfeitamente ao calibre do seu projeto. Nas imagens abaixo, você confere como o aplicativo oferece um *Escapamento de Titânio Akrapovič* para um **Nissan GT-R** e um *Coletor Esportivo Inox* focado em custo-benefício para um **Honda Fit**.
- **Carrinho e Desempenho**: Ao selecionar as modificações, a barra inferior soma automaticamente o Custo e o Ganho de HP (cavalos de potência) do seu carro em tempo real.
- **Proteção de Orçamento**: A barra de progresso te mostra exatamente o quanto da sua verba já foi comprometida!

![Peças Nissan GT-R](docs/screenshots/print_6.jpg)
![Peças Honda Fit](docs/screenshots/print_2.jpg)

### 4. 📊 Relatório Final e Análise da IA
Na página de Detalhes do Projeto, você tem a visão de cima sobre a sua obra de arte:
- **Ficha Técnica Real**: Mostra o nome do projeto e os detalhes originais do modelo.
- **Peças Instaladas**: Uma lista compacta mostrando onde o dinheiro foi gasto.
- **Análise da Inteligência Artificial**: O GearUp age como o seu **Mecânico Chefe Virtual**. Ele analisa todas as peças que você selecionou e te entrega um parecer técnico e visceral sobre como o seu carro vai se comportar na pista, curvas e nas ruas!

![Detalhes e IA](docs/screenshots/print_3.jpg)

---

## 🚀 Resumo das Funcionalidades

- **Gerenciamento de Múltiplos Projetos**: Tenha dezenas de builds salvas na sua garagem.
- **Peças Baseadas em Banco de Dados**: A nuvem puxa peças compatíveis focadas em estética e mecânica específicas para o calibre do seu carro.
- **Cálculo em Tempo Real**: Veja na hora o quanto cada peça aumenta no seu orçamento e no seu desempenho (+HP).
- **Tema Automotivo Dark**: Interface desenhada com fundo preto esportivo e destaque em "Racing Orange", remetendo aos painéis de superesportivos.
- **Inteligência Artificial Nativa**: Consulta de VINs misteriosos e feedbacks reais da sua montagem via AI.

---

## 💻 Aspectos Técnicos e Setup da API

Este aplicativo foi desenvolvido como projeto da disciplina de Desenvolvimento Mobile (AP2) e conta com integração a uma robusta API REST.

### Tecnologias Utilizadas
- **Aplicativo Android (Frontend)**: Desenvolvido em Kotlin, utilizando boas práticas, Intents (explícitas e implícitas), RecyclerViews, e a implementação de **Fragments** para injeção dinâmica de Views (como o Resumo da IA).
- **Backend (API REST)**: Desenvolvido em Python utilizando o framework **FastAPI**.
- **Banco de Dados**: Persistência de dados feita em banco de dados relacional robusto (**PostgreSQL** hospedado no Google Cloud SQL, integrado via SQLAlchemy).

### ☁️ API na Nuvem (Bônus AP2)

Nossa API REST está em produção e hospedada no **Google Cloud Run**, permitindo que o aplicativo funcione sem a necessidade de rodar o servidor localmente!
Isso garante uma experiência fluída e atende ao requisito bônus da faculdade de *deploy em ambiente de nuvem*.

- **Endpoint Base**: `https://appmobile-api-908144816287.us-central1.run.app`

### 📖 Documentação da API (Swagger)
O backend atende à obrigatoriedade de documentação Open API. Você pode explorar e testar todos os endpoints (Cadastro, Consulta, Alteração e Remoção) diretamente no navegador através da nuvem:
- **Swagger UI (Online)**: [https://appmobile-api-908144816287.us-central1.run.app/docs](https://appmobile-api-908144816287.us-central1.run.app/docs)

### Como Rodar o Backend Localmente (Opcional)
Caso deseje rodar a API de forma local para desenvolvimento:
1. Navegue até a pasta `api` do projeto e instale as dependências com `pip install -r requirements.txt`.
2. Inicie o servidor FastAPI: `uvicorn app.main:app --host 0.0.0.0 --port 8000 --reload`
3. O Swagger local estará em: `http://localhost:8000/docs`

---
*GearUp - Construa seu projeto perfeito antes de sujar as mãos de graxa.*
