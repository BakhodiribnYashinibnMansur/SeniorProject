# Terraform Roadmap

- Roadmap: https://roadmap.sh/terraform
- PDF: [terraform.pdf](./terraform.pdf)

## 1. Introduction
- What is Infrastructure as Code?
- What is Terraform?
- Usecases and Benefits
- CaC vs IaC
- Installing Terraform

## 2. Getting Started

### 2.1 Hashicorp Config Language (HCL)
- What is HCL?
- Basic Syntax

### 2.2 Project Initialization

## 3. Providers
- Terraform Registry
- Configuring Providers
- Versions

## 4. Resources
- Resource Behavior
- Resource Lifecycle
- Meta Arguments
  - depends_on
  - count
  - for_each
  - provider
  - lifecycle

## 5. Variables
- Input Variables
- Type Constraints
- Variable Definition File
- Local Values
- Environment Variables
- Validation Rules

## 6. Outputs
- Output Syntax
- Sensitive Outputs
- Preconditions

## 7. Format & Validate
- terraform fmt
- terraform validate
- TFLint

## 8. State Management

### 8.1 State
- Sensitive Data
- Versioning
- Splitting State Files
- Import Existing Resources
- State Locking
- Remote State

### 8.2 Best Practices for State

### 8.3 Inspect / Modify State
- graph
- show
- list
- output
- rm
- mv
- -replace option in apply
- state pull / push
- state replace-provider
- state force-unlock

## 9. Deployment
- terraform plan
- terraform apply

## 10. Clean Up
- terraform destroy

## 11. Modules
- Root vs Child Modules
- Published Modules Usage
- Creating Local Modules
- Inputs / Outputs
- Modules Best Practices

## 12. Provisioners
- When to Use?
- Creation / Destroy Time
- file provisioner
- local-exec provisioner
- remote-exec provisioner
- Custom Provisioners

## 13. Data Sources

## 14. Template Files

## 15. Workspaces

## 16. CI / CD Integration
- GitHub Actions
- Circle CI
- GitLab CI
- Jenkins

## 17. Testing
- Unit Testing
- Contract Testing
- Integration Testing
- End to End Testing
- Testing Modules

## 18. Scaling Terraform
- Splitting Large State
- Parallelism
- Deployment Workflow
- Version Management
- Terragrunt
- Infracost

## 19. Security
- Secret Management
- Compliance / Sentinel
- Terrascan
- Checkov
- Trivy
- KICS

## 20. HCP
- What and when to use HCP?
- Enterprise Features
- Authentication
- Workspaces
- VCS Integration
- Run Tasks
