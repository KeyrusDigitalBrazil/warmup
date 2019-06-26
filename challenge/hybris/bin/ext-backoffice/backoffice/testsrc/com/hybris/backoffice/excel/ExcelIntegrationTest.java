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
package com.hybris.backoffice.excel;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.impex.ImportConfig;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.testframework.Transactional;
import de.hybris.platform.variants.model.VariantProductModel;
import de.hybris.platform.variants.model.VariantTypeModel;

import javax.annotation.Nonnull;
import javax.annotation.Resource;

import org.junit.Before;


@Transactional
@IntegrationTest
public abstract class ExcelIntegrationTest extends ServicelayerTest
{
	protected static final String PRODUCT_SHEET_NAME = "Product";
	protected static final String PRODUCT_VARIANT_SHEET_NAME = "ProductVariant";
	protected static final String TYPE_SYSTEM_SHEET_NAME = "TypeSystem";

	@Resource
	TypeService typeService;

	@Resource
	ModelService modelService;

	@Before
	public void setUp() throws Exception
	{
		JaloSession.getCurrentSession().setUser(UserManager.getInstance().getAdminEmployee());
	}

	protected CatalogVersionModel createCatalogVersionModel(final @Nonnull String catalogId, final @Nonnull String catalogVersion)
	{
		final CatalogModel catalogModel = new CatalogModel();
		catalogModel.setId(catalogId);

		final CatalogVersionModel catalogVersionModel = new CatalogVersionModel();
		catalogVersionModel.setVersion(catalogVersion);
		catalogVersionModel.setCatalog(catalogModel);
		return catalogVersionModel;
	}

	protected <T extends ItemModel> T saveItem(final @Nonnull T itemModel)
	{
		modelService.save(itemModel);
		modelService.detach(itemModel);
		return itemModel;
	}

	protected AttributeDescriptorModel getAttributeDescriptorOf(final @Nonnull ItemModel item,
			final @Nonnull String attributeQualifier)
	{
		return typeService.getAttributeDescriptor(typeService.getComposedTypeForClass(item.getClass()), attributeQualifier);
	}

	protected AttributeDescriptorModel getAttributeDescriptorOf(final @Nonnull Class clazz,
			final @Nonnull String attributeQualifier)
	{
		return typeService.getAttributeDescriptor(typeService.getComposedTypeForClass(clazz), attributeQualifier);
	}

	protected ProductModel prepareProduct(final String code, final CatalogVersionModel catalogVersionModel)
	{
		final ProductModel productModel = new ProductModel();
		productModel.setCode(code);
		productModel.setCatalogVersion(catalogVersionModel);

		return productModel;
	}

	protected ProductModel prepareProductWithVariant(final ProductModel productModel, final VariantTypeModel variantType)
	{
		productModel.setVariantType(variantType);
		return productModel;
	}

	protected VariantProductModel prepareVariantProductModel(final ProductModel baseProduct, final VariantTypeModel variantType)
	{
		final VariantProductModel variantProductModel = new VariantProductModel();
		variantProductModel.setBaseProduct(baseProduct);
		variantProductModel.setVariantType(variantType);
		variantProductModel.setCode("variantProduct");
		variantProductModel.setCatalogVersion(baseProduct.getCatalogVersion());
		return variantProductModel;
	}

	protected VariantTypeModel prepareVariant()
	{
		final VariantTypeModel variantTypeModel = new VariantTypeModel();
		variantTypeModel.setCode("ProductVariant");
		variantTypeModel.setCatalogItemType(false);
		variantTypeModel.setGenerate(false);
		variantTypeModel.setSingleton(false);
		return variantTypeModel;
	}

	protected ImportConfig createImportConfig(final String script)
	{
		final ImportConfig importConfig = new ImportConfig();
		importConfig.setSynchronous(true);
		importConfig.setFailOnError(true);
		importConfig.setDistributedImpexEnabled(false);
		importConfig.setScript(script);
		return importConfig;
	}

	public TypeService getTypeService()
	{
		return typeService;
	}

	public ModelService getModelService()
	{
		return modelService;
	}
}
