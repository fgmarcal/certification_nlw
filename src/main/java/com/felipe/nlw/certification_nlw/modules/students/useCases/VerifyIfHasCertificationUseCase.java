package com.felipe.nlw.certification_nlw.modules.students.useCases;

import com.felipe.nlw.certification_nlw.modules.students.dto.VerifyHasCertificationDTO;
import com.felipe.nlw.certification_nlw.modules.students.repositories.CertificationStudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VerifyIfHasCertificationUseCase {

    @Autowired
    private CertificationStudentRepository certificationStudentRepository;

    public boolean execute(VerifyHasCertificationDTO dto){
        var search = this.certificationStudentRepository.findByStudentEmailAndTechnology(dto.getEmail(), dto.getTechnology());
        if(!search.isEmpty())
            return true;
        return false;
    }
}
