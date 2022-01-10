Feature: comments

  Scenario: create and delete comment
    * call read('classpath:kottage/users/admins/share.feature@signIn')
    * call read('share.feature@createEntry')
    * def serialNumber = response.serialNumber
    # POST /entries/{serialNumber}/comments as admin
    Given url baseUrl + '/entries/' + serialNumber + '/comments'
    When request {name: "admin", body: "new comment"}
    And method POST
    Then status 201
    And match response == {id: '#number', entrySerialNumber: '#(serialNumber)', name: 'admin', body: 'new comment', createdAt: '#string', author: {id: '#number', screenName: '#string'}}
    * def createdCommentAsAdmin = response
    # POST /entries/{serialNumber}/comments without user session
    * call read('classpath:kottage/users/share.feature@signOut')
    Given url baseUrl + '/entries/' + serialNumber + '/comments'
    When request {name: "Taro", body: "new comment"}
    And method POST
    Then status 201
    And match response == {id: '#number', entrySerialNumber: '#(serialNumber)', name: 'Taro', body: 'new comment', createdAt: '#string', author: '#notpresent'}
    * def createdCommentAsAnonymous = response
    # GET /entries/{serialNumber}/comments
    Given url baseUrl + '/entries/' + serialNumber + '/comments'
    And method GET
    Then status 200
    And match response.totalCount == 2
    And match response.items[*] contains createdCommentAsAdmin
    And match response.items[*] contains createdCommentAsAnonymous
    # GET /entries/{serialNumber}
    Given url baseUrl + '/entries/' + serialNumber
    And method GET
    Then status 200
    And match response.commentsTotalCount == 2
    # DELETE /entries/{serialNumber}/comments/{commentId}
    * call read('classpath:kottage/users/admins/share.feature@signIn')
    Given url baseUrl + '/entries/' + serialNumber + '/comments/' + createdCommentAsAdmin.id
    And method DELETE
    Then status 200

  Scenario: create comment to not exist entry
    * call read('classpath:kottage/users/admins/share.feature@signIn')
    # GET /entries/{serialNumber}/comments
    Given url baseUrl + '/entries/99999999/comments'
    And method GET
    Then status 404
    # POST /entries/{serialNumber}/comments
    Given url baseUrl + '/entries/9999999/comments'
    When request {name: 'Taro', body: "new comment"}
    And method POST
    Then status 403
    * configure headers = { 'X-CSRF-Token': '#(responseHeaders["X-CSRF-Token"])', Origin: '#(origin)' }
    When request {name: 'Taro', body: "new comment"}
    And method POST
    Then status 404
