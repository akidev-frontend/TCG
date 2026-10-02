from app.schemas.scan_schema import ScanRequest, ScanResponse
from app.services.n8n_client import N8nClient
from app.database import SessionLocal
from app.models.card_model import Card
from app.models.set_model import Set


class ScanService:
    def __init__(self):
        self.n8n = N8nClient()

    async def process_scan_with_image(self, image_bytes: bytes) -> ScanResponse:
        n8n_result = await self.n8n.send_image(image_bytes)
        if n8n_result is None:
            return ScanResponse(
                success=False,
                name="",
                set_name="",
                set_code="",
                card_number="",
                rarity=None,
                variant=None,
                message="Error al comunicarse con n8n",
            )

        validated = self._validate_n8n_response(n8n_result)
        if not validated:
            return ScanResponse(
                success=False,
                name="",
                set_name="",
                set_code="",
                card_number="",
                rarity=None,
                variant=None,
                message="Respuesta inválida de n8n",
            )

        card = self._find_card_in_catalog(validated)
        if card is None:
            return ScanResponse(
                success=False,
                name=validated.get("name", ""),
                set_name=validated.get("set", ""),
                set_code=validated.get("set_code", ""),
                card_number=validated.get("card_number", ""),
                rarity=validated.get("rarity"),
                variant=validated.get("variant"),
                message="Carta no encontrada en el catálogo",
            )

        return ScanResponse(
            success=True,
            card_id=card.id,
            name=card.name,
            set_name=card.set_.name if card.set_ else validated.get("set", ""),
            set_code=validated.get("set_code", ""),
            card_number=validated.get("card_number", ""),
            rarity=validated.get("rarity"),
            variant=validated.get("variant"),
            message=None,
        )

    def _validate_n8n_response(self, data: dict) -> dict | None:
        required_fields = ["name", "set", "set_code", "card_number"]
        for field in required_fields:
            if field not in data or not data[field]:
                return None
        return data

    def _find_card_in_catalog(self, n8n_data: dict) -> Card | None:
        db = SessionLocal()
        try:
            card = (
                db.query(Card)
                .join(Set, Card.set_id == Set.id)
                .filter(
                    Card.name.ilike(f"%{n8n_data['name']}%"),
                    Set.code == n8n_data.get("set_code"),
                )
                .first()
            )
            return card
        finally:
            db.close()