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
package de.hybris.platform.accountsummaryaddon.facade.impl;


import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.accountsummaryaddon.constants.AccountsummaryaddonConstants;
import de.hybris.platform.accountsummaryaddon.data.AccountSummaryInfoData;
import de.hybris.platform.accountsummaryaddon.document.AmountRange;
import de.hybris.platform.accountsummaryaddon.document.B2BDocumentQueryBuilder;
import de.hybris.platform.accountsummaryaddon.document.DateRange;
import de.hybris.platform.accountsummaryaddon.document.criteria.DefaultCriteria;
import de.hybris.platform.accountsummaryaddon.document.criteria.FilterByCriteriaData;
import de.hybris.platform.accountsummaryaddon.document.data.B2BAmountBalanceData;
import de.hybris.platform.accountsummaryaddon.document.data.B2BDocumentData;
import de.hybris.platform.accountsummaryaddon.document.data.B2BDocumentPaymentInfoData;
import de.hybris.platform.accountsummaryaddon.document.data.B2BDragAndDropData;
import de.hybris.platform.accountsummaryaddon.document.service.B2BDocumentPaymentInfoService;
import de.hybris.platform.accountsummaryaddon.document.service.B2BDocumentService;
import de.hybris.platform.accountsummaryaddon.document.service.B2BDocumentTypeService;
import de.hybris.platform.accountsummaryaddon.enums.DocumentStatus;
import de.hybris.platform.accountsummaryaddon.facade.B2BAccountSummaryFacade;
import de.hybris.platform.accountsummaryaddon.model.B2BDocumentModel;
import de.hybris.platform.accountsummaryaddon.model.B2BDocumentPaymentInfoModel;
import de.hybris.platform.accountsummaryaddon.model.B2BDocumentTypeModel;
import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bacceleratorfacades.exception.EntityValidationException;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the B2BAccountSummary facade
 *
 */
public class DefaultB2BAccountSummaryFacade implements B2BAccountSummaryFacade
{
	protected static final Logger LOG = Logger.getLogger(DefaultB2BAccountSummaryFacade.class);

	private B2BDocumentPaymentInfoService b2bDocumentPaymentInfoService;
	private B2BDocumentService b2bDocumentService;
	private B2BDocumentTypeService b2bDocumentTypeService;
	private B2BCommerceUnitService b2BCommerceUnitService;
	private Converter<B2BDocumentModel, B2BDocumentData> b2bDocumentConverter;
	private Converter<B2BDocumentPaymentInfoModel, B2BDocumentPaymentInfoData> b2bDocumentPaymentInfoConverter;
	private Converter<B2BUnitModel, B2BAmountBalanceData> b2bAmountBalanceConverter;
	private Converter<B2BUnitModel, AccountSummaryInfoData> accountSummaryInfoConverter;

	@Override
	public SearchPageData<B2BDocumentData> findDocuments(final Map<String, String> queryParameters)
	{
		final Map<String, Object> criteria = validateAndBuildFindDocumentsCriteria(queryParameters);

		final B2BDocumentQueryBuilder queryBuilder = buildDocumentQuery(queryParameters, criteria);

		return convertPageData(b2bDocumentService.findDocuments(queryBuilder.build()), getB2bDocumentConverter());
	}

	protected Map<String, Object> validateAndBuildFindDocumentsCriteria(final Map<String, String> parameters)
	{

		final Map<String, Object> criteria = new HashMap<String, Object>();

		if (isDateCriteria(parameters.get("searchBy")) && StringUtils.isNotBlank(parameters.get("searchRangeMin"))
				&& StringUtils.isNotBlank(parameters.get("searchRangeMax")))
		{
			criteria.put(parameters.get("searchBy"), new DateRange(validateAndFormatDate(parameters.get("searchRangeMin")),
					validateAndFormatDate(parameters.get("searchRangeMax"))));
		}
		else if (isAmountCriteria(parameters.get("searchBy"))
				&& (StringUtils.isNotBlank(parameters.get("searchRangeMin")) || StringUtils.isNotBlank(parameters
						.get("searchRangeMax"))))
		{
			criteria.put(parameters.get("searchBy"), new AmountRange(validateAndFormatAmount(parameters.get("searchRangeMin")),
					validateAndFormatAmount(parameters.get("searchRangeMax"))));
		}
		else if (StringUtils.isNotBlank(parameters.get("searchValue")))
		{
			// Uppercase Document Number
			if (AccountsummaryaddonConstants.SEARCH_BY_DOCUMENTNUMBER.equalsIgnoreCase(parameters.get("searchBy")))
			{
				criteria.put(parameters.get("searchBy"), parameters.get("searchValue").toUpperCase());
			}
			else
			{
				criteria.put(parameters.get("searchBy"), parameters.get("searchValue"));
			}
		}

		criteria.put(B2BDocumentModel.UNIT, parameters.get("unit"));

		return criteria;
	}

