package com.project.e_commerce.Controller;

import com.project.e_commerce.DTO.UserDTO;
import com.project.e_commerce.Model.UserPrincipal;
import com.project.e_commerce.Service.UserService;
import com.project.e_commerce.Utils.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("user")
public class UserController {

    private UserService userService;

    // this function is used to get the User Id of the Current Logged In USER.
    private Integer getUserId() {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer userId = userPrincipal.getUserId();
        return userId;
    }

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("{id}")
    public ResponseEntity<ApiResponse<Object>> fetchUserDetails(@PathVariable("id") Integer id) {
        if (id != getUserId()) {
            return new ResponseEntity(new ApiResponse<>(false, "Invalid User.", null), HttpStatus.BAD_REQUEST);
        }
        return userService.fetchUserDetails(id);
    }

    @PutMapping("update/{id}")
    public ResponseEntity<ApiResponse<Object>> updateUserDetails(@PathVariable("id") Integer id, @RequestBody UserDTO userDTO){
        System.out.println("Debug 1");
        if (id != getUserId()) {
            return new ResponseEntity(new ApiResponse<>(false, "Invalid User.", null), HttpStatus.BAD_REQUEST);
        }
        System.out.println("Debug 1");
        return userService.updateUserDetails(id,userDTO);

    }

}
