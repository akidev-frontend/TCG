"""Initial migration: create sets, cards, and collection tables

Revision ID: 000000000001
Revises: 
Create Date: 2026-10-02 12:00:00.000000

"""
from typing import Union

from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision = "000000000001"
down_revision = None
branch_labels = None
depends_on = None


def upgrade() -> None:
    op.create_table(
        "sets",
        sa.Column("id", sa.Integer, primary_key=True, autoincrement=True),
        sa.Column("name", sa.String, nullable=False),
        sa.Column("code", sa.String, unique=True, nullable=False),
        sa.Column("language", sa.String, nullable=False),
        sa.Column("series", sa.String),
        sa.Column("release_date", sa.Date),
        sa.Column("total_cards", sa.Integer, nullable=False),
    )

    op.create_table(
        "cards",
        sa.Column("id", sa.Integer, primary_key=True, autoincrement=True),
        sa.Column("set_id", sa.Integer, sa.ForeignKey("sets.id"), nullable=False),
        sa.Column("name", sa.String, nullable=False),
        sa.Column("number", sa.String, nullable=False),
        sa.Column("language", sa.String, nullable=False),
        sa.Column("rarity", sa.String),
        sa.Column("variant", sa.String),
    )

    op.create_table(
        "collection",
        sa.Column("id", sa.Integer, primary_key=True, autoincrement=True),
        sa.Column("card_id", sa.Integer, sa.ForeignKey("cards.id"), nullable=False),
        sa.Column("quantity", sa.Integer, nullable=False, server_default="1"),
        sa.Column("condition", sa.String),
        sa.Column("purchase_price", sa.Float),
        sa.Column("purchase_date", sa.Date),
        sa.Column("notes", sa.Text),
        sa.Column("created_at", sa.DateTime, server_default=sa.func.now()),
        sa.Column("updated_at", sa.DateTime, server_default=sa.func.now(), onupdate=sa.func.now()),
    )


def downgrade() -> None:
    op.drop_table("collection")
    op.drop_table("cards")
    op.drop_table("sets")