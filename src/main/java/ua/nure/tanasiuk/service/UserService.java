package ua.nure.tanasiuk.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ua.nure.tanasiuk.dao.UserIdentityRepository;
import ua.nure.tanasiuk.model.UserIdentity;

import lombok.extern.slf4j.Slf4j;


@Service("userDetailsService")
@Slf4j
public class UserService implements UserDetailsService {
    private final UserIdentityRepository userIdentityRepository;

    public UserService(UserIdentityRepository userIdentityRepository) {
        this.userIdentityRepository = userIdentityRepository;
    }

    @Transactional(readOnly = true)
    public List<UserIdentity> findByEmail(String email) {
        return userIdentityRepository.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        UserIdentity identity = userIdentityRepository.findById(Long.parseLong(id)).orElse(null);
        if (identity == null) {
            throw new UsernameNotFoundException("UserIdentity by id is not found!");
        }
        return identity;
    }
}
