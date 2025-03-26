package com.hsf302.ecommerce.service;

import com.hsf302.ecommerce.dto.request.AddressCreationRequest;
import com.hsf302.ecommerce.dto.request.AddressUpdateRequest;
import com.hsf302.ecommerce.dto.response.AddressResponse;
import com.hsf302.ecommerce.entity.Address;
import com.hsf302.ecommerce.entity.User;
import com.hsf302.ecommerce.exception.AppException;
import com.hsf302.ecommerce.exception.ErrorCode;
import com.hsf302.ecommerce.repository.AddressRepository;
import com.hsf302.ecommerce.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

public interface AddressService {
    void addAddress(AddressCreationRequest request);
    void updateAddress(Long addressId, AddressUpdateRequest request);
    void deleteAddress(Long addressId);
    List<AddressResponse> getAllAddresses();
    AddressResponse setDefaultAddress(Long addressId);
}

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
class AddressServiceImpl implements AddressService{

    AddressRepository addressRepository;
    UserRepository userRepository;

    @Override
    public void addAddress(AddressCreationRequest request) {
        User user = getAuthenticatedUser();
        if (request.getIsDefault()) {
            // Lấy tất cả các địa chỉ của người dùng
            List<Address> existingAddresses = addressRepository.findByUser(user);
            // Cập nhật tất cả địa chỉ cũ thành không mặc định
            existingAddresses.forEach(address -> address.setIsDefault(false));
            addressRepository.saveAll(existingAddresses); // Lưu tất cả các thay đổi
        }
        Address address = Address.builder()
                .name(request.getName())
                .phone(request.getPhone())
                .city(request.getCity())
                .district(request.getDistrict())
                .ward(request.getWard())
                .street(request.getStreet())
                .addressLine(request.getAddressLine())
                .isDefault(request.getIsDefault())
                .user(user)
                .build();
        addressRepository.save(address);
    }

    @Override
    public void updateAddress(Long addressId, AddressUpdateRequest request) {
        User user = getAuthenticatedUser();
        Address newaddress = addressRepository.findById(addressId)
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));
        if (request.getIsDefault()) {
            // Lấy tất cả các địa chỉ của người dùng
            List<Address> existingAddresses = addressRepository.findByUser(user);
            // Cập nhật tất cả địa chỉ cũ thành không mặc định
            existingAddresses.forEach(address -> address.setIsDefault(false));
            addressRepository.saveAll(existingAddresses); // Lưu tất cả các thay đổi
        }
        newaddress.setName(request.getName());
        newaddress.setPhone(request.getPhone());
        newaddress.setCity(request.getCity());
        newaddress.setDistrict(request.getDistrict());
        newaddress.setWard(request.getWard());
        newaddress.setStreet(request.getStreet());
        newaddress.setAddressLine(request.getAddressLine());
        newaddress.setIsDefault(request.getIsDefault());
        addressRepository.save(newaddress);
    }

    @Override
    public void deleteAddress(Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));
        addressRepository.delete(address);
    }

    @Override
    public List<AddressResponse> getAllAddresses() {
        User user = getAuthenticatedUser();
        List<Address> addresses = addressRepository.findByUser(user);
        return addresses.stream()
                .map(address -> new AddressResponse(address.getId(),
                        address.getName(), address.getPhone(), address.getCity(),
                        address.getDistrict(), address.getWard(), address.getStreet(),
                        address.getAddressLine(), address.getIsDefault()))
                .collect(Collectors.toList());
    }

    @Override
    public AddressResponse setDefaultAddress(Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));
        // Set all other addresses to non-default
        addressRepository.findByUser(getAuthenticatedUser()).forEach(addr -> addr.setIsDefault(false));
        address.setIsDefault(true);
        addressRepository.save(address);
        return new AddressResponse(address.getId(), address.getName(), address.getPhone(), address.getCity(),
                address.getDistrict(), address.getWard(), address.getStreet(), address.getAddressLine(), true);
    }
    private User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));
    }
}
