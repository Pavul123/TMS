package com.transport.tms.master.customer.service;

import com.transport.tms.common.exception.Exceptions;
import com.transport.tms.common.util.IdGenerator;
import com.transport.tms.master.customer.entity.Customer;
import com.transport.tms.master.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final IdGenerator idGenerator;

    @Transactional(readOnly = true)
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Customer getCustomerById(String id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new Exceptions.ResourceNotFoundException("Customer", "id", id));
    }

    @Transactional(readOnly = true)
    public Customer getCustomerByPhone(String phone) {
        return customerRepository.findByPhone(phone.trim())
                .orElseThrow(() -> new Exceptions.ResourceNotFoundException("Customer", "phone", phone));
    }

    @Transactional(readOnly = true)
    public List<Customer> searchCustomers(String query) {
        return customerRepository.searchCustomers(query);
    }

    @Transactional
    public Customer createCustomer(Customer customer) {
        if (customerRepository.existsByPhone(customer.getPhone().trim())) {
            throw new Exceptions.BadRequestException("A customer with phone " + customer.getPhone() + " already exists!");
        }

        if (customer.getId() == null || customer.getId().isBlank()) {
            customer.setId(idGenerator.generateCustomerId());
        }
        customer.setPhone(customer.getPhone().trim());
        return customerRepository.save(customer);
    }

    @Transactional
    public Customer updateCustomer(String id, Customer request) {
        Customer customer = getCustomerById(id);
        
        // If phone changed, verify uniqueness
        if (!customer.getPhone().equalsIgnoreCase(request.getPhone().trim())) {
            if (customerRepository.existsByPhone(request.getPhone().trim())) {
                throw new Exceptions.BadRequestException("Phone number " + request.getPhone() + " is already assigned to another customer!");
            }
            customer.setPhone(request.getPhone().trim());
        }

        customer.setName(request.getName());
        customer.setAlternatePhone(request.getAlternatePhone());
        customer.setAddress(request.getAddress());
        customer.setGstin(request.getGstin());
        customer.setCreditTerms(request.getCreditTerms());
        customer.setStatus(request.getStatus());
        customer.setNotes(request.getNotes());

        return customerRepository.save(customer);
    }
}
