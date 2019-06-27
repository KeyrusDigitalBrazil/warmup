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
package de.hybris.platform.accountsummaryaddon.document.dao.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateIfAnyResult;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.accountsummaryaddon.constants.AccountsummaryaddonConstants;
import de.hybris.platform.accountsummaryaddon.document.AccountSummaryDocumentQuery;
import de.hybris.platform.accountsummaryaddon.document.Range;
import de.hybris.platform.accountsummaryaddon.document.criteria.DefaultCriteria;
import de.hybris.platform.accountsummaryaddon.document.dao.PagedB2BDocumentDao;
import de.hybris.platform.accountsummaryaddon.model.B2BDocumentModel;
import de.hybris.platform.accountsummaryaddon.model.B2BDocumentTypeModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commerceservices.search.dao.impl.DefaultPagedGenericDao;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.SortData;
import de.hybris.platform.util.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


public class DefaultPagedB2BDocumentDao extends DefaultPagedGenericDao<B2BDocumentModel> implements PagedB2BDocumentDao
{

	private static final Logger LOG = Logger.getLogger(DefaultPagedB2BDocumentDao.class);

	private static final String DEFAULT_SORT_CODE = Config.getString(AccountsummaryaddonConstants.DEFAULT_SORT_CODE_PROP,
			"byDocumentDateAsc");

	private static final String ORDER_BY_STATEMENT = " ORDER BY ";
	private static final String AND_STATEMENT = " AND ";
	private static final String WHERE_STATEMENT = " WHERE ";

	private static final String B2B_UNIT_CRITERIA = "{" + B2BUnitModel._TYPECODE + ":" + B2BUnitModel.UID + "} = ?b2bUnitCode";

	private static final String FIND_DOCUMENT = "SELECT {" + B2BDocumentModel._TYPECODE + ":" + B2BDocumentModel.PK + "}  FROM { "
			+ B2BDocumentModel._TYPECODE + " as " + B2BDocumentModel._TYPECODE + " join " + B2BDocumentTypeModel._TYPECODE + " as "
			+ B2BDocumentTypeModel._TYPECODE + " on {" + B2BDocumentModel._TYPECODE + ":" + B2BDocumentModel.DOCUMENTTYPE + "} = {"
			+ B2BDocumentTypeModel._TYPECODE + ":" + B2BDocumentTypeModel.PK + "} join " + B2BUnitModel._TYPECODE + " as "
			+ B2BUnitModel._TYPECODE + " on {" + B2BDocumentModel._TYPECODE + ":" + B2BDocumentModel.UNIT + "} = {"
			+ B2BUnitModel._TYPECODE + ":" + B2BUnitModel.PK + "} }  ";

	private static final String FIND_ALL_DOCUMENTS = "SELECT {" + B2BDocumentModel._TYPECODE + ":" + B2BDocumentModel.PK
			+ "} FROM { " + B2BDocumentModel._TYPECODE + " as " + B2BDocumentModel._TYPECODE + " join "
			+ B2BDocumentTypeModel._TYPECODE + " as " + B2BDocumentTypeModel._TYPECODE + " on {" + B2BDocumentModel._TYPECODE + ":"
			+ B2BDocumentModel.DOCUMENTTYPE + "} = {" + B2BDocumentTypeModel._TYPECODE + ":" + B2BDocumentTypeModel.PK + "} }";

	private static final String FIND_ALL_DOCUMENTS_FOR_UNIT_DOCUMENT_TYPE = "SELECT {" + B2BDocumentModel._TYPECODE + ":"
			+ B2BDocumentModel.PK + "} FROM { " + B2BDocumentModel._TYPECODE + " as " + B2BDocumentModel._TYPECODE + " join "
			+ B2BDocumentTypeModel._TYPECODE + " as " + B2BDocumentTypeModel._TYPECODE + " on {" + B2BDocumentModel._TYPECODE + ":"
			+ B2BDocumentModel.DOCUMENTTYPE + "} = {" + B2BDocumentTypeModel._TYPECODE + ":" + B2BDocumentTypeModel.PK + "} join "
			+ B2BUnitModel._TYPECODE + " as " + B2BUnitModel._TYPECODE + " on {" + B2BDocumentModel._TYPECODE + ":"
			+ B2BDocumentModel.UNIT + "} = {" + B2BUnitModel._TYPECODE + ":" + B2BUnitModel.PK + "} }";

	private static final String DESC = "DESC";

	private static final String ORDER_BY_QUERY = " ORDER BY { %s }";

	private Map<String, String> b2bDocumentSortMap;

	/**
	 * @return the b2bDocumentSortMap
	 */
	public Map<String, String> getB2bDocumentSortMap()
	{
		return b2bDocumentSortMap;
	}

