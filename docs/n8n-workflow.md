# Workflow de n8n para escaneo de cartas Pokémon

## Flujo del workflow

```
Webhook (POST /scan)
  → Obtener imagen del request
  → Enviar imagen a modelo de IA (Gemini/OpenAI)
  → Recibir JSON con identificación
  → Consultar PostgreSQL para validar/corregir
  → Devolver JSON estructurado a FastAPI
```

## Nodo 1: Webhook

- **Tipo**: Webhook
- **Método**: POST
- **Path**: `/webhook/scan`
- **Content-Type**: `multipart/form-data`

Este nodo recibe la imagen enviada por FastAPI.

### Configuración

| Campo | Valor |
|-------|-------|
| HTTP Method | POST |
| Path | scan |
| Response Mode | Using 'Respond to Webhook' node |

## Nodo 2: Obtener imagen

- **Tipo**: Code (JavaScript)
- Extrae el archivo de imagen del request

### Código

```javascript
const items = $input.all();
const imageBuffer = items[0].binary.image.data;
const imageBase64 = Buffer.from(imageBuffer, 'base64').toString('base64');

return [{ json: { image_base64: imageBase64 } }];
```

## Nodo 3: Llamada a IA (Gemini)

- **Tipo**: HTTP Request
- **Método**: POST
- **URL**: `https://generativelanguage.googleapis.com/v1/models/gemini-2.0-flash:generateContent?key={{$env.GEMINI_API_KEY}}`

### Headers

```
Content-Type: application/json
```

### Body (JSON)

```json
{
  "contents": [
    {
      "parts": [
        {
          "inline_data": {
            "mime_type": "image/jpeg",
            "data": "{{ $json.image_base64 }}"
          }
        },
        {
          "text": "Identify this Pokémon card. Extract the following fields and return ONLY a valid JSON object with these exact keys: name, set, set_code, card_number, rarity, variant, language. The card_number format should be like '201/165'. If a field is not found or not applicable, use null. Do not include any text outside the JSON."
        }
      ]
    }
  ],
  "generationConfig": {
    "temperature": 0.1,
    "responseMimeType": "application/json"
  }
}
```

## Nodo 4: Parsear respuesta de IA

- **Tipo**: Code (JavaScript)
- Extrae el JSON de la respuesta de la IA

### Código

```javascript
const response = items[0].json;
const candidates = response.candidates;
if (!candidates || candidates.length === 0) {
  return [{ json: { error: 'No response from AI' } }];
}

const parts = candidates[0].content.parts;
const text = parts[0].text;

let parsed;
try {
  parsed = JSON.parse(text);
} catch (e) {
  return [{ json: { error: 'Invalid JSON from AI', raw: text } }];
}

return [{ json: parsed }];
```

## Nodo 5: Consultar PostgreSQL - Set

- **Tipo**: PostgreSQL
- **Operation**: Execute Query

### Query

```sql
SELECT id, name, code, total_cards
FROM sets
WHERE code = '{{ $json.set_code }}'
  AND language = '{{ $json.language }}'
LIMIT 1;
```

## Nodo 6: Consultar PostgreSQL - Card

- **Tipo**: PostgreSQL
- **Operation**: Execute Query

### Query

```sql
SELECT id, name, number, rarity, variant
FROM cards
WHERE set_id = (SELECT id FROM sets WHERE code = '{{ $json.set_code }}')
  AND name ILIKE '%{{ $json.name }}%'
  AND number = '{{ $json.card_number }}'
LIMIT 1;
```

## Nodo 7: Construir respuesta final

- **Tipo**: Code (JavaScript)
- Combina los datos de la IA con los del catálogo local

### Código

```javascript
const aiData = items[3].json;
const setData = items[4].json;
const cardData = items[5].json;

if (!setData || setData.length === 0) {
  return [{ json: {
    success: false,
    name: aiData.name || '',
    set_name: aiData.set || '',
    set_code: aiData.set_code || '',
    card_number: aiData.card_number || '',
    rarity: aiData.rarity,
    variant: aiData.variant,
    message: 'Set no encontrado en el catálogo'
  }}];
}

if (!cardData || cardData.length === 0) {
  return [{ json: {
    success: false,
    name: aiData.name || '',
    set_name: setData[0].name,
    set_code: aiData.set_code || '',
    card_number: aiData.card_number || '',
    rarity: aiData.rarity,
    variant: aiData.variant,
    message: 'Carta no encontrada en el catálogo'
  }}];
}

return [{ json: {
  success: true,
  card_id: cardData[0].id,
  name: cardData[0].name,
  set_name: setData[0].name,
  set_code: aiData.set_code || '',
  card_number: aiData.card_number || '',
  rarity: aiData.rarity || cardData[0].rarity,
  variant: aiData.variant || cardData[0].variant,
  message: null
}}];
```

## Nodo 8: Respond to Webhook

- **Tipo**: Respond to Webhook
- **Response Body**: Output del nodo 7
- **Response Code**: 200

## Variables de entorno necesarias en n8n

```env
GEMINI_API_KEY=your-gemini-api-key
DATABASE_URL=postgresql://user:password@host:5432/pokemon_tcg
```

## Estructura de respuesta esperada

### Respuesta exitosa

```json
{
  "success": true,
  "card_id": 42,
  "name": "Charizard",
  "set_name": "Pokémon Card 151",
  "set_code": "SV2A",
  "card_number": "201/165",
  "rarity": "Special Art Rare",
  "variant": null,
  "message": null
}
```

### Respuesta con error

```json
{
  "success": false,
  "name": "",
  "set_name": "",
  "set_code": "",
  "card_number": "",
  "rarity": null,
  "variant": null,
  "message": "Carta no encontrada en el catálogo"
}
```

## Notas importantes

- La IA **NO** debe contener prompts duros en el código de FastAPI.
- n8n es responsable de toda la lógica de IA.
- FastAPI solo valida la respuesta y busca en el catálogo local.
- El catálogo local es la fuente de verdad para la identificación.
- Si la IA identifica mal la carta, el catálogo local la corregirá o indicará que no existe.