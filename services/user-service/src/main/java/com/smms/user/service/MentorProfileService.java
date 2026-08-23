package com.smms.user.service;

import com.smms.user.dto.request.MentorProfileUpdateRequest;
import com.smms.user.dto.response.MentorProfileResponse;

public interface MentorProfileService {

    MentorProfileResponse updateMentorProfile(Long userId, MentorProfileUpdateRequest request);

    MentorProfileResponse getMentorProfileByUserId(Long userId);
}