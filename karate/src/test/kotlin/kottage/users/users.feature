Feature: users

  Scenario: create user
    * def getCurrentTime =
    """
      function(){ return java.lang.System.currentTimeMillis() + '' }
    """
    * def screenName = "karate_man_" + getCurrentTime()
    # POST /users
    Given url baseUrl + '/users'
    When request {screenName: '#(screenName)', password: "password"}
    And method POST
    Then status 201
    And match response == {id: '#number', screenName: '#(screenName)'}
    And match responseCookies.user_session contains { value: '#regex [0-9a-z]+', httponly: false}
    * def location = responseHeaders['Location'][0]
    # PATCH /users/:id
    Given url location
    When request {screenName: "modified"}
    And method PATCH
    Then status 200
    And match response == {id: '#number', screenName: "modified"}
    # PATCH /users/:id => 400
    Given url location
    When request ""
    And header Content-Type = "application/json"
    And method PATCH
    Then status 400
    # POST /sign-out
    Given url baseUrl + '/sign-out'
    And method POST
    Then status 200
    And match responseCookies.user_session contains { value: '#regex ^$'}
    # POST /sign-in
    Given url baseUrl + '/sign-in'
    When request {screenName: "modified", password: "password"}
    And method POST
    Then status 200
    And match response == {id: '#number', screenName: "modified"}
    And match responseCookies.user_session contains { value: '#regex [0-9a-z]+'}
    # GET /users/current
    Given url baseUrl + '/users/current'
    And method GET
    Then status 200
    And match response == {id: '#number', screenName: "modified"}
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

  Scenario: get users
    # GET /users => 401 when not admin
    Given url baseUrl + '/users'
    And method GET
    Then status 401
    # sign in as admin
    * call read('classpath:kottage/users/admins/share.feature@signIn')
    # GET /users => 200 when admin
    Given url baseUrl + '/users'
    And method GET
    Then status 200
    And match response.items contains { id: '#number', screenName: '#(screenName)'}
    # POST /sign-out
    * call read('share.feature@signOut')
