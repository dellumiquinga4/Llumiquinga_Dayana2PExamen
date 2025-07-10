# BanQuito - Microservicio General

## Descripción

Microservicio profesional para el sistema bancario BanQuito que gestiona cuentas bancarias y movimientos financieros con MongoDB. Implementa todas las buenas prácticas de arquitectura bancaria y cumple con estándares empresariales.

## Características Principales

### 🏦 Funcionalidades Bancarias
- **Gestión de Cuentas**: Crear, consultar, bloquear/desbloquear cuentas
- **Movimientos Bancarios**: Registrar débitos y créditos con validaciones
- **Consultas Avanzadas**: Búsquedas por cliente, fechas, tipos, montos
- **Validaciones de Negocio**: Saldo insuficiente, cuentas inactivas, límites
- **Auditoría Completa**: Logs detallados y trazabilidad

### 🚀 Arquitectura Profesional
- **Spring Boot 3.5.3** con Java 21
- **MongoDB** como base de datos NoSQL
- **MapStruct** para mapeo automático de DTOs
- **OpenAPI/Swagger** para documentación
- **Spring Cloud OpenFeign** para consumo de servicios
- **Paginación y Filtrado** en todas las consultas masivas
- **Validaciones Bean Validation** en DTOs
- **Manejo de Excepciones** personalizado

### 📋 Tipos de Cuenta Soportados
- **Cuentas de Ahorros** (AHORROS)
- **Cuentas Corrientes** (CORRIENTE)
- **Cuentas de Plazo Fijo** (PLAZO_FIJO)
- **Cuentas Vista** (VISTA)

### 🔒 Estados de Cuenta
- **ACTIVA**: Cuenta operativa
- **INACTIVA**: Sin movimientos recientes
- **BLOQUEADA**: Bloqueada por seguridad
- **CERRADA**: Cerrada definitivamente

## Arquitectura del Proyecto

```
src/main/java/com/banco/banquito/general/
├── config/             # Configuraciones (CORS, OpenAPI)
├── controller/         # Controladores REST
│   ├── dto/           # DTOs con validaciones
│   └── mapper/        # Mappers MapStruct
├── enums/             # Enumeraciones del dominio
├── exception/         # Excepciones personalizadas
├── model/             # Modelos de dominio (MongoDB)
├── repository/        # Repositorios Spring Data MongoDB
└── service/           # Lógica de negocio
```

## Endpoints Principales

### 🏦 Cuentas Bancarias (`/v1/cuentas`)

#### Crear Cuenta
```http
POST /v1/cuentas
Content-Type: application/json

{
  "numeroCuenta": "1234567890",
  "clienteIdentificacion": "1234567890",
  "clienteNombre": "Juan Pérez García",
  "tipoCuenta": "AHORROS",
  "saldoInicial": 500.00,
  "limiteSobregiro": 200.00,
  "moneda": "USD",
  "sucursal": "001",
  "ejecutivo": "María González",
  "requestId": "REQ-2024-001"
}
```

#### Consultar Cuenta
```http
GET /v1/cuentas/{numeroCuenta}
```

#### Consultar Saldo
```http
GET /v1/cuentas/{numeroCuenta}/saldo
```

#### Consultar Cuentas por Cliente (Paginado)
```http
GET /v1/cuentas/cliente/{clienteIdentificacion}?page=0&size=10&sortBy=fechaCreacion&sortDir=desc&estado=ACTIVA
```

#### Bloquear/Desbloquear Cuenta
```http
PATCH /v1/cuentas/{numeroCuenta}/bloquear
PATCH /v1/cuentas/{numeroCuenta}/desbloquear
```

#### Validar Saldo Disponible
```http
GET /v1/cuentas/{numeroCuenta}/validar-saldo?monto=250.00
```

### 💸 Movimientos Bancarios (`/v1/movimientos`)

#### Crear Movimiento
```http
POST /v1/movimientos
Content-Type: application/json

{
  "numeroCuenta": "1234567890",
  "numeroComprobante": "COMP-2024-001",
  "tipoMovimiento": "DEBITO",
  "monto": 150.75,
  "concepto": "RETIRO CAJERO",
  "descripcion": "Retiro en cajero automático",
  "sucursal": "001",
  "cajero": "ATM-001",
  "canalTransaccion": "ATM"
}
```

