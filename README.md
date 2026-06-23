# NAGP Kubernetes, DevOps and FinOps Assignment

## Repository

GitHub Repository:

```text
https://github.com/NRAJKU/NAGP-assignment-k8s
```

## Docker Image

Docker Hub Repository:

```text
https://hub.docker.com/r/nraj/nagp-assignment/
```

Image Tag:

```text
0.0.1 0.0.2 0.0.3
```

## Service API URL

Application Load Balancer URL:

```text
http://k8s-nagpassi-servicea-1df60cbb95-1917124674.ap-south-1.elb.amazonaws.com/api/customers
```

## Deployment Architecture

Internet
→ AWS Application Load Balancer
→ Kubernetes Ingress
→ Service API Service
→ Service API Deployment (4 Pods)
→ PostgreSQL Service
→ PostgreSQL StatefulSet
→ Amazon EBS Persistent Volume

## Kubernetes Resources

* Namespace
* ConfigMap
* Secret
* Deployment
* StatefulSet
* Service
* Ingress
* Horizontal Pod Autoscaler
* Network Policy
* Pod Disruption Budget

## Validation Commands

### List Resources

```bash
kubectl get all -n nagp-assignment
```

### Verify Ingress

```bash
kubectl get ingress -n nagp-assignment
```

### Verify configMap
```bash
kubectl get configmap service-api-config -n nagp-assignment
```

### Verify secret
```bash
kubectl get secret postgres-secret -n nagp-assignment
```

### Verify Persistence Volumes
```bash
kubectl get pvc -n nagp-assignment
kubectl get pv -n nagp-assignment
```

### Verify External Access to App
```bash
kubectl get ingress -n nagp-assignment
invoke-RestMethod http://k8s-nagpassi-servicea-1df60cbb95-1917124674.ap-south-1.elb.amazonaws.com/api/customers
```

### Verify Database Records
```bash
kubectl exec -it postgres-0 -n nagp-assignment -- psql -U appuser customerdb
```
```sql
select * from customers;
```

### Verify API Self Healing pod death/restarts
```bash
kubectl get pods -n nagp-assignment

kubectl delete pod <pod name>

kubectl get pods -w -n nagp-assignment
```

### Verify DB self healing and no data loss
```bash
kubectl delete pod postgres-0 -n nagp-assignment
kubectl get pods -w -n nagp-assignment
kubectl exec -it postgres-0 -n nagp-assignment -- psql -U appuser customerdb
```
```sql
select * from customers;
```

### Demonstrating Rolling updates
```bash
docker build -t nraj/nagp-assignment:0.0.5 .
docker push nraj/nagp-assignment:0.0.5

kubectl set image deployment/service-api service-api=nraj/nagp-assignment:0.0.5 -n nagp-assignment

kubectl rollout status deployment/service-api -n nagp-assignment
```

### Verify HPA
```bash
kubectl get hpa -n nagp-assignment
kubectl top pods -n nagp-assignment
```

## Screen Recording

Recording Link:

```text
https://nagarro-my.sharepoint.com/:v:/p/neeraj_kumar08/IQD6gXI8n8cMQrqy3ikVBbKPAfUdMFbem6eOonCXgxpQnTA?e=m2REF0
```

## Assignment Requirement Mapping
 - External API access through Ingress
 - Database internal only via ClusterIP
 - ConfigMap for database configuration
 - Secret for credentials
 - Rolling Updates
 - Self Healing
 - Persistent Storage
 - HPA
 - FinOps Resource Optimization