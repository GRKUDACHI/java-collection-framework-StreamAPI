# ☁️ AWS Services - Quick Reference

## 📋 Core Concepts

### AWS Basics
- **Region**: Geographic area (e.g., us-east-1, eu-west-1)
- **Availability Zone (AZ)**: Data center within region
- **VPC**: Virtual Private Cloud (isolated network)
- **Pay-as-you-go**: Pay only for what you use

---

## 🖥️ EC2 (Elastic Compute Cloud)

### What is EC2?
Virtual servers in the cloud - rent compute capacity

### Instance Types
- **t2/t3**: General purpose (free tier: t2.micro)
- **m5/m6i**: General purpose (balanced)
- **c5/c6i**: Compute optimized (high CPU)
- **r5/r6i**: Memory optimized (high RAM)
- **i3**: Storage optimized (high IOPS)

### Instance States
- **Running**: Active
- **Stopped**: Shut down (EBS persists)
- **Terminated**: Deleted permanently
- **Pending**: Launching

### Pricing Models
- **On-Demand**: Pay per hour, no commitment
- **Reserved**: 1-3 years, up to 75% discount
- **Spot**: Bid on unused capacity, up to 90% discount
- **Savings Plans**: Flexible commitment

### Key Features
- **Security Groups**: Virtual firewall
- **Key Pairs**: SSH access
- **Elastic IP**: Static IPv4 address
- **Auto Scaling**: Automatic scaling

### Quick Commands
```bash
# Launch instance
aws ec2 run-instances --image-id ami-xxx --instance-type t2.micro

# List instances
aws ec2 describe-instances

# Stop instance
aws ec2 stop-instances --instance-ids i-xxx

# Terminate instance
aws ec2 terminate-instances --instance-ids i-xxx
```

---

## 🔐 IAM (Identity and Access Management)

### What is IAM?
Manages users, groups, roles, and permissions

### Core Components
- **Users**: Individual accounts
- **Groups**: Collection of users
- **Roles**: Temporary credentials
- **Policies**: JSON permission documents

### Policy Structure
```json
{
  "Effect": "Allow",
  "Action": "s3:GetObject",
  "Resource": "arn:aws:s3:::bucket/*"
}
```

### Best Practices
- ✅ Use roles, not users (for services)
- ✅ Principle of least privilege
- ✅ Enable MFA
- ✅ Rotate credentials regularly
- ❌ Don't store access keys in code

### Quick Commands
```bash
# Create user
aws iam create-user --user-name developer

# Attach policy
aws iam attach-user-policy --user-name developer --policy-arn arn:aws:iam::aws:policy/AmazonS3ReadOnlyAccess

# Create role
aws iam create-role --role-name ec2-role --assume-role-policy-document file://trust-policy.json
```

---

## 💾 EBS (Elastic Block Store)

### What is EBS?
Persistent block storage for EC2 (virtual hard drive)

### Volume Types
- **gp3**: General purpose SSD (recommended, cost-effective)
- **gp2**: General purpose SSD (legacy)
- **io1/io2**: Provisioned IOPS (high performance)
- **st1**: Throughput optimized HDD
- **sc1**: Cold HDD (lowest cost)

### Key Features
- **Snapshots**: Point-in-time backups (incremental)
- **Encryption**: At rest and in transit
- **Multi-Attach**: Attach to multiple instances (io1/io2)

### EBS vs Instance Store
| Feature | EBS | Instance Store |
|---------|-----|----------------|
| Persistence | ✅ Yes | ❌ No (ephemeral) |
| Performance | Good | Very High |
| Cost | Additional | Included |

### Quick Commands
```bash
# Create volume
aws ec2 create-volume --volume-type gp3 --size 100 --availability-zone us-east-1a

# Attach volume
aws ec2 attach-volume --volume-id vol-xxx --instance-id i-xxx --device /dev/sdf

# Create snapshot
aws ec2 create-snapshot --volume-id vol-xxx --description "Backup"
```

---

## ⚖️ ELB (Elastic Load Balancer)

### What is ELB?
Distributes traffic across multiple targets

### Types
- **ALB** (Application LB): Layer 7 (HTTP/HTTPS), web apps
- **NLB** (Network LB): Layer 4 (TCP/UDP), high performance
- **CLB** (Classic LB): Legacy (deprecated)
- **GWLB** (Gateway LB): Layer 3, virtual appliances

### Components
- **Target Groups**: Group of targets (EC2, IPs, Lambda)
- **Listeners**: Protocol and port configuration
- **Health Checks**: Monitor target health

