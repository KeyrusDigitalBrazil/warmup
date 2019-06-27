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

package de.hybris.platform.b2bacceleratorfacades.order.populators;

import de.hybris.platform.b2b.services.B2BWorkflowIntegrationService;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BOrderApprovalData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.workflow.model.WorkflowActionModel;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * Populates {@link de.hybris.platform.workflow.model.WorkflowActionModel} to
 * {@link de.hybris.platform.b2bacceleratorfacades.order.data.B2BOrderApprovalData}.
 */
public class B2BOrderApprovalDashboardPopulator implements Populator<WorkflowActionModel, B2BOrderApprovalData>
{
	private B2BWorkflowIntegrationService b2bWorkflowIntegrationService;
	private Converter<OrderModel, OrderData> b2bOrderApprovalDashboardListConverter;
	@Override
	public void populate(final WorkflowActionModel source, final B2BOrderApprovalData target)
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");
		final OrderModel orderModel = getB2bWorkflowIntegrationService().getOrderFromAction(source);
		target.setWorkflowActionModelCode(source.getCode());
		target.setB2bOrderData(getB2bOrderApprovalDashboardListConverter().convert(orderModel));
	}

	protected B2BWorkflowIntegrationService getB2bWorkflowIntegrationService()
	{
		return b2bWorkflowIntegrationService;
	}

	@Required
	public void setB2bWorkflowIntegrationService(final B2BWorkflowIntegrationService b2bWorkflowIntegrationService)
	{
		this.b2bWorkflowIntegrationService = b2bWorkflowIntegrationService;
	}

	protected Converter<OrderModel, OrderData> getB2bOrderApprovalDashboardListConverter()
	{
		return b2bOrderApprovalDashboardListConverter;
	}

	@Required
	public void setB2bOrderApprovalDashboardListConverter(
			final Converter<OrderModel, OrderData> b2bOrderApprovalDashboardListConverter)
	{
		this.b2bOrderApprovalDashboardListConverter = b2bOrderApprovalDashboardListConverter;
	}




}
