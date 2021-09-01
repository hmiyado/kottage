Feature: health

  Scenario: health check
    Given url 'http://localhost:8080/health'
    When request
    And method GET
    Then status 200
    And match response == {description: 'OK'}
