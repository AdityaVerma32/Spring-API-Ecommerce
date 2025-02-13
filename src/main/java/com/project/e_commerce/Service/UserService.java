package com.project.e_commerce.Service;

import com.project.e_commerce.DTO.UserDTO;
import com.project.e_commerce.Model.Users;
import com.project.e_commerce.Projections.UsersProjection;
import com.project.e_commerce.Repo.UserRepo;
import com.project.e_commerce.Utils.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private UserRepo userRepo;

    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public ResponseEntity<ApiResponse<Object>> fetchUserDetails(Integer id) {

        Optional<UsersProjection> return_usr = userRepo.findProjectionById(id);
        return new ResponseEntity<>(new ApiResponse<>(true, "User details", return_usr), HttpStatus.OK);
    }

    public ResponseEntity<ApiResponse<Object>> updateUserDetails(Integer id, UserDTO userDTO) {

        System.out.println("Debug 3");
        if (id == null) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Id not found.", null), HttpStatus.OK);
        }

        Optional<Users> optional_user = userRepo.findById(id);
        Users user = optional_user.get();
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        Users saved_user = userRepo.save(user);

        Optional<UsersProjection> return_user = userRepo.findProjectionById(saved_user.getId());
        return new ResponseEntity<>(new ApiResponse<>(true, "User Details Updated", return_user), HttpStatus.OK);

    }
}
