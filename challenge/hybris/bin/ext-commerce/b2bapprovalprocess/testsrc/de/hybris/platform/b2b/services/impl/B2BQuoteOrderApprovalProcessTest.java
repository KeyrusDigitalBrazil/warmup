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
import de.hybris.platform.b2b.WorkflowIntegrationTest;
import de.hybris.platform.b2b.dao.impl.BaseDao;
import de.hybris.platform.b2b.model.B2BCommentModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.process.approval.actions.B2BPermissionResultHelperImpl;
import de.hybris.platform.b2b.process.approval.actions.EscalationTaskRunner;
import de.hybris.platform.b2b.process.approval.model.B2BApprovalProcessModel;
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
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.CartFactory;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.enums.ProcessState;
import de.hybris.platform.processengine.helpers.ProcessParameterHelper;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.validation.services.ValidationService;
import de.hybris.platform.workflow.WorkflowActionService;
import de.hybris.platform.workflow.WorkflowProcessingService;
import de.hybris.platform.workflow.WorkflowService;
import de.hybris.platform.workflow.WorkflowTemplateService;
import de.hybris.platform.workflow.jalo.WorkflowAction;
import de.hybris.platform.workflow.model.WorkflowActionModel;
import de.hybris.platform.workflow.model.WorkflowDecisionModel;
import de.hybris.platform.workflow.model.WorkflowModel;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;


@Ignore("BTOBA-30")
@IntegrationTest
@ContextConfiguration(locations =
{ "classpath:/b2bapprovalprocess-spring-test.xml" })
public class B2BQuoteOrderApprovalProcessTest extends WorkflowIntegrationTest
{
	private static final Logger LOG = Logger.getLogger(B2BQuoteOrderApprovalProcessTest.class);
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
	@Resource
	private SearchRestrictionService searchRestrictionService;

	@Before
	public void before() throws Exception
	{
		B2BIntegrationTest.loadTestData();
		importCsv("/b2bapprovalprocess/test/b2borganizations.csv", "UTF-8");
		//importCsv("/b2bapprovalprocess/test/organizationdata.csv", "UTF-8");

		sessionService.getCurrentSession().setAttribute("user",
				this.modelService.<Object> toPersistenceLayer(userService.getAdminUser()));
		i18nService.setCurrentLocale(Locale.ENGLISH);
		commonI18NService.setCurrentLanguage(commonI18NService.getLanguage("en"));
		commonI18NService.setCurrentCurrency(commonI18NService.getCurrency("USD"));
	}


	@Test
	public void shouldStartSalesQuotesProcessAndAssertApprovalFromMerchant() throws Exception
	{
		final OrderModel order = createOrder("GC CEO", 140, OrderStatus.PENDING_QUOTE);

		Assert.assertEquals(Boolean.TRUE, Boolean.valueOf(b2bOrderService.isQuoteAllowed(order)));

		final B2BApprovalProcessModel b2bApprovalProcessModel = baseDao.findFirstByAttribute(B2BApprovalProcessModel.ORDER, order,
				B2BApprovalProcessModel.class);
		Assert.assertNotNull(b2bApprovalProcessModel);

		if (this.waitForProcessAction(b2bApprovalProcessModel.getCode(), "waitProcessSalesQuote", 60000))
		{
			modelService.refresh(order);
			final WorkflowModel workflow = order.getWorkflow();
			Assert.assertNotNull(workflow);
			modelService.refresh(workflow);
			final Collection<WorkflowActionModel> actions = workflowActionService.getStartWorkflowActions(workflow);
			Assert.assertEquals(1, actions.size());
			this.approveWorkflowAction(actions.iterator().next());
		}

		modelService.refresh(b2bApprovalProcessModel);
		this.waitForProcessToEnd(b2bApprovalProcessModel.getCode(), 60000);
		modelService.refresh(order);
		Assert.assertEquals(OrderStatus.APPROVED_QUOTE, order.getStatus());
		Assert.assertEquals(ProcessState.SUCCEEDED, b2bApprovalProcessModel.getProcessState());
	}

