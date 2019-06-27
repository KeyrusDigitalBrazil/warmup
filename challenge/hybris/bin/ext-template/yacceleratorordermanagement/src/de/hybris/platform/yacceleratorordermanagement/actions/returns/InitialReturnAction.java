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

package de.hybris.platform.yacceleratorordermanagement.actions.returns;

import de.hybris.platform.basecommerce.enums.ReturnAction;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.processengine.action.AbstractAction;
import de.hybris.platform.returns.model.ReturnProcessModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.task.RetryLaterException;
import de.hybris.platform.warehousing.returns.service.RestockConfigService;
import de.hybris.platform.warehousing.returns.strategy.RestockWarehouseSelectionStrategy;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;


/**
 * Check whether the return request is an instore or an online request and redirects it to the appropriate step.
 */
public class InitialReturnAction extends AbstractAction<ReturnProcessModel>
{
	private static final Logger LOG = LoggerFactory.getLogger(InitialReturnAction.class);
	private RestockWarehouseSelectionStrategy restockWarehouseSelectionStrategy;
	private RestockConfigService restockConfigService;

	@Override
	public String execute(ReturnProcessModel returnProcess) throws RetryLaterException, Exception
	{
		validateParameterNotNullStandardMessage("returnProcess", returnProcess);
		final ReturnRequestModel returnRequest = returnProcess.getReturnRequest();
		validateParameterNotNullStandardMessage("returnRequest", returnRequest);
		Assert.isTrue(CollectionUtils.isNotEmpty(returnRequest.getReturnEntries()), "No Return entries found for return request");

		LOG.info("Process: {} in step {}", returnProcess.getCode(), getClass().getSimpleName());

		final Boolean isOnline = returnRequest.getReturnEntries().stream()
				.noneMatch(entry -> ReturnAction.IMMEDIATE.equals(entry.getAction()));

		String transition;
		if (isOnline)
		{
			if (getRestockConfigService().getRestockConfig() != null && Boolean.TRUE
					.equals(getRestockConfigService().getRestockConfig().getIsUpdateStockAfterReturn()))
			{
				final WarehouseModel returnWarehouse = getRestockWarehouseSelectionStrategy().performStrategy(returnRequest);
				returnRequest.setReturnWarehouse(returnWarehouse);
				getModelService().save(returnRequest);
			}
			transition = Transition.ONLINE.toString();
		}
		else
		{
			transition = Transition.INSTORE.toString();
		}

		LOG.debug("Process: {} transitions to {}", returnProcess.getCode(), transition);

		return transition;
	}

	@Override
	public Set<String> getTransitions()
	{
		return Transition.getStringValues();
	}

	protected RestockWarehouseSelectionStrategy getRestockWarehouseSelectionStrategy()
	{
		return restockWarehouseSelectionStrategy;
	}

	@Required
	public void setRestockWarehouseSelectionStrategy(final RestockWarehouseSelectionStrategy restockWarehouseSelectionStrategy)
	{
		this.restockWarehouseSelectionStrategy = restockWarehouseSelectionStrategy;
	}

	protected RestockConfigService getRestockConfigService()
	{
		return restockConfigService;
	}

	@Required
	public void setRestockConfigService(final RestockConfigService restockConfigService)
	{
		this.restockConfigService = restockConfigService;
	}

	protected enum Transition
	{
		ONLINE, INSTORE;

		public static Set<String> getStringValues()
		{
			final Set<String> res = new HashSet<String>();

			for (final Transition transition : Transition.values())
			{
				res.add(transition.toString());
			}
			return res;
		}
	}

}