### Features
- High availability (multi-AZ)
- Auto Scaling integration
- SSL/TLS termination
- Sticky sessions
- Connection draining

### Selection Guide
- **Web Apps** → ALB
- **High Performance** → NLB
- **Legacy** → Migrate to ALB/NLB

### Quick Commands
```bash
# Create ALB
aws elbv2 create-load-balancer --name my-alb --subnets subnet-xxx --security-groups sg-xxx

# Create target group
aws elbv2 create-target-group --name my-targets --protocol HTTP --port 80 --vpc-id vpc-xxx

# Register targets
aws elbv2 register-targets --target-group-arn arn:xxx --targets Id=i-xxx
```

---

## 🖼️ AMIs (Amazon Machine Images)

### What is an AMI?
Template for launching EC2 instances (OS + apps + config)

### Types
- **Public**: Available to everyone (AWS, community)
- **Private**: Only your account
- **Shared**: Shared with specific accounts

### AMI Lifecycle
1. Launch instance from base AMI
2. Configure and install software
3. Create custom AMI
4. Launch new instances from custom AMI

### AMI vs Snapshot
- **AMI**: Complete template (OS + data + config)
- **Snapshot**: Backup of EBS volume (just data)
- **AMI includes snapshots**

### Best Practices
- Keep AMIs updated
- Use Golden AMIs (pre-configured)
- Tag AMIs
- Deregister unused AMIs

### Quick Commands
```bash
# Create AMI
aws ec2 create-image --instance-id i-xxx --name "MyServer-2024"

# Launch from AMI
aws ec2 run-instances --image-id ami-xxx --instance-type t2.micro

# List AMIs
aws ec2 describe-images --owners self
```

---

## 🪣 S3 (Simple Storage Service)

### What is S3?
Object storage service (massive cloud hard drive)

### Key Concepts
- **Buckets**: Containers (globally unique name)
- **Objects**: Files (key + value + metadata)
- **Keys**: Object path/name

### Storage Classes
- **Standard**: Frequently accessed (highest cost)
- **Standard-IA**: Infrequently accessed
- **Intelligent-Tiering**: Auto-optimization
- **Glacier Instant**: Archive, immediate access
- **Glacier Flexible**: Archive, 3-5 hour retrieval
- **Deep Archive**: Long-term, 12 hour retrieval (lowest cost)

### Features
- **Versioning**: Multiple versions of objects
- **Lifecycle Policies**: Auto-transition between classes
- **Encryption**: SSE-S3, SSE-KMS, SSE-C
- **Static Website Hosting**: Host websites
- **Access Control**: Bucket policies, IAM, ACLs

### S3 vs EBS
| Feature | S3 | EBS |
|---------|-----|-----|
| Type | Object storage | Block storage |
| Capacity | Unlimited | Up to 64 TiB |
| Access | API | File system |
| Durability | 99.999999999% | 99.8-99.9% |

### Quick Commands
```bash
# Create bucket
aws s3 mb s3://my-bucket-name

# Upload file
aws s3 cp file.txt s3://my-bucket/folder/file.txt

# Download file
aws s3 cp s3://my-bucket/folder/file.txt ./file.txt

# Sync directory
aws s3 sync ./local s3://my-bucket/remote

# Enable versioning
aws s3api put-bucket-versioning --bucket my-bucket --versioning-configuration Status=Enabled
```

---

## 🔄 CI/CD Pipelines

### What is CI/CD?
- **CI**: Continuous Integration (build & test)
- **CD**: Continuous Deployment (deploy automatically)

### AWS Services
- **CodePipeline**: Orchestrates release process
- **CodeBuild**: Builds and tests code
- **CodeDeploy**: Deploys to environments
- **CodeCommit**: Private Git repository

### Pipeline Flow
```
Source (CodeCommit/GitHub)
    ↓
Build (CodeBuild)
    ↓
Deploy (CodeDeploy)
    ↓
Production
```

### Best Practices
- Automate everything
- Use Infrastructure as Code
- Test in staging first
- Blue/green deployments
- Monitor deployments

### Alternative Tools
- Jenkins
- GitHub Actions
- GitLab CI/CD
- CircleCI

---

## ✅ Best Practices Summary

### Security
- ✅ Use IAM roles (not users for services)
- ✅ Enable MFA
- ✅ Encrypt data (at rest & in transit)
- ✅ Use VPC
- ✅ Principle of least privilege
- ✅ Regular updates

### Cost Optimization
- ✅ Right-size instances
- ✅ Use Reserved Instances (predictable workloads)
- ✅ Use Spot Instances (fault-tolerant)
- ✅ Lifecycle policies (S3)
- ✅ Delete unused resources
- ✅ Monitor costs