	/**
	 * @param b2bDocumentSortMap
	 *           the b2bDocumentSortMap to set
	 */
	public void setB2bDocumentSortMap(final Map<String, String> b2bDocumentSortMap)
	{
		this.b2bDocumentSortMap = b2bDocumentSortMap;
	}

	public DefaultPagedB2BDocumentDao()
	{
		super(B2BDocumentModel._TYPECODE);
	}

	@Override
	public SearchPageData<B2BDocumentModel> findDocuments(final AccountSummaryDocumentQuery query)
	{
		final Map<String, Object> mapCriteria = createMapCriteria(query.getSearchCriteria());

		return getPagedFlexibleSearchService().search(createQuery(query, mapCriteria), mapCriteria, query.getPageableData());
	}

	protected Map<String, Object> createMapCriteria(final Map<String, Object> searchCriteria)
	{
		final Map<String, Object> mapCriteria = new HashMap<String, Object>();

		for (final Entry<String, Object> entry : searchCriteria.entrySet())
		{
			final String entryKey = entry.getKey();
			final Object entryValue = entry.getValue();
			if (entryValue instanceof Range)
			{
				final Range range = (Range) entryValue;

				mapCriteria.put(entryKey + "_min", range.getMinBoundary());
				mapCriteria.put(entryKey + "_max", range.getMaxBoundary());
			}
			else
			{
				mapCriteria.put(entryKey, entryValue);
			}
		}

		return mapCriteria;
	}


	protected String createQuery(final AccountSummaryDocumentQuery query, final Map<String, Object> mapCriteria)
	{
		final StringBuilder queryBuff = new StringBuilder();
		queryBuff.append(FIND_DOCUMENT);

		queryBuff.append(getWhereStatement(mapCriteria));

		queryBuff.append(getOrderStatement(query.getPageableData().getSort(), query.isAsc()));

		return queryBuff.toString();
	}

	protected String getWhereStatement(final Map<String, Object> criteria)
	{
		final StringBuilder whereStatement = new StringBuilder();
		whereStatement.append(documentTypeDisplayInAllListFilter(criteria));

		if (criteria != null && !criteria.isEmpty())
		{
			if (StringUtils.isBlank(whereStatement.toString()))
			{
				whereStatement.append(WHERE_STATEMENT);
			}
			else
			{
				whereStatement.append(AND_STATEMENT);
			}


			final String[] criteriaNames = criteria.keySet().toArray(new String[0]);
			for (int i = 0; i < criteriaNames.length; i++)
			{
				if (StringUtils.endsWith(criteriaNames[i], "_min"))
				{
					whereStatement.append(formatField(criteriaNames[i])).append(" >= ?").append(criteriaNames[i]);
				}
				else if (StringUtils.endsWith(criteriaNames[i], "_max"))
				{
					whereStatement.append(formatField(criteriaNames[i])).append(" <= ?").append(criteriaNames[i]);
				}
				else if (StringUtils.equalsIgnoreCase(B2BDocumentModel.UNIT, criteriaNames[i]))
				{
					whereStatement.append(formatField(criteriaNames[i])).append(" = ?").append(criteriaNames[i]);
				}
				else if (criteria.get(criteriaNames[i]) instanceof List<?>)
				{
					whereStatement.append(formatField(criteriaNames[i])).append(" IN (?").append(criteriaNames[i]).append(")");
				}
				else
				{
					whereStatement.append(formatField(criteriaNames[i])).append(" like ?").append(criteriaNames[i]);
				}


				if (i + 1 < criteriaNames.length)
				{
					whereStatement.append(AND_STATEMENT);
				}
			}
		}
		return whereStatement.toString();
	}

	protected String documentTypeDisplayInAllListFilter(final Map<String, Object> criteria)
	{
		final StringBuilder whereStatement = new StringBuilder();

		// If the document type of one document is set to displayInAllList=false, this document can show up in result list
		// only when search by document type or number
		//
		// for example: for document "statement" STA-001, it is set to displayInAllList=false the "statement" documents
		// show up in the result list only when user searches by document type = "statement" or document#="STA-001"
		if (criteria == null || criteria.isEmpty()
				|| (!criteria.containsKey(B2BDocumentModel.DOCUMENTTYPE) && !criteria.containsKey(B2BDocumentModel.DOCUMENTNUMBER)))
		{
			whereStatement.append(WHERE_STATEMENT);
			whereStatement.append("{" + B2BDocumentTypeModel._TYPECODE + ":" + B2BDocumentTypeModel.DISPLAYINALLLIST + " } = true ");
		}

		return whereStatement.toString();
	}

	protected String getOrderStatement(final String sortField, final boolean isAsc)
	{
		return ORDER_BY_STATEMENT + formatField(sortField) + (isAsc ? " ASC " : " DESC ");
	}

