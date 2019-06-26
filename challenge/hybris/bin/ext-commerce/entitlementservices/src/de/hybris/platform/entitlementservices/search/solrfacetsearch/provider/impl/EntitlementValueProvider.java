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
package de.hybris.platform.entitlementservices.search.solrfacetsearch.provider.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.entitlementservices.model.ProductEntitlementModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import de.hybris.platform.solrfacetsearch.provider.FieldValueProvider;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractPropertyFieldValueProvider;
import de.hybris.platform.util.localization.Localization;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;


/**
 * This ValueProvider will provide the value for a {@link ProductModel}'s productEntitlement attribute
 */
public class EntitlementValueProvider extends AbstractPropertyFieldValueProvider implements FieldValueProvider, Serializable
{
	private static final Integer ENTITLEMENT_UNLIMITED_QUANTITY = -1;

	private CommonI18NService commonI18NService;
	private SessionService sessionService;
	private FieldNameProvider fieldNameProvider;
	private String entitlementId = "";

	@Override
	public Collection<FieldValue> getFieldValues(final IndexConfig indexConfig, final IndexedProperty indexedProperty,
			final Object model) throws FieldValueProviderException
	{
		validateParameterNotNullStandardMessage("model", model);

		final List<FieldValue> fieldValues = new ArrayList<>();

		if (!indexedProperty.isLocalized() || !(model instanceof ProductModel))
		{
			return fieldValues;
		}

		final Collection<LanguageModel> languages = indexConfig.getLanguages();
		for (final LanguageModel language : languages)
		{
			final List<Object> values = getSessionService().executeInLocalView(new SessionExecutionBody()
			{
				@Override
				public List<Object> execute()
				{
					getCommonI18NService().setCurrentLanguage(language);
					return getPropertyValue(model);
				}
			});

			if (values == null)
			{
				continue;
			}

			final Collection<String> fieldNames = getFieldNameProvider().getFieldNames(indexedProperty, language.getIsocode());
			for (final String fieldName : fieldNames)
			{
				fieldValues.addAll(values.stream().filter(entitlement -> entitlement != null)
						.map(entitlement -> new FieldValue(fieldName, entitlement.toString())).collect(Collectors.toList()));
			}
		}


		return fieldValues;
	}

	protected List<Object> getPropertyValue(final Object model)
	{
		final List<Object> propertyValues = new ArrayList<>();

		if (model instanceof ProductModel)
		{
			final Collection<ProductEntitlementModel> productEntitlements = ((ProductModel) model).getProductEntitlements();

			if (productEntitlements != null)
			{
				productEntitlements
						.stream()
						.filter(productEntitlement -> entitlementId.equals(productEntitlement.getEntitlement().getId()))
						.forEach(
								productEntitlement -> {
									final Integer quantity = productEntitlement.getQuantity();
									if (ENTITLEMENT_UNLIMITED_QUANTITY.equals(quantity))
									{
										propertyValues.add(Localization
												.getLocalizedString("entitlementservices.label.Entitlement.unlimited.name"));
									}
									else if (quantity != null)
									{
										propertyValues.add(quantity.toString());
									}
								});
			}
		}

		return propertyValues;
	}

	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	protected SessionService getSessionService()
	{
		return sessionService;
	}

	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	protected FieldNameProvider getFieldNameProvider()
	{
		return fieldNameProvider;
	}

	@Required
	public void setFieldNameProvider(final FieldNameProvider fieldNameProvider)
	{
		this.fieldNameProvider = fieldNameProvider;
	}

	public void setEntitlementId(final String entitlementId)
	{
		this.entitlementId = entitlementId;
	}
}
