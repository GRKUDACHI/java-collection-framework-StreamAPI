# ☁️ AWS Services - Complete Tutorial

## 📖 Table of Contents
1. [Introduction to AWS](#introduction-to-aws)
2. [EC2 (Elastic Compute Cloud)](#ec2-elastic-compute-cloud)
3. [IAM (Identity and Access Management)](#iam-identity-and-access-management)
4. [EBS (Elastic Block Store)](#ebs-elastic-block-store)
5. [ELB (Elastic Load Balancer)](#elb-elastic-load-balancer)
6. [AMIs (Amazon Machine Images)](#amis-amazon-machine-images)
7. [S3 (Simple Storage Service)](#s3-simple-storage-service)
8. [CI/CD Pipelines on AWS](#cicd-pipelines-on-aws)
9. [Best Practices](#best-practices)
10. [Common Interview Questions](#common-interview-questions)

---

## 🎓 Introduction to AWS

**AWS (Amazon Web Services)** is a comprehensive cloud computing platform provided by Amazon. It offers over 200 services including computing, storage, networking, databases, analytics, and more.

### Key AWS Concepts:
- **Region**: Geographic area where AWS resources are located (e.g., us-east-1, eu-west-1)
- **Availability Zone (AZ)**: Data centers within a region (for high availability)
- **VPC (Virtual Private Cloud)**: Isolated network environment
- **Pay-as-you-go**: Pay only for what you use
- **Scalability**: Auto-scale resources based on demand

---

## 🖥️ EC2 (Elastic Compute Cloud)

### What is EC2?
**EC2** provides resizable compute capacity in the cloud. Think of it as **renting virtual computers** in the cloud.

### Key Concepts:

#### 1. **Instance Types**
Different configurations of CPU, memory, storage, and networking:

- **General Purpose**: t2, t3, m5, m6i (balanced CPU, memory, networking)
- **Compute Optimized**: c5, c6i (high-performance processors)
- **Memory Optimized**: r5, r6i (high memory for databases)
- **Storage Optimized**: i3, d2 (high IOPS for databases)

**Example**: `t2.micro` = 1 vCPU, 1 GB RAM (free tier eligible)

#### 2. **Instance States**
- **Running**: Instance is active and running
- **Stopped**: Instance is shut down (not charged for compute, but EBS storage is charged)
- **Terminated**: Instance is permanently deleted
- **Pending**: Instance is launching

#### 3. **Key Features**
- **Auto Scaling**: Automatically adjust number of instances
- **Elastic IP**: Static IPv4 address for your instance
- **Security Groups**: Virtual firewall controlling inbound/outbound traffic
- **Key Pairs**: SSH key pairs for secure access

### EC2 Pricing Models:

1. **On-Demand**: Pay per hour, no commitment
2. **Reserved Instances**: 1-3 year commitment, up to 75% discount
3. **Spot Instances**: Bid on unused capacity, up to 90% discount (can be terminated)
4. **Savings Plans**: Flexible pricing model with commitment

### EC2 Use Cases:
- Web applications
- Development and testing environments
- High-performance computing
- Big data processing

### EC2 Hands-On Example:
```bash
# Launch EC2 instance via AWS CLI
aws ec2 run-instances \
  --image-id ami-0c55b159cbfafe1f0 \
  --instance-type t2.micro \
  --key-name my-key-pair \
  --security-group-ids sg-12345678 \
  --subnet-id subnet-12345678
```

---

## 🔐 IAM (Identity and Access Management)

### What is IAM?
**IAM** is AWS's service for managing **users, groups, roles, and permissions**. It controls **WHO can do WHAT** in your AWS account.

### Core Components:

#### 1. **Users**
Individual accounts for people or applications:
- **Root User**: The account owner (use sparingly!)
- **IAM Users**: Individual users with specific permissions

#### 2. **Groups**
Collection of users with same permissions:
- Example: "Developers", "Admins", "ReadOnly"

#### 3. **Roles**
Temporary credentials for AWS services or external users:
- **Service Roles**: For AWS services (EC2, Lambda, etc.)
- **Cross-Account Roles**: Access resources in another AWS account

#### 4. **Policies**
JSON documents that define permissions:
```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": "s3:GetObject",
      "Resource": "arn:aws:s3:::my-bucket/*"
    }
  ]
}
```

### IAM Policy Structure:
- **Effect**: Allow or Deny
- **Action**: What action (e.g., s3:GetObject, ec2:RunInstances)
- **Resource**: Which resource (ARN - Amazon Resource Name)
- **Condition**: Optional conditions (IP address, time, etc.)

### IAM Best Practices:

1. **Principle of Least Privilege**: Give minimum required permissions
2. **Use Roles, Not Users**: For applications and services
3. **Enable MFA**: Multi-factor authentication for root and admin users
4. **Rotate Credentials**: Regularly rotate access keys
5. **Use IAM Roles for EC2**: Don't store access keys in EC2 instances

### IAM Example - Creating a User:
```bash
# Create IAM user
aws iam create-user --user-name developer

# Attach policy
aws iam attach-user-policy \
  --user-name developer \
  --policy-arn arn:aws:iam::aws:policy/AmazonS3ReadOnlyAccess

# Create access key
aws iam create-access-key --user-name developer
```

### Common IAM Policies:
- **AdministratorAccess**: Full AWS access
- **PowerUserAccess**: Full access except IAM management
- **ReadOnlyAccess**: Read-only access to all services
- **AmazonS3FullAccess**: Full S3 access
- **AmazonEC2FullAccess**: Full EC2 access

---

## 💾 EBS (Elastic Block Store)

### What is EBS?
**EBS** provides **persistent block storage volumes** for EC2 instances. Think of it as a **virtual hard drive** that persists even after instance termination.

### EBS Volume Types:

#### 1. **gp3 (General Purpose SSD)**
- **Use Case**: Most workloads (boot volumes, databases, dev/test)
- **IOPS**: 3,000 (baseline), up to 16,000 (provisioned)
- **Throughput**: 125 MB/s (baseline), up to 1,000 MB/s
- **Cost**: Most cost-effective SSD

#### 2. **gp2 (General Purpose SSD)**
- **Use Case**: Boot volumes, small to medium databases
- **IOPS**: 3 IOPS per GB (min 100, max 16,000)
- **Throughput**: 250 MB/s
- **Cost**: Slightly more expensive than gp3

#### 3. **io1/io2 (Provisioned IOPS SSD)**
- **Use Case**: Critical applications, large databases
- **IOPS**: Up to 64,000 (io2), 256,000 (io2 Block Express)
- **Throughput**: Up to 4,000 MB/s
- **Cost**: Most expensive, but highest performance

#### 4. **st1 (Throughput Optimized HDD)**
- **Use Case**: Big data, data warehouses, log processing
- **IOPS**: 500 (baseline), 500 (burst)
- **Throughput**: 500 MB/s
- **Cost**: Lower cost for high throughput

#### 5. **sc1 (Cold HDD)**
- **Use Case**: Infrequently accessed data, backups
- **IOPS**: 250 (baseline), 250 (burst)
- **Throughput**: 250 MB/s
- **Cost**: Lowest cost

### EBS Features:

#### 1. **Snapshots**
- Point-in-time backup of EBS volume
- Incremental (only changed blocks)
- Can create new volumes from snapshots
- Stored in S3 (but managed by EBS)

#### 2. **Encryption**
- Encrypt data at rest and in transit
- Uses AWS KMS (Key Management Service)
- No performance impact

#### 3. **Multi-Attach**
- Attach same volume to multiple instances (io1/io2 only)
- Use case: Clustered applications

### EBS vs Instance Store:
| Feature | EBS | Instance Store |
|---------|-----|----------------|
| Persistence | Yes (survives instance stop) | No (ephemeral) |
| Performance | Good | Very High |
| Cost | Additional charge | Included with instance |
| Use Case | Production, data persistence | Temporary, high-performance |

### EBS Example:
```bash
# Create EBS volume
aws ec2 create-volume \
  --volume-type gp3 \
  --size 100 \
  --availability-zone us-east-1a

# Attach to instance
aws ec2 attach-volume \
  --volume-id vol-12345678 \
  --instance-id i-12345678 \
  --device /dev/sdf

# Create snapshot
aws ec2 create-snapshot \
  --volume-id vol-12345678 \
  --description "Backup before update"
```

---

## ⚖️ ELB (Elastic Load Balancer)

### What is ELB?
**ELB** automatically **distributes incoming traffic** across multiple EC2 instances, containers, or IP addresses. It provides **high availability** and **scalability**.

### Types of Load Balancers:

#### 1. **Application Load Balancer (ALB)**
- **Layer**: Layer 7 (HTTP/HTTPS)
- **Use Case**: Web applications, microservices
- **Features**:
  - Content-based routing (path, host, headers)
  - SSL/TLS termination
  - WebSocket support
  - Container support (ECS, EKS)
- **Health Checks**: HTTP/HTTPS

#### 2. **Network Load Balancer (NLB)**
- **Layer**: Layer 4 (TCP/UDP)
- **Use Case**: High-performance, low-latency applications
- **Features**:
  - Ultra-low latency
  - Static IP addresses
  - Preserves source IP
  - Handles millions of requests per second
- **Health Checks**: TCP/HTTP/HTTPS

#### 3. **Classic Load Balancer (CLB)**
- **Layer**: Layer 4 and Layer 7
- **Use Case**: Legacy applications
- **Status**: Deprecated (use ALB or NLB instead)

#### 4. **Gateway Load Balancer (GWLB)**
- **Layer**: Layer 3 (IP)
- **Use Case**: Third-party virtual appliances (firewalls, intrusion detection)

### Load Balancer Components:

#### 1. **Target Groups**
- Group of targets (EC2 instances, IPs, Lambda functions)
- Health checks configured at target group level
- Routing rules determine which target group receives traffic

#### 2. **Listeners**
- Check for connection requests
- Protocol and port configuration
- Rules for routing traffic

#### 3. **Health Checks**
- Monitor health of targets
- Unhealthy targets are removed from rotation
- Configurable: protocol, port, path, interval, timeout

### ELB Features:

1. **High Availability**: Automatically distributes across multiple AZs
2. **Auto Scaling Integration**: Works with Auto Scaling groups
3. **SSL/TLS Termination**: Handles SSL certificates
4. **Sticky Sessions**: Route same user to same instance
5. **Connection Draining**: Gracefully remove instances

### ELB Example:
```bash
# Create Application Load Balancer
aws elbv2 create-load-balancer \
  --name my-alb \
  --subnets subnet-123 subnet-456 \
  --security-groups sg-12345678

# Create target group
aws elbv2 create-target-group \
  --name my-targets \
  --protocol HTTP \
  --port 80 \
  --vpc-id vpc-12345678

# Register targets
aws elbv2 register-targets \
  --target-group-arn arn:aws:elasticloadbalancing:... \
  --targets Id=i-12345678 Id=i-87654321
```

### Load Balancer Selection Guide:
- **Web Applications**: Use ALB
- **High Performance/Low Latency**: Use NLB
- **Legacy Applications**: Use CLB (but migrate to ALB/NLB)
- **Virtual Appliances**: Use GWLB

---

## 🖼️ AMIs (Amazon Machine Images)

### What is an AMI?
**AMI** is a **template** that contains the software configuration (OS, applications, libraries) needed to launch an EC2 instance. Think of it as a **snapshot** of a configured server.

### AMI Components:

1. **Root Volume Template**: OS, applications, configuration
2. **Launch Permissions**: Who can use the AMI
3. **Block Device Mapping**: EBS volumes and instance store volumes

### Types of AMIs:

#### 1. **Public AMIs**
- Provided by AWS, community, or vendors
- Free or paid
- Examples: Amazon Linux, Ubuntu, Windows Server

#### 2. **Private AMIs**
- Created by you or your organization
- Only accessible to your account

#### 3. **Shared AMIs**
- Shared with specific AWS accounts
- Use case: Sharing within organization

### AMI Lifecycle:

1. **Launch Instance**: Start with base AMI (e.g., Amazon Linux)
2. **Configure**: Install software, configure settings
3. **Create AMI**: Create custom AMI from configured instance
4. **Launch from AMI**: Launch new instances from custom AMI

### AMI Best Practices:

1. **Keep AMIs Updated**: Regularly update with security patches
2. **Use Golden AMIs**: Pre-configured AMIs for faster launches
3. **Tag AMIs**: Use tags for organization
4. **Deregister Unused AMIs**: Save storage costs
5. **Encrypt AMIs**: For sensitive data

### AMI Example:
```bash
# Create AMI from running instance
aws ec2 create-image \
  --instance-id i-12345678 \
  --name "MyWebServer-2024" \
  --description "Web server with Apache and PHP"

# Launch instance from AMI
aws ec2 run-instances \
  --image-id ami-12345678 \
  --instance-type t2.micro \
  --key-name my-key-pair
```

### AMI vs Snapshot:
- **AMI**: Complete template (OS + data + configuration)
- **Snapshot**: Backup of EBS volume (just data)
- **AMI includes snapshots**: AMI contains references to EBS snapshots

---

## 🪣 S3 (Simple Storage Service)

### What is S3?
**S3** is **object storage** service for storing and retrieving any amount of data. Think of it as a **massive hard drive in the cloud**.

### Key Concepts:

#### 1. **Buckets**
- Container for objects (like folders)
- Globally unique name
- Region-specific
- Can host static websites

#### 2. **Objects**
- Files stored in buckets
- Consist of:
  - **Key**: Object name (path)
  - **Value**: Data (file content)
  - **Version ID**: For versioning
  - **Metadata**: Custom metadata

#### 3. **Keys**
- Unique identifier for object
- Example: `folder1/folder2/file.txt`

### S3 Storage Classes:

#### 1. **S3 Standard**
- **Use Case**: Frequently accessed data
- **Availability**: 99.99%
- **Durability**: 99.999999999% (11 9's)
- **Cost**: Highest

#### 2. **S3 Intelligent-Tiering**
- **Use Case**: Unknown or changing access patterns
- **Features**: Automatically moves objects between tiers
- **Cost**: Monitoring fee + storage cost

#### 3. **S3 Standard-IA (Infrequent Access)**
- **Use Case**: Less frequently accessed data
- **Availability**: 99.9%
- **Cost**: Lower storage, retrieval fee

#### 4. **S3 One Zone-IA**
- **Use Case**: Non-critical, infrequently accessed data
- **Availability**: 99.5% (single AZ)
- **Cost**: 20% cheaper than Standard-IA

#### 5. **S3 Glacier Instant Retrieval**
- **Use Case**: Archive data with immediate access needs
- **Retrieval Time**: Milliseconds
- **Cost**: Lower storage, retrieval fee

#### 6. **S3 Glacier Flexible Retrieval**
- **Use Case**: Archive data (backups, compliance)
- **Retrieval Options**: Expedited (1-5 min), Standard (3-5 hours), Bulk (5-12 hours)
- **Cost**: Very low storage, retrieval fee

#### 7. **S3 Glacier Deep Archive**
- **Use Case**: Long-term archive (7-10 years)
- **Retrieval Time**: 12 hours
- **Cost**: Lowest storage cost

### S3 Features:

#### 1. **Versioning**
- Keep multiple versions of same object
- Protects against accidental deletion
- Can restore previous versions

#### 2. **Lifecycle Policies**
- Automatically transition objects between storage classes
- Delete objects after specified time
- Example: Move to Glacier after 90 days

#### 3. **Encryption**
- **Server-Side Encryption (SSE)**:
  - SSE-S3: AWS-managed keys
  - SSE-KMS: AWS KMS keys
  - SSE-C: Customer-provided keys
- **Client-Side Encryption**: Encrypt before uploading

#### 4. **Access Control**
- **Bucket Policies**: JSON policies for bucket-level access
- **ACLs**: Legacy access control
- **IAM Policies**: User/role-based access
- **CORS**: Cross-origin resource sharing

#### 5. **Static Website Hosting**
- Host static websites
- No web server needed
- Example: `http://my-bucket.s3-website-us-east-1.amazonaws.com`

### S3 Example:
```bash
# Create bucket
aws s3 mb s3://my-unique-bucket-name

# Upload file
aws s3 cp file.txt s3://my-bucket/folder/file.txt

# Download file
aws s3 cp s3://my-bucket/folder/file.txt ./file.txt

# Sync directory
aws s3 sync ./local-folder s3://my-bucket/remote-folder

# Enable versioning
aws s3api put-bucket-versioning \
  --bucket my-bucket \
  --versioning-configuration Status=Enabled

# Set lifecycle policy
aws s3api put-bucket-lifecycle-configuration \
  --bucket my-bucket \
  --lifecycle-configuration file://lifecycle.json
```

### S3 Best Practices:

1. **Use Appropriate Storage Class**: Match storage class to access pattern
2. **Enable Versioning**: For critical data
3. **Use Lifecycle Policies**: Automate cost optimization
4. **Enable Encryption**: Always encrypt sensitive data
5. **Use Bucket Policies**: For fine-grained access control
6. **Enable MFA Delete**: Require MFA for deletion
7. **Use CloudFront**: For global content delivery

---

## 🔄 CI/CD Pipelines on AWS

### What is CI/CD?
**CI/CD** stands for **Continuous Integration** and **Continuous Deployment**:
- **CI**: Automatically build and test code changes
- **CD**: Automatically deploy to production

### AWS CI/CD Services:

#### 1. **AWS CodePipeline**
- Fully managed CI/CD service
- Orchestrates the release process
- Integrates with other AWS services

#### 2. **AWS CodeBuild**
- Fully managed build service
- Compiles source code, runs tests
- Scales automatically

#### 3. **AWS CodeDeploy**
- Automates code deployments
- Deploys to EC2, Lambda, ECS, on-premises
- Supports blue/green and rolling deployments

#### 4. **AWS CodeCommit**
- Private Git repository
- Fully managed source control

### CI/CD Pipeline Flow:

```
Source Code (CodeCommit/GitHub)
    ↓
CodeBuild (Build & Test)
    ↓
CodeDeploy (Deploy to EC2/ECS/Lambda)
    ↓
Production Environment
```

### CI/CD Pipeline Example:

#### 1. **CodePipeline Configuration**
```json
{
  "pipeline": {
    "name": "MyAppPipeline",
    "stages": [
      {
        "name": "Source",
        "actions": [
          {
            "name": "SourceAction",
            "actionTypeId": {
              "category": "Source",
              "owner": "AWS",
              "provider": "CodeCommit"
            }
          }
        ]
      },
      {
        "name": "Build",
        "actions": [
          {
            "name": "BuildAction",
            "actionTypeId": {
              "category": "Build",
              "owner": "AWS",
              "provider": "CodeBuild"
            }
          }
        ]
      },
      {
        "name": "Deploy",
        "actions": [
          {
            "name": "DeployAction",
            "actionTypeId": {
              "category": "Deploy",
              "owner": "AWS",
              "provider": "CodeDeploy"
            }
          }
        ]
      }
    ]
  }
}
```

#### 2. **buildspec.yml (CodeBuild)**
```yaml
version: 0.2
phases:
  pre_build:
    commands:
      - echo Logging in to Amazon ECR...
      - aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin 123456789.dkr.ecr.us-east-1.amazonaws.com
  build:
    commands:
      - echo Build started on `date`
      - echo Building the Docker image...
      - docker build -t myapp:latest .
      - docker tag myapp:latest 123456789.dkr.ecr.us-east-1.amazonaws.com/myapp:latest
  post_build:
    commands:
      - echo Build completed on `date`
      - echo Pushing the Docker image...
      - docker push 123456789.dkr.ecr.us-east-1.amazonaws.com/myapp:latest
```

#### 3. **appspec.yml (CodeDeploy)**
```yaml
version: 0.0
os: linux
files:
  - source: /
    destination: /var/www/html
hooks:
  BeforeInstall:
    - location: scripts/install_dependencies.sh
      timeout: 300
  AfterInstall:
    - location: scripts/restart_server.sh
      timeout: 300
```

### CI/CD Best Practices:

1. **Automate Everything**: Build, test, deploy automatically
2. **Use Infrastructure as Code**: CloudFormation, Terraform
3. **Test in Staging**: Always test before production
4. **Use Blue/Green Deployments**: Zero-downtime deployments
5. **Monitor Deployments**: CloudWatch alarms and logs
6. **Security Scanning**: Scan for vulnerabilities in pipeline
7. **Version Control**: Everything in Git

### Alternative CI/CD Tools:
- **Jenkins**: Self-hosted CI/CD
- **GitHub Actions**: GitHub's CI/CD
- **GitLab CI/CD**: GitLab's CI/CD
- **CircleCI**: Cloud-based CI/CD

---

## ✅ Best Practices

### Security:
1. **Use IAM Roles**: Don't store credentials in code
2. **Enable MFA**: For root and admin users
3. **Encrypt Data**: At rest and in transit
4. **Use VPC**: Isolate resources
5. **Security Groups**: Restrict access (principle of least privilege)
6. **Regular Updates**: Keep AMIs and software updated

### Cost Optimization:
1. **Right-Sizing**: Use appropriate instance types
2. **Reserved Instances**: For predictable workloads
3. **Spot Instances**: For fault-tolerant workloads
4. **Lifecycle Policies**: Move old data to cheaper storage
5. **Delete Unused Resources**: Regular cleanup
6. **Monitor Costs**: Use Cost Explorer and budgets

### High Availability:
1. **Multi-AZ Deployment**: Deploy across multiple availability zones
2. **Load Balancers**: Distribute traffic
3. **Auto Scaling**: Handle traffic spikes
4. **Backup Strategy**: Regular snapshots and backups
5. **Disaster Recovery**: Plan for failures

### Performance:
1. **Use CDN**: CloudFront for static content
2. **Caching**: ElastiCache for frequently accessed data
3. **Database Optimization**: Use appropriate instance types
4. **Connection Pooling**: For database connections
5. **Monitoring**: CloudWatch for performance metrics

---

## 🎤 Common Interview Questions

### EC2 Questions:

**Q1: What is EC2 and what are its key features?**
- EC2 provides virtual servers in the cloud
- Key features: Scalability, flexibility, pay-as-you-go, multiple instance types, security groups, key pairs

**Q2: Explain the difference between stopping and terminating an EC2 instance.**
- **Stopping**: Instance is shut down, EBS volumes persist, can be restarted, charged for EBS storage
- **Terminating**: Instance is permanently deleted, EBS volumes deleted (unless DeleteOnTermination=false), cannot be restarted

**Q3: What are Security Groups and how do they work?**
- Virtual firewall for EC2 instances
- Control inbound and outbound traffic
- Stateful (return traffic automatically allowed)
- Default: All outbound allowed, all inbound denied
- Can have multiple security groups per instance

**Q4: Explain EC2 pricing models.**
- **On-Demand**: Pay per hour, no commitment, flexible
- **Reserved Instances**: 1-3 year commitment, up to 75% discount
- **Spot Instances**: Bid on unused capacity, up to 90% discount, can be terminated
- **Savings Plans**: Flexible pricing with commitment

**Q5: What is the difference between instance store and EBS?**
- **Instance Store**: Ephemeral, high performance, included with instance, data lost on stop/terminate
- **EBS**: Persistent, good performance, additional cost, data persists after stop

### IAM Questions:

**Q6: What is IAM and why is it important?**
- IAM manages users, groups, roles, and permissions
- Provides secure access to AWS services
- Enables principle of least privilege
- Centralized access management

**Q7: Explain the difference between IAM Users and IAM Roles.**
- **Users**: Permanent credentials for people or applications, long-term access
- **Roles**: Temporary credentials, assumed by users or services, better security practice

**Q8: What is the principle of least privilege?**
- Grant only minimum permissions required
- Reduces security risk
- Easier to audit and manage

**Q9: What is an IAM Policy and what are its components?**
- JSON document defining permissions
- Components: Effect (Allow/Deny), Action (what can be done), Resource (which resource), Condition (optional conditions)

**Q10: Why should you use IAM Roles for EC2 instead of storing access keys?**
- Roles provide temporary credentials (more secure)
- Automatic credential rotation
- No need to manage keys
- Better audit trail

### EBS Questions:

**Q11: What is EBS and what are its volume types?**
- EBS provides persistent block storage
- Types: gp3, gp2 (general purpose), io1/io2 (provisioned IOPS), st1 (throughput optimized), sc1 (cold HDD)

**Q12: What is the difference between EBS snapshots and AMIs?**
- **Snapshot**: Backup of EBS volume (just data)
- **AMI**: Complete template including OS, data, configuration (includes snapshots)

**Q13: How do EBS snapshots work?**
- Point-in-time backup
- Incremental (only changed blocks stored)
- Stored in S3 (managed by EBS)
- Can create new volumes from snapshots

**Q14: Can you attach an EBS volume to multiple instances?**
- Yes, with Multi-Attach (io1/io2 only)
- Use case: Clustered applications
- Requires cluster-aware file system

**Q15: What happens to EBS volumes when an instance is terminated?**
- By default, root volume is deleted (DeleteOnTermination=true)
- Additional volumes persist (DeleteOnTermination=false)
- Can configure per volume

### ELB Questions:

**Q16: What is a Load Balancer and why do you need it?**
- Distributes traffic across multiple targets
- Provides high availability and scalability
- Health checks remove unhealthy targets
- SSL/TLS termination

**Q17: Explain the difference between ALB, NLB, and CLB.**
- **ALB**: Layer 7 (HTTP/HTTPS), content-based routing, web applications
- **NLB**: Layer 4 (TCP/UDP), high performance, low latency
- **CLB**: Legacy, deprecated, use ALB or NLB

**Q18: What is a Target Group?**
- Group of targets (EC2 instances, IPs, Lambda)
- Health checks configured at target group level
- Routing rules determine which target group receives traffic

**Q19: How do health checks work in ELB?**
- Monitor health of targets
- Configurable: protocol, port, path, interval, timeout, healthy/unhealthy thresholds
- Unhealthy targets removed from rotation

**Q20: What is connection draining?**
- Gracefully remove instances from load balancer
- Allows existing connections to complete
- Prevents dropped connections during deployment

### AMI Questions:

**Q21: What is an AMI?**
- Template for launching EC2 instances
- Contains OS, applications, configuration
- Includes references to EBS snapshots

**Q22: How do you create a custom AMI?**
1. Launch instance from base AMI
2. Configure and install software
3. Create AMI from instance
4. Launch new instances from custom AMI

**Q23: What is a Golden AMI?**
- Pre-configured AMI with standard software
- Faster instance launches
- Consistent configuration
- Security patches applied

**Q24: Can you share AMIs between AWS accounts?**
- Yes, using AMI sharing
- Specify account IDs
- Use case: Sharing within organization

**Q25: What's the difference between public and private AMIs?**
- **Public**: Available to everyone (AWS, community, vendors)
- **Private**: Only accessible to your account
- **Shared**: Shared with specific accounts

### S3 Questions:

**Q26: What is S3 and what are its key features?**
- Object storage service
- Features: Versioning, lifecycle policies, encryption, static website hosting, access control

**Q27: Explain S3 storage classes.**
- **Standard**: Frequently accessed, highest cost
- **Standard-IA**: Infrequently accessed, lower cost
- **Glacier**: Archive, very low cost, retrieval time varies
- **Deep Archive**: Long-term archive, lowest cost

**Q28: What is S3 versioning?**
- Keep multiple versions of same object
- Protects against accidental deletion
- Can restore previous versions
- Each version has unique version ID

**Q29: How do S3 lifecycle policies work?**
- Automatically transition objects between storage classes
- Delete objects after specified time
- Example: Move to Glacier after 90 days, delete after 1 year

**Q30: What is the difference between S3 and EBS?**
- **S3**: Object storage, unlimited capacity, accessed via API, 99.999999999% durability
- **EBS**: Block storage, attached to EC2, accessed as file system, 99.8-99.9% availability

### CI/CD Questions:

**Q31: What is CI/CD and why is it important?**
- **CI**: Automatically build and test code
- **CD**: Automatically deploy to production
- Benefits: Faster releases, fewer errors, consistent deployments

**Q32: Explain AWS CodePipeline.**
- Fully managed CI/CD service
- Orchestrates release process
- Integrates with CodeBuild, CodeDeploy, CodeCommit
- Visual pipeline representation

**Q33: What is the difference between CI and CD?**
- **CI**: Continuous Integration - build and test automatically
- **CD**: Continuous Deployment - deploy automatically to production
- **CD can also mean**: Continuous Delivery - deploy to staging, manual production

**Q34: What is blue/green deployment?**
- Two identical production environments
- Deploy new version to inactive environment
- Switch traffic when ready
- Zero-downtime deployment

**Q35: How do you handle rollbacks in CI/CD?**
- Keep previous version/AMI
- Use versioning in S3
- Database migrations should be reversible
- Automated rollback scripts

### General AWS Questions:

**Q36: What is the difference between a region and an availability zone?**
- **Region**: Geographic area (e.g., us-east-1)
- **Availability Zone**: Data center within region (e.g., us-east-1a)
- Multiple AZs per region for high availability

**Q37: What is VPC?**
- Virtual Private Cloud
- Isolated network environment
- Control IP ranges, subnets, routing, security

**Q38: Explain the shared responsibility model.**
- **AWS**: Security OF the cloud (infrastructure, hardware, software)
- **Customer**: Security IN the cloud (data, applications, access control)

**Q39: What is CloudWatch?**
- Monitoring and observability service
- Metrics, logs, alarms
- Monitor AWS resources and applications

**Q40: How do you ensure high availability in AWS?**
- Multi-AZ deployment
- Load balancers
- Auto Scaling
- Regular backups
- Health checks and monitoring

---

## 📚 Additional Resources

### AWS Documentation:
- [AWS EC2 Documentation](https://docs.aws.amazon.com/ec2/)
- [AWS IAM Documentation](https://docs.aws.amazon.com/iam/)
- [AWS S3 Documentation](https://docs.aws.amazon.com/s3/)
- [AWS Well-Architected Framework](https://aws.amazon.com/architecture/well-architected/)

### Practice:
- AWS Free Tier (12 months free)
- AWS Hands-On Labs
- AWS Certification Practice Tests

### Tools:
- AWS CLI
- AWS Console
- CloudFormation (Infrastructure as Code)
- Terraform (Infrastructure as Code)

---

**Good luck with your AWS journey! 🚀**

