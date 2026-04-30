# Diagrama de Arquitectura – Servicio de Incentivos – Entrega 1

## Diagrama de Componentes

```mermaid
graph TD
    C["Cliente HTTP"]

    FI["Fachada\nimplements FachadaIncentivos"]
    DR["DonadorRepo"]
    IR["InsigniaRepo"]
    MR["MisionRepo"]
    D["Donador"]
    CC["CambioCategoria"]
    INS["Insignia"]
    MA["Mision abstracta"]
    MC["MisionCompletitud"]
    MD["MisionDonacionesExitosas"]

    FDE["FachadaDonadoresYEntidades\nexterno - simulado con mock"]
    FD["FachadaDonaciones\nexterno - simulado con mock"]

    C -->|invoca metodos| FI
    FI -->|verifica existencia donador| FDE
    FI -->|consulta historial donaciones| FD
    FI --> DR
    FI --> IR
    FI --> MR
    DR --> D
    IR --> INS
    MR --> MA
    MC -->|extiende| MA
    MD -->|extiende| MA
    D --> INS
    D --> MA
    D --> CC
```

---

## Diagrama de Despliegue

```mermaid
graph LR
    subgraph JVM["JVM - proceso unico durante tests"]
        FACHADA["Fachada\nServicio Incentivos"]
        MOCK_DE["Mock FachadaDonadoresYEntidades\nMockito"]
        MOCK_DON["Mock FachadaDonaciones\nMockito"]
        MEM["Repositorios en memoria\nDonadorRepo - InsigniaRepo - MisionRepo"]
    end

    FACHADA -->|setter| MOCK_DE
    FACHADA -->|setter| MOCK_DON
    FACHADA --> MEM
```

> No hay base de datos ni persistencia en disco. Los repos usan listas en memoria siguiendo el patron Repository. Se reemplazara con persistencia real en entregas posteriores.

---

## Interacciones simuladas

| Origen | Destino | Operacion | Como se simula |
|---|---|---|---|
| Incentivos | Donadores y Entidades | buscarDonadorPorID | Mock Mockito |
| Incentivos | Donaciones | buscarPorDonadorYFechaInicio | Mock Mockito |

---

## Flujo principal: procesarDonador

```mermaid
sequenceDiagram
    actor Cron
    participant F as Fachada Incentivos
    participant DE as Mock DonadoresYEntidades
    participant DON as Mock Donaciones
    participant DR as DonadorRepo
    participant D as Donador

    Cron->>F: procesarDonador(donadorID)
    F->>DE: buscarDonadorPorID(donadorID)
    DE-->>F: Donador externo o lanza excepcion
    F->>DR: obtenerOCrearDonador(donadorID)
    DR-->>F: Donador
    F->>DON: buscarPorDonadorYFechaInicio(donadorID, fecha)
    DON-->>F: lista de Donacion
    F->>F: extraerDatosParaMision + estaCumplida
    alt mision cumplida
        F->>D: agregarInsignia + avanzarCategoria
        Note right of D: registrar CambioCategoria
    end
```
