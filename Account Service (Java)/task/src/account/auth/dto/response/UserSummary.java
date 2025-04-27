package account.auth.dto.response;

import account.auth.model.Group;
import account.user.model.AppUser;
import lombok.Builder;

import java.util.List;

@Builder
public record UserSummary(Long id, String name, String lastname, String email, List<String> roles) {
    public static UserSummary from(AppUser user) {
        return UserSummary.builder()
                .id(user.getId())
                .name(user.getName())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .roles(user.getRoles().stream()
                        .map(Group::getName)
                        .map(name -> "ROLE_" + name)
                        .sorted() // in ascending order
                        .toList())
                .build();
    }
}