	protected Date validateAndFormatDate(final String date)
	{
		try
		{
			return DateUtils.parseDate(date, new String[]
			{ AccountsummaryaddonConstants.DATE_FORMAT_MM_DD_YYYY });
		}
		catch (final ParseException e)
		{
			throw new EntityValidationException(String.format("Invalid date [%s]", date), e);
		}
	}

	protected BigDecimal validateAndFormatAmount(final String amount)
	{
		try
		{
			if (StringUtils.isBlank(amount))
			{
				return BigDecimal.ZERO;
			}
			else
			{
				return new BigDecimal(amount);
			}
		}
		catch (final RuntimeException e)
		{
			throw new EntityValidationException(String.format("Invalid amount [%s]", amount), e);
		}
	}

	protected B2BDocumentQueryBuilder buildDocumentQuery(final Map<String, String> params, final Map<String, Object> criteria)
	{
		final B2BDocumentQueryBuilder queryBuilder = new B2BDocumentQueryBuilder(Integer.parseInt(params.get("currentPage")),
				Integer.parseInt(params.get("pageSize")), params.get("searchBy"), StringUtils.equals(
						AccountsummaryaddonConstants.DEFAULT_SORT, params.get("sort")));

		if (AccountsummaryaddonConstants.SEARCH_STATUS_OPEN.equalsIgnoreCase(params.get("status")))
		{
			queryBuilder.addCriteria(B2BDocumentModel.STATUS, DocumentStatus.OPEN);
		}
		else if (AccountsummaryaddonConstants.SEARCH_STATUS_CLOSED.equalsIgnoreCase(params.get("status")))
		{
			queryBuilder.addCriteria(B2BDocumentModel.STATUS, DocumentStatus.CLOSED);
		}

		queryBuilder.addAllCriterias(criteria);

		return queryBuilder;
	}

	protected boolean isAmountCriteria(final String searchType)
	{
		return StringUtils.equals(B2BDocumentModel.AMOUNT, searchType)
				|| StringUtils.equals(B2BDocumentModel.OPENAMOUNT, searchType);
	}

	protected boolean isDateCriteria(final String searchType)
	{
		return StringUtils.equals(B2BDocumentModel.DATE, searchType) || StringUtils.equals(B2BDocumentModel.DUEDATE, searchType);
	}

	@Override
	public B2BAmountBalanceData getAmountBalance(final B2BUnitModel unit)
	{
		return b2bAmountBalanceConverter.convert(unit);
	}

	@Override
	public SearchResult<B2BDocumentModel> getOpenDocuments(final MediaModel mediaModel)
	{
		return b2bDocumentService.getOpenDocuments(mediaModel);
	}

	protected <S, T> SearchPageData<T> convertPageData(final SearchPageData<S> source, final Converter<S, T> converter)
	{
		final SearchPageData<T> result = new SearchPageData<T>();
		result.setPagination(source.getPagination());
		result.setSorts(source.getSorts());
		result.setResults(Converters.convertAll(source.getResults(), converter));
		return result;
	}

	@Required
	public void setB2bDocumentConverter(final Converter<B2BDocumentModel, B2BDocumentData> b2bDocumentConverter)
	{
		this.b2bDocumentConverter = b2bDocumentConverter;
	}

	@Required
	public void setB2bDocumentPaymentInfoConverter(
			final Converter<B2BDocumentPaymentInfoModel, B2BDocumentPaymentInfoData> converter)
	{
		this.b2bDocumentPaymentInfoConverter = converter;
	}

