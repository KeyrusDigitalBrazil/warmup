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
package com.hybris.backoffice.excel.translators.generic.factory;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.testframework.Transactional;

import javax.annotation.Resource;

import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;

import com.hybris.backoffice.excel.translators.generic.RequiredAttribute;


@Transactional
@IntegrationTest
public class DefaultRequiredAttributesFactoryIntegrationTest extends ServicelayerTest
{

	@Resource
	TypeService typeService;

	@Resource
	RequiredAttributesFactory requiredAttributesFactory;

	@Rule
	public JUnitSoftAssertions soft = new JUnitSoftAssertions();

	@Test
	public void shouldPrepareStructureForCatalogVersion()
	{
		// given
		final AttributeDescriptorModel catalogVersionModel = typeService.getAttributeDescriptor(ProductModel._TYPECODE,
				ProductModel.CATALOGVERSION);

		// when
		final RequiredAttribute requiredAttribute = requiredAttributesFactory.create(catalogVersionModel);

		// then
		// Product.catalogVersion
		soft.assertThat(requiredAttribute.getEnclosingType()).isEqualTo(ProductModel._TYPECODE);
		soft.assertThat(requiredAttribute.getQualifier()).isEqualTo(ProductModel.CATALOGVERSION);
		soft.assertThat(requiredAttribute.getChildren()).hasSize(2);

		// Product.catalogVersion.version
		soft.assertThat(requiredAttribute.getChildren().get(0).getEnclosingType()).isEqualTo(CatalogVersionModel._TYPECODE);
		soft.assertThat(requiredAttribute.getChildren().get(0).getQualifier()).isEqualTo(CatalogVersionModel.VERSION);
		soft.assertThat(requiredAttribute.getChildren().get(0).getChildren()).isEmpty();

		// Product.catalogVersion.catalog
		soft.assertThat(requiredAttribute.getChildren().get(1).getEnclosingType()).isEqualTo(CatalogVersionModel._TYPECODE);
		soft.assertThat(requiredAttribute.getChildren().get(1).getQualifier()).isEqualTo(CatalogVersionModel.CATALOG);
		soft.assertThat(requiredAttribute.getChildren().get(1).getChildren()).hasSize(1);

		// Product.catalogVersion.catalog.id
		soft.assertThat(requiredAttribute.getChildren().get(1).getChildren().get(0).getEnclosingType())
				.isEqualTo(CatalogModel._TYPECODE);
		soft.assertThat(requiredAttribute.getChildren().get(1).getChildren().get(0).getQualifier()).isEqualTo(CatalogModel.ID);
		soft.assertThat(requiredAttribute.getChildren().get(1).getChildren().get(0).getChildren()).isEmpty();
	}

	@Test
	public void shouldPrepareStructureForSupercategories()
	{
		// given
		final AttributeDescriptorModel catalogVersionModel = typeService.getAttributeDescriptor(ProductModel._TYPECODE,
				ProductModel.SUPERCATEGORIES);

		// when
		final RequiredAttribute requiredAttribute = requiredAttributesFactory.create(catalogVersionModel);

		// then
		// Product.supercategories
		soft.assertThat(requiredAttribute.getEnclosingType()).isEqualTo(ProductModel._TYPECODE);
		soft.assertThat(requiredAttribute.getQualifier()).isEqualTo(ProductModel.SUPERCATEGORIES);
		soft.assertThat(requiredAttribute.getChildren()).hasSize(2);

		// Product.supercategories.code
		soft.assertThat(requiredAttribute.getChildren().get(0).getEnclosingType()).isEqualTo(CategoryModel._TYPECODE);
		soft.assertThat(requiredAttribute.getChildren().get(0).getQualifier()).isEqualTo(CategoryModel.CODE);
		soft.assertThat(requiredAttribute.getChildren().get(0).getChildren()).isEmpty();

		// Product.supercategories.catalogVersion
		soft.assertThat(requiredAttribute.getChildren().get(0).getEnclosingType()).isEqualTo(CategoryModel._TYPECODE);
		soft.assertThat(requiredAttribute.getChildren().get(1).getQualifier()).isEqualTo(CategoryModel.CATALOGVERSION);
		soft.assertThat(requiredAttribute.getChildren().get(1).getChildren()).hasSize(2);

		// Product.supercategories.catalogVersion.version
		soft.assertThat(requiredAttribute.getChildren().get(1).getChildren().get(0).getEnclosingType())
				.isEqualTo(CatalogVersionModel._TYPECODE);
		soft.assertThat(requiredAttribute.getChildren().get(1).getChildren().get(0).getQualifier())
				.isEqualTo(CatalogVersionModel.VERSION);
		soft.assertThat(requiredAttribute.getChildren().get(1).getChildren().get(0).getChildren()).isEmpty();

		// Product.supercategories.catalogVersion.catalog
		soft.assertThat(requiredAttribute.getChildren().get(1).getChildren().get(1).getEnclosingType())
				.isEqualTo(CatalogVersionModel._TYPECODE);
		soft.assertThat(requiredAttribute.getChildren().get(1).getChildren().get(1).getQualifier())
				.isEqualTo(CatalogVersionModel.CATALOG);
		soft.assertThat(requiredAttribute.getChildren().get(1).getChildren().get(1).getChildren()).hasSize(1);

		// Product.supercategories.catalogVersion.catalog.id
		soft.assertThat(requiredAttribute.getChildren().get(1).getChildren().get(1).getChildren().get(0).getEnclosingType())
				.isEqualTo(CatalogModel._TYPECODE);
		soft.assertThat(requiredAttribute.getChildren().get(1).getChildren().get(1).getChildren().get(0).getQualifier())
				.isEqualTo(CatalogModel.ID);
		soft.assertThat(requiredAttribute.getChildren().get(1).getChildren().get(1).getChildren().get(0).getChildren()).isEmpty();
	}

