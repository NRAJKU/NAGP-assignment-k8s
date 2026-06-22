# Kubernetes, DevOps and FinOps Home Assignment

## 1. Requirement Understanding

The objective of this assignment was to design, containerize and deploy a multi-tier application on Kubernetes consisting of a Service API tier and a Database tier.

The Service API exposes REST endpoints that retrieve data from a PostgreSQL database.

The solution addresses the following requirements:

* External access to the Service API tier
* Internal-only access to the Database tier
* Rolling updates for the Service API tier
* Self-healing for both tiers
* Horizontal Pod Autoscaling for the Service API tier
* Persistent storage for the Database tier
* Externalized configuration using ConfigMaps
* Secure handling of credentials using Secrets
* Resource optimization using Kubernetes metrics

## 2. Assumptions

The following assumptions were made during implementation:

* AWS was used as the cloud platform.
* The solution was deployed in the `ap-south-1` region.
* Amazon EKS was used as the Kubernetes platform.
* The cluster consisted of two worker nodes.
* AWS Load Balancer Controller was installed to support Ingress.
* Metrics Server was installed to support Horizontal Pod Autoscaler.
* Amazon EBS CSI Driver was available for dynamic volume provisioning.
* The workload was intended for demonstration purposes and not for production use.
* The database did not require high availability or replication.

## 3. Solution Overview

### 3.1 Technology Stack

| Component            | Technology                   |
| -------------------- | ---------------------------- |
| Cloud Platform       | AWS                          |
| Kubernetes Platform  | Amazon EKS                   |
| Programming Language | Java 17                      |
| Framework            | Spring Boot 3.5.x            |
| Web Framework        | Spring WebFlux               |
| Database Access      | Spring Data R2DBC            |
| Database             | PostgreSQL 16                |
| Database Migration   | Flyway                       |
| Metrics              | Micrometer and Prometheus    |
| Containerization     | Docker                       |
| Container Registry   | Docker Hub                   |
| Ingress              | AWS Load Balancer Controller |
| Storage              | Amazon EBS gp3               |

### 3.2 Architecture

```
┌───────────────────────────────────────────────────────────────────────────────┐
│                           AWS Cloud (ap-south-1)                              │
│                                                                               │
│  Internet                                                                     │
│      │                                                                        │
│      ▼                                                                        │
│  ┌───────────────────────────────┐                                            │
│  │ Application Load Balancer     │                                            │
│  └───────────────┬───────────────┘                                            │
│                  │                                                            │
│                  ▼                                                            │
│  ┌─────────────────────────────────────────────────────────────────────────┐  │
│  │                  Amazon EKS Cluster - nagp-assignment                   │  │
│  │                                                                         │  │
│  │  ┌───────────────────────────────┐                                      │  │
│  │  │ Ingress                       │                                      │  │
│  │  └───────────────┬───────────────┘                                      │  │
│  │                  │                                                      │  │
│  │                  ▼                                                      │  │
│  │  ┌───────────────────────────────┐                                      │  │
│  │  │ Service API Service           │                                      │  │
│  │  │ Type: ClusterIP               │                                      │  │
│  │  └───────────────┬───────────────┘                                      │  │
│  │                  │                                                      │  │
│  │                  ▼                                                      │  │
│  │  ┌───────────────────────────────────────────────────────────────────┐  │  │
│  │  │ Service API Deployment                                            │  │  │
│  │  │ Replicas: 4                                                       │  │  │
│  │  │                                                                   │  │  │
│  │  │  ┌────────┐  ┌────────┐  ┌────────┐  ┌────────┐                   │  │  │
│  │  │  │ Pod-1  │  │ Pod-2  │  │ Pod-3  │  │ Pod-4  │                   │  │  │
│  │  │  └────┬───┘  └────┬───┘  └────┬───┘  └────┬───┘                   │  │  │
│  │  └───────┼───────────┼───────────┼───────────┼───────────────────────┘  │  │
│  │          │           │           │           │                          │  │
│  │          └───────────┴───────────┴───────────┘                          │  │
│  │                              │                                          │  │
│  │                              ▼                                          │  │
│  │  ┌───────────────────────────────┐                                      │  │
│  │  │ PostgreSQL Service            │                                      │  │
│  │  │ Type: ClusterIP               │                                      │  │
│  │  └───────────────┬───────────────┘                                      │  │
│  │                  │                                                      │  │
│  │                  ▼                                                      │  │
│  │  ┌───────────────────────────────┐                                      │  │
│  │  │ PostgreSQL StatefulSet        │                                      │  │
│  │  │ Replicas: 1                   │                                      │  │
│  │  │                               │                                      │  │
│  │  │        ┌─────────────┐        │                                      │  │
│  │  │        │ postgres-0  │        │                                      │  │
│  │  │        └──────┬──────┘        │                                      │  │
│  │  └───────────────┼───────────────┘                                      │  │
│  │                  │                                                      │  │
│  │                  ▼                                                      │  │
│  │  ┌───────────────────────────────┐                                      │  │
│  │  │ Persistent Volume Claim       │                                      │  │
│  │  │ Storage: 5 GiB                │                                      │  │
│  │  └───────────────┬───────────────┘                                      │  │
│  └──────────────────┼──────────────────────────────────────────────────────┘  │
│                     │                                                         │
│                     ▼                                                         │
│           ┌───────────────────────────────┐                                   │
│           │ Amazon EBS Volume (gp3)       │                                   │
│           └───────────────────────────────┘                                   │
└───────────────────────────────────────────────────────────────────────────────┘
```

