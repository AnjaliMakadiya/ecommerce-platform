package com.ecommerce.plateform.orderservice.stepdefinations;

import com.ecommerce.plateform.orderservice.dto.CreateOrderRequest;
import com.ecommerce.plateform.orderservice.persistence.postgres.entity.Order;
import com.ecommerce.plateform.orderservice.persistence.postgres.repository.OrderRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.*;


@RequiredArgsConstructor
public class OrderStepDefinition {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private OrderRepository orderRepository;

    private ResponseEntity<Order> response;

    @Given("Order Service is running")
    public void orderServiceIsRunning() {
        System.out.println("Order Service Started");
    }

    @When("I create the order with customerId {string}")
    public void createOrder(String customerId) {

        System.out.println("Customer Id : " + customerId);
        CreateOrderRequest request = new CreateOrderRequest();
//        request.setCustomerId(customerId);

        response = restTemplate.postForEntity("/orders",request,Order.class);
    }

    @Then("the response status should be {int}")
    public void verifyStatus(int statusCode) {

        System.out.println("Status : " + statusCode);
        assertEquals(statusCode,response.getStatusCode().value());
    }

    @And("the order status should be {string}")
    public void verifyOrderStatus(String status) {

        System.out.println("Order Status : " + status);
        assertEquals(status, response.getBody().getStatus().name());
    }
}
