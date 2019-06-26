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
package de.hybris.platform.b2b.services.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.dao.PrincipalGroupMembersDao;
import de.hybris.platform.b2b.dao.impl.BaseDao;
import de.hybris.platform.b2b.dao.impl.DefaultB2BWorkflowActionDao;
import de.hybris.platform.b2b.dao.impl.DefaultB2BWorkflowDao;
import de.hybris.platform.b2b.mail.OrderInfoContextDtoFactory;
import de.hybris.platform.b2b.mock.HybrisMokitoTest;
import de.hybris.platform.b2b.model.B2BBudgetModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.process.approval.actions.B2BPermissionResultHelperImpl;
import de.hybris.platform.b2b.services.B2BApproverService;
import de.hybris.platform.b2b.services.B2BBudgetService;
import de.hybris.platform.b2b.services.B2BCartService;
import de.hybris.platform.b2b.services.B2BCurrencyConversionService;
import de.hybris.platform.b2b.services.B2BCustomerService;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.commons.renderer.RendererService;
import de.hybris.platform.orderscheduling.ScheduleOrderService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.type.impl.DefaultTypeService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.spring.TenantScope;
import de.hybris.platform.workflow.WorkflowActionService;
import de.hybris.platform.workflow.WorkflowAttachmentService;
import de.hybris.platform.workflow.WorkflowProcessingService;
import de.hybris.platform.workflow.WorkflowService;
import de.hybris.platform.workflow.WorkflowTemplateService;

import org.junit.Test;
import org.mockito.Mock;
import org.springframework.transaction.PlatformTransactionManager;


@UnitTest
public class DefaultB2BEscalationServiceMockTest extends HybrisMokitoTest
{
	@Mock
	private UserService mockUserService;
	@Mock
	private BaseDao mockBaseDao;
	@Mock
	private ModelService mockModelService;
	@Mock
	private B2BUnitService<B2BUnitModel, B2BCustomerModel> mockB2bUnitService;
	@Mock
	private SessionService mockSessionService;
	@Mock
	private CommonI18NService mockCommonI18NService;
	@Mock
	private DefaultB2BWorkflowDao mockB2BWorkflowDao;
	@Mock
	private DefaultB2BWorkflowActionDao mockB2BWorkflowActionDao;
	@Mock
	private TenantScope mockTenantScope;
	@Mock
	private WorkflowActionService mockwWorkflowActionService;
	@Mock
	private WorkflowAttachmentService mockwWorkflowAttachmentService;
	@Mock
	private WorkflowProcessingService mockWorkflowProcessingService;
	@Mock
	private WorkflowService mockWorkflowService;
	@Mock
	private WorkflowTemplateService mockWorkflowTemplateService;
	@Mock
	private PlatformTransactionManager mockTxManager;
	@Mock
	private OrderInfoContextDtoFactory orderInfoContextDtoFactory;
	@Mock
	private RendererService rendererService;
	@Mock
	private B2BCurrencyConversionService mockB2BCurrencyConversionService;
	@Mock
	private B2BBudgetService<B2BBudgetModel, B2BCustomerModel> b2BudgetService;
	@Mock
	private B2BCartService mockB2BCartService;
	@Mock
	private B2BCustomerService<B2BCustomerModel, B2BUnitModel> mockB2BCustomerService;
	@Mock
	private ScheduleOrderService mockScheduleOrderService;
	@Mock
	private B2BApproverService mockB2BApproverService;
	@Mock
	private B2BPermissionResultHelperImpl mockB2BPermissionResultHelperImpl;
	@Mock
	private DefaultTypeService mockTypeService;
	@Mock
	private PrincipalGroupMembersDao mockPrincipalGroupMemberDao;

	@Test
	public void testEscalate() throws Exception
	{
		//TODO: implement test
	}

	@Test
	public void testCanEscalate() throws Exception
	{
		//TODO: implement test

	}

	@Test
	public void testScheduleEscalationTask() throws Exception
	{
		//TODO: implement test

	}

	@Test
	public void testHandleEscalationFailure() throws Exception
	{
		//TODO: implement test

	}
}
