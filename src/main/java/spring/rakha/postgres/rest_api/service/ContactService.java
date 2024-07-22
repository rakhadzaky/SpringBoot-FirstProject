package spring.rakha.postgres.rest_api.service;

import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import spring.rakha.postgres.rest_api.entity.Contact;
import spring.rakha.postgres.rest_api.entity.User;
import spring.rakha.postgres.rest_api.model.ContactResponse;
import spring.rakha.postgres.rest_api.model.CreateContactRequest;
import spring.rakha.postgres.rest_api.model.SearchContactRequest;
import spring.rakha.postgres.rest_api.repository.ContactRepository;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j

@Service
public class ContactService {

    @Autowired
    private ValidationService validationService;

    @Autowired
    private ContactRepository contactRepository;

    @Transactional
    public ContactResponse create(User user, CreateContactRequest request) {
        validationService.validate(request);

        Contact contact = new Contact();
        contact.setFirstName(request.getFirstName());
        contact.setLastName(request.getLastName());
        contact.setPhone(request.getPhone());
        contact.setEmail(request.getEmail());
        contact.setUser(user);

        contactRepository.save(contact);

        return toContactResponse(contact);
    }

    @Transactional(readOnly = true)
    public ContactResponse get(User user, Integer contactId) {
        Contact contact = contactRepository.findFirstByUserAndId(user, contactId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact Not Found"));

        return toContactResponse(contact);
    }

    @Transactional
    public ContactResponse update(User user, CreateContactRequest request, Integer contactId) {
        validationService.validate(request);

        Contact contact = contactRepository.findFirstByUserAndId(user, contactId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact Not Found"));

        if (Objects.nonNull(request.getFirstName())){
            contact.setFirstName(request.getFirstName());
        }

        if (Objects.nonNull(request.getLastName())){
            contact.setLastName(request.getLastName());
        }

        if (Objects.nonNull(request.getPhone())){
            contact.setPhone(request.getPhone());
        }

        if (Objects.nonNull(request.getEmail())){
            contact.setEmail(request.getEmail());
        }

        contactRepository.save(contact);

        return toContactResponse(contact);
    }

    @Transactional
    public void delete(User user, Integer contactId) {
        Contact contact = contactRepository.findFirstByUserAndId(user, contactId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact Not Found"));

        contactRepository.delete(contact);
    }

    @Transactional(readOnly = true)
    public Page<ContactResponse> search(User user, SearchContactRequest request) {
        Specification<Contact> specification = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(builder.equal(root.get("user"), user));
            if (Objects.nonNull(request.getName())){
                predicates.add(builder.or(
                        builder.like(root.get("firstName"), "%"+request.getName()+"%"),
                        builder.like(root.get("lastName"), "%"+request.getName()+"%")
                ));
            }
            if (Objects.nonNull(request.getEmail())) {
                predicates.add(builder.like(root.get("email"), "%"+request.getEmail()+"%"));
            }
            if (Objects.nonNull(request.getPhone())) {
                predicates.add(builder.like(root.get("phone"), "%"+request.getPhone()+"%"));
            }

            return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
        };

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<Contact> contacts = contactRepository.findAll(specification, pageable);
        List<ContactResponse> contactResponses = contacts.getContent().stream()
                .map(this::toContactResponse)
                .toList();

        return new PageImpl<>(contactResponses, pageable, contacts.getTotalElements());
    }

    private ContactResponse toContactResponse(Contact contact){
        return ContactResponse.builder()
                .firstName(contact.getFirstName())
                .lastName(contact.getLastName())
                .email(contact.getEmail())
                .phone(contact.getPhone())
                .build();
    }
}
