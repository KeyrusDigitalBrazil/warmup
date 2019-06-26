/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.productconfig.services.event.util.impl;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.sap.productconfig.services.model.ProductConfigurationPersistenceCleanUpCronJobModel;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import org.springframework.beans.factory.annotation.Required;


/**
 * Helper Class returns user session ID using event info, relevant for session closed and user changed events
 */
public class ProductConfigEventListenerUtil
{
	static final String SELECT_CRON_JOB_MODEL = "SELECT {pk} FROM {ProductConfigurationPersistenceCleanUpCronJob}";

	private FlexibleSearchService flexibleSearchService;

	/**
	 *
	 * @param evt
	 *           Event like BeforeSessionCloseEvent or AfterSessionUserChangeEvent
	 * @return session id
	 */
	public String getUserSessionId(final AbstractEvent evt)
	{
		String sessionId = null;
		final Object source = evt.getSource();
		if (source instanceof JaloSession)
		{
			sessionId = ((JaloSession) source).getSessionID();
		}
		return sessionId;
	}

	public BaseSiteModel getBaseSiteFromCronJob()
	{
		BaseSiteModel baseSite = null;
		final SearchResult<ProductConfigurationPersistenceCleanUpCronJobModel> result = getFlexibleSearchService()
				.search(SELECT_CRON_JOB_MODEL);
		if (result.getTotalCount() > 0 && null != result.getResult().get(0).getBaseSite())
		{
			baseSite = result.getResult().get(0).getBaseSite();
		}
		return baseSite;
	}

	protected FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	@Required
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

}
