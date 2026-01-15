package com.boardcamp.api.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameDTO {

    @NotBlank
    private String name;
    
    private String image;

    @NotNull
    @Min(1)
    private Integer stockTotal;

    @NotNull
    @Min(1)
    private Integer pricePerDay;
}
