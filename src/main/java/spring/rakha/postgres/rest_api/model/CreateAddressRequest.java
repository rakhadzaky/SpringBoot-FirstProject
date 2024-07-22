package spring.rakha.postgres.rest_api.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateAddressRequest {

    private String street;

    private String city;

    private String province;

    @NotNull
    private String country;

    private String postalCode;
}
