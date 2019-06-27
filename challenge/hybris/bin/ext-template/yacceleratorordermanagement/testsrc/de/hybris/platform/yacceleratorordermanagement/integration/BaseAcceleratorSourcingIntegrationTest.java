/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 */
package de.hybris.platform.yacceleratorordermanagement.integration;

import de.hybris.platform.commerceservices.stock.CommerceStockService;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.stock.StockService;
import de.hybris.platform.warehousing.constants.WarehousingTestConstants;
import de.hybris.platform.warehousing.sourcing.ban.service.SourcingBanService;
import de.hybris.platform.warehousing.stock.services.WarehouseStockService;
import de.hybris.platform.warehousing.util.models.AtpFormulas;
import de.hybris.platform.warehousing.util.models.AutomatedWorkflowActionTemplates;
import de.hybris.platform.warehousing.util.models.BaseStores;
import de.hybris.platform.warehousing.util.models.CommentTypes;
import de.hybris.platform.warehousing.util.models.Components;
import de.hybris.platform.warehousing.util.models.CsAgentGroups;
import de.hybris.platform.warehousing.util.models.Domains;
import de.hybris.platform.warehousing.util.models.EmailAddresses;
import de.hybris.platform.warehousing.util.models.Orders;
import de.hybris.platform.warehousing.util.models.PointsOfService;
import de.hybris.platform.warehousing.util.models.Products;
import de.hybris.platform.warehousing.util.models.RestockConfigs;
import de.hybris.platform.warehousing.util.models.SourcingConfigs;
import de.hybris.platform.warehousing.util.models.StockLevels;
import de.hybris.platform.warehousing.util.models.Users;
import de.hybris.platform.warehousing.util.models.Warehouses;
import de.hybris.platform.warehousing.util.models.WorkflowActionTemplates;
import de.hybris.platform.warehousing.util.models.WorkflowTemplates;
import de.hybris.platform.workflow.WorkflowService;
import de.hybris.platform.workflow.model.WorkflowTemplateModel;
import de.hybris.platform.yacceleratorordermanagement.integration.util.CancellationUtil;
import de.hybris.platform.yacceleratorordermanagement.integration.util.DeclineUtil;
import de.hybris.platform.yacceleratorordermanagement.integration.util.ReturnUtil;
import de.hybris.platform.yacceleratorordermanagement.integration.util.SourcingUtil;
import de.hybris.platform.yacceleratorordermanagement.integration.util.WorkflowUtil;

import javax.annotation.Resource;

import java.util.Arrays;

import com.google.common.collect.Lists;
import org.junit.After;
import org.junit.Before;


public class BaseAcceleratorSourcingIntegrationTest extends BaseAcceleratorIntegrationTest
{
	protected static final String CODE_MEMORY_CARD = "memorycard";
	protected static final String CODE_MONTREAL = "montreal";
	protected static final String CODE_MONTREAL_EXTERNAL = "montrealExternal";
	protected static final String CODE_TORONTO = "toronto";
	protected static final String CODE_BOSTON = "boston";
	protected static final String CODE_PARIS = "paris";
	protected static final Long CAMERA_QTY = Long.valueOf(3L);
	protected static final Long LEFTOVER_QTY = Long.valueOf(1L);
	protected static final Long MEMORY_CARD_QTY = Long.valueOf(2L);
	protected static final Long LENS_QTY = Long.valueOf(4L);
	protected static final String CAMERA_CODE = "camera";
	protected static final String MEMORY_CARD_CODE = "memorycard";
	protected static final String LENS_CODE = "lens";
	protected static final String DECLINE_ENTRIES = "declineEntries";
	protected static final int timeOut = 15; //seconds

	protected static final String CONSIGNMENT_ACTION_EVENT_NAME = "ConsignmentActionEvent";
	protected static final String REALLOCATE_CONSIGNMENT_CHOICE = "reallocateConsignment";
	protected static final String ORDER_ACTION_EVENT_NAME = "OrderActionEvent";
	protected static final String RE_SOURCE_CHOICE = "reSource";

