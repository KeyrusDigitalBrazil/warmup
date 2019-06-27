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
package de.hybris.platform.cmsfacades.namedquery.validator;

import de.hybris.platform.cms2.enums.SortDirection;
import de.hybris.platform.cmsfacades.constants.CmsfacadesConstants;
import de.hybris.platform.cmsfacades.data.NamedQueryData;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.google.common.base.Preconditions;


/**
 * Validates DTO for executing named query.
 *
 * <p>
 * Rules:</br>
 * <ul>
 * <li>namedQuery not null</li>
 * <li>namedQuery exists</li>
 * <li>pageSize > 0, if provided</li>
 * <li>pageSize <= <code>${cmswebservices.media.namedquery.max.pagesize}</code>, if provided</li>
 * <li>currentPage >= 0, if provided</li>
 * <li>sort formatting, if sort provided</li>
 * <li>sort parameter names, if sort provided</li>
 * <li>sort directions, if sort provided</li>
 * <li>params not null</li>
 * <li>params formatting</li>
 * <li>params parameter names</li>
 * </ul>
 * </p>
 */
public class NamedQueryDataValidator implements Validator
{
	private static final Logger LOG = LoggerFactory.getLogger(NamedQueryDataValidator.class);

	private static final String COMMA = ",";
	private static final String COLON = ":";

	private static final String PAGE_SIZE = "pageSize";
	private static final String CURRENT_PAGE = "currentPage";
	private static final String NAMED_QUERY = "namedQuery";
	private static final String SORT = "sort";
	private static final String PARAMS = "params";

	private Predicate<String> namedQueryExistsPredicate;
	private int maxPageSize;

	@Override
	public boolean supports(final Class<?> clazz)
	{
		return clazz.isAssignableFrom(NamedQueryData.class);
	}

	@Override
	public void validate(final Object obj, final Errors errors)
	{
		final NamedQueryData target = (NamedQueryData) obj;
		Preconditions.checkArgument(target.getQueryType() != null);

		final Set<String> validParamNames = getValidParameterNames(target.getQueryType());

		if (StringUtils.isEmpty(target.getNamedQuery()))
		{
			errors.rejectValue(NAMED_QUERY, CmsfacadesConstants.FIELD_REQUIRED);
		}
		else if (!getNamedQueryExistsPredicate().test(target.getNamedQuery()))
		{
			errors.rejectValue(NAMED_QUERY, CmsfacadesConstants.FIELD_DOES_NOT_EXIST);
		}

		if (!Objects.isNull(target.getPageSize()))
		{
			try
			{
				final int pageSize = Integer.parseInt(target.getPageSize());

				if (pageSize < 1)
				{
					errors.rejectValue(PAGE_SIZE, CmsfacadesConstants.FIELD_MIN_VIOLATED);
				}
				else if (pageSize >= getMaxPageSize())
				{
					errors.rejectValue(PAGE_SIZE, CmsfacadesConstants.FIELD_MAX_VIOLATED);
				}
			}
			catch (final NumberFormatException e)
			{
				errors.rejectValue(PAGE_SIZE, CmsfacadesConstants.FIELD_MIN_VIOLATED);
			}
		}

		if (!Objects.isNull(target.getCurrentPage()))
		{
			try
			{
				final int currentPage = Integer.parseInt(target.getCurrentPage());

				if (currentPage < 0)
				{
					errors.rejectValue(CURRENT_PAGE, CmsfacadesConstants.FIELD_MIN_VIOLATED);
				}
			}
			catch (final NumberFormatException e)
			{
				errors.rejectValue(PAGE_SIZE, CmsfacadesConstants.FIELD_MIN_VIOLATED);
			}
		}

		if (!StringUtils.isEmpty(target.getSort()))
		{
			final String[] sortPairs = target.getSort().split(COMMA);
			try (Stream<String> stream = Arrays.stream(sortPairs))
			{
				stream.forEach(sortPair -> validateSortPair(sortPair, validParamNames, errors));
			}
		}

		if (StringUtils.isEmpty(target.getParams()))
		{
			errors.rejectValue(PARAMS, CmsfacadesConstants.FIELD_REQUIRED);
		}
		else
		{
			final String[] paramPairs = target.getParams().split(COMMA);
			try (Stream<String> stream = Arrays.stream(paramPairs))
			{
				stream.forEach(paramPair -> validateParamPair(paramPair, validParamNames, errors));
			}
		}
	}

