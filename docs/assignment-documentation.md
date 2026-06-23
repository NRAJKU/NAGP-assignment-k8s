# Kubernetes, DevOps and FinOps Home Assignment

## 1. Requirement Understanding

The assignment required deploying a multi-tier application on Kubernetes while demonstrating the operational characteristics expected from a modern cloud-native workload.

From my perspective, the interesting part of the assignment was not simply getting containers running inside EKS, but showing how the platform behaves under normal operational events such as pod failures, rolling deployments, storage persistence and workload scaling.

The solution therefore focuses on four areas:

* Reliable deployment of a stateless API tier.
* Persistent management of a stateful PostgreSQL database.
* Secure and predictable communication between application and database layers.
* Cost-conscious infrastructure choices appropriate for a small workload.

The final implementation demonstrates:

* External access to the application through an AWS Application Load Balancer.
* Internal-only database access through Kubernetes services.
* Self-healing of both stateless and stateful workloads.
* Persistent storage surviving pod recreation.
* Rolling deployments.
* Horizontal Pod Autoscaling.
* Externalized configuration and secret management.
* Resource sizing aligned with the scale of the workload.

---

## 2. Assumptions

A few assumptions were made during implementation.

The solution is intended as a demonstration environment rather than a production deployment. Because of that, I prioritized simplicity and operational clarity over high-availability database architectures.

A single PostgreSQL instance was considered sufficient since database replication and disaster recovery were not part of the assignment requirements.

AWS was selected as the target cloud platform and Amazon EKS was used as the managed Kubernetes offering. Docker Hub was used as the image registry to keep the deployment process straightforward and reproducible.

The expected workload is relatively small. The API exposes a simple customer retrieval service and does not perform CPU-intensive processing, which allowed the cluster to be sized conservatively.

---

## 3. Solution Overview

The application consists of a Spring Boot 3.5 service built on WebFlux and a PostgreSQL 16 database.

One design choice worth calling out is the use of an end-to-end reactive stack. The API layer uses WebFlux together with R2DBC instead of traditional JDBC. While this application is small, the reactive model allows the service tier to handle concurrent requests efficiently without requiring a large thread pool. This aligns well with the FinOps goal of running on relatively small worker nodes.

Database schema management is handled through Flyway migrations. The application automatically initializes the schema and seed data during startup, ensuring a repeatable deployment process across environments.

The deployed architecture is intentionally simple:

```text
Internet
   |
AWS Application Load Balancer
   |
Kubernetes Ingress
   |
Service API Deployment
   |
PostgreSQL Service
   |
PostgreSQL StatefulSet
   |
Persistent Volume
```

From an infrastructure perspective, the solution uses:

* Amazon EKS as the Kubernetes control plane.
* AWS Load Balancer Controller for ingress management.
* PostgreSQL deployed as a StatefulSet.
* Persistent storage backed by Amazon EBS.
* Horizontal Pod Autoscaler for workload scaling.
* ConfigMaps and Secrets for runtime configuration.

A conscious decision was made to keep PostgreSQL internal to the cluster. Only the API tier is exposed externally. This follows a common production pattern where database access is restricted to trusted workloads rather than exposed directly.

---

## 4. Justification for the Resources Utilized

Resource sizing was driven primarily by workload characteristics and cost efficiency rather than maximizing performance.

### EKS Worker Nodes

The cluster runs on two `t3.small` worker nodes.

For a workload of this size, larger instances would provide little practical benefit while increasing infrastructure cost. The selected node size was sufficient to run:

* Kubernetes system components.
* AWS Load Balancer Controller.
* Metrics Server.
* PostgreSQL.
* Multiple API replicas.

During testing, the cluster was intentionally kept small enough to expose scheduling constraints and autoscaling behaviour, which provided useful validation of the deployment configuration.

### Service API Resources

The API tier uses relatively conservative resource requests and limits.

The service performs lightweight database operations and spends most of its time waiting on I/O rather than consuming CPU. Restricting the container footprint improves node utilization and allows more efficient pod placement.

Because the application uses WebFlux and R2DBC, it can sustain concurrent requests without requiring large thread pools or excessive memory allocation.

### PostgreSQL Resources

Although the database workload is modest, PostgreSQL behaves very differently from the API tier.

Unlike a stateless service, PostgreSQL benefits from memory availability for caching and query execution. For a production deployment I would allocate substantially more memory and separate the database onto dedicated infrastructure.

For this assignment, the selected resource profile provides enough capacity for schema migrations, seed data loading and application queries while remaining cost-effective.

### Persistent Storage

PostgreSQL storage is backed by an EBS-backed Persistent Volume Claim.

The objective was not raw storage performance but persistence. The important requirement was ensuring that customer data survives pod deletion and StatefulSet recreation events. This behavior was validated during testing by deleting the PostgreSQL pod and confirming that previously inserted data remained available after recovery.

### Autoscaling

Horizontal Pod Autoscaling was included primarily to demonstrate platform elasticity.

The API deployment scales based on CPU utilization and allows additional replicas to be created when demand increases. While the application workload is small, enabling HPA demonstrates how the deployment can adapt to changing traffic patterns without manual intervention.

---

## 5. Operational Validation

Rather than treating deployment as the finish line, I validated a number of common operational scenarios.

### API Tier Self-Healing

Service API pods were manually deleted to verify that the Deployment controller recreated healthy replacements automatically.

The application remained available throughout the recovery process.

### Database Recovery

The PostgreSQL pod was deleted and recreated through the StatefulSet controller.

The key validation point was ensuring that data remained intact after recovery. Because the database volume is externalized through a Persistent Volume Claim, data survived pod replacement as expected.

### Rolling Deployments

Application updates were deployed through Kubernetes rolling updates.

This verified that new application versions could be introduced without requiring a full service outage and also highlighted the impact of resource constraints when running on a small cluster.

### Database Initialization

Flyway migrations were validated by deploying against a clean database and confirming automatic creation of schema objects and seed data.

This ensures that new environments can be provisioned consistently without manual database setup.

### Horizontal Pod Autoscaler

The HPA configuration was validated using generated load. Additional replicas were created automatically once utilization thresholds were exceeded and scaled back down when demand decreased.

---

## 6. Conclusion

The final solution delivers a complete Kubernetes deployment of a multi-tier application on Amazon EKS while demonstrating the operational capabilities requested in the assignment.

Beyond simply deploying containers, the implementation validates persistence, self-healing, rolling updates, autoscaling and secure service-to-service communication. Resource choices were intentionally conservative to align with FinOps principles while still providing sufficient capacity for the workload.

If this system were evolving beyond a demonstration environment, the next areas I would address would be database high availability, observability expansion, automated CI/CD pipelines and stronger ingress security controls. For the scope of this assignment, however, the current implementation satisfies the functional and operational requirements while remaining intentionally simple and cost-conscious.
