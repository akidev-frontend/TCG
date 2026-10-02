# Configuración de PostgreSQL en EasyPanel

## Paso 1: Crear la base de datos

1. Iniciar sesión en EasyPanel.
2. Ir a **Services** → **Create Service**.
3. Seleccionar **PostgreSQL** en la lista de servicios disponibles.
4. Configurar los siguientes valores:

| Campo | Valor |
|-------|-------|
| Nombre del servicio | `pokemon-tcg-db` |
| Versión de PostgreSQL | `16` (o superior) |
| Usuario | `pokemon_user` |
| Contraseña | `<Contraseña>` |
| Base de datos | `pokemon_tcg` |
| Puerto | `5432` |
| Memoria | `256Mi` (mínimo recomendado) |

5. Pulsar **Create** o **Deploy**.
6. Esperar a que el servicio esté en estado **Running**.

## Paso 2: Obtener las credenciales

Una vez creado el servicio, EasyPanel proporciona las credenciales en la pestaña **Environment Variables** del servicio.

La `DATABASE_URL` tendrá el formato:

```
postgresql://pokemon_user:<contraseña>@<host>:5432/pokemon_tcg
```

Donde:
- `<contraseña>` es la contraseña que definiste en el paso 1.
- `<host>` es el hostname o IP proporcionado por EasyPanel (no `localhost`).
- El puerto es `5432` por defecto.

**Copiar esta URL completa** — se usará como valor de `DATABASE_URL`.

## Paso 3: Configurar el backend

En el servicio de backend en EasyPanel, añadir las siguientes variables de entorno:

```env
DATABASE_URL=postgresql://pokemon_user:<contraseña>@<host>:5432/pokemon_tcg
N8N_WEBHOOK_URL=http://<n8n-host>:5678/webhook/scan
N8N_API_KEY=
CORS_ORIGINS=https://tu-dominio.com
```

### Notas sobre las variables

| Variable | Descripción |
|----------|-------------|
| `DATABASE_URL` | URL de conexión a PostgreSQL (obtenida en el paso 2) |
| `N8N_WEBHOOK_URL` | URL pública del webhook de n8n |
| `N8N_API_KEY` | Clave API de n8n (si está habilitada) |
| `CORS_ORIGINS` | Orígenes permitidos para CORS (separados por coma) |

## Paso 4: Ejecutar las migraciones

Después de desplegar el backend, ejecutar las migraciones de Alembic para crear las tablas.

### Opción A: Desde EasyPanel (recomendado)

Si EasyPanel permite ejecutar comandos en el contenedor:

1. Ir al servicio de backend en EasyPanel.
2. Buscar la opción de **Execute Command** o **Terminal**.
3. Ejecutar:

```bash
alembic upgrade head
```

### Opción B: Localmente (antes del despliegue)

```bash
cd backend
export DATABASE_URL="postgresql://pokemon_user:<contraseña>@<host>:5432/pokemon_tcg"
alembic upgrade head
```

### Opción C: Dentro de un contenedor Docker temporal

```bash
docker run --rm \
  -e DATABASE_URL="postgresql://pokemon_user:<contraseña>@<host>:5432/pokemon_tcg" \
  -v $(pwd)/backend:/app \
  -w /app \
  tcg-backend \
  alembic upgrade head
```

## Paso 5: Importar el catálogo de cartas

Una vez creadas las tablas, hay que poblarlas con los datos del catálogo de cartas Pokémon.

### Opción A: Script de seed

Crear un script `backend/seed_catalog.py` que lea la base de datos descargable de cartas y inserte los datos en `sets` y `cards`.

### Opción B: Importación manual con psql

```bash
psql -h <host> -U pokemon_user -d pokemon_tcg -f catalog_data.sql
```

## Paso 6: Verificar la conexión

Desde el backend, verificar que todo funciona:

```bash
curl http://localhost:8000/health
```

Respuesta esperada:

```json
{"status": "ok"}
```

## Paso 7: Verificar las tablas

```bash
psql -h <host> -U pokemon_user -d pokemon_tcg -c "\dt"
```

Debería mostrar:

```
         List of relations
 Schema |    Name    | Type  |  Owner
--------+------------+-------+----------
 public | cards      | table | pokemon_user
 public | collection | table | pokemon_user
 public | sets       | table | pokemon_user
```

## Estructura de la base de datos

### Tabla `sets`

| Columna | Tipo | Descripción |
|---------|------|-------------|
| `id` | SERIAL | Clave primaria |
| `name` | VARCHAR | Nombre del set |
| `code` | VARCHAR | Código del set (único) |
| `language` | VARCHAR | Idioma del set |
| `series` | VARCHAR | Serie (ej: Scarlet & Violet) |
| `release_date` | DATE | Fecha de lanzamiento |
| `total_cards` | INTEGER | Número total de cartas |

### Tabla `cards`

| Columna | Tipo | Descripción |
|---------|------|-------------|
| `id` | SERIAL | Clave primaria |
| `set_id` | INTEGER | FK → sets.id |
| `name` | VARCHAR | Nombre de la carta |
| `number` | VARCHAR | Número de carta en el set |
| `language` | VARCHAR | Idioma de la carta |
| `rarity` | VARCHAR | Rareza |
| `variant` | VARCHAR | Variante (opcional) |

### Tabla `collection`

| Columna | Tipo | Descripción |
|---------|------|-------------|
| `id` | SERIAL | Clave primaria |
| `card_id` | INTEGER | FK → cards.id |
| `quantity` | INTEGER | Cantidad de copias |
| `condition` | VARCHAR | Condición (Near Mint, etc.) |
| `purchase_price` | FLOAT | Precio de compra |
| `purchase_date` | DATE | Fecha de compra |
| `notes` | TEXT | Notas adicionales |
| `created_at` | TIMESTAMP | Fecha de creación |
| `updated_at` | TIMESTAMP | Fecha de última actualización |

## Notas importantes

- PostgreSQL **NO** se incluye en `docker-compose.yml`.
- PostgreSQL **NO** se ejecuta dentro del Docker del backend.
- Las credenciales **nunca** deben estar en el código fuente.
- Usar siempre variables de entorno para la configuración de conexión.
- Las migraciones de Alembic se ejecutan después del despliegue.
- El catálogo de cartas se importa como paso independiente.
