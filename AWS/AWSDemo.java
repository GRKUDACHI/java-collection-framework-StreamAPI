/**
 * AWS Services Demo - Java Examples
 * 
 * This file demonstrates AWS SDK usage for:
 * - EC2 (Elastic Compute Cloud)
 * - IAM (Identity and Access Management)
 * - EBS (Elastic Block Store)
 * - ELB (Elastic Load Balancer)
 * - S3 (Simple Storage Service)
 * 
 * Prerequisites:
 * 1. AWS SDK for Java 2.x
 * 2. AWS credentials configured (~/.aws/credentials or environment variables)
 * 3. Appropriate IAM permissions
 * 
 * Maven Dependencies:
 * <dependency>
 *     <groupId>software.amazon.awssdk</groupId>
 *     <artifactId>ec2</artifactId>
 *     <version>2.20.0</version>
 * </dependency>
 * <dependency>
 *     <groupId>software.amazon.awssdk</groupId>
 *     <artifactId>iam</artifactId>
 *     <version>2.20.0</version>
 * </dependency>
 * <dependency>
 *     <groupId>software.amazon.awssdk</groupId>
 *     <artifactId>s3</artifactId>
 *     <version>2.20.0</version>
 * </dependency>
 * <dependency>
 *     <groupId>software.amazon.awssdk</groupId>
 *     <artifactId>elasticloadbalancingv2</artifactId>
 *     <version>2.20.0</version>
 * </dependency>
 */

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.*;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.elasticloadbalancingv2.ElasticLoadBalancingV2Client;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.*;

import java.util.List;
import java.util.ArrayList;

public class AWSDemo {
    
    // Region configuration
    private static final Region AWS_REGION = Region.US_EAST_1;
    
    public static void main(String[] args) {
        System.out.println("=== AWS Services Demo ===\n");
        
        // EC2 Examples
        demonstrateEC2();
        
        // IAM Examples
        demonstrateIAM();
        
        // EBS Examples
        demonstrateEBS();
        
        // S3 Examples
        demonstrateS3();
        
        // ELB Examples
        demonstrateELB();
    }
    
    // ============================================
    // EC2 (Elastic Compute Cloud) Examples
    // ============================================
    
    /**
     * Demonstrates EC2 operations:
     * - List instances
     * - Describe instance types
     * - Create security group
     * - Launch instance (commented out - requires actual resources)
     */
    public static void demonstrateEC2() {
        System.out.println("=== EC2 (Elastic Compute Cloud) Demo ===\n");
        
        try (Ec2Client ec2Client = Ec2Client.builder()
                .region(AWS_REGION)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build()) {
            
            // 1. List all EC2 instances
            System.out.println("1. Listing EC2 Instances:");
            DescribeInstancesRequest describeRequest = DescribeInstancesRequest.builder().build();
            DescribeInstancesResponse response = ec2Client.describeInstances(describeRequest);
            
            int instanceCount = 0;
            for (Reservation reservation : response.reservations()) {
                for (Instance instance : reservation.instances()) {
                    instanceCount++;
                    System.out.println("   Instance ID: " + instance.instanceId());
                    System.out.println("   State: " + instance.state().name());
                    System.out.println("   Type: " + instance.instanceType());
                    System.out.println("   AMI: " + instance.imageId());
                    System.out.println("   Public IP: " + instance.publicIpAddress());
                    System.out.println("   Private IP: " + instance.privateIpAddress());
                    System.out.println("   ---");
                }
            }
            
            if (instanceCount == 0) {
                System.out.println("   No instances found.");
            }
            
            // 2. Describe available instance types
            System.out.println("\n2. Available Instance Types (sample):");
            DescribeInstanceTypesRequest typesRequest = DescribeInstanceTypesRequest.builder()
                    .maxResults(5)
                    .build();
            DescribeInstanceTypesResponse typesResponse = ec2Client.describeInstanceTypes(typesRequest);
            
            for (InstanceTypeInfo typeInfo : typesResponse.instanceTypes()) {
                System.out.println("   Type: " + typeInfo.instanceType());
                System.out.println("   vCPU: " + typeInfo.vCpuInfo().defaultVCpus());
                System.out.println("   Memory: " + typeInfo.memoryInfo().sizeInMiB() + " MiB");
                System.out.println("   ---");
            }
            
            // 3. List Security Groups
            System.out.println("\n3. Security Groups:");
            DescribeSecurityGroupsRequest sgRequest = DescribeSecurityGroupsRequest.builder().build();
            DescribeSecurityGroupsResponse sgResponse = ec2Client.describeSecurityGroups(sgRequest);
            
            for (SecurityGroup sg : sgResponse.securityGroups()) {
                System.out.println("   Group ID: " + sg.groupId());
                System.out.println("   Group Name: " + sg.groupName());
                System.out.println("   Description: " + sg.description());
                System.out.println("   VPC ID: " + sg.vpcId());
                System.out.println("   ---");
            }
            
            // 4. Example: Launch EC2 Instance (commented out - requires actual resources)
            /*
            System.out.println("\n4. Launching EC2 Instance (Example):");
            RunInstancesRequest runRequest = RunInstancesRequest.builder()
                    .imageId("ami-0c55b159cbfafe1f0")  // Amazon Linux 2
                    .instanceType(InstanceType.T2_MICRO)
                    .minCount(1)
                    .maxCount(1)
                    .keyName("my-key-pair")
                    .securityGroupIds("sg-12345678")
                    .build();
            
            RunInstancesResponse runResponse = ec2Client.runInstances(runRequest);
            Instance newInstance = runResponse.instances().get(0);
            System.out.println("   Launched Instance ID: " + newInstance.instanceId());
            */
            
        } catch (Exception e) {
            System.err.println("EC2 Error: " + e.getMessage());
        }
        
        System.out.println("\n");
    }
    
