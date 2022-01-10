Feature: shared scenarios

  @ignore @createEntry
  Scenario: create entry
    * call read('classpath:kottage/users/admins/share.feature@signIn')
    Given url baseUrl + '/entries'
    When request {title: "from karate", body: "karate body"}
    And method POST
    * configure headers = { 'X-CSRF-Token': '#(responseHeaders["X-CSRF-Token"])', Origin: '#(origin)' }
    When request {title: "from karate", body: "karate body"}
    And method POST