	public Converter<B2BDocumentModel, B2BDocumentData> getB2bDocumentConverter()
	{
		return b2bDocumentConverter;
	}

	@Required
	public void setB2bDocumentService(final B2BDocumentService b2bDocumentService)
	{
		this.b2bDocumentService = b2bDocumentService;
	}


	@Required
	public void setB2bDocumentPaymentInfoService(final B2BDocumentPaymentInfoService service)
	{
		this.b2bDocumentPaymentInfoService = service;
	}

	public void setB2bDocumentTypeService(final B2BDocumentTypeService b2bDocumentTypeService)
	{
		this.b2bDocumentTypeService = b2bDocumentTypeService;
	}

	@Override
	public SearchResult<B2BDocumentTypeModel> getAllDocumentTypes()
	{
		return b2bDocumentTypeService.getAllDocumentTypes();
	}

	@Override
	public List<B2BDocumentPaymentInfoData> getDocumentPaymentInfo(final String documentNumber)
	{
		final SearchResult<B2BDocumentPaymentInfoModel> result = b2bDocumentPaymentInfoService
				.getDocumentPaymentInfo(documentNumber);
		return Converters.convertAll(result.getResult(), b2bDocumentPaymentInfoConverter);
	}

	@Override
	public void applyDragAndDropActions(final List<B2BDragAndDropData> lstActions)
	{
		b2bDocumentPaymentInfoService.applyPayment(lstActions);

	}

	public Converter<B2BUnitModel, B2BAmountBalanceData> getB2bAmountBalanceConverter()
	{
		return b2bAmountBalanceConverter;
	}

	@Required
	public void setB2bAmountBalanceConverter(final Converter<B2BUnitModel, B2BAmountBalanceData> b2bAmountBalanceConverter)
	{
		this.b2bAmountBalanceConverter = b2bAmountBalanceConverter;
	}

	/**
	 * @return the b2BCommerceUnitService
	 */
	public B2BCommerceUnitService getB2BCommerceUnitService()
	{
		return b2BCommerceUnitService;
	}

	/**
	 * @param b2bCommerceUnitService
	 *           the b2BCommerceUnitService to set
	 */
	public void setB2BCommerceUnitService(final B2BCommerceUnitService b2bCommerceUnitService)
	{
		b2BCommerceUnitService = b2bCommerceUnitService;
	}

	/**
	 * @return the accountSummaryInfoConverter
	 */
	public Converter<B2BUnitModel, AccountSummaryInfoData> getAccountSummaryInfoConverter()
	{
		return accountSummaryInfoConverter;
	}

	/**
	 * @param accountSummaryInfoConverter
	 *           the accountSummaryInfoConverter to set
	 */
	public void setAccountSummaryInfoConverter(final Converter<B2BUnitModel, AccountSummaryInfoData> accountSummaryInfoConverter)
	{
		this.accountSummaryInfoConverter = accountSummaryInfoConverter;
	}

	@Override
	public AccountSummaryInfoData getAccountSummaryInfoData(final String b2bUnitCode)
	{
		validateParameterNotNull(b2bUnitCode, "b2bUnitCode must not be null");

		final B2BUnitModel b2bUnitModel = getB2BCommerceUnitService().getUnitForUid(b2bUnitCode);
		if (b2bUnitModel == null)
		{
			throw new UnknownIdentifierException("Unit with code " + b2bUnitCode + " not found for current user");
		}

		return getAccountSummaryInfoConverter().convert(b2bUnitModel);
	}

	@Override
	public SearchPageData<B2BDocumentData> getPagedDocumentsForUnit(final String b2bUnitCode, final PageableData pageableData,
			final FilterByCriteriaData filterByCriteriaData, final DefaultCriteria criteria)
	{
		validateParameterNotNull(b2bUnitCode, "b2bUnitCode must not be null");
		validateParameterNotNull(pageableData, "pageableData must not be null");
		validateParameterNotNull(criteria, "criteria must not be null");

		criteria.setCriteriaValues(filterByCriteriaData);

		final List<DefaultCriteria> filterByCriteriaList = Collections.singletonList(criteria);

		return convertPageData(b2bDocumentService.getPagedDocumentsForUnit(b2bUnitCode, pageableData, filterByCriteriaList),
				getB2bDocumentConverter());
	}
}