    // ============================================
    // IAM (Identity and Access Management) Examples
    // ============================================
    
    /**
     * Demonstrates IAM operations:
     * - List users
     * - List roles
     * - List policies
     * - Create user (commented out)
     */
    public static void demonstrateIAM() {
        System.out.println("=== IAM (Identity and Access Management) Demo ===\n");
        
        try (IamClient iamClient = IamClient.builder()
                .region(AWS_REGION)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build()) {
            
            // 1. List IAM Users
            System.out.println("1. IAM Users:");
            ListUsersRequest usersRequest = ListUsersRequest.builder().build();
            ListUsersResponse usersResponse = iamClient.listUsers(usersRequest);
            
            for (User user : usersResponse.users()) {
                System.out.println("   User Name: " + user.userName());
                System.out.println("   User ARN: " + user.arn());
                System.out.println("   Created: " + user.createDate());
                System.out.println("   ---");
            }
            
            // 2. List IAM Roles
            System.out.println("\n2. IAM Roles:");
            ListRolesRequest rolesRequest = ListRolesRequest.builder().maxItems(10).build();
            ListRolesResponse rolesResponse = iamClient.listRoles(rolesRequest);
            
            for (Role role : rolesResponse.roles()) {
                System.out.println("   Role Name: " + role.roleName());
                System.out.println("   Role ARN: " + role.arn());
                System.out.println("   ---");
            }
            
            // 3. List Managed Policies
            System.out.println("\n3. Managed Policies (sample):");
            ListPoliciesRequest policiesRequest = ListPoliciesRequest.builder()
                    .scope(PolicyScopeType.AWS)
                    .maxItems(5)
                    .build();
            ListPoliciesResponse policiesResponse = iamClient.listPolicies(policiesRequest);
            
            for (Policy policy : policiesResponse.policies()) {
                System.out.println("   Policy Name: " + policy.policyName());
                System.out.println("   Policy ARN: " + policy.arn());
                System.out.println("   ---");
            }
            
            // 4. Example: Create IAM User (commented out)
            /*
            System.out.println("\n4. Creating IAM User (Example):");
            CreateUserRequest createUserRequest = CreateUserRequest.builder()
                    .userName("demo-user")
                    .build();
            CreateUserResponse createUserResponse = iamClient.createUser(createUserRequest);
            System.out.println("   Created User: " + createUserResponse.user().userName());
            
            // Attach policy
            AttachUserPolicyRequest attachRequest = AttachUserPolicyRequest.builder()
                    .userName("demo-user")
                    .policyArn("arn:aws:iam::aws:policy/AmazonS3ReadOnlyAccess")
                    .build();
            iamClient.attachUserPolicy(attachRequest);
            System.out.println("   Attached S3 ReadOnly policy");
            */
            
        } catch (Exception e) {
            System.err.println("IAM Error: " + e.getMessage());
        }
        
        System.out.println("\n");
    }
    
