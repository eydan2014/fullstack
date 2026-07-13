# Sistema de Microservicios Coffee

## Descripción del proyecto

Este proyecto corresponde a una arquitectura basada en microservicios desarrollada con Spring Boot, para la evaluación **EFT — DSY1103 Desarrollo FullStack 1**. El sistema simula la gestión integral de una cafetería: gestión de usuarios, menú, pedidos, cocina, pagos, cupones de descuento, fidelización de clientes, inventario, reseñas y avisos. Toda la arquitectura está centralizada mediante un **API Gateway** y un **Eureka Server** para el descubrimiento de servicios.

El objetivo principal es aplicar una arquitectura distribuida donde cada microservicio tenga responsabilidades separadas (patrón CSR), comunicación REST entre servicios, documentación Swagger/OpenAPI, persistencia real con JPA + Hibernate, validaciones, manejo centralizado de errores, pruebas unitarias, contenedores Docker y despliegue mediante Docker Compose.

---

## Integrantes

- Eydan Pérez
- Leonardo Martínez

---

## Arquitectura general

El sistema está compuesto por **10 microservicios de negocio** más el **Gateway** y el **Servidor de descubrimiento (Eureka)**:

| Servicio | Puerto | Base de datos | Descripción |
|---|---|---|---|
| ms-gateway | 8080 | — | Punto único de entrada para consumir todos los microservicios |
| ms-eureka | 8761 | — | Servidor de descubrimiento de servicios |
| ms-pedidos | 8081 | pedidos_coffee | Gestión de pedidos realizados por clientes |
| user | 8082 | ms-usuario | Registro, autenticación y validación de usuarios (JWT) |
| menu | 8083 | db_menu | Gestión de productos/menú de la cafetería |
| pago | 8084 | db_pagos | Procesamiento de pagos asociados a pedidos |
| ms-cocina | 8085 | cocina_coffee | Gestión de tickets de cocina asociados a pedidos |
| inventario | 8086 | db_inventario | Gestión de insumos e inventario |
| fidelidad | 8087 | db_fidelidad | Programa de puntos y fidelización de clientes |
| aviso | 8088 | db_aviso | Envío y gestión de avisos/notificaciones |
| ms-cupones | 8089 | cupones_coffee | Gestión, validación y aplicación de cupones de descuento |
| resena | 8090 | db_resena | Reseñas de productos realizadas por clientes |

Todos los servicios comparten un mismo motor MySQL (contenedor `mysql`), con **una base de datos/esquema independiente por microservicio**, inicializado vía `mysql-init/init.sql` cuando se usa Docker Compose.

---

## Tecnologías utilizadas

- Java 17 / Spring Boot
- Spring Web / Spring Data JPA
- Spring Validation (Bean Validation JSR 380)
- Spring Cloud Gateway
- Spring Cloud Netflix Eureka
- Spring Security + JWT (en los servicios que lo requieren)
- Springdoc OpenAPI / Swagger
- WebClient
- MySQL
- Flyway
- Docker / Docker Compose
- Maven
- JUnit / Mockito
- GitHub

---

## Microservicios y endpoints principales

### ms-pedidos (8081)
Gestión de pedidos de clientes.
```
GET    /api/pedidos
GET    /api/pedidos/{id}
POST   /api/pedidos
PUT    /api/pedidos/{id}/estado
DELETE /api/pedidos/{id}
```

### user (8082)
Registro, login y validación de usuarios.
```
POST /api/auth/register
POST /api/auth/login
POST /api/auth/refresh
GET  /api/auth/{username}/existe
GET  /api/auth/id/{id}/existe
```

### menu (8083)
Catálogo de productos de la cafetería.
```
GET    /api/productos
GET    /api/productos/{id}
POST   /api/productos
PUT    /api/productos/{id}
DELETE /api/productos/{id}
GET    /api/productos/{id}/precio
GET    /api/productos/{id}/existe
```

### pago (8084)
Procesamiento de pagos de pedidos.
```
POST /api/pagos/realizar_pago
GET  /api/pagos/{id}
```

