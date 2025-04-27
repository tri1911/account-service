package account.user.controller;

import account.auth.dto.response.UserSummary;
import account.auth.dto.request.UserRolesUpdateRequest;
import account.auth.dto.response.DeleteUserResponse;
import account.user.dto.AccessUpdateRequest;
import account.user.dto.AccessUpdateResponse;
import account.user.model.AppUser;
import account.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/")
    public List<UserSummary> getUsers() {
        return userService.getAllUsers();
    }

    @PutMapping("/role")
    public UserSummary changeUserRoles(@RequestBody @Valid UserRolesUpdateRequest request) {
        AppUser updatedUser = userService.updateUserRoles(request);
        return UserSummary.from(updatedUser);
    }

    @DeleteMapping(path = "/{email}")
    public DeleteUserResponse deleteUser(@PathVariable String email) {
        userService.deleteUserByEmail(email);
        return DeleteUserResponse.succeed(email);
    }

    @PutMapping("/access")
    public AccessUpdateResponse updateUserAccess(@RequestBody @Valid AccessUpdateRequest request) {
        userService.updateUserAccess(request);
        return AccessUpdateResponse.success(request.user().toLowerCase(), request.operation());
    }
}
