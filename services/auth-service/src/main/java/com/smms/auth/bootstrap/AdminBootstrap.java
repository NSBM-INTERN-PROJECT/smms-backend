package com.smms.auth.bootstrap;

import com.smms.auth.domain.Role;
import com.smms.auth.repository.UserRepository;
import com.smms.auth.dto.request.CreateAccountRequest;
import com.smms.auth.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminBootstrap implements ApplicationRunner {

    private final UserRepository userRepository;
    private final AccountService accountService;
    private final Environment env;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String adminEmail = env.getProperty("ADMIN_EMAIL", "admin@smms.lk");
        String adminFullName = env.getProperty("ADMIN_FULL_NAME", "System Administrator");
        String adminTempPassword = env.getProperty("ADMIN_TEMP_PASSWORD");

        if (adminTempPassword == null || adminTempPassword.isBlank()) {
            log.warn("ADMIN_TEMP_PASSWORD not set — skipping bootstrap admin creation. Set ADMIN_TEMP_PASSWORD to enable seeding.");
            return;
        }

        try {
            if (userRepository.existsByEmail(adminEmail)) {
                log.info("Admin account already exists: {}", adminEmail);
                return;
            }

            CreateAccountRequest request = new CreateAccountRequest();
            request.setEmail(adminEmail);
            request.setFullName(adminFullName);
            request.setRole(Role.ADMIN);
            request.setTemporaryPassword(adminTempPassword);

            // adminId and HttpServletRequest are null here because this action is system-initiated
            accountService.createAccount(request, null, null);
            log.info("Seeded admin account: {}", adminEmail);
        } catch (Exception ex) {
            log.error("Failed to create bootstrap admin account", ex);
            // Do not rethrow; allow application to continue startup even if seeding fails
        }
    }
}
