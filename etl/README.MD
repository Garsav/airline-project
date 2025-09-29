# ETL (Extract, Transform, Load)

This folder contains the Python ETL pipeline for the Airline Capstone project.

## Purpose
- Ingest raw provider flight schedules (CSV/JSON).
- Validate and clean data (timestamps, required fields).
- Convert to Parquet for efficient storage.
- Load into Postgres (UPSERT logic for deduplication).

## Contents (planned)
- `etl.py` → main ETL script.
- `schema.sql` → defines the `flights` table.
- `requirements.txt` → Python dependencies.
- `Dockerfile` → containerized ETL image.
