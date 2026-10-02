from app.routers.health import router as health_router
from app.routers.scan import router as scan_router
from app.routers.sets import router as sets_router
from app.routers.cards import router as cards_router
from app.routers.collection import router as collection_router
from app.routers.progress import router as progress_router

__all__ = [
    "health_router",
    "scan_router",
    "sets_router",
    "cards_router",
    "collection_router",
    "progress_router",
]