#### Consultar Movimientos por Cuenta (Paginado)
```http
GET /v1/movimientos/cuenta/{numeroCuenta}?page=0&size=20&sortBy=fechaMovimiento&sortDir=desc
```

#### Consultar Movimientos por Fechas
```http
GET /v1/movimientos/cuenta/{numeroCuenta}/fechas?fechaInicio=2024-01-01T00:00:00&fechaFin=2024-01-31T23:59:59&tipoMovimiento=DEBITO
```

#### Consultar Movimientos por Concepto
```http
GET /v1/movimientos/cuenta/{numeroCuenta}/concepto?concepto=RETIRO
```

#### Procesar y Reversar Movimientos
```http
PATCH /v1/movimientos/comprobante/{numeroComprobante}/procesar
PATCH /v1/movimientos/comprobante/{numeroComprobante}/reversar?motivo=Solicitud del cliente
```

## Configuración

### application.properties
```properties
spring.application.name=banquito-general
server.port=8080

# MongoDB
spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017
spring.data.mongodb.database=banquito_general

# OpenAPI
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.api-docs.path=/api-docs
```

### Dependencias Clave
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-mongodb</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-openfeign</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    </dependency>
    <dependency>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct</artifactId>
    </dependency>
</dependencies>
```

## Validaciones de Negocio

### Creación de Cuentas
- ✅ Número de cuenta único
- ✅ Máximo 5 cuentas por cliente
- ✅ Solo una cuenta de plazo fijo por cliente
- ✅ Validación de datos del cliente

### Movimientos Bancarios
- ✅ Validación de saldo disponible para débitos
- ✅ Cuenta debe estar activa
- ✅ Permisos de débito/crédito
- ✅ Comprobante único
- ✅ Montos positivos

### Seguridad
- ✅ Validación de estados de cuenta
- ✅ Idempotencia en operaciones críticas
- ✅ Logs de auditoría completos
- ✅ Manejo de excepciones robusto

## Ejemplos de Uso

### Flujo Típico Bancario

1. **Crear cuenta de ahorros**
2. **Consultar saldo inicial**
3. **Registrar depósito (crédito)**
4. **Registrar retiro (débito)**
5. **Consultar historial de movimientos**
6. **Generar reportes por fechas**

### Casos de Uso Avanzados

- **Búsqueda de cuentas inactivas** para procesos de mantenimiento
- **Análisis de movimientos por sucursal** para reportes gerenciales
- **Validaciones de límites** para operaciones automatizadas
- **Reversas de movimientos** para corrección de errores

## Documentación API

Una vez levantado el servicio, la documentación interactiva está disponible en:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

## Monitoreo y Salud

- **Health Check**: http://localhost:8080/actuator/health
- **Métricas**: http://localhost:8080/actuator/metrics
- **Info**: http://localhost:8080/actuator/info

## Ejecución

```bash
# Compilar
mvn clean compile

# Ejecutar tests
mvn test

# Ejecutar aplicación
mvn spring-boot:run
```

## Consideraciones de Producción

### Base de Datos
- Configurar replica set de MongoDB
- Implementar backup automático
- Configurar índices para optimización

### Seguridad
- Implementar JWT para autenticación
- Configurar HTTPS
- Limitar CORS a dominios específicos

### Monitoreo
- Integrar con herramientas como Prometheus
- Configurar alertas para errores críticos
- Implementar distributed tracing

## Tecnologías Utilizadas

- **Java 21** - Lenguaje de programación
- **Spring Boot 3.5.3** - Framework principal
- **MongoDB** - Base de datos NoSQL
- **MapStruct** - Mapeo de objetos
- **OpenAPI 3** - Documentación API
- **Spring Cloud OpenFeign** - Cliente HTTP
- **Lombok** - Reducción de código boilerplate
- **SLF4J** - Logging
- **Bean Validation** - Validaciones

---

**Desarrollado por:** Equipo BanQuito  
**Versión:** 1.0.0  
**Licencia:** MIT 