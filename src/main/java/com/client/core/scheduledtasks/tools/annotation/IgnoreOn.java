package com.client.core.scheduledtasks.tools.annotation;

import com.client.core.scheduledtasks.model.helper.CustomSubscriptionEvent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation to mark an {@link com.client.core.scheduledtasks.workflow.node.EventTask} as ignored upon certain
 * conditions. The {@link com.client.core.scheduledtasks.service.EventWorkflowFactory} should respect this annotation and ignore events based on this criteria
 *
 * @author Tomás El Fakih
 * @see com.client.core.scheduledtasks.service.EventWorkflowFactory
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface IgnoreOn {
    /**
     * Array of private label IDs upon which to ignore this task. The PL ID is obtained by the Updating User's PL.
     *
     * @return {@link String} array of private label IDs which this {@link com.client.core.scheduledtasks.workflow.node.EventTask} should be ignored.
     * @see com.bullhornsdk.data.model.entity.core.standard.CorporateUser
     * @see com.bullhornsdk.data.model.entity.core.standard.PrivateLabel
     */
    String[] privateLabelIDs() default {};

    /**
     * Array of subscription names upon which to ignore this task. The Subscription Name is obtained by the event instance
     *
     * @return {@link String} array of subscription names which this {@link com.client.core.scheduledtasks.workflow.node.EventTask} should be ignored.
     * @see CustomSubscriptionEvent#getSubscriptionName()
     */
    String[] subscriptionNames() default {};
}
