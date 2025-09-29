import os
from flask import Flask, jsonify, request
import psycopg2
from psycopg2.extras import RealDictCursor

DB_HOST=os.getenv("DB_HOST","localhost")
DB_PORT=int(os.getenv("DB_PORT","5432"))
DB_NAME=os.getenv("DB_NAME","airline")
DB_USER=os.getenv("DB_USER","airuser")
DB_PASS=os.getenv("DB_PASS","airpass")

app=Flask(__name__)
def conn():
    return psycopg2.connect(host=DB_HOST,port=DB_PORT,dbname=DB_NAME,user=DB_USER,password=DB_PASS)

@app.route("/health")
def health(): return {"status":"ok"}

@app.route("/flights")
def flights():
    origin=request.args.get("origin")
    dest=request.args.get("destination")
    q="SELECT flight_id,airline,origin,destination,departure_utc,arrival_utc,status FROM flights WHERE 1=1"
    p=[]
    if origin: q+=" AND origin=%s"; p.append(origin)
    if dest:   q+=" AND destination=%s"; p.append(dest)
    q+=" ORDER BY departure_utc ASC LIMIT 100"
    with conn() as c:
        with c.cursor(cursor_factory=RealDictCursor) as cur:
            cur.execute(q,p)
            return jsonify(cur.fetchall())

if __name__=="__main__":
    app.run(host="0.0.0.0",port=5000)
