# 📊 Diagrama de Clases – Entrega 2 - Componente Incentivos

## Descripción General

El componente de Incentivos del TP de DDSI gestiona insignias (reconocimientos) y misiones (objetivos) para donadores. Las misiones evalúan el cumplimiento según diferentes criterios y permiten que los donadores avancen de categoría.

---

## Diagrama de Clases UML

```mermaid
classDiagram
    direction TB

    %% ========== INTERFACES (Cátedra) ==========
    class FachadaIncentivos {
        <<interface>>
        +agregarInsignia(nombre, descripcion) Insignia
        +agregarMision(nombre, insigniaID, categoriaInicio, categoriaFin, tipo) Mision
        +asignarInsigniaADonador(donadorID, Insignia) void
        +asignarMisionADonador(donadorID, Mision) void
        +getInsigniasDeDonador(donadorID) List~Insignia~
        +getMisionEnCursoDeDonador(donadorID) Mision
        +procesarDonador(donadorID) void
        +setFachadaDonaciones(FachadaDonaciones) void
        +setFachadaDonadoresYEntidades(FachadaDonadoresYEntidades) void
    }

    class FachadaDonaciones {
        <<interface>>
        +buscarPorDonadorYFechaInicio(donadorID, fecha) List~Donacion~
        +buscarProductoPorID(productoID) Producto
    }

    class FachadaDonadoresYEntidades {
        <<interface>>
        +buscarDonadorPorID(donadorID) Donador
        +agregarDonador(donador) Donador
    }

    %% ========== FACHADA (Núcleo) ==========
    class Fachada {
        -DonadorRepo donadorRepo
        -MisionRepo misionRepo
        -InsigniaRepo insigniaRepo
        -AtomicLong insigniaSeq
        -AtomicLong misionSeq
        -FachadaDonaciones fachadaDonaciones
        -FachadaDonadoresYEntidades fachadaDonadoresYEntidades
        +agregarInsignia(nombre, descripcion) Insignia
        +agregarMision(nombre, insigniaID, categoriaInicio, categoriaFin, tipo) Mision
        +getInsignias() List~Insignia~
        +getMisiones() List~Mision~
        +asignarInsigniaADonador(donadorID, Insignia) void
        +asignarMisionADonador(donadorID, Mision) void
        +getInsigniasDeDonador(donadorID) List~Insignia~
        +getMisionEnCursoDeDonador(donadorID) Mision
        +procesarDonador(donadorID) void
        -verificarExistenciaExterna(donadorID) void
        -obtenerOCrearDonador(donadorID) Donador
        -extraerDatosParaMision(Mision, List) List~String~
        -buscarMisionParaCategoria(CategoriaDonadorEnum) Mision
    }

    %% ========== DOMINIO - INSIGNIAS ==========
    class Insignia {
        -String insigniaID
        -String nombre
        -String descripcion
        +getInsigniaID() String
        +getNombre() String
        +getDescripcion() String
    }

    %% ========== DOMINIO - MISIONES ==========
    class Mision {
        <<abstract>>
        -String misionID
        -String nombre
        -String insigniaID
        -CategoriaDonadorEnum categoriaInicio
        -CategoriaDonadorEnum categoriaFin
        +estaCumplida(List) boolean*
        +getTipo() TipoMisionEnum*
        +getMisionID() String
        +getNombre() String
        +getInsigniaID() String
        +getCategoriaInicio() CategoriaDonadorEnum
        +getCategoriaFin() CategoriaDonadorEnum
        +setNombre(String) void
        +setInsigniaID(String) void
    }

    class MisionCompletitud {
        +estaCumplida(donaciones: List) boolean
        +getTipo() TipoMisionEnum
    }

    class MisionDonacionesExitosas {
        +estaCumplida(donaciones: List) boolean
        +getTipo() TipoMisionEnum
    }

    class MisionDonacionesAscendentes {
        +estaCumplida(donaciones: List) boolean
        +getTipo() TipoMisionEnum
    }

    class MisionRevolucionDonadora {
        +estaCumplida(donaciones: List) boolean
        +getTipo() TipoMisionEnum
    }

    %% ========== DOMINIO - DONADOR ==========
    class Donador {
        -String donadorID
        -CategoriaDonadorEnum categoria
        -List~Insignia~ insignias
        -List~CambioCategoria~ historialCategorias
        -Mision misionActual
        +agregarInsignia(Insignia) void
        +avanzarCategoria(CategoriaDonadorEnum, Mision) void
        +getDonadorID() String
        +getCategoria() CategoriaDonadorEnum
        +getInsignias() List~Insignia~
        +getMisionActual() Mision
        +getHistorialCategorias() List~CambioCategoria~
        +setMisionActual(Mision) void
        +setCategoria(CategoriaDonadorEnum) void
    }

    class CambioCategoria {
        -CategoriaDonadorEnum categoriaAnterior
        -CategoriaDonadorEnum categoriaNueva
        -LocalDateTime fecha
        -String motivo
    }

    %% ========== REPOSITORIOS ==========
    class DonadorRepo {
        -List~Donador~ donadores
        +guardar(Donador) void
        +buscar(String) Donador
        +existe(String) boolean
        +todos() List~Donador~
    }

    class InsigniaRepo {
        -List~Insignia~ insignias
        +guardar(Insignia) void
        +buscar(String) Insignia
        +todas() List~Insignia~
    }

    class MisionRepo {
        -List~Mision~ misiones
        +guardar(Mision) void
        +buscar(String) Mision
        +todas() List~Mision~
    }

    %% ========== CONTROLADOR REST ==========
    class IncentivosController {
        -Fachada fachada
        +crearInsignia(InsigniaDTO) ResponseEntity
        +listarInsignias() ResponseEntity
        +buscarInsignia(id) ResponseEntity
        +asignarInsigniaADonador(donadorID, request) ResponseEntity
        +getInsigniasDeDonador(donadorID) ResponseEntity
        +crearMision(MisionDTO) ResponseEntity
        +listarMisiones() ResponseEntity
        +buscarMision(id) ResponseEntity
        +asignarMisionADonador(donadorID, request) ResponseEntity
        +getMisionEnCursoDeDonador(donadorID) ResponseEntity
        +procesarDonador(donadorID) ResponseEntity
    }

    %% ========== EXCEPCIONES ==========
    class DonadorNoEncontradoException {
    }

    class EntidadNoEncontradaException {
    }

    %% ========== ENUMERACIONES ==========
    class CategoriaDonadorEnum {
        <<enumeration>>
        OCASIONAL
        COLABORADOR
        TRANSFORMADOR
    }

    class TipoMisionEnum {
        <<enumeration>>
        COMPLETITUD
        DONACIONES_EXITOSAS
        DONACIONES_ASCENDENTES
        REVOLUCION_DONADORA
    }

    %% ========== RELACIONES ==========
    Fachada ..|> FachadaIncentivos
    Fachada ..> FachadaDonaciones : usa
    Fachada ..> FachadaDonadoresYEntidades : usa
    Fachada --> DonadorRepo
    Fachada --> InsigniaRepo
    Fachada --> MisionRepo

    IncentivosController --> Fachada : usa

    DonadorRepo --> Donador
    DonadorRepo ..> DonadorNoEncontradoException : lanza
    InsigniaRepo --> Insignia
    InsigniaRepo ..> EntidadNoEncontradaException : lanza
    MisionRepo --> Mision
    MisionRepo ..> EntidadNoEncontradaException : lanza

    Donador "1" o-- "*" Insignia : tiene
    Donador "1" o-- "1" Mision : misionActual
    Donador "1" o-- "*" CambioCategoria : registra
    Donador --> CategoriaDonadorEnum

    Mision <|-- MisionCompletitud
    Mision <|-- MisionDonacionesExitosas
    Mision <|-- MisionDonacionesAscendentes
    Mision <|-- MisionRevolucionDonadora
    Mision --> CategoriaDonadorEnum
    Mision --> TipoMisionEnum

    CambioCategoria --> CategoriaDonadorEnum
```