	@Test
	public void shouldPrepareStructureForComposedType()
	{
		// given
		final ComposedTypeModel productComposedType = typeService.getComposedTypeForCode(ProductModel._TYPECODE);

		// when
		final RequiredAttribute requiredAttribute = requiredAttributesFactory.create(productComposedType);

		// then
		// Product
		soft.assertThat(requiredAttribute.getEnclosingType()).isEqualTo(ProductModel._TYPECODE);
		soft.assertThat(requiredAttribute.getQualifier()).isNullOrEmpty();
		soft.assertThat(requiredAttribute.getChildren()).hasSize(2);
		soft.assertThat(requiredAttribute.getTypeModel()).isNotNull();

		// Product.code
		soft.assertThat(requiredAttribute.getChildren().get(0).getEnclosingType()).isEqualTo(ProductModel._TYPECODE);
		soft.assertThat(requiredAttribute.getChildren().get(0).getQualifier()).isEqualTo(ProductModel.CODE);
		soft.assertThat(requiredAttribute.getChildren().get(0).getChildren()).isEmpty();

		// Product.catalogVersion
		soft.assertThat(requiredAttribute.getChildren().get(1).getEnclosingType()).isEqualTo(ProductModel._TYPECODE);
		soft.assertThat(requiredAttribute.getChildren().get(1).getQualifier()).isEqualTo(ProductModel.CATALOGVERSION);
		soft.assertThat(requiredAttribute.getChildren().get(1).getChildren()).hasSize(2);

		// Product.catalogVersion.version
		soft.assertThat(requiredAttribute.getChildren().get(1).getChildren().get(0).getEnclosingType())
				.isEqualTo(CatalogVersionModel._TYPECODE);
		soft.assertThat(requiredAttribute.getChildren().get(1).getChildren().get(0).getQualifier())
				.isEqualTo(CatalogVersionModel.VERSION);
		soft.assertThat(requiredAttribute.getChildren().get(1).getChildren().get(0).getChildren()).isEmpty();

		// Product.catalogVersion.catalog
		soft.assertThat(requiredAttribute.getChildren().get(1).getChildren().get(1).getEnclosingType())
				.isEqualTo(CatalogVersionModel._TYPECODE);
		soft.assertThat(requiredAttribute.getChildren().get(1).getChildren().get(1).getQualifier())
				.isEqualTo(CatalogVersionModel.CATALOG);
		soft.assertThat(requiredAttribute.getChildren().get(1).getChildren().get(1).getChildren()).hasSize(1);

		// Product.catalogVersion.catalog
		soft.assertThat(requiredAttribute.getChildren().get(1).getChildren().get(1).getChildren().get(0).getEnclosingType())
				.isEqualTo(CatalogModel._TYPECODE);
		soft.assertThat(requiredAttribute.getChildren().get(1).getChildren().get(1).getChildren().get(0).getQualifier())
				.isEqualTo(CatalogModel.ID);
		soft.assertThat(requiredAttribute.getChildren().get(1).getChildren().get(1).getChildren().get(0).getChildren()).isEmpty();
	}
}
