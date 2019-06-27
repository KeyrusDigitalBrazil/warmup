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
import de.hybris.platform.cmsfacades.common.predicate.attributes.NestedOrPartOfAttributePredicate;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class NestedOrPartOfAttributePredicateTest
{

    @Mock
    private Predicate<AttributeDescriptorModel> nestedPredicate1;

    @Mock
    private Predicate<AttributeDescriptorModel> nestedPredicate2;

    @Mock
    private AttributeDescriptorModel attributeDescriptorModel;

    @InjectMocks
    private NestedOrPartOfAttributePredicate predicate;

    @Before
    public void setup()
    {
        Set<Predicate<AttributeDescriptorModel>> nestedPredicates = new HashSet<>();
        nestedPredicates.add(nestedPredicate1);
        nestedPredicates.add(nestedPredicate2);
        predicate.setNestedAttributePredicates(nestedPredicates);
    }

    @Test
    public void predicate_shouldPass_whenAttributeIsPartOf()
    {
        // Arrange
        doReturn(false).when(nestedPredicate1).test(attributeDescriptorModel);
        doReturn(false).when(nestedPredicate2).test(attributeDescriptorModel);
        doReturn(true).when(attributeDescriptorModel).getPartOf();

        // Act
        boolean result = predicate.test(attributeDescriptorModel);

        // Assert
        assertTrue(result);
    }

    @Test
    public void predicate_shouldPass_whenOneNestedPredicatePasses()
    {
        // Arrange
        doReturn(false).when(nestedPredicate1).test(attributeDescriptorModel);
        doReturn(true).when(nestedPredicate2).test(attributeDescriptorModel);
        doReturn(false).when(attributeDescriptorModel).getPartOf();

        // Act
        boolean result = predicate.test(attributeDescriptorModel);

        // Assert
        assertTrue(result);
    }

    @Test
    public void predicate_shouldPass_whenAllNestedPredicatePass()
    {
        // Arrange
        doReturn(true).when(nestedPredicate1).test(attributeDescriptorModel);
        doReturn(true).when(nestedPredicate2).test(attributeDescriptorModel);
        doReturn(false).when(attributeDescriptorModel).getPartOf();

        // Act
        boolean result = predicate.test(attributeDescriptorModel);

        // Assert
        assertTrue(result);
    }

    @Test
    public void predicate_shouldFail_whenAttributeIsNotPartOfOrNested()
    {
        // Arrange
        doReturn(false).when(nestedPredicate1).test(attributeDescriptorModel);
        doReturn(false).when(nestedPredicate2).test(attributeDescriptorModel);
        doReturn(false).when(attributeDescriptorModel).getPartOf();

        // Act
        boolean result = predicate.test(attributeDescriptorModel);

        // Assert
        assertFalse(result);
    }
}
