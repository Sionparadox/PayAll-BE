package com.example.PayAll_BE.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.PayAll_BE.entity.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
	List<Account> findAllByUserId(Long id);
}