### 3.3 Service API Tier

The Service API tier was implemented using Spring Boot WebFlux with a reactive programming model.

The application exposes REST endpoints that retrieve customer data from PostgreSQL.

The implementation includes:

* Reactive database access using R2DBC
* Database connection pooling
* Health endpoints for Kubernetes probes
* Prometheus metrics
* Graceful shutdown support
* Externalized configuration using ConfigMaps and Secrets

### 3.4 Database Tier

The database tier uses PostgreSQL deployed as a StatefulSet.

The implementation includes:

* Persistent storage using Amazon EBS
* Automatic recovery after pod deletion
* Internal-only access through a ClusterIP service
* Database schema management using Flyway
* Seed data managed through versioned migrations

### 3.5 Kubernetes Resources

| Resource                | Purpose                         |
| ----------------------- | ------------------------------- |
| Namespace               | Resource isolation              |
| ConfigMap               | Non-sensitive configuration     |
| Secret                  | Database credentials            |
| Deployment              | Service API management          |
| StatefulSet             | Database management             |
| Service                 | Service discovery               |
| Ingress                 | External access                 |
| HorizontalPodAutoscaler | Automatic scaling               |
| NetworkPolicy           | Database access restriction     |
| PodDisruptionBudget     | Availability during maintenance |

## 4. Requirement Traceability

| Requirement                       | Implementation                             |
| --------------------------------- | ------------------------------------------ |
| External API access               | Ingress with AWS Application Load Balancer |
| Internal database access          | ClusterIP Service and NetworkPolicy        |
| Four API pods                     | Deployment replicas                        |
| One database pod                  | StatefulSet replicas                       |
| Rolling updates                   | Deployment strategy                        |
| Self-healing                      | Deployment and StatefulSet controllers     |
| Persistent storage                | EBS-backed Persistent Volume               |
| Externalized configuration        | ConfigMap                                  |
| Secure password management        | Secret                                     |
| Service discovery without Pod IPs | Kubernetes Services                        |
| Horizontal Pod Autoscaling        | HPA                                        |
| CPU and memory limits             | Resource requests and limits               |
| Database seed data                | Flyway migrations                          |

## 5. Resource Justification

### 5.1 Compute Resources

The Service API tier was configured with the following resources:

| Resource | Request | Limit |
| -------- | ------- | ----- |
| CPU      | 100m    | 500m  |
| Memory   | 256Mi   | 512Mi |

These values provided sufficient capacity for the expected workload while preventing resource overconsumption.

### 5.2 Storage

A 5 GiB Amazon EBS gp3 volume was allocated for PostgreSQL persistence.

The gp3 storage class was selected because it provides lower cost and independent scaling of storage and performance.

### 5.3 Node Configuration

The EKS cluster used two `t3.small` worker nodes to support:

* Four API pods
* One database pod
* Kubernetes system components

### 5.4 FinOps Considerations

The following cost optimization measures were implemented:

1. Resource requests and limits were configured to avoid over-provisioning.
2. Horizontal Pod Autoscaler was used to scale the Service API tier based on demand.
3. Amazon EBS gp3 volumes were used instead of gp2.
4. Small worker node instances were selected for the demonstration environment.
5. The cluster was deleted after validation to avoid unnecessary costs.

### 5.5 Resource Optimization Based on Metrics

Resource utilization was monitored using Kubernetes metrics.

Commands used:

```bash
kubectl top pods -n nagp-assignment
kubectl top nodes
```

Based on observed utilization, CPU and memory requests were adjusted to align with actual workload requirements.

Include the observed values collected during testing in the table below.

| Resource | Initial Request | Observed Usage | Optimized Request |
| -------- | --------------- | -------------- | ----------------- |
| CPU      | 100m            |                |                   |
| Memory   | 256Mi           |                |                   |

## 6. Deployment Steps

1. Build the application.

```bash
./gradlew clean build
```

2. Build the Docker image.

```bash
docker build -t <dockerhub-user>/nagp-assignment:1.0.0 .
```

3. Push the image to Docker Hub.

```bash
docker push <dockerhub-user>/nagp-assignment:1.0.0
```

4. Deploy Kubernetes resources.

```bash
kubectl apply -f k8s/
```

5. Verify deployment status.

```bash
kubectl get all -n nagp-assignment
```

6. Verify API access.

```bash
curl https://<ingress-url>/api/customers
```

## 7. Validation

The following scenarios were verified:

* API access through Ingress
* Database connectivity
* Rolling updates
* API pod self-healing
* Database pod self-healing
* Data persistence after database pod recreation
* Horizontal Pod Autoscaler functionality

## 8. Deliverables

The repository includes:

* Application source code
* Dockerfile
* Kubernetes manifests
* Flyway migrations
* README
* Assignment documentation

The following items are provided separately:

* Git repository URL
* Docker Hub image URL
* Service API endpoint
* Screen recording demonstrating deployment, self-healing, persistence, rolling updates and FinOps considerations
  }
