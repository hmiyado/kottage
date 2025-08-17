Feature: shared scenario

  @ignore @signIn
  Scenario: sign in as admin
    * def screenName = karate.get(java.lang.System.getenv('ADMIN_NAME'), "admin")
    * def password = karate.get(java.lang.System.getenv('ADMIN_PASSWORD'), "admin")
      # sign in as Admin
    Given url baseUrl + '/sign-in'
    When request {screenName: '#(screenName)', password: '#(password)'}
    And method POST
    * def csrfToken = responseHeaders['X-CSRF-Token']
    Given url baseUrl + '/sign-in'
    When request {screenName: '#(screenName)', password: '#(password)'}
    And header X-CSRF-Token = csrfToken
    And method POST
