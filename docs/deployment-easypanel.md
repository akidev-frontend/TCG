# Despliegue del Backend en EasyPanel

## Requisitos previos

1. PostgreSQL creado en EasyPanel (ver `docs/postgresql-easypanel.md`).
2. Backend compilado y listo para desplegar.
3. EasyPanel configurado con un proyecto Docker.

## Paso 1: Preparar el backend

### Verificar la configuración

Asegurarse de que `.env.example` tiene los valores correctos:

```env
DATABASE_URL=postgresql://pokemon_user:<contraseña>@<host>:5432/pokemon_tcg
N8N_WEBHOOK_URL=http://<n8n-host>:5678/webhook/scan
N8N_API_KEY=
CORS_ORIGINS=https://tu-dominio.com
```

### Construir la imagen Docker

```bash
cd backend
docker build -t tcg-backend .
```

### Probar localmente

```bash
docker run --rm \
  -e DATABASE_URL="postgresql://pokemon_user:<contraseña>@<host>:5432/pokemon_tcg" \
  -e N8N_WEBHOOK_URL="http://<n8n-host>:5678/webhook/scan" \
  -p 8000:8000 \
  tcg-backend
```

Verificar que `/health` responde:

```bash
curl http://localhost:8000/health
```

## Paso 2: Desplegar en EasyPanel

### Opción A: Desde EasyPanel (Docker)

1. Ir a **Services** → **Create Service**.
2. Seleccionar **Docker** o **Custom**.
3. Configurar:

| Campo | Valor |
|-------|-------|
| Nombre del servicio | `tcg-backend` |
| Imagen | `tcg-backend:latest` (o desde tu registry) |
| Puerto | `8000` |
| Variables de entorno | Ver tabla abajo |

### Variables de entorno en EasyPanel

```env
DATABASE_URL=postgresql://pokemon_user:<contraseña>@<host>:5432/pokemon_tcg
N8N_WEBHOOK_URL=http://<n8n-host>:5678/webhook/scan
N8N_API_KEY=
CORS_ORIGINS=https://tu-dominio.com
```

### Opción B: Usando docker-compose en EasyPanel

Si EasyPanel soporta docker-compose:

1. Crear un nuevo servicio de tipo **Docker Compose**.
2. Subir el `docker-compose.yml` del backend.
3. Configurar las variables de entorno en la interfaz de EasyPanel.

## Paso 3: Ejecutar migraciones

Después del despliegue, ejecutar las migraciones de Alembic:

### Desde EasyPanel (terminal del contenedor)

```bash
alembic upgrade head
```

### Desde el host (si tienes acceso SSH)

```bash
docker exec <nombre-contenedor> alembic upgrade head
```

### Verificar las tablas

```bash
docker exec <nombre-contenedor> python -c "
from app.database import engine
from app.models import Set, Card, Collection
print('Tablas creadas correctamente')
"
```

## Paso 4: Importar el catálogo de cartas

Una vez creadas las tablas, importar los datos del catálogo:

```bash
# Opción A: Desde el contenedor
docker exec -i <nombre-contenedor> python seed_catalog.py < /ruta/a/catalog_data.json

# Opción B: Localmente contra la BD remota
export DATABASE_URL="postgresql://pokemon_user:<contraseña>@<host>:5432/pokemon_tcg"
python backend/seed_catalog.py
```

## Paso 5: Verificar el despliegue

### Health check

```bash
curl https://tu-dominio.com/health
```

### Probar endpoints

```bash
# Listar sets
curl https://tu-dominio.com/sets

# Progreso de un set
curl https://tu-dominio.com/sets/1/progress
```

## Paso 6: Configurar n8n

El webhook de n8n debe apuntar a la URL pública del backend:

```
N8N_WEBHOOK_URL=https://tu-dominio.com/scan
```

## Estructura de archivos para despliegue

```
backend/
├── Dockerfile
├── docker-compose.yml
├── alembic.ini
├── alembic/
│   ├── env.py
│   ├── script.py.mako
│   └── versions/
│       └── 000000000001_initial.py
├── app/
│   ├── main.py
│   ├── config.py
│   ├── database.py
│   ├── models/
│   ├── schemas/
│   ├── routers/
│   ├── services/
│   └── utils/
├── requirements.txt
├── .env.example
└── seed_catalog.py    # (por crear)
```

## Solución de problemas

### Error de conexión a la base de datos

- Verificar que `DATABASE_URL` es correcta.
- Verificar que PostgreSQL está en ejecución.
- Verificar que el host es accesible desde el contenedor Docker.

### Error de migraciones

- Asegurarse de que `alembic.ini` está configurado correctamente.
- Verificar que `alembic/env.py` usa `DATABASE_URL` de `app.config`.

### Error de CORS

- Verificar que `CORS_ORIGINS` incluye el origen de la app Android/web.
- Reiniciar el contenedor tras cambiar las variables de entorno.