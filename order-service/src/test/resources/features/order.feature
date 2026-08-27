Feature: Order API

  Scenario: Create a new order successfully
    Given Order Service is running
    When I create the order with customerId "CUST101"
    Then the response status should be 201
    And the order status should be "CREATED"