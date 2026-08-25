package com.smms.user.service;

import com.smms.user.domain.MentorProfile;
import com.smms.user.dto.request.CreateMentorProfileRequest;
import com.smms.user.dto.request.UpdateMentorProfileRequest;
import com.smms.user.dto.response.MentorProfileResponse;
import com.smms.user.dto.response.PagedResponse;
import com.smms.user.exception.DuplicateProfileException;
import com.smms.user.exception.ProfileNotFoundException;
import com.smms.user.repository.MentorProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MentorProfileService {

    private final MentorProfileRepository mentorRepo;

    @Transactional
    public MentorProfileResponse create(CreateMentorProfileRequest req) {
        if (mentorRepo.existsByUserId(req.getUserId()))
            throw new DuplicateProfileException("Mentor profile already exists for userId: " + req.getUserId());
        if (mentorRepo.existsByEmployeeId(req.getEmployeeId()))
            throw new DuplicateProfileException("Employee ID already in use: " + req.getEmployeeId());

        MentorProfile profile = MentorProfile.builder()
                .userId(req.getUserId()).fullName(req.getFullName())
                .employeeId(req.getEmployeeId()).department(req.getDepartment())
                .specialization(req.getSpecialization()).phone(req.getPhone())
                .maxStudents(req.getMaxStudents() != null ? req.getMaxStudents() : 5)
                .isActive(true).build();

        return MentorProfileResponse.from(mentorRepo.save(profile));
    }

    @Transactional(readOnly = true)
    public MentorProfileResponse getByUserId(Long userId) {
        return MentorProfileResponse.from(
                mentorRepo.findByUserId(userId).orElseThrow(() -> new ProfileNotFoundException(userId)));
    }

    @Transactional(readOnly = true)
    public PagedResponse<MentorProfileResponse> listAll(int page, int size, String department) {
        var pageable = PageRequest.of(page, size, Sort.by("fullName"));
        var result = (department != null && !department.isBlank())
                ? mentorRepo.findByIsActiveTrueAndDepartmentContainingIgnoreCase(department, pageable)
                : mentorRepo.findByIsActiveTrue(pageable);
        return PagedResponse.from(result, MentorProfileResponse::from);
    }

    @Transactional
    public MentorProfileResponse update(Long userId, UpdateMentorProfileRequest req) {
        MentorProfile profile = mentorRepo.findByUserId(userId)
                .orElseThrow(() -> new ProfileNotFoundException(userId));
        if (req.getFullName() != null)      profile.setFullName(req.getFullName());
        if (req.getDepartment() != null)    profile.setDepartment(req.getDepartment());
        if (req.getSpecialization() != null) profile.setSpecialization(req.getSpecialization());
        if (req.getPhone() != null)         profile.setPhone(req.getPhone());
        if (req.getMaxStudents() != null)   profile.setMaxStudents(req.getMaxStudents());
        if (req.getIsActive() != null)      profile.setIsActive(req.getIsActive());
        return MentorProfileResponse.from(mentorRepo.save(profile));
    }
}
