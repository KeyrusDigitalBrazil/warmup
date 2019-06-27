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
import de.hybris.platform.cms2.model.navigation.CMSNavigationEntryModel;
import de.hybris.platform.cms2.model.navigation.CMSNavigationNodeModel;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrors;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrorsProvider;
import de.hybris.platform.cmsfacades.common.validator.impl.DefaultValidationErrors;
import de.hybris.platform.cmsfacades.validator.data.ValidationError;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.junit.Assert.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCMSNavigationNodeValidatorTest
{
    @InjectMocks
    private DefaultCMSNavigationNodeValidator validator;

    @Mock
    private ValidationErrorsProvider validationErrorsProvider;

    private ValidationErrors validationErrors = new DefaultValidationErrors();

    private final CMSNavigationNodeModel NODE_1 = new CMSNavigationNodeModel();
    private final CMSNavigationNodeModel NODE_2 = new CMSNavigationNodeModel();
    final CMSNavigationEntryModel ENTRY_1_ASSOCIATED_TO_NODE_1 = new CMSNavigationEntryModel();

    @Before
    public void setup()
    {
        when(validationErrorsProvider.getCurrentValidationErrors()).thenReturn(validationErrors);

        NODE_1.setName("NODE_1");
        NODE_1.setUid("NODE_1");

        NODE_2.setName("NODE_2");
        NODE_2.setUid("NODE_2");

        ENTRY_1_ASSOCIATED_TO_NODE_1.setName("ENTRY_1");
        ENTRY_1_ASSOCIATED_TO_NODE_1.setNavigationNode(NODE_1);

    }

    @Test
    public void testValidationWithCorrectInputs()
    {
        NODE_1.setEntries(Arrays.asList(ENTRY_1_ASSOCIATED_TO_NODE_1));

        validator.validate(NODE_1);

        final List<ValidationError> errors = validationErrorsProvider.getCurrentValidationErrors().getValidationErrors();
        assertEquals(0, errors.size());
    }

    @Test
    public void testValidationWithAnEntryAssociatedToAnotherNode()
    {
        final CMSNavigationEntryModel ENTRY_2_ASSOCIATED_TO_NODE_2 = new CMSNavigationEntryModel();
        ENTRY_2_ASSOCIATED_TO_NODE_2.setName("ENTRY_2");
        final CMSNavigationEntryModel ENTRY_3_ASSOCIATED_TO_NULL = new CMSNavigationEntryModel();
        ENTRY_3_ASSOCIATED_TO_NULL.setName("ENTRY_3");

        ENTRY_2_ASSOCIATED_TO_NODE_2.setNavigationNode(NODE_2);
        NODE_1.setEntries(Arrays.asList(ENTRY_1_ASSOCIATED_TO_NODE_1, ENTRY_2_ASSOCIATED_TO_NODE_2, ENTRY_3_ASSOCIATED_TO_NULL));

        validator.validate(NODE_1);

        final List<ValidationError> errors = validationErrorsProvider.getCurrentValidationErrors().getValidationErrors();

        assertEquals(1, errors.size());
        assertThat(errors.get(0).getField(), is("entries"));
        assertThat(errors.get(0).getErrorCode(), is("invalid.navigation.entries"));
        assertThat(errors.get(0).getErrorArgs()[0], is("ENTRY_2"));
    }

}
