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

import de.hybris.platform.cms2.model.contents.components.CMSLinkComponentModel;
import de.hybris.platform.cmsfacades.common.function.Validator;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrorsProvider;
import de.hybris.platform.cmsfacades.constants.CmsfacadesConstants;
import de.hybris.platform.cmsfacades.languages.LanguageFacade;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Required;

import java.util.Objects;
import java.util.stream.Stream;

import static de.hybris.platform.cmsfacades.common.validator.ValidationErrorBuilder.newValidationErrorBuilder;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_REQUIRED_L10N;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.INVALID_URL_FORMAT;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Default implementation of the validator for {@link CMSLinkComponentModel}
 */
public class DefaultCMSLinkComponentValidator implements Validator<CMSLinkComponentModel>
{
    private static final String LINK_TO = "linkTo";

    private ValidationErrorsProvider validationErrorsProvider;
    private LanguageFacade languageFacade;
    private CommonI18NService commonI18NService;

    @Override
    public void validate(final CMSLinkComponentModel validatee)
    {
        verifyOnlyOneTypeProvided(validatee);

        getLanguageFacade().getLanguages().stream() //
                .filter(LanguageData::isRequired) //
                .forEach(languageData -> {
                    if (isBlank(validatee.getLinkName(getCommonI18NService().getLocaleForIsoCode(languageData.getIsocode()))))
                    {
                        getValidationErrorsProvider().getCurrentValidationErrors().add(
                                newValidationErrorBuilder() //
                                        .field(CMSLinkComponentModel.LINKNAME) //
                                        .language(languageData.getIsocode())
                                        .errorCode(FIELD_REQUIRED_L10N) //
                                        .errorArgs(new Object[] {languageData.getIsocode()}) //
                                        .build()
                        );
                    }
                });


        if (Objects.nonNull(validatee.getUrl()) && !UrlValidator.getInstance().isValid(validatee.getUrl()))
        {
            getValidationErrorsProvider().getCurrentValidationErrors().add(
                    newValidationErrorBuilder() //
                            .field(CMSLinkComponentModel.URL) //
                            .errorCode(INVALID_URL_FORMAT) //
                            .build()
            );
        }
    }

    /**
     * Verifies that one of the following is specified: category, content page, product or url
     *
     * @param target - the link component dto
     */
    protected void verifyOnlyOneTypeProvided(final CMSLinkComponentModel target)
    {
        final long count = Stream
                .of(target.getCategory(), target.getContentPage(), target.getProduct(), target.getUrl())
                .filter(item -> !isNull(item)).count();
        if (count > 1)
        {
            getValidationErrorsProvider().getCurrentValidationErrors().add(
                    newValidationErrorBuilder() //
                            .field(CMSLinkComponentModel.CONTENTPAGE)
                            .errorCode(CmsfacadesConstants.LINK_ITEMS_EXCEEDED) //
                            .build()
            );
            getValidationErrorsProvider().getCurrentValidationErrors().add(
                    newValidationErrorBuilder() //
                            .field(CMSLinkComponentModel.PRODUCT)
                            .errorCode(CmsfacadesConstants.LINK_ITEMS_EXCEEDED) //
                            .build()
            );
            getValidationErrorsProvider().getCurrentValidationErrors().add(
                    newValidationErrorBuilder() //
                            .field(CMSLinkComponentModel.CATEGORY)
                            .errorCode(CmsfacadesConstants.LINK_ITEMS_EXCEEDED) //
                            .build()
            );
            getValidationErrorsProvider().getCurrentValidationErrors().add(
                    newValidationErrorBuilder() //
                            .field(CMSLinkComponentModel.URL)
                            .errorCode(CmsfacadesConstants.LINK_ITEMS_EXCEEDED) //
                            .build()
            );
        }
        else if (count < 1)
        {
            getValidationErrorsProvider().getCurrentValidationErrors().add(
                    newValidationErrorBuilder() //
                            .field(LINK_TO)
                            .errorCode(CmsfacadesConstants.LINK_MISSING_ITEMS) //
                            .build()
            );
            getValidationErrorsProvider().getCurrentValidationErrors().add(
                    newValidationErrorBuilder() //
                            .field(CMSLinkComponentModel.CONTENTPAGE)
                            .errorCode(CmsfacadesConstants.LINK_MISSING_ITEMS) //
                            .build()
            );
            getValidationErrorsProvider().getCurrentValidationErrors().add(
                    newValidationErrorBuilder() //
                            .field(CMSLinkComponentModel.PRODUCT)
                            .errorCode(CmsfacadesConstants.LINK_MISSING_ITEMS) //
                            .build()
            );
            getValidationErrorsProvider().getCurrentValidationErrors().add(
                    newValidationErrorBuilder() //
                            .field(CMSLinkComponentModel.CATEGORY)
                            .errorCode(CmsfacadesConstants.LINK_MISSING_ITEMS) //
                            .build()
            );
            getValidationErrorsProvider().getCurrentValidationErrors().add(
                    newValidationErrorBuilder() //
                            .field(CMSLinkComponentModel.URL)
                            .errorCode(CmsfacadesConstants.LINK_MISSING_ITEMS) //
                            .build()
            );
        }
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
