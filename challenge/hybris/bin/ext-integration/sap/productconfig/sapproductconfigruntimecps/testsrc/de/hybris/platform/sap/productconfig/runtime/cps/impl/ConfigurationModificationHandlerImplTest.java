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
package de.hybris.platform.sap.productconfig.runtime.cps.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.cps.masterdata.service.ConfigurationMasterDataService;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSVariantCondition;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ConfigurationRetrievalOptions;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ProductConfigurationDiscount;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.VariantConditionModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticValueModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.InstanceModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.VariantConditionModelImpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ConfigurationModificationHandlerImplTest
{
	private static final String SUB_PRODUCT = "subProduct";
	private static final String VARIANT_CONDITION_THAT_WILL_NOT_BE_MODIFIED = "variant condition that will not be modified";
	private static final BigDecimal DEFAULT_FACTOR = BigDecimal.ONE;
	private static final String VARIANT_CONDITION_KEY = "variant condition key";
	private static final String ANOTHER_VARIANT_CONDITION_KEY = "another variant condition key";
	private static final String PRODUCT_KEY = "product key";
	private static final String KBID = "kbid";
	private static final BigDecimal DISCOUNT = BigDecimal.valueOf(20.0);
	private static final String CSTIC_VALUE = "cstic value";
	private static final String CSTIC_NAME = "cstic name";
	private static final BigDecimal ANOTHER_DISCOUNT = BigDecimal.valueOf(40.0);
	private static final String ANOTHER_CSTIC_VALUE = "another cstic value";
	private static final String ANOTHER_CSTIC_NAME = "another cstic name";

	private ConfigurationModificationHandlerImpl classUnderTest;
	private ConfigurationRetrievalOptions options;
	private ConfigModel config;
	private ProductConfigurationDiscount modification;
	private ProductConfigurationDiscount anotherModification;
	private VariantConditionModel variantCondition1;
	private VariantConditionModel variantCondition2;
	private VariantConditionModel variantCondition3;
	private InstanceModel subInstance;
	private InstanceModel rootInstance;

	@Mock
	private ConfigurationMasterDataService masterDataService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new ConfigurationModificationHandlerImpl();
		classUnderTest.setConfigurationMasterDataService(masterDataService);
		options = new ConfigurationRetrievalOptions();
		options.setDiscountList(new ArrayList<>());
		prepareConfiguration();
		createVariantConditionModification();
		createAnotherVariantConditionModification();
		given(masterDataService.getValuePricingKey(KBID, PRODUCT_KEY, CSTIC_NAME, CSTIC_VALUE)).willReturn(VARIANT_CONDITION_KEY);
		given(masterDataService.getValuePricingKey(KBID, SUB_PRODUCT, ANOTHER_CSTIC_NAME, ANOTHER_CSTIC_VALUE))
				.willReturn(ANOTHER_VARIANT_CONDITION_KEY);
	}

	private void prepareConfiguration()
	{
		config = new ConfigModelImpl();
		config.setKbId(KBID);
		config.setName(PRODUCT_KEY);
		rootInstance = new InstanceModelImpl();
		config.setRootInstance(rootInstance);
		rootInstance.setName(PRODUCT_KEY);
		rootInstance.setVariantConditions(new ArrayList<>());
		variantCondition1 = new VariantConditionModelImpl();
		variantCondition1.setKey(VARIANT_CONDITION_KEY);
		variantCondition1.setFactor(DEFAULT_FACTOR);
		rootInstance.getVariantConditions().add(variantCondition1);
		variantCondition2 = new VariantConditionModelImpl();
		variantCondition2.setKey(VARIANT_CONDITION_THAT_WILL_NOT_BE_MODIFIED);
		variantCondition2.setFactor(DEFAULT_FACTOR);
		rootInstance.getVariantConditions().add(variantCondition2);
		variantCondition3 = new VariantConditionModelImpl();
		variantCondition3.setKey(ANOTHER_VARIANT_CONDITION_KEY);
		variantCondition3.setFactor(DEFAULT_FACTOR);
		final CsticModel cstic1 = new CsticModelImpl();
		cstic1.setName(CSTIC_NAME);
		final CsticValueModel value1 = new CsticValueModelImpl();
		value1.setName(CSTIC_VALUE);
		cstic1.setAssignedValues(Collections.singletonList(value1));
		rootInstance.setCstics(Collections.singletonList(cstic1));

		subInstance = new InstanceModelImpl();
		subInstance.setVariantConditions(new ArrayList<>());
		subInstance.getVariantConditions().add(variantCondition3);
		subInstance.setName(SUB_PRODUCT);
		final CsticModel cstic3 = new CsticModelImpl();
		cstic3.setName(ANOTHER_CSTIC_NAME);
		final CsticValueModel value3 = new CsticValueModelImpl();
		value3.setName(ANOTHER_CSTIC_VALUE);
		cstic3.setAssignedValues(Collections.singletonList(value3));
		subInstance.setCstics(new ArrayList<>());
		subInstance.addCstic(cstic3);

		rootInstance.setSubInstances(Collections.singletonList(subInstance));
	}

	private void createVariantConditionModification()
	{
		modification = new ProductConfigurationDiscount();
		modification.setCsticName(CSTIC_NAME);
		modification.setCsticValueName(CSTIC_VALUE);
		modification.setDiscount(DISCOUNT);
		options.getDiscountList().add(modification);
	}

	private void createAnotherVariantConditionModification()
	{
		anotherModification = new ProductConfigurationDiscount();
		anotherModification.setCsticName(ANOTHER_CSTIC_NAME);
		anotherModification.setCsticValueName(ANOTHER_CSTIC_VALUE);
		anotherModification.setDiscount(ANOTHER_DISCOUNT);
		options.getDiscountList().add(anotherModification);
	}


	@Test
	public void testAdjustVariantConditions()
	{
		classUnderTest.adjustVariantConditions(config, options);
		final List<VariantConditionModel> result = config.getRootInstance().getVariantConditions();
		assertEquals(2, result.size());
		assertEquals(VARIANT_CONDITION_KEY, result.get(0).getKey());
		assertEquals("0.8", result.get(0).getFactor().toString());
		checkConditionWasNotModified(1);
		checkSubItemConditionWasModified();
	}

	@Test
	public void testAdjustVariantConditionsNoSubInstances()
	{
		config.getRootInstance().setSubInstances(null);
		classUnderTest.adjustVariantConditions(config, options);
		final List<VariantConditionModel> result = config.getRootInstance().getVariantConditions();
		assertEquals(2, result.size());
		assertEquals(VARIANT_CONDITION_KEY, result.get(0).getKey());
		assertEquals("0.8", result.get(0).getFactor().toString());
		checkConditionWasNotModified(1);
	}


	@Test
	public void testAdjustVariantConditionNotExisting()
	{
		config.getRootInstance().getVariantConditions().remove(0);
		classUnderTest.adjustVariantConditions(config, options);
		final List<VariantConditionModel> result = config.getRootInstance().getVariantConditions();
		assertEquals(1, result.size());
		checkConditionWasNotModified(0);
		checkSubItemConditionWasModified();
	}

	@Test
	public void testAdjustVariantConditionCSticNotExisting()
	{
		config.getRootInstance().setCstics(Collections.emptyList());
		config.getRootInstance().getVariantConditions().remove(0);
		classUnderTest.adjustVariantConditions(config, options);
		final List<VariantConditionModel> result = config.getRootInstance().getVariantConditions();
		assertEquals(1, result.size());
		checkConditionWasNotModified(0);
		checkSubItemConditionWasModified();
	}

	protected void checkConditionWasNotModified(final int pos)
	{
		final List<VariantConditionModel> result = config.getRootInstance().getVariantConditions();
		assertEquals(VARIANT_CONDITION_THAT_WILL_NOT_BE_MODIFIED, result.get(pos).getKey());
		assertEquals(DEFAULT_FACTOR, result.get(pos).getFactor());
	}


	protected void checkSubItemConditionWasModified()
	{
		final List<VariantConditionModel> subResult = config.getRootInstance().getSubInstances().get(0).getVariantConditions();
		assertEquals(1, subResult.size());
		assertEquals(ANOTHER_VARIANT_CONDITION_KEY, subResult.get(0).getKey());
		assertEquals("0.6", subResult.get(0).getFactor().toString());
	}


	@Test
	public void testAdjustVariantConditionsNull()
	{
		config.getRootInstance().setVariantConditions(null);
		classUnderTest.adjustVariantConditions(config, options);
		assertNull(config.getRootInstance().getVariantConditions());
	}

	@Test
	public void testComputeFactorWithDiscount()
	{
		assertEquals(0, BigDecimal.valueOf(0.8).compareTo(classUnderTest.computeFactorWithDiscount("1", DISCOUNT)));
		assertEquals(0, BigDecimal.valueOf(0.64).compareTo(classUnderTest.computeFactorWithDiscount("0.8", DISCOUNT)));
	}

	@Test
	public void testAdjustVariantConditionsCsticOccursInMultipleItems()
	{
		given(masterDataService.getValuePricingKey(KBID, SUB_PRODUCT, CSTIC_NAME, CSTIC_VALUE)).willReturn(VARIANT_CONDITION_KEY);
		final CsticModel cstic4 = new CsticModelImpl();
		cstic4.setName(ANOTHER_CSTIC_NAME);
		final CsticValueModel value4 = new CsticValueModelImpl();
		value4.setName(ANOTHER_CSTIC_VALUE);
		cstic4.setAssignedValues(Collections.singletonList(value4));
		assertNotNull(subInstance);
		subInstance.addCstic(cstic4);

		final VariantConditionModel variantCondition4 = new VariantConditionModelImpl();
		variantCondition4.setKey(VARIANT_CONDITION_KEY);
		variantCondition4.setFactor(DEFAULT_FACTOR);
		subInstance.getVariantConditions().add(variantCondition4);

		classUnderTest.adjustVariantConditions(config, options);

		final List<VariantConditionModel> resultRootItem = config.getRootInstance().getVariantConditions();
		assertEquals(2, resultRootItem.size());
		assertEquals(VARIANT_CONDITION_KEY, resultRootItem.get(0).getKey());
		assertEquals("0.8", resultRootItem.get(0).getFactor().toString());

		assertEquals(VARIANT_CONDITION_THAT_WILL_NOT_BE_MODIFIED, resultRootItem.get(1).getKey());
		assertEquals(0, DEFAULT_FACTOR.compareTo(resultRootItem.get(1).getFactor()));

		final List<VariantConditionModel> resultSubItem = config.getRootInstance().getSubInstances().get(0).getVariantConditions();
		assertEquals(2, resultSubItem.size());
		assertEquals(VARIANT_CONDITION_KEY, resultSubItem.get(1).getKey());
		assertEquals("0.8", resultSubItem.get(1).getFactor().toString());

		assertEquals(ANOTHER_VARIANT_CONDITION_KEY, resultSubItem.get(0).getKey());
		assertEquals("0.6", resultSubItem.get(0).getFactor().toString());
	}

	@Test
	public void testApplyDiscount()
	{
		final CPSVariantCondition condition = new CPSVariantCondition();
		condition.setKey(VARIANT_CONDITION_KEY);
		condition.setFactor(DEFAULT_FACTOR.toString());
		final HashMap<String, BigDecimal> variantConditionDiscounts = new HashMap();
		variantConditionDiscounts.put(VARIANT_CONDITION_KEY, DISCOUNT);
		classUnderTest.applyConditionDiscount(condition, variantConditionDiscounts);
		assertEquals("0.8", condition.getFactor());
	}

	@Test
	public void testApplyDiscountNoDiscount()
	{
		final CPSVariantCondition condition = new CPSVariantCondition();
		condition.setKey(VARIANT_CONDITION_KEY);
		condition.setFactor(DEFAULT_FACTOR.toString());
		classUnderTest.applyConditionDiscount(condition, new HashMap());
		assertEquals(DEFAULT_FACTOR.toString(), condition.getFactor());
	}
}
