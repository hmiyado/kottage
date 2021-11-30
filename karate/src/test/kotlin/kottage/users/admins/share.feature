Feature: shared scenario

  @ignore @signIn
  Scenario: sign in as admin
    * def screenName = karate.get(java.lang.System.getenv('ADMIN_NAME'), "admin")
    * def password = karate.get(java.lang.System.getenv('ADMIN_PASSWORD'), "admin")
      # sign in as Admin
    Given url 'http://localhost:8080/api/v1/sign-in'
    When request {screenName: '#(screenName)', password: '#(password)'}
    And method POST
    Then status 200
