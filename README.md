Sistema de Microservicios Coffee

Descripción del proyecto

Este proyecto corresponde a una arquitectura basada en microservicios desarrollada con Spring Boot. El sistema simula la gestión de una cafetería, integrando servicios independientes para pedidos, cocina y cupones, además de un API Gateway como punto único de entrada y Eureka Server para el descubrimiento de servicios.

El objetivo principal es aplicar una arquitectura distribuida donde cada microservicio tenga responsabilidades separadas, comunicación REST, documentación Swagger/OpenAPI, enlaces HATEOAS, pruebas unitarias, contenedores Docker y despliegue local mediante Docker Compose.

---

Integrantes:

- Eydan Pérez
- Lucas Tapia
- Leonardo Matinez

---

Arquitectura general

La arquitectura se compone de los siguientes servicios:

Servicio| Puerto| Descripción
ms-gateway| 8080| Punto único de entrada para consumir los microservicios
ms-eureka| 8761| Servidor de descubrimiento de servicios
ms-pedidos| 8081| Gestión de pedidos realizados por clientes
ms-cocina| 8085| Gestión de tickets de cocina asociados a pedidos
ms-cupones| 8089| Gestión, validación y aplicación de cupones de descuento
mysql-pedidos| 3307| Base de datos MySQL para pedidos
mysql-cocina| 3308| Base de datos MySQL para cocina
mysql-cupones| 3309| Base de datos MySQL para cupones

---

Tecnologías utilizadas

- Java 17
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Validation
- Spring Cloud Gateway
- Spring Cloud Netflix Eureka
- Springdoc OpenAPI / Swagger
- HATEOAS
- WebClient
- MySQL
- Flyway
- Docker
- Docker Compose
- Maven
- JUnit
- Mockito
- GitHub

---

Microservicios principales

ms-pedidos

Microservicio encargado de gestionar los pedidos de los clientes.

Funciones principales:

- Crear pedidos.
- Listar pedidos.
- Buscar pedido por ID.
- Actualizar estado del pedido.
- Eliminar pedido.
- Aplicar cupones mediante comunicación con "ms-cupones".

Endpoints principales:

GET    /api/pedidos
GET    /api/pedidos/{id}
POST   /api/pedidos
PUT    /api/pedidos/{id}/estado
DELETE /api/pedidos/{id}

---

ms-cocina

Microservicio encargado de gestionar los tickets de cocina generados a partir de los pedidos.

Funciones principales:

- Crear ticket de cocina.
- Listar tickets.
- Buscar ticket por ID.
- Buscar tickets por pedido.
- Buscar tickets por estado.
- Actualizar estado del ticket.
- Agregar observaciones.
- Eliminar ticket.

Endpoints principales:

GET    /api/cocina
GET    /api/cocina/{id}
GET    /api/cocina/pedido/{pedidoId}
GET    /api/cocina/estado/{estado}
POST   /api/cocina
PUT    /api/cocina/{id}/estado
PUT    /api/cocina/{id}/observacion
DELETE /api/cocina/{id}

---

ms-cupones

Microservicio encargado de administrar cupones de descuento.

Funciones principales:

- Crear cupones.
- Listar cupones.
- Buscar cupón por ID.
- Buscar cupón por código.
- Actualizar cupón.
- Cambiar estado del cupón.
- Validar cupón.
- Aplicar cupón a un monto.
- Eliminar cupón.

Endpoints principales:

GET    /api/cupones
GET    /api/cupones/{id}
GET    /api/cupones/codigo/{codigo}
POST   /api/cupones
PUT    /api/cupones/{id}
PUT    /api/cupones/{id}/estado
POST   /api/cupones/validar
POST   /api/cupones/aplicar
DELETE /api/cupones/{id}

---

API Gateway

El servicio "ms-gateway" centraliza el acceso a los microservicios.

Rutas configuradas:

http://localhost:8080/api/pedidos   -> ms-pedidos
http://localhost:8080/api/cocina    -> ms-cocina
http://localhost:8080/api/cupones   -> ms-cupones

El Gateway utiliza Eureka y balanceo mediante rutas con:

lb://ms-pedidos
lb://ms-cocina
lb://ms-cupones

---

Eureka Server

El servicio "ms-eureka" permite registrar y descubrir microservicios dentro del sistema.

URL local:

http://localhost:8761

Servicios esperados en Eureka:

MS-PEDIDOS
MS-COCINA
MS-CUPONES
MS-GATEWAY

---

Documentación Swagger

Cada microservicio cuenta con documentación Swagger/OpenAPI.

URLs locales:

http://localhost:8081/swagger
http://localhost:8085/swagger
http://localhost:8089/swagger

También se puede acceder mediante:

http://localhost:8081/swagger-ui/index.html
http://localhost:8085/swagger-ui/index.html
http://localhost:8089/swagger-ui/index.html

JSON OpenAPI:

http://localhost:8081/v3/api-docs
http://localhost:8085/v3/api-docs
http://localhost:8089/v3/api-docs

---

HATEOAS

Los endpoints de búsqueda por ID incluyen enlaces HATEOAS para facilitar la navegación entre recursos.

Ejemplo esperado en la respuesta:

"_links": {
  "self": {
    "href": "http://localhost:8081/api/pedidos/1"
  },
  "all": {
    "href": "http://localhost:8081/api/pedidos"
  },
  "update": {
    "href": "http://localhost:8081/api/pedidos/1/estado?estado=PAGADO"
  },
  "delete": {
    "href": "http://localhost:8081/api/pedidos/1"
  }
}

