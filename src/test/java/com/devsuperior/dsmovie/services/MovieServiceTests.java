package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class MovieServiceTests {
	
	@InjectMocks
	private MovieService movieService;

	@Mock
	private MovieRepository movieRepository;

	private MovieEntity movie;
	private MovieDTO dto;
	private Long existingId,nonExistingId,dependentId;
	private PageImpl<MovieEntity> page;

	@BeforeEach
	void setUp() throws Exception{
		existingId = 1L;
		nonExistingId = 2L;
		dependentId = 3L;
		movie = MovieFactory.createMovieEntity();
		dto = MovieFactory.createMovieDTO();
		page = new PageImpl<>(List.of(movie));

		Mockito.when(movieRepository.searchByTitle(any(),(Pageable)any())).thenReturn(page);

		Mockito.when(movieRepository.findById(existingId)).thenReturn(Optional.of(movie));
		Mockito.when(movieRepository.findById(nonExistingId)).thenReturn(Optional.empty());

		Mockito.when(movieRepository.save(any())).thenReturn(movie);

		Mockito.when(movieRepository.getReferenceById(existingId)).thenReturn(movie);
		Mockito.when(movieRepository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);

		Mockito.when(movieRepository.existsById(existingId)).thenReturn(true);
		Mockito.when(movieRepository.existsById(nonExistingId)).thenReturn(false);
		Mockito.when(movieRepository.existsById(dependentId)).thenReturn(true);

		Mockito.doNothing().when(movieRepository).deleteById(existingId);
		Mockito.doThrow(DataIntegrityViolationException.class).when(movieRepository).deleteById(dependentId);
	}
	
	@Test
	public void findAllShouldReturnPagedMovieDTO() {
		Pageable pageable = PageRequest.of(0,10);
		String name = "Test Movie";
		Page<MovieDTO> result = movieService.findAll(name,pageable);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getSize(),1);
		Assertions.assertEquals(result.iterator().next().getTitle(),name);
		Mockito.verify(movieRepository).searchByTitle(any(),(Pageable) any());
	}
	
	@Test
	public void findByIdShouldReturnMovieDTOWhenIdExists() {
		MovieDTO result = movieService.findById(existingId);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getId(),existingId);
		Assertions.assertEquals(result.getTitle(),movie.getTitle());
		Mockito.verify(movieRepository).findById(existingId);
	}
	
	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		Assertions.assertThrows(ResourceNotFoundException.class,()->{
			movieService.findById(nonExistingId);
		});
	}
	
	@Test
	public void insertShouldReturnMovieDTO() {
		MovieDTO result = movieService.insert(dto);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getId(),existingId);
		Assertions.assertEquals(result.getTitle(),dto.getTitle());
		Assertions.assertDoesNotThrow(()->{
			movieService.update(existingId,dto);
		});
	}
	
	@Test
	public void updateShouldReturnMovieDTOWhenIdExists() {
		MovieDTO result = movieService.update(existingId,dto);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getId(),movie.getId());
		Assertions.assertEquals(result.getTitle(),movie.getTitle());
	}
	
	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		Assertions.assertThrows(ResourceNotFoundException.class,()->{
			movieService.update(nonExistingId,dto);
		});
		}
	
	@Test
	public void deleteShouldDoNothingWhenIdExists() {
		Assertions.assertDoesNotThrow(()->{
			movieService.delete(existingId);
		});
		Mockito.verify(movieRepository).deleteById(existingId);
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		Assertions.assertThrows(ResourceNotFoundException.class,()->{
			movieService.delete(nonExistingId);
		});
	}
	
	@Test
	public void deleteShouldThrowDatabaseExceptionWhenDependentId() {
	}
}
