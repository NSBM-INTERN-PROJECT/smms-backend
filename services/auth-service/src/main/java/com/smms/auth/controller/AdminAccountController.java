package com.smms.auth.controller;

import com.smms.auth.dto.request.CreateAccountRequest;
import com.smms.auth.dto.request.ResetPasswordRequest;
import com.smms.auth.dto.request.UpdateAccountRequest;
import com.smms.auth.dto.response.AccountResponse;
import com.smms.auth.dto.response.PagedResponse;
import com.smms.auth.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Admin-only account management endpoints.
 * Access controlled by @PreAuthorize which reads the role injected
 * by RoleHeaderAuthFilter (sourced from the gateway's X-User-Role header).
 */
@RestController
@RequestMapping("/api/v1/auth/admin/accounts")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin — Account Management", description = "Create and manage user accounts")
public class AdminAccountController {

    private final AccountService accountService;

    @Operation(summary = "Create a new user account (Admin only)")
    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
            @Valid @RequestBody CreateAccountRequest request,
            @RequestHeader("X-User-Id") Long adminId,
            HttpServletRequest httpRequest) {
        AccountResponse response = accountService.createAccount(request, adminId, httpRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "List all user accounts (paginated)")
    @GetMapping
    public ResponseEntity<PagedResponse<AccountResponse>> listAccounts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(accountService.listAccounts(page, size));
    }

    @Operation(summary = "Get a single user account by ID")
    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.getAccount(id));
    }

    @Operation(summary = "Update account details (role, status, name)")
    @PutMapping("/{id}")
    public ResponseEntity<AccountResponse> updateAccount(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAccountRequest request,
            @RequestHeader("X-User-Id") Long adminId,
            HttpServletRequest httpRequest) {
        return ResponseEntity.ok(accountService.updateAccount(id, request, adminId, httpRequest));
    }

    @Operation(summary = "Force-reset a user's password (Admin only)")
    @PostMapping("/{id}/reset-password")
    public ResponseEntity<Void> resetPassword(
            @PathVariable Long id,
            @Valid @RequestBody ResetPasswordRequest request,
            @RequestHeader("X-User-Id") Long adminId,
            HttpServletRequest httpRequest) {
        accountService.resetPassword(id, request, adminId, httpRequest);
        return ResponseEntity.noContent().build();
    }
}
