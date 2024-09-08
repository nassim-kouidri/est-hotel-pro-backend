package com.ede.est_hotel_pro.controller;


import com.ede.est_hotel_pro.dto.create.CreateAccountRequest;
import com.ede.est_hotel_pro.dto.create.LoginRequest;
import com.ede.est_hotel_pro.dto.out.AccountResponse;
import com.ede.est_hotel_pro.dto.out.LoginResponse;
import com.ede.est_hotel_pro.entity.account.AccountEntity;
import com.ede.est_hotel_pro.service.AccountService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Tag(name = "accounts", description = "Route to manipulate accounts")
@RestController
@RequestMapping("/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    @PreAuthorize("hasRole(T(com.ede.est_hotel_pro.entity.account.Role).ADMIN)")
    public List<AccountResponse> getAllAccounts() {
        List<AccountEntity> accounts = accountService.getAccounts();
        return accounts.stream().map(AccountResponse::toDto).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole(T(com.ede.est_hotel_pro.entity.account.Role).ADMIN)")
    public AccountResponse getAccountById(@PathVariable UUID id) {
        AccountEntity account = accountService.getAccountById(id);
        return AccountResponse.toDto(account);
    }

    @PostMapping()
    @PreAuthorize("hasRole(T(com.ede.est_hotel_pro.entity.account.Role).ADMIN)")
    public AccountResponse createAccount(@RequestBody CreateAccountRequest createAccount) {
        AccountEntity newAccount = accountService.createAccount(createAccount);
        return AccountResponse.toDto(newAccount);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        String token = accountService.login(loginRequest);
        return new LoginResponse(token, loginRequest.name(), loginRequest.password());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole(T(com.ede.est_hotel_pro.entity.account.Role).ADMIN)")
    public AccountResponse updateAccount(@PathVariable UUID id, @RequestBody CreateAccountRequest updatedAccount) {
        AccountEntity updatedEntity = accountService.updateAccount(id, updatedAccount);
        return AccountResponse.toDto(updatedEntity);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole(T(com.ede.est_hotel_pro.entity.account.Role).ADMIN)")
    public void deleteAccount(@PathVariable UUID id) {
        accountService.deleteAccount(id);
    }

    @PostMapping("/encodePassword")
    public String encodePassword(@RequestBody String password) {
        return accountService.encodePassword(password);
    }
}
