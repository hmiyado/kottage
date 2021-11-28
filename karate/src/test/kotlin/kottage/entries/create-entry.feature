Feature: create entry

  Scenario: create entry
    * def screenName = karate.get(java.lang.System.getenv('ADMIN_NAME'), "admin")
    * def password = karate.get(java.lang.System.getenv('ADMIN_PASSWORD'), "admin")
      # sign in as Admin
    Given url 'http://localhost:8080/api/v1/sign-in'
    When request {screenName: '#(screenName)', password: '#(password)'}
    And method POST
    Then status 200
    Given url 'http://localhost:8080/api/v1/entries'
    When request {title: "from karate", body: "karate body"}
    And method POST
    Then status 201
