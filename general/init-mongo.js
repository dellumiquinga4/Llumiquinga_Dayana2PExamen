// Script de inicialización de MongoDB para BanQuito
// Este script se ejecuta automáticamente cuando se inicia el contenedor de MongoDB

// Cambiar a la base de datos banquito
db = db.getSiblingDB('banquito');

// Crear usuario para la aplicación
db.createUser({
  user: 'banquito_user',
  pwd: 'banquito_pass',
  roles: [
    {
      role: 'readWrite',
      db: 'banquito'
    }
  ]
});

// Crear colecciones iniciales con validaciones
db.createCollection('cuentas_bancarias', {
  validator: {
    $jsonSchema: {
      bsonType: 'object',
      required: ['numeroCuenta', 'clienteIdentificacion', 'clienteNombre', 'tipoCuenta', 'estadoCuenta'],
      properties: {
        numeroCuenta: {
          bsonType: 'string',
          description: 'Número único de cuenta bancaria'
        },
        clienteIdentificacion: {
          bsonType: 'string',
          description: 'Identificación del cliente'
        },
        clienteNombre: {
          bsonType: 'string',
          description: 'Nombre completo del cliente'
        },
        tipoCuenta: {
          bsonType: 'string',
          enum: ['AHORROS', 'CORRIENTE', 'PLAZO_FIJO', 'VISTA'],
          description: 'Tipo de cuenta bancaria'
        },
        estadoCuenta: {
          bsonType: 'string',
          enum: ['ACTIVA', 'INACTIVA', 'BLOQUEADA', 'CERRADA'],
          description: 'Estado actual de la cuenta'
        },
        saldoDisponible: {
          bsonType: 'decimal',
          description: 'Saldo disponible para transacciones'
        },
        saldoContable: {
          bsonType: 'decimal',
          description: 'Saldo contable de la cuenta'
        }
      }
    }
  }
});

db.createCollection('movimientos_cuenta', {
  validator: {
    $jsonSchema: {
      bsonType: 'object',
      required: ['numeroCuenta', 'numeroComprobante', 'tipoMovimiento', 'monto', 'concepto'],
      properties: {
        numeroCuenta: {
          bsonType: 'string',
          description: 'Número de cuenta asociada'
        },
        numeroComprobante: {
          bsonType: 'string',
          description: 'Número único del comprobante'
        },
        tipoMovimiento: {
          bsonType: 'string',
          enum: ['DEBITO', 'CREDITO'],
          description: 'Tipo de movimiento'
        },
        monto: {
          bsonType: 'decimal',
          description: 'Monto del movimiento'
        },
        concepto: {
          bsonType: 'string',
          description: 'Concepto del movimiento'
        }
      }
    }
  }
});

// Crear índices para optimizar consultas
db.cuentas_bancarias.createIndex({ 'numeroCuenta': 1 }, { unique: true });
db.cuentas_bancarias.createIndex({ 'clienteIdentificacion': 1 });
db.cuentas_bancarias.createIndex({ 'estadoCuenta': 1 });
db.cuentas_bancarias.createIndex({ 'tipoCuenta': 1 });

db.movimientos_cuenta.createIndex({ 'numeroCuenta': 1 });
db.movimientos_cuenta.createIndex({ 'numeroComprobante': 1 }, { unique: true });
db.movimientos_cuenta.createIndex({ 'fechaMovimiento': -1 });
db.movimientos_cuenta.createIndex({ 'numeroCuenta': 1, 'fechaMovimiento': -1 });

// Insertar datos de prueba (opcional)
print('Inicializando base de datos BanQuito...');
print('Usuario banquito_user creado exitosamente');
print('Colecciones y índices creados exitosamente');
print('Base de datos lista para usar'); 