import re


def validate_card_number(card_number: str) -> bool:
    pattern = r"^\d{1,4}/\d{1,4}$"
    return bool(re.match(pattern, card_number))


def validate_set_code(set_code: str) -> bool:
    pattern = r"^[A-Z0-9]{2,10}$"
    return bool(re.match(pattern, set_code))