	protected String formatField(final String fieldName)
	{
		if (StringUtils.equalsIgnoreCase(B2BDocumentModel.DOCUMENTTYPE, fieldName))
		{
			return " {" + B2BDocumentTypeModel._TYPECODE + ":" + B2BDocumentTypeModel.CODE + " } ";
		}
		else if (StringUtils.equalsIgnoreCase(B2BDocumentModel.UNIT, fieldName))
		{
			return " {" + B2BUnitModel._TYPECODE + ":" + B2BUnitModel.UID + " } ";
		}
		else
		{
			return " {" + B2BDocumentModel._TYPECODE + ":" + getFiedName(fieldName) + "} ";
		}
	}

	protected String getFiedName(final String fieldName)
	{
		if (StringUtils.endsWith(fieldName, "_max") || StringUtils.endsWith(fieldName, "_min"))
		{
			return StringUtils.substringBefore(fieldName, "_");
		}
		return fieldName;
	}

	@Override
	public SearchPageData<B2BDocumentModel> getPagedDocumentsForUnit(final String b2bUnitCode, final PageableData pageableData,
			final List<DefaultCriteria> criteriaList)
	{
		validateParameterNotNull(b2bUnitCode, "b2bUnitCode must not be null");
		return getPagedDocuments(Optional.of(b2bUnitCode), pageableData, criteriaList);
	}

	@Override
	public SearchPageData<B2BDocumentModel> getAllPagedDocuments(final PageableData pageableData,
			final List<DefaultCriteria> criteriaList)
	{
		return getPagedDocuments(Optional.empty(), pageableData, criteriaList);
	}

	protected SearchPageData<B2BDocumentModel> getPagedDocuments(final Optional<String> b2bUnitCode,
			final PageableData pageableData, final List<DefaultCriteria> criteriaList)
	{
		validateParameterNotNull(pageableData, "pageableData must not be null");
		validateIfAnyResult(criteriaList, "criteria must not be null");

		final StringBuilder queryBuilder = new StringBuilder();
		final List<String> whereQueryList = new ArrayList<String>();
		final Map<String, Object> queryParams = new HashMap<String, Object>();
		if (b2bUnitCode.isPresent())
		{
			queryParams.put("b2bUnitCode", b2bUnitCode.get());
			queryBuilder.append(FIND_ALL_DOCUMENTS_FOR_UNIT_DOCUMENT_TYPE);
			whereQueryList.add(B2B_UNIT_CRITERIA);
		}
		else
		{
			queryBuilder.append(FIND_ALL_DOCUMENTS);
		}

		criteriaList.forEach(criteria -> criteria.populateCriteriaQueryAndParamsMap(whereQueryList, queryParams));

		final String selectedSortCode = StringUtils.isNotBlank(pageableData.getSort()) ? pageableData.getSort() : DEFAULT_SORT_CODE;
		final boolean isDesc = StringUtils.containsIgnoreCase(selectedSortCode, "desc");
		final String sortValue = getB2bDocumentSortMap().get(selectedSortCode);
		buildWhereQuery(whereQueryList, queryBuilder);
		queryBuilder.append(String.format(ORDER_BY_QUERY,
				(StringUtils.containsIgnoreCase(selectedSortCode, B2BDocumentModel.DOCUMENTTYPE) ? B2BDocumentTypeModel._TYPECODE
						+ ":" : StringUtils.EMPTY)
						+ sortValue));
		if (isDesc)
		{
			queryBuilder.append(DESC);
		}

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Search Query : " + queryBuilder.toString());
		}

		final SearchPageData<B2BDocumentModel> searchPageData = getPagedFlexibleSearchService().search(queryBuilder.toString(),
				queryParams, pageableData);

		// Specify which sort was used
		searchPageData.getPagination().setSort(selectedSortCode);
		searchPageData.setSorts(createSorts(getB2bDocumentSortMap().keySet(), selectedSortCode));

		return searchPageData;
	}

	protected List<SortData> createSorts(final Set<String> sortKeys, final String selectedSortCode)
	{
		final List<SortData> result = new ArrayList<SortData>(sortKeys.size());
		for (final String sortKey : sortKeys)
		{
			result.add(createSort(sortKey, selectedSortCode));
		}
		return result;
	}

	protected SortData createSort(final String sortKey, final String selectedSortCode)
	{
		final SortData sortData = new SortData();
		sortData.setCode(sortKey);
		sortData.setSelected(selectedSortCode != null && selectedSortCode.equalsIgnoreCase(sortKey));
		return sortData;
	}

	protected void buildWhereQuery(final List<String> whereQueryList, final StringBuilder queryBuilder)
	{
		boolean first = true;
		for (final String whereQuery : whereQueryList)
		{
			if (first)
			{
				queryBuilder.append(WHERE_STATEMENT);
			}
			else
			{
				queryBuilder.append(AND_STATEMENT);
			}
			queryBuilder.append(whereQuery);
			first = false;
		}
	}
}
