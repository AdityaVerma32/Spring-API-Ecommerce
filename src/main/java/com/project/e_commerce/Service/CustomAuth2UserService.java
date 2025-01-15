//package com.project.e_commerce.Service;
//
//import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
//import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
//import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
//import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
//import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.stereotype.Service;
//
//import java.util.Map;
//
//@Service
//public class CustomAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
//
//    private AuthService authService;
//
//    public CustomAuth2UserService(AuthService authService) {
//        this.authService = authService;
//    }
//
//    @Override
//    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
//        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);
//
//        Map<String,Object> attribute = oAuth2User.getAttributes();
//
//        String email = (String) attribute.get("email");
//        String name = (String) attribute.get("name");
//
//        authService.saveOrUpdateUser(email,name);
//
//        return new DefaultOAuth2User(
//                oAuth2User.getAuthorities(),
//                attribute,
//                "email" // Key to fetch user's principal (email in this case)
//        );
//    }
//}
