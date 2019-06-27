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
package de.hybris.platform.accountsummaryaddon.utils;

import de.hybris.platform.accountsummaryaddon.constants.AccountsummaryaddonConstants;
import de.hybris.platform.accountsummaryaddon.document.criteria.AmountRangeCriteria;
import de.hybris.platform.accountsummaryaddon.document.criteria.DateRangeCriteria;
import de.hybris.platform.accountsummaryaddon.document.criteria.DefaultCriteria;
import de.hybris.platform.accountsummaryaddon.document.criteria.DocumentTypeCriteria;
import de.hybris.platform.accountsummaryaddon.document.criteria.RangeCriteria;
import de.hybris.platform.accountsummaryaddon.document.criteria.SingleValueCriteria;
import de.hybris.platform.accountsummaryaddon.enums.DocumentStatus;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


public class AccountSummaryAddonUtils
{
	private static final Logger LOG = Logger.getLogger(AccountSummaryAddonUtils.class);

	private AccountSummaryAddonUtils()
	{
		throw new IllegalStateException("Cannot Instantiate an Utility Class");
	}

	public static DefaultCriteria createFilterByCriteriaObject(final String documentStatus, final String filterByKey)
	{
		return new DefaultCriteria(filterByKey, documentStatus);
	}

	public static DocumentTypeCriteria createTypeCriteriaObject(final String typeCriteriaCode, final String documentStatus,
			final String filterByKey)
	{
		return new DocumentTypeCriteria(filterByKey, typeCriteriaCode, documentStatus);
	}

	public static SingleValueCriteria createSingleValueCriteriaObject(final String filterByValue, final String documentStatus,
			final String filterByKey)
	{
		return new SingleValueCriteria(filterByKey, filterByValue, documentStatus);
	}

	public static RangeCriteria createRangeCriteriaObject(final String startRange, final String endRange,
			final String documentStatus, final String filterByKey)
	{
		return new RangeCriteria(filterByKey, startRange, endRange, documentStatus);
	}

	public static AmountRangeCriteria createAmountRangeCriteriaObject(final String startRange, final String endRange,
			final String documentStatus, final String filterByKey)
	{
		return new AmountRangeCriteria(filterByKey, startRange, endRange, documentStatus);
	}

	public static DateRangeCriteria createDateRangeCriteriaObject(final String startRange, final String endRange,
			final String documentStatus, final String filterByKey)
	{
		return new DateRangeCriteria(filterByKey, startRange, endRange, documentStatus);
	}

	public static List<String> getDocumentStatusList()
	{
		return Stream.of(DocumentStatus.values()).map(DocumentStatus::name).collect(Collectors.toList());
	}

	public static Optional<BigDecimal> parseBigDecimal(final String value)
	{
		Optional<BigDecimal> optionalValue = Optional.empty();
		try
		{
			optionalValue = Optional.of(new BigDecimal(value));
		}
		catch (final NumberFormatException nfe)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug(nfe.getMessage());
			}
		}

		return optionalValue;
	}

	public static Optional<Date> parseDate(final String value, final String format)
	{
		Optional<Date> optionalValue = Optional.empty();
		try
		{
			final SimpleDateFormat sdf = new SimpleDateFormat(format);
			sdf.setLenient(false);
			optionalValue = Optional.of(sdf.parse(value));
		}
		catch (final ParseException pe)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug(pe.getMessage());
			}
		}

		return optionalValue;
	}

	public static final Optional<Object> toOptional(final String s)
	{
		return StringUtils.isNotBlank(s) ? Optional.of(s) : Optional.empty();
	}

	public static final Optional<BigDecimal> parseBigDecimalToOptional(final String s)
	{
		return StringUtils.isNotBlank(s) ? parseBigDecimal(s) : Optional.empty();
	}

	public static final Optional<Date> parseDateToOptional(final String s)
	{
		return StringUtils.isNotBlank(s) ? parseDate(s, AccountsummaryaddonConstants.DATE_FORMAT_MM_DD_YYYY) : Optional.empty();
	}

}
