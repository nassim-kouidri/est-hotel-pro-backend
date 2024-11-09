package com.ede.est_hotel_pro.service;

import com.ede.est_hotel_pro.configuration.JwtTokenUtil;
import com.ede.est_hotel_pro.dto.create.CreateAccountRequest;
import com.ede.est_hotel_pro.dto.create.LoginRequest;
import com.ede.est_hotel_pro.dto.out.AccountResponse;
import com.ede.est_hotel_pro.dto.out.LoginResponse;
import com.ede.est_hotel_pro.entity.account.AccountEntity;
import com.ede.est_hotel_pro.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.ede.est_hotel_pro.entity.account.Role.STAFF;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private static final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$";
    private static final String PHONE_NUMBER_REGEX = "^0[0-9]{9}$";

    public List<AccountEntity> getAccounts() {
        return accountRepository.findAll().stream().filter(accountEntity -> accountEntity.getRole().equals(STAFF)).toList();
    }

    public AccountEntity findAccountByName(String name) {
        return accountRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Account with name '%s' not found", name)));
    }

    public AccountEntity findAccountById(UUID accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Account with id '%s' not found", accountId)));
    }

    public AccountEntity createAccount(CreateAccountRequest createAccount) {
        checkCreateAndUpdateAccount(createAccount);
        AccountEntity accountEntity = new AccountEntity().toBuilder()
                .name(createAccount.name())
                .firstName(createAccount.firstName())
                .role(STAFF)
                .phoneNumber(createAccount.phoneNumber())
                .password(passwordEncoder.encode(createAccount.password()))
                .build();

        return accountRepository.save(accountEntity);
    }

    public LoginResponse login(LoginRequest loginRequest) {
        AccountEntity account = findAccountByName(loginRequest.name());
        if (passwordEncoder.matches(loginRequest.password(), account.getPassword())) {
            String token = jwtTokenUtil.generateToken(account.getName());
            return new LoginResponse(token, AccountResponse.toDto(account));
        } else {
            throw new IllegalArgumentException("name or password is incorrect");
        }
    }

    public AccountEntity updateAccount(UUID accountId, CreateAccountRequest updatedAccount) {
        AccountEntity account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Account with id '%s' not found", accountId)));
        checkCreateAndUpdateAccount(updatedAccount);

        account.setName(updatedAccount.name());
        account.setFirstName(updatedAccount.firstName());
        account.setPhoneNumber(updatedAccount.phoneNumber());
        account.setPassword(passwordEncoder.encode(updatedAccount.password()));

        return accountRepository.save(account);
    }

    public void deleteAccountById(UUID accountId) {
        AccountEntity account = findAccountById(accountId);
        accountRepository.delete(account);
    }

    private void checkCreateAndUpdateAccount(CreateAccountRequest createAccount) {
        if (!isPasswordValid(createAccount.password())) {
            throw new IllegalArgumentException("Password does not meet the required criteria");
        }

        if (!isPhoneNumberValid(createAccount.phoneNumber())) {
            throw new IllegalArgumentException("Phone number must have 10 digits and start with 0");
        }

        if (accountRepository.existsByPhoneNumber(createAccount.phoneNumber())) {
            throw new IllegalArgumentException("Phone number already in use");
        }

        if (accountRepository.existsByName(createAccount.name())) {
            throw new IllegalArgumentException("Account already exist");
        }
    }

    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    private boolean isPasswordValid(String password) {
        Pattern pattern = Pattern.compile(PASSWORD_REGEX);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    private boolean isPhoneNumberValid(String phoneNumber) {
        Pattern pattern = Pattern.compile(PHONE_NUMBER_REGEX);
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }
}
