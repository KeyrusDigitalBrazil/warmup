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
package de.hybris.platform.acceleratorfacades.cmsitems.attributevalidators;

import static de.hybris.platform.cmsfacades.common.validator.ValidationErrorBuilder.newValidationErrorBuilder;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_MEDIA_FORMAT_REQUIRED_L10N;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.INVALID_MEDIA_FORMAT_MEDIA_CODE_L10N;
import static org.springframework.util.StringUtils.isEmpty;

import de.hybris.platform.cmsfacades.cmsitems.attributevalidators.AbstractAttributeContentValidator;
import de.hybris.platform.cmsfacades.languages.LanguageFacade;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.cmsfacades.validator.data.ValidationError;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Required;


/**
 * Media Container attribute content validator adds validation errors when value Map fails to meet format and media,
 * required languages and media content.
 */
public class MediaContainerAttributeContentValidator extends AbstractAttributeContentValidator<Map<String, Map<String, String>>>
{

	private UniqueItemIdentifierService uniqueItemIdentifierService;

	private List<String> cmsRequiredMediaFormatQualifiers;

	private LanguageFacade languageFacade;

	@Override
	public List<ValidationError> validate(final Map<String, Map<String, String>> value, final AttributeDescriptorModel attribute)
	{
		final List<ValidationError> errors = new ArrayList<>();

		if (value == null)
		{
			// validate all required languages
			getLanguageFacade().getLanguages().stream() //
					.filter(LanguageData::isRequired) //
					.forEach(languageData -> validateMediaFormat(attribute, languageData.getIsocode(), null, errors));
		}
		else
		{
			// validate media formats
			getLanguageFacade().getLanguages().stream().filter(LanguageData::isRequired)
					.forEach(language ->
					{
						final Map<String, String> formatUuidMap = value.get(language.getIsocode());
						validateMediaFormat(attribute, language.getIsocode(), formatUuidMap, errors);
					});
			// validate media codes
			value.entrySet().stream()
					.forEach(entry ->
					{
						final String language = entry.getKey();
						final Map<String, String> formatUuidMap = entry.getValue();

						validateAllMediaCodes(attribute, language, formatUuidMap, errors);
					});
		}
		return errors;
	}

	/**
	 * Validates all media codes from the media container
	 * 
	 * @param attribute
	 *           the mediaContainer attribute
	 * @param language
	 *           the String Locale of this MediaContainer.
	 * @param formatUuidMap
	 *           the mediaFormatUuidMap containing the media container data
	 * @param errors
	 *           the list of errors
	 */
	protected void validateAllMediaCodes(final AttributeDescriptorModel attribute, final String language,
			final Map<String, String> formatUuidMap, final List<ValidationError> errors)
	{
		if (formatUuidMap == null)
		{
			return;
		}
		formatUuidMap.entrySet() //
				.stream() //
				.forEach(entry ->
				{
					final String format = entry.getKey();
					final String mediaCode = entry.getValue();
					final Optional<MediaModel> mediaModel = getUniqueItemIdentifierService().getItemModel(mediaCode, MediaModel.class);
					if (!mediaModel.isPresent())
					{
						errors.add(
								newValidationErrorBuilder() //
										.field(attribute.getQualifier()) //
										.errorCode(INVALID_MEDIA_FORMAT_MEDIA_CODE_L10N) //
										.language(language) //
										.errorArgs(new Object[]
						{ language, format }) //
										.rejectedValue(formatUuidMap) //
										.build());
					}
				});
	}

	/**
	 * Validate required media formats
	 * 
	 * @param attribute
	 *           the mediaContainer attribute
	 * @param language
	 *           the String Locale of this MediaContainer.
	 * @param formatUuidMap
	 *           the mediaFormatUuidMap containing the media container data
	 * @param errors
	 *           the list of errors
	 */
	protected void validateMediaFormat(final AttributeDescriptorModel attribute, final String language,
			final Map<String, String> formatUuidMap, final List<ValidationError> errors)
	{
		//
		getCmsRequiredMediaFormatQualifiers().forEach(format ->
		{
			if (formatUuidMap == null || isEmpty(formatUuidMap.get(format)))
			{
				errors.add(
						newValidationErrorBuilder() //
								.field(attribute.getQualifier()) //
								.errorCode(FIELD_MEDIA_FORMAT_REQUIRED_L10N) //
								.language(language) //
								.errorArgs(new Object[]
				{ language, format }) //
								.rejectedValue(formatUuidMap) //
								.build());
			}
		});
	}

	protected UniqueItemIdentifierService getUniqueItemIdentifierService()
	{
		return uniqueItemIdentifierService;
	}

	@Required
	public void setUniqueItemIdentifierService(final UniqueItemIdentifierService uniqueItemIdentifierService)
	{
		this.uniqueItemIdentifierService = uniqueItemIdentifierService;
	}

	protected List<String> getCmsRequiredMediaFormatQualifiers()
	{
		return cmsRequiredMediaFormatQualifiers;
	}

	@Required
	public void setCmsRequiredMediaFormatQualifiers(final List<String> cmsRequiredMediaFormatQualifiers)
	{
		this.cmsRequiredMediaFormatQualifiers = cmsRequiredMediaFormatQualifiers;
	}

	protected LanguageFacade getLanguageFacade()
	{
		return languageFacade;
	}

	@Required
	public void setLanguageFacade(final LanguageFacade languageFacade)
	{
		this.languageFacade = languageFacade;
	}
}
