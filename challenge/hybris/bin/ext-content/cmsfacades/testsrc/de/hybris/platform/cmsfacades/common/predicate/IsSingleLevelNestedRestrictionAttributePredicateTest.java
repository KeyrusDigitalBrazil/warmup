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
package de.hybris.platform.cmsfacades.common.predicate;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.RestrictionTypeModel;
import de.hybris.platform.cms2.model.restrictions.AbstractRestrictionModel;
import de.hybris.platform.cms2.servicelayer.services.AttributeDescriptorModelHelperService;
import de.hybris.platform.cmsfacades.common.predicate.attributes.IsSingleLevelNestedRestrictionAttributePredicate;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class IsSingleLevelNestedRestrictionAttributePredicateTest
{
    private final Class OTHER_CLASS = String.class;

    @Mock
    private AttributeDescriptorModelHelperService attributeDescriptorModelHelperService;

    @Mock
    private AttributeDescriptorModel attributeDescriptorModel;

    private RestrictionTypeModel restrictionTypeModel;
    private ComposedTypeModel composedTypeModel;

    @InjectMocks
    private IsSingleLevelNestedRestrictionAttributePredicate predicate;

    @Before
    public void setup()
    {
        restrictionTypeModel = new RestrictionTypeModel();
        composedTypeModel = new ComposedTypeModel();
    }

    @Test
    public void predicate_shouldFail_whenAttributeDoesNotContainRestrictions()
    {
        // Arrange
        doReturn(OTHER_CLASS).when(attributeDescriptorModelHelperService).getAttributeClass(attributeDescriptorModel);
        doReturn(composedTypeModel).when(attributeDescriptorModel).getEnclosingType();

        // Act
        boolean result = predicate.test(attributeDescriptorModel);

        // Assert
        assertFalse(result);
    }

    @Test
    public void predicate_shouldFail_whenAttributeContainsRestrictionsAndIsNestedInOtherRestriction()
    {
        // Arrange
        doReturn(AbstractRestrictionModel.class).when(attributeDescriptorModelHelperService).getAttributeClass(attributeDescriptorModel);
        doReturn(restrictionTypeModel).when(attributeDescriptorModel).getEnclosingType();

        // Act
        boolean result = predicate.test(attributeDescriptorModel);

        // Assert
        assertFalse(result);
    }

    @Test
    public void predicate_shouldFail_whenAttributeDoesNotContainRestrictionsAndIsNestedInOtherRestriction()
    {
        // Arrange
        doReturn(OTHER_CLASS).when(attributeDescriptorModelHelperService).getAttributeClass(attributeDescriptorModel);
        doReturn(restrictionTypeModel).when(attributeDescriptorModel).getEnclosingType();

        // Act
        boolean result = predicate.test(attributeDescriptorModel);

        // Assert
        assertFalse(result);
    }

    @Test
    public void predicate_shouldPass_whenAttributeContainsRestrictionsAndIsNotPartOfOtherRestriction()
    {
        // Arrange
        doReturn(AbstractRestrictionModel.class).when(attributeDescriptorModelHelperService).getAttributeClass(attributeDescriptorModel);
        doReturn(composedTypeModel).when(attributeDescriptorModel).getEnclosingType();

        // Act
        boolean result = predicate.test(attributeDescriptorModel);

        // Assert
        assertTrue(result);
    }

}
