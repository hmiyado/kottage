Feature: entries

  Scenario: create entry
    Given url 'http://localhost:8080/entries'
    When request {title: "from karate", body: "karate body"}
    And header Authorization = 'Basic YWRtaW46YWRtaW4='
    And method POST
    Then status 201
    And match response == {serialNumber: '#number', title: "from karate", body: "karate body", dateTime: '#string'}
