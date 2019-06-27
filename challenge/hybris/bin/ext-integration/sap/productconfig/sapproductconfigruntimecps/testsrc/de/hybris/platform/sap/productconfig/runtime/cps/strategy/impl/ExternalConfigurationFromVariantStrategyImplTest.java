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
package de.hybris.platform.sap.productconfig.runtime.cps.strategy.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.ProductFeatureModel;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeValueModel;
import de.hybris.platform.classification.ClassificationService;
import de.hybris.platform.classification.features.Feature;
import de.hybris.platform.classification.features.FeatureList;
import de.hybris.platform.classification.features.FeatureValue;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.sap.productconfig.runtime.cps.cache.MasterDataCacheAccessService;
import de.hybris.platform.sap.productconfig.runtime.cps.constants.SapproductconfigruntimecpsConstants;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalCharacteristic;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalItem;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalObjectKey;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalValue;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataCharacteristicContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataKnowledgeBaseContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSQuantity;
import de.hybris.platform.sap.productconfig.runtime.interf.services.ConfigurationProductUtil;
import de.hybris.platform.sap.sapmodel.model.ERPVariantProductModel;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
@SuppressWarnings("javadoc")
public class ExternalConfigurationFromVariantStrategyImplTest
{
	private static final String variantCode = "DRAGON_CAR_V16";
	private static final String baseProductCode = "DRAGON_CAR";
	private static final String kbId = "123";
	private static final String csticKey = "COLOUR";
	private static final String classificationAttributeNonCPQ = "NON CPQ";
	private static final String csticValue = "BK";
	private static final String csticValueInClassificationSystem = csticKey + "_" + csticValue;
	private static final String classificationValueIdentifier = "8796093809248";
	private static final String productPk = "8796183429121";
	private static final String searchString = "select {pk} from {productfeature} where {stringvalue}='"
			+ classificationValueIdentifier + "'" + "and {product}='" + productPk + "'";
	private static final String AUTHOR_CONSTRAINT = "4";
	private static final String UNIT = "PCE";

	private final ExternalConfigurationFromVariantStrategyImpl classUnderTest = new ExternalConfigurationFromVariantStrategyImpl();

	@Mock
	private ClassificationService mockClassificationService;
	@Mock
	private MasterDataCacheAccessService mockMasterDataCacheAccessService;
	@Mock
	private I18NService mockI18NService;
	@Mock
	private FlexibleSearchService mockFlexibleSearchService;
	@Mock
	private ERPVariantProductModel mockVariantModel;
	@Mock
	private ProductModel mockBaseProductModel;
	@Mock
	private FeatureList mpckFeatureList;
	@Mock
	private Feature mockFeatureRelated;
	@Mock
	private Feature mockFeatureNotRelated;
	@Mock
	private ClassAttributeAssignmentModel mockClassAttributeAssignment;
	@Mock
	private ClassAttributeAssignmentModel mockClassAttributeAssignmentNotRelated;
	@Mock
	private ClassificationAttributeModel mockClassificationAttribute;
	@Mock
	private ClassificationAttributeModel mockClassificationAttributeNotRelated;
	@Mock
	private FeatureValue mockFeatureValue;
	@Mock
	private ClassificationAttributeValueModel mockClassificationAttributeValue;
	@Mock
	private SearchResult<Object> mockSearchResult;
	@Mock
	private ProductFeatureModel mockProductFeature;
	@Mock
	private UnitModel mockUnitModel;
	@Mock
	private ConfigurationProductUtil mockConfigProductUtil;

	private final PK pkClassificationAttribute = PK.parse(classificationValueIdentifier);
	private final PK pkProduct = PK.parse(productPk);

	private final List<Feature> features = new ArrayList();
	private final CPSMasterDataKnowledgeBaseContainer kbContainer = new CPSMasterDataKnowledgeBaseContainer();
	private final Map<String, CPSMasterDataCharacteristicContainer> characteristics = new HashMap<>();
	private final CPSMasterDataCharacteristicContainer csticContainer = new CPSMasterDataCharacteristicContainer();
	private final List<FeatureValue> featureValueList = new ArrayList<>();
	private final List<Object> featureModelList = new ArrayList<>();