	@Resource
	protected SourcingUtil sourcingUtil;
	@Resource
	protected ReturnUtil returnUtil;
	@Resource
	protected CancellationUtil cancellationUtil;
	@Resource
	protected DeclineUtil declineUtil;
	@Resource
	protected WorkflowUtil workflowUtil;

	protected PointsOfService pointsOfService;
	protected ModelService modelService;
	protected DeliveryModeModel deliveryMode;
	protected OrderModel order;
	protected StockLevels stockLevels;
	protected Products products;
	protected ProductService productService;
	protected SourcingBanService sourcingBanService;
	protected Warehouses warehouses;
	protected StockService stockService;
	protected CommerceStockService commerceStockService;
	protected Orders orders;
	protected BaseStores baseStores;
	protected Users users;
	protected CsAgentGroups csAgentGroups;
	protected Domains domains;
	protected Components components;
	protected CommentTypes commentTypes;
	protected SourcingConfigs sourcingConfigs;
	protected RestockConfigs restockConfigs;
	protected WarehouseStockService warehouseStockService;
	protected BusinessProcessService orderBusinessProcessService;
	protected EmailAddresses emailAddresses;
	protected AtpFormulas atpFormulas;
	protected WorkflowTemplates workflowTemplates;
	protected WorkflowActionTemplates workflowActionTemplates;
	protected WorkflowService workflowService;
	protected AutomatedWorkflowActionTemplates automatedWorkflowActionTemplates;

	@Before
	public void setup() throws ImpExException
	{
		importCsv("/impex/projectdata-dynamic-business-process-order.impex", WarehousingTestConstants.ENCODING);
		importCsv("/impex/projectdata-dynamic-business-process-consignment.impex", WarehousingTestConstants.ENCODING);
		importCsv("/impex/projectdata-dynamic-business-process-return.impex", WarehousingTestConstants.ENCODING);
		importCsv("/impex/projectdata-dynamic-business-process-sendReturnLabelEmail.impex", WarehousingTestConstants.ENCODING);

		users = sourcingUtil.getUsers();
		csAgentGroups = sourcingUtil.getCsAgentGroups();
		domains = sourcingUtil.getDomains();
		components = sourcingUtil.getComponents();
		commentTypes = sourcingUtil.getCommentTypes();
		baseStores = sourcingUtil.getBaseStores();
		restockConfigs = returnUtil.getRestockConfigs();
		pointsOfService = sourcingUtil.getPointsOfService();
		sourcingBanService = sourcingUtil.getSourcingBanService();
		order = sourcingUtil.getOrder();
		stockLevels = sourcingUtil.getStockLevels();
		products = sourcingUtil.getProducts();
		productService = sourcingUtil.getProductService();
		warehouses = sourcingUtil.getWarehouses();
		stockService = sourcingUtil.getStockService();
		commerceStockService = sourcingUtil.getCommerceStockService();
		orders = sourcingUtil.getOrders();
		modelService = sourcingUtil.getModelService();
		deliveryMode = sourcingUtil.getDeliveryModes().standardShipment();
		sourcingConfigs = sourcingUtil.getSourcingConfigs();
		warehouseStockService = sourcingUtil.getWarehouseStockService();
		orderBusinessProcessService = sourcingUtil.getOrderBusinessProcessService();
		emailAddresses = sourcingUtil.getEmailAddresses();
		atpFormulas = sourcingUtil.getAtpFormulas();
		workflowTemplates = sourcingUtil.getWorkflowTemplates();
		workflowActionTemplates = sourcingUtil.getWorkflowActionTemplates();
		automatedWorkflowActionTemplates = sourcingUtil.getAutomatedWorkflowActionTemplates();
		buildConsignmentWorkflow();
		buildAsnWorkflow();

		workflowService = sourcingUtil.getNewestWorkflowService();
		workflowUtil.setupRelations();

		restockConfigs.RestockAfterReturn();
		users.Nancy();
		csAgentGroups.fraudAgentGroup();
		domains.ticketSystem();
		components.ticketComponent();
		commentTypes.ticketCreationEvent();
		baseStores.NorthAmerica()
				.setPointsOfService(Lists.newArrayList(pointsOfService.Boston(), pointsOfService.Montreal_Downtown()));
		baseStores.NorthAmerica().setWarehouses(
				Lists.newArrayList(warehouses.Boston(), warehouses.Montreal(), warehouses.Griffintown(),
						warehouses.Montreal_External(), warehouses.Paris(), warehouses.Toronto()));
		baseStores.NorthAmerica().setSourcingConfig(sourcingConfigs.HybrisConfig());
		saveAll();
	}

