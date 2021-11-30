Feature: shared users scenario

  @ignore @signOut
  Scenario: sign out
    # POST /sign-out
    Given url 'http://localhost:8080/api/v1/sign-out'
    And method POST
    Then status 200
