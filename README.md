## Configuration for running bff on localhost

Edit Configurations for bff.Main </br>
Program Arguments:
```
--server.port=8082
--ssm=local
--spring.profiles.active=localstack,qa
```
### start localstack
```bash
$ docker compose up &
```
In case you want to use parameter store locally to add a property
```bash
AWS_PROFILE=local aws ssm put-parameter --name "/local/bff/<name of the property as you put it in @Value>" --value "true" --type "String" --region us-west-2 --endpoint-url=http://localhost:4566
```
###` stop localstack
