package com.quiz.question_service.graphql;

import com.quiz.question_service.model.Question;
import com.quiz.question_service.model.QuestionWrapper;
import com.quiz.question_service.model.Response;
import com.quiz.question_service.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class QuestionGraphQLController {

    @Autowired
    private QuestionService questionService;

    // Queries
    @QueryMapping
    public List<Question> allQuestions() {
        return questionService.getAllQuestions().getBody();
    }

    @QueryMapping
    public List<Question> questionsByCategory(@Argument String category) {
        return questionService.getQuestionsByCategory(category).getBody();
    }

    @QueryMapping
    public List<Integer> generateQuestions(@Argument String categoryName, @Argument Integer numQuestions) {
        return questionService.getQuestionsForQuiz(categoryName, numQuestions).getBody();
    }

    @QueryMapping
    public List<QuestionWrapper> getQuestionsFromId(@Argument List<Integer> questionIds) {
        return questionService.getQuestionsFromId(questionIds).getBody();
    }

    // Mutations
    @MutationMapping
    public String addQuestion(@Argument Question question) {
        return questionService.addQuestion(question).getBody();
    }

    @MutationMapping
    public String updateQuestion(@Argument Integer id, @Argument Question question) {
        return questionService.updateQuestion(id, question).getBody();
    }

    @MutationMapping
    public String deleteQuestion(@Argument Integer id) {
        return questionService.deleteQuestion(id).getBody();
    }

    @MutationMapping
    public Integer getScore(@Argument List<Response> responses) {
        return questionService.getScore(responses).getBody();
    }
}
