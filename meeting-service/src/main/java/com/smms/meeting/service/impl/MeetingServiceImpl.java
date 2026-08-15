package com.smms.meeting.service.impl;

import com.smms.meeting.dto.request.MeetingRequest;
import com.smms.meeting.dto.response.MeetingResponse;
import com.smms.meeting.entity.Meeting;
import com.smms.meeting.repository.MeetingRepository;
import com.smms.meeting.service.MeetingService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MeetingServiceImpl implements MeetingService {

    private final MeetingRepository meetingRepository;
    private final JavaMailSender mailSender; 
    @Override
    public MeetingResponse scheduleMeeting(MeetingRequest request) {
        Meeting meeting = Meeting.builder()
                .allocationId(request.getAllocationId())
                .title(request.getTitle())
                .description(request.getDescription())
                .meetingDate(request.getMeetingDate())
                .meetingTime(request.getMeetingTime())
                .locationOrLink(request.getLocationOrLink())
                .status("SCHEDULED")
                .build();

        Meeting savedMeeting = meetingRepository.save(meeting);
        
        
        sendMeetingEmail(savedMeeting);

        return mapToResponse(savedMeeting);
    }

    private void sendMeetingEmail(Meeting meeting) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("student-email@example.com"); 
        message.setSubject("New Meeting Scheduled: " + meeting.getTitle());
        message.setText("Hi,\n\nA new meeting has been scheduled for you.\n" +
                        "Topic: " + meeting.getTitle() + "\n" +
                        "Date: " + meeting.getMeetingDate() + "\n" +
                        "Time: " + meeting.getMeetingTime() + "\n" +
                        "Link: " + meeting.getLocationOrLink());
        mailSender.send(message);
    }

    @Override
    public List<MeetingResponse> getAllMeetings() {
        return meetingRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<MeetingResponse> getMeetingsByAllocationId(Long allocationId) {
        return meetingRepository.findByAllocationId(allocationId).stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private MeetingResponse mapToResponse(Meeting meeting) {
        return MeetingResponse.builder()
                .id(meeting.getId())
                .allocationId(meeting.getAllocationId())
                .title(meeting.getTitle())
                .description(meeting.getDescription())
                .meetingDate(meeting.getMeetingDate())
                .meetingTime(meeting.getMeetingTime())
                .locationOrLink(meeting.getLocationOrLink())
                .status(meeting.getStatus())
                .createdAt(meeting.getCreatedAt())
                .build();
    }
}