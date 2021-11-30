Feature: create entry

  Scenario: create entry
    * call read('classpath:kottage/users/admins/share.feature@signIn')
    Given url baseUrl + '/entries'
    When request {title: "from karate", body: "karate body"}
    And method POST
    Then status 201
