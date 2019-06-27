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
package de.hybris.platform.cms2.model.restrictions;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@IntegrationTest
public class CMSProductRestrictionModelTest extends ServicelayerBaseTest // NOPMD
{
	@Resource
	private ModelService modelService;
	@Resource
	private TypeService typeService;
	@Mock
	private ProductModel product1;
	@Mock
	private ProductModel product2;
	@Mock
	private ProductModel product3;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * Test method for {@link de.hybris.platform.cms2.model.restrictions.CMSProductRestrictionModel#getDescription()}.
	 */
	@Test
	public void shouldHaveDynamicAttributeDescriptor()
	{
		//given
		final ComposedTypeModel type = typeService.getComposedTypeForClass(CMSProductRestrictionModel.class);

		//when
		final AttributeDescriptorModel attributeDescriptor = typeService.getAttributeDescriptor(type,
				CMSCategoryRestrictionModel.DESCRIPTION);

		//then
		assertThat(attributeDescriptor).isNotNull();
		assertThat(attributeDescriptor.getAttributeHandler()).isEqualTo("productRestrictionDynamicDescription");
	}

	/**
	 * Test method for {@link de.hybris.platform.cms2.model.restrictions.CMSProductRestrictionModel#getDescription()}.
	 */
	@Test
	public void shouldReturnRestrictionDescription()
	{
		//given
		final Collection<ProductModel> products = new ArrayList<ProductModel>();
		products.add(product1);
		products.add(product2);
		products.add(product3);
		given(product1.getName()).willReturn("Name1");
		given(product2.getName()).willReturn("Name2");
		given(product1.getCode()).willReturn("Code1");
		given(product2.getCode()).willReturn("Code2");
		given(product3.getCode()).willReturn("Code3");
		final CMSProductRestrictionModel restriction = modelService.create(CMSProductRestrictionModel.class);
		restriction.setProducts(products);

		//when
		final String description = restriction.getDescription();

		//then
		assertThat(description).isNotNull();
		assertThat(description).isEqualTo("Display for products: Name1 (Code1); Name2 (Code2); (Code3);");
	}
}
