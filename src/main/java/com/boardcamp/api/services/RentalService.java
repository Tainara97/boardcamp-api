package com.boardcamp.api.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.boardcamp.api.dtos.RentalDTO;
import com.boardcamp.api.errors.CustomerNotFoundError;
import com.boardcamp.api.errors.GameNotFoundError;
import com.boardcamp.api.errors.GameUnavailableError;
import com.boardcamp.api.errors.RentalAlreadyReturnedError;
import com.boardcamp.api.errors.RentalNotFoundError;
import com.boardcamp.api.errors.RentalNotReturnedError;
import com.boardcamp.api.models.CustomerModel;
import com.boardcamp.api.models.GameModel;
import com.boardcamp.api.models.RentalModel;
import com.boardcamp.api.repositories.CustomerRepository;
import com.boardcamp.api.repositories.GameRepository;
import com.boardcamp.api.repositories.RentalRepository;

@Service
public class RentalService {
    final RentalRepository rentalRepository;
    final CustomerRepository customerRepository;
    final GameRepository gameRepository;

    RentalService(RentalRepository rentalRepository, CustomerRepository customerRepository,
            GameRepository gameRepository) {
        this.rentalRepository = rentalRepository;
        this.customerRepository = customerRepository;
        this.gameRepository = gameRepository;
    }

    public RentalModel createRental(RentalDTO body) {

        GameModel game = gameRepository
                .findById(body.getGameId())
                .orElseThrow(() -> new GameNotFoundError("Could not find game with this id"));

        CustomerModel customer = customerRepository
                .findById(body.getCustomerId())
                .orElseThrow(() -> new CustomerNotFoundError("Could not find customer with this id"));

        int openRentals = rentalRepository.countByGameAndReturnDateIsNull(game);

        if (openRentals >= game.getStockTotal()) {
            throw new GameUnavailableError("No stock available for this game");
        }

        RentalModel rental = new RentalModel();

        rental.setCustomer(customer);
        rental.setGame(game);
        rental.setDaysRented(body.getDaysRented());
        rental.setRentDate(LocalDate.now());
        rental.setReturnDate(null);
        rental.setDelayFee(0);

        int originalPrice = body.getDaysRented() * game.getPricePerDay();
        rental.setOriginalPrice(originalPrice);

        return rentalRepository.save(rental);
    }

    public List<RentalModel> getRentals() {
        return rentalRepository.findAll();
    }

    public RentalModel returnRental(Long id) {
        RentalModel rental = rentalRepository
                .findById(id)
                .orElseThrow(() -> new RentalNotFoundError("Could not find rental with this id"));

        if(rental.getReturnDate() != null) {
            throw new RentalAlreadyReturnedError("Rental already finished");
        }

        LocalDate date = LocalDate.now();
        rental.setReturnDate(date);

        LocalDate expectedReturnDate = rental.getRentDate().plusDays(rental.getDaysRented());

        if(date.isAfter(expectedReturnDate)) {
            int delayDays = expectedReturnDate.until(date).getDays();
            int delayFee = delayDays * rental.getGame().getPricePerDay();
            rental.setDelayFee(delayFee);
        } else {
            rental.setDelayFee(0);
        }

        return rentalRepository.save(rental);
        
    }

    public void deleteRental(Long id) {
        RentalModel rental = rentalRepository
                .findById(id)
                .orElseThrow(() -> new RentalNotFoundError("Could not find rental with this id"));

        if(rental.getReturnDate() == null) {
            throw new RentalNotReturnedError("Rental has not been returned yet");
        }

        rentalRepository.delete(rental);

    }
}
