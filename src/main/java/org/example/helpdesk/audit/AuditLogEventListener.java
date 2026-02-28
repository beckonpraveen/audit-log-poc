package org.example.helpdesk.audit;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.example.helpdesk.entity.Impact;
import org.example.helpdesk.entity.Priority;
import org.example.helpdesk.entity.SlaRule;
import org.example.helpdesk.entity.Ticket;
import org.example.helpdesk.entity.TicketComment;
import org.example.helpdesk.entity.User;
import org.example.helpdesk.security.AuditActorProvider;
import org.hibernate.event.spi.PostDeleteEvent;
import org.hibernate.event.spi.PostDeleteEventListener;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.stereotype.Component;

@Component
public class AuditLogEventListener implements PostInsertEventListener, PostUpdateEventListener, PostDeleteEventListener {

    private static final Set<String> AUDITED_ENTITIES = Set.of(
            User.class.getName(),
            Ticket.class.getName(),
            TicketComment.class.getName(),
            Priority.class.getName(),
            Impact.class.getName(),
            SlaRule.class.getName()
    );

    private final AuditLogWriter auditLogWriter;
    private final AuditActorProvider auditActorProvider;

    public AuditLogEventListener(AuditLogWriter auditLogWriter, AuditActorProvider auditActorProvider) {
        this.auditLogWriter = auditLogWriter;
        this.auditActorProvider = auditActorProvider;
    }

