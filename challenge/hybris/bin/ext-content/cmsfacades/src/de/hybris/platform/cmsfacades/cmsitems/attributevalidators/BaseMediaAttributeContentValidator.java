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
package de.hybris.platform.cmsfacades.cmsitems.attributevalidators;

import static de.hybris.platform.cmsfacades.common.validator.ValidationErrorBuilder.newValidationErrorBuilder;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.INVALID_MEDIA_CODE_L10N;

import de.hybris.platform.cmsfacades.cmsitems.AttributeContentValidator;
import de.hybris.platform.cmsfacades.languages.LanguageFacade;
import de.hybris.platform.cmsfacades.validator.data.ValidationError;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;

/**
 * Base Media attribute content validator adds validation errors when media formats are not present.  
 */
public class BaseMediaAttributeContentValidator implements AttributeContentValidator<Map<String, String>>
{
	
	private LanguageFacade languageFacade;
	
	@Override
	public List<ValidationError> validate(final Map<String, String> value, final AttributeDescriptorModel attribute)
	{
		final List<ValidationError> errors = new ArrayList<>();

		if (value == null)
		{
			// validate all required languages
			getLanguageFacade().getLanguages().stream() //
					.filter(LanguageData::isRequired) //
					.forEach(languageData -> validateMedia(attribute, languageData.getIsocode(), null, errors));
		}
		else
		{
			// validate media formats
			getLanguageFacade().getLanguages().stream().filter(LanguageData::isRequired)
					.forEach(language -> {
						final String mediaUuid = value.get(language.getIsocode());
						validateMedia(attribute, language.getIsocode(), mediaUuid, errors);
					});
		}
		return errors;
	}

	/**
	 * Validate media codes
	 * @param attribute the attribute of the type being validated
	 * @param language the language of the media uuid
	 * @param mediaUuid the media uuid key
	 * @param errors the local error collection
	 */
	protected void validateMedia(final AttributeDescriptorModel attribute, final String language,
			final String mediaUuid, final List<ValidationError> errors)
	{
		if (mediaUuid == null)
		{
			errors.add(
					newValidationErrorBuilder() //
							.field(attribute.getQualifier()) //
							.errorCode(INVALID_MEDIA_CODE_L10N) //
							.language(language) //
							.errorArgs(new Object[] {language}) //
							.rejectedValue(mediaUuid) //
							.build()
			);
		}
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