    // ============================================
    // EBS (Elastic Block Store) Examples
    // ============================================
    
    /**
     * Demonstrates EBS operations:
     * - List volumes
     * - List snapshots
     * - Create volume (commented out)
     */
    public static void demonstrateEBS() {
        System.out.println("=== EBS (Elastic Block Store) Demo ===\n");
        
        try (Ec2Client ec2Client = Ec2Client.builder()
                .region(AWS_REGION)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build()) {
            
            // 1. List EBS Volumes
            System.out.println("1. EBS Volumes:");
            DescribeVolumesRequest volumesRequest = DescribeVolumesRequest.builder().build();
            DescribeVolumesResponse volumesResponse = ec2Client.describeVolumes(volumesRequest);
            
            for (Volume volume : volumesResponse.volumes()) {
                System.out.println("   Volume ID: " + volume.volumeId());
                System.out.println("   Size: " + volume.size() + " GiB");
                System.out.println("   Type: " + volume.volumeType());
                System.out.println("   State: " + volume.state());
                System.out.println("   AZ: " + volume.availabilityZone());
                System.out.println("   Encrypted: " + volume.encrypted());
                System.out.println("   ---");
            }
            
            // 2. List EBS Snapshots
            System.out.println("\n2. EBS Snapshots (your snapshots):");
            DescribeSnapshotsRequest snapshotsRequest = DescribeSnapshotsRequest.builder()
                    .ownerIds("self")  // Only your snapshots
                    .maxResults(5)
                    .build();
            DescribeSnapshotsResponse snapshotsResponse = ec2Client.describeSnapshots(snapshotsRequest);
            
            for (Snapshot snapshot : snapshotsResponse.snapshots()) {
                System.out.println("   Snapshot ID: " + snapshot.snapshotId());
                System.out.println("   Volume ID: " + snapshot.volumeId());
                System.out.println("   Size: " + snapshot.volumeSize() + " GiB");
                System.out.println("   State: " + snapshot.state());
                System.out.println("   Created: " + snapshot.startTime());
                System.out.println("   ---");
            }
            
            // 3. Example: Create EBS Volume (commented out)
            /*
            System.out.println("\n3. Creating EBS Volume (Example):");
            CreateVolumeRequest createVolumeRequest = CreateVolumeRequest.builder()
                    .volumeType(VolumeType.GP3)
                    .size(100)
                    .availabilityZone("us-east-1a")
                    .encrypted(true)
                    .build();
            
            CreateVolumeResponse createVolumeResponse = ec2Client.createVolume(createVolumeRequest);
            System.out.println("   Created Volume ID: " + createVolumeResponse.volumeId());
            */
            
        } catch (Exception e) {
            System.err.println("EBS Error: " + e.getMessage());
        }
        
        System.out.println("\n");
    }
    
    // ============================================
    // S3 (Simple Storage Service) Examples
    // ============================================
    
