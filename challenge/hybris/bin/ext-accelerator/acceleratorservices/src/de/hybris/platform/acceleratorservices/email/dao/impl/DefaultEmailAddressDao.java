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
package de.hybris.platform.acceleratorservices.email.dao.impl;

import de.hybris.platform.acceleratorservices.email.dao.EmailAddressDao;
import de.hybris.platform.acceleratorservices.model.email.EmailAddressModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.util.ServicesUtil;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;


/**
 * Default Data Access for looking up email addresses.
 */
public class DefaultEmailAddressDao extends DefaultGenericDao<EmailAddressModel> implements EmailAddressDao
{
	private static final Logger LOG = Logger.getLogger(DefaultEmailAddressDao.class);


	public DefaultEmailAddressDao()
	{
		super(EmailAddressModel._TYPECODE);
	}

	@Override
	public EmailAddressModel findEmailAddressByEmailAndDisplayName(final String emailAddress, final String displayName)
	{
		ServicesUtil.validateParameterNotNull(emailAddress, "emailAddress must not be null");

		final Map<String, Object> params = new HashMap<String, Object>();

		params.put(EmailAddressModel.EMAILADDRESS, emailAddress);
		if (displayName != null)
		{
			params.put(EmailAddressModel.DISPLAYNAME, displayName);
		}

		if (LOG.isDebugEnabled())
		{
			LOG.info("Searching for emailAddress [" + emailAddress + "] displayName [" + displayName + "]");
		}

		final StringBuilder query = new StringBuilder("SELECT {").append(ItemModel.PK).append("} FROM  {")
				.append(EmailAddressModel._TYPECODE).append("} WHERE {").append(EmailAddressModel.EMAILADDRESS)
				.append("} = ?").append(EmailAddressModel.EMAILADDRESS);
		if (displayName == null)
		{
			query.append(" AND {").append(EmailAddressModel.DISPLAYNAME).append("} is null ");
		}
		else
		{
			query.append(" AND {").append(EmailAddressModel.DISPLAYNAME).append("} = ?").append(EmailAddressModel.DISPLAYNAME);
		}


		final SearchResult<EmailAddressModel> results = getFlexibleSearchService().<EmailAddressModel> search(query.toString(),
				params);

		if (LOG.isDebugEnabled())
		{
			LOG.info("Results: " + (results == null ? "null" : String.valueOf(results.getCount())));
		}

		return CollectionUtils.isEmpty(results.getResult()) ? null : results.getResult().iterator().next(); // NOSONAR
	}
}
