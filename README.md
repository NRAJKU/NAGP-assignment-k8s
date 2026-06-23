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
→ Service API Deployment (2 Pods)
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

### Verify Persistence

```bash
kubectl delete pod postgres-0 -n nagp-assignment
```

### Verify API Self-Healing

```bash
kubectl delete pod <api-pod-name> -n nagp-assignment
```

### Verify HPA

```bash
kubectl get hpa -n nagp-assignment
```

### Verify Metrics

```bash
kubectl top pods -n nagp-assignment
```

## Screen Recording

Recording Link:

```text
<REPLACE_WITH_RECORDING_LINK>
```