	@Before
	public void initialize()
	{
		MockitoAnnotations.initMocks(this);

		when(mockConfigProductUtil.getProductForCurrentCatalog(variantCode)).thenReturn(mockVariantModel);
		when(mockConfigProductUtil.getProductForCurrentCatalog(baseProductCode)).thenReturn(mockBaseProductModel);
		when(mockVariantModel.getBaseProduct()).thenReturn(mockBaseProductModel);
		when(mockVariantModel.getPk()).thenReturn(pkProduct);
		when(mockBaseProductModel.getCode()).thenReturn(baseProductCode);
		when(mockBaseProductModel.getUnit()).thenReturn(mockUnitModel);
		when(mockUnitModel.getCode()).thenReturn(UNIT);
		when(mockClassificationService.getFeatures(mockVariantModel)).thenReturn(mpckFeatureList);
		when(mockI18NService.getCurrentLocale()).thenReturn(Locale.US);
		when(mockMasterDataCacheAccessService.getKbContainer(kbId, Locale.US.getLanguage())).thenReturn(kbContainer);
		when(mpckFeatureList.getFeatures()).thenReturn(features);
		when(mockFeatureRelated.getClassAttributeAssignment()).thenReturn(mockClassAttributeAssignment);
		when(mockFeatureRelated.getValues()).thenReturn(featureValueList);
		when(mockFeatureNotRelated.getClassAttributeAssignment()).thenReturn(mockClassAttributeAssignmentNotRelated);
		when(mockFeatureValue.getValue()).thenReturn(mockClassificationAttributeValue);
		when(mockClassificationAttributeValue.getCode()).thenReturn(csticValueInClassificationSystem);
		when(mockClassificationAttributeValue.getPk()).thenReturn(pkClassificationAttribute);
		when(mockClassAttributeAssignment.getClassificationAttribute()).thenReturn(mockClassificationAttribute);
		when(mockClassAttributeAssignmentNotRelated.getClassificationAttribute()).thenReturn(mockClassificationAttributeNotRelated);
		when(mockClassificationAttribute.getCode()).thenReturn(csticKey);
		when(mockClassificationAttributeNotRelated.getCode()).thenReturn(classificationAttributeNonCPQ);
		when(mockFlexibleSearchService.search(searchString)).thenReturn(mockSearchResult);
		when(mockSearchResult.getResult()).thenReturn(featureModelList);
		when(mockProductFeature.getAuthor()).thenReturn(AUTHOR_CONSTRAINT);

		features.add(mockFeatureRelated);
		features.add(mockFeatureNotRelated);
		featureValueList.add(mockFeatureValue);
		featureModelList.add(mockProductFeature);

		kbContainer.setCharacteristics(characteristics);
		characteristics.put(csticKey, csticContainer);
		classUnderTest.setClassificationService(mockClassificationService);
		classUnderTest.setMasterDataCacheAccessService(mockMasterDataCacheAccessService);
		classUnderTest.setI18NService(mockI18NService);
		classUnderTest.setFlexibleSearchService(mockFlexibleSearchService);
		classUnderTest.setConfigurationProductUtil(mockConfigProductUtil);
	}

	@Test
	public void testClassificationService()
	{
		assertEquals(mockClassificationService, classUnderTest.getClassificationService());
	}

	@Test
	public void testFlexibleSearchService()
	{
		assertEquals(mockFlexibleSearchService, classUnderTest.getFlexibleSearchService());
	}

	@Test
	public void testCreateExternalConfiguration()
	{
		final CPSExternalConfiguration configuration = classUnderTest.createExternalConfiguration(variantCode, kbId);
		assertNotNull(configuration);
		assertEquals(kbId, configuration.getKbId());
		assertTrue(configuration.isComplete());
		assertTrue(configuration.isConsistent());
		final CPSExternalItem rootItem = configuration.getRootItem();
		assertNotNull(rootItem);
		assertNull(rootItem.getSubItems());
		final List<CPSExternalCharacteristic> rootItemCstics = rootItem.getCharacteristics();
		assertNotNull(rootItemCstics);
		assertEquals(1, rootItemCstics.size());
		final CPSExternalCharacteristic cpsExternalCharacteristic = rootItemCstics.get(0);
		assertEquals(csticKey, cpsExternalCharacteristic.getId());
		final List<CPSExternalValue> values = cpsExternalCharacteristic.getValues();
		assertNotNull(values);
		assertEquals(1, values.size());
		final CPSExternalValue cpsExternalValue = values.get(0);
		assertEquals(csticValue, cpsExternalValue.getValue());
	}

