# ByteBank Lite (Console)

## 📋 Sobre o Projeto

O **ByteBank Lite** simula um sistema bancário mínimo rodando via **console**, com:

- cadastro de clientes
- criação de contas
- operações de saldo (depósito/saque/transferência)
- **extrato/histórico de operações**
- **persistência em arquivo (CSV)** para manter estado entre execuções

O projeto é organizado para refletir um sistema real:

- **Domain**: regras de negócio e invariantes
- **App**: orquestração e interface (console)
- **Infra**: persistência (CSV) e detalhes técnicos

---

## 🎯 Objetivos Técnicos

- Praticar **encapsulamento** e **imutabilidade**
- Modelar entidades com **identidade (UUID)** e regras claras
- Diferenciar **criação** vs **reconstrução** de entidades (persistência)
- Aplicar **interfaces (contratos)** para repositórios
- Persistir dados sem frameworks, entendendo o fluxo completo

---

## 🛠️ Tecnologias

- **Java 17**
- **Maven**
- **Console Application**
- Persistência simples via **CSV (java.nio.file)**

---

## ✅ Funcionalidades

### Público (sem login)

- [x] Criar cliente
- [x] Listar clientes
- [x] Login (selecionar cliente)

### Cliente logado

- [x] Criar conta para o cliente logado
- [x] Listar minhas contas
- [x] Depositar / Sacar / Transferir
- [x] **Extrato da conta (histórico de operações)**
- [x] Logout

### Persistência

- [x] Clientes persistidos em `data/clientes.csv`
- [x] Contas persistidas em `data/contas.csv`
- [x] Lançamentos persistidos em `data/lancamentos.csv`

---

## 🧱 Arquitetura e Pacotes

```
br.com.bytebank
├── app
│ ├── Main.java # composição (wiring)
│ └── AppConsole.java # UI console + fluxo
│
├── domain
│ ├── cliente
│ │ ├── Cliente.java
│ │ └── RepositorioCliente.java
│ ├── conta
│ │ ├── Conta.java
│ │ └── RepositorioConta.java
│ └── extrato
│ ├── Lancamento.java
│ ├── TipoOperacao.java
│ └── RepositorioLancamento.java
│
└── infra
├── persistencia
│ └── CsvIO.java
├── cliente
│ └── RepositorioClienteEmArquivo.java
├── conta
│ └── RepositorioContaEmArquivo.java
└── extrato
└── RepositorioLancamentoEmArquivo.java
```

### Regras de Arquitetura

- `domain` **não depende** de `app` nem de `infra`
- `app` orquestra o fluxo e chama o domínio (não contém regra)
- `infra` implementa detalhes técnicos (persistência em arquivo) via **interfaces do domínio**
- Nenhuma classe fora do domínio altera o estado diretamente sem passar por métodos do domínio

---

## 🧠 Conceitos Aplicados

### Encapsulamento (Conta)

- saldo não é público
- não existe `setSaldo`
- saldo muda apenas por `depositar`, `sacar`, `transferirPara`

### Imutabilidade (Cliente)

- `Cliente` não muda após criado
- correções são feitas por substituição mantendo o mesmo `id`

### Identidade

- `equals/hashCode` baseados no `UUID`
- identidade ≠ dados mutáveis (nome/saldo)

### Persistência sem Framework

- Repositórios por contrato (`RepositorioX`)
- Implementação em CSV (`RepositorioXEmArquivo`)
- `Conta.reconstruir(...)` e `Lancamento.reconstruir(...)` para rehidratar estado corretamente

### Extrato (Eventos de domínio)

- `Lancamento` representa um **fato ocorrido** (não “ação”)
- contém: `contaId`, `instante`, `tipo`, `valor`, `saldoApos`, contrapartida (opcional)
- extrato é lido a partir dos lançamentos persistidos

---

## 🚀 Como Executar

### Pré-requisitos

- Java 17+
- Maven 3.8+

### Rodando

```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="br.com.bytebank.app.Main"
```

Ou execute diretamente pelo IntelliJ:

- br.com.bytebank.app.Main

---

### Persistência

Ao executar, o sistema cria/usa a pasta data/ na raiz do projeto:

- data/clientes.csv
- data/contas.csv
- data/lancamentos.csv

---

### 🧪 Fluxo sugerido (teste manual)

    1 - Criar cliente (público)
    2 - Login como cliente
    3 - Criar conta
    4 - Depositar
    5 - Sacar
    6 - Transferir para outra conta (UUID)
    7 - Ver extrato
    8 - Encerrar e abrir novamente para validar persistência

---

### 🗺️ Roadmap

- [ ] Testes unitários do domínio (JUnit)
- [ ] Regras de validação e exceções específicas (ex: SaldoInsuficienteException)
- [ ] Extrato com filtro por período e ordenação customizada
- [ ] UI Swing (camada de apresentação reutilizando o domínio)
- [ ] Camada de serviço (application services) para deixar AppConsole mais fina

---

### 👨‍💻 Autor

**Wesley Werikis**

Projeto desenvolvido como estudo prático de fundamentos Java com foco em:
clareza, consistência e evolução consciente.

---
