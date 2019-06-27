/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.odata2services.odata.persistence;

import static de.hybris.platform.integrationservices.constants.IntegrationservicesConstants.INTEGRATION_KEY_PROPERTY_NAME;
import static de.hybris.platform.odata2services.filter.ExpressionVisitorParameters.parametersBuilder;
import static de.hybris.platform.odata2services.odata.persistence.ItemLookupRequest.itemLookupRequestBuilder;

import de.hybris.platform.integrationservices.search.WhereClauseConditions;
import de.hybris.platform.odata2services.config.ODataServicesConfiguration;
import de.hybris.platform.odata2services.filter.ExpressionVisitorFactory;
import de.hybris.platform.odata2services.filter.ExpressionVisitorParameters;
import de.hybris.platform.odata2services.filter.NoFilterResultException;
import de.hybris.platform.odata2services.odata.integrationkey.IntegrationKeyToODataEntryGenerator;
import de.hybris.platform.odata2services.odata.persistence.exception.InvalidIntegrationKeyException;
import de.hybris.platform.odata2services.odata.processor.ServiceNameExtractor;

import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.olingo.odata2.api.commons.HttpHeaders;
import org.apache.olingo.odata2.api.commons.InlineCount;
import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.edm.EdmEntityType;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.exception.ODataApplicationException;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.apache.olingo.odata2.api.uri.KeyPredicate;
import org.apache.olingo.odata2.api.uri.UriInfo;
import org.apache.olingo.odata2.api.uri.expression.ExceptionVisitExpression;
import org.apache.olingo.odata2.api.uri.expression.ExpressionVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

/**
 * Default implementation of the {@link ItemLookupRequestFactory}
 */
