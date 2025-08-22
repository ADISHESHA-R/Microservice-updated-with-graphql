package com.quiz.question_service;

import com.quiz.question_service.service.QuestionService;

import com.quiz.question_service.model.Question;
import com.quiz.question_service.model.QuestionWrapper;
import com.quiz.question_service.model.Response;
import com.quiz.question_service.repository.QuestionDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestionServiceTest {

	@Mock
	private QuestionDao questionDao;

	@Mock
	private RestTemplate restTemplate;

	@InjectMocks
	private QuestionService questionService;

	private Question question;

	@BeforeEach
	void setUp() {
		question = new Question();
		question.setId(1);
		question.setQuestionTitle("What is 2+2?");
		question.setOption1("3");
		question.setOption2("4");
		question.setOption3("5");
		question.setOption4("6");
		question.setCorrectAnswer("4");
		question.setCategory("Math");
	}

	@Test
	void testGetAllQuestions() {
		when(questionDao.findAll()).thenReturn(Arrays.asList(question));

		ResponseEntity<List<Question>> response = questionService.getAllQuestions();

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).hasSize(1);
		verify(questionDao, times(1)).findAll();
	}

	@Test
	void testGetQuestionsByCategory() {
		when(questionDao.findByCategory("Math")).thenReturn(Arrays.asList(question));

		ResponseEntity<List<Question>> response = questionService.getQuestionsByCategory("Math");

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).hasSize(1);
		verify(questionDao, times(1)).findByCategory("Math");
	}

	@Test
	void testAddQuestion() {
		when(questionDao.save(question)).thenReturn(question);

		ResponseEntity<String> response = questionService.addQuestion(question);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getBody()).isEqualTo("success");
		verify(questionDao, times(1)).save(question);
	}

	@Test
	void testGetQuestionsForQuiz() {
		when(questionDao.findRandomQuestionsByCategory("Math", 1)).thenReturn(Arrays.asList(1));

		ResponseEntity<List<Integer>> response = questionService.getQuestionsForQuiz("Math", 1);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).containsExactly(1);
	}

	@Test
	void testGetQuestionsFromId() {
		when(questionDao.findById(1)).thenReturn(Optional.of(question));

		ResponseEntity<List<QuestionWrapper>> response = questionService.getQuestionsFromId(Arrays.asList(1));

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).hasSize(1);
		assertThat(response.getBody().get(0).getQuestionTitle()).isEqualTo("What is 2+2?");
	}

	@Test
	void testGetScore() {
		Response r = new Response();
		r.setId(1);
		r.setResponse("4");

		when(questionDao.findById(1)).thenReturn(Optional.of(question));

		ResponseEntity<Integer> response = questionService.getScore(Arrays.asList(r));

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isEqualTo(1);
	}

	@Test
	void testUpdateQuestion() {
		Question updated = new Question();
		updated.setQuestionTitle("Updated Q");
		updated.setOption1("A");
		updated.setOption2("B");
		updated.setOption3("C");
		updated.setOption4("D");
		updated.setCorrectAnswer("B");
		updated.setCategory("Science");

		when(questionDao.findById(1)).thenReturn(Optional.of(question));

		ResponseEntity<String> response = questionService.updateQuestion(1, updated);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isEqualTo("Question updated");
		verify(questionDao, times(1)).save(question);
	}

	@Test
	void testDeleteQuestion() {
		when(questionDao.existsById(1)).thenReturn(true);
		doNothing().when(restTemplate).delete(anyString());

		ResponseEntity<String> response = questionService.deleteQuestion(1);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isEqualTo("Question deleted successfully");
		verify(questionDao, times(1)).deleteById(1);
	}

	@Test
	void testDeleteQuestion_NotFound() {
		when(questionDao.existsById(99)).thenReturn(false);

		ResponseEntity<String> response = questionService.deleteQuestion(99);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(response.getBody()).isEqualTo("Question not found");
	}
}
