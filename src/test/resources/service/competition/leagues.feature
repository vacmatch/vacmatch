Feature: Leagues

  Scenario: Create league
    Given a federation
    When I create a league for a federation with a name "Alevines" and slug "alevines"
    Then The league gets created
    And the league has name "Alevines"
    And the league has url "alevines"
    And url "/league/alevines" exists