	@Test
	public void testCreateRootInstanceFromVariant()
	{
		final CPSExternalItem rootItem = classUnderTest.createExternalRootItem(variantCode);
		assertNotNull(rootItem);
		final CPSQuantity quantity = rootItem.getQuantity();
		assertNotNull(quantity);
		assertEquals(Double.valueOf(1), quantity.getValue());
		assertEquals(UNIT, quantity.getUnit());
		assertEquals(ExternalConfigurationFromVariantStrategyImpl.INSTANCE_ID_ROOT, rootItem.getId());
		final CPSExternalObjectKey objectKey = rootItem.getObjectKey();
		assertNotNull(objectKey);
		assertEquals(SapproductconfigruntimecpsConstants.ITEM_TYPE_MARA, objectKey.getType());
		assertEquals(ExternalConfigurationFromVariantStrategyImpl.DEFAULT_CLASS_TYPE, objectKey.getClassType());
		assertEquals(baseProductCode, objectKey.getId());
		assertEquals(ExternalConfigurationFromVariantStrategyImpl.AUTHOR_USER, rootItem.getObjectKeyAuthor());
		assertTrue(rootItem.isComplete());
		assertTrue(rootItem.isConsistent());
	}

	@Test
	public void testDetermineBaseProduct()
	{
		final ProductModel base = classUnderTest.determineBaseProduct(variantCode);
		assertEquals(mockBaseProductModel, base);
	}

	@Test(expected = IllegalStateException.class)
	public void testDetermineBaseProductNoVariant()
	{
		classUnderTest.determineBaseProduct(baseProductCode);
	}

	@Test
	public void testGetMasterDataCacheAccessService()
	{
		assertEquals(mockMasterDataCacheAccessService, classUnderTest.getMasterDataCacheAccessService());
	}

	@Test
	public void testIsFeatureRelated()
	{
		assertTrue(classUnderTest.isFeatureRelatedToCurrentProduct(mockFeatureRelated, kbId));
	}

	@Test
	public void testIsFeatureRelatedNotRelated()
	{
		assertFalse(classUnderTest.isFeatureRelatedToCurrentProduct(mockFeatureNotRelated, kbId));
	}

	@Test
	public void testGetI18NService()
	{
		assertEquals(mockI18NService, classUnderTest.getI18NService());
	}

	@Test
	public void testReadCharacteristicName()
	{
		assertEquals(csticKey, classUnderTest.readCharacteristicName(mockFeatureRelated));
	}

	@Test(expected = NullPointerException.class)
	public void testReadCharacteristicNameNoClassAttributeAssignment()
	{
		when(mockFeatureRelated.getClassAttributeAssignment()).thenReturn(null);
		classUnderTest.readCharacteristicName(mockFeatureRelated);
	}

	@Test(expected = NullPointerException.class)
	public void testReadCharacteristicNameNoClassificationAttribute()
	{
		when(mockClassAttributeAssignment.getClassificationAttribute()).thenReturn(null);
		classUnderTest.readCharacteristicName(mockFeatureRelated);
	}

	@Test
	public void testMapToCPSCharacteristics()
	{
		final CPSExternalCharacteristic characteristic = classUnderTest.mapToCPSCharacteristics(mockFeatureRelated,
				mockVariantModel);
		assertNotNull(characteristic);
		assertEquals(csticKey, characteristic.getId());
		final List<CPSExternalValue> values = characteristic.getValues();
		assertNotNull(values);
		assertTrue(values.size() > 0);
	}

	@Test
	public void testAddCPSCharacteristicValue()
	{
		final List<CPSExternalValue> values = new ArrayList<>();
		classUnderTest.addCPSCharacteristicValue(mockClassificationAttributeValue, values, csticKey, mockVariantModel);
		assertEquals(1, values.size());
		final CPSExternalValue externalValue = values.get(0);
		assertEquals(csticValue, externalValue.getValue());
	}

