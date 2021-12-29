Feature: comments

  Scenario: create and delete comment
    * call read('classpath:kottage/users/admins/share.feature@signIn')
    * call read('share.feature@createEntry')
    * def serialNumber = response.serialNumber
    # POST /entries/{serialNumber}/comments
    Given url baseUrl + '/entries/' + serialNumber + '/comments'
    When request {body: "new comment"}
    And method POST
    Then status 201
    And match response == {id: '#number', body: 'new comment', createdAt: '#string', author: {id: '#number', screenName: '#string'}}
    * def createdComment = response
    # GET /entries/{serialNumber}/comments
    Given url baseUrl + '/entries/' + serialNumber + '/comments'
    And method GET
    Then status 200
    And match response.totalCount == 1
    And match response.items[*] contains createdComment
    # GET /entries/{serialNumber}
    Given url baseUrl + '/entries/' + serialNumber
    And method GET
    Then status 200
    And match response.commentsTotalCount == 1
    # DELETE /entries/{serialNumber}/comments/{commentId}
    Given url baseUrl + '/entries/' + serialNumber + '/comments/' + createdComment.id
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
    When request {body: "new comment"}
    And method POST
    Then status 403
    * configure headers = { 'X-CSRF-Token': '#(responseHeaders["X-CSRF-Token"])' }
    When request {body: "new comment"}
    And method POST
    Then status 404
