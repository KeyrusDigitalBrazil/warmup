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
package de.hybris.platform.cmsfacades.cmsitems.converter;

import de.hybris.platform.cmsfacades.cmsitems.AttributeValueToRepresentationStrategy;
import de.hybris.platform.cmsfacades.cmsitems.CloneComponentContextProvider;
import de.hybris.platform.cmsfacades.cmsitems.attributeconverters.CollectionToRepresentationConverter;
import de.hybris.platform.cmsfacades.common.populator.LocalizedPopulator;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrorsProvider;
import de.hybris.platform.cmsfacades.exception.ValidationException;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.model.ModelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static de.hybris.platform.cmsfacades.common.validator.ValidationErrorBuilder.newValidationErrorBuilder;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.CMSITEMS_INVALID_CONVERSION_ERROR;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.SESSION_CLONE_COMPONENT_LOCALE;


/**
 * This class is used by the {@link DefaultCMSItemConverter} to convert items into a representation suited for admin
 * purposes, like CMS.
 */
public class DefaultAttributeValueToRepresentationStrategy implements AttributeValueToRepresentationStrategy
{

	// --------------------------------------------------------------------------
	// Variables
	// --------------------------------------------------------------------------
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultAttributeValueToRepresentationStrategy.class);

	private ModelService modelService;
	private LocalizedPopulator localizedPopulator;
	private ValidationErrorsProvider validationErrorsProvider;
	private CloneComponentContextProvider cloneComponentContextProvider;
	private CollectionToRepresentationConverter collectionToRepresentationConverter;

	// --------------------------------------------------------------------------
	// Public API
	// --------------------------------------------------------------------------
	@Override
	public Supplier<Object> getSimpleGetter(final AttributeDescriptorModel attribute, final ItemModel sourceModel,
			final Function<Object, Object> goDeeperOrSerialize)
	{
		return () -> goDeeperOrSerialize.apply(getModelService().getAttributeValue(sourceModel, attribute.getQualifier()));
	}

	@Override
	public Supplier<Object> getCollectionGetter(AttributeDescriptorModel attribute, ItemModel sourceModel,
			Function<Object, Object> goDeeperOrSerialize)
	{
		return () -> getCollectionToRepresentationConverter().convert(attribute,
				getModelService().getAttributeValue(sourceModel, attribute.getQualifier()), goDeeperOrSerialize);
	}

	@Override
	public Supplier<Object> getLocalizedGetter(AttributeDescriptorModel attribute, ItemModel sourceModel,
			Function<Object, Object> goDeeperOrSerialize)
	{
		return () ->
		{
			final Map<String, Object> mapValue = getLocalizedPopulator().populateAsMapOfLanguages(
					locale -> getModelService().getAttributeValue(sourceModel, attribute.getQualifier(), locale)
			);

			return convertLocalizedValue(attribute, mapValue, goDeeperOrSerialize);
		};
	}

	@Override
	public Supplier<Object> getLocalizedCollectionGetter(AttributeDescriptorModel attribute, ItemModel sourceModel,
			Function<Object, Object> goDeeperOrSerialize)
	{
		return () ->
		{
			final Map<String, Object> mapValue = getLocalizedPopulator().populateAsMapOfLanguages(
					locale -> getModelService().getAttributeValue(sourceModel, attribute.getQualifier(), locale)
			);

			return convertLocalizedValue(attribute, mapValue,
					e -> getCollectionToRepresentationConverter().convert(attribute, (Collection<Object>) e, goDeeperOrSerialize));
		};
	}

	// --------------------------------------------------------------------------
	// Helper Methods
	// --------------------------------------------------------------------------
	protected Map<String, Object> convertLocalizedValue(final AttributeDescriptorModel attribute, final Map<String, Object> languageValueMap,
			final Function<Object, Object> transform)
	{
		final BiFunction<String, Object, Object> transformValue = (language, value) ->
		{
			try
			{
				return transform.apply(value);
			}
			catch (final ValidationException e)
			{
				getValidationErrorsProvider().collectValidationErrors(e, Optional.of(language), Optional.empty());
			}
			catch (final ConversionException e)
			{
				LOGGER.error("Error converting attribute for [" + attribute.getQualifier() + "] and language [" + language + "] with value [" + value + "]", e);
				getValidationErrorsProvider().getCurrentValidationErrors().add(
						newValidationErrorBuilder() //
								.field(attribute.getQualifier()) //
								.language(language) //
								.rejectedValue(value) //
								.errorCode(CMSITEMS_INVALID_CONVERSION_ERROR) //
								.exceptionMessage(e.getMessage()) //
								.build()
				);
			}

			return null;
		};

		if( languageValueMap != null )
		{
			final Map<String, Object> responseMap = new HashMap<>();
			languageValueMap.forEach((language, value) -> {
				if (getCloneComponentContextProvider().isInitialized())
				{
					getCloneComponentContextProvider()
							.initializeItem(new AbstractMap.SimpleImmutableEntry<>(SESSION_CLONE_COMPONENT_LOCALE, language));
				}

				responseMap.put(language, transformValue.apply(language, value));
			});

			return responseMap;
		}

		return null;
	}

	// --------------------------------------------------------------------------
	// Getters/Setters
	// --------------------------------------------------------------------------
	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected CollectionToRepresentationConverter getCollectionToRepresentationConverter()
	{
		return collectionToRepresentationConverter;
	}

	@Required
	public void setCollectionToRepresentationConverter(CollectionToRepresentationConverter collectionToRepresentationConverter)
	{
		this.collectionToRepresentationConverter = collectionToRepresentationConverter;
	}

	protected ValidationErrorsProvider getValidationErrorsProvider()
	{
		return validationErrorsProvider;
	}

	@Required
	public void setValidationErrorsProvider(ValidationErrorsProvider validationErrorsProvider)
	{
		this.validationErrorsProvider = validationErrorsProvider;
	}

	protected CloneComponentContextProvider getCloneComponentContextProvider()
	{
		return cloneComponentContextProvider;
	}

	@Required
	public void setCloneComponentContextProvider(
			CloneComponentContextProvider cloneComponentContextProvider)
	{
		this.cloneComponentContextProvider = cloneComponentContextProvider;
	}

	protected LocalizedPopulator getLocalizedPopulator()
	{
		return localizedPopulator;
	}

	@Required
	public void setLocalizedPopulator(LocalizedPopulator localizedPopulator)
	{
		this.localizedPopulator = localizedPopulator;
	}
}