	@Test
	public void testAddCPSCharacteristicValueDouble()
	{
		final List<CPSExternalValue> values = new ArrayList<>();
		final Double numericValue = Double.valueOf(3);
		classUnderTest.addCPSCharacteristicValue(numericValue, values, csticKey, mockVariantModel);
		assertEquals(1, values.size());
		final CPSExternalValue externalValue = values.get(0);
		assertEquals(numericValue.toString(), externalValue.getValue());
	}

	@Test
	public void testAddCPSCharacteristicStringDouble()
	{
		final List<CPSExternalValue> values = new ArrayList<>();
		final String freeTextValue = "Huhu";
		classUnderTest.addCPSCharacteristicValue(freeTextValue, values, csticKey, mockVariantModel);
		assertEquals(1, values.size());
		final CPSExternalValue externalValue = values.get(0);
		assertEquals(freeTextValue, externalValue.getValue());
	}

	@Test(expected = IllegalStateException.class)
	public void testAddCPSCharacteristicValueCsticValueDoesNotContainCstic()
	{
		final List<CPSExternalValue> values = new ArrayList<>();
		when(mockClassificationAttributeValue.getCode()).thenReturn(csticValue);
		classUnderTest.addCPSCharacteristicValue(mockClassificationAttributeValue, values, csticKey, mockVariantModel);
	}

	@Test
	public void testFindAuthor()
	{
		final String author = classUnderTest.findAuthor(classificationValueIdentifier, productPk);
		assertEquals(AUTHOR_CONSTRAINT, author);
	}

	@Test
	public void testFindAuthorDontReturnNull()
	{
		when(mockProductFeature.getAuthor()).thenReturn(null);
		final String author = classUnderTest.findAuthor(classificationValueIdentifier, productPk);
		assertEquals(ExternalConfigurationFromVariantStrategyImpl.AUTHOR_USER, author);
	}


	@Test(expected = IllegalStateException.class)
	public void testFindAuthorNoProductFeatures()
	{
		featureModelList.clear();
		classUnderTest.findAuthor(classificationValueIdentifier, productPk);
	}

	@Test
	public void testDetermineCharacteristics()
	{
		final List<CPSExternalCharacteristic> cpsCharacteristics = classUnderTest.determineCharacteristics(kbId, mockVariantModel);
		assertNotNull(cpsCharacteristics);
		assertEquals(1, cpsCharacteristics.size());
	}

	@Test
	public void testDetermineCharacteristicsNoValues()
	{
		featureValueList.clear();
		final List<CPSExternalCharacteristic> cpsCharacteristics = classUnderTest.determineCharacteristics(kbId, mockVariantModel);
		assertNotNull(cpsCharacteristics);
		assertTrue(cpsCharacteristics.isEmpty());
	}

	@Test
	public void testMapToValueModel()
	{
		final Object valueModel = classUnderTest.mapToValueModel(mockFeatureValue);
		assertTrue(valueModel instanceof ClassificationAttributeValueModel);
		assertEquals(csticValueInClassificationSystem, ((ClassificationAttributeValueModel) valueModel).getCode());
	}

	@Test
	public void testMapToValueModelDouble()
	{
		final Double numericValue = Double.valueOf(3);
		when(mockFeatureValue.getValue()).thenReturn(numericValue);
		final Object valueModel = classUnderTest.mapToValueModel(mockFeatureValue);
		assertTrue(valueModel instanceof Double);
	}

	@Test
	public void testMapToValueModelString()
	{
		final String freeTextValue = "Huhu";
		when(mockFeatureValue.getValue()).thenReturn(freeTextValue);
		final Object valueModel = classUnderTest.mapToValueModel(mockFeatureValue);
		assertTrue(valueModel instanceof String);
	}


	@Test(expected = IllegalStateException.class)
	public void testMapToValueModelWrongFeatureType()
	{
		when(mockFeatureValue.getValue()).thenReturn(mockProductFeature);
		classUnderTest.mapToValueModel(mockFeatureValue);
	}

}
