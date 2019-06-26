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

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BMerchantCheckResultModel;
import de.hybris.platform.b2b.services.B2BMerchantCheckService;
import de.hybris.platform.b2b.strategies.EvaluateStrategy;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the {@link B2BMerchantCheckService}.
 *
 * @spring.bean b2bMerchantCheckService
 */

public class DefaultB2BMerchantCheckService implements B2BMerchantCheckService
{
	private Set<EvaluateStrategy<B2BMerchantCheckResultModel, AbstractOrderModel, B2BCustomerModel>> evaluateStrategies;
	protected List<String> merchantCheckTypes;
	private SessionService sessionService;
	private UserService userService;

	@Override
	public Set<B2BMerchantCheckResultModel> evaluateMerchantChecks(final AbstractOrderModel order, final B2BCustomerModel customer)
	{
		final Set<B2BMerchantCheckResultModel> merchantCheckResults = new HashSet<B2BMerchantCheckResultModel>();
		getSessionService().executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public void executeWithoutResult()
			{
				getUserService().setCurrentUser(getUserService().getAdminUser());
				for (final EvaluateStrategy<B2BMerchantCheckResultModel, AbstractOrderModel, B2BCustomerModel> evaluateStrategy : evaluateStrategies)
				{
					final Set<B2BMerchantCheckResultModel> evaluationResult = (Set<B2BMerchantCheckResultModel>) evaluateStrategy
							.evaluate(order, customer);
					merchantCheckResults.addAll(evaluationResult);

				}
			}
		});

		return merchantCheckResults;

	}


	@Required
	public void setEvaluateStrategies(
			final Set<EvaluateStrategy<B2BMerchantCheckResultModel, AbstractOrderModel, B2BCustomerModel>> evaluateStrategies)
	{
		this.evaluateStrategies = evaluateStrategies;
	}

	protected List<String> getMerchantCheckTypes()
	{
		return merchantCheckTypes;
	}

	public void setMerchantCheckTypes(final List<String> merchantCheckTypes)
	{
		this.merchantCheckTypes = merchantCheckTypes;
	}

	protected SessionService getSessionService()
	{
		return sessionService;
	}

	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}


	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}



}
