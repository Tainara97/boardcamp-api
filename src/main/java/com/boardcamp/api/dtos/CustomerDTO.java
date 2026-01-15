package com.boardcamp.api.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDTO {

    @NotBlank
    private String name;

    @NotBlank
    @Pattern(regexp = "\\d{10}|\\d{11}")
    private String phone;

    @NotBlank
    @Size(min = 11, max = 11)
    private String cpf;

}
