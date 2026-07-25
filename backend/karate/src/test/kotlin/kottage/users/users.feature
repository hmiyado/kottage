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
    Then status 403
    * configure headers = { 'X-CSRF-Token': '#(responseHeaders["X-CSRF-Token"])', Origin: '#(origin)' }
    When request {screenName: '#(screenName)', password: "password"}
    And method POST
    Then status 201
    And match response == {id: '#number', screenName: '#(screenName)'}
    # The session is stateless: the cookie carries the URI-encoded payload followed by
    # %2F (an encoded '/') and the 64 hex chars of its HMAC-SHA256 signature, rather than
    # an opaque server-side id. httpOnly is on because no JavaScript reads these cookies.
    And match responseCookies.user_session contains { value: '#regex .+%2F[0-9a-f]{64}', httponly: true}
    * def location = responseHeaders['Location'][0]
  # PATCH /users/:id
    Given url location
    * def newScreenName = "modified_" + getCurrentTime()
    When request {screenName: '#(newScreenName)'}
    And method PATCH
    Then status 200
    And match response == {id: '#number', screenName: '#(newScreenName)'}
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
    And match responseCookies.user_session contains { value: ''}
  # POST /sign-in
    Given url baseUrl + '/sign-in'
    When request {screenName: '#(newScreenName)', password: "password"}
    And method POST
    Then status 200
    And match response == {id: '#number', screenName: '#(newScreenName)', accountLinks: [{service:"Google", linking: false}]}
    And match responseCookies.user_session contains { value: '#regex .+%2F[0-9a-f]{64}'}
  # GET /users/current
    Given url baseUrl + '/users/current'
    And method GET
    Then status 200
    And match response == {id: '#number', screenName: '#(newScreenName)', accountLinks: [{service:"Google", linking: false}]}
  # DELETE /users/:id
    Given url location
    And method DELETE
    Then status 200
  # GET /users/:id
    Given url location
    When request ''
    And method Get
    Then status 401
    * call read('share.feature@signOut')
    * call read('classpath:kottage/users/admins/share.feature@signIn')
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
