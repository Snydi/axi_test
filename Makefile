.PHONY: install build up start down stop restart logs ps test clean
.NOTPARALLEL: install

-include .env

COMPOSE := docker compose
WEB_PORT ?= 8080

install: build start

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
	$(COMPOSE) run --rm frontend npm run build

clean:
	$(COMPOSE) down --volumes --remove-orphans
