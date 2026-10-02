from fastapi import APIRouter, UploadFile, File, HTTPException
from app.services.scan_service import ScanService
from app.schemas.scan_schema import ScanResponse

router = APIRouter()


@router.post("/scan", response_model=ScanResponse)
async def scan_card(image: UploadFile = File(...)):
    image_bytes = await image.read()
    try:
        service = ScanService()
        result = await service.process_scan_with_image(image_bytes)
        return result
    except Exception as e:
        raise HTTPException(
            status_code=500,
            detail=str(e),
        )