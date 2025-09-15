package com.junkard.service;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.junkard.dto.AddressDTO;
import com.junkard.dto.CustomerDTO;
import com.junkard.dto.CustomerSearchResponseDTO;
import com.junkard.dto.PhoneDTO;
import com.junkard.model.Address;
import com.junkard.model.Customer;
import com.junkard.model.Phone;
import com.junkard.repository.CustomerRepository;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Transactional(readOnly = true)
    public CustomerDTO findCustomerByDocument(String document) {
        return customerRepository.findByDocument(document)
                .map(this::convertEntityToDetailDto)
                .orElseThrow(() -> new RuntimeException("Customer not found with document: " + document));
    }
    
    
    @Transactional
    public Customer createCustomer(CustomerDTO dto) {
        if (customerRepository.existsByDocument(dto.getDocument())) {
            throw new IllegalArgumentException("Error: Document is already in use!");
        }
        if (customerRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Error: Email is already in use!");
        }

        Customer customer = new Customer();
        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());
        customer.setEmail(dto.getEmail());
        customer.setDocument(dto.getDocument());

        dto.getPhones().forEach(phoneDto -> customer.getPhones().add(convertPhoneDtoToEntity(phoneDto, customer)));
        dto.getAddresses().forEach(addressDto -> customer.getAddresses().add(convertAddressDtoToEntity(addressDto, customer)));
        
        return customerRepository.save(customer);
    }
    
    @Transactional(readOnly = true)
    public Page<CustomerSearchResponseDTO> searchCustomersPaginated(String term, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Customer> customerPage = (term == null || term.trim().isEmpty())
                ? customerRepository.findAll(pageable)
                : customerRepository.searchByTerm(term, pageable);

        return customerPage.map(this::convertToSearchDto);
    }

    @Transactional(readOnly = true)
    public CustomerDTO getCustomerById(Long id) {
        return customerRepository.findById(id)
                .map(this::convertEntityToDetailDto)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
    }

    @Transactional
    public CustomerDTO updateCustomer(Long id, CustomerDTO dto) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));

        if (!customer.getDocument().equals(dto.getDocument()) && customerRepository.existsByDocument(dto.getDocument())) {
             throw new IllegalArgumentException("Error: Document is already in use by another customer!");
        }
        if (!customer.getEmail().equals(dto.getEmail()) && customerRepository.existsByEmail(dto.getEmail())) {
             throw new IllegalArgumentException("Error: Email is already in use by another customer!");
        }
        
        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());
        customer.setEmail(dto.getEmail());
        customer.setDocument(dto.getDocument());
        
        customer.getPhones().clear();
        dto.getPhones().forEach(phoneDto -> customer.getPhones().add(convertPhoneDtoToEntity(phoneDto, customer)));

        customer.getAddresses().clear();
        dto.getAddresses().forEach(addressDto -> customer.getAddresses().add(convertAddressDtoToEntity(addressDto, customer)));
        
        Customer savedCustomer = customerRepository.save(customer);
        return convertEntityToDetailDto(savedCustomer);
    }

    
    private CustomerSearchResponseDTO convertToSearchDto(Customer customer) {
        String fullName = customer.getFirstName() + " " + customer.getLastName();
        return new CustomerSearchResponseDTO(customer.getId(), fullName, customer.getEmail(), customer.getPrimaryPhone(), customer.getDocument());
    }

    private CustomerDTO convertEntityToDetailDto(Customer customer) {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(customer.getId());
        dto.setFirstName(customer.getFirstName());
        dto.setLastName(customer.getLastName());
        dto.setEmail(customer.getEmail());
        dto.setDocument(customer.getDocument());
        dto.setPhones(customer.getPhones().stream().map(this::convertPhoneEntityToDto).collect(Collectors.toList()));
        dto.setAddresses(customer.getAddresses().stream().map(this::convertAddressEntityToDto).collect(Collectors.toList()));
        return dto;
    }

    private Phone convertPhoneDtoToEntity(PhoneDTO dto, Customer customer) {
        Phone phone = new Phone();
        phone.setPhoneNumber(dto.getPhoneNumber());
        phone.setType(dto.getType());
        phone.setPrimary(dto.isPrimary());
        phone.setCustomer(customer);
        return phone;
    }

    private PhoneDTO convertPhoneEntityToDto(Phone phone) {
        PhoneDTO dto = new PhoneDTO();
        dto.setId(phone.getId());
        dto.setPhoneNumber(phone.getPhoneNumber());
        dto.setType(phone.getType());
        dto.setPrimary(phone.isPrimary());
        return dto;
    }
    
    private Address convertAddressDtoToEntity(AddressDTO dto, Customer customer) {
        Address address = new Address();
        address.setStreet(dto.getStreet());
        address.setCity(dto.getCity());
        address.setState(dto.getState());
        address.setZipCode(dto.getZipCode());
        address.setPrimary(dto.isPrimary());
        address.setCustomer(customer);
        return address;
    }

    private AddressDTO convertAddressEntityToDto(Address address) {
        AddressDTO dto = new AddressDTO();
        dto.setId(address.getId());
        dto.setStreet(address.getStreet());
        dto.setCity(address.getCity());
        dto.setState(address.getState());
        dto.setZipCode(address.getZipCode());
        dto.setPrimary(address.isPrimary());
        return dto;
    }
    
    
    
    
}

