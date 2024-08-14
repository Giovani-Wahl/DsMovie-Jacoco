package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.dto.ScoreDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.entities.ScoreEntity;
import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.repositories.ScoreRepository;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;
import com.devsuperior.dsmovie.tests.ScoreFactory;
import com.devsuperior.dsmovie.tests.UserFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class ScoreServiceTests {
	
	@InjectMocks
	private ScoreService scoreService;

	@Mock
	private MovieRepository movieRepository;
	@Mock
	private ScoreRepository scoreRepository;
	@Mock
	private UserService userService;

	private MovieEntity movie;
	private UserEntity user;
	private ScoreEntity score;
	private MovieDTO movieDTO;
	private ScoreDTO scoreDTO;
	private Long existingMovieId,nonExistingMovieId;

	@BeforeEach
	void setUp() throws Exception{
		movie = MovieFactory.createMovieEntity();
		score = ScoreFactory.createScoreEntity();
		scoreDTO = ScoreFactory.createScoreDTO();
		user = UserFactory.createUserEntity();
		existingMovieId = 1L;
		nonExistingMovieId = 2L;

		Mockito.when(userService.authenticated()).thenReturn(user);

		Mockito.when(movieRepository.findById(existingMovieId)).thenReturn(Optional.of(movie));
		Mockito.when(movieRepository.findById(nonExistingMovieId)).thenReturn(Optional.empty());

		Mockito.when(movieRepository.save(any())).thenReturn(movie);

		Mockito.when(scoreRepository.saveAndFlush(any())).thenReturn(score);
	}
	
	@Test
	public void saveScoreShouldReturnMovieDTO() {
		movie.getScores().add(ScoreFactory.createScoreEntity());
		movie.getScores().add(ScoreFactory.createScoreEntity());
		MovieDTO result = scoreService.saveScore(scoreDTO);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(movie.getId(), result.getId());
		Assertions.assertEquals(movie.getTitle(), result.getTitle());
		Assertions.assertEquals(movie.getScore(), result.getScore());
		Assertions.assertEquals(movie.getCount(), result.getCount());
		Assertions.assertTrue(result.getScore()>0);

		Mockito.verify(userService).authenticated();
		Mockito.verify(movieRepository).findById(existingMovieId);
		Mockito.verify(scoreRepository).saveAndFlush(any());
		Mockito.verify(movieRepository).save(any());
	}
	
	@Test
	public void saveScoreShouldThrowResourceNotFoundExceptionWhenNonExistingMovieId() {
		scoreDTO.setMovieId(nonExistingMovieId);

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			scoreService.saveScore(scoreDTO);
		});

		Mockito.verify(userService).authenticated();
		Mockito.verify(movieRepository).findById(nonExistingMovieId);
		Mockito.verify(scoreRepository, Mockito.never()).save(any());
		Mockito.verify(movieRepository, Mockito.never()).save(any());
	}
}
