package com.boardcamp.api.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import com.boardcamp.api.dtos.CustomerDTO;
import com.boardcamp.api.models.CustomerModel;
import com.boardcamp.api.repositories.CustomerRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class CustomerControllerTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CustomerRepository customerRepository;

    @BeforeEach
    @AfterEach
    void cleanDatabase() {
        customerRepository.deleteAll();
    }

    @Test
    void givenRepeatedCpf_whenCreatingCustomer_thenThrowsError() {

        CustomerModel existingCustomer = new CustomerModel(null, "Test", "11111111111", "12345678910");
        customerRepository.save(existingCustomer);

        CustomerDTO customerDTO = new CustomerDTO("NewTest", "22222222222", "12345678910");

        HttpEntity<CustomerDTO> body = new HttpEntity<>(customerDTO);

        ResponseEntity<String> response = restTemplate.exchange(
                "/customers",
                HttpMethod.POST,
                body,
                String.class);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("A customer with this cpf already exists", response.getBody());
    }

    @Test
    void givenInvalidId_whenFindindCustomer_thenThrowsError() {

        ResponseEntity<String> response = restTemplate.exchange(
                "/customers/999",
                HttpMethod.GET,
                null,
                String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Could not find customer with this id", response.getBody());
    }

    @Test
    void givenValidCustomer_whenCreatingCustomer_thenSavesCustomer() {
        CustomerDTO customerDTO = new CustomerDTO("Test", "11111111111", "12345678910");

        HttpEntity<CustomerDTO> body = new HttpEntity<>(customerDTO);

        ResponseEntity<CustomerModel> response = restTemplate.exchange(
                "/customers",
                HttpMethod.POST,
                body,
                CustomerModel.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertEquals("Test", response.getBody().getName());
    }
}
