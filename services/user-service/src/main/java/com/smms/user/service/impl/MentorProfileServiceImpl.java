package com.smms.user.service.impl;

import com.smms.user.dto.request.MentorProfileUpdateRequest;
import com.smms.user.dto.response.MentorProfileResponse;
import com.smms.user.entity.MentorProfile;
import com.smms.user.exception.MentorProfileNotFoundException;
import com.smms.user.repository.MentorProfileRepository;
import com.smms.user.service.MentorProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MentorProfileServiceImpl implements MentorProfileService {

    private final MentorProfileRepository mentorProfileRepository;

    @Override
    @Transactional
    public MentorProfileResponse updateMentorProfile(Long userId, MentorProfileUpdateRequest request) {

        MentorProfile profile = mentorProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new MentorProfileNotFoundException(
                        "Mentor profile not found for userId: " + userId));

        profile.setFullName(request.getFullName());
        profile.setEmployeeId(request.getEmployeeId());
        profile.setDepartment(request.getDepartment());
        profile.setSpecialization(request.getSpecialization());
        profile.setPhone(request.getPhone());
        profile.setMaxStudents(request.getMaxStudents());

        if (request.getIsActive() != null) {
            profile.setIsActive(request.getIsActive());
        }

        MentorProfile saved = mentorProfileRepository.save(profile);

        return mapToResponse(saved);
    }

    @Override
    public MentorProfileResponse getMentorProfileByUserId(Long userId) {

        MentorProfile profile = mentorProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new MentorProfileNotFoundException(
                        "Mentor profile not found for userId: " + userId));

        return mapToResponse(profile);
    }

    private MentorProfileResponse mapToResponse(MentorProfile profile) {
        return MentorProfileResponse.builder()
                .id(profile.getId())
                .userId(profile.getUserId())
                .fullName(profile.getFullName())
                .employeeId(profile.getEmployeeId())
                .department(profile.getDepartment())
                .specialization(profile.getSpecialization())
                .phone(profile.getPhone())
                .maxStudents(profile.getMaxStudents())
                .currentStudentCount(0) // TODO: fetch from allocation-service via Feign later
                .isActive(profile.getIsActive())
                .build();
    }
}