public class DefaultItemLookupRequestFactory implements ItemLookupRequestFactory
{
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultItemLookupRequestFactory.class);

	private IntegrationKeyToODataEntryGenerator integrationKeyToODataEntryGenerator;
	private ODataContextLanguageExtractor localeExtractor;
	private ServiceNameExtractor serviceNameExtractor;
	private ODataServicesConfiguration oDataServicesConfiguration;
	private ExpressionVisitorFactory expressionVisitorFactory;

	@Override
	public ItemLookupRequest create(final UriInfo uriInfo, final ODataContext context, final String contentType)
	{
		try
		{
			final ItemLookupRequest.ItemLookupRequestBuilder builder = hasKey(uriInfo) ?
					createWithIntegrationKey(uriInfo, context, contentType) :
					createBasic(uriInfo, context, contentType);
			return builder.build();
		}
		catch (final ODataException e)
		{
			LOGGER.warn("Failed to create a look up request");
			throw new InternalProcessingException(e);
		}
	}

	@Override
	public ItemLookupRequest create(final ODataContext context, final EdmEntitySet edmEntitySet, final ODataEntry oDataEntry, final String integrationKey) throws EdmException
	{
		final Locale locale = getLocaleExtractor().extractFrom(context, HttpHeaders.ACCEPT_LANGUAGE);
		return itemLookupRequestBuilder()
			.withAcceptLocale(locale)
			.withIntegrationObject(getServiceNameFromContext(context, null))
			.withEntitySet(edmEntitySet)
			.withODataEntry(oDataEntry)
			.withIntegrationKey(integrationKey)
			.build();
	}

	@Override
	public ItemLookupRequest create(final ODataContext context, final EdmEntitySet edmEntitySet, final Pair<String, String> attribute) throws EdmException
	{
		final Locale locale = getLocaleExtractor().extractFrom(context, HttpHeaders.ACCEPT_LANGUAGE);
		return itemLookupRequestBuilder()
				.withAcceptLocale(locale)
				.withIntegrationObject(getServiceNameFromContext(context, null))
				.withEntitySet(edmEntitySet)
				.withAttribute(attribute)
				.withTop(10)
				.build();
	}

	@Override
	public ItemLookupRequest createFrom(
			final ItemLookupRequest request,
			final EdmEntitySet entitySet,
			final ODataEntry oDataEntry) throws EdmException
	{
		return itemLookupRequestBuilder().from(request)
				.withEntitySet(entitySet)
				.withODataEntry(oDataEntry)
				.build();
	}

	private ItemLookupRequest.ItemLookupRequestBuilder createBasic(
			final UriInfo uriInfo,
			final ODataContext context,
			final String contentType) throws ODataException
	{
		final EdmEntitySet entitySet = uriInfo.getStartEntitySet();
		final Locale locale = getLocaleExtractor().extractFrom(context, HttpHeaders.ACCEPT_LANGUAGE);
		final ItemLookupRequest.ItemLookupRequestBuilder builder = itemLookupRequestBuilder()
				.withEntitySet(entitySet)
				.withAcceptLocale(locale)
				.withSkip(deriveSkip(uriInfo))
				.withTop(deriveTop(uriInfo.getTop()))
				.withCount(uriInfo.getInlineCount() == InlineCount.ALLPAGES || uriInfo.isCount())
				.withCountOnly(uriInfo.isCount())
				.withExpand(uriInfo.getExpand())
				.withNavigationSegments(uriInfo.getNavigationSegments())
				.withIntegrationObject(getServiceNameFromContext(context, null))
				.withServiceRoot(context.getPathInfo().getServiceRoot())
				.withContentType(contentType)
				.withRequestUri(context.getPathInfo().getRequestUri());
		return buildWithFilter(builder, uriInfo, context);
	}

	protected ItemLookupRequest.ItemLookupRequestBuilder buildWithFilter(final ItemLookupRequest.ItemLookupRequestBuilder builder,
			final UriInfo uriInfo, final ODataContext context)
			throws ExceptionVisitExpression, ODataApplicationException
	{
		if (uriInfo.getFilter() != null)
		{
			final ExpressionVisitor visitor = createExpressionVisitor(uriInfo, context);
			try
			{
				builder.withFilter((WhereClauseConditions) uriInfo.getFilter().accept(visitor));
			}
			catch (final NoFilterResultException e)
			{
				LOGGER.trace("No filter result found", e);
				builder.withHasNoFilterResult(true);
			}
		}
		return builder;
	}

	protected ExpressionVisitor createExpressionVisitor(final UriInfo uriInfo, final ODataContext context)
	{
		final ExpressionVisitorParameters parameters = parametersBuilder()
				.withODataContext(context)
				.withUriInfo(uriInfo)
				.build();

		return expressionVisitorFactory.create(parameters);
	}

	private ItemLookupRequest.ItemLookupRequestBuilder createWithIntegrationKey(
			final UriInfo uriInfo,
			final ODataContext context,
			final String contentType) throws ODataException
	{
		final ItemLookupRequest.ItemLookupRequestBuilder builder = createBasic(uriInfo, context, contentType);
		final EdmEntitySet entitySet = uriInfo.getStartEntitySet();
		final String integrationKey = getIntegrationKey(entitySet, uriInfo.getKeyPredicates());
		final ODataEntry calculatedODataEntry = getIntegrationKeyToODataEntryGenerator().generate(entitySet, integrationKey);
		return builder
				.withIntegrationKey(integrationKey)
				.withODataEntry(calculatedODataEntry)
				.withIntegrationObject(getServiceNameFromContext(context, integrationKey));
	}

	protected static boolean hasKey(final UriInfo uriInfo)
	{
		return !uriInfo.getKeyPredicates().isEmpty();
	}

	protected Integer deriveSkip(final UriInfo uriInfo)
	{
		final String skipToken = uriInfo.getSkipToken();
		final Integer skip = uriInfo.getSkip();

		if (skip != null)
		{
			if (skipToken == null)
			{
				return skip;
			}
			throw new InvalidQueryParameterException("$skip and $skiptoken query parameters cannot be combined in the same get request.");
		}
		else if (skipToken != null)
		{
			if (skipToken.matches("\\d+"))
			{
				return Integer.parseInt(skipToken);
			}
			throw new InvalidQueryParameterException("$skiptoken value must be an integer");
		}
		return 0;
	}

	protected Integer deriveTop(final Integer uriInfoTop)
	{
		if (uriInfoTop != null)
		{
			final int maxPageSize = getODataServicesConfiguration().getMaxPageSize();
			if (uriInfoTop > maxPageSize)
			{
				LOGGER.warn("Requested parameter value for $top exceeds the maximum value of {}.", maxPageSize);
				return maxPageSize;
			}
			return uriInfoTop;
		}
		return getODataServicesConfiguration().getDefaultPageSize();
	}

	protected String getServiceNameFromContext(final ODataContext context, final String integrationKey)
	{
		return getServiceNameExtractor().extract(context, integrationKey);
	}

	protected String getIntegrationKey(final EdmEntitySet edmEntitySet,
			final List<KeyPredicate> keyPredicates) throws EdmException
	{
		final EdmEntityType entityType = edmEntitySet.getEntityType();
		if (keyPredicates.size() == 1
				&& INTEGRATION_KEY_PROPERTY_NAME.equals(keyPredicates.get(0).getProperty().getName()))
		{
			return keyPredicates.get(0).getLiteral();
		}

		throw new InvalidIntegrationKeyException(entityType.getName());
	}

	protected ODataServicesConfiguration getODataServicesConfiguration()
	{
		return oDataServicesConfiguration;
	}

	protected IntegrationKeyToODataEntryGenerator getIntegrationKeyToODataEntryGenerator()
	{
		return integrationKeyToODataEntryGenerator;
	}

	@Required
	public void setIntegrationKeyToODataEntryGenerator(final IntegrationKeyToODataEntryGenerator integrationKeyToODataEntryGenerator)
	{
		this.integrationKeyToODataEntryGenerator = integrationKeyToODataEntryGenerator;
	}

	protected ODataContextLanguageExtractor getLocaleExtractor()
	{
		return localeExtractor;
	}

	@Required
	public void setLocaleExtractor(final ODataContextLanguageExtractor localeExtractor)
	{
		this.localeExtractor = localeExtractor;
	}

	protected ServiceNameExtractor getServiceNameExtractor()
	{
		return serviceNameExtractor;
	}

	@Required
	public void setServiceNameExtractor(final ServiceNameExtractor serviceNameExtractor)
	{
		this.serviceNameExtractor = serviceNameExtractor;
	}

	@Required
	public void setODataServicesConfiguration(final ODataServicesConfiguration oDataServicesConfiguration)
	{
		this.oDataServicesConfiguration = oDataServicesConfiguration;
	}

	protected ExpressionVisitorFactory getExpressionVisitorFactory()
	{
		return expressionVisitorFactory;
	}

	@Required
	public void setExpressionVisitorFactory(final ExpressionVisitorFactory expressionVisitorFactory)
	{
		this.expressionVisitorFactory = expressionVisitorFactory;
	}
}
