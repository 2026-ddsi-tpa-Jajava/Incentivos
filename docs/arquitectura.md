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
    subgraph Render["Plataforma Render"]
        subgraph Docker["Contenedor Docker (App)"]
            FACHADA["Fachada\nServicio Incentivos"]
            MEM["Repositorios JPA\nDonadorRepo - InsigniaRepo - MisionRepo"]
            HTTP_DE["FachadaDonadoresYEntidadesHttp\n(Cliente REST)"]
            HTTP_DON["FachadaDonacionesHttp\n(Cliente REST)"]
        end
        DB[("PostgreSQL\n(Instancia Render)")]
    end

    subgraph API_Companeros["Otros Microservicios"]
        API_DE["API Donadores y Entidades"]
        API_DON["API Donaciones"]
    end

    FACHADA --> MEM
    MEM -->|Persistencia ORM| DB
    FACHADA --> HTTP_DE
    FACHADA --> HTTP_DON
    HTTP_DE -->|HTTP/REST| API_DE
    HTTP_DON -->|HTTP/REST| API_DON
```
> Nota de Arquitectura: En esta entrega se migró la persistencia en memoria hacia una base de datos relacional PostgreSQL desplegada en Render, utilizando Spring Data JPA como ORM. Además, las integraciones simuladas fueron reemplazadas por clientes HTTP (utilizando HttpClient nativo de Java 11+) para comunicarse de forma sincrónica con las APIs reales del resto del equipo.

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
