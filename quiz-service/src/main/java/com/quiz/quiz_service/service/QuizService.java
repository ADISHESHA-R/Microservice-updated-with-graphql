package com.quiz.quiz_service.service;

import com.quiz.quiz_service.model.QuestionWrapper;
import com.quiz.quiz_service.model.Quiz;
import com.quiz.quiz_service.model.QuizDto;
import com.quiz.quiz_service.model.Response;
import com.quiz.quiz_service.repository.QuizDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class QuizService {

    @Autowired
    private QuizDao quizDao;

    @Autowired
    private RestTemplate restTemplate;

    private static final String QUESTION_SERVICE_BASE_URL = "http://localhost:8761/question";

    public ResponseEntity<String> createQuiz(String category, int numQ, String title) {
        String url = QUESTION_SERVICE_BASE_URL + "/generate?categoryName=" + category + "&numQuestions=" + numQ;

        ResponseEntity<List<Integer>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Integer>>() {}
        );

        List<Integer> questions = response.getBody();
        if (questions == null || questions.isEmpty()) {
            return new ResponseEntity<>("No questions found", HttpStatus.NO_CONTENT);
        }

        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setQuestionIds(questions);
        quizDao.save(quiz);

        return new ResponseEntity<>("Quiz created successfully", HttpStatus.OK);
    }

    public ResponseEntity<List<QuestionWrapper>> getQuizQuestions(Integer id) {
        Optional<Quiz> quizOpt = quizDao.findById(id);
        if (quizOpt.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Quiz quiz = quizOpt.get();
        List<Integer> questionIds = quiz.getQuestionIds();
        if (questionIds == null || questionIds.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        String url = QUESTION_SERVICE_BASE_URL + "/getQuestions";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<List<Integer>> requestEntity = new HttpEntity<>(questionIds, headers);

        ResponseEntity<List> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                List.class
        );

        List<QuestionWrapper> questions = response.getBody();
        return new ResponseEntity<>(questions, HttpStatus.OK);
    }

    public ResponseEntity<Integer> calculateResult(Integer id, List<Response> responses) {
        String url = QUESTION_SERVICE_BASE_URL + "/getScore";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<List<Response>> requestEntity = new HttpEntity<>(responses, headers);

        ResponseEntity<Integer> scoreResponse = restTemplate.postForEntity(
                url,
                requestEntity,
                Integer.class
        );

        return scoreResponse;
    }

    // UPDATE QUIZ method
    public ResponseEntity<String> updateQuiz(Integer id, QuizDto quizDto) {
        Optional<Quiz> quizOpt = quizDao.findById(id);
        if (quizOpt.isEmpty()) {
            return new ResponseEntity<>("Quiz not found", HttpStatus.NOT_FOUND);
        }

        Quiz quiz = quizOpt.get();
        // Update quiz details
        quiz.setTitle(quizDto.getTitle());

        // Option 1: If you want to regenerate questionIds based on category and numQuestions
        if (quizDto.getCategoryName() != null && quizDto.getNumQuestions() > 0) {
            String url = QUESTION_SERVICE_BASE_URL + "/generate?categoryName=" + quizDto.getCategoryName()
                    + "&numQuestions=" + quizDto.getNumQuestions();

            ResponseEntity<List<Integer>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Integer>>() {}
            );

            List<Integer> questions = response.getBody();
            if (questions != null && !questions.isEmpty()) {
                quiz.setQuestionIds(questions);
            }
        }
        // Option 2: Alternatively, allow directly setting question IDs if passed
        if (quizDto.getQuestionIds() != null && !quizDto.getQuestionIds().isEmpty()) {
            quiz.setQuestionIds(quizDto.getQuestionIds());
        }

        quizDao.save(quiz);
        return new ResponseEntity<>("Quiz updated successfully", HttpStatus.OK);
    }
    public ResponseEntity<String> removeQuestionFromAllQuizzes(Integer questionId) {
        List<Quiz> quizzes = quizDao.findAll();

        for (Quiz quiz : quizzes) {
            List<Integer> questionIds = quiz.getQuestionIds();
            if (questionIds != null && questionIds.contains(questionId)) {
                questionIds.remove(questionId);
                quiz.setQuestionIds(questionIds);
                quizDao.save(quiz);
            }
        }

        return new ResponseEntity<>("Question removed from all quizzes", HttpStatus.OK);
    }
}
