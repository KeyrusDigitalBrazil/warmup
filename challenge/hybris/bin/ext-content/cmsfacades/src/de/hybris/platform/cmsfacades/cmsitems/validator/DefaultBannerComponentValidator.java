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
package de.hybris.platform.cmsfacades.cmsitems.validator;

import static de.hybris.platform.cmsfacades.common.validator.ValidationErrorBuilder.newValidationErrorBuilder;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_REQUIRED_L10N;
import static java.util.Objects.isNull;

import de.hybris.platform.cms2lib.model.components.BannerComponentModel;
import de.hybris.platform.cmsfacades.common.function.Validator;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrorsProvider;
import de.hybris.platform.cmsfacades.languages.LanguageFacade;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.util.function.Function;

import org.springframework.beans.factory.annotation.Required;

/**
 * Default implementation of the validator for {@link BannerComponentModel}
 */
public class DefaultBannerComponentValidator implements Validator<BannerComponentModel>
{
	
	private ValidationErrorsProvider validationErrorsProvider;
	private LanguageFacade languageFacade;
	private CommonI18NService commonI18NService;
	
	@Override
	public void validate(final BannerComponentModel validatee)
	{
		validateField((languageData) -> validatee.getContent(getCommonI18NService().getLocaleForIsoCode(languageData.getIsocode())),
				BannerComponentModel.CONTENT);
		
		validateField((languageData) -> validatee.getHeadline(getCommonI18NService().getLocaleForIsoCode(languageData.getIsocode())),
				BannerComponentModel.HEADLINE);

		validateField((languageData) -> validatee.getMedia(getCommonI18NService().getLocaleForIsoCode(languageData.getIsocode())),
				BannerComponentModel.MEDIA);
	}

	/**
	 * Validates a field using the getter function
	 * @param value the getter function by language Data
	 * @param field the field being validated
	 */
	protected void validateField(final Function<LanguageData, Object> value, final String field)
	{
		getLanguageFacade().getLanguages().stream() //
			.filter(LanguageData::isRequired) //
			.forEach(languageData -> {
				if (isNull(value.apply(languageData)))
				{
					getValidationErrorsProvider().getCurrentValidationErrors().add(
							newValidationErrorBuilder() //
									.field(field) //
									.language(languageData.getIsocode())
									.errorCode(FIELD_REQUIRED_L10N) //
									.errorArgs(new Object[] {languageData.getIsocode()}) //
									.build()
					);
				}
			});
	}

	protected ValidationErrorsProvider getValidationErrorsProvider()
	{
		return validationErrorsProvider;
	}

	@Required
	public void setValidationErrorsProvider(final ValidationErrorsProvider validationErrorsProvider)
	{
		this.validationErrorsProvider = validationErrorsProvider;
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
