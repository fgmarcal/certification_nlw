package com.felipe.nlw.certification_nlw.modules.students.useCases;

import com.felipe.nlw.certification_nlw.modules.questions.entities.QuestionEntity;
import com.felipe.nlw.certification_nlw.modules.questions.repositories.QuestionRepository;
import com.felipe.nlw.certification_nlw.modules.students.dto.StudentCertificationAnswerDTO;
import com.felipe.nlw.certification_nlw.modules.students.dto.VerifyHasCertificationDTO;
import com.felipe.nlw.certification_nlw.modules.students.entities.AnswersCertificationEntity;
import com.felipe.nlw.certification_nlw.modules.students.entities.CertificationStudentEntity;
import com.felipe.nlw.certification_nlw.modules.students.entities.StudentEntity;
import com.felipe.nlw.certification_nlw.modules.students.repositories.CertificationStudentRepository;
import com.felipe.nlw.certification_nlw.modules.students.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class StudentCertificationAnswersUseCase {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private CertificationStudentRepository certificationStudentRepository;

    @Autowired
    private VerifyIfHasCertificationUseCase verifyIfHasCertificationUseCase;

    public CertificationStudentEntity execute(StudentCertificationAnswerDTO dto) throws Exception {

        var hasCertification = this.verifyIfHasCertificationUseCase.execute(new VerifyHasCertificationDTO(dto.getEmail(), dto.getTechnology()));

        if(hasCertification){
            throw new Exception("Você já tirou sua certificação");
        }

        List<QuestionEntity> questionEntities = questionRepository.findByTechnology(dto.getTechnology());
        List<AnswersCertificationEntity> answersCertificationList = new ArrayList<>();
        AtomicInteger correctAnswers = new AtomicInteger(0);

        dto.getQuestionsAnswers()
                .stream().forEach(questionAnswer -> {
                    var questionResult = questionEntities.stream().filter(question ->
                            question.getId().equals(questionAnswer.getQuestionID())).findFirst().get();
                    var findCorrectAlternative = questionResult.getAlternatives().stream().filter(alternative ->
                            alternative.isCorrect()).findFirst().get();

                    if(findCorrectAlternative.getId().equals(questionAnswer.getAlternativeID())){
                        questionAnswer.setCorrect(true);
                        correctAnswers.incrementAndGet();
                    }else{
                        questionAnswer.setCorrect(false);
                    }
                    var answersACertificationEntityList = AnswersCertificationEntity.builder()
                            .answerID(questionAnswer.getAlternativeID())
                            .questionID(questionAnswer.getQuestionID())
                            .isCorrect(questionAnswer.isCorrect())
                            .build();
                    answersCertificationList.add(answersACertificationEntityList);
                });

        var student = studentRepository.findByEmail(dto.getEmail());
        UUID studentId;
        if(student.isEmpty()){
            var studentCreate = StudentEntity.builder()
                    .email(dto.getEmail())
                    .build();
            var created = studentRepository.save(studentCreate);
            studentId = created.getId();
        }
        else{
            studentId = student.get().getId();
        }

        CertificationStudentEntity certificationStudentEntity = CertificationStudentEntity.builder()
                .technology(dto.getTechnology())
                .grade(correctAnswers.get())
                .studentID(studentId)
                .build();

        var certificationStudentCreated = certificationStudentRepository.save(certificationStudentEntity);

        answersCertificationList.stream().forEach(answerCertification -> {
            answerCertification.setCertificationID(certificationStudentEntity.getId());
            answerCertification.setCertificationStudentEntity(certificationStudentEntity);
        });

        certificationStudentEntity.setAnswersCertificationEntities(answersCertificationList);

        certificationStudentRepository.save(certificationStudentEntity);

        return certificationStudentCreated;
    }
}
