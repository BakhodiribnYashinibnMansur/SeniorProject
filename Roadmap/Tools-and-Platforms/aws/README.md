# AWS Roadmap

- Roadmap: https://roadmap.sh/aws

## 1. Introduction
- 1.1 What is Cloud Computing?
- 1.2 IaaS vs PaaS vs SaaS
- 1.3 Public vs Private vs Hybrid Cloud
- 1.4 Introduction to AWS
- 1.5 AWS Global Infrastructure
- 1.6 Shared Responsibility Model
- 1.7 Well Architected Framework

## 2. Start with Essential Services

### 2.1 EC2
- 2.1.1 Instance Types
- 2.1.2 CPU Credits
- 2.1.3 Storage / Volumes
- 2.1.4 Keypairs
- 2.1.5 Elastic IP
- 2.1.6 User Data Scripts
- 2.1.7 Purchasing Options

### 2.2 VPC
- 2.2.1 CIDR Blocks
- 2.2.2 Subnets
  - 2.2.2.1 Private Subnet
  - 2.2.2.2 Public Subnet
- 2.2.3 Route Tables
- 2.2.4 Security Groups
- 2.2.5 Internet Gateway
- 2.2.6 NAT Gateway

### 2.3 IAM
- 2.3.1 Policies
  - 2.3.1.1 Identity-based
  - 2.3.1.2 Resource-based
- 2.3.2 Users / User Groups
- 2.3.3 Roles
  - 2.3.3.1 Instance Profiles
  - 2.3.3.2 Assuming Roles

## 3. Auto-Scaling
- 3.1 AMIs
- 3.2 Launch Templates
- 3.3 Auto-Scaling Groups
- 3.4 Scaling Policies
- 3.5 Elastic Load Balancers

## 4. S3
- 4.1 Buckets / Objects
- 4.2 Bucket / Object Lifecycle
- 4.3 Storage Types
  - 4.3.1 Standard
  - 4.3.2 S3-IA
  - 4.3.3 Glacier

## 5. SES
- 5.1 Sandbox / Sending Limits
- 5.2 Identity Verification
- 5.3 DKIM Setup
- 5.4 Feedback Handling
- 5.5 Configuration Sets
- 5.6 Sender Reputation
- 5.7 Dedicated IP

## 6. Route53
- 6.1 Hosted Zones
- 6.2 Routing Policies
- 6.3 Health Checks

## 7. Cloudwatch
- 7.1 Metrics
- 7.2 Events
- 7.3 Logs

## 8. Cloudfront
- 8.1 Distributions
- 8.2 Policies
- 8.3 Invalidations

## 9. RDS
- 9.1 DB Instances
- 9.2 Storage Types
  - 9.2.1 General Purpose
  - 9.2.2 Provisioned IOPS
  - 9.2.3 Magnetic
- 9.3 Backup / Restore

## 10. DynamoDB
- 10.1 Tables / Items / Attributes
- 10.2 Primary Keys / Secondary Indexes
- 10.3 Data Modeling
- 10.4 Streams
- 10.5 Capacity Settings
- 10.6 Limits
- 10.7 Backup / Restore
- 10.8 DynamoDB Local

## 11. ElastiCache
- 11.1 Quotas
- 11.2 ECR

## 12. ECS
- 12.1 Clusters / ECS Container Agents
- 12.2 Tasks
- 12.3 Services
- 12.4 Launch Config / Autoscaling Groups
- 12.5 Fargate

## 13. EKS

## 14. Lambda
- 14.1 Creating / Invoking Functions
- 14.2 Layers
- 14.3 Custom Runtimes
- 14.4 Versioning / Aliases
- 14.5 Event Bridge / Scheduled Execution
- 14.6 Cold Start and Limitations
- 14.7 API Gateway
- 14.8 Lambda@Edge
