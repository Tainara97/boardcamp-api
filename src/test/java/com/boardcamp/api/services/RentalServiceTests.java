package com.boardcamp.api.services;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.any;

import com.boardcamp.api.dtos.RentalDTO;
import com.boardcamp.api.errors.CustomerNotFoundError;
import com.boardcamp.api.errors.GameNotFoundError;
import com.boardcamp.api.errors.GameUnavailableError;
import com.boardcamp.api.errors.RentalAlreadyReturnedError;
import com.boardcamp.api.errors.RentalNotReturnedError;
import com.boardcamp.api.models.CustomerModel;
import com.boardcamp.api.models.GameModel;
import com.boardcamp.api.models.RentalModel;
import com.boardcamp.api.repositories.CustomerRepository;
import com.boardcamp.api.repositories.GameRepository;
import com.boardcamp.api.repositories.RentalRepository;

@ExtendWith(MockitoExtension.class)
class RentalServiceTests {

    @InjectMocks
    private RentalService rentalService;

    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private GameRepository gameRepository;

    @Test
    void givenInvalidGameId_whenCreatingRental_thenThrowsError() {
        RentalDTO dto = new RentalDTO(1L ,1L, 3);

        doReturn(Optional.empty()).when(gameRepository).findById(1L);

        assertThrows(
            GameNotFoundError.class, 
            () -> rentalService.createRental(dto)    
        );
    }

    @Test
    void givenInvalidCustomerId_whenCreatingCustomer_thenThrowsError() {
        RentalDTO dto = new RentalDTO(1L ,1L, 3);
        
        GameModel game = new GameModel(1L, "Test", null, 2, 1000);

        doReturn(Optional.of(game)).when(gameRepository).findById(1L);
        doReturn(Optional.empty()).when(customerRepository).findById(1L);

        assertThrows(
            CustomerNotFoundError.class,
            () -> rentalService.createRental(dto)
        );
    }

    @Test
    void givenNoStockAvailable_whenCreatingRental_thenThrowsError() {
        RentalDTO dto = new RentalDTO(1L, 1L, 3);

        GameModel game = new GameModel(1L, "Test", null, 1, 1000);
        CustomerModel customer = new CustomerModel(1L, "Test", "22222222222", "12345678910");

        doReturn(Optional.of(game)).when(gameRepository).findById(1L);
        doReturn(Optional.of(customer)).when(customerRepository).findById(1L);

        doReturn(1).when(rentalRepository).countByGameAndReturnDateIsNull(game);

        assertThrows(
            GameUnavailableError.class,
            () -> rentalService.createRental(dto)
        );
    }

    @Test
    void givenValidRental_whenCreatingRental_thenSavesRental() {
        RentalDTO dto = new RentalDTO(1L, 1L, 3);

        GameModel game = new GameModel(1L, "Test", null, 1, 1000);
        CustomerModel customer = new CustomerModel(1L, "Test", "22222222222", "12345678910");

        doReturn(Optional.of(game)).when(gameRepository).findById(1L);
        doReturn(Optional.of(customer)).when(customerRepository).findById(1L);
        doReturn(0).when(rentalRepository).countByGameAndReturnDateIsNull(game);

        rentalService.createRental(dto);

        verify(rentalRepository, times(1)).save(any());
    }

    @Test
    void givenReturnedRental_whenReturningRental_thenThrowsError() {
        RentalModel rental = new RentalModel();
        rental.setId(1L);
        rental.setReturnDate(LocalDate.now());

        doReturn(Optional.of(rental)).when(rentalRepository).findById(1L);

        assertThrows(
            RentalAlreadyReturnedError.class,
            () -> rentalService.returnRental(1L)
        );
    }

    @Test
    void givenNotReturnedRental_whenDeletingRental_thenThrowsError() {
        RentalModel rental = new RentalModel();
        rental.setId(1L);
        rental.setReturnDate(null);

        doReturn(Optional.of(rental)).when(rentalRepository).findById(1L);

        assertThrows(
            RentalNotReturnedError.class,
            () -> rentalService.deleteRental(1L)
        );
    }

}