### High Availability
- ✅ Multi-AZ deployment
- ✅ Load balancers
- ✅ Auto Scaling
- ✅ Regular backups
- ✅ Disaster recovery plan

### Performance
- ✅ Use CDN (CloudFront)
- ✅ Caching (ElastiCache)
- ✅ Database optimization
- ✅ Connection pooling
- ✅ Monitoring (CloudWatch)

---

## 🎤 Top Interview Questions

### EC2
1. **What is EC2?** Virtual servers in the cloud
2. **Stop vs Terminate?** Stop = shut down (persists), Terminate = delete
3. **What are Security Groups?** Virtual firewall for EC2
4. **Pricing models?** On-Demand, Reserved, Spot, Savings Plans
5. **Instance Store vs EBS?** Instance Store = ephemeral, EBS = persistent

### IAM
6. **What is IAM?** Manages users, groups, roles, permissions
7. **Users vs Roles?** Users = permanent, Roles = temporary
8. **Principle of least privilege?** Grant minimum required permissions
9. **IAM Policy components?** Effect, Action, Resource, Condition
10. **Why roles for EC2?** Temporary credentials, more secure

### EBS
11. **What is EBS?** Persistent block storage
12. **Volume types?** gp3, gp2, io1/io2, st1, sc1
13. **Snapshots vs AMIs?** Snapshot = data backup, AMI = complete template
14. **Multi-attach?** Attach to multiple instances (io1/io2 only)
15. **What happens on termination?** Root volume deleted by default

### ELB
16. **What is Load Balancer?** Distributes traffic
17. **ALB vs NLB?** ALB = Layer 7, NLB = Layer 4
18. **What is Target Group?** Group of targets
19. **Health checks?** Monitor target health
20. **Connection draining?** Gracefully remove instances

### AMI
21. **What is AMI?** Template for EC2 instances
22. **How to create?** Configure instance → Create AMI
23. **Golden AMI?** Pre-configured standard AMI
24. **Share AMIs?** Yes, with specific accounts
25. **Public vs Private?** Public = everyone, Private = your account

### S3
26. **What is S3?** Object storage service
27. **Storage classes?** Standard, Standard-IA, Glacier, Deep Archive
28. **Versioning?** Multiple versions of objects
29. **Lifecycle policies?** Auto-transition between classes
30. **S3 vs EBS?** S3 = object storage, EBS = block storage

### CI/CD
31. **What is CI/CD?** Continuous Integration & Deployment
32. **CodePipeline?** Orchestrates release process
33. **CI vs CD?** CI = build/test, CD = deploy
34. **Blue/green deployment?** Two environments, zero downtime
35. **Rollbacks?** Keep previous version, reversible migrations

### General
36. **Region vs AZ?** Region = geographic area, AZ = data center
37. **What is VPC?** Virtual Private Cloud
38. **Shared responsibility?** AWS = security OF cloud, Customer = security IN cloud
39. **CloudWatch?** Monitoring and observability
40. **High availability?** Multi-AZ, load balancers, auto scaling

---

## 📚 Quick Commands Cheat Sheet

### EC2
```bash
aws ec2 run-instances --image-id ami-xxx --instance-type t2.micro
aws ec2 describe-instances
aws ec2 stop-instances --instance-ids i-xxx
aws ec2 terminate-instances --instance-ids i-xxx
```

### IAM
```bash
aws iam create-user --user-name developer
aws iam attach-user-policy --user-name developer --policy-arn arn:xxx
aws iam create-role --role-name ec2-role --assume-role-policy-document file://policy.json
```

### EBS
```bash
aws ec2 create-volume --volume-type gp3 --size 100 --availability-zone us-east-1a
aws ec2 attach-volume --volume-id vol-xxx --instance-id i-xxx --device /dev/sdf
aws ec2 create-snapshot --volume-id vol-xxx --description "Backup"
```

### S3
```bash
aws s3 mb s3://my-bucket
aws s3 cp file.txt s3://my-bucket/
aws s3 sync ./local s3://my-bucket/remote
aws s3api put-bucket-versioning --bucket my-bucket --versioning-configuration Status=Enabled
```

### ELB
```bash
aws elbv2 create-load-balancer --name my-alb --subnets subnet-xxx
aws elbv2 create-target-group --name my-targets --protocol HTTP --port 80 --vpc-id vpc-xxx
aws elbv2 register-targets --target-group-arn arn:xxx --targets Id=i-xxx
```

---

**Keep this reference handy for interviews! 🚀**

