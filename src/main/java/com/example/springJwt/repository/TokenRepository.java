package com.example.springJwt.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.springJwt.model.Token;

public interface TokenRepository extends JpaRepository<Token, Integer> {

	// @Query(value = "select t from Token t inner join User u on t.user.id = u.id
	// where t.user.id = :userId and t.loggedOut = false", nativeQuery = true)
	// List<Token> findAllAccessTokensByUser(Integer userId);

	@Query(value = "SELECT * FROM token WHERE user_id = :userId AND is_logged_out != 1", nativeQuery = true)
	List<Token> findActiveTokensByUserId(@Param("userId") Integer integer);

	Optional<Token> findByAccessToken(String token);

	Optional<Token> findByRefreshToken(String token);
}
