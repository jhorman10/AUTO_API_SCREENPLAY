Feature: HU-03 - Registro de Turno con Prioridad
  Valida el registro de turnos con niveles de prioridad (high, medium, low)
  según lo requerido por HU-01 y HT-01 de la iniciativa de Asignación Inteligente de Turnos.

  Background: Configuración base de la API
    Given the API is configured for HU03 tests
    And a receptionist is authenticated

  @hu03 @registro @smoke
  Scenario: Creación exitosa de turno con prioridad alta
    When creates an appointment with idCard 711001 name "Juan Garcia" and priority "high"
    Then the appointment is accepted with status 202
    When queries the appointment by idCard 711001
    Then the response status is 200
    And the appointment status is "waiting"
    And the appointment priority is "high"

  @hu03 @registro @smoke
  Scenario: Creación exitosa de turno con prioridad baja
    When creates an appointment with idCard 711004 name "Sofia Ramirez" and priority "low"
    Then the appointment is accepted with status 202
    When queries the appointment by idCard 711004
    Then the response status is 200
    And the appointment status is "waiting"
    And the appointment priority is "low"

  @hu03 @registro @negative
  Scenario: Rechazo de turno sin campo de prioridad
    When attempts to create an appointment with idCard 711002 name "Maria Lopez" without priority
    Then receives error with status 400
    And the error message contains "prioridad"

  @hu03 @registro @negative
  Scenario: Rechazo de turno con valor de prioridad inválido
    When creates an appointment with idCard 711003 name "Carlos Ruiz" and priority "critica"
    Then receives error with status 400
    And the error message contains "prioridad"
