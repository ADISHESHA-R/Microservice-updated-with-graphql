package com.quiz.quiz_service.controller;

import com.quiz.quiz_service.model.QuestionWrapper;
import com.quiz.quiz_service.model.QuizDto;
import com.quiz.quiz_service.model.Response;
import com.quiz.quiz_service.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("quiz")
public class QuizController {

    @Autowired
    QuizService quizService;

    @PostMapping("create")
    public ResponseEntity<String> createQuiz(@RequestBody QuizDto quizDto) {
        return quizService.createQuiz(quizDto.getCategoryName(), quizDto.getNumQuestions(), quizDto.getTitle());
    }

    @GetMapping("get/{id}")
    public ResponseEntity<List<QuestionWrapper>> getQuizQuestions(@PathVariable Integer id) {
        return quizService.getQuizQuestions(id);
    }

    @PostMapping("submit/{id}")
    public ResponseEntity<Integer> submitQuiz(@PathVariable Integer id, @RequestBody List<Response> responses) {
        return quizService.calculateResult(id, responses);
    }

    // Update Quiz endpoint
    @PutMapping("update/{id}")
    public ResponseEntity<String> updateQuiz(@PathVariable Integer id, @RequestBody QuizDto quizDto) {
        return quizService.updateQuiz(id, quizDto);
    }
    @DeleteMapping("/remove-question/{questionId}")
    public ResponseEntity<String> removeQuestionFromQuizzes(@PathVariable Integer questionId) {
        return quizService.removeQuestionFromAllQuizzes(questionId);
    }
}
