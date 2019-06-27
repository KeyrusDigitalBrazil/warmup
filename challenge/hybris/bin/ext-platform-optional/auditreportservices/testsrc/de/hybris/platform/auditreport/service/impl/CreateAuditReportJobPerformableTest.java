/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.auditreport.service.impl;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.auditreport.model.AuditReportDataModel;
import de.hybris.platform.auditreport.model.CreateAuditReportCronJobModel;
import de.hybris.platform.auditreport.service.AuditReportDataService;
import de.hybris.platform.auditreport.service.CreateAuditReportParams;
import de.hybris.platform.auditreport.service.ReportGenerationException;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.workflow.WorkflowProcessingService;
import de.hybris.platform.workflow.WorkflowService;
import de.hybris.platform.workflow.WorkflowTemplateService;
import de.hybris.platform.workflow.model.WorkflowTemplateModel;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;



@RunWith(MockitoJUnitRunner.class)
public class CreateAuditReportJobPerformableTest
{
	public static final String WORKFLOW_TEMPLATE_NAME = "WFL";
	public static final String WORKFLOW_TEMPLATE_NAME_UNKNOWN = "unknown";
	private static final String CONFIG_NAME = "configName";
	private static final String REPORT_ID = "reportId";
	@Captor
	protected ArgumentCaptor<List<ItemModel>> captor;
	@Spy
	@InjectMocks
	private CreateAuditReportJobPerformable jobPerformable;
	@Mock
	private AuditReportDataService auditReportDataService;
	@Mock
	private UserService userService;
	@Mock
	private WorkflowTemplateService workflowTemplateService;
	@Mock
	private WorkflowService workflowService;
	@Mock
	private WorkflowProcessingService workflowProcessingService;
	@Mock
	private CreateAuditReportCronJobModel cronJobModel;
	@Mock
	private ItemModel rootType;
	@Mock
	private WorkflowTemplateModel workflowTemplate;
	@Mock
	private SessionService sessionService;

	@Before
	public void setup()
	{
		when(cronJobModel.getRootItem()).thenReturn(rootType);
		when(cronJobModel.getConfigName()).thenReturn(CONFIG_NAME);
		when(cronJobModel.getReportId()).thenReturn(REPORT_ID);
		when(workflowTemplate.getName()).thenReturn("Workflow template name");
		when(workflowTemplateService.getWorkflowTemplateForCode(any())).thenReturn(workflowTemplate);
	}

	@Test
	public void testPerformWhenReportCannotBeCreated()
	{
		// given
		when(auditReportDataService.createReport(any())).thenThrow(ReportGenerationException.class);

		// when
		final PerformResult result = jobPerformable.perform(cronJobModel);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getResult()).isSameAs(CronJobResult.FAILURE);
		assertThat(result.getStatus()).isSameAs(CronJobStatus.FINISHED);
	}

	@Test
	public void testPerformWhenReportIsCreatedSuccessfully()
	{
		// given
		final AuditReportDataModel report = mock(AuditReportDataModel.class);
		when(auditReportDataService.createReport(new CreateAuditReportParams(rootType, CONFIG_NAME, REPORT_ID, false, null, null)))
				.thenReturn(report);

		// when
		final PerformResult result = jobPerformable.perform(cronJobModel);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getResult()).isSameAs(CronJobResult.SUCCESS);
		assertThat(result.getStatus()).isSameAs(CronJobStatus.FINISHED);
	}

	@Test
	public void testStartWorkflow()
	{
		final AuditReportDataModel report = mock(AuditReportDataModel.class);
		when(report.getCode()).thenReturn(StringUtils.EMPTY);
		jobPerformable.setWorkflowTemplateName(WORKFLOW_TEMPLATE_NAME);
		jobPerformable.startWorkflow(report);

		verify(workflowTemplateService).getWorkflowTemplateForCode(WORKFLOW_TEMPLATE_NAME);
		verify(workflowService).createWorkflow(any(), eq(workflowTemplate), captor.capture(), any());
		verify(workflowProcessingService).startWorkflow(any());
		assertThat(captor.getValue()).hasSize(1);
		assertThat(captor.getValue()).contains(report);
	}

	@Test
	public void testStartWorkflowUnknownTemplate()
	{
		final AuditReportDataModel report = mock(AuditReportDataModel.class);
		when(workflowTemplateService.getWorkflowTemplateForCode(WORKFLOW_TEMPLATE_NAME_UNKNOWN))
				.thenThrow(UnknownIdentifierException.class);
		jobPerformable.setWorkflowTemplateName(WORKFLOW_TEMPLATE_NAME_UNKNOWN);

		jobPerformable.startWorkflow(report);

		verify(workflowTemplateService).getWorkflowTemplateForCode(WORKFLOW_TEMPLATE_NAME_UNKNOWN);
		Mockito.verifyNoMoreInteractions(workflowTemplateService);
	}
}
