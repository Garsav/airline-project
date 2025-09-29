CREATE TABLE IF NOT EXISTS flights (
  flight_id     INTEGER PRIMARY KEY,
  airline       VARCHAR(16) NOT NULL,
  origin        VARCHAR(8)  NOT NULL,
  destination   VARCHAR(8)  NOT NULL,
  departure_utc TIMESTAMP   NOT NULL,
  arrival_utc   TIMESTAMP   NOT NULL,
  status        VARCHAR(16) NOT NULL,
  ingested_at   TIMESTAMP   NOT NULL DEFAULT NOW()
);
