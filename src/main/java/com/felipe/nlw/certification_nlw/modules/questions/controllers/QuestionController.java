package com.felipe.nlw.certification_nlw.modules.questions.controllers;

import com.felipe.nlw.certification_nlw.modules.questions.dto.AlternativeResultDTO;
import com.felipe.nlw.certification_nlw.modules.questions.dto.QuestionResultDTO;
import com.felipe.nlw.certification_nlw.modules.questions.entities.AlternativesEntity;
import com.felipe.nlw.certification_nlw.modules.questions.entities.QuestionEntity;
import com.felipe.nlw.certification_nlw.modules.questions.repositories.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/questions")
public class QuestionController {

    @Autowired
    private QuestionRepository questionRepository;

    @GetMapping("/technology/{technology}")
    public List<QuestionResultDTO> findByTechnology(@PathVariable String technology){
        var result = this.questionRepository.findByTechnology(technology);

        var toMap = result.stream().map(question -> mapQuestionToDTO(question))
                .collect(Collectors.toList());

        return toMap;
    }



    static QuestionResultDTO mapQuestionToDTO(QuestionEntity question){
        var questionResultDTO = QuestionResultDTO.builder()
                .technology(question.getTechnology())
                .id(question.getId())
                .description(question.getDescription()).build();

        List<AlternativeResultDTO> alternativeResultDTOS = question.getAlternatives().stream().map(
                alternative -> mapAlternativeDTO(alternative)
        ).collect(Collectors.toList());

        questionResultDTO.setAlternatives(alternativeResultDTOS);

        return questionResultDTO;
    }

    static AlternativeResultDTO mapAlternativeDTO(AlternativesEntity alternative){
        return AlternativeResultDTO.builder()
                .id(alternative.getId())
                .description(alternative.getDescription()).build();
    }
}
