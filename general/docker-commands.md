# Comandos Docker para BanQuito General Banking Service

## 🚀 Comandos Básicos

### Construcción y Ejecución con Docker Compose
```bash
# Construir y levantar todos los servicios
docker-compose up --build

# Levantar en segundo plano
docker-compose up -d

# Parar todos los servicios
docker-compose down

# Parar y eliminar volúmenes
docker-compose down -v

# Ver logs de todos los servicios
docker-compose logs -f

# Ver logs de un servicio específico
docker-compose logs -f banquito-app
```

### Construcción Manual del Docker
```bash
# Construir la imagen
docker build -t banquito-general:latest .

# Ejecutar la aplicación sola (necesita MongoDB externo)
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=docker \
  -e SPRING_DATA_MONGODB_HOST=localhost \
  banquito-general:latest
```

## 🔧 Comandos de Desarrollo

### Acceso a Contenedores
```bash
# Acceder al contenedor de la aplicación
docker exec -it banquito-general-app bash

# Acceder al contenedor de MongoDB
docker exec -it banquito-mongodb mongosh

# Acceder a la base de datos banquito
docker exec -it banquito-mongodb mongosh banquito -u banquito_user -p banquito_pass
```

### Gestión de Volúmenes
```bash
# Listar volúmenes
docker volume ls

# Inspeccionar volumen de MongoDB
docker volume inspect general_mongodb_data

# Backup de la base de datos
docker exec banquito-mongodb mongodump --db banquito --out /data/backup

# Restaurar base de datos
docker exec banquito-mongodb mongorestore --db banquito /data/backup/banquito
```

## 📊 Monitoreo y Logs

### Health Checks
```bash
# Verificar health de la aplicación
curl http://localhost:8080/actuator/health

# Verificar health de MongoDB
docker exec banquito-mongodb mongosh --eval "db.adminCommand('ping')"
```

### Logs Detallados
```bash
# Logs de la aplicación en tiempo real
docker-compose logs -f banquito-app

# Logs de MongoDB
docker-compose logs -f mongodb

# Logs de Mongo Express
docker-compose logs -f mongo-express
```

### Métricas y Monitoreo
```bash
# Estadísticas de contenedores
docker stats

# Información detallada de un contenedor
docker inspect banquito-general-app

# Uso de recursos
docker-compose top
```

## 🌐 URLs de Acceso

### Aplicación Principal
- **API REST**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/api-docs
- **Health Check**: http://localhost:8080/actuator/health
- **Metrics**: http://localhost:8080/actuator/metrics

### MongoDB
- **MongoDB**: mongodb://localhost:27017
- **Mongo Express**: http://localhost:8081

### Credenciales
- **MongoDB Admin**: admin / banquito123
- **MongoDB App User**: banquito_user / banquito_pass

## 🛠️ Comandos de Mantenimiento

### Limpieza
```bash
# Eliminar contenedores parados
docker container prune

# Eliminar imágenes no utilizadas
docker image prune

# Limpieza completa del sistema
docker system prune -a

# Eliminar todo (contenedores, imágenes, volúmenes, redes)
docker system prune -a --volumes
```

### Actualización
```bash
# Reconstruir solo la aplicación
docker-compose build banquito-app

# Reiniciar un servicio específico
docker-compose restart banquito-app

# Actualizar imagen de MongoDB
docker-compose pull mongodb
```

## 🔄 Comandos de Producción

### Construcción Optimizada
```bash
# Construir para producción
docker build --target production -t banquito-general:prod .

# Ejecutar en modo producción
docker run -d \
  --name banquito-prod \
  -p 8080:8080 \
  --restart unless-stopped \
  -e SPRING_PROFILES_ACTIVE=docker \
  banquito-general:prod
```

### Backup y Restore
```bash
# Crear backup completo
docker exec banquito-mongodb mongodump --uri="mongodb://banquito_user:banquito_pass@localhost:27017/banquito" --out /backup

# Restaurar desde backup
docker exec banquito-mongodb mongorestore --uri="mongodb://banquito_user:banquito_pass@localhost:27017/banquito" /backup/banquito
```

## 🚨 Troubleshooting

### Problemas Comunes
```bash
# Verificar conectividad entre contenedores
docker exec banquito-general-app ping mongodb

# Verificar variables de entorno
docker exec banquito-general-app env | grep SPRING

# Verificar puertos ocupados
netstat -tulpn | grep :8080

# Reiniciar servicios con problemas
docker-compose restart
```

### Debug de la Aplicación
```bash
# Ejecutar en modo debug
docker-compose -f docker-compose.yml -f docker-compose.debug.yml up

# Ver configuración final
docker-compose config

# Verificar logs de error
docker-compose logs banquito-app | grep ERROR
``` 