# ByteBank Lite (Console)
### Java Fundamentals by Design

Projeto didático e evolutivo para consolidar **fundamentos reais de Java**, com foco em **design consciente**, não apenas em “fazer funcionar”.

Este projeto foi criado com a ideia de aprender Java **fora do piloto automático**, entendendo *por que* cada decisão é tomada.

---

## 🎯 Objetivo do Projeto

- Consolidar fundamentos essenciais da linguagem Java
- Praticar **encapsulamento**, **imutabilidade** e **identidade**
- Separar claramente:
    - **Domínio** (regras de negócio)
    - **Aplicação** (orquestração / UI)
- Ver a aplicação **rodando de verdade**, desde o início
- Criar um projeto pequeno, mas **bem pensado**

> ❌ Não é um projeto focado em framework  
> ✅ É um projeto focado em **pensar como desenvolvedor Java**

---

## 🧱 Stack

- Java 17
- Maven
- Aplicação Console (Sprint 1)
- Swing (planejado para sprints futuros)

---

## 📦 Estrutura de Pacotes

```
br.com.bytebank
├── app
│ ├── Main.java
│ └── AppConsole.java
│
├── domain
│ ├── cliente
│ │ └── Cliente.java
│ └── conta
│ └── Conta.java
│
└── exception
└── (planejado)
```


### Regras de Arquitetura

- `domain`
    - Contém **toda a regra de negócio**
    - Protege o estado das entidades
    - Não depende de UI nem de infraestrutura

- `app`
    - Apenas **orquestra** o fluxo
    - Lê dados do usuário
    - Chama métodos do domínio
    - **Não decide regras**

📌 Nada fora do `domain` pode alterar diretamente o estado das entidades.

---

## ✅ Funcionalidades (Sprint 1)

- Criar cliente
- Criar conta para cliente existente
- Listar clientes
- Listar contas
- Depositar valor em conta
- Sacar valor de conta
- Transferir valor entre contas

Tudo executado via **console**.

---

## ▶️ Como Rodar

### IntelliJ IDEA (recomendado)
1. Abra o projeto Maven
2. Localize a classe: br.com.bytebank.app.Main
3. Execute o método `main`

### Fluxo sugerido de uso
1. Criar cliente
2. Copiar o UUID gerado
3. Criar conta usando o UUID do cliente
4. Depositar
5. Sacar
6. Criar outra conta e transferir

---

## 🧠 Conceitos Aplicados (Sprint 1)

### Cliente Imutável
- Um `Cliente` não muda depois de criado
- Correções são feitas por **substituição**, mantendo o mesmo `id`
- Evita efeitos colaterais e bugs invisíveis

### Conta com Estado Mutável Controlado
- `saldo` **pode mudar**, mas:
- nunca é público
- nunca tem `setSaldo`
- só muda via métodos (`depositar`, `sacar`, `transferirPara`)

### Identidade
- `equals` e `hashCode` baseados apenas no `UUID`
- Identidade ≠ dados mutáveis

### Encapsulamento Real
- Leitura permitida (`getSaldo`)
- Escrita apenas via regras do domínio

### BigDecimal para Dinheiro
- Evita problemas clássicos de arredondamento do `double`
- Padronização de casas decimais

---

## 🗺️ Roadmap (Visão Geral)

- **Sprint 2**
- Repositórios via interface
- Persistência simples (arquivo ou memória)
- **Sprint 3**
- Transações e extrato (objetos imutáveis)
- **Sprint 4**
- equals/hashCode na prática com `Map` e `Set`
- **Sprint 5**
- Exceções de domínio específicas
- **Sprint 6**
- Organização por módulos e pacotes
- **Sprint 7**
- UI Swing simples
- **Sprint 8**
- Conexão conceitual com Spring (opcional)

---

## 🧩 Filosofia do Projeto

> “Imutabilidade não impede mudança.  
> Ela impede mudança invisível.”

> “Domínio protege regras.  
> Aplicação apenas orquestra.”

Este repositório é sobre **decisões**, não sobre quantidade de código.

---

## 👨‍💻 Autor

Projeto desenvolvido como estudo prático de fundamentos Java, com foco em:
- clareza
- consistência
- evolução consciente

---
