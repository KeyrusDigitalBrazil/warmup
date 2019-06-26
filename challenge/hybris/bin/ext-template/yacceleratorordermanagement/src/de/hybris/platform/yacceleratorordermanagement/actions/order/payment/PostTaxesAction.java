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
package de.hybris.platform.yacceleratorordermanagement.actions.order.payment;

import de.hybris.platform.commerceservices.externaltax.ExternalTaxesService;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.action.AbstractProceduralAction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;


/**
 * This step should post a record of taxes committed.
 */
public class PostTaxesAction extends AbstractProceduralAction<OrderProcessModel>
{
	private static final Logger LOG = LoggerFactory.getLogger(PostTaxesAction.class);
	private ExternalTaxesService externalTaxesService;

	@Override
	public void executeAction(final OrderProcessModel process)
	{
		LOG.info("Process: {} in step {}", process.getCode(), getClass().getSimpleName());

		final OrderModel order = process.getOrder();
		LOG.info("Calculating taxes. Order : {}", order.getCode());

		// Default behaviour. Needs to be changed when using another implementation of taxes service.
		getExternalTaxesService().calculateExternalTaxes(order);
	}

	protected ExternalTaxesService getExternalTaxesService()
	{
		return externalTaxesService;
	}

	@Required
	public void setExternalTaxesService(final ExternalTaxesService externalTaxesService)
	{
		this.externalTaxesService = externalTaxesService;
	}
}
