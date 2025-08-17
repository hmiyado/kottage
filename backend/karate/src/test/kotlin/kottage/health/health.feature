Feature: health

  Scenario: health check
    Given url baseUrl + '/health'
    When request
    And method GET
    Then status 200
    And match response == {description: 'OK', version: '#string', databaseType: '#string'}
