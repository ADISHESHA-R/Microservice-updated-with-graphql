package com.quiz.question_service.service;

import com.quiz.question_service.model.Question;
import com.quiz.question_service.model.QuestionWrapper;
import com.quiz.question_service.model.Response;
import com.quiz.question_service.repository.QuestionDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class QuestionService {

    private static final Logger logger = LoggerFactory.getLogger(QuestionService.class);

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private RestTemplate restTemplate;  // Inject RestTemplate bean

    public ResponseEntity<List<Question>> getAllQuestions() {
        logger.info("Fetching all questions");
        try {
            List<Question> questions = questionDao.findAll();
            logger.info("Fetched {} questions", questions.size());
            return new ResponseEntity<>(questions, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error fetching all questions", e);
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<List<Question>> getQuestionsByCategory(String category) {
        logger.info("Fetching questions by category: {}", category);
        List<Question> questions = questionDao.findByCategory(category);
        logger.info("Fetched {} questions for category '{}'", questions.size(), category);
        return new ResponseEntity<>(questions, HttpStatus.OK);
    }

    public ResponseEntity<String> addQuestion(Question question) {
        logger.info("Adding new question: {}", question);
        questionDao.save(question);
        logger.info("Question added successfully with ID {}", question.getId());
        return new ResponseEntity<>("success", HttpStatus.CREATED);
    }

    public ResponseEntity<List<Integer>> getQuestionsForQuiz(String categoryName, Integer numQuestions) {
        logger.info("Generating quiz questions for category: {}, count: {}", categoryName, numQuestions);
        List<Integer> questions = questionDao.findRandomQuestionsByCategory(categoryName, numQuestions);
        logger.info("Generated {} question IDs for quiz", questions.size());
        return new ResponseEntity<>(questions, HttpStatus.OK);
    }

    public ResponseEntity<List<QuestionWrapper>> getQuestionsFromId(List<Integer> questionIds) {
        logger.info("Fetching full question details for IDs: {}", questionIds);

        List<QuestionWrapper> wrappers = new ArrayList<>();
        for (Integer id : questionIds) {
            Question q = questionDao.findById(id).orElse(null);
            if (q != null) {
                QuestionWrapper wrapper = new QuestionWrapper();
                wrapper.setId(q.getId());
                wrapper.setQuestionTitle(q.getQuestionTitle());
                wrapper.setOption1(q.getOption1());
                wrapper.setOption2(q.getOption2());
                wrapper.setOption3(q.getOption3());
                wrapper.setOption4(q.getOption4());
                wrappers.add(wrapper);
            } else {
                logger.warn("Question with ID {} not found", id);
            }
        }

        logger.info("Returning {} wrapped questions", wrappers.size());
        return new ResponseEntity<>(wrappers, HttpStatus.OK);
    }

    public ResponseEntity<Integer> getScore(List<Response> responses) {
        logger.info("Calculating score for {} responses", responses.size());

        int right = 0;
        for (Response response : responses) {
            Question q = questionDao.findById(response.getId()).orElse(null);
            if (q != null) {
                if (response.getResponse().equals(q.getCorrectAnswer())) {
                    right++;
                }
            } else {
                logger.warn("Question not found for response ID {}", response.getId());
            }
        }

        logger.info("Score calculated: {}/{}", right, responses.size());
        return new ResponseEntity<>(right, HttpStatus.OK);
    }

    public ResponseEntity<String> updateQuestion(Integer id, Question updatedQuestion) {
        Optional<Question> existing = questionDao.findById(id);
        if (existing.isEmpty()) {
            return new ResponseEntity<>("Question not found", HttpStatus.NOT_FOUND);
        }
        Question q = existing.get();
        q.setQuestionTitle(updatedQuestion.getQuestionTitle());
        q.setOption1(updatedQuestion.getOption1());
        q.setOption2(updatedQuestion.getOption2());
        q.setOption3(updatedQuestion.getOption3());
        q.setOption4(updatedQuestion.getOption4());
        q.setCorrectAnswer(updatedQuestion.getCorrectAnswer());
        q.setCategory(updatedQuestion.getCategory());
        questionDao.save(q);
        return new ResponseEntity<>("Question updated", HttpStatus.OK);
    }

    public ResponseEntity<String> deleteQuestion(Integer id) {
        if (!questionDao.existsById(id)) {
            return new ResponseEntity<>("Question not found", HttpStatus.NOT_FOUND);
        }

        // Call quiz-service to remove question id from quizzes before deleting question to avoid FK constraints
        String url = "http://localhost:8090/quiz/remove-question/" + id;

        try {
            restTemplate.delete(url);
            logger.info("Notified quiz-service to remove question id: {}", id);
        } catch (Exception e) {
            logger.error("Error calling quiz service to remove question: {}", e.getMessage());
            return new ResponseEntity<>("Failed to update quiz service", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Delete question now safely
        questionDao.deleteById(id);
        return new ResponseEntity<>("Question deleted successfully", HttpStatus.OK);
    }
}
