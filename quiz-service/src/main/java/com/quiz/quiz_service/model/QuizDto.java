package com.quiz.quiz_service.model;

import lombok.Data;

import java.util.List;

@Data
public class QuizDto {
    String categoryName;
    Integer numQuestions;
    String title;
    private List<Integer> questionIds;

}
