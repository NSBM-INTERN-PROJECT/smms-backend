package com.smms.user.service;

import com.smms.user.dto.request.ProfileUpdateRejectRequest;
import com.smms.user.dto.response.ProfileUpdateRequestResponse;

import java.util.List;

public interface ProfileUpdateRequestService {

    List<ProfileUpdateRequestResponse> getPendingRequestsForMentor(Long mentorUserId);

    ProfileUpdateRequestResponse approveRequest(Long requestId);

    ProfileUpdateRequestResponse rejectRequest(Long requestId, ProfileUpdateRejectRequest rejectRequest);
}