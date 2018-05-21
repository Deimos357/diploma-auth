package ua.nure.tanasiuk.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import ua.nure.tanasiuk.model.UserIdentity;

public interface UserIdentityRepository extends CrudRepository<UserIdentity, Long> {
    List<UserIdentity> findByEmail(String email);
}
