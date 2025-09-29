# Python API (Flask)

This folder contains a containerized REST API built with **Flask** to expose flight schedules.

## Purpose
- Query the `flights` table in Postgres.
- Filter by origin/destination.
- Provide lightweight health and status endpoints for monitoring.

## Contents (planned)
- `app.py` → Flask app with `/health` and `/flights`.
- `requirements.txt` → Flask dependencies.
- `Dockerfile` → container image for the API.

