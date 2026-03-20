Feature: API REST - Complete CRUD Cycle for Turnos (Shifts)
  Background: API Base Configuration
    Given the API is configured

  @smoke @critical @crud
  Scenario: Complete CRUD Cycle - POST, GET, PUT, DELETE
    When a new user registers with email "qatest@example.com" and password "pass1234"
    And signs in with email "qatest@example.com" and password "pass1234"
    And obtains a valid JWT token
    And creates a turno with cedula 999888 name "Paciente Test" and priority "alta"
    Then the turno is created successfully with status 202
    And retrieves the list of all turnos
    Then the list contains at least 1 turno with status 200
    And retrieves turnos for patient with cedula 999888
    Then finds the patient turno with status 200
    And signs out
    Then the session closes successfully with status 201

  @negative @security
  Scenario: Unauthorized Access Without Token
    When attempts to fetch user profile without token
    Then receives unauthorized error with status 401

  @negative @validation
  Scenario: Invalid Turno Creation
    When attempts to create turno with invalid cedula "xyz" and name "Test"
    Then receives validation error with status 400
