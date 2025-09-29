import os, sys, pandas as pd
from dateutil import parser
import psycopg2
from psycopg2.extras import execute_values

CSV_PATH = os.getenv("CSV_PATH", "/app/data/flights_sample.csv")
DB_HOST  = os.getenv("DB_HOST", "localhost")
DB_PORT  = int(os.getenv("DB_PORT", "5432"))
DB_NAME  = os.getenv("DB_NAME", "airline")
DB_USER  = os.getenv("DB_USER", "airuser")
DB_PASS  = os.getenv("DB_PASS", "airpass")

def normalize_row(row):
    try:
        row["departure_utc"] = parser.isoparse(row["departure_utc"]).strftime("%Y-%m-%d %H:%M:%S")
        row["arrival_utc"]   = parser.isoparse(row["arrival_utc"]).strftime("%Y-%m-%d %H:%M:%S")
    except Exception as e:
        row["_error"] = f"bad_timestamp:{e}"
    for col in ["flight_id","airline","origin","destination","status"]:
        if pd.isna(row.get(col)) or str(row[col]).strip()=="":
            row["_error"] = f"missing_required:{col}"
    return row

def validate_dataframe(df):
    errs=[]
    if df["flight_id"].duplicated().any(): errs.append("duplicate flight_id")
    for col in ["airline","origin","destination","departure_utc","arrival_utc","status"]:
        if df[col].isna().any(): errs.append(f"nulls in {col}")
    return errs

def to_parquet(df, path="/app/data/clean_flights.parquet"):
    df.to_parquet(path, index=False)

def load_to_db(df):
    rows = list(df[["flight_id","airline","origin","destination","departure_utc","arrival_utc","status"]].itertuples(index=False, name=None))
    conn = psycopg2.connect(host=DB_HOST, port=DB_PORT, dbname=DB_NAME, user=DB_USER, password=DB_PASS)
    try:
        with conn:
            with conn.cursor() as cur:
                execute_values(cur, """
                  INSERT INTO flights (flight_id, airline, origin, destination, departure_utc, arrival_utc, status)
                  VALUES %s
                  ON CONFLICT (flight_id) DO UPDATE
                    SET airline=EXCLUDED.airline,
                        origin=EXCLUDED.origin,
                        destination=EXCLUDED.destination,
                        departure_utc=EXCLUDED.departure_utc,
                        arrival_utc=EXCLUDED.arrival_utc,
                        status=EXCLUDED.status;
                """, rows)
    finally:
        conn.close()

def main():
    print(f"[ETL] Reading {CSV_PATH}")
    raw = pd.read_csv(CSV_PATH)
    cleaned = raw.apply(normalize_row, axis=1)
    bad = cleaned[cleaned.get("_error").notna()] if "_error" in cleaned.columns else pd.DataFrame()
    if not bad.empty:
        print(f"[ETL][WARN] {len(bad)} bad rows -> /app/data/bad_rows.csv")
        bad.to_csv("/app/data/bad_rows.csv", index=False)
        cleaned = cleaned[cleaned.get("_error").isna()]
    cleaned["flight_id"] = cleaned["flight_id"].astype(int)
    errs = validate_dataframe(cleaned)
    if errs:
        print(f"[ETL][ERROR] Validation failed: {errs}")
        sys.exit(2)
    to_parquet(cleaned)
    load_to_db(cleaned)
    print("[ETL] Success. Loaded rows:", len(cleaned))

if __name__ == "__main__":
    main()
