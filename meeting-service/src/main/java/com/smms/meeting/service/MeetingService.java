package com.smms.meeting.service;

import com.smms.meeting.dto.request.MeetingRequest;
import com.smms.meeting.dto.response.MeetingResponse;
import java.util.List;

public interface MeetingService {
    MeetingResponse scheduleMeeting(MeetingRequest request);
    List<MeetingResponse> getAllMeetings();
    List<MeetingResponse> getMeetingsByAllocationId(Long allocationId);
}