	@Test
	@Ignore("hybris calculation does not support product without prices")
	public void shouldStartSalesQuotesProcessForOrderWithProductWithNoPriceAndAssertApprovalFromMerchant() throws Exception
	{


		final ProductModel testProduct0 = productService.getProductForCode("testProduct0");
		final ProductModel productWithOutPrice = modelService.create(ProductModel.class);
		productWithOutPrice.setCode("productWithOutPrice");
		productWithOutPrice.setCatalogVersion(testProduct0.getCatalogVersion());
		productWithOutPrice.setUnit(testProduct0.getUnit());
		this.modelService.save(productWithOutPrice);

		final OrderModel order = createOrder(this.login("GC CEO"), 140, OrderStatus.PENDING_QUOTE, productWithOutPrice);
		calculationService.calculate(order);
		Assert.assertEquals(Boolean.TRUE, Boolean.valueOf(b2bOrderService.isQuoteAllowed(order)));

		final B2BApprovalProcessModel b2bApprovalProcessModel = baseDao.findFirstByAttribute(B2BApprovalProcessModel.ORDER, order,
				B2BApprovalProcessModel.class);
		Assert.assertNotNull(b2bApprovalProcessModel);

		if (this.waitForProcessAction(b2bApprovalProcessModel.getCode(), "waitProcessSalesQuote", 60000))
		{
			final WorkflowModel workflow = order.getWorkflow();
			modelService.refresh(workflow);
			final Collection<WorkflowActionModel> actions = workflowActionService.getStartWorkflowActions(workflow);
			Assert.assertEquals(1, actions.size());
			this.approveWorkflowAction(actions.iterator().next());
		}

		modelService.refresh(b2bApprovalProcessModel);
		this.waitForProcessToEnd(b2bApprovalProcessModel.getCode(), 60000);
		modelService.refresh(order);
		Assert.assertEquals(OrderStatus.APPROVED_QUOTE, order.getStatus());
		Assert.assertEquals(ProcessState.SUCCEEDED, b2bApprovalProcessModel.getProcessState());
	}

	@Test
	public void testDeleteQuoteAndOrder() throws Exception
	{
		List<OrderModel> pendingQuoteOrders = null;
		final OrderModel order = createOrder("GC S HH", 140, OrderStatus.PENDING_QUOTE);
		login("GC S HH");

		Assert.assertEquals(Boolean.TRUE, Boolean.valueOf(b2bOrderService.isQuoteAllowed(order)));

		final B2BApprovalProcessModel b2bApprovalProcessModel = baseDao.findFirstByAttribute(B2BApprovalProcessModel.ORDER, order,
				B2BApprovalProcessModel.class);
		Assert.assertNotNull(b2bApprovalProcessModel);

		if (this.waitForProcessAction(b2bApprovalProcessModel.getCode(), "waitProcessSalesQuote", 60000))
		{
			modelService.refresh(order);
			final WorkflowModel workflow = order.getWorkflow();
			Assert.assertNotNull(workflow);
			modelService.refresh(workflow);
			final Collection<WorkflowActionModel> actions = workflowActionService.getStartWorkflowActions(workflow);
			Assert.assertEquals(1, actions.size());
		}
		modelService.refresh(b2bApprovalProcessModel);
		modelService.refresh(order);

		pendingQuoteOrders = b2bOrderService.getPendingQuoteOrders(userService.getUserForUID("GC S HH"));
		Assert.assertEquals(1, pendingQuoteOrders.size());

		b2bOrderService.deleteOrder(order.getCode());

		pendingQuoteOrders = b2bOrderService.getPendingQuoteOrders(userService.getUserForUID("GC S HH"));
		Assert.assertEquals(0, pendingQuoteOrders.size());
	}

	@Override
	public void approveWorkflowAction(final WorkflowActionModel workflowActionModel)
	{
		sessionService.executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public void executeWithoutResult()
			{
				userService.setCurrentUser(userService.getAdminUser());
				final Collection<WorkflowDecisionModel> decisions = workflowActionModel.getDecisions();
				for (final WorkflowDecisionModel workflowDecisionModel : decisions)
				{
					if (StringUtils.equals(workflowDecisionModel.getQualifier(),
							B2BWorkflowIntegrationService.DECISIONCODES.APPROVE.name()))
					{
						workflowActionModel.setSelectedDecision(workflowDecisionModel);
						modelService.save(workflowActionModel);
						((WorkflowAction) modelService.getSource(workflowActionModel)).decide();
						if (LOG.isDebugEnabled())
						{
							LOG.debug(String.format("Approving %s workflow action", workflowActionModel.getCode()));
						}
						break;
					}
				}
			}
		});
	}


	public void approveQuoteWorkflowAction(final WorkflowActionModel workflowActionModel, final OrderModel order)
	{
		sessionService.executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public void executeWithoutResult()
			{
				userService.setCurrentUser(userService.getAdminUser());
				final B2BCommentModel comment = modelService.create(B2BCommentModel.class);
				comment.setComment("The Quote is accepted");
				comment.setOwner(userService.getCurrentUser());
				order.setB2bcomments(Collections.singleton(comment));


				final Collection<WorkflowDecisionModel> decisions = workflowActionModel.getDecisions();
				for (final WorkflowDecisionModel workflowDecisionModel : decisions)
				{
					if (StringUtils.equals(workflowDecisionModel.getQualifier(),
							B2BWorkflowIntegrationService.DECISIONCODES.APPROVE.name()))
					{
						workflowActionModel.setSelectedDecision(workflowDecisionModel);
						modelService.save(workflowActionModel);
						((WorkflowAction) modelService.getSource(workflowActionModel)).decide();
						if (LOG.isDebugEnabled())
						{
							LOG.debug(String.format("Approving %s workflow action", workflowActionModel.getCode()));
						}
						break;
					}
				}
			}
		});
	}

}
