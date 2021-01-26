provider "aws" {
  version = "~> 3.0"
  region = var.region
}

provider "template" {
  version = "~> 2.1.2"
}

provider "random" {
  version = "~> 2.3"
}

provider "external" {
  version = "~> 1.1"
}

terraform {
  backend "s3" {
    bucket         = "wabi2b-terraform-state-store"
    key            = "bff"
    region         = "us-west-2"
    dynamodb_table = "terraform_lock"
  }
}

data "terraform_remote_state" "foundation" {
  backend   = "s3"
  workspace = terraform.workspace

  config = {
    bucket = "wabi2b-terraform-state-store"
    key    = "foundation"
    region = "us-west-2"
  }
}

data "terraform_remote_state" "repositories" {
  backend   = "s3"

  config = {
    bucket = "wabi2b-terraform-state-store"
    key    = "repositories"
    region = "us-west-2"
  }
}


locals {
  name  = "bff-${terraform.workspace}"
}

data "template_file" "build_buildspec" {
  template = file("${path.module}/build.yml")
}

resource "random_pet" "codebuild_name" {
}

resource "aws_codebuild_project" "build" {
  name          = "${local.name}-build-${random_pet.codebuild_name.id}"
  build_timeout = "10"

  cache {
    type     = "S3"
    location = "${data.terraform_remote_state.foundation.outputs.codebuild_cache}/${local.name}"
  }
  service_role = data.terraform_remote_state.foundation.outputs.codebuild_role

  environment {
    compute_type = "BUILD_GENERAL1_SMALL"
    image        = "aws/codebuild/amazonlinux2-x86_64-standard:3.0"
    type         = "LINUX_CONTAINER"
  }

  artifacts {
    type = "CODEPIPELINE"
  }

  source {
    type      = "CODEPIPELINE"
    buildspec = data.template_file.build_buildspec.rendered
  }
}

module "codepipeline" {
  source            = "./elb_codepipeline"
  name              = "wabi2b-bff-app"
  environment_name  = "bff-${terraform.workspace == "prod" ?  "prod": "dev"}"
  repository_name   = data.terraform_remote_state.repositories.outputs.bff
  repository_branch = terraform.workspace == "prod" ? "master" : "develop"
  build_step        = aws_codebuild_project.build.id
}