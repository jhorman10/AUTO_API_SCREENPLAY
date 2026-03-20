# language: es

Funcionalidad: Automatización de API REST — Ciclo Completo de Conversiones (CRUD)
  Como Ingeniero de QA
  Quiero automatizar las operaciones CRUD de conversiones
  Para validar la integridad de los servicios REST

  Antecedentes:
    Dado que el ApiTester está listo para crear conversiones

  @smoke @critical
  Escenario: Ciclo Completo de Conversión (CREATE, READ, UPDATE, DELETE)
    Cuando crea una conversión con tipo "venta" y valor 150.50
    Entonces la conversión se crea exitosamente con status 201
    Y obtiene la lista de conversiones
    Y la lista contiene al menos 1 conversión
    Y obtiene la conversión por su id creado
    Y los datos retornados son consistentes con los enviados
    Y actualiza la conversión a estado "completada"
    Y la actualización es exitosa con status 200
    Y el estado de la conversión es "completada"
    Y elimina la conversión
    Y la eliminación es exitosa con status 204

  @negative @validation
  Escenario: Error al crear conversión sin datos obligatorios
    Cuando intenta crear una conversión sin proporcionar el user_id
    Entonces recibe error de validación con status 422
    Y el mensaje de error menciona el campo faltante

  @negative @not-found
  Escenario: Error al obtener conversión con ID inexistente
    Cuando intenta obtener una conversión con ID inválido
    Entonces recibe error de no encontrado con status 404

  @negative @not-found
  Escenario: Error al actualizar conversión inexistente
    Cuando intenta actualizar una conversión con ID inválido con estado "completada"
    Entonces recibe error de no encontrado con status 404

  @negative @not-found
  Escenario: Error al eliminar conversión inexistente
    Cuando intenta eliminar una conversión con ID inválido
    Entonces recibe error de no encontrado con status 404

  @negative @validation
  Escenario: Error al crear conversión con valor negativo
    Cuando intenta crear una conversión con tipo "venta" y valor negativo -50.00
    Entonces recibe error de validación con status 422
    Y el mensaje de error indica que el valor debe ser positivo

  @positive @edge-case
  Escenario: Crear múltiples conversiones y validar lista
    Cuando crea una conversión con tipo "descarga" y valor 50.00
    Y obtiene la conversión creada
    Entonces la conversión tiene estado "pendiente" por defecto
    Cuando crea otra conversión con tipo "suscripcion" y valor 99.99
    Y obtiene la lista de conversiones
    Entonces la lista contiene al menos 2 conversiones
