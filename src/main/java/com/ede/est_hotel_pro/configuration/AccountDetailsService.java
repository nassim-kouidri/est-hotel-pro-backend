package com.ede.est_hotel_pro.configuration;

import com.ede.est_hotel_pro.entity.account.AccountEntity;
import com.ede.est_hotel_pro.entity.account.Role;
import com.ede.est_hotel_pro.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountDetailsService implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        AccountEntity account = accountRepository.findByName(name)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("Account with name '%s' not found", name)));
        return new User(
                account.getName(),
                account.getPassword(),
                getGrantedAuthorities(account.getRole())
        );
    }

    private List<GrantedAuthority> getGrantedAuthorities(Role role) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        return authorities;
    }
}
