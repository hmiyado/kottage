Feature: entries

  Scenario: create and patch and delete entry as admin
    * call read('classpath:kottage/users/admins/share.feature@signIn')
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
    And match response.items == karate.sort(response.items, i => -1 * i.serialNumber)
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

  Scenario: user not admin cannot create entry
    * def getCurrentTime =
    """
      function(){ return java.lang.System.currentTimeMillis() + '' }
    """
    * def screenName = "entry_creator_" + getCurrentTime()
    # sign up as user not admin
    Given url 'http://localhost:8080/api/v1/users'
    When request {screenName: '#(screenName)', password: "password"}
    And method POST
    Then status 201
    * def userLocation = responseHeaders['Location'][0]
    # POST /entries => 401
    Given url 'http://localhost:8080/api/v1/entries'
    When request {title: "from karate", body: "karate body"}
    And method POST
    Then status 401
    # delete user
    Given url userLocation
    And method DELETE
    Then status 200

  Scenario: entry's datetime is valid in UTC
    * def allowedStartEntryTime = karate.properties['allowedStartEntryTime']
    * def allowedEndEntryTime = karate.properties['allowedEndEntryTime']
    # sign inn as admin
    * call read('classpath:kottage/users/admins/share.feature@signIn')
    * def author = {"id": #(response.id), "screenName": '#(screenName)'}
    # POST /entries
    Given url 'http://localhost:8080/api/v1/entries'
    When request {title: "from karate", body: "karate body"}
    And method POST
    Then status 201
    * print 'dateTime should in [', allowedStartEntryTime, '..', allowedEndEntryTime ,']'
    And match response contains {dateTime: '#? _ >= allowedStartEntryTime && _ <= allowedEndEntryTime'}
