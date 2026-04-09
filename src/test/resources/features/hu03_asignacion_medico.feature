Feature: HU-03 - Comportamiento de Asignación de Médico
  Valida el comportamiento del turno respecto a la asignación de médico y estado de espera
  según lo requerido por HU-03, HT-04 y HT-05 de la iniciativa de Asignación Inteligente de Turnos.

  Background: Configuración base de la API
    Given the API is configured for HU03 tests

  @hu03 @asignacion @smoke
  Scenario: El turno permanece en espera cuando no hay médico disponible
    Given a receptionist is authenticated
    When creates an appointment with idCard 733001 name "Laura Esperando" and priority "high"
    And waits briefly for potential assignment
    When queries the appointment by idCard 733001
    Then the appointment status is "waiting"
    And the doctor name is null
    And the office is null

  @hu03 @asignacion @smoke
  Scenario: Múltiples turnos mantienen posiciones correctas en cola
    Given a receptionist is authenticated
    When creates an appointment with idCard 733005 name "Primer Paciente" and priority "medium"
    And creates an appointment with idCard 733006 name "Segundo Paciente" and priority "high"
    And waits briefly for potential assignment
    When queries the queue position for idCard 733006
    Then the queue status is "waiting"
    And the position is a number greater than 0
