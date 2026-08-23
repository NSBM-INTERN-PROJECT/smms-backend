package com.smms.user.service;

import com.smms.user.dto.request.StudentProfileUpdateRequest;
import com.smms.user.dto.response.StudentProfileResponse;

public interface StudentProfileService {

    StudentProfileResponse updateStudentProfile(Long userId, StudentProfileUpdateRequest request);

    StudentProfileResponse getStudentProfileByUserId(Long userId);
}