### ms-cocina (8085)
Tickets de cocina generados a partir de los pedidos.
```
GET    /api/cocina
GET    /api/cocina/{id}
GET    /api/cocina/pedido/{pedidoId}
GET    /api/cocina/estado/{estado}
POST   /api/cocina
PUT    /api/cocina/{id}/estado
PUT    /api/cocina/{id}/observacion
DELETE /api/cocina/{id}
```

### inventario (8086)
Gestión de insumos.
```
POST /api/inventario/actualizar
```

### fidelidad (8087)
Puntos de fidelización por cliente.
```
POST /fidelidad/acreditar
GET  /fidelidad/{usuario}
```

### aviso (8088)
Envío de notificaciones/avisos.
```
POST /api/avisos/enviar
```

### ms-cupones (8089)
Cupones de descuento.
```
GET    /api/cupones
GET    /api/cupones/{id}
GET    /api/cupones/codigo/{codigo}
POST   /api/cupones
PUT    /api/cupones/{id}
PUT    /api/cupones/{id}/estado
POST   /api/cupones/validar
POST   /api/cupones/aplicar
DELETE /api/cupones/{id}
```

### resena (8090)
Reseñas de productos.
```
POST /api/resenas/crear
GET  /api/resenas/producto/{idProducto}
```

---

## API Gateway

El servicio `ms-gateway` centraliza el acceso a los 10 microservicios de negocio mediante rutas balanceadas por Eureka (`lb://`):

```
http://localhost:8080/api/pedidos    -> ms-pedidos
http://localhost:8080/api/auth       -> user
http://localhost:8080/api/productos  -> menu
http://localhost:8080/api/pagos      -> pago
http://localhost:8080/api/cocina     -> ms-cocina
http://localhost:8080/api/inventario -> inventario
http://localhost:8080/fidelidad      -> fidelidad
http://localhost:8080/api/avisos     -> aviso
http://localhost:8080/api/cupones    -> ms-cupones
http://localhost:8080/api/resenas    -> resena
```

Además, el Gateway agrega rutas de agregación de documentación (`/docs/{servicio}/v3/api-docs`) para centralizar el acceso a la especificación OpenAPI de cada microservicio.

---

## Eureka Server

El servicio `ms-eureka` permite registrar y descubrir los microservicios del sistema.

URL local: `http://localhost:8761`

Servicios esperados registrados en Eureka: `MS-PEDIDOS`, `USER`, `MENU`, `PAGO`, `MS-COCINA`, `INVENTARIO`, `FIDELIDAD`, `AVISO`, `MS-CUPONES`, `RESENA`, `MS-GATEWAY`.

---

## Documentación Swagger

Cada microservicio expone su documentación en `/swagger` (UI) y `/v3/api-docs` (JSON), por ejemplo:

```
http://localhost:8081/swagger   (ms-pedidos)
http://localhost:8085/swagger   (ms-cocina)
http://localhost:8089/swagger   (ms-cupones)
...
```

También se puede acceder a la documentación agregada de cada servicio a través del Gateway en `http://localhost:8080/docs/{servicio}/v3/api-docs`.

---

## Comunicación entre microservicios

La comunicación entre microservicios se realiza mediante llamadas REST, resolviendo los nombres lógicos (`spring.application.name`) a través de Eureka:

- `ms-cocina` se comunica con `ms-pedidos` (WebClient) para validar que el pedido exista antes de crear un ticket.
- `pago` se comunica con `ms-pedidos` y `menu` (RestTemplate) para validar pedidos y precios de productos.
- `aviso` y `fidelidad` se comunican con `user` (RestTemplate) para validar la existencia del usuario.
- `resena` se comunica con `menu` y `user` (RestTemplate) para validar producto y usuario.
- `ms-gateway` redirige y balancea las peticiones hacia todos los microservicios registrados en Eureka.

---

## Variables de entorno

Ejemplo (aplica a todos los microservicios, con su propio nombre de base de datos y puerto):

