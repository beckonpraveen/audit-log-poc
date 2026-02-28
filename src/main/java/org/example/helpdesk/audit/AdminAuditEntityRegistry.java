package org.example.helpdesk.audit;

import java.util.Set;
import java.util.stream.Collectors;
import org.example.helpdesk.entity.AdminAuditableEntity;
import org.example.helpdesk.entity.Impact;
import org.example.helpdesk.entity.Priority;
import org.example.helpdesk.entity.SlaRule;
import org.springframework.stereotype.Component;

@Component
public class AdminAuditEntityRegistry {

    private static final Set<Class<? extends AdminAuditableEntity>> ADMIN_ENTITY_TYPES = Set.of(
            Priority.class,
            Impact.class,
            SlaRule.class
    );

    public Set<String> getAdminEntityNames() {
        return ADMIN_ENTITY_TYPES.stream().map(Class::getSimpleName).collect(Collectors.toSet());
    }

    public boolean isAdminEntityName(String entityName) {
        return ADMIN_ENTITY_TYPES.stream().map(Class::getSimpleName).anyMatch(entityName::equals);
    }
}
