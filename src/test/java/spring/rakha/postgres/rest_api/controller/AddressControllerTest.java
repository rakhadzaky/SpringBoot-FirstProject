package spring.rakha.postgres.rest_api.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import spring.rakha.postgres.rest_api.entity.Address;
import spring.rakha.postgres.rest_api.entity.Contact;
import spring.rakha.postgres.rest_api.entity.User;
import spring.rakha.postgres.rest_api.model.AddressResponse;
import spring.rakha.postgres.rest_api.model.CreateAddressRequest;
import spring.rakha.postgres.rest_api.model.WebResponse;
import spring.rakha.postgres.rest_api.repository.AddressRepository;
import spring.rakha.postgres.rest_api.repository.ContactRepository;
import spring.rakha.postgres.rest_api.repository.UserRepository;
import spring.rakha.postgres.rest_api.security.BCrypt;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j

@SpringBootTest
@AutoConfigureMockMvc
class AddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        addressRepository.deleteAll();
        contactRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setName("Test");
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() + 10000000L);
        userRepository.save(user);

        Contact contact = new Contact();
        contact.setFirstName("First Name");
        contact.setLastName("Last Name");
        contact.setPhone("0928375");
        contact.setEmail("something@example.com");
        contact.setUser(user);
        contactRepository.save(contact);
    }

    @Test
    void createAddressContactNotFound() throws Exception {
        CreateAddressRequest request = new CreateAddressRequest();
        request.setStreet("Some Street");
        request.setCity("Some City");
        request.setProvince("Some Province");
        request.setCountry("Indonesia");
        request.setPostalCode("12356");

        mockMvc.perform(
                post("/api/contacts/"+99+"/addresses")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void createAddressSuccess() throws Exception {
        User user = userRepository.findFirstByToken("test").orElseThrow();
        List<Contact> contacts = user.getContacts();
        log.info("Check contact first id: {}",contacts.size());

        Long contactsFirstId = contacts.getFirst().getId();

        log.info("Check contact first id: {}",contactsFirstId);

        CreateAddressRequest request = new CreateAddressRequest();
        request.setStreet("Some Street");
        request.setCity("Some City");
        request.setProvince("Some Province");
        request.setCountry("Indonesia");
        request.setPostalCode("12356");

        mockMvc.perform(
                post("/api/contacts/"+contactsFirstId+"/addresses")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getErrors());

            assertEquals("Some Street", response.getData().getStreet());
            assertEquals("Some City", response.getData().getCity());
            assertEquals("Some Province", response.getData().getProvince());
            assertEquals("Indonesia", response.getData().getCountry());
            assertEquals("12356", response.getData().getPostalCode());
        });
    }

    @Test
    void getAddressContactNotFound() throws Exception {
        mockMvc.perform(
                get("/api/contacts/"+99+"/addresses/"+99)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void getAddressAddressNotFound() throws Exception {
        User user = userRepository.findFirstByToken("test").orElseThrow();
        List<Contact> contacts = user.getContacts();

        mockMvc.perform(
                get("/api/contacts/"+contacts.getFirst().getId()+"/addresses/"+99)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void getAddressSuccess() throws Exception {
        User user = userRepository.findFirstByToken("test").orElseThrow();
        List<Contact> contacts = user.getContacts();

        Address address = new Address();
        address.setCountry("test country");
        address.setStreet("test street");
        address.setCity("test city");
        address.setProvince("test province");
        address.setPostalCode("test postal code");
        address.setContact(contacts.getFirst());
        addressRepository.save(address);

        mockMvc.perform(
                get("/api/contacts/"+contacts.getFirst().getId()+"/addresses/"+address.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getErrors());

            assertEquals("test country", response.getData().getCountry());
            assertEquals("test street", response.getData().getStreet());
            assertEquals("test city", response.getData().getCity());
            assertEquals("test province", response.getData().getProvince());
            assertEquals("test postal code", response.getData().getPostalCode());
        });
    }

    @Test
    void updateAddressContactNotFound() throws Exception {
        CreateAddressRequest request = new CreateAddressRequest();
        request.setStreet("Some Street");
        request.setCity("Some City");
        request.setProvince("Some Province");
        request.setCountry("Indonesia");
        request.setPostalCode("12356");

        mockMvc.perform(
                put("/api/contacts/"+99+"/addresses/"+99)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void updateAddressAddressNotFound() throws Exception {
        User user = userRepository.findFirstByToken("test").orElseThrow();
        List<Contact> contacts = user.getContacts();

        CreateAddressRequest request = new CreateAddressRequest();
        request.setStreet("Some Street");
        request.setCity("Some City");
        request.setProvince("Some Province");
        request.setCountry("Indonesia");
        request.setPostalCode("12356");

        mockMvc.perform(
                put("/api/contacts/"+contacts.getFirst().getId()+"/addresses/"+99)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void updateAddressSuccess() throws Exception {
        User user = userRepository.findFirstByToken("test").orElseThrow();
        List<Contact> contacts = user.getContacts();

        Address address = new Address();
        address.setCountry("test country");
        address.setStreet("test street");
        address.setCity("test city");
        address.setProvince("test province");
        address.setPostalCode("test postal code");
        address.setContact(contacts.getFirst());
        addressRepository.save(address);

        CreateAddressRequest request = new CreateAddressRequest();
        request.setStreet("Some Street");
        request.setCity("Some City");
        request.setProvince("Some Province");
        request.setCountry("Indonesia");
        request.setPostalCode("12356");

        mockMvc.perform(
                put("/api/contacts/"+contacts.getFirst().getId()+"/addresses/"+address.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getErrors());

            assertEquals("Some Street", response.getData().getStreet());
            assertEquals("Some City", response.getData().getCity());
            assertEquals("Some Province", response.getData().getProvince());
            assertEquals("Indonesia", response.getData().getCountry());
            assertEquals("12356", response.getData().getPostalCode());
        });
    }

    @Test
    void deleteAddressContactNotFound() throws Exception {
        mockMvc.perform(
                delete("/api/contacts/"+99+"/addresses/"+99)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void deleteAddressAddressNotFound() throws Exception {
        User user = userRepository.findFirstByToken("test").orElseThrow();
        List<Contact> contacts = user.getContacts();

        mockMvc.perform(
                delete("/api/contacts/"+contacts.getFirst().getId()+"/addresses/"+99)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void deleteAddressSuccess() throws Exception {
        User user = userRepository.findFirstByToken("test").orElseThrow();
        List<Contact> contacts = user.getContacts();

        Address address = new Address();
        address.setCountry("test country");
        address.setStreet("test street");
        address.setCity("test city");
        address.setProvince("test province");
        address.setPostalCode("test postal code");
        address.setContact(contacts.getFirst());
        addressRepository.save(address);

        mockMvc.perform(
                delete("/api/contacts/"+contacts.getFirst().getId()+"/addresses/"+address.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getErrors());
        });
    }

    @Test
    void listAddressContactNotFound() throws Exception {
        mockMvc.perform(
                get("/api/contacts/"+99+"/addresses")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void listAddressSuccess() throws Exception {
        User user = userRepository.findFirstByToken("test").orElseThrow();
        List<Contact> contacts = user.getContacts();

        Address address = new Address();
        address.setCountry("test country");
        address.setStreet("test street");
        address.setCity("test city");
        address.setProvince("test province");
        address.setPostalCode("test postal code");
        address.setContact(contacts.getFirst());
        addressRepository.save(address);

        mockMvc.perform(
                get("/api/contacts/"+contacts.getFirst().getId()+"/addresses")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<AddressResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getErrors());

            assertEquals(1, response.getData().size());
        });
    }
}