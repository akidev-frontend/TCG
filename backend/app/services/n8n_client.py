import httpx
from app.config import N8N_WEBHOOK_URL, N8N_API_KEY, SCAN_TIMEOUT


class N8nClient:
    def __init__(self):
        self.webhook_url = N8N_WEBHOOK_URL
        self.api_key = N8N_API_KEY
        self.timeout = SCAN_TIMEOUT

    async def send_image(self, image_bytes: bytes) -> dict | None:
        headers = {}
        if self.api_key:
            headers["Authorization"] = f"Bearer {self.api_key}"

        files = {"image": ("scan.jpg", image_bytes, "image/jpeg")}

        async with httpx.AsyncClient(timeout=self.timeout) as client:
            try:
                response = await client.post(
                    self.webhook_url,
                    headers=headers,
                    files=files,
                )
                response.raise_for_status()
                return response.json()
            except httpx.TimeoutException:
                return None
            except httpx.HTTPStatusError:
                return None
            except httpx.RequestError:
                return None