    @Override
    public void onPostInsert(PostInsertEvent event) {
        if (!isAudited(event.getEntity())) {
            return;
        }
        Map<String, Object> after = extractState(event.getPersister().getPropertyNames(), event.getState(), event.getId());
        ParentRef parentRef = resolveParent(event.getEntity(), after);
        auditLogWriter.write(
                event.getEntity().getClass().getSimpleName(),
                String.valueOf(event.getId()),
                parentRef.name(),
                parentRef.id(),
                AuditOperation.CREATE,
                auditActorProvider.getCurrentActor(),
                after
        );
    }

    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        if (!isAudited(event.getEntity())) {
            return;
        }
        Map<String, Object> changes = extractDirtyState(
                event.getPersister().getPropertyNames(),
                event.getOldState(),
                event.getState(),
                event.getDirtyProperties()
        );
        if (changes.isEmpty()) {
            return;
        }
        Map<String, Object> after = extractState(event.getPersister().getPropertyNames(), event.getState(), event.getId());
        ParentRef parentRef = resolveParent(event.getEntity(), after);
        auditLogWriter.write(
                event.getEntity().getClass().getSimpleName(),
                String.valueOf(event.getId()),
                parentRef.name(),
                parentRef.id(),
                AuditOperation.UPDATE,
                auditActorProvider.getCurrentActor(),
                changes
        );
    }

    @Override
    public void onPostDelete(PostDeleteEvent event) {
        if (!isAudited(event.getEntity())) {
            return;
        }
        Map<String, Object> before = extractState(event.getPersister().getPropertyNames(), event.getDeletedState(), event.getId());
        ParentRef parentRef = resolveParent(event.getEntity(), before);
        auditLogWriter.write(
                event.getEntity().getClass().getSimpleName(),
                String.valueOf(event.getId()),
                parentRef.name(),
                parentRef.id(),
                AuditOperation.DELETE,
                auditActorProvider.getCurrentActor(),
                before
        );
    }

    @Override
    public boolean requiresPostCommitHandling(EntityPersister persister) {
        return false;
    }

    private boolean isAudited(Object entity) {
        return AUDITED_ENTITIES.contains(entity.getClass().getName());
    }

    private Map<String, Object> extractState(String[] propertyNames, Object[] state, Object id) {
        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("id", normalizeIdentifier(id));
        if (propertyNames == null || state == null) {
            return snapshot;
        }

        int limit = Math.min(propertyNames.length, state.length);
        for (int i = 0; i < limit; i++) {
            snapshot.put(propertyNames[i], normalizeValue(propertyNames[i], state[i]));
        }
        if (propertyNames.length > limit) {
            Arrays.stream(propertyNames, limit, propertyNames.length)
                    .forEach(name -> snapshot.put(name, null));
        }
        return snapshot;
    }

    private Map<String, Object> extractDirtyState(
            String[] propertyNames,
            Object[] oldState,
            Object[] state,
            int[] dirtyProperties
    ) {
        Map<String, Object> changes = new LinkedHashMap<>();
        if (propertyNames == null || state == null || oldState == null || dirtyProperties == null) {
            return changes;
        }

        for (int dirtyIndex : dirtyProperties) {
            if (dirtyIndex < 0 || dirtyIndex >= propertyNames.length || dirtyIndex >= oldState.length || dirtyIndex >= state.length) {
                continue;
            }
            Map<String, Object> value = new LinkedHashMap<>();
            String property = propertyNames[dirtyIndex];
            value.put("old", normalizeValue(property, oldState[dirtyIndex]));
            value.put("new", normalizeValue(property, state[dirtyIndex]));
            changes.put(propertyNames[dirtyIndex], value);
        }
        return changes;
    }

    private Object normalizeValue(String propertyName, Object value) {
        if (value == null) {
            return null;
        }
        if ("user".equals(propertyName)) {
            return normalizeUserReference(value);
        }
        if ("priority".equals(propertyName) || "impact".equals(propertyName) || "slaRule".equals(propertyName)) {
            return normalizeNamedReference(value);
        }
        if ("ticket".equals(propertyName)) {
            Object nestedId = extractNestedEntityId(value);
            return nestedId != null ? nestedId : value;
        }
        return normalizeGeneric(value);
    }

    private Object normalizeGeneric(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Enum<?> enumValue) {
            return enumValue.name();
        }
        if (value instanceof HibernateProxy proxy) {
            return proxy.getHibernateLazyInitializer().getIdentifier();
        }

        Object nestedId = extractNestedEntityId(value);
        return nestedId != null ? nestedId : value;
    }

    private Object normalizeIdentifier(Object value) {
        return normalizeGeneric(value);
    }

    private Object normalizeUserReference(Object value) {
        Long userId = extractLongId(value);
        if (userId == null) {
            return normalizeGeneric(value);
        }

        String name = extractUserName(value);
        Map<String, Object> userRef = new LinkedHashMap<>();
        userRef.put("id", userId);
        if (name != null && !name.isBlank()) {
            userRef.put("name", name);
        }
        return userRef;
    }

    private Object normalizeNamedReference(Object value) {
        Long id = extractLongId(value);
        if (id == null) {
            return normalizeGeneric(value);
        }

        String name = extractEntityName(value);
        Map<String, Object> ref = new LinkedHashMap<>();
        ref.put("id", id);
        if (name != null && !name.isBlank()) {
            ref.put("name", name);
        }
        return ref;
    }

    private Long extractLongId(Object value) {
        Object id = value instanceof HibernateProxy proxy
                ? proxy.getHibernateLazyInitializer().getIdentifier()
                : extractNestedEntityId(value);
        if (id instanceof Number number) {
            return number.longValue();
        }
        if (id instanceof String text) {
            try {
                return Long.parseLong(text);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    private String extractUserName(Object value) {
        if (value instanceof User user) {
            return user.getName();
        }
        return extractEntityName(value);
    }

    private String extractEntityName(Object value) {
        try {
            Method nameMethod = value.getClass().getMethod("getName");
            Object name = nameMethod.invoke(value);
            return name == null ? null : String.valueOf(name);
        } catch (Exception ignored) {
            return null;
        }
    }

    private Object extractNestedEntityId(Object value) {
        try {
            Method idMethod = value.getClass().getMethod("getId");
            return idMethod.invoke(value);
        } catch (Exception ignored) {
            return null;
        }
    }

    private ParentRef resolveParent(Object entity, Map<String, Object> state) {
        if (!(entity instanceof TicketComment)) {
            return ParentRef.none();
        }
        Object ticketId = state == null ? null : state.get("ticket");
        return ticketId == null
                ? ParentRef.of("Ticket", null)
                : ParentRef.of("Ticket", String.valueOf(ticketId));
    }

    private record ParentRef(String name, String id) {
        private static ParentRef none() {
            return new ParentRef(null, null);
        }

        private static ParentRef of(String name, String id) {
            return new ParentRef(name, id);
        }
    }
}
