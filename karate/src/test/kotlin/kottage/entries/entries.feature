Feature: entries

  Scenario: create and patch and delete entry
    * configure headers = {Authorization: 'Basic YWRtaW46YWRtaW4='}
    Given url 'http://localhost:8080/entries'
    When request {title: "from karate", body: "karate body"}
    And method POST
    Then status 201
    And match response == {serialNumber: '#number', title: "from karate", body: "karate body", dateTime: '#string'}
    * def location = responseHeaders['Location'][0]
    Given url location
    When request {title: "modified"}
    And method PATCH
    Then status 200
    And match response == {serialNumber: '#number', title: "modified", body: "karate body", dateTime: '#string'}
    Given url location
    When request ''
    And method DELETE
    Then status 200
    Given url location
    When request ''
    And method Get
    Then status 404
