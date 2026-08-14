[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/q5A4m_h4)
# 🧪 2026 - Trabajo Práctico Anual

## 👤 Datos del Alumno
- **Nombre: Luciano**
- **Apellido: Suarez**

---

🧩 Componente Desarrollado
- **Incentivos**

---
🧩 Link al swagger
- https://app.swaggerhub.com/apis/utn-5df/Incentivos_endpoints/1.0.0

- Swagger UI (app): https://incentivos-yuse.onrender.com/swagger-ui/index.html 
---

🧩 Link al despliegue en Render
-https://incentivos-yuse.onrender.com

---

🧩 Variables de entorno utilizadas
- `URL_DONADORES_ENTIDADES`
- `URL_DONACIONES`
- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `DB_DRIVER`
- `DB_DIALECT`
- `DD_API_KEY`
- `INCENTIVOS_PROCESAMIENTO_INTERVALO_MS`


---

🧩 Criterios de misiones
- `COMPLETITUD`: 3 categorías distintas.
- `DONACIONES_EXITOSAS`: 20 donaciones aceptadas.
- `DONACIONES_ASCENDENTES`: últimas 5 donaciones en tendencia ascendente.
- `REVOLUCION_DONADORA`: más de 10 donaciones con cantidad mayor a 50.

---

🧩 Procesamiento periódico de misiones
- Se incorporó un cron-job que procesa automáticamente a los donadores con misión asignada.
- Intervalo configurable con `INCENTIVOS_PROCESAMIENTO_INTERVALO_MS` (por defecto: `60000` ms).
- Si un donador pierde la condición de la misión **Donaciones Exitosas** (menos de 20 `ACEPTADA`), se aplica rollback:
  - se remueve la insignia de esa misión,
  - se retrocede de categoría,
  - se reasigna la misión para recomenzar el progreso.
- Se agregaron logs de trazabilidad del flujo de procesamiento y del cron para facilitar diagnóstico.

---


