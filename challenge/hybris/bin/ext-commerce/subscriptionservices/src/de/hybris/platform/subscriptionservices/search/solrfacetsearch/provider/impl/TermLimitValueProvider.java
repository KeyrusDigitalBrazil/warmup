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
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import de.hybris.platform.solrfacetsearch.provider.FieldValueProvider;
import de.hybris.platform.subscriptionservices.enums.TermOfServiceFrequency;
import de.hybris.platform.subscriptionservices.model.SubscriptionTermModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.springframework.beans.factory.annotation.Required;


/**
 * This ValueProvider will provide the value for a subscription {@link ProductModel}'s service term limit which is the
 * combination of its TermOfServiceNumber and TermOfServiceFrequency, e.g. "12 Month(s)"
 */
public class TermLimitValueProvider extends SubscriptionAwareFieldValueProvider implements FieldValueProvider, Serializable
{
	private CommonI18NService commonI18NService;
	private SessionService sessionService;
	private FieldNameProvider fieldNameProvider;
	private TypeService typeService;

	@Override
	public Collection<FieldValue> getFieldValues(final IndexConfig indexConfig,@Nonnull final IndexedProperty indexedProperty,
			@Nonnull final Object model) throws FieldValueProviderException
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
		if (!(model instanceof ProductModel))
		{
			return null;
		}
			
		final SubscriptionTermModel subscriptionTerm = ((ProductModel) model).getSubscriptionTerm();
		if (subscriptionTerm == null)
		{
			return null;
		}
				
		final TermOfServiceFrequency termOfServiceFrequency = subscriptionTerm.getTermOfServiceFrequency();
		final Integer termOfServiceNumber = subscriptionTerm.getTermOfServiceNumber();
		if (termOfServiceFrequency == null)
		{
				return null;
		}
				
		String locName = getTypeService().getEnumerationValue(TermOfServiceFrequency._TYPECODE,
					termOfServiceFrequency.getCode()).getName();
		if (locName == null)
		{
			// no localization for current language -> take code
			locName = termOfServiceFrequency.getCode();
		}
		return (termOfServiceNumber == null ? "" : termOfServiceNumber.toString() + " ") + locName;

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

	@Required
	public void setTypeService(final TypeService typeService)
	{
		this.typeService = typeService;
	}

	protected TypeService getTypeService()
	{
		return typeService;
	}

}
