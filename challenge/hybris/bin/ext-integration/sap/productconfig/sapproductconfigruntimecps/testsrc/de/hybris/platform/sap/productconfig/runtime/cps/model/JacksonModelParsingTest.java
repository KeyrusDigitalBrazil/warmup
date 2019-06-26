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
package de.hybris.platform.sap.productconfig.runtime.cps.model;

import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalCharacteristic;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalItem;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalObjectKey;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalValue;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.CPSMasterDataCharacteristic;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.CPSMasterDataCharacteristicGroup;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.CPSMasterDataCharacteristicSpecific;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.CPSMasterDataClass;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.CPSMasterDataDependency;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.CPSMasterDataKnowledgeBase;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.CPSMasterDataPossibleValue;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.CPSMasterDataPossibleValueSpecific;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.CPSMasterDataProduct;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.AccessDate;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.AlternateProductUnit;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.Attribute;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.ConditionPurpose;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.ConditionResult;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.PricingDocumentInput;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.PricingDocumentResult;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.PricingItemInput;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.PricingItemResult;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.ProductInfo;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.Subtotal;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSCharacteristic;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSCharacteristicGroup;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSItem;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSPossibleValue;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSQuantity;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSValue;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSVariantCondition;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;


@SuppressWarnings("javadoc")
@UnitTest
public class JacksonModelParsingTest
{
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Test
	public void testCPSConfiguration()
	{
		assertTrue(objectMapper.canSerialize(CPSConfiguration.class));
		assertTrue(objectMapper.canSerialize(CPSCharacteristic.class));
		assertTrue(objectMapper.canSerialize(CPSCharacteristicGroup.class));
		assertTrue(objectMapper.canSerialize(CPSItem.class));
		assertTrue(objectMapper.canSerialize(CPSPossibleValue.class));
		assertTrue(objectMapper.canSerialize(CPSValue.class));
		assertTrue(objectMapper.canSerialize(CPSVariantCondition.class));
	}

	@Test
	public void testCPSExternalConfiguration()
	{
		assertTrue(objectMapper.canSerialize(CPSExternalConfiguration.class));
		assertTrue(objectMapper.canSerialize(CPSExternalCharacteristic.class));
		assertTrue(objectMapper.canSerialize(CPSExternalItem.class));
		assertTrue(objectMapper.canSerialize(CPSExternalObjectKey.class));
		assertTrue(objectMapper.canSerialize(CPSExternalValue.class));
	}

	@Test
	public void testCPSMasterDataKnowledgeBase()
	{
		assertTrue(objectMapper.canSerialize(CPSMasterDataKnowledgeBase.class));
		assertTrue(objectMapper.canSerialize(CPSMasterDataCharacteristic.class));
		assertTrue(objectMapper.canSerialize(CPSMasterDataCharacteristicGroup.class));
		assertTrue(objectMapper.canSerialize(CPSMasterDataCharacteristicSpecific.class));
		assertTrue(objectMapper.canSerialize(CPSMasterDataClass.class));
		assertTrue(objectMapper.canSerialize(CPSMasterDataDependency.class));
		assertTrue(objectMapper.canSerialize(CPSMasterDataPossibleValue.class));
		assertTrue(objectMapper.canSerialize(CPSMasterDataPossibleValueSpecific.class));
		assertTrue(objectMapper.canSerialize(CPSMasterDataProduct.class));
	}

	@Test
	public void testPricing()
	{
		assertTrue(objectMapper.canSerialize(PricingDocumentInput.class));
		assertTrue(objectMapper.canSerialize(PricingDocumentResult.class));
		assertTrue(objectMapper.canSerialize(AccessDate.class));
		assertTrue(objectMapper.canSerialize(AlternateProductUnit.class));
		assertTrue(objectMapper.canSerialize(Attribute.class));
		assertTrue(objectMapper.canSerialize(ConditionPurpose.class));
		assertTrue(objectMapper.canSerialize(ConditionResult.class));
		assertTrue(objectMapper.canSerialize(PricingItemInput.class));
		assertTrue(objectMapper.canSerialize(PricingItemResult.class));
		assertTrue(objectMapper.canSerialize(ProductInfo.class));
		assertTrue(objectMapper.canSerialize(CPSQuantity.class));
		assertTrue(objectMapper.canSerialize(Subtotal.class));
	}

}
