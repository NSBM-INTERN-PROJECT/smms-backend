package com.smms.user.service;

import com.smms.user.dto.request.StudentExtendedProfileRequest;
import com.smms.user.dto.response.StudentExtendedProfileResponse;

public interface StudentExtendedProfileService {

    // First-time submission (direct save) OR subsequent update (creates approval request)
    StudentExtendedProfileResponse submitOrUpdateExtendedProfile(
            Long studentUserId, Long mentorUserId, StudentExtendedProfileRequest request);

    StudentExtendedProfileResponse getOwnExtendedProfile(Long studentUserId);
}