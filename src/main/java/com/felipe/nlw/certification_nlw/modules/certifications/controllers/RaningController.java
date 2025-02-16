package com.felipe.nlw.certification_nlw.modules.certifications.controllers;

import com.felipe.nlw.certification_nlw.modules.certifications.useCases.Top10RankingUseCase;
import com.felipe.nlw.certification_nlw.modules.students.entities.CertificationStudentEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ranking")
public class RaningController {

    @Autowired
    private Top10RankingUseCase top10RankingUseCase;

    @GetMapping("/top10")
    public List<CertificationStudentEntity> top10 (){
        return this.top10RankingUseCase.execute();
    }
}