```
server.port=${PORT:8081}

spring.datasource.url=jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:pedidos_coffee}
spring.datasource.username=${DB_USER:root}
spring.datasource.password=${DB_PASSWORD:}

eureka.client.service-url.defaultZone=${EUREKA_URL:http://localhost:8761/eureka/}
```

Esto permite ejecutar el proyecto tanto localmente como en Docker o en servicios cloud (Railway, Render, etc.).

---

## Ejecución local sin Docker

Requisitos:
- Java 17
- Maven
- MySQL local (o Laragon)
- Puertos disponibles: 8080–8090, 8761, 3306

Bases de datos necesarias:
```sql
CREATE DATABASE pedidos_coffee;
CREATE DATABASE `ms-usuario`;
CREATE DATABASE db_menu;
CREATE DATABASE db_pagos;
CREATE DATABASE cocina_coffee;
CREATE DATABASE db_inventario;
CREATE DATABASE db_fidelidad;
CREATE DATABASE db_aviso;
CREATE DATABASE cupones_coffee;
CREATE DATABASE db_resena;
```

Orden recomendado de ejecución:
1. `ms-eureka`
2. `user`, `menu` (servicios sin dependencias de otros)
3. `ms-pedidos`, `ms-cupones`, `inventario`
4. `pago`, `ms-cocina`, `fidelidad`, `aviso`, `resena` (dependen de los anteriores)
5. `ms-gateway`

Comando para ejecutar cada microservicio:
```
mvn spring-boot:run
```
O si existe Maven Wrapper:
```
./mvnw spring-boot:run
```

---

## Ejecución con Docker Compose

Requisitos:
- Docker Desktop
- Docker Compose

Desde la raíz del proyecto:
```
docker compose up --build
```
En segundo plano:
```
docker compose up -d --build
```
Detener contenedores:
```
docker compose down
```
Detener y eliminar volúmenes de base de datos:
```
docker compose down -v
```

El archivo `docker-compose.yml` levanta los siguientes contenedores: `mysql`, `ms-eureka`, `ms-gateway`, `ms-pedidos`, `user`, `menu`, `pago`, `ms-cocina`, `inventario`, `fidelidad`, `aviso`, `ms-cupones`, `resena`.

---

## Pruebas

El proyecto incluye pruebas unitarias para controladores, servicios y repositorios en cada microservicio.

Comando para ejecutar las pruebas de un microservicio:
```
mvn clean test
```

---

## Estructura del proyecto

```
fullstack-main/
├── docker-compose.yml
├── mysql-init/
│   └── init.sql
├── README.md
├── ms-eureka/
├── ms-gateway/
├── ms-pedidos/
├── user/
├── menu/
├── pago/
├── ms-cocina/
├── inventario/
├── fidelidad/
├── aviso/
├── ms-cupones/
└── resena/
```

Cada carpeta de microservicio sigue la misma estructura interna (patrón CSR):
```
src/main/java/com/example/<servicio>/
├── controller/
├── service/
├── repository/
├── model/ (entidades JPA)
├── dto/
├── client/      (si consume otros microservicios)
├── config/      (Swagger, WebClient/RestTemplate, seguridad)
└── exception/   (GlobalExceptionHandler)
```

---

## Estado del proyecto

Funcionalidades implementadas:
- Arquitectura de 10 microservicios de negocio + Gateway + Eureka.
- Patrón CSR (Controller–Service–Repository) en todos los servicios.
- Persistencia con JPA/Hibernate + MySQL, migraciones con Flyway.
- Validaciones con Bean Validation (JSR 380).
- Manejo centralizado de excepciones (`@ControllerAdvice`).
- Comunicación entre microservicios vía REST (WebClient/RestTemplate) balanceada con Eureka.
- Documentación Swagger/OpenAPI por servicio, agregada a través del Gateway.
- Pruebas unitarias con JUnit y Mockito.
- Dockerfile por microservicio y Docker Compose para levantar el ecosistema completo.
- Configuración mediante variables de entorno (perfiles local/Docker).

---

**Institución:** DUOC UC
**Carrera:** Ingeniería en Informática
**Asignatura:** DSY1103 — Desarrollo FullStack 1
