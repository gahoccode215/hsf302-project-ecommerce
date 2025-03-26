package com.hsf302.ecommerce.repository;

import com.hsf302.ecommerce.entity.Address;
import com.hsf302.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUser(User user);
}
