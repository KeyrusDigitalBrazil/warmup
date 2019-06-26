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
package de.hybris.platform.subscriptionservices.search.solrfacetsearch.provider.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import de.hybris.platform.solrfacetsearch.provider.FieldValueProvider;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;


/**
 * This ValueProvider will provide the value for a subscription {@link ProductModel}'s billing frequency attribute.
 */
public class ProductBillingTimeValueProvider extends SubscriptionAwareFieldValueProvider implements FieldValueProvider,
		Serializable
{
	private CommonI18NService commonI18NService;
	private SessionService sessionService;
	private FieldNameProvider fieldNameProvider;

	@Override
	public Collection<FieldValue> getFieldValues(final IndexConfig indexConfig, final IndexedProperty indexedProperty,
			final Object model) throws FieldValueProviderException
	{
		validateParameterNotNullStandardMessage("model", model);
		validateParameterNotNullStandardMessage("indexedProperty", indexedProperty);

		final List<FieldValue> fieldValues = new ArrayList<>();

		if (indexedProperty.isLocalized())
		{
			final Collection<LanguageModel> languages = indexConfig.getLanguages();
			for (final LanguageModel language : languages)
			{
				final Object value = getSessionService().executeInLocalView(new SessionExecutionBody()
				{
					@Override
					public Object execute()
					{
						getCommonI18NService().setCurrentLanguage(language);
						return getPropertyValue(model);
					}
				});

				if (value != null)
				{
					final Collection<String> fieldNames = getFieldNameProvider().getFieldNames(indexedProperty, language.getIsocode());
					fieldValues.addAll(fieldNames.stream().map(fieldName -> new FieldValue(fieldName, value)).collect(Collectors.toList()));
				}
			}
		}

		return fieldValues;
	}

	protected Object getPropertyValue(final Object model)
	{
		if (model instanceof ProductModel)
		{
			final ProductModel planProduct = (ProductModel) model;

			if (getSubscriptionProductService().isSubscription(planProduct)
				&& planProduct.getSubscriptionTerm() != null
				&& planProduct.getSubscriptionTerm().getBillingPlan() != null
				&& planProduct.getSubscriptionTerm().getBillingPlan().getBillingFrequency() != null)
			{
				return planProduct.getSubscriptionTerm().getBillingPlan().getBillingFrequency().getNameInCart();
			}
		}
		return null;
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

}
