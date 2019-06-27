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
package de.hybris.platform.commerceservices.consent.dao.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.commerceservices.consent.dao.ConsentDao;
import de.hybris.platform.commerceservices.model.consent.ConsentModel;
import de.hybris.platform.commerceservices.model.consent.ConsentTemplateModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;


/**
 * Default implementation of {@link ConsentDao} interface extending {@link DefaultGenericDao}
 */
public class DefaultConsentDao extends DefaultGenericDao<ConsentModel> implements ConsentDao
{
	private static final String FIND_CONSENT_BY_CUSTOMER_AND_TEMPLATE = "SELECT {uc:" + ConsentModel.PK + "} FROM {"
			+ ConsentModel._TYPECODE + " as uc} WHERE {uc:" + ConsentModel.CONSENTTEMPLATE + "} = ?" + ConsentModel.CONSENTTEMPLATE
			+ " AND {" + ConsentModel.CUSTOMER + "} = ?" + ConsentModel.CUSTOMER;

	private static final String FIND_CONSENTS_BY_CUSTOMER = "SELECT {uc:" + ConsentModel.PK + "} FROM {" + ConsentModel._TYPECODE
			+ " as uc} WHERE {" + ConsentModel.CUSTOMER + "} = ?" + ConsentModel.CUSTOMER;

	private static final String ORDER_BY_CONSENT_GIVEN_DATE_DESC = " ORDER BY {uc:" + ConsentModel.CONSENTGIVENDATE + "} DESC";

	public DefaultConsentDao()
	{
		super(ConsentModel._TYPECODE);
	}

	@Override
	public ConsentModel findConsentByCustomerAndConsentTemplate(final CustomerModel customer,
			final ConsentTemplateModel consentTemplate)
	{
		validateParameterNotNullStandardMessage("customer", customer);
		validateParameterNotNullStandardMessage("consentTemplate", consentTemplate);

		final Map<String, Object> queryParams = populateBasicQueryParams(customer, consentTemplate);

		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(
				FIND_CONSENT_BY_CUSTOMER_AND_TEMPLATE + ORDER_BY_CONSENT_GIVEN_DATE_DESC);
		flexibleSearchQuery.getQueryParameters().putAll(queryParams);
		flexibleSearchQuery.setCount(1);

		final List<ConsentModel> consents = getFlexibleSearchService().<ConsentModel> search(flexibleSearchQuery).getResult();
		return CollectionUtils.isNotEmpty(consents) ? consents.get(0) : null;
	}

	protected Map<String, Object> populateBasicQueryParams(final CustomerModel customer,
			final ConsentTemplateModel consentTemplate)
	{
		final Map<String, Object> queryParams = new HashMap<>();
		queryParams.put(ConsentModel.CUSTOMER, customer);
		queryParams.put(ConsentModel.CONSENTTEMPLATE, consentTemplate);
		return queryParams;
	}

	@Override
	public List<ConsentModel> findAllConsentsByCustomer(final CustomerModel customer)
	{
		validateParameterNotNullStandardMessage("customer", customer);

		final SearchResult<ConsentModel> consents = getFlexibleSearchService().search(
				FIND_CONSENTS_BY_CUSTOMER + ORDER_BY_CONSENT_GIVEN_DATE_DESC,
				Collections.singletonMap(ConsentModel.CUSTOMER, customer));
		return consents.getResult();
	}
}
