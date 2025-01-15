package com.project.e_commerce.Service;

import com.project.e_commerce.Model.UserPrincipal;
import com.project.e_commerce.Model.Users;
import com.project.e_commerce.Repo.UserRepo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private UserRepo userRepo;

    public MyUserDetailsService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // Here we are performing the User Authentication
        Users user = userRepo.findByEmail(email);

        if(user == null){
            System.out.println("User Not Found!");
            throw new UsernameNotFoundException("User Not Found!");
        }

        // Here we need to return UserDetails but as it is an Interface so we created a Class for it.
        // UserPrinciple extends userDetails and itn return 3 things
        return new UserPrincipal(user);
    }
}
