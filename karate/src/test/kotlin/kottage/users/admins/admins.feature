Feature: admins

  Scenario: /users/admins
    # GET /users/admins as not admin
    Given url 'http://localhost:8080/api/v1/users/admins'
    And method GET
    Then status 401
    # sign in as Admin
    * call read('classpath:kottage/users/admins/share.feature@signIn')
    * def adminId = response.id
    # GET /users/admins as admin
    Given url 'http://localhost:8080/api/v1/users/admins'
    And method GET
    Then status 200
    And match response.items contains { id: '#(adminId)' }
    # sign out
    Given url 'http://localhost:8080/api/v1/sign-out'
    And method POST
    Then status 200

  Scenario: add/remove admin
    # create not admin user
    Given url 'http://localhost:8080/api/v1/users'
    When request {screenName: 'notadmin', password: 'notadmin'}
    And method POST
    Then status 201
    * def notAdminId = response.id
    # not admin user cannot make self admin
    Given url 'http://localhost:8080/api/v1/users/admins'
    When request {id: '#(notAdminId)'}
    And method PATCH
    Then status 401
    # sign out
    Given url 'http://localhost:8080/api/v1/sign-out'
    And method POST
    Then status 200
    # sign in as Admin
    * call read('classpath:kottage/users/admins/share.feature@signIn')
    * def adminId = response.id
    # PATCH /users/admins
    # make not-admin-user admin
    Given url 'http://localhost:8080/api/v1/users/admins'
    When request {id: '#(notAdminId)'}
    And method PATCH
    Then status 200
    # verify not admin user becomes admin
    Given url 'http://localhost:8080/api/v1/users/admins'
    And method GET
    Then status 200
    And match response.items contains { id: '#(notAdminId)' }
    # DELETE /users/admins
    # make not-admin-user not-admin
    Given url 'http://localhost:8080/api/v1/users/admins'
    When request {id: '#(notAdminId)'}
    And method DELETE
    Then status 200
    # verify not admin user is not admin
    Given url 'http://localhost:8080/api/v1/users/admins'
    And method GET
    Then status 200
    And match response.items !contains { id: '#(notAdminId)' }
    # sign out
    Given url 'http://localhost:8080/api/v1/sign-out'
    And method POST
    Then status 200
    # sign in delete not admin user
    Given url 'http://localhost:8080/api/v1/sign-in'
    When request {screenName: 'notadmin', password: 'notadmin'}
    And method POST
    Then status 200
    Given url 'http://localhost:8080/api/v1/users/' + notAdminId
    And method DELETE
    Then status 200
