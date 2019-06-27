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
package de.hybris.platform.secaddon.services;

import de.hybris.platform.secaddon.constants.SecaddonConstants;
import de.hybris.platform.secaddon.data.CustomerInfo;
import de.hybris.platform.util.Config;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import com.sap.platform.sapcpconfiguration.service.SapCpServiceFactory;

import rx.Observable;

/**
 *
 * Customer Mapping adapter for retrieving customer id from YaaS in a blocking
 * way but not to affect other YaaS Client
 *
 */
public class CustomerMappingAdapter implements CustomerMappingClient {

	private SapCpServiceFactory sapCpServiceFactory;

	public SapCpServiceFactory getSapCpServiceFactory() {
		return sapCpServiceFactory;
	}

	@Required
	public void setSapCpServiceFactory(SapCpServiceFactory sapCpServiceFactory) {
		this.sapCpServiceFactory = sapCpServiceFactory;
	}

	@Override
	public Observable<List<CustomerInfo>> getCustomer(final String lang, final String mixinQuery, final String id) {
		return getAdaptee().getCustomer(lang, mixinQuery, id);
	}

	public Observable<List<CustomerInfo>> getCustomer(final String lang, final String id) {

		return getCustomer(lang, StringUtils.defaultIfEmpty(Config.getParameter(SecaddonConstants.MIXIN_QUERY_KEY),
				SecaddonConstants.MIXIN_QUERY_DEFAULT_VALUE), id);
	}

	public CustomerMappingClient getAdaptee() {

		return sapCpServiceFactory.lookupService(de.hybris.platform.secaddon.services.CustomerMappingClient.class);

	}

}
