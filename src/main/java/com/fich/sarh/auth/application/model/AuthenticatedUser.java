package com.fich.sarh.auth.application.model;

import org.springframework.security.core.userdetails.UserDetails;

public interface AuthenticatedUser extends UserDetails {

    Long getId();

    boolean getMustChangePassword();
}
