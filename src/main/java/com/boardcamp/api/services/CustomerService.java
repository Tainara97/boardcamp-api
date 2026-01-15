package com.boardcamp.api.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.boardcamp.api.dtos.CustomerDTO;
import com.boardcamp.api.errors.CustomerCpfConflictError;
import com.boardcamp.api.errors.CustomerNotFoundError;
import com.boardcamp.api.models.CustomerModel;
import com.boardcamp.api.repositories.CustomerRepository;

@Service
public class CustomerService {
    final CustomerRepository customerRepository;

    CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public CustomerModel createCustomer(CustomerDTO body) {

        if (customerRepository.existsByCpf(body.getCpf())) {
            throw new CustomerCpfConflictError("A customer with this cpf already exists");
        }

        CustomerModel customer = new CustomerModel(body);
        return customerRepository.save(customer);

    }

    public List<CustomerModel> getCustomers() {
        return customerRepository.findAll();
    }

    public CustomerModel getCustomerById(Long id) {
        return customerRepository
            .findById(id)
            .orElseThrow(() -> new CustomerNotFoundError("Could not find customer with this id"));
    }

}
