package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.projections.UserDetailsProjection;
import com.devsuperior.dsmovie.repositories.UserRepository;
import com.devsuperior.dsmovie.tests.UserDetailsFactory;
import com.devsuperior.dsmovie.tests.UserFactory;
import com.devsuperior.dsmovie.utils.CustomUserUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
public class UserServiceTests {

	@InjectMocks
	private UserService userService;

	@Mock
	private UserRepository userRepository;
	@Mock
	private CustomUserUtil customUserUtil;

	private String existingUserNane,nonExistingUserNane;
	private UserEntity user;
	private List<UserDetailsProjection> userDetails;

	@BeforeEach
	void setUp() throws Exception{
		existingUserNane = "maria@gmail.com";
		nonExistingUserNane = "notuser@gmail.com";

		user = UserFactory.createUserEntity();
		userDetails = UserDetailsFactory.createCustomAdminUser(existingUserNane);

		Mockito.when(userRepository.findByUsername(existingUserNane)).thenReturn(Optional.of(user));
		Mockito.when(userRepository.findByUsername(nonExistingUserNane)).thenReturn(Optional.empty());

		Mockito.when(userRepository.searchUserAndRolesByUsername(existingUserNane)).thenReturn(userDetails);
		Mockito.when(userRepository.searchUserAndRolesByUsername(nonExistingUserNane)).thenReturn(new ArrayList<>());
	}


	@Test
	public void authenticatedShouldReturnUserEntityWhenUserExists() {
		Mockito.when(customUserUtil.getLoggedUsername()).thenReturn(existingUserNane);
		UserEntity result = userService.authenticated();
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getUsername(),existingUserNane);
	}

	@Test
	public void authenticatedShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() {
		Mockito.doThrow(ClassCastException.class).when(customUserUtil).getLoggedUsername();
		Assertions.assertThrows(UsernameNotFoundException.class,()->{
			userService.authenticated();
		});
	}

	@Test
	public void loadUserByUsernameShouldReturnUserDetailsWhenUserExists() {
		UserDetails result = userService.loadUserByUsername(existingUserNane);
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getUsername(),existingUserNane);
	}

	@Test
	public void loadUserByUsernameShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() {
		Assertions.assertThrows(UsernameNotFoundException.class,()->{
			userService.loadUserByUsername(nonExistingUserNane);
		});
	}
}
