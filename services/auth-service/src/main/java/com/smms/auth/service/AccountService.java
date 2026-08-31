package com.smms.auth.service;

import com.smms.auth.domain.AuditAction;
import com.smms.auth.domain.AuditLog;
import com.smms.auth.domain.User;
import com.smms.auth.domain.UserStatus;
import com.smms.auth.dto.request.CreateAccountRequest;
import com.smms.auth.dto.request.ResetPasswordRequest;
import com.smms.auth.dto.request.UpdateAccountRequest;
import com.smms.auth.dto.response.AccountResponse;
import com.smms.auth.dto.response.PagedResponse;
import com.smms.auth.exception.AccountNotFoundException;
import com.smms.auth.exception.EmailAlreadyExistsException;
import com.smms.auth.repository.AuditLogRepository;
import com.smms.auth.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final AuditLogRepository auditLogRepository;

    @Transactional
    public AccountResponse createAccount(CreateAccountRequest request,
                                         Long adminId, HttpServletRequest httpRequest) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        // Derive username from email prefix
        String username = request.getEmail().split("@")[0]
                .replaceAll("[^a-zA-Z0-9_]", "");
        // Ensure uniqueness by appending a number if needed
        if (userRepository.existsByUsername(username)) {
            username = username + System.currentTimeMillis() % 10000;
        }

        User user = User.builder()
                .email(request.getEmail())
                .username(username)
                .fullName(request.getFullName())
                .passwordHash(passwordEncoder.encode(request.getTemporaryPassword()))
                .role(request.getRole())
                .status(UserStatus.ACTIVE)
                .mustChangePassword(true)
                .isActive(true)
                .build();

        user = userRepository.save(user);

        emailService.sendWelcomeEmail(user.getEmail(), user.getFullName(),
                request.getTemporaryPassword());

        auditLog(adminId, AuditAction.ACCOUNT_CREATED, httpRequest,
                "Created account for: " + user.getEmail());

        log.info("Admin {} created account for {}", adminId, user.getEmail());
        return AccountResponse.from(user);
    }

    @Transactional(readOnly = true)
    public PagedResponse<AccountResponse> listAccounts(int page, int size) {
        Page<User> users = userRepository.findAll(
                PageRequest.of(page, size, Sort.by("createdAt").descending()));
        return PagedResponse.from(users, AccountResponse::from);
    }

    @Transactional(readOnly = true)
    public AccountResponse getAccount(Long id) {
        return userRepository.findById(id)
                .map(AccountResponse::from)
                .orElseThrow(() -> new AccountNotFoundException(id));
    }

    @Transactional
    public AccountResponse updateAccount(Long id, UpdateAccountRequest request,
                                         Long adminId, HttpServletRequest httpRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));

        String oldValues = "role=" + user.getRole() + ",status=" + user.getStatus();

        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
            user.setIsActive(request.getStatus() == UserStatus.ACTIVE);
        }

        user = userRepository.save(user);

        auditLog(adminId, AuditAction.ACCOUNT_UPDATED, httpRequest,
                "Old: " + oldValues + " | New: role=" + user.getRole() + ",status=" + user.getStatus());

        return AccountResponse.from(user);
    }

    @Transactional
    public void resetPassword(Long id, ResetPasswordRequest request,
                              Long adminId, HttpServletRequest httpRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        user.setMustChangePassword(true);
        userRepository.save(user);

        emailService.sendPasswordResetEmail(user.getEmail(), user.getFullName(),
                request.getNewPassword());

        auditLog(adminId, AuditAction.PASSWORD_RESET, httpRequest,
                "Password reset for: " + user.getEmail());
    }

    private void auditLog(Long actorId, AuditAction action,
                          HttpServletRequest request, String note) {
        AuditLog log = AuditLog.builder()
                .userId(actorId)
                .action(action)
                .ipAddress(getClientIp(request))
                .newValue(note)
                .build();
        auditLogRepository.save(log);
    }

    private String getClientIp(HttpServletRequest request) {
        if (request == null) return null;
        String forwarded = request.getHeader("X-Forwarded-For");
        return (forwarded != null) ? forwarded.split(",")[0].trim() : request.getRemoteAddr();
    }
}