    /**
     * Demonstrates S3 operations:
     * - List buckets
     * - List objects in bucket
     * - Create bucket (commented out)
     * - Upload file (commented out)
     */
    public static void demonstrateS3() {
        System.out.println("=== S3 (Simple Storage Service) Demo ===\n");
        
        try (S3Client s3Client = S3Client.builder()
                .region(AWS_REGION)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build()) {
            
            // 1. List S3 Buckets
            System.out.println("1. S3 Buckets:");
            ListBucketsRequest bucketsRequest = ListBucketsRequest.builder().build();
            ListBucketsResponse bucketsResponse = s3Client.listBuckets(bucketsRequest);
            
            for (Bucket bucket : bucketsResponse.buckets()) {
                System.out.println("   Bucket Name: " + bucket.name());
                System.out.println("   Created: " + bucket.creationDate());
                
                // Get bucket location
                try {
                    GetBucketLocationRequest locationRequest = GetBucketLocationRequest.builder()
                            .bucket(bucket.name())
                            .build();
                    GetBucketLocationResponse locationResponse = s3Client.getBucketLocation(locationRequest);
                    System.out.println("   Region: " + locationResponse.locationConstraint());
                } catch (Exception e) {
                    System.out.println("   Region: Unable to determine");
                }
                System.out.println("   ---");
            }
            
            // 2. List Objects in a Bucket (if buckets exist)
            if (!bucketsResponse.buckets().isEmpty()) {
                String bucketName = bucketsResponse.buckets().get(0).name();
                System.out.println("\n2. Objects in bucket '" + bucketName + "':");
                
                try {
                    ListObjectsV2Request objectsRequest = ListObjectsV2Request.builder()
                            .bucket(bucketName)
                            .maxKeys(10)
                            .build();
                    ListObjectsV2Response objectsResponse = s3Client.listObjectsV2(objectsRequest);
                    
                    for (S3Object s3Object : objectsResponse.contents()) {
                        System.out.println("   Key: " + s3Object.key());
                        System.out.println("   Size: " + s3Object.size() + " bytes");
                        System.out.println("   Last Modified: " + s3Object.lastModified());
                        System.out.println("   ---");
                    }
                } catch (Exception e) {
                    System.out.println("   Error listing objects: " + e.getMessage());
                }
            }
            
            // 3. Example: Create S3 Bucket (commented out)
            /*
            System.out.println("\n3. Creating S3 Bucket (Example):");
            String newBucketName = "my-demo-bucket-" + System.currentTimeMillis();
            CreateBucketRequest createBucketRequest = CreateBucketRequest.builder()
                    .bucket(newBucketName)
                    .build();
            s3Client.createBucket(createBucketRequest);
            System.out.println("   Created Bucket: " + newBucketName);
            */
            
            // 4. Example: Upload File to S3 (commented out)
            /*
            System.out.println("\n4. Uploading File to S3 (Example):");
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket("my-bucket")
                    .key("folder/file.txt")
                    .build();
            
            s3Client.putObject(putRequest, RequestBody.fromString("Hello, S3!"));
            System.out.println("   Uploaded file to s3://my-bucket/folder/file.txt");
            */
            
        } catch (Exception e) {
            System.err.println("S3 Error: " + e.getMessage());
        }
        
        System.out.println("\n");
    }
    
    // ============================================
    // ELB (Elastic Load Balancer) Examples
    // ============================================
    
    /**
     * Demonstrates ELB operations:
     * - List load balancers
     * - List target groups
     * - Describe load balancer attributes
     */
    public static void demonstrateELB() {
        System.out.println("=== ELB (Elastic Load Balancer) Demo ===\n");
        
        try (ElasticLoadBalancingV2Client elbClient = ElasticLoadBalancingV2Client.builder()
                .region(AWS_REGION)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build()) {
            
            // 1. List Load Balancers
            System.out.println("1. Load Balancers:");
            DescribeLoadBalancersRequest lbRequest = DescribeLoadBalancersRequest.builder().build();
            DescribeLoadBalancersResponse lbResponse = elbClient.describeLoadBalancers(lbRequest);
            
            for (LoadBalancer lb : lbResponse.loadBalancers()) {
                System.out.println("   Load Balancer Name: " + lb.loadBalancerName());
                System.out.println("   ARN: " + lb.loadBalancerArn());
                System.out.println("   Type: " + lb.type());
                System.out.println("   State: " + lb.state().code());
                System.out.println("   DNS Name: " + lb.dnsName());
                System.out.println("   Scheme: " + lb.scheme());
                System.out.println("   ---");
            }
            
            // 2. List Target Groups
            System.out.println("\n2. Target Groups:");
            DescribeTargetGroupsRequest tgRequest = DescribeTargetGroupsRequest.builder().build();
            DescribeTargetGroupsResponse tgResponse = elbClient.describeTargetGroups(tgRequest);
            
            for (TargetGroup tg : tgResponse.targetGroups()) {
                System.out.println("   Target Group Name: " + tg.targetGroupName());
                System.out.println("   ARN: " + tg.targetGroupArn());
                System.out.println("   Protocol: " + tg.protocol());
                System.out.println("   Port: " + tg.port());
                System.out.println("   Health Check Path: " + tg.healthCheckPath());
                System.out.println("   ---");
            }
            
            // 3. Example: Create Application Load Balancer (commented out)
            /*
            System.out.println("\n3. Creating ALB (Example):");
            CreateLoadBalancerRequest createLbRequest = CreateLoadBalancerRequest.builder()
                    .name("my-demo-alb")
                    .subnets("subnet-12345678", "subnet-87654321")
                    .securityGroups("sg-12345678")
                    .scheme(LoadBalancerSchemeEnum.INTERNET_FACING)
                    .type(LoadBalancerTypeEnum.APPLICATION)
                    .build();
            
            CreateLoadBalancerResponse createLbResponse = elbClient.createLoadBalancer(createLbRequest);
            System.out.println("   Created ALB: " + createLbResponse.loadBalancers().get(0).loadBalancerName());
            */
            
        } catch (Exception e) {
            System.err.println("ELB Error: " + e.getMessage());
        }
        
        System.out.println("\n=== Demo Complete ===\n");
    }
    
