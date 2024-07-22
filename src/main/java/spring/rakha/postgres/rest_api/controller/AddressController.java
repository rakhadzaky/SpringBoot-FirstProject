package spring.rakha.postgres.rest_api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import spring.rakha.postgres.rest_api.entity.User;
import spring.rakha.postgres.rest_api.model.AddressResponse;
import spring.rakha.postgres.rest_api.model.CreateAddressRequest;
import spring.rakha.postgres.rest_api.model.WebResponse;
import spring.rakha.postgres.rest_api.service.AddressService;

import java.util.List;

@RestController
public class AddressController {

    @Autowired
    private AddressService addressService;


    @PostMapping(
            path = "/api/contacts/{contactId}/addresses",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<AddressResponse> create(User user, @RequestBody CreateAddressRequest request, @PathVariable("contactId") Integer contactId) {
        AddressResponse addressResponse = addressService.create(user, contactId, request);

        return WebResponse.<AddressResponse>builder().data(addressResponse).build();
    }

    @GetMapping(
            path = "/api/contacts/{contactId}/addresses/{addressId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<AddressResponse> get(User user,
                                            @PathVariable("contactId") Integer contactId,
                                            @PathVariable("addressId") Integer addressId){
        AddressResponse addressResponse = addressService.get(user, contactId, addressId);

        return WebResponse.<AddressResponse>builder().data(addressResponse).build();
    }

    @PutMapping(
            path = "/api/contacts/{contactId}/addresses/{addressId}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public  WebResponse<AddressResponse> update(User user,
                                             @PathVariable("contactId") Integer contactId,
                                             @PathVariable("addressId") Integer addressId,
                                             @RequestBody CreateAddressRequest request) {
        AddressResponse addressResponse = addressService.update(user, contactId, addressId, request);

        return WebResponse.<AddressResponse>builder().data(addressResponse).build();
    }

    @DeleteMapping(
            path = "/api/contacts/{contactId}/addresses/{addressId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> delete(User user,
                                      @PathVariable("contactId") Integer contactId,
                                      @PathVariable("addressId") Integer addressId) {
        addressService.delete(user, contactId, addressId);

        return WebResponse.<String>builder().data("Ok").build();
    }

    @GetMapping(
            path = "/api/contacts/{contactId}/addresses",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<AddressResponse>> list(User user,
                                                   @PathVariable("contactId") Integer contactId) {
        List<AddressResponse> addressResponseList = addressService.list(user, contactId);

        return WebResponse.<List<AddressResponse>>builder().data(addressResponseList).build();
    }
}
