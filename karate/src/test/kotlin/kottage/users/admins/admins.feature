Feature: admins

  Scenario: /users/admins
    # GET /users/admins as not admin
    Given url baseUrl + '/users/admins'
    And method GET
    Then status 401
    # sign in as Admin
    * call read('classpath:kottage/users/admins/share.feature@signIn')
    * def adminId = response.id
    # GET /users/admins as admin
    Given url baseUrl + '/users/admins'
    And method GET
    Then status 200
    And match response.items contains { id: '#(adminId)' }
    # sign out
    * call read('classpath:kottage/users/share.feature@signOut')

  Scenario: add/remove admin
    # create not admin user
    Given url baseUrl + '/users'
    When request {screenName: 'notadmin', password: 'notadmin'}
    And method POST
    Then status 201
    * def notAdminId = response.id
    # not admin user cannot make self admin
    Given url baseUrl + '/users/admins'
    When request {id: '#(notAdminId)'}
    And method PATCH
    Then status 401
    # sign out
    * call read('classpath:kottage/users/share.feature@signOut')
    # sign in as Admin
    * call read('classpath:kottage/users/admins/share.feature@signIn')
    * def adminId = response.id
    # PATCH /users/admins
    # make not-admin-user admin
    Given url baseUrl + '/users/admins'
    When request {id: '#(notAdminId)'}
    And method PATCH
    Then status 200
    # verify not admin user becomes admin
    Given url baseUrl + '/users/admins'
    And method GET
    Then status 200
    And match response.items contains { id: '#(notAdminId)' }
    # DELETE /users/admins
    # make not-admin-user not-admin
    Given url baseUrl + '/users/admins'
    When request {id: '#(notAdminId)'}
    And method DELETE
    Then status 403
    * def csrfToken = responseHeaders['X-CSRF-Token']
    Given url baseUrl + '/users/admins'
    When request {id: '#(notAdminId)'}
    And header X-CSRF-Token = csrfToken
    And method DELETE
    Then status 200
    # verify not admin user is not admin
    Given url baseUrl + '/users/admins'
    And method GET
    Then status 200
    And match response.items !contains { id: '#(notAdminId)' }
    # sign out
    * call read('classpath:kottage/users/share.feature@signOut')
    # sign in delete not admin user
    Given url baseUrl + '/sign-in'
    When request {screenName: 'notadmin', password: 'notadmin'}
    And method POST
    Then status 200
    Given url baseUrl + '/users/' + notAdminId
    And method DELETE
    Then status 403
    * def csrfToken = responseHeaders['X-CSRF-Token']
    Given url baseUrl + '/users/' + notAdminId
    And header X-CSRF-Token = csrfToken
    And method DELETE
    Then status 200
