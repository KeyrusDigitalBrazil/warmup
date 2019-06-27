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

import static de.hybris.platform.cms2.model.contents.components.CMSParagraphComponentModel.CONTENT;
import static de.hybris.platform.cmsfacades.common.validator.ValidationErrorBuilder.newValidationErrorBuilder;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_REQUIRED_L10N;

import de.hybris.platform.cms2.model.contents.components.CMSParagraphComponentModel;
import de.hybris.platform.cmsfacades.common.function.Validator;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrorsProvider;
import de.hybris.platform.cmsfacades.languages.LanguageFacade;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;

/**
 * Default implementation of the validator for {@link CMSParagraphComponentModel}
 */
public class DefaultCMSParagraphComponentValidator implements Validator<CMSParagraphComponentModel>
{
    private ValidationErrorsProvider validationErrorsProvider;
    private LanguageFacade languageFacade;
    private CommonI18NService commonI18NService;

    @Override
    public void validate(final CMSParagraphComponentModel validatee)
    {
        getLanguageFacade().getLanguages().stream() //
                .filter(LanguageData::isRequired) //
                .forEach(languageData -> {
                    if (StringUtils.isBlank(validatee.getContent(getCommonI18NService().getLocaleForIsoCode(languageData.getIsocode()))))
                    {
                        getValidationErrorsProvider().getCurrentValidationErrors().add(
                                newValidationErrorBuilder() //
                                        .field(CONTENT) //
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
