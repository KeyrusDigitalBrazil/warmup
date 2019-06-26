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
package de.hybris.platform.integrationservices.populator;

import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;

public class LocalizedAttribute2MapPopulator<S extends ItemToMapConversionContext, T extends Map<String, Object>>
		extends AbstractItem2MapPopulator<S, T>
{
	private CommonI18NService commonI18NService;
	private static final String LOCALIZED_ATTRIBUTES = "localizedAttributes";
	private static final String LANGUAGE = "language";
	

	@Override
	protected void populateToMap(final IntegrationObjectItemAttributeModel attr, final String qualifier, final S source, final T target)
	{
		final Locale[] supportedLocales = getSupportedLocales();
		final Map<Locale, String> attrLocalizations = getModelService().getAttributeValues(source.getItemModel(), qualifier, supportedLocales);

		final Object localizedAttributes = target.get(LOCALIZED_ATTRIBUTES);
		if(localizedAttributes instanceof List)
		{
			final List<Map<String, String>> existingLocalizedAttributes = (List<Map<String, String>>) localizedAttributes;
			attrLocalizations
					.entrySet()
					.forEach(
							localeAttrValueEntry ->
									addAttributeToExistingLocaleEntry(existingLocalizedAttributes, localeAttrValueEntry, qualifier)
					);
		}
		else
		{
			final List<Map<String, String>> itemTranslations = createLocalizedAttributes(qualifier, attrLocalizations);
			target.put(LOCALIZED_ATTRIBUTES, itemTranslations);
		}
	}

	private Locale[] getSupportedLocales()
	{
		return getCommonI18NService().getAllLanguages().stream().map(getCommonI18NService()::getLocaleForLanguage).toArray(Locale[]::new);
	}

	private List<Map<String, String>> createLocalizedAttributes(final String qualifier, final Map<Locale, String> attrTranslations)
	{
		return attrTranslations.entrySet().stream().map(localeAttrValueEntry -> createLocaleEntryWithAttribute(localeAttrValueEntry, qualifier)).collect(Collectors.toList());
	}

	private Map<String, String> createLocaleEntryWithAttribute(final Map.Entry<Locale, String> localeAttrValueEntry, final String qualifier)
	{
		final Map<String, String> translation = new HashMap<>();
		translation.put(LANGUAGE, localeAttrValueEntry.getKey().getLanguage());
		translation.put(qualifier, localeAttrValueEntry.getValue());
		return translation;
	}

	private void addAttributeToExistingLocaleEntry(final List<Map<String, String>> existingLocalizedAttributes, final Map.Entry<Locale, String> localeAttrValueEntry, final String qualifier)
	{
		final String language = localeAttrValueEntry.getKey().getLanguage();
		final String value = localeAttrValueEntry.getValue();
		final Optional<Map<String, String>> existingLocalizationMapOptional = existingLocalizedAttributes.stream()
				.filter(localizedEntry -> localizedEntry.get(LANGUAGE).equals(language))
				.findFirst();

		if(existingLocalizationMapOptional.isPresent())
		{
			existingLocalizationMapOptional.get().put(qualifier, value);
		}
		else
		{
			existingLocalizedAttributes.add(createLocaleEntryWithAttribute(localeAttrValueEntry, qualifier));
		}
	}

	@Override
	protected boolean isApplicable(final AttributeDescriptorModel attributeDescriptor)
	{
		return Boolean.TRUE.equals(attributeDescriptor.getLocalized());
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
}
