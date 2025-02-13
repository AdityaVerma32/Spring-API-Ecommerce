package com.project.e_commerce.Projections;

import java.time.LocalDateTime;

public interface UsersProjection {

    String getEmail();
    Integer getId();
    LocalDateTime getCreatedAt();
    LocalDateTime getUpdatedAt();
    String getFirstName();
    String getLastName();

}
