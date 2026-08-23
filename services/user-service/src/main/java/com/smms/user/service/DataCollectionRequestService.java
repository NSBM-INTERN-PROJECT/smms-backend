package com.smms.user.service;

import com.smms.user.dto.request.DataCollectionRequestCreate;
import com.smms.user.dto.response.DataCollectionRequestResponse;

import java.util.List;

public interface DataCollectionRequestService {

    // Creates the request and notifies matching students (student list currently mocked —
    // will come from a real query / Feign call once other services are integrated)
    DataCollectionRequestResponse createRequest(Long mentorUserId, DataCollectionRequestCreate request,
                                                 List<Long> matchedStudentIds);

    List<DataCollectionRequestResponse> getMyRequests(Long mentorUserId);

    DataCollectionRequestResponse getRequestWithRecipientStatus(Long requestId);
}