# TCG - Portfolio Personal de Cartas Pokémon

Aplicación personal para escanear cartas Pokémon mediante fotografía y gestionar una colección digital.

## Arquitectura

```
Android → FastAPI → n8n → FastAPI → Android
```

- **Android** (Kotlin + Jetpack Compose): Cliente principal para capturar y gestionar cartas
- **FastAPI** (Python): API principal del backend
- **n8n**: Motor de automatización para procesamiento de IA
- **PostgreSQL**: Base de datos en EasyPanel (externa, no en Docker)

## Estructura del proyecto

```
TCG/
├── backend/              # FastAPI + SQLAlchemy + Alembic
│   ├── app/
│   │   ├── main.py       # Punto de entrada de la aplicación
│   │   ├── config.py     # Configuración (variables de entorno)
│   │   ├── database.py   # Conexión a PostgreSQL
│   │   ├── models/       # Modelos SQLAlchemy (sets, cards, collection)
│   │   ├── schemas/      # Esquemas Pydantic
│   │   ├── routers/      # Endpoints de la API
│   │   ├── services/     # Lógica de negocio (n8n, scan, progress)
│   │   └── utils/        # Utilidades
│   ├── alembic/          # Migraciones de base de datos
│   ├── Dockerfile
│   ├── docker-compose.yml # Solo backend + n8n (sin PostgreSQL)
│   ├── requirements.txt
│   ├── .env.example
│   └── seed_catalog.py   # Script para importar catálogo de cartas
├── android/              # Aplicación Android (Kotlin + Jetpack Compose)
│   ├── app/
│   │   ├── build.gradle.kts
│   │   └── src/main/
│   │       ├── AndroidManifest.xml
│   │       ├── java/com/tcg/portfolio/
│   │       │   ├── MainActivity.kt
│   │       │   ├── data/
│   │       │   │   ├── model/
│   │       │   │   ├── network/
│   │       │   │   └── repository/
│   │       │   ├── navigation/
│   │       │   └── ui/
│   │       │       ├── screens/
│   │       │       └── theme/
│   │       └── res/
│   ├── build.gradle.kts
│   ├── gradle.properties
│   └── settings.gradle.kts
├── docs/                 # Documentación práctica
│   ├── postgresql-easypanel.md
│   ├── deployment-easypanel.md
│   └── n8n-workflow.md
├── .gitignore
└── README.md
```

## Endpoints de la API

| Método | Ruta | Descripción |
|--------|------|-------------|
| `GET` | `/health` | Verificación de estado |
| `POST` | `/scan` | Escanear una carta (multipart/form-data con imagen) |
| `GET` | `/sets` | Listar sets |
| `GET` | `/sets/{id}` | Detalle de un set |
| `GET` | `/sets/{id}/progress` | Progreso de un set |
| `GET` | `/cards` | Listar cartas del catálogo |
| `GET` | `/cards/{id}` | Detalle de una carta |
| `GET` | `/collection` | Listar colección del usuario |
| `POST` | `/collection` | Añadir carta a la colección |
| `PUT` | `/collection/{id}` | Actualizar entrada de colección |
| `DELETE` | `/collection/{id}` | Eliminar entrada de colección |

## Variables de entorno

```env
DATABASE_URL=postgresql://user:password@host:5432/pokemon_tcg
N8N_WEBHOOK_URL=http://n8n:5678/webhook/scan
N8N_API_KEY=
CORS_ORIGINS=http://localhost:3000,http://localhost:8080
```

## Inicio rápido (desarrollo local)

### 1. Instalar dependencias del backend

```bash
cd backend
pip install -r requirements.txt
```

### 2. Configurar variables de entorno

```bash
cp .env.example .env
# Editar .env con los valores reales
```

### 3. Ejecutar migraciones

```bash
alembic upgrade head
```

### 4. Ejecutar el servidor

```bash
uvicorn app.main:app --host 0.0.0.0 --port 8000
```

### 5. Importar el catálogo (opcional)

```bash
python seed_catalog.py < archivo_catalogo.json
```

## Base de datos

### Tablas

- **sets**: Catálogo de sets de cartas
- **cards**: Catálogo completo de cartas
- **collection**: Cartas que posee el usuario

### Relaciones

```
sets 1───N cards
cards 1───N collection
```

Una carta se considera que el usuario la tiene si existe una entrada en `collection`.
Una carta se considera que falta si pertenece a un set pero no existe en `collection`.

## Flujo de escaneo

1. Android envía la foto a `POST /scan`
2. FastAPI envía la imagen al webhook de n8n
3. n8n procesa la imagen con IA
4. n8n devuelve un JSON con la identificación
5. FastAPI valida la respuesta y busca la carta en el catálogo local
6. FastAPI devuelve el resultado a Android
7. El usuario confirma y se añade/actualiza `collection`

## Despliegue en EasyPanel

1. Crear PostgreSQL como servicio independiente en EasyPanel
2. Crear backend como servicio Docker en EasyPanel
3. Añadir `DATABASE_URL` y demás variables en las variables de entorno del backend
4. Ejecutar `alembic upgrade head`
5. Importar el catálogo
6. Configurar n8n con el webhook apuntando al backend

Ver `docs/` para guías detalladas de cada paso.

## Tecnologías

- **Backend**: Python, FastAPI, SQLAlchemy, Pydantic, Alembic
- **Base de datos**: PostgreSQL
- **Automatización**: n8n
- **Android**: Kotlin, Jetpack Compose, Retrofit, OkHttp
- **Infraestructura**: Docker, EasyPanel, VPS