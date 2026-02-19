package com.billing.billingapp.customer.service;

import com.billing.billingapp.common.exception.ResourceNotFoundException;
import com.billing.billingapp.customer.dto.CustomerRequestDto;
import com.billing.billingapp.customer.dto.CustomerResponseDto;
import com.billing.billingapp.customer.entity.Customer;
import com.billing.billingapp.customer.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Long create(CustomerRequestDto dto) {
        Customer customer = new Customer();
        customer.setName(dto.getName());
        customer.setPhone(dto.getPhone());
        customer.setEmail(dto.getEmail());
        customer.setAddress(dto.getAddress());

        return customerRepository.save(customer).getId();
    }

    public List<CustomerResponseDto> getAll() {
        return customerRepository.findAllByActiveTrue()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public CustomerResponseDto getById(Long id) {
        Customer customer = customerRepository
                .findByIdAndActiveTrue(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Customer not found with id: " + id
                        )
                );

        return mapToDto(customer);
    }

    public void update(Long id, CustomerRequestDto dto) {
        Customer customer = customerRepository
                .findByIdAndActiveTrue(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Customer not found with id: " + id
                        )
                );

        customer.setName(dto.getName());
        customer.setPhone(dto.getPhone());
        customer.setEmail(dto.getEmail());
        customer.setAddress(dto.getAddress());

        customerRepository.save(customer);
    }

    public void delete(Long id) {
        Customer customer = customerRepository
                .findByIdAndActiveTrue(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Customer not found with id: " + id
                        )
                );

        customer.setActive(false);
        customerRepository.save(customer);
    }

    private CustomerResponseDto mapToDto(Customer c) {
        CustomerResponseDto dto = new CustomerResponseDto();
        dto.setId(c.getId());
        dto.setName(c.getName());
        dto.setPhone(c.getPhone());
        dto.setEmail(c.getEmail());
        dto.setAddress(c.getAddress());
        return dto;
    }
}
