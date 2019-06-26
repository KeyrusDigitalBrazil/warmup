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

import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_REQUIRED;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_REQUIRED_L10N;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.LINK_MISSING_ITEMS;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.contents.components.CMSLinkComponentModel;
import de.hybris.platform.cms2.model.contents.components.CMSParagraphComponentModel;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrors;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrorsProvider;
import de.hybris.platform.cmsfacades.common.validator.impl.DefaultValidationErrors;
import de.hybris.platform.cmsfacades.languages.LanguageFacade;
import de.hybris.platform.cmsfacades.validator.data.ValidationError;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCMSParagraphComponentValidatorTest
{
    @InjectMocks
    private DefaultCMSParagraphComponentValidator validator;

    @Mock
    private LanguageFacade languageFacade;
    @Mock
    private CommonI18NService commonI18NService;
    @Mock
    private ValidationErrorsProvider validationErrorsProvider;

    private ValidationErrors validationErrors = new DefaultValidationErrors();

    @Before
    public void setup()
    {
        final LanguageData language = new LanguageData();
        language.setRequired(true);
        language.setIsocode(Locale.ENGLISH.toLanguageTag());
        when(languageFacade.getLanguages()).thenReturn(Arrays.asList(language));
        when(commonI18NService.getLocaleForIsoCode(Locale.ENGLISH.toLanguageTag())).thenReturn(Locale.ENGLISH);
        when(validationErrorsProvider.getCurrentValidationErrors()).thenReturn(validationErrors);
    }

    @Test
    public void testValidateWithoutRequiredAttributeAddErrors()
    {
        final CMSParagraphComponentModel itemModel = new CMSParagraphComponentModel();
        validator.validate(itemModel);

        final List<ValidationError> errors = validationErrorsProvider.getCurrentValidationErrors().getValidationErrors();

        assertEquals(1, errors.size());
        assertThat(errors.get(0).getField(), is(CMSParagraphComponentModel.CONTENT));
        assertThat(errors.get(0).getErrorCode(), is(FIELD_REQUIRED_L10N));
    }

    @Test
    public void testValidateWithContentModelAddNoError()
    {
        final CMSParagraphComponentModel itemModel = new CMSParagraphComponentModel();
        itemModel.setContent("test", Locale.ENGLISH);
        validator.validate(itemModel);

        final List<ValidationError> errors = validationErrorsProvider.getCurrentValidationErrors().getValidationErrors();

        assertTrue(errors.isEmpty());
    }
}
