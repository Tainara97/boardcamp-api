package com.boardcamp.api.services;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.boardcamp.api.dtos.CustomerDTO;
import com.boardcamp.api.errors.CustomerCpfConflictError;
import com.boardcamp.api.errors.CustomerNotFoundError;
import com.boardcamp.api.repositories.CustomerRepository;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTests {

    @InjectMocks
    private CustomerService customerService;

    @Mock
    private CustomerRepository customerRepository;

    @Test
    void givenRepeatedCpf_whenCreatingCustomer_thenThrowsError() {
        CustomerDTO dto = new CustomerDTO("Test", "11111111111", "12345678910" );
   
    doReturn(true).when(customerRepository).existsByCpf(dto.getCpf());

    assertThrows(
        CustomerCpfConflictError.class, 
        () -> customerService.createCustomer(dto)
    );
    }

    @Test
    void givenInvalidId_whenFindindCustomer_thenThrowsError() {
        doReturn(Optional.empty()).when(customerRepository).findById(1L);

        assertThrows(
            CustomerNotFoundError.class, 
            () -> customerService.getCustomerById(1L)
        );
    }

    @Test
    void givenNewCpf_whenCreatingCustomer_thenSavesCustomer() {
        CustomerDTO dto = new CustomerDTO("Test", "11111111111", "12345678910" );

        doReturn(false).when(customerRepository).existsByCpf(dto.getCpf());

        customerService.createCustomer(dto);

        verify(customerRepository, times(1)).save(any());
    }

}
