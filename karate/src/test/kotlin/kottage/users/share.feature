Feature: shared users scenario

  @ignore @signOut
  Scenario: sign out
    # POST /sign-out
    Given url baseUrl + '/sign-out'
    And method POST
    Then status 200
