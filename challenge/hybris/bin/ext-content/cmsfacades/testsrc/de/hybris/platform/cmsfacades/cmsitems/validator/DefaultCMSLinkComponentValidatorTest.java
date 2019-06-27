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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.contents.components.CMSLinkComponentModel;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrors;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrorsProvider;
import de.hybris.platform.cmsfacades.common.validator.impl.DefaultValidationErrors;
import de.hybris.platform.cmsfacades.languages.LanguageFacade;
import de.hybris.platform.cmsfacades.validator.data.ValidationError;
import de.hybris.platform.core.model.product.ProductModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.INVALID_URL_FORMAT;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.LINK_MISSING_ITEMS;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCMSLinkComponentValidatorTest
{
    private static final String VALID_URL = "https://www.dummy-url.com";
    private static final String INVALID_URL = "www.dummy-url.com";

    @InjectMocks
    private DefaultCMSLinkComponentValidator validator;

    @Mock
    private LanguageFacade languageFacade;
    @Mock
    private ValidationErrorsProvider validationErrorsProvider;

    private ValidationErrors validationErrors = new DefaultValidationErrors();

    @Before
    public void setup()
    {
        when(validationErrorsProvider.getCurrentValidationErrors()).thenReturn(validationErrors);
        when(languageFacade.getLanguages()).thenReturn(Collections.emptyList());
    }

    @Test
    public void testValidateWithoutRequiredAttributeAddErrors()
    {
        final CMSLinkComponentModel itemModel = new CMSLinkComponentModel();
        validator.validate(itemModel);

        final List<ValidationError> errors = validationErrorsProvider.getCurrentValidationErrors().getValidationErrors();

        assertEquals(5, errors.size());
        assertThat(errors.get(0).getField(), is("linkTo"));
        assertThat(errors.get(0).getErrorCode(), is(LINK_MISSING_ITEMS));
    }

    @Test
    public void testValidateWithProductModelAddNoError()
    {
        final CMSLinkComponentModel itemModel = new CMSLinkComponentModel();
        itemModel.setProduct(new ProductModel());
        validator.validate(itemModel);

        final List<ValidationError> errors = validationErrorsProvider.getCurrentValidationErrors().getValidationErrors();

        assertTrue(errors.isEmpty());
    }

    @Test
    public void shouldValidateExternalLink()
    {
        final CMSLinkComponentModel model = new CMSLinkComponentModel();
        model.setUrl(VALID_URL);

        validator.validate(model);

        final List<ValidationError> errors = validationErrorsProvider.getCurrentValidationErrors().getValidationErrors();

        assertEquals(0, errors.size());
    }

    @Test
    public void shouldFailInvalidUrlFormat()
    {
        final CMSLinkComponentModel model = new CMSLinkComponentModel();
        model.setUrl(INVALID_URL);

        validator.validate(model);

        final List<ValidationError> errors = validationErrorsProvider.getCurrentValidationErrors().getValidationErrors();
        assertEquals(1, errors.size());
        assertThat(errors.get(0).getField(), is(CMSLinkComponentModel.URL));
        assertThat(errors.get(0).getErrorCode(), is(INVALID_URL_FORMAT));
    }
}
