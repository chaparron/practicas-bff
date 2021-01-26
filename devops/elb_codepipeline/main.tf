resource "aws_s3_bucket" "artifacts_bucket" {
  bucket_prefix = "${lower(replace(var.name, "_", "-"))}-pipelinestore-"
  force_destroy = true
}

resource "aws_iam_role" "bff-codepipeline-role" {
  name_prefix = "${var.name}_codepipeline_"

  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Service": [
          "codepipeline.amazonaws.com",
          "events.amazonaws.com",
          "s3.amazonaws.com"

        ]
      },
      "Action": "sts:AssumeRole"
    }
  ]
}
EOF

}

resource "aws_iam_role_policy" "policy" {
  role = aws_iam_role.bff-codepipeline-role.id

  policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect":"Allow",
      "Action": [
        "s3:GetObject",
        "s3:GetObjectAcl",
        "s3:GetBucketPolicy",
        "s3:GetBucketLocation",
        "s3:ListBucket",
        "s3:GetObjectVersion",
        "s3:GetBucketVersioning",
        "s3:PutObject",
        "s3:PutObjectAcl"
      ],
      "Resource": [
        "${aws_s3_bucket.artifacts_bucket.arn}",
        "${aws_s3_bucket.artifacts_bucket.arn}/*"
      ]
    },
    {
      "Effect": "Allow",
      "Action": [
        "codebuild:BatchGetBuilds",
        "codebuild:StartBuild"
      ],
      "Resource": "*"
    },
    {
      "Effect":"Allow",
      "Action": [
        "codecommit:GetBranch",
        "codecommit:GetCommit",
        "codecommit:UploadArchive",
        "codecommit:GetUploadArchiveStatus"
      ],
      "Resource": "*"
    },
    {
      "Effect": "Allow",
      "Action": [ "codepipeline:StartPipelineExecution" ],
      "Resource": "${aws_codepipeline.bff-codepipeline.arn}"
    },
    {
      "Effect": "Allow",
      "Action": [
        "ec2:DescribeInstances",
        "ec2:DescribeInstanceStatus",
        "ec2:GetConsoleOutput",
        "ec2:AssociateAddress",
        "ec2:DescribeAddresses",
        "ec2:DescribeSecurityGroups",
        "ec2:describeVpcs",
        "ec2:DescribeImages",
        "sqs:GetQueueAttributes",
        "sqs:GetQueueUrl",
        "autoscaling:DescribeAutoScalingGroups",
        "autoscaling:DescribeAutoScalingInstances",
        "autoscaling:DescribeScalingActivities",
        "autoscaling:DescribeNotificationConfigurations"
      ],
      "Resource": [
        "*"
      ]
    },
    {
      "Effect": "Allow",
      "Action": [
        "elasticbeanstalk:CreateApplicationVersion",
        "elasticbeanstalk:DescribeApplications",
        "elasticbeanstalk:DescribeApplicationVersions",
        "elasticbeanstalk:DescribeEnvironments",
        "elasticbeanstalk:DescribeEvents",
        "elasticbeanstalk:DeleteApplicationVersion",
        "elasticbeanstalk:UpdateEnvironment",
        "elasticbeanstalk:CreateStorageLocation",
        "elasticbeanstalk:DescribeEvents",
        "s3:ListAllMyBuckets"
      ],
      "Resource": [
        "*"
      ]
    },
    {
      "Action": [
        "autoscaling:SuspendProcesses",
        "autoscaling:DescribeScalingActivities",
        "autoscaling:ResumeProcesses",
        "autoscaling:DescribeAutoScalingGroups",
        "autoscaling:DescribeLaunchConfigurations",
        "autoscaling:PutNotificationConfiguration"
      ],
      "Effect": "Allow",
      "Resource": "*"
    }
  ]
}
EOF

}


resource "aws_iam_role_policy_attachment" "elasticbeanstalk-attach-bff-1" {
    role = aws_iam_role.bff-codepipeline-role.name
    policy_arn = "arn:aws:iam::aws:policy/AWSElasticBeanstalkFullAccess"
}

resource "aws_codepipeline" "bff-codepipeline" {
  name     = "${var.name}_${terraform.workspace}_codepipeline"
  role_arn = aws_iam_role.bff-codepipeline-role.arn

  artifact_store {
    location = aws_s3_bucket.artifacts_bucket.bucket
    type     = "S3"
  }

  stage {
    name = "Source"

    action {
      version          = "1"
      name             = "Source"
      category         = "Source"
      owner            = "AWS"
      provider         = "CodeCommit"
      output_artifacts = ["source"]

      configuration = {
        PollForSourceChanges = false
        RepositoryName       = var.repository_name
        BranchName           = var.repository_branch
      }
    }
  }

  stage {
    name = "Build"

    action {
      version         = "1"
      name            = "Build"
      category        = "Build"
      owner           = "AWS"
      provider        = "CodeBuild"
      input_artifacts = ["source"]

      output_artifacts = ["dist"]

      configuration = {
        ProjectName = var.build_step
      }
    }
  }

  stage {
    name = "Deploy"

    action {
      version         = "1"
      name            = "Deploy"
      category        = "Deploy"
      owner           = "AWS"
      provider        = "ElasticBeanstalk"
      input_artifacts = ["dist"]


      configuration = {
        ApplicationName = var.name
        EnvironmentName = var.environment_name
      }
    }
  }
}

data "aws_codecommit_repository" "codecommit" {
  repository_name = var.repository_name
}

resource "aws_cloudwatch_event_rule" "scm_change" {
  name_prefix = "${var.name}_scm_change_"

  event_pattern = <<PATTERN
{
  "source": [ "aws.codecommit" ],
  "detail-type": [ "CodeCommit Repository State Change" ],
  "resources": [ "${data.aws_codecommit_repository.codecommit.arn}" ],
  "detail": {
    "event": [ "referenceCreated", "referenceUpdated" ],
    "referenceType": [ "branch" ],
    "referenceName": [ "${var.repository_branch}" ]
  }
}
PATTERN

}

resource "aws_cloudwatch_event_target" "scm_change_target" {
  rule     = aws_cloudwatch_event_rule.scm_change.name
  arn      = aws_codepipeline.bff-codepipeline.arn
  role_arn = aws_iam_role.bff-codepipeline-role.arn
}

