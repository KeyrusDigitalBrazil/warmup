/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.commercefacades.product.converters.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeUnitModel;
import de.hybris.platform.classification.features.Feature;
import de.hybris.platform.classification.features.FeatureValue;
import de.hybris.platform.commercefacades.product.data.FeatureData;
import de.hybris.platform.converters.Populator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static org.mockito.BDDMockito.given;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class FeaturePopulatorTest
{
    private static final String FEATURE_CODE = "featCode";
    private static final String CLASS_ATTR_DESCRIPTION = "classAttrDesc";
    private static final String CLASS_UNIT_NAME = "unitName";

    @Mock
    private Feature source;

    private FeatureData target;

    @Mock
    private FeatureValue featureValue;

    @Mock
    private ClassAttributeAssignmentModel classAttributeAssignmentModel;

    @Mock
    private ClassificationAttributeUnitModel classificationAttributeUnitModel;

    private final Populator<Feature, FeatureData> featurePopulator = new FeaturePopulator();

    @Before
    public void setUp()
    {
        target = new FeatureData();

        given(source.getCode()).willReturn(FEATURE_CODE);
        given(source.getClassAttributeAssignment()).willReturn(classAttributeAssignmentModel);
        given(classAttributeAssignmentModel.getComparable()).willReturn(Boolean.TRUE);
        given(classAttributeAssignmentModel.getDescription()).willReturn(CLASS_ATTR_DESCRIPTION);
        given(classAttributeAssignmentModel.getUnit()).willReturn(classificationAttributeUnitModel);
        given(classificationAttributeUnitModel.getName()).willReturn(CLASS_UNIT_NAME);
    }

    @Test
    public void testConvert()
    {
        given(source.getValues()).willReturn(Collections.singletonList(featureValue));

        featurePopulator.populate(source, target);

        Assert.assertEquals(FEATURE_CODE, target.getCode());
        Assert.assertEquals(CLASS_ATTR_DESCRIPTION, target.getDescription());
        Assert.assertEquals(CLASS_UNIT_NAME, target.getFeatureUnit().getName());
        Assert.assertEquals(1, target.getFeatureValues().size());
    }

    @Test
    public void testConvertWithoutPrecision()
    {
        given(featureValue.getValue()).willReturn(new Double("1.000000"));
        given(source.getValues()).willReturn(Collections.singletonList(featureValue));

        featurePopulator.populate(source, target);

        Assert.assertEquals(FEATURE_CODE, target.getCode());
        Assert.assertEquals(CLASS_ATTR_DESCRIPTION, target.getDescription());
        Assert.assertEquals(CLASS_UNIT_NAME, target.getFeatureUnit().getName());
        Assert.assertEquals(1, target.getFeatureValues().size());
        Assert.assertEquals("1", target.getFeatureValues().iterator().next().getValue());
    }

    @Test
    public void testConvertWithPrecision()
    {
        given(featureValue.getValue()).willReturn(new Double("1.523"));
        given(source.getValues()).willReturn(Collections.singletonList(featureValue));

        featurePopulator.populate(source, target);

        Assert.assertEquals(FEATURE_CODE, target.getCode());
        Assert.assertEquals(CLASS_ATTR_DESCRIPTION, target.getDescription());
        Assert.assertEquals(CLASS_UNIT_NAME, target.getFeatureUnit().getName());
        Assert.assertEquals(1, target.getFeatureValues().size());
        Assert.assertEquals("1.523", target.getFeatureValues().iterator().next().getValue());
    }
}
