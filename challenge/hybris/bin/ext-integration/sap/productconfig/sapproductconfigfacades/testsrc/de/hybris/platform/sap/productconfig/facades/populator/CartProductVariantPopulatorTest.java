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
package de.hybris.platform.sap.productconfig.facades.populator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.BaseOptionData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.facades.VariantConfigurationInfoProvider;
import de.hybris.platform.sap.productconfig.services.impl.CPQConfigurableChecker;
import de.hybris.platform.sap.productconfig.services.impl.ConfigurationVariantUtilImpl;
import de.hybris.platform.sap.sapmodel.model.ERPVariantProductModel;
import de.hybris.platform.variants.model.VariantTypeModel;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class CartProductVariantPopulatorTest
{

	private CartProductVariantPopulator classUnderTest;
	private CartModel source;
	private CartData target;
	private List<OrderEntryData> targetEntryList;
	private OrderEntryData targetEntry;
	private OrderEntryData targetEntry2;
	private OrderEntryData targetEntry3;
	private OrderEntryData targetEntry4;
	private final Integer entryNo1 = Integer.valueOf(1);
	private final Integer entryNo2 = Integer.valueOf(2);
	private final Integer entryNo3 = Integer.valueOf(3);
	private final Integer entryNo4 = Integer.valueOf(4);
	private ProductModel productModelKmat;
	private ERPVariantProductModel productModelVariant;
	private ERPVariantProductModel productModelChangeableVariant;
	private ProductModel productModel;
	private ProductModel baseProductModel;
	@Mock
	private VariantConfigurationInfoProvider variantConfigurationInfoProvider;

	@Mock
	private AbstractOrderEntryModel sourceEntry;
	@Mock
	private AbstractOrderEntryModel sourceEntry2;
	@Mock
	private AbstractOrderEntryModel sourceEntry3;
	@Mock
	private AbstractOrderEntryModel sourceEntry4;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		source = new CartModel();

		final List<AbstractOrderEntryModel> entryList = new ArrayList();
		entryList.add(sourceEntry);
		entryList.add(sourceEntry2);
		entryList.add(sourceEntry3);
		entryList.add(sourceEntry4);

		target = new CartData();
		targetEntryList = new ArrayList();
		target.setEntries(targetEntryList);
		targetEntry = new OrderEntryData();
		targetEntry.setEntryNumber(entryNo1);
		targetEntry.setItemPK("123");
		targetEntryList.add(targetEntry);
		targetEntry2 = new OrderEntryData();
		targetEntry2.setEntryNumber(entryNo2);
		targetEntry2.setItemPK("456");
		targetEntry2.setProduct(new ProductData());
		targetEntry2.getProduct().setBaseOptions(new ArrayList<>());
		targetEntry2.getProduct().getBaseOptions().add(new BaseOptionData());
		targetEntryList.add(targetEntry2);
		targetEntry3 = new OrderEntryData();
		targetEntry3.setEntryNumber(entryNo3);
		targetEntry3.setItemPK("789");
		targetEntryList.add(targetEntry3);
		targetEntry4 = new OrderEntryData();
		targetEntry4.setEntryNumber(entryNo4);
		targetEntry4.setItemPK("876");
		targetEntry4.setProduct(new ProductData());
		targetEntry4.getProduct().setBaseOptions(new ArrayList<>());
		targetEntry4.getProduct().getBaseOptions().add(new BaseOptionData());
		targetEntryList.add(targetEntry4);


		source.setEntries(entryList);

		final VariantTypeModel variantType = new VariantTypeModel();
		variantType.setCode(ERPVariantProductModel._TYPECODE);
		baseProductModel = new ProductModel();
		baseProductModel.setVariantType(variantType);

		productModel = new ProductModel();
		productModel.setCode("Non-Configurable Product");
		productModelKmat = new ProductModel();
		productModelKmat.setCode("Configurable Product");
		productModelVariant = new ERPVariantProductModel();
		productModelVariant.setCode("Product Variant");
		productModelVariant.setChangeable(false);
		productModelVariant.setBaseProduct(baseProductModel);
		productModelChangeableVariant = new ERPVariantProductModel();
		productModelChangeableVariant.setCode("Changeable Product Variant");
		productModelChangeableVariant.setChangeable(true);
		productModelChangeableVariant.setBaseProduct(baseProductModel);

		classUnderTest = new CartProductVariantPopulator();
		classUnderTest.setVariantConfigurationInfoProvider(variantConfigurationInfoProvider);
		final ConfigurationVariantUtilImpl variantUtil = new ConfigurationVariantUtilImpl();
		final CPQConfigurableChecker checker = new CPQConfigurableChecker();
		checker.setConfigurationVariantUtil(variantUtil);
		classUnderTest.setCpqConfigurableChecker(checker);
	}

	@Test
	public void testPopulate()
	{
		assertFalse(target.getEntries().get(1).getProduct().getBaseOptions().isEmpty());
		final PK value = initializeSourceItem();
		final int numberOfMaxCstics = 2;
		classUnderTest.populate(source, target);
		// only the product variant should be populated (not the changeable product variant and other products)
		verify(variantConfigurationInfoProvider, times(1)).retrieveVariantConfigurationInfo(any());
		final OrderEntryData targetEntryProductVariant = target.getEntries().get(1);
		assertTrue("ItemPK not set", targetEntryProductVariant.getItemPK().equals(value.toString()));
		assertTrue(targetEntryProductVariant.getProduct().getBaseOptions().isEmpty());
	}

	private PK initializeSourceItem()
	{
		final PK value = PK.fromLong(123);
		Mockito.when(sourceEntry.getPk()).thenReturn(value);
		Mockito.when(sourceEntry.getProduct()).thenReturn(productModel);
		Mockito.when(sourceEntry.getEntryNumber()).thenReturn(entryNo1);

		final PK value2 = PK.fromLong(456);
		Mockito.when(sourceEntry2.getPk()).thenReturn(value2);
		Mockito.when(sourceEntry2.getProduct()).thenReturn(productModelVariant);
		Mockito.when(sourceEntry2.getEntryNumber()).thenReturn(entryNo2);

		final PK value3 = PK.fromLong(789);
		Mockito.when(sourceEntry3.getPk()).thenReturn(value3);
		Mockito.when(sourceEntry3.getProduct()).thenReturn(productModelKmat);
		Mockito.when(sourceEntry3.getEntryNumber()).thenReturn(entryNo3);

		final PK value4 = PK.fromLong(876);
		Mockito.when(sourceEntry4.getPk()).thenReturn(value4);
		Mockito.when(sourceEntry4.getProduct()).thenReturn(productModelChangeableVariant);
		Mockito.when(sourceEntry4.getEntryNumber()).thenReturn(entryNo4);

		return value2;
	}
}
