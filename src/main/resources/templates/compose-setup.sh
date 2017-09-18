#!/bin/bash

check_mandatory_flags() {
  if [ -z "$PROFILE" ]; then
    echo "profile not set, use the -e flag." >&2
    usage
    exit 1
  fi

  if [ -z "$APP_PORT" ]; then
    echo "app port not set, use the -p flag." >&2
    usage
    exit 1
  fi

  if [ -z "$MYSQL_SERVER_IP" ]; then
    echo "mysql server ip not set, use the -s flag." >&2
    usage
    exit 1
  fi
}

create_environment_file() {
  printf "\n"
  echo "#############################"
  echo "# Creating environment file #"
  echo "#############################"
  printf "\n"

  # overwrite
  echo "PROFILE=${PROFILE}" > .env

  # append
  echo "MYSQL_SERVER_IP=${MYSQL_SERVER_IP}" >> .env
  echo "APP_PORT=${APP_PORT}" >> .env

  echo "------------- .env"
  cat .env
  echo "-------------"
  printf "\n"
}

upgrade_database_schema() {
  printf "\n"
  echo "#############################"
  echo "# Upgrading database schema #"
  echo "#############################"
  printf "\n"

  gradle \
    -Dorg.gradle.daemon=false \
    -PspringBootProfile=${PROFILE} \
    -PmysqlServerIP=${MYSQL_SERVER_IP} \
    flyway-gradle-cli:clean \
    flyway-gradle-cli:flywayMigrate && \
  printf "\n" && \
  echo "Upgrading complete!" && \
  printf "\n"
}

build() {
  printf "\n"
  echo "########################"
  echo "# Building application #"
  echo "########################"
  printf "\n"

  gradle -Dorg.gradle.daemon=false clean build -x test && \
  printf "\n" && \
  echo "Building complete!" && \
  printf "\n"
}

usage() {
  echo "Usage: $0 [-b] -e PROFILE -p APP_PORT -s MYSQL_SERVER_IP"
  echo "  -b                     Build images before starting containers"
  echo "  -e PROFILE             Profile to use, possible values are: docker-local, docker-dev, docker-prod"
  echo "  -p APP_PORT            Nginx port that will be exposed to"
  echo "  -s MYSQL_SERVER_IP     IP address of external mysql that services need connect to"
}

REBUILD=0
while getopts "be:p:s:" opt; do
  case "${opt}" in
    b)
      REBUILD=1
      ;;
    e)
      PROFILE=$OPTARG
      ;;
    p)
      APP_PORT=$OPTARG
      ;;
    s)
      MYSQL_SERVER_IP=$OPTARG
      ;;
    *)
      echo "Invalid option: -$OPTARG" >&2
      usage
      exit 1
      ;;
  esac
done

SCRIPT_DIR=`cd $(dirname $0) && pwd`

cd ${SCRIPT_DIR}

check_mandatory_flags
create_environment_file
upgrade_database_schema

COMPOSE_OPTS='-d'
if [ ${REBUILD} -eq 1 ] ; then
  COMPOSE_OPTS="${COMPOSE_OPTS} --build"
  build
fi

printf "\n"
echo "#########################"
echo "# Composing application #"
echo "#########################"
printf "\n"

docker-compose up ${COMPOSE_OPTS}

cat << EOM

###########
# SUCCESS #
###########

EOM

echo "Admin Console"
printf "\n"
echo "  http://host:${APP_PORT}/admin/"
printf "\n"

echo "Portal Docs"
printf "\n"
echo "  - Swagger UI"
echo "    http://host:${APP_PORT}/portal/swagger-ui.html"
printf "\n"
echo "  - Public API"
echo "    http://host:${APP_PORT}/portal/docs/public.html"
printf "\n"
echo "  - Private API"
echo "    http://host:${APP_PORT}/portal/docs/private.html"
