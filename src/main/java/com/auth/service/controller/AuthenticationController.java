package com.auth.service.controller;

import com.auth.service.dto.ApiResponse;
import com.auth.service.exception.*;
import com.auth.service.dto.RegisterRequestDto;
import com.auth.service.model.AuthenticationRequest;
import com.auth.service.model.AuthenticationResponse;
import com.auth.service.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody RegisterRequestDto request)
            throws UserAlreadyExistsException {
        try {
            ApiResponse response = authenticationService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (UserAlreadyExistsException uae) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(uae.getMessage()));
        } catch (NotFoundException ne) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(ne.getMessage()));
        } catch (IllegalArgumentException ie) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(ie.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage()));
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody @Valid AuthenticationRequest authenticationRequest, HttpServletRequest httpServletRequest) {
        try {
            return ResponseEntity.ok(authenticationService.authenticate(authenticationRequest, httpServletRequest));
        } catch (IllegalArgumentException ie) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AuthenticationResponse(ie.getMessage()));
        } catch (InvalidCredentialsException ice) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthenticationResponse(ice.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AuthenticationResponse(e.getMessage()));
        }
    }

    @PostMapping("/refresh-token")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        authenticationService.refreshToken(request, response);
    }

    @PostMapping("/approve-registration")
    public ResponseEntity<ApiResponse> registrationApprove(@RequestBody @Valid RegisterRequestDto request)
            throws UserAlreadyExistsException {
        try {
            ApiResponse response = authenticationService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (UserAlreadyExistsException uae) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(uae.getMessage()));
        } catch (NotFoundException ne) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(ne.getMessage()));
        } catch (IllegalArgumentException ie) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(ie.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage()));
        }
    }

}
