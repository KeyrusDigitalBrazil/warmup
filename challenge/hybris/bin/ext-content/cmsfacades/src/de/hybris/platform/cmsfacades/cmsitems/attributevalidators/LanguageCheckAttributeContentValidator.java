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
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_REQUIRED_L10N;

import de.hybris.platform.cmsfacades.languages.LanguageFacade;
import de.hybris.platform.cmsfacades.validator.data.ValidationError;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Required;

/**
 * Language attribute content validator adds validation errors when a localized attribute does not contain the required languages. 
 */
public class LanguageCheckAttributeContentValidator extends AbstractAttributeContentValidator<Map<String, Object>>
{
	private LanguageFacade languageFacade;
	
	@Override
	public List<ValidationError> validate(final Map<String, Object> value, final AttributeDescriptorModel attribute)
	{
		final List<ValidationError> errors = new ArrayList<>();
		
		getAllLanguages() //
				.filter(LanguageData::isRequired) //
				.forEach(languageData -> {
					if (value == null || value.get(languageData.getIsocode()) == null)
					{
						errors.add(
								newValidationErrorBuilder() //
										.field(attribute.getQualifier()) //
										.language(languageData.getIsocode())
										.errorCode(FIELD_REQUIRED_L10N) //
										.errorArgs(new Object[] {languageData.getIsocode()}) //
										.build()
						);	
					}
				});
		return errors;
	}


	protected Stream<LanguageData> getAllLanguages()
	{
		return getLanguageFacade().getLanguages().stream();
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
