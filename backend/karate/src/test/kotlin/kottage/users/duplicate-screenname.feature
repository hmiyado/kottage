Feature: prevent duplicate screenName

  Scenario: cannot create user with duplicate screenName
    * def getCurrentTime =
  """
    function(){ return java.lang.System.currentTimeMillis() + '' }
  """
    * def screenName = "duplicate_test_" + getCurrentTime()
  # Create first user
    Given url baseUrl + '/users'
    When request {screenName: '#(screenName)', password: "password1"}
    And method POST
    Then status 403
    * configure headers = { 'X-CSRF-Token': '#(responseHeaders["X-CSRF-Token"])', Origin: '#(origin)' }
    When request {screenName: '#(screenName)', password: "password1"}
    And method POST
    Then status 201
    And match response == {id: '#number', screenName: '#(screenName)'}
    * def firstUserId = response.id
  # Sign out
    * call read('share.feature@signOut')
  # Try to create second user with same screenName
    Given url baseUrl + '/users'
    When request {screenName: '#(screenName)', password: "password2"}
    And method POST
    Then status 403
    * configure headers = { 'X-CSRF-Token': '#(responseHeaders["X-CSRF-Token"])', Origin: '#(origin)' }
    When request {screenName: '#(screenName)', password: "password2"}
    And method POST
    Then status 400
    And match response.message == 'screenName "' + screenName + '" is duplicated'
  # Clean up: delete first user
    Given url baseUrl + '/sign-in'
    When request {screenName: '#(screenName)', password: "password1"}
    And method POST
    Then status 200
    * configure headers = { 'X-CSRF-Token': '#(responseHeaders["X-CSRF-Token"])', Origin: '#(origin)' }
    When request {screenName: '#(screenName)', password: "password1"}
    And method POST
    Then status 200
    Given url baseUrl + '/users/' + firstUserId
    And method DELETE
    Then status 200

  Scenario: admin user is not duplicated on multiple sign-ins
    * def adminScreenName = karate.get(java.lang.System.getenv('ADMIN_NAME'), "admin")
    * def adminPassword = karate.get(java.lang.System.getenv('ADMIN_PASSWORD'), "admin")
  # Sign in as admin (first time - ensures admin exists)
    * call read('classpath:kottage/users/admins/share.feature@signIn')
    * def firstAdminId = response.id
  # Get admin count before sign out
    Given url baseUrl + '/users/admins'
    And method GET
    Then status 200
    * def adminCountBefore = response.items.length
  # Sign out
    * call read('share.feature@signOut')
  # Sign in as admin again (should not create duplicate)
    * call read('classpath:kottage/users/admins/share.feature@signIn')
    * def secondAdminId = response.id
  # Verify same admin user
    And match firstAdminId == secondAdminId
  # Get admin count after second sign in
    Given url baseUrl + '/users/admins'
    And method GET
    Then status 200
    * def adminCountAfter = response.items.length
  # Verify admin count did not increase
    And match adminCountBefore == adminCountAfter
  # Sign out
    * call read('share.feature@signOut')
