package account.auth.service;

import account.auth.model.Group;
import account.auth.repository.GroupRepository;
import account.common.exception.ResourceNotFound;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class GroupService {

    private final GroupRepository groupRepository;

    public GroupService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @Transactional(readOnly = true)
    public Group getGroupByName(@NonNull Group.RoleType name) {
        return groupRepository.findByName(name)
                .orElseThrow(() -> {
                    log.info("Role not found: {}", name);
                    return new ResourceNotFound("Role not found!");
                });
    }
}
