# Kubernetes Manifests

This folder contains YAML manifests for deploying the Airline Capstone components to Kubernetes or OpenShift.

## Purpose
- Deploy and configure Postgres database.
- Deploy Python API and Spring API.
- Configure Services (NodePort/ClusterIP).
- Add health probes for monitoring.
- Define a CronJob for nightly ETL runs.

## Contents (planned)
- `postgres-deployment.yaml`
- `api-python.yaml`
- `api-spring.yaml`
- `etl-cronjob.yaml`
- `configmap.yaml` and `secret.yaml`

