package com.smms.user.service;

import com.smms.user.domain.StudentExtendedProfile;
import com.smms.user.dto.request.ExtendedProfileRequest;
import com.smms.user.dto.response.ExtendedProfileResponse;
import com.smms.user.repository.StudentExtendedProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service @RequiredArgsConstructor @Slf4j
public class ExtendedProfileService {

    private final StudentExtendedProfileRepository extRepo;

    @Transactional(readOnly = true)
    public ExtendedProfileResponse get(Long studentUserId) {
        return ExtendedProfileResponse.from(
                extRepo.findByStudentUserId(studentUserId)
                       .orElseGet(() -> StudentExtendedProfile.builder()
                               .studentUserId(studentUserId).formSubmitted(false).build()));
    }

    /** Student submits (or updates) their extended profile form. */
    @Transactional
    public ExtendedProfileResponse submit(Long studentUserId, ExtendedProfileRequest req) {
        StudentExtendedProfile ext = extRepo.findByStudentUserId(studentUserId)
                .orElse(StudentExtendedProfile.builder().studentUserId(studentUserId).build());

        ext.setParentName(req.getParentName());
        ext.setParentPhone(req.getParentPhone());
        ext.setParentEmail(req.getParentEmail());
        ext.setHomeDistrict(req.getHomeDistrict());
        ext.setResidenceAddress(req.getResidenceAddress());
        ext.setEmergencyContactName(req.getEmergencyContactName());
        ext.setEmergencyContactPhone(req.getEmergencyContactPhone());

        if (!Boolean.TRUE.equals(ext.getFormSubmitted())) {
            ext.setFormSubmitted(true);
            ext.setSubmittedAt(LocalDateTime.now());
        }

        return ExtendedProfileResponse.from(extRepo.save(ext));
    }
}
