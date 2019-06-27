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
package de.hybris.platform.accountsummaryaddon.document.criteria;

import de.hybris.platform.accountsummaryaddon.enums.DocumentStatus;
import de.hybris.platform.accountsummaryaddon.model.B2BDocumentModel;
import de.hybris.platform.accountsummaryaddon.model.B2BDocumentTypeModel;
import de.hybris.platform.accountsummaryaddon.utils.AccountSummaryAddonUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


/**
 *
 */
public class DefaultCriteria
{
	private static final Logger LOG = Logger.getLogger(DefaultCriteria.class);

	protected final String DOCUMENT_STATUS_QUERY = "{" + B2BDocumentModel._TYPECODE + ":" + B2BDocumentModel.STATUS + "} = %s";
	protected final String RANGE_QUERY = "{" + B2BDocumentModel._TYPECODE + ":%s } %s= %s";
	protected final String DISPLAY_IN_ALL_LIST = "{" + B2BDocumentTypeModel._TYPECODE + ":"
			+ B2BDocumentTypeModel.DISPLAYINALLLIST + " } = true";
	protected static final String QUERY_CRITERIA = "populating query criteria with: \"%s\" and query params map with: \"%s\"";

	protected String filterByKey;
	protected Optional<DocumentStatus> b2bDocumentStatus;

	public DefaultCriteria(final String filterByKey)
	{
		this(filterByKey, StringUtils.EMPTY);
	}

	public DefaultCriteria(final String filterByKey, final String documentStatus)
	{
		super();
		this.setFilterByKey(filterByKey);
		this.setB2bDocumentStatus(documentStatus);
	}

	/**
	 * @return the filterByKey
	 */
	public String getFilterByKey()
	{
		return filterByKey;
	}

	/**
	 * @param filterByKey
	 *           the filterByKey to set
	 */
	protected final void setFilterByKey(final String filterByKey)
	{
		this.filterByKey = filterByKey;
	}

	/**
	 * @return the b2bDocumentStatus
	 */
	public Optional<DocumentStatus> getB2bDocumentStatus()
	{
		return b2bDocumentStatus;
	}

	/**
	 * @param b2bDocumentStatus
	 *           the b2bDocumentStatus to set
	 */
	protected final void setB2bDocumentStatus(final String b2bDocumentStatus)
	{
		this.b2bDocumentStatus = StringUtils.isNotBlank(b2bDocumentStatus)
				&& AccountSummaryAddonUtils.getDocumentStatusList().contains(b2bDocumentStatus) ? Optional.of(DocumentStatus
				.valueOf(b2bDocumentStatus)) : Optional.empty();

	}

	public void setCriteriaValues(final FilterByCriteriaData filterByCriteriaData)
	{
		setB2bDocumentStatus(filterByCriteriaData.getDocumentStatus());
	}

	/**
	 * @param whereQueryList
	 * @param queryParams
	 */
	public void populateCriteriaQueryAndParamsMap(final List<String> whereQueryList, final Map<String, Object> queryParams)
	{
		if (getB2bDocumentStatus().isPresent())
		{
			final String formattedQuery = String.format(DOCUMENT_STATUS_QUERY, "?b2bDocumentStatus");

			if (LOG.isDebugEnabled())
			{
				LOG.debug(String.format(QUERY_CRITERIA, formattedQuery, getB2bDocumentStatus().get().name()));
			}
			whereQueryList.add(formattedQuery);
			queryParams.put("b2bDocumentStatus", getB2bDocumentStatus().get());
		}

		/*
		 * If the DocumentType of a Document is set to displayInAllList=false, then that document can only show up in
		 * result list if the search/filter is by documentType or documentNumber.
		 */
		if (!(StringUtils.equalsIgnoreCase(getFilterByKey(), B2BDocumentModel.DOCUMENTTYPE) || StringUtils.equalsIgnoreCase(
				getFilterByKey(), B2BDocumentModel.DOCUMENTNUMBER)))
		{
			whereQueryList.add(DISPLAY_IN_ALL_LIST);
		}
	}

}