---

## Descripciones de Clases

### Interfaz Principal (Cátedra)
| Clase | Responsabilidad |
|-------|-----------------|
| **FachadaIncentivos** | Contrato que define las operaciones del componente de Incentivos |

### Interfaces Externas (Cátedra)
| Clase | Responsabilidad |
|-------|-----------------|
| **FachadaDonaciones** | Acceso a datos de donaciones de otros componentes |
| **FachadaDonadoresYEntidades** | Acceso a datos de donadores y entidades |

### Fachada (Núcleo del Componente)
| Clase | Responsabilidad |
|-------|-----------------|
| **Fachada** | Orquesta operaciones del componente. Coordina repositorios locales e interfaces externas |

### Dominio - Insignias
| Clase | Responsabilidad |
|-------|-----------------|
| **Insignia** | Entidad de dominio que representa un reconocimiento |

### Dominio - Misiones
| Clase | Responsabilidad |
|-------|-----------------|
| **Mision** | Clase abstracta que define el contrato de una misión |
| **MisionCompletitud** | Donador debe donar de 3+ categorías diferentes |
| **MisionDonacionesExitosas** | Donador debe tener 20+ donaciones aceptadas |
| **MisionDonacionesAscendentes** | Donador debe realizar donaciones de cantidad ascendente |
| **MisionRevolucionDonadora** | Donador debe alcanzar volumen específico total |

