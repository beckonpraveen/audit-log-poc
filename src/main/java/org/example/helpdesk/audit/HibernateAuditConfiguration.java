package org.example.helpdesk.audit;

import java.util.List;
import java.util.Map;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.spi.BootstrapContext;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.jpa.boot.spi.IntegratorProvider;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HibernateAuditConfiguration implements HibernatePropertiesCustomizer {

    private final AuditLogEventListener auditLogEventListener;

    public HibernateAuditConfiguration(AuditLogEventListener auditLogEventListener) {
        this.auditLogEventListener = auditLogEventListener;
    }

    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        Integrator integrator = new Integrator() {
            @Override
            public void integrate(
                    Metadata metadata,
                    BootstrapContext bootstrapContext,
                    SessionFactoryImplementor sessionFactory
            ) {
                EventListenerRegistry registry = sessionFactory
                        .getServiceRegistry()
                        .getService(EventListenerRegistry.class);

                registry.appendListeners(EventType.POST_INSERT, auditLogEventListener);
                registry.appendListeners(EventType.POST_UPDATE, auditLogEventListener);
                registry.appendListeners(EventType.POST_DELETE, auditLogEventListener);
            }

            @Override
            public void disintegrate(
                    SessionFactoryImplementor sessionFactory,
                    SessionFactoryServiceRegistry serviceRegistry
            ) {
                // no-op
            }
        };

        hibernateProperties.put("hibernate.integrator_provider", (IntegratorProvider) () -> List.of(integrator));
    }
}
