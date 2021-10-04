Feature: users

  Scenario: create user
    * def getCurrentTime =
    """
      function(){ return java.lang.System.currentTimeMillis() + '' }
    """
    * def screenName = "karate_man_" + getCurrentTime()
    # POST /users
    Given url 'http://localhost:8080/api/v1/users'
    When request {screenName: '#(screenName)', password: "password"}
    And method POST
    Then status 201
    And match response == {id: '#number', screenName: '#(screenName)'}
    And match responseHeaders['Set-Cookie'][0] contains "user_session"
    * def location = responseHeaders['Location'][0]
    # PATCH /users/:id
    Given url location
    When request {screenName: "modified"}
    And method PATCH
    Then status 200
    And match response == {id: '#number', screenName: "modified"}
    # PATCH /users/:id => 400
    Given url location
    When request {}
    And method PATCH
    Then status 400
    # POST /signOut
    Given url 'http://localhost:8080/api/v1/signOut'
    And method POST
    Then status 200
    And match responseHeaders['Set-Cookie'][0] contains "user_session=;"
    # POST /signIn
    Given url 'http://localhost:8080/api/v1/signIn'
    When request {screenName: "modified", password: "password"}
    And method POST
    Then status 200
    And match response == {id: '#number', screenName: "modified"}
    And match responseHeaders['Set-Cookie'][0] contains "user_session"
    # DELETE /users/:id
    Given url location
    When request ''
    And method DELETE
    Then status 200
    # GET /users/:id
    Given url location
    When request ''
    And method Get
    Then status 404
