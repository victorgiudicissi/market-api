![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Swagger](https://img.shields.io/badge/-Swagger-%23Clojure?style=for-the-badge&logo=swagger&logoColor=white)
![MongoDB](https://img.shields.io/badge/MongoDB-%234ea94b.svg?style=for-the-badge&logo=mongodb&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/github%20actions-%232671E5.svg?style=for-the-badge&logo=githubactions&logoColor=white)
# MARKET-API

This application in focused in simulate a market, covering the following contexts:

- Items
- Market Data
- Cart

## Prerequisites

- [Jabba](https://github.com/shyiko/jabba) (Optional) - It's a Java Version Manager
- [Conduktor](https://www.conduktor.io/get-started/) (Optional)
- Java 11
- [Docker](https://docs.docker.com/get-started/overview/)
- [MongoDB](https://www.mongodb.com/docs/manual/)

## Run mongoDB

We are using mongoDB as our database, so if you want to run it locally, you just need to run this command:

`docker run -it -p 27017:27017 mongo`

## Run API

After setting up DB, we can execute the API running the following command:

`./gradlew bootRun`

## Swagger

You can see API configuration going to:

`http://localhost:8080/swagger-ui/index.html`

## Tests

To run unit tests, you can execute the following command:

`./gradlew test`

To generate coverage reports:

`./gradlew test jacocoTestReport `

## Docker-compose

Use docker-compose to simplify the setup of your development environment:

`docker-compose up`
