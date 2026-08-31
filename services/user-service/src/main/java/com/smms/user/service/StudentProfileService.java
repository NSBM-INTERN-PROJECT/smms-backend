package com.smms.user.service;

import com.smms.user.domain.StudentProfile;
import com.smms.user.dto.request.CreateStudentProfileRequest;
import com.smms.user.dto.request.UpdateStudentProfileRequest;
import com.smms.user.dto.response.PagedResponse;
import com.smms.user.dto.response.StudentProfileResponse;
import com.smms.user.exception.DuplicateProfileException;
import com.smms.user.exception.ProfileNotFoundException;
import com.smms.user.repository.StudentProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor @Slf4j
public class StudentProfileService {

    private final StudentProfileRepository studentRepo;

    @Transactional
    public StudentProfileResponse create(CreateStudentProfileRequest req) {
        if (studentRepo.existsByUserId(req.getUserId()))
            throw new DuplicateProfileException("Student profile already exists for userId: " + req.getUserId());
        if (studentRepo.existsByStudentId(req.getStudentId()))
            throw new DuplicateProfileException("Student ID already in use: " + req.getStudentId());

        StudentProfile profile = StudentProfile.builder()
                .userId(req.getUserId()).fullName(req.getFullName()).studentId(req.getStudentId())
                .email(req.getEmail()).phone(req.getPhone()).degreeProgram(req.getDegreeProgram())
                .department(req.getDepartment()).batch(req.getBatch()).intake(req.getIntake())
                .academicYear(req.getAcademicYear()).isActive(true).build();

        return StudentProfileResponse.from(studentRepo.save(profile));
    }

    @Transactional(readOnly = true)
    public StudentProfileResponse getByUserId(Long userId) {
        return StudentProfileResponse.from(
                studentRepo.findByUserId(userId).orElseThrow(() -> new ProfileNotFoundException(userId)));
    }

    @Transactional(readOnly = true)
    public PagedResponse<StudentProfileResponse> listAll(int page, int size,
                                                          String batch, String department, String riskStatus) {
        var pageable = PageRequest.of(page, size, Sort.by("fullName"));
        var rs = riskStatus != null ? com.smms.user.domain.RiskStatus.valueOf(riskStatus) : null;
        return PagedResponse.from(
                studentRepo.findByFilters(batch, department, rs, pageable),
                StudentProfileResponse::from);
    }

    @Transactional
    public StudentProfileResponse update(Long userId, UpdateStudentProfileRequest req) {
        StudentProfile s = studentRepo.findByUserId(userId)
                .orElseThrow(() -> new ProfileNotFoundException(userId));
        if (req.getFullName() != null)      s.setFullName(req.getFullName());
        if (req.getPhone() != null)         s.setPhone(req.getPhone());
        if (req.getDegreeProgram() != null) s.setDegreeProgram(req.getDegreeProgram());
        if (req.getDepartment() != null)    s.setDepartment(req.getDepartment());
        if (req.getBatch() != null)         s.setBatch(req.getBatch());
        if (req.getIntake() != null)        s.setIntake(req.getIntake());
        if (req.getAcademicYear() != null)  s.setAcademicYear(req.getAcademicYear());
        if (req.getRiskStatus() != null)    s.setRiskStatus(req.getRiskStatus());
        if (req.getIsActive() != null)      s.setIsActive(req.getIsActive());
        return StudentProfileResponse.from(studentRepo.save(s));
    }
}
