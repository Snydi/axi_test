.PHONY: install build up start down stop restart logs ps test clean db-migrate db-status db-validate db-rollback
.NOTPARALLEL: install

-include .env

COMPOSE := docker compose
WEB_PORT ?= 8080

install: build db-migrate start

build:
	$(COMPOSE) build

up:
	$(COMPOSE) up --build

start:
	$(COMPOSE) up --detach --wait --wait-timeout 120

down stop:
	$(COMPOSE) down --remove-orphans

restart: down start

test:
	$(COMPOSE) run --rm backend mvn test
	$(COMPOSE) run --rm frontend npm run test

db-migrate:
	$(COMPOSE) run --rm --build liquibase update

db-status:
	$(COMPOSE) run --rm --build liquibase status

db-validate:
	$(COMPOSE) run --rm --build liquibase validate

db-rollback:
	$(COMPOSE) run --rm --build liquibase rollback-count --count=1

clean:
	$(COMPOSE) down --volumes --remove-orphans
