Feature: API REST - Turnos Management (2 POST + 2 GET)
  Background: API Base Configuration
    Given the API is configured

  @smoke @critical
  Scenario: Complete Flow - Create and Retrieve Turnos
    When a new user registers with email "qatest@example.com" and password "pass1234"
    And creates a turno with cedula 999888 name "Paciente Test" and priority "alta"
    Then the turno is created successfully with status 202
    And retrieves the list of all turnos
    Then the list contains at least 1 turno with status 200
    And retrieves turnos for patient with cedula 999888
    Then finds the patient turno with status 200

  @negative @validation
  Scenario: Invalid Turno Creation
    When attempts to create turno with invalid cedula "xyz" and name "Test"
    Then receives validation error with status 400