	/**
	 * setup the workflow for Consignment
	 */
	protected void buildConsignmentWorkflow()
	{
		// Setup the task assignment workflow
		workflowTemplates.ConsignmentTemplate();

		final WorkflowTemplateModel workflowTemplate = sourcingUtil.getWorkflowTemplateService()
				.getWorkflowTemplateForCode("ConsignmentTemplate");
		modelService.refresh(workflowTemplate);
		workflowTemplate.setActions(Arrays.asList(workflowActionTemplates.Picking(), workflowActionTemplates.Packing(),
				workflowActionTemplates.Shipping(), workflowActionTemplates.Pickup(), workflowActionTemplates.End(),
				automatedWorkflowActionTemplates.AutoPacking(), automatedWorkflowActionTemplates.AutoShipping(),
				automatedWorkflowActionTemplates.AutoPickup()));
	}

	/**
	 * setup the workflow for Advance Shipping Notice
	 */
	protected void buildAsnWorkflow()
	{
		workflowTemplates.AsnTemplate();

		final WorkflowTemplateModel asnWorkflowTemplate = sourcingUtil.getWorkflowTemplateService()
				.getWorkflowTemplateForCode("AsnTemplate");
		modelService.refresh(asnWorkflowTemplate);
		asnWorkflowTemplate.setActions(
				Arrays.asList(workflowActionTemplates.EndAsn(), automatedWorkflowActionTemplates.AutoReallocateConsignment(),
						automatedWorkflowActionTemplates.AutoDeleteCancellationEvent(),
						automatedWorkflowActionTemplates.AutoDeleteStockLevel()));
	}

	@After
	public void resetFactors()
	{
		modelService.remove(baseStores.NorthAmerica().getSourcingConfig());
	}

	protected void saveAll()
	{
		modelService.saveAll();
	}

	protected void cleanUpData()
	{
		cleanUpModel("Order");
		cleanUpModel("Consignment");
		cleanUpModel("BusinessProcess");
		cleanUpModel("InventoryEvent");
		cleanUpModel("ConsignmentEntryEvent");
		cleanUpModel("SourcingBan");
		cleanUpModel("PickUpDeliveryMode");
		cleanUpModel("TaskCondition");
		cleanUpModel("Task");
		cleanUpModel("StockLevel");
		cleanUpModel("OrderCancelConfig");
		cleanUpModel("RestockConfig");
		cleanUpModel("BaseStore");
		cleanUpModel("PointOfService");
		cleanUpModel("Warehouse");
		cleanUpModel("Workflow");
		cleanUpModel("WorkflowAction");
		cleanUpModel("WorkflowDecision");
	}

	protected void cleanUpModel(String modelName)
	{
		try
		{
			SearchResult<FlexibleSearchQuery> result = flexibleSearchService.search("SELECT {pk} FROM {" + modelName + "}");
			if (result.getCount() != 0)
				modelService.removeAll(result.getResult());
		}
		catch (NullPointerException e)
		{
			//do nothing
		}
	}

	/**
	 * Refreshes the {@link ConsignmentModel} and its {@link ConsignmentEntryModel}(s)
	 *
	 * @param consignmentModel
	 * 		the {@link ConsignmentModel} to be refreshed
	 */
	protected void refreshConsignmentAndEntries(final ConsignmentModel consignmentModel)
	{
		modelService.refresh(consignmentModel);
		consignmentModel.getConsignmentEntries().forEach(modelService::refresh);
	}
}
