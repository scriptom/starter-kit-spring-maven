package com.client.core.scheduledtasks.service.impl;

import com.bullhornsdk.data.api.BullhornData;
import com.bullhornsdk.data.model.entity.core.certificationrequirement.CandidateCertificationRequirement;
import com.bullhornsdk.data.model.entity.core.certificationrequirement.JobSubmissionCertificationRequirement;
import com.bullhornsdk.data.model.entity.core.standard.*;
import com.bullhornsdk.data.model.entity.core.type.BullhornEntity;
import com.bullhornsdk.data.model.entity.core.type.UpdateEntity;
import com.client.core.base.model.relatedentity.*;
import com.client.core.base.model.relatedentity.credentialing.CandidateCertificationRelatedEntity;
import com.client.core.base.model.relatedentity.credentialing.CandidateCertificationRequirementRelatedEntity;
import com.client.core.base.model.relatedentity.credentialing.JobSubmissionCertificationRequirementRelatedEntity;
import com.client.core.base.model.relatedentity.credentialing.PlacementCertificationRelatedEntity;
import com.client.core.base.util.Utility;
import com.client.core.scheduledtasks.model.helper.CustomSubscriptionEvent;
import com.client.core.scheduledtasks.model.helper.ScheduledTaskHelper;
import com.client.core.scheduledtasks.model.helper.impl.*;
import com.client.core.scheduledtasks.model.helper.impl.credentialing.CandidateCertificationRequirementScheduledTaskHelper;
import com.client.core.scheduledtasks.model.helper.impl.credentialing.CandidateCertificationScheduledTaskHelper;
import com.client.core.scheduledtasks.model.helper.impl.credentialing.JobSubmissionCertificationRequirementScheduledTaskHelper;
import com.client.core.scheduledtasks.model.helper.impl.credentialing.PlacementCertificationScheduledTaskHelper;
import com.client.core.scheduledtasks.service.EventWorkflowFactory;
import com.client.core.scheduledtasks.tools.annotation.IgnoreOn;
import com.client.core.scheduledtasks.workflow.node.EventTask;
import com.client.core.scheduledtasks.workflow.traversing.ScheduledTasksTraverser;
import com.client.core.scheduledtasks.workflow.traversing.impl.*;
import com.client.core.scheduledtasks.workflow.traversing.impl.credentialing.CandidateCertificationEventTraverser;
import com.client.core.scheduledtasks.workflow.traversing.impl.credentialing.CandidateCertificationRequirementEventTraverser;
import com.client.core.scheduledtasks.workflow.traversing.impl.credentialing.JobSubmissionCertificationRequirementEventTraverser;
import com.client.core.scheduledtasks.workflow.traversing.impl.credentialing.PlacementCertificationEventTraverser;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StandardEventWorkflowFactory implements EventWorkflowFactory {

    private final Map<Class<? extends BullhornEntity>, Map<? extends BullhornRelatedEntity, Set<String>>> allRelatedEntityFields;

    private final List<EventTask<Appointment, AppointmentScheduledTaskHelper, AppointmentEventTraverser>> appointmentNodes;
    private final List<EventTask<Candidate, CandidateScheduledTaskHelper, CandidateEventTraverser>> candidateNodes;
    private final List<EventTask<CandidateEducation, CandidateEducationScheduledTaskHelper, CandidateEducationEventTraverser>> candidateEducationNodes;
    private final List<EventTask<CandidateReference, CandidateReferenceScheduledTaskHelper, CandidateReferenceEventTraverser>> candidateReferenceNodes;
    private final List<EventTask<CandidateWorkHistory, CandidateWorkHistoryScheduledTaskHelper, CandidateWorkHistoryEventTraverser>> candidateWorkHistoryNodes;
    private final List<EventTask<ClientContact, ClientContactScheduledTaskHelper, ClientContactEventTraverser>> clientContactNodes;
    private final List<EventTask<ClientCorporation, ClientCorporationScheduledTaskHelper, ClientCorporationEventTraverser>> clientCorporationNodes;
    private final List<EventTask<CorporateUser, CorporateUserScheduledTaskHelper, CorporateUserEventTraverser>> corporateUserNodes;
    private final List<EventTask<JobOrder, JobOrderScheduledTaskHelper, JobOrderEventTraverser>> jobOrderNodes;
    private final List<EventTask<JobSubmission, JobSubmissionScheduledTaskHelper, JobSubmissionEventTraverser>> jobSubmissionNodes;
    private final List<EventTask<Lead, LeadScheduledTaskHelper, LeadEventTraverser>> leadNodes;
    private final List<EventTask<Note, NoteScheduledTaskHelper, NoteEventTraverser>> noteNodes;
    private final List<EventTask<Opportunity, OpportunityScheduledTaskHelper, OpportunityEventTraverser>> opportunityNodes;
    private final List<EventTask<PlacementChangeRequest, PlacementChangeRequestScheduledTaskHelper, PlacementChangeRequestEventTraverser>> placementChangeRequestNodes;
    private final List<EventTask<PlacementCommission, PlacementCommissionScheduledTaskHelper, PlacementCommissionEventTraverser>> placementCommissionNodes;
    private final List<EventTask<Placement, PlacementScheduledTaskHelper, PlacementEventTraverser>> placementNodes;
    private final List<EventTask<Sendout, SendoutScheduledTaskHelper, SendoutEventTraverser>> sendoutNodes;
    private final List<EventTask<Task, TaskScheduledTaskHelper, TaskEventTraverser>> taskNodes;

    private final List<EventTask<CandidateCertification, CandidateCertificationScheduledTaskHelper, CandidateCertificationEventTraverser>> candidateCertificationNodes;
    private final List<EventTask<CandidateCertificationRequirement, CandidateCertificationRequirementScheduledTaskHelper, CandidateCertificationRequirementEventTraverser>> candidateCertificationRequirementNodes;
    private final List<EventTask<JobSubmissionCertificationRequirement, JobSubmissionCertificationRequirementScheduledTaskHelper, JobSubmissionCertificationRequirementEventTraverser>> jobSubmissionCertificationRequirementNodes;
    private final List<EventTask<PlacementCertification, PlacementCertificationScheduledTaskHelper, PlacementCertificationEventTraverser>> placementCertificationNodes;

    private final BullhornData bullhornData;

    @Autowired
    public StandardEventWorkflowFactory(Optional<List<EventTask<Appointment, AppointmentScheduledTaskHelper, AppointmentEventTraverser>>> appointmentNodes, Optional<List<EventTask<Candidate, CandidateScheduledTaskHelper, CandidateEventTraverser>>> candidateNodes, Optional<List<EventTask<CandidateEducation, CandidateEducationScheduledTaskHelper, CandidateEducationEventTraverser>>> candidateEducationNodes, Optional<List<EventTask<CandidateReference, CandidateReferenceScheduledTaskHelper, CandidateReferenceEventTraverser>>> candidateReferenceNodes, Optional<List<EventTask<CandidateWorkHistory, CandidateWorkHistoryScheduledTaskHelper, CandidateWorkHistoryEventTraverser>>> candidateWorkHistoryNodes, Optional<List<EventTask<ClientContact, ClientContactScheduledTaskHelper, ClientContactEventTraverser>>> clientContactNodes, Optional<List<EventTask<ClientCorporation, ClientCorporationScheduledTaskHelper, ClientCorporationEventTraverser>>> clientCorporationNodes, Optional<List<EventTask<CorporateUser, CorporateUserScheduledTaskHelper, CorporateUserEventTraverser>>> corporateUserNodes, Optional<List<EventTask<JobOrder, JobOrderScheduledTaskHelper, JobOrderEventTraverser>>> jobOrderNodes, Optional<List<EventTask<JobSubmission, JobSubmissionScheduledTaskHelper, JobSubmissionEventTraverser>>> jobSubmissionNodes, Optional<List<EventTask<Lead, LeadScheduledTaskHelper, LeadEventTraverser>>> leadNodes, Optional<List<EventTask<Note, NoteScheduledTaskHelper, NoteEventTraverser>>> noteNodes, Optional<List<EventTask<Opportunity, OpportunityScheduledTaskHelper, OpportunityEventTraverser>>> opportunityNodes, Optional<List<EventTask<PlacementChangeRequest, PlacementChangeRequestScheduledTaskHelper, PlacementChangeRequestEventTraverser>>> placementChangeRequestNodes, Optional<List<EventTask<PlacementCommission, PlacementCommissionScheduledTaskHelper, PlacementCommissionEventTraverser>>> placementCommissionNodes, Optional<List<EventTask<Placement, PlacementScheduledTaskHelper, PlacementEventTraverser>>> placementNodes, Optional<List<EventTask<Sendout, SendoutScheduledTaskHelper, SendoutEventTraverser>>> sendoutNodes, Optional<List<EventTask<Task, TaskScheduledTaskHelper, TaskEventTraverser>>> taskNodes,
                                        Optional<List<EventTask<CandidateCertification, CandidateCertificationScheduledTaskHelper, CandidateCertificationEventTraverser>>> candidateCertificationNodes,
                                        Optional<List<EventTask<CandidateCertificationRequirement, CandidateCertificationRequirementScheduledTaskHelper, CandidateCertificationRequirementEventTraverser>>> candidateCertificationRequirementNodes,
                                        Optional<List<EventTask<JobSubmissionCertificationRequirement, JobSubmissionCertificationRequirementScheduledTaskHelper, JobSubmissionCertificationRequirementEventTraverser>>> jobSubmissionCertificationRequirementNodes,
                                        Optional<List<EventTask<PlacementCertification, PlacementCertificationScheduledTaskHelper, PlacementCertificationEventTraverser>>> placementCertificationNodes, BullhornData bullhornData) {
        this.appointmentNodes = sort(appointmentNodes);
        this.candidateNodes = sort(candidateNodes);
        this.candidateEducationNodes = sort(candidateEducationNodes);
        this.candidateReferenceNodes = sort(candidateReferenceNodes);
        this.candidateWorkHistoryNodes = sort(candidateWorkHistoryNodes);
        this.clientContactNodes = sort(clientContactNodes);
        this.clientCorporationNodes = sort(clientCorporationNodes);
        this.corporateUserNodes = sort(corporateUserNodes);
        this.jobOrderNodes = sort(jobOrderNodes);
        this.jobSubmissionNodes = sort(jobSubmissionNodes);
        this.leadNodes = sort(leadNodes);
        this.noteNodes = sort(noteNodes);
        this.opportunityNodes = sort(opportunityNodes);
        this.placementChangeRequestNodes = sort(placementChangeRequestNodes);
        this.placementCommissionNodes = sort(placementCommissionNodes);
        this.placementNodes = sort(placementNodes);
        this.sendoutNodes = sort(sendoutNodes);
        this.taskNodes = sort(taskNodes);
        this.candidateCertificationNodes = sort(candidateCertificationNodes);
        this.candidateCertificationRequirementNodes = sort(candidateCertificationRequirementNodes);
        this.jobSubmissionCertificationRequirementNodes = sort(jobSubmissionCertificationRequirementNodes);
        this.placementCertificationNodes = sort(placementCertificationNodes);
        this.bullhornData = bullhornData;
        this.allRelatedEntityFields = constructAllRelatedEntityFields();
    }

    @Override
    public <T extends BullhornEntity> void execute(Class<T> type, CustomSubscriptionEvent event) {
        if (Appointment.class.equals(type)) {
            AppointmentEventTraverser traverser = new AppointmentEventTraverser(event, allRelatedEntityFields.get(Appointment.class));

            appointmentNodes.stream()
                    .filter((eventTask) -> {
                        return !shouldEventTaskBeIgnored(eventTask, event);
                    })
                    .forEach((eventTask) -> {
                        eventTask.handle(traverser);
                    });

            handleResult(traverser);
        } else if (CandidateEducation.class.equals(type)) {
            CandidateEducationEventTraverser traverser = new CandidateEducationEventTraverser(event, allRelatedEntityFields.get(CandidateEducation.class));

            candidateEducationNodes.stream()
                    .filter((eventTask) -> {
                        return !shouldEventTaskBeIgnored(eventTask, event);
                    })
                    .forEach((eventTask) -> {
                        eventTask.handle(traverser);
                    });

            handleResult(traverser);
        } else if (Candidate.class.equals(type)) {
            CandidateEventTraverser traverser = new CandidateEventTraverser(event, allRelatedEntityFields.get(Candidate.class));

            candidateNodes.stream()
                    .filter((eventTask) -> {
                        return !shouldEventTaskBeIgnored(eventTask, event);
                    })
                    .forEach((eventTask) -> {
                        eventTask.handle(traverser);
                    });

            handleResult(traverser);
        } else if (CandidateReference.class.equals(type)) {
            CandidateReferenceEventTraverser traverser = new CandidateReferenceEventTraverser(event, allRelatedEntityFields.get(CandidateReference.class));

            candidateReferenceNodes.stream()
                    .filter((eventTask) -> {
                        return !shouldEventTaskBeIgnored(eventTask, event);
                    })
                    .forEach((eventTask) -> {
                        eventTask.handle(traverser);
                    });
        } else if (CandidateWorkHistory.class.equals(type)) {
            CandidateWorkHistoryEventTraverser traverser = new CandidateWorkHistoryEventTraverser(event, allRelatedEntityFields.get(CandidateWorkHistory.class));

            candidateWorkHistoryNodes.stream()
                    .filter((eventTask) -> {
                        return !shouldEventTaskBeIgnored(eventTask, event);
                    })
                    .forEach((eventTask) -> {
                        eventTask.handle(traverser);
                    });

            handleResult(traverser);
        } else if (ClientContact.class.equals(type)) {
            ClientContactEventTraverser traverser = new ClientContactEventTraverser(event, allRelatedEntityFields.get(ClientContact.class));

            clientContactNodes.stream()
                    .filter((eventTask) -> {
                        return !shouldEventTaskBeIgnored(eventTask, event);
                    })
                    .forEach((eventTask) -> {
                        eventTask.handle(traverser);
                    });

            handleResult(traverser);
        } else if (ClientCorporation.class.equals(type)) {
            ClientCorporationEventTraverser traverser = new ClientCorporationEventTraverser(event, allRelatedEntityFields.get(ClientCorporation.class));

            clientCorporationNodes.stream()
                    .filter((eventTask) -> {
                        return !shouldEventTaskBeIgnored(eventTask, event);
                    })
                    .forEach((eventTask) -> {
                        eventTask.handle(traverser);
                    });

            handleResult(traverser);
        } else if (CorporateUser.class.equals(type)) {
            CorporateUserEventTraverser traverser = new CorporateUserEventTraverser(event, allRelatedEntityFields.get(CorporateUser.class));

            corporateUserNodes.stream()
                    .filter((eventTask) -> {
                        return !shouldEventTaskBeIgnored(eventTask, event);
                    })
                    .forEach((eventTask) -> {
                        eventTask.handle(traverser);
                    });

            handleResult(traverser);
        } else if (JobOrder.class.equals(type)) {
            JobOrderEventTraverser traverser = new JobOrderEventTraverser(event, allRelatedEntityFields.get(JobOrder.class));

            jobOrderNodes.stream()
                    .filter((eventTask) -> {
                        return !shouldEventTaskBeIgnored(eventTask, event);
                    })
                    .forEach((eventTask) -> {
                        eventTask.handle(traverser);
                    });

            handleResult(traverser);
        } else if (JobSubmission.class.equals(type)) {
            JobSubmissionEventTraverser traverser = new JobSubmissionEventTraverser(event, allRelatedEntityFields.get(JobSubmission.class));

            jobSubmissionNodes.stream()
                    .filter((eventTask) -> {
                        return !shouldEventTaskBeIgnored(eventTask, event);
                    })
                    .forEach((eventTask) -> {
                        eventTask.handle(traverser);
                    });

            handleResult(traverser);
        } else if (Lead.class.equals(type)) {
            LeadEventTraverser traverser = new LeadEventTraverser(event, allRelatedEntityFields.get(Lead.class));

            leadNodes.stream()
                    .filter((eventTask) -> {
                        return !shouldEventTaskBeIgnored(eventTask, event);
                    })
                    .forEach((eventTask) -> {
                        eventTask.handle(traverser);
                    });

            handleResult(traverser);
        } else if (Note.class.equals(type)) {
            NoteEventTraverser traverser = new NoteEventTraverser(event, allRelatedEntityFields.get(Note.class));

            noteNodes.stream()
                    .filter((eventTask) -> {
                        return !shouldEventTaskBeIgnored(eventTask, event);
                    })
                    .forEach((eventTask) -> {
                        eventTask.handle(traverser);
                    });

            handleResult(traverser);
        } else if (Opportunity.class.equals(type)) {
            OpportunityEventTraverser traverser = new OpportunityEventTraverser(event, allRelatedEntityFields.get(Opportunity.class));

            opportunityNodes.stream()
                    .filter((eventTask) -> {
                        return !shouldEventTaskBeIgnored(eventTask, event);
                    })
                    .forEach((eventTask) -> {
                        eventTask.handle(traverser);
                    });

            handleResult(traverser);
        } else if (PlacementChangeRequest.class.equals(type)) {
            PlacementChangeRequestEventTraverser traverser = new PlacementChangeRequestEventTraverser(event, allRelatedEntityFields.get(PlacementChangeRequest.class));

            placementChangeRequestNodes.stream()
                    .filter((eventTask) -> {
                        return !shouldEventTaskBeIgnored(eventTask, event);
                    })
                    .forEach((eventTask) -> {
                        eventTask.handle(traverser);
                    });

            handleResult(traverser);
        } else if (PlacementCommission.class.equals(type)) {
            PlacementCommissionEventTraverser traverser = new PlacementCommissionEventTraverser(event, allRelatedEntityFields.get(PlacementCommission.class));

            placementCommissionNodes.stream()
                    .filter((eventTask) -> {
                        return !shouldEventTaskBeIgnored(eventTask, event);
                    })
                    .forEach((eventTask) -> {
                        eventTask.handle(traverser);
                    });

            handleResult(traverser);
        } else if (Placement.class.equals(type)) {
            PlacementEventTraverser traverser = new PlacementEventTraverser(event, allRelatedEntityFields.get(Placement.class));

            placementNodes.stream()
                    .filter((eventTask) -> {
                        return !shouldEventTaskBeIgnored(eventTask, event);
                    })
                    .forEach((eventTask) -> {
                        eventTask.handle(traverser);
                    });

            handleResult(traverser);
        } else if (Sendout.class.equals(type)) {
            SendoutEventTraverser traverser = new SendoutEventTraverser(event, allRelatedEntityFields.get(Sendout.class));

            sendoutNodes.stream()
                    .filter((eventTask) -> {
                        return !shouldEventTaskBeIgnored(eventTask, event);
                    })
                    .forEach((eventTask) -> {
                        eventTask.handle(traverser);
                    });

            handleResult(traverser);
        } else if (Task.class.equals(type)) {
            TaskEventTraverser traverser = new TaskEventTraverser(event, allRelatedEntityFields.get(Task.class));

            taskNodes.stream()
                    .filter((eventTask) -> {
                        return !shouldEventTaskBeIgnored(eventTask, event);
                    })
                    .forEach((eventTask) -> {
                        eventTask.handle(traverser);
                    });

            handleResult(traverser);
        } else if (CandidateCertification.class.equals(type)) {
            CandidateCertificationEventTraverser traverser = new CandidateCertificationEventTraverser(event, allRelatedEntityFields.get(CandidateCertification.class));

            candidateCertificationNodes.stream()
                    .filter((eventTask) -> {
                        return !shouldEventTaskBeIgnored(eventTask, event);
                    })
                    .forEach((eventTask) -> {
                        eventTask.handle(traverser);
                    });

            handleResult(traverser);
        } else if (CandidateCertificationRequirement.class.equals(type)) {
            CandidateCertificationRequirementEventTraverser traverser = new CandidateCertificationRequirementEventTraverser(event, allRelatedEntityFields.get(CandidateCertificationRequirement.class));

            candidateCertificationRequirementNodes.stream()
                    .filter((eventTask) -> {
                        return !shouldEventTaskBeIgnored(eventTask, event);
                    })
                    .forEach((eventTask) -> {
                        eventTask.handle(traverser);
                    });

            handleResult(traverser);
        } else if (JobSubmissionCertificationRequirement.class.equals(type)) {
            JobSubmissionCertificationRequirementEventTraverser traverser = new JobSubmissionCertificationRequirementEventTraverser(event, allRelatedEntityFields.get(JobSubmissionCertificationRequirement.class));

            jobSubmissionCertificationRequirementNodes.stream()
                    .filter((eventTask) -> {
                        return !shouldEventTaskBeIgnored(eventTask, event);
                    })
                    .forEach((eventTask) -> {
                        eventTask.handle(traverser);
                    });

            handleResult(traverser);
        } else if (PlacementCertification.class.equals(type)) {
            PlacementCertificationEventTraverser traverser = new PlacementCertificationEventTraverser(event, allRelatedEntityFields.get(PlacementCertification.class));

            placementCertificationNodes.stream()
                    .filter((eventTask) -> {
                        return !shouldEventTaskBeIgnored(eventTask, event);
                    })
                    .forEach((eventTask) -> {
                        eventTask.handle(traverser);
                    });

            handleResult(traverser);
        }
    }

    private <U extends UpdateEntity, E extends BullhornEntity, H extends ScheduledTaskHelper<E>, T extends ScheduledTasksTraverser<H>> void handleResult(T traverser) {
        Map<String, U> dtosToSave = traverser.getScheduledTaskHelper().getAllEntitiesToSave();

        for (Map.Entry<String, U> entry : dtosToSave.entrySet()) {
            bullhornData.updateEntity(entry.getValue());
        }
    }

    private <E extends BullhornEntity, H extends ScheduledTaskHelper<E>, T extends ScheduledTasksTraverser<H>> List<EventTask<E, H, T>> sort(Optional<List<EventTask<E, H, T>>> values) {
        return values.orElseGet(Lists::newArrayList).stream().sorted().collect(Collectors.toList());
    }

    private <E extends BullhornEntity, H extends ScheduledTaskHelper<E>, T extends ScheduledTasksTraverser<H>> boolean shouldEventTaskBeIgnored(EventTask<E, H, T> eventTask, CustomSubscriptionEvent event) {
        IgnoreOn ignoreCriteria = eventTask.getClass().getAnnotation(IgnoreOn.class);
        if (ignoreCriteria == null) {
            return false;
        }
        Integer userPrivateLabelID = getUserPrivateLabelID(event.getUpdatingUserId());
        if (userPrivateLabelID == null) {
            // Should never happen, but just in case...
            return false;
        }
        for (String privateLabelID : ignoreCriteria.privateLabelIDs()) {
            if (Objects.equals(privateLabelID, userPrivateLabelID.toString())) {
                return true;
            }
        }
        for (String subscriptionName : ignoreCriteria.subscriptionNames()) {
            if (Objects.equals(subscriptionName, event.getSubscriptionName())) {
                return true;
            }
        }
        return false;
    }

    private Integer getUserPrivateLabelID(Integer userId) {
        CorporateUser corporateUser = bullhornData.findEntity(CorporateUser.class, userId, Sets.newHashSet("privateLabel(id)"));
        if (corporateUser == null) {
            return null;
        }
        return corporateUser.getPrivateLabel().getId();
    }

    private Map<Class<? extends BullhornEntity>, Map<? extends BullhornRelatedEntity, Set<String>>> constructAllRelatedEntityFields() {
        Map<Class<? extends BullhornEntity>, Map<? extends BullhornRelatedEntity, Set<String>>> allRelatedEntityFields = Maps.newLinkedHashMap();

        allRelatedEntityFields.put(Appointment.class, Utility.getRequestedFields(AppointmentRelatedEntity.values(), appointmentNodes));
        allRelatedEntityFields.put(Candidate.class, Utility.getRequestedFields(CandidateRelatedEntity.values(), candidateNodes));
        allRelatedEntityFields.put(CandidateEducation.class, Utility.getRequestedFields(CandidateEducationRelatedEntity.values(), candidateEducationNodes));
        allRelatedEntityFields.put(CandidateReference.class, Utility.getRequestedFields(CandidateReferenceRelatedEntity.values(), candidateReferenceNodes));
        allRelatedEntityFields.put(CandidateWorkHistory.class, Utility.getRequestedFields(CandidateWorkHistoryRelatedEntity.values(), candidateWorkHistoryNodes));
        allRelatedEntityFields.put(ClientContact.class, Utility.getRequestedFields(ClientContactRelatedEntity.values(), clientContactNodes));
        allRelatedEntityFields.put(ClientCorporation.class, Utility.getRequestedFields(ClientCorporationRelatedEntity.values(), clientCorporationNodes));
        allRelatedEntityFields.put(CorporateUser.class, Utility.getRequestedFields(CorporateUserRelatedEntity.values(), corporateUserNodes));
        allRelatedEntityFields.put(JobOrder.class, Utility.getRequestedFields(JobOrderRelatedEntity.values(), jobOrderNodes));
        allRelatedEntityFields.put(JobSubmission.class, Utility.getRequestedFields(JobSubmissionRelatedEntity.values(), jobSubmissionNodes));
        allRelatedEntityFields.put(Lead.class, Utility.getRequestedFields(LeadRelatedEntity.values(), leadNodes));
        allRelatedEntityFields.put(Note.class, Utility.getRequestedFields(NoteRelatedEntity.values(), noteNodes));
        allRelatedEntityFields.put(Opportunity.class, Utility.getRequestedFields(OpportunityRelatedEntity.values(), opportunityNodes));
        allRelatedEntityFields.put(PlacementChangeRequest.class, Utility.getRequestedFields(PlacementChangeRequestRelatedEntity.values(), placementChangeRequestNodes));
        allRelatedEntityFields.put(PlacementCommission.class, Utility.getRequestedFields(PlacementCommissionRelatedEntity.values(), placementCommissionNodes));
        allRelatedEntityFields.put(Placement.class, Utility.getRequestedFields(PlacementRelatedEntity.values(), placementNodes));
        allRelatedEntityFields.put(Sendout.class, Utility.getRequestedFields(SendoutRelatedEntity.values(), sendoutNodes));
        allRelatedEntityFields.put(Task.class, Utility.getRequestedFields(TaskRelatedEntity.values(), taskNodes));
        allRelatedEntityFields.put(CandidateCertification.class, Utility.getRequestedFields(CandidateCertificationRelatedEntity.values(), candidateCertificationNodes));
        allRelatedEntityFields.put(CandidateCertificationRequirement.class, Utility.getRequestedFields(CandidateCertificationRequirementRelatedEntity.values(), candidateCertificationRequirementNodes));
        allRelatedEntityFields.put(JobSubmissionCertificationRequirement.class, Utility.getRequestedFields(JobSubmissionCertificationRequirementRelatedEntity.values(), jobSubmissionCertificationRequirementNodes));
        allRelatedEntityFields.put(PlacementCertification.class, Utility.getRequestedFields(PlacementCertificationRelatedEntity.values(), placementCertificationNodes));

        return allRelatedEntityFields;
    }

}
