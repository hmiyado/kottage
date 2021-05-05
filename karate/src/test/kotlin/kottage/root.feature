Feature: root

  Scenario: Hello World!

    Given url 'http://0.0.0.0:8080/'
    And request ''
    When method Get
    Then status 200
    And match response == 'Hello World!'
