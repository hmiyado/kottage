Feature: entries pagination

  Scenario: pagination with 50 entries
    * def minEntryCount = 50
    Given url baseUrl + '/entries'
    And method GET
    Then status 200
    * def createEntry = function(){ karate.call('create-entry.feature') }
    * if( response.totalCount < minEntryCount ) karate.repeat(minEntryCount, createEntry)
    * def defaultLimit = 20
    # no limit, no offset
    Given url baseUrl + '/entries'
    And method GET
    Then status 200
    And match response contains {totalCount: '#? _ >= 50'}
    And match karate.sizeOf(response.items) == defaultLimit
    * def totalCount = response.totalCount
    * def lastEntry = response.items[defaultLimit - 1]
    # limit and offset
    * def limit = 10
    Given url baseUrl + '/entries?limit='+limit+'&offset='+(defaultLimit-1)
    And method GET
    Then status 200
    And match response contains {totalCount: '#(totalCount)'}
    And match karate.sizeOf(response.items) == limit
    And match response.items[0] == lastEntry