    // ============================================
    // Helper Methods
    // ============================================
    
    /**
     * Example: Get instance details by ID
     */
    public static void getInstanceDetails(String instanceId) {
        try (Ec2Client ec2Client = Ec2Client.builder()
                .region(AWS_REGION)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build()) {
            
            DescribeInstancesRequest request = DescribeInstancesRequest.builder()
                    .instanceIds(instanceId)
                    .build();
            
            DescribeInstancesResponse response = ec2Client.describeInstances(request);
            
            if (!response.reservations().isEmpty()) {
                Instance instance = response.reservations().get(0).instances().get(0);
                System.out.println("Instance: " + instance.instanceId());
                System.out.println("State: " + instance.state().name());
                System.out.println("Type: " + instance.instanceType());
            }
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
    
    /**
     * Example: Stop an EC2 instance
     */
    public static void stopInstance(String instanceId) {
        try (Ec2Client ec2Client = Ec2Client.builder()
                .region(AWS_REGION)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build()) {
            
            StopInstancesRequest request = StopInstancesRequest.builder()
                    .instanceIds(instanceId)
                    .build();
            
            StopInstancesResponse response = ec2Client.stopInstances(request);
            System.out.println("Stopping instance: " + instanceId);
            System.out.println("Current State: " + response.stoppingInstances().get(0).currentState().name());
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
    
    /**
     * Example: Create EBS snapshot
     */
    public static void createSnapshot(String volumeId, String description) {
        try (Ec2Client ec2Client = Ec2Client.builder()
                .region(AWS_REGION)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build()) {
            
            CreateSnapshotRequest request = CreateSnapshotRequest.builder()
                    .volumeId(volumeId)
                    .description(description)
                    .build();
            
            CreateSnapshotResponse response = ec2Client.createSnapshot(request);
            System.out.println("Created Snapshot: " + response.snapshotId());
            System.out.println("State: " + response.state());
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}

/**
 * Key Takeaways:
 * 
 * 1. EC2:
 *    - Virtual servers in the cloud
 *    - Multiple instance types for different workloads
 *    - Security groups control access
 *    - Key pairs for SSH access
 * 
 * 2. IAM:
 *    - Manages users, groups, roles, and permissions
 *    - Use roles for services (not users)
 *    - Principle of least privilege
 * 
 * 3. EBS:
 *    - Persistent block storage
 *    - Multiple volume types (gp3, io1/io2, etc.)
 *    - Snapshots for backups
 * 
 * 4. S3:
 *    - Object storage service
 *    - Unlimited capacity
 *    - Multiple storage classes
 *    - Versioning and lifecycle policies
 * 
 * 5. ELB:
 *    - Distributes traffic
 *    - ALB (Layer 7), NLB (Layer 4)
 *    - Health checks and auto scaling integration
 * 
 * Interview Tips:
 * - Understand the use cases for each service
 * - Know the differences between similar services (EBS vs S3, ALB vs NLB)
 * - Understand pricing models
 * - Know best practices (security, cost optimization, high availability)
 */