---

Comunicación entre microservicios

La comunicación entre microservicios se realiza mediante llamadas REST.

Ejemplos:

- "ms-pedidos" se comunica con "ms-cupones" para aplicar descuentos.
- "ms-cocina" se comunica con "ms-pedidos" para validar pedidos existentes.
- "ms-gateway" redirige las peticiones a los microservicios correspondientes.
- "ms-eureka" permite el descubrimiento de servicios registrados.

---

Bases de datos

Cada microservicio con lógica de negocio tiene su propia base de datos.

Microservicio| Base de datos
ms-pedidos| pedidos_coffee
ms-cocina| cocina_coffee
ms-cupones| cupones_coffee

Las tablas son gestionadas mediante scripts de migración Flyway.

---

Variables de entorno

Los microservicios están preparados para ejecutarse con variables de entorno.

Ejemplo:

server.port=${PORT:8081}

spring.datasource.url=jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:pedidos_coffee}
spring.datasource.username=${DB_USER:root}
spring.datasource.password=${DB_PASSWORD:}

eureka.client.service-url.defaultZone=${EUREKA_URL:http://localhost:8761/eureka/}

Esto permite ejecutar el proyecto tanto localmente como en Docker o en servicios cloud.

---

Ejecución local sin Docker

Requisitos:

- Java 17
- Maven
- MySQL o Laragon
- Bases de datos creadas localmente
- Puertos disponibles

Bases de datos necesarias:

CREATE DATABASE pedidos_coffee;
CREATE DATABASE cocina_coffee;
CREATE DATABASE cupones_coffee;

Orden recomendado de ejecución:

1. ms-eureka
2. ms-pedidos
3. ms-cupones
4. ms-cocina
5. ms-gateway

Comando para ejecutar cada microservicio:

mvn spring-boot:run

O si existe Maven Wrapper:

.\mvnw spring-boot:run

---

Ejecución con Docker Compose

Requisitos:

- Docker Desktop
- Docker Compose

Desde la raíz del proyecto ejecutar:

docker compose up --build

Para levantar en segundo plano:

docker compose up -d --build

Para detener los contenedores:

docker compose down

Para detener y eliminar volúmenes de base de datos:

docker compose down -v

---

Contenedores definidos

El archivo "docker-compose.yml" levanta los siguientes contenedores:

mysql-pedidos
mysql-cocina
mysql-cupones
ms-eureka
ms-pedidos
ms-cocina
ms-cupones
ms-gateway

---

Pruebas

El proyecto incluye pruebas unitarias para controladores, servicios y repositorios.

Comando para ejecutar pruebas:

mvn clean test

Si el proyecto tiene Maven Wrapper:

.\mvnw clean test

Para generar reporte de cobertura con JaCoCo:

mvn clean test

Reporte esperado:

target/site/jacoco/index.html

---

Evidencias de funcionamiento

Se recomienda verificar el sistema mediante:

Eureka:

http://localhost:8761

Gateway:

http://localhost:8080/api/pedidos
http://localhost:8080/api/cocina
http://localhost:8080/api/cupones

Swagger:

http://localhost:8081/swagger
http://localhost:8085/swagger
http://localhost:8089/swagger

Docker:

docker ps

---

Estructura del proyecto

proye
├── docker-compose.yml
├── README.md
├── ms-eureka
│   ├── Dockerfile
│   ├── pom.xml
│   └── src
├── ms-gateway
│   ├── Dockerfile
│   ├── pom.xml
│   └── src
├── ms-pedidos
│   ├── Dockerfile
│   ├── pom.xml
│   └── src
├── ms-cocina
│   ├── Dockerfile
│   ├── pom.xml
│   └── src
└── ms-cupones
    ├── Dockerfile
    ├── pom.xml
    └── src

---

Comandos útiles de Docker

Ver contenedores activos:

docker ps

Ver logs de un servicio:

docker logs ms-pedidos
docker logs ms-cocina
docker logs ms-cupones
docker logs ms-gateway
docker logs ms-eureka

Reiniciar un servicio:

docker restart ms-pedidos

Reconstruir imágenes sin caché:

docker compose build --no-cache

---

Estado del proyecto

Funcionalidades implementadas:

- Arquitectura basada en microservicios.
- API Gateway centralizado.
- Eureka Server para descubrimiento de servicios.
- Comunicación REST entre microservicios.
- Documentación Swagger/OpenAPI.
- HATEOAS en recursos principales.
- Persistencia con MySQL.
- Migraciones con Flyway.
- Dockerfile por microservicio.
- Docker Compose para levantar el ecosistema completo.
- Configuración mediante variables de entorno.
- Pruebas unitarias.

---

Defensa técnica

Puntos clave para explicar:

- Cada microservicio tiene una responsabilidad específica.
- El Gateway centraliza las peticiones del cliente.
- Eureka permite descubrir servicios sin depender directamente del puerto físico.
- Docker Compose permite levantar bases de datos y microservicios con un solo comando.
- Las variables de entorno permiten ejecutar el sistema en distintos ambientes.
- Swagger documenta y permite probar los endpoints.
- HATEOAS agrega enlaces de navegación a las respuestas REST.
- Flyway gestiona la creación y evolución de las tablas.
- Las pruebas unitarias validan controladores, servicios y repositorios.

---

Autor

Proyecto desarrollado para evaluación académica de microservicios.

Institución: DUOC UC
Carrera: Ingeniería en Informática
Asignatura: Desarrollo de Microservicios