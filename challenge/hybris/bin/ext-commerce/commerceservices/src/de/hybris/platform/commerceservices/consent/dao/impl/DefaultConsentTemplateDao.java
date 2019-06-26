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

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.consent.dao.ConsentTemplateDao;
import de.hybris.platform.commerceservices.model.consent.ConsentTemplateModel;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Default implementation of {@link ConsentTemplateDao} interface extending {@link DefaultGenericDao}
 */
public class DefaultConsentTemplateDao extends DefaultGenericDao<ConsentTemplateModel> implements ConsentTemplateDao
{
	private static final String FIND_LATEST_CONSENT_BY_ID_AND_BASESITE = MessageFormat.format(
			"SELECT '{'{0}'}' FROM '{'{1}'}' WHERE '{'{2}'}' = ?{2} AND '{'{3}'}' = ?{3} ORDER BY '{'{4}'}' DESC",
			ConsentTemplateModel.PK, ConsentTemplateModel._TYPECODE, ConsentTemplateModel.ID, ConsentTemplateModel.BASESITE,
			ConsentTemplateModel.VERSION);

	private static final String FIND_CONSENT_TEMPLATE_BY_ID_AND_VERSION_AND_BASESITE = MessageFormat.format(
			"SELECT '{'{0}'}' FROM '{'{1}'}' WHERE '{'{2}'}' = ?{2} AND '{'{3}'}' = ?{3} AND '{'{4}'}' = ?{4}",
			ConsentTemplateModel.PK, ConsentTemplateModel._TYPECODE, ConsentTemplateModel.ID, ConsentTemplateModel.BASESITE,
			ConsentTemplateModel.VERSION);

	private static final String FIND_CONSENT_TEMPLATES_BY_BASESITE = MessageFormat.format(
			"SELECT '{'{0}'}' FROM '{'{1} AS ct'}' WHERE '{'{4}'}' = ?{4} AND '{'{3}'}' = "
					+ "('{{' SELECT MAX('{'{3}'}') FROM '{'{1}'}' WHERE '{'{2}'}' = '{'ct:{2}'}' AND '{'{4}'}' = '{'ct:{4}'} }}')",
			ConsentTemplateModel.PK, ConsentTemplateModel._TYPECODE, ConsentTemplateModel.ID, ConsentTemplateModel.VERSION,
			ConsentTemplateModel.BASESITE);

	private static final String ORDER_BY_ID_ASC = " ORDER BY {" + ConsentTemplateModel.ID + "}";

	public DefaultConsentTemplateDao()
	{
		super(ConsentTemplateModel._TYPECODE);
	}

	@Override
	public ConsentTemplateModel findLatestConsentTemplateByIdAndSite(final String consentTemplateId, final BaseSiteModel baseSite)
	{
		validateParameterNotNullStandardMessage("consentTemplateId", consentTemplateId);
		validateParameterNotNullStandardMessage("baseSite", baseSite);

		final Map<String, Object> queryParams = new HashMap<>();
		queryParams.put(ConsentTemplateModel.ID, consentTemplateId);
		queryParams.put(ConsentTemplateModel.BASESITE, baseSite);

		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(FIND_LATEST_CONSENT_BY_ID_AND_BASESITE);
		flexibleSearchQuery.getQueryParameters().putAll(queryParams);
		flexibleSearchQuery.setCount(1);

		return getFlexibleSearchService().searchUnique(flexibleSearchQuery);
	}

	@Override
	public ConsentTemplateModel findConsentTemplateByIdAndVersionAndSite(final String consentTemplateId,
			final Integer consentTemplateVersion, final BaseSiteModel baseSite)
	{
		validateParameterNotNullStandardMessage("consentTemplateId", consentTemplateId);
		validateParameterNotNullStandardMessage("consentTemplateVersion", consentTemplateVersion);
		validateParameterNotNullStandardMessage("baseSite", baseSite);

		final Map<String, Object> queryParams = new HashMap<>();
		queryParams.put(ConsentTemplateModel.ID, consentTemplateId);
		queryParams.put(ConsentTemplateModel.BASESITE, baseSite);
		queryParams.put(ConsentTemplateModel.VERSION, consentTemplateVersion);

		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(
				FIND_CONSENT_TEMPLATE_BY_ID_AND_VERSION_AND_BASESITE);
		flexibleSearchQuery.getQueryParameters().putAll(queryParams);

		return getFlexibleSearchService().searchUnique(flexibleSearchQuery);
	}

	@Override
	public List<ConsentTemplateModel> findConsentTemplatesBySite(final BaseSiteModel baseSite)
	{
		validateParameterNotNullStandardMessage("baseSite", baseSite);

		final Map<String, Object> queryParams = new HashMap<>();
		queryParams.put(ConsentTemplateModel.BASESITE, baseSite);

		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(
				FIND_CONSENT_TEMPLATES_BY_BASESITE + ORDER_BY_ID_ASC);
		flexibleSearchQuery.getQueryParameters().putAll(queryParams);
		return getFlexibleSearchService().<ConsentTemplateModel> search(flexibleSearchQuery).getResult();
	}
}
