package com.smms.user.service.impl;

import com.smms.user.dto.request.StudentProfileUpdateRequest;
import com.smms.user.dto.response.StudentProfileResponse;
import com.smms.user.entity.StudentProfile;
import com.smms.user.exception.StudentProfileNotFoundException;
import com.smms.user.repository.StudentProfileRepository;
import com.smms.user.service.StudentProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudentProfileServiceImpl implements StudentProfileService {

    private final StudentProfileRepository studentProfileRepository;

    @Override
    @Transactional
    public StudentProfileResponse updateStudentProfile(Long userId, StudentProfileUpdateRequest request) {

        StudentProfile profile = studentProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new StudentProfileNotFoundException(
                        "Student profile not found for userId: " + userId));

        profile.setFullName(request.getFullName());
        profile.setStudentId(request.getStudentId());
        profile.setEmail(request.getEmail());
        profile.setPhone(request.getPhone());
        profile.setDegreeProgram(request.getDegreeProgram());
        profile.setDepartment(request.getDepartment());
        profile.setBatch(request.getBatch());
        profile.setIntake(request.getIntake());
        profile.setAcademicYear(request.getAcademicYear());

        if (request.getRiskStatus() != null) {
            profile.setRiskStatus(request.getRiskStatus());
        }
        if (request.getIsActive() != null) {
            profile.setIsActive(request.getIsActive());
        }

        StudentProfile saved = studentProfileRepository.save(profile);

        return mapToResponse(saved);
    }

    @Override
    public StudentProfileResponse getStudentProfileByUserId(Long userId) {

        StudentProfile profile = studentProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new StudentProfileNotFoundException(
                        "Student profile not found for userId: " + userId));

        return mapToResponse(profile);
    }

    private StudentProfileResponse mapToResponse(StudentProfile profile) {
        return StudentProfileResponse.builder()
                .id(profile.getId())
                .userId(profile.getUserId())
                .fullName(profile.getFullName())
                .studentId(profile.getStudentId())
                .email(profile.getEmail())
                .phone(profile.getPhone())
                .degreeProgram(profile.getDegreeProgram())
                .department(profile.getDepartment())
                .batch(profile.getBatch())
                .intake(profile.getIntake())
                .academicYear(profile.getAcademicYear())
                .riskStatus(profile.getRiskStatus())
                .isActive(profile.getIsActive())
                .build();
    }
}