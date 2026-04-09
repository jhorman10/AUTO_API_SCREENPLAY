Feature: HU-03 - Posición en Cola y Ordenamiento
  Valida la consulta de posición en cola y el ordenamiento basado en prioridad
  según lo requerido por HU-02, HT-02 y HT-03 de la iniciativa de Asignación Inteligente de Turnos.

  Background: Configuración base de la API
    Given the API is configured for HU03 tests

  @hu03 @cola @smoke
  Scenario: Consultar posición en cola para un turno en espera
    Given a receptionist is authenticated
    And creates an appointment with idCard 722001 name "Ana Torres" and priority "medium"
    When queries the queue position for idCard 722001
    Then the response status is 200
    And the queue status is "waiting"
    And the position is a number greater than 0
    And the total in queue is at least 1

  @hu03 @cola @negative
  Scenario: Posición en cola para paciente inexistente retorna no encontrado
    When queries the queue position for idCard 799999
    Then the response status is 200
    And the queue status is "not_found"
    And the position is 0

  @hu03 @cola @smoke
  Scenario: Los turnos pueden consultarse por número de cédula
    Given a receptionist is authenticated
    And creates an appointment with idCard 722005 name "Pedro Consulta" and priority "high"
    When queries the appointment by idCard 722005
    Then the response status is 200
    And the appointment status is "waiting"
    And the appointment priority is "high"
