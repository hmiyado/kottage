Feature: users

  Scenario: create user
    * def getCurrentTime =
    """
      function(){ return java.lang.System.currentTimeMillis() + '' }
    """
    * def screenName = "karate_man_" + getCurrentTime()
    Given url 'http://localhost:8080/users'
    When request {screenName: '#(screenName)', password: "password"}
    And method POST
    Then status 201
    And match response == {id: '#number', screenName: '#(screenName)'}
    * def location = responseHeaders['Location'][0]
    Given url location
    When request {screenName: "modified"}
    And method PATCH
    Then status 200
    And match response == {id: '#number', screenName: "modified"}
    Given url location
    When request ''
    And method DELETE
    Then status 200
    Given url location
    When request ''
    And method Get
    Then status 404
