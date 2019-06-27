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

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.B2BIntegrationTest;
import de.hybris.platform.b2b.dao.impl.BaseDao;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.process.approval.actions.B2BPermissionResultHelperImpl;
import de.hybris.platform.b2b.process.approval.actions.EscalationTaskRunner;
import de.hybris.platform.b2b.services.B2BApproverService;
import de.hybris.platform.b2b.services.B2BCartService;
import de.hybris.platform.b2b.services.B2BCostCenterService;
import de.hybris.platform.b2b.services.B2BCustomerService;
import de.hybris.platform.b2b.services.B2BEmailService;
import de.hybris.platform.b2b.services.B2BEscalationService;
import de.hybris.platform.b2b.services.B2BOrderService;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.b2b.services.B2BWorkflowIntegrationService;
import de.hybris.platform.b2b.util.B2BDateUtils;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.CartFactory;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.helpers.ProcessParameterHelper;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.validation.services.ValidationService;
import de.hybris.platform.workflow.WorkflowActionService;
import de.hybris.platform.workflow.WorkflowProcessingService;
import de.hybris.platform.workflow.WorkflowService;
import de.hybris.platform.workflow.WorkflowTemplateService;

import java.util.Locale;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;


@Ignore("BTOBA-30")
@IntegrationTest
@ContextConfiguration(locations =
{ "classpath:/b2bapprovalprocess-spring-test.xml" })
public class B2BPermissionServiceTest extends B2BIntegrationTest
{
	@Resource
	private B2BApproverService<B2BCustomerModel> b2bApproverService;
	@Resource
	private B2BCustomerService<B2BCustomerModel, B2BUnitModel> b2bCustomerService;
	@Resource
	private B2BEscalationService b2bEscalationService;
	@Resource
	private CommonI18NService commonI18NService;
	@Resource
	private DefaultB2BPermissionService b2bPermissionService;
	@Resource
	private BaseDao baseDao;
	@Resource
	private ModelService modelService;
	@Resource
	private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;
	@Resource
	private KeyGenerator orderCodeGenerator;
	@Resource
	private UserService userService;
	@Resource
	private BusinessProcessService businessProcessService;
	@Resource
	private B2BWorkflowIntegrationService b2bWorkflowIntegrationService;
	@Resource
	private ProcessParameterHelper processParameterHelper;
	@Resource
	private CartFactory b2bCartFactory;
	@Resource
	private B2BCartService b2bCartService;
	@Resource
	private B2BOrderService b2bOrderService;
	@Resource
	private ProductService productService;
	@Resource
	private EscalationTaskRunner escalationTaskRunner;
	@Resource
	private B2BCostCenterService b2bCostCenterService;
	@Resource
	private B2BPermissionResultHelperImpl permissionResultHelper;
	@Resource
	private SessionService sessionService;
	@Resource
	private I18NService i18nService;
	@Resource
	private KeyGenerator b2bProcessCodeGenerator;
	@Resource
	private WorkflowActionService workflowActionService;
	@Resource
	private B2BEmailService b2bEmailService;
	@Resource
	private CalculationService calculationService;
	@Resource
	private SearchRestrictionService searchRestrictionService;
	@Resource
	private WorkflowProcessingService workflowProcessingService;
	@Resource
	private WorkflowService newestWorkflowService;
	@Resource
	private FlexibleSearchService flexibleSearchService;
	@Resource
	private B2BDateUtils b2bDateUtils;
	@Resource
	private WorkflowTemplateService workflowTemplateService;
	@Resource
	private ValidationService validationService;

	@Before
	public void before() throws Exception
	{
		B2BIntegrationTest.loadTestData();
		importCsv("/b2bapprovalprocess/test/organizationdata.csv", "UTF-8");

		sessionService.getCurrentSession().setAttribute("user",
				this.modelService.<Object> toPersistenceLayer(userService.getAdminUser()));
		i18nService.setCurrentLocale(Locale.ENGLISH);
		commonI18NService.setCurrentLanguage(commonI18NService.getLanguage("en"));
		commonI18NService.setCurrentCurrency(commonI18NService.getCurrency("USD"));
	}


	@Test
	public void testNeedsApprovalShouldReturnFalse() throws Exception
	{
		final OrderModel order = this.createOrder("GC S Det", 1, OrderStatus.CREATED);
		Assert.assertNotNull(order);
		Thread.sleep(20000);
		this.modelService.refresh(order);
		Assert.assertFalse("order should not need approval", this.b2bPermissionService.needsApproval(order));
	}

	@Test
	public void testNeedsApprovalShouldReturnTrue() throws Exception
	{
		final OrderModel order = this.createOrder("GC S Det", 100, OrderStatus.CREATED);
		Assert.assertNotNull(order);
		Thread.sleep(20000);
		this.modelService.refresh(order);
		Assert.assertTrue("order should not need approval", this.b2bPermissionService.needsApproval(order));
	}

	@Test
	public void testShouldGetEligableApprovers() throws Exception
	{
		// order total is 10000
		final OrderModel order = this.createOrder("GC S Det", 10, OrderStatus.CREATED);
		Assert.assertNotNull(order);
		this.modelService.refresh(order);
		Assert.assertEquals("wrong number of eligable approvers found", 3, this.b2bPermissionService.getEligableApprovers(order)
				.size());
	}

	@Test
	public void testShouldGetNoEligableApprovers() throws Exception
	{
		// order total is 7.999999992E10! which exceeds the 9.99999999E8 threshold.
		final OrderModel order = this.createOrder(999999999);
		Assert.assertNotNull(order);
		this.modelService.refresh(order);
		Assert.assertEquals("wrong number of eligable approvers found", 0, this.b2bPermissionService.getEligableApprovers(order)
				.size());
	}
}
