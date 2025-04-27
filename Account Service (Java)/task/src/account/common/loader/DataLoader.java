package account.common.loader;

import account.auth.model.Group;
import account.auth.repository.GroupRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

@Slf4j
@Component
public class DataLoader implements CommandLineRunner {

    private final GroupRepository groupRepository;

    public DataLoader(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @Override
    public void run(String... args) {
        try {
            createRoles();
            log.info("Roles initialized successfully");
        } catch (Exception e) {
            log.error("Failed to initialize roles: {}", e.getMessage(), e);
        }
    }

    private void createRoles() {
        List<Group.RoleType> roleNames = new ArrayList<>(EnumSet.allOf(Group.RoleType.class));

        for (Group.RoleType roleName : roleNames) {
            if (!groupRepository.existsByName(roleName)) {
                groupRepository.save(new Group(roleName));
            }
        }
    }
}
