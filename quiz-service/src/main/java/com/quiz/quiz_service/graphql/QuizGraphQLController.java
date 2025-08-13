package com.quiz.quiz_service.graphql;

import com.quiz.quiz_service.model.QuestionWrapper;
import com.quiz.quiz_service.model.QuizDto;
import com.quiz.quiz_service.model.Response;
import com.quiz.quiz_service.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class QuizGraphQLController {

    @Autowired
    private QuizService quizService;

    @MutationMapping
    public String createQuiz(@Argument QuizDto quizDto) {
        return quizService.createQuiz(
                quizDto.getCategoryName(),
                quizDto.getNumQuestions(),
                quizDto.getTitle()
        ).getBody();
    }

    @QueryMapping
    public List<QuestionWrapper> getQuizQuestions(@Argument Integer id) {
        return quizService.getQuizQuestions(id).getBody();
    }

    @MutationMapping
    public Integer submitQuiz(@Argument Integer id, @Argument List<Response> responses) {
        return quizService.calculateResult(id, responses).getBody();
    }

    @MutationMapping
    public String updateQuiz(@Argument Integer id, @Argument QuizDto quizDto) {
        return quizService.updateQuiz(id, quizDto).getBody();
    }

    @MutationMapping
    public String removeQuestionFromQuizzes(@Argument Integer questionId) {
        return quizService.removeQuestionFromAllQuizzes(questionId).getBody();
    }
}

