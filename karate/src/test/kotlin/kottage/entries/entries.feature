Feature: entries

  Scenario: create and patch and delete entry
    * def getCurrentTime =
    """
      function(){ return java.lang.System.currentTimeMillis() + '' }
    """
    * def screenName = "entry_creator_" + getCurrentTime()
    # sign up
    Given url 'http://localhost:8080/api/v1/users'
    When request {screenName: '#(screenName)', password: "password"}
    And method POST
    Then status 201
    * def userLocation = responseHeaders['Location'][0]
    * def author = {"id": #(response.id), "screenName": '#(screenName)'}
    # POST /entries
    Given url 'http://localhost:8080/api/v1/entries'
    When request {title: "from karate", body: "karate body"}
    And method POST
    Then status 201
    And match response == {serialNumber: '#number', title: "from karate", body: "karate body", dateTime: '#string', "author":'#(author)'}
    * def createdEntry = response
    * def location = responseHeaders['Location'][0]
    # GET /entries
    Given url 'http://localhost:8080/api/v1/entries'
    And method GET
    Then status 200
    And match response.items[*] contains createdEntry
    # PATCH /entries/:id
    Given url location
    When request {title: "modified"}
    And method PATCH
    Then status 200
    And match response == {serialNumber: '#number', title: "modified", body: "karate body", dateTime: '#string', "author": '#(author)'}
    # DELETE /entries/:id
    Given url location
    When request ''
    And method DELETE
    Then status 200
    # GET /entries/:id
    Given url location
    When request ''
    And method Get
    Then status 404
    # delete user
    Given url userLocation
    And method DELETE
    Then status 200
