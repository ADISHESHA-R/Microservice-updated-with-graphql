package com.quiz.question_service.controller;

import com.quiz.question_service.model.Question;
import com.quiz.question_service.model.QuestionWrapper;
import com.quiz.question_service.model.Response;
import com.quiz.question_service.service.QuestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("question")
public class QuestionController {

    private static final Logger logger = LoggerFactory.getLogger(QuestionController.class);

    @Autowired
    QuestionService questionService;

    @GetMapping("allQuestions")
    public ResponseEntity<List<Question>> getAllQuestions() {
        logger.info("GET /question/allQuestions called");
        return questionService.getAllQuestions();
    }

    @GetMapping("category/{category}")
    public ResponseEntity<List<Question>> getQuestionsByCategory(@PathVariable String category) {
        logger.info("GET /question/category/{} called", category);
        return questionService.getQuestionsByCategory(category);
    }

    @PostMapping("add")
    public ResponseEntity<String> addQuestions(@RequestBody Question question) {
        logger.info("POST /question/add called with question: {}", question);
        return questionService.addQuestion(question);
    }

    @GetMapping("generate")
    public ResponseEntity<List<Integer>> getQuestionsForQuiz(
            @RequestParam String categoryName,
            @RequestParam Integer numQuestions
    ) {
        logger.info("GET /question/generate called with categoryName={}, numQuestions={}", categoryName, numQuestions);
        return questionService.getQuestionsForQuiz(categoryName, numQuestions);
    }

    @PostMapping("getQuestions")
    public ResponseEntity<List<QuestionWrapper>> getQuestionsFromId(@RequestBody List<Integer> questionIds) {
        logger.info("POST /question/getQuestions called with questionIds={}", questionIds);
        return questionService.getQuestionsFromId(questionIds);
    }

    @PostMapping("getScore")
    public ResponseEntity<Integer> getScore(@RequestBody List<Response> responses) {
        logger.info("POST /question/getScore called with {} responses", responses.size());
        return questionService.getScore(responses);
    }

//    @GetMapping("call-quiz")
//    public ResponseEntity<String> callQuizService() {
//        logger.info("GET /question/call-quiz called");
//        return questionService.callQuizService();
//    }
    @PutMapping("update/{id}")
    public ResponseEntity<String> updateQuestion(@PathVariable Integer id, @RequestBody Question question) {
        return questionService.updateQuestion(id, question);
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<String> deleteQuestion(@PathVariable Integer id) {
        return questionService.deleteQuestion(id);
    }

}