	/**
	 * Get valid parameters names as the attributes of the named query's query type.
	 *
	 * @param queryType
	 *           - the class representing the type of query
	 * @return list of valid params
	 */
	protected Set<String> getValidParameterNames(final Class<?> queryType)
	{
		final Field[] attributes = queryType.getDeclaredFields();
		try (Stream<Field> stream = Arrays.stream(attributes))
		{
			return stream//
					.filter(field -> Modifier.isPrivate(field.getModifiers())) //
					.map(field -> field.getName()) //
					.collect(Collectors.toSet());
		}
	}

	/**
	 * Validate the parameter name and sort direction of a single sortPair.
	 * <p>
	 * Expected Format: <code>{paramName}:{sortDirection}</code></br>
	 * Example: <code>code:ASC</code>
	 * </p>
	 *
	 * @param sortPair
	 * @param errors
	 */
	protected void validateSortPair(final String sortPair, final Set<String> validParamNames, final Errors errors)
	{
		final String[] sortValues = sortPair.split(COLON);
		if (sortValues.length != 2)
		{
			errors.rejectValue(SORT, CmsfacadesConstants.FIELD_FORMAT_INVALID);
		}
		else
		{
			if (!validParamNames.contains(sortValues[0]))
			{
				errors.rejectValue(SORT, CmsfacadesConstants.FIELD_NOT_ALLOWED, sortValues[0]);
			}
			if (!validSortDirection(sortValues[1]))
			{
				errors.rejectValue(SORT, CmsfacadesConstants.FIELD_NOT_ALLOWED, sortValues[1]);
			}
		}
	}

	/**
	 * Validate the parameter name of a single paramPair.
	 * <p>
	 * Expected Format: <code>{paramName}:{paramValue}</code></br>
	 * Example: <code>code:banner</code>
	 * </p>
	 *
	 * @param paramPair
	 * @param validParamNames
	 * @param errors
	 */
	@SuppressWarnings("squid:S2629")
	protected void validateParamPair(final String paramPair, final Set<String> validParamNames, final Errors errors)
	{
		final String[] paramValues = paramPair.split(COLON);
		if (paramValues.length != 2)
		{
			if (validParamNames.contains(paramValues[0]))
			{
				LOG.info(String.format("No param value found for param name '%s'. Assume param value to be empty.", paramValues[0]));
			}
			else
			{
				errors.rejectValue(PARAMS, CmsfacadesConstants.FIELD_FORMAT_INVALID);
			}
		}
		else if (!validParamNames.contains(paramValues[0]))
		{
			errors.rejectValue(PARAMS, CmsfacadesConstants.FIELD_NOT_ALLOWED, paramValues[0]);
		}
	}

	/**
	 * Check whether the sort direction provided matches a valid enum value from {@link SortDirection}.
	 *
	 * @param direction
	 * @return <code>true</code> if the sort direction is valid, <code>false</code> otherwise
	 */
	/*
	 * Suppress sonar warning (squid:S1166 | Exception handlers should preserve the original exceptions) : It is
	 * perfectly acceptable not to handle "e" here
	 */
	@SuppressWarnings("squid:S1166")
	protected boolean validSortDirection(final String direction)
	{
		boolean result = true;
		try
		{
			SortDirection.valueOf(direction);
		}
		catch (final IllegalArgumentException e)
		{
			result = false;
		}
		return result;
	}

	protected int getMaxPageSize()
	{
		return maxPageSize;
	}

	@Required
	public void setMaxPageSize(final int maxPageSize)
	{
		this.maxPageSize = maxPageSize;
	}

	protected Predicate<String> getNamedQueryExistsPredicate()
	{
		return namedQueryExistsPredicate;
	}

	@Required
	public void setNamedQueryExistsPredicate(final Predicate<String> namedQueryExistsPredicate)
	{
		this.namedQueryExistsPredicate = namedQueryExistsPredicate;
	}

}
