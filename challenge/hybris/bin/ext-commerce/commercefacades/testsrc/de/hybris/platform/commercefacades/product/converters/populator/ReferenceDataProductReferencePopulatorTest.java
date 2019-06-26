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
import de.hybris.platform.catalog.enums.ProductReferenceTypeEnum;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.ProductReferenceData;
import de.hybris.platform.commerceservices.product.data.ReferenceData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.BDDMockito.given;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ReferenceDataProductReferencePopulatorTest
{

    public static final String PRODUCT_DESCRIPTION = "product description";

    @Mock
    private Converter<ProductModel, ProductData> productConverter;

    @InjectMocks
    private Populator<ReferenceData<ProductReferenceTypeEnum, ProductModel>, ProductReferenceData> populator = new ReferenceDataProductReferencePopulator();

    @Mock
    private ReferenceData<ProductReferenceTypeEnum, ProductModel> source;

    @Mock
    private ProductModel productModel;

    @Mock
    private ProductData productData;

    private ProductReferenceData productReferenceData;

    @Before
    public void setUp() throws Exception
    {
        productReferenceData = new ProductReferenceData();
        given(source.getTarget()).willReturn(productModel);
        given(source.getDescription()).willReturn(PRODUCT_DESCRIPTION);
        given(source.getQuantity()).willReturn(Integer.MAX_VALUE);
        given(source.getReferenceType()).willReturn(ProductReferenceTypeEnum.BASE_PRODUCT);
        given(productConverter.convert(productModel)).willReturn(productData);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfSourceIsNull() throws Exception
    {
        populator.populate(null, productReferenceData);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfTargetIsNull() throws Exception
    {
        populator.populate(source, null);
    }

    @Test
    public void shouldPopulateTarget() throws Exception
    {
        populator.populate(source, productReferenceData);

        assertEquals("Description does not match", PRODUCT_DESCRIPTION, productReferenceData.getDescription());
        assertEquals("Quantity does not match", Integer.MAX_VALUE, productReferenceData.getQuantity().intValue());
        assertSame("ProductReferenceTypeEnum does not match", ProductReferenceTypeEnum.BASE_PRODUCT, productReferenceData.getReferenceType());
        assertSame("Product does not match", productData, productReferenceData.getTarget());
    }
}