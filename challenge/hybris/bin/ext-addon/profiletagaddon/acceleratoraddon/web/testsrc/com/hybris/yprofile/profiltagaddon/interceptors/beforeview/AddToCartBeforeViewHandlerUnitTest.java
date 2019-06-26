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
package com.hybris.yprofile.profiltagaddon.interceptors.beforeview;

import com.hybris.yprofile.profiletagaddon.interceptors.beforeview.AddToCartBeforeViewHandler;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

@UnitTest
public class AddToCartBeforeViewHandlerUnitTest {

    private static final String PRODUCT_CODE = "productCode";
    private static final String PRODUCT_NAME = "productName";

    private static final ProductData PRODUCT = createProduct(PRODUCT_CODE, PRODUCT_NAME);

    private static final String CATEGORY_CODE_1 = "categoryCode";
    private static final String CATEGORY_NAME_1 = "categoryName";
    private static final String PARENT_CATEGORY_NAME_1 = "parentCategoryName";

    private static final String CATEGORY_CODE_2 = "categoryCode";
    private static final String CATEGORY_NAME_2 = "categoryName";

    private static final CategoryData CATEGORY_1 = createCategory(CATEGORY_CODE_1, CATEGORY_NAME_1, PARENT_CATEGORY_NAME_1);
    private static final CategoryData CATEGORY_2 = createCategory(CATEGORY_CODE_2, CATEGORY_NAME_2, null);

    private static final Collection<CategoryData> CATEGORIES = Arrays.asList(
            CATEGORY_1,
            CATEGORY_2
    );

    @InjectMocks
    private AddToCartBeforeViewHandler addToCartBeforeViewHandler;






    @Mock
    private ProductFacade productFacade;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Before
    public final void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldAddCategoriesToAdToCartModel()
    {
        ProductData productDataWithCategories = new ProductData();
        productDataWithCategories.setCategories(CATEGORIES);

        Map<String, Object> productModel = new HashMap<>();
        productModel.put("product", PRODUCT);
        ModelAndView modelAndView = new ModelAndView(PRODUCT_CODE, productModel);

        when(productFacade.getProductForCodeAndOptions(eq(PRODUCT_CODE), Collections.singleton(ProductOption.CATEGORIES)))
                .thenReturn(productDataWithCategories);

        addToCartBeforeViewHandler.beforeView(request, response, modelAndView);

        // assert that all product data and all categories are contained in the modelAndView
        // i.e. the product data is properly merged
        ProductData mergedProductData = (ProductData) modelAndView.getModel().get(PRODUCT_CODE);

        Assert.assertThat(mergedProductData.getCode(), allOf(notNullValue(String.class), is(PRODUCT_CODE)));
        Assert.assertThat(mergedProductData.getName(), allOf(notNullValue(String.class), is(PRODUCT_NAME)));
        Assert.assertThat(mergedProductData.getCategories(), allOf(notNullValue(Collection.class), is(CATEGORIES)));

    }

    private static final CategoryData createCategory(String code, String name, String parentCategory) {
        CategoryData categoryData = new CategoryData();
        categoryData.setCode(code);
        categoryData.setName(name);
        categoryData.setParentCategoryName(parentCategory);
        return  categoryData;
    }

    private static final ProductData createProduct(String code, String name) {
        ProductData productData = new ProductData();
        productData.setCode(code);
        productData.setName(name);
        return productData;
    }
}
