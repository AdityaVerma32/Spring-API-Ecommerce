package com.project.e_commerce.Service;

import com.project.e_commerce.Model.ShippingAddress;
import com.project.e_commerce.Model.UserPrincipal;
import com.project.e_commerce.Model.Users;
import com.project.e_commerce.Repo.ShippingAddressRepo;
import com.project.e_commerce.Repo.UserRepo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Service
public class ShippingAddressService {

    private final ShippingAddressRepo shippingAddressRepo;
    private UserRepo userRepo;

    public ShippingAddressService(ShippingAddressRepo shippingAddressRepo, UserRepo userRepo) {
        this.shippingAddressRepo = shippingAddressRepo;
        this.userRepo = userRepo;
    }

    private Integer getUserId() {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer userId = userPrincipal.getUserId();
        return userId;
    }

    public ResponseEntity<?> createNewAddress(ShippingAddress shippingAddress) {
        Optional<Users> user = userRepo.findById(getUserId());
        ShippingAddress shippingAddress1 = new ShippingAddress();
        shippingAddress1.setStreet(shippingAddress.getStreet());
        shippingAddress1.setState(shippingAddress.getState());
        shippingAddress1.setCountry(shippingAddress.getCountry());
        shippingAddress1.setCity(shippingAddress.getCity());
        shippingAddress1.setPostalCode(shippingAddress.getPostalCode());
        shippingAddress1.setUser(user.get());

        ShippingAddress savedShippingAddress = shippingAddressRepo.save(shippingAddress1);
        return new ResponseEntity<>("Address Saved Successfully.", HttpStatus.CREATED);

    }

    public ResponseEntity<?> deleteAddress(Integer id) {
        Optional<ShippingAddress> shippingAddress = shippingAddressRepo.findById(id);
        if(shippingAddress.isPresent()){
            shippingAddressRepo.deleteById(id);
            return new ResponseEntity<>("Address Deleted Successfully", HttpStatus.OK);
        }else{
            return new ResponseEntity<>("Address not Found", HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> updateAddress(Integer id, ShippingAddress shippingAddress) {
        Optional<ShippingAddress> shippingAddress1 = shippingAddressRepo.findById(id);
        if(shippingAddress1.isPresent()){
            shippingAddress1.get().setState(shippingAddress.getState());
            shippingAddress1.get().setStreet(shippingAddress.getStreet());
            shippingAddress1.get().setCity(shippingAddress.getCity());
            shippingAddress1.get().setPostalCode(shippingAddress.getPostalCode());
            shippingAddress1.get().setCountry(shippingAddress.getCountry());
            shippingAddressRepo.save(shippingAddress1.get());
            return new ResponseEntity<>("Address Updated Successfully", HttpStatus.OK);
        }else{
            return new ResponseEntity<>("Address not Found", HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> fetchAllShippingAddress() {
        Optional<Users> user = userRepo.findById(getUserId());
        List<ShippingAddress> addresses = shippingAddressRepo.findByUser(user.get());
        return new ResponseEntity<>(addresses,HttpStatus.OK);
    }
}
