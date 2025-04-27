 package account.auth.controller;

import account.auth.dto.response.PasswordChangeResponse;
import account.auth.dto.response.UserSummary;
import account.auth.service.AuthService;
import account.auth.dto.request.ChangePasswordRequest;
import account.auth.dto.request.SignUpRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping(path = "/signup")
    public ResponseEntity<UserSummary> signUp(@RequestBody @Valid SignUpRequest request) {
        return ResponseEntity.ok(authService.signUp(request));
    }

    @PostMapping(path = "/changepass")
    public ResponseEntity<PasswordChangeResponse> changePassword(@RequestBody @Valid ChangePasswordRequest request) {
        return ResponseEntity.ok(authService.changePassword(request));
    }
}
