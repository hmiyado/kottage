Feature: shared users scenario

  @ignore @signOut
  Scenario: sign out
    # POST /sign-out
    Given url baseUrl + '/sign-out'
    And method POST
    * def csrfToken = responseHeaders['X-CSRF-Token']
    Given url baseUrl + '/sign-out'
    And header X-CSRF-Token = csrfToken
    And method POST