### Dominio - Donador Local
| Clase | Responsabilidad |
|-------|-----------------|
| **Donador** | Entidad local que rastrea insignias, misión actual e historial de categorías |
| **CambioCategoria** | Registro de transiciones de categoría |

### Repositorios
| Clase | Responsabilidad |
|-------|-----------------|
| **DonadorRepo** | Persiste donadores locales en memoria |
| **InsigniaRepo** | Persiste insignias en memoria |
| **MisionRepo** | Persiste misiones en memoria |

### Controlador REST
| Clase | Responsabilidad |
|-------|-----------------|
| **IncentivosController** | Expone endpoints REST del componente |

### Excepciones
| Clase | Responsabilidad |
|-------|-----------------|
| **DonadorNoEncontradoException** | Lanzada cuando donador no existe |
| **EntidadNoEncontradaException** | Lanzada cuando insignia o misión no existe |

### Enumeraciones
| Clase | Valores |
|-------|--------|
| **CategoriaDonadorEnum** | OCASIONAL, COLABORADOR, TRANSFORMADOR |
| **TipoMisionEnum** | COMPLETITUD, DONACIONES_EXITOSAS, DONACIONES_ASCENDENTES, REVOLUCION_DONADORA |

---

## Flujos Principales

### 1. Crear Insignia
```
POST /insignias → Controller → Fachada.agregarInsignia() → InsigniaRepo
```

### 2. Asignar Insignia a Donador
```
POST /insignias/{donadorID}
→ Fachada.asignarInsigniaADonador()
→ Verifica existencia en FachadaDonadoresYEntidades
→ Obtiene/crea Donador local
→ Añade Insignia a Donador
```

### 3. Procesar Donador
```
POST /procesamiento/{donadorID}
→ Fachada.procesarDonador()
→ Obtiene Mision en curso
→ Consulta FachadaDonaciones
→ Evalúa Mision.estaCumplida()
→ Si cumplida: asigna insignia y avanza categoría
```

---

## Notas Importantes

- La entrega se centra **únicamente** en el componente de Incentivos
- No se modifican archivos en `/catedra`
- Los modelos locales (`Donador`, `Insignia`, `Mision`) persisten en memoria
- Las consultas de donadores externos usan `FachadaDonadoresYEntidades`
- Las consultas de donaciones externas usan `FachadaDonaciones`
