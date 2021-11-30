Feature: create entry

  Scenario: create entry
    * call read('classpath:kottage/users/admins/share.feature@signIn')
    Given url 'http://localhost:8080/api/v1/entries'
    When request {title: "from karate", body: "karate body"}
    And method POST
    Then status 201
