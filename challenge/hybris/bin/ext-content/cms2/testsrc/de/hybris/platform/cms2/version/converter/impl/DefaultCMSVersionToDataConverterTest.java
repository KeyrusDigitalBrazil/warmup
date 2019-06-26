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
package de.hybris.platform.cms2.version.converter.impl;

import static java.util.Arrays.asList;
import static java.util.Locale.ENGLISH;
import static java.util.Locale.FRENCH;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.cmsitems.converter.AttributeContentConverter;
import de.hybris.platform.cms2.cmsitems.converter.AttributeStrategyConverterProvider;
import de.hybris.platform.cms2.common.service.CollectionHelper;
import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cms2.version.converter.attribute.data.VersionAttributeDescriptor;
import de.hybris.platform.cms2.version.converter.customattribute.CustomAttributeContentConverter;
import de.hybris.platform.cms2.version.converter.customattribute.CustomAttributeStrategyConverterProvider;
import de.hybris.platform.cms2.version.service.CMSVersionHelper;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.directpersistence.cache.SLDDataContainer;
import de.hybris.platform.persistence.audit.internal.LocalizedAttributesList;
import de.hybris.platform.persistence.audit.payload.PayloadSerializer;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCMSVersionToDataConverterTest
{

	private final String SUPER_COMPOSED_TYPE_CODE_0 = CMSItemModel._TYPECODE;
	private final String ATT_QUALIFIER_0 = "qualifier0";
	private final String ATT_QUALIFIER_1 = "qualifier1";
	private final String SUPER_ATT_QUALIFIER_0 = "super_qualifier0";
	private final String SUPER_ATT_QUALIFIER_1 = "super_qualifier1";
	private final PK LANGUAGE0_PK = PK.fromLong(Long.valueOf(123));
	private final PK LANGUAGE1_PK = PK.fromLong(Long.valueOf(456));
	private final String CUSTOM_ATTRIBUTE_QUALIFIER0 = "custom_qualifier0";
	private final String CUSTOM_ATTRIBUTE_QUALIFIER1 = "custom_qualifier1";
	private final String CUSTOM_ATTRIBUTE_CONVERTED_VALUE0 = "custom_attribute_value0";
	private final String CUSTOM_ATTRIBUTE_CONVERTED_VALUE1 = "custom_attribute_value1";

	@InjectMocks
	@Spy
	private DefaultCMSVersionToDataConverter versionConverter;
	@Mock
	private CommonI18NService commonI18NService;
	@Mock
	private ModelService modelService;
	@Mock
	private AttributeStrategyConverterProvider<VersionAttributeDescriptor> converterProvider;
	@Mock
	private CustomAttributeStrategyConverterProvider customConverterProvider;
	@Mock
	private CMSVersionHelper cmsVersionHelper;
	@Mock
	private CollectionHelper collectionHelper;
	@Mock
	private Predicate<AttributeDescriptorModel> isCollectionPredicate;
	@Mock
	private Predicate<AttributeDescriptorModel> isCollectionPredicateNegate;

	@Mock
	private PayloadSerializer payloadSerializer;

	@Mock
	private LanguageModel languageModel0;

	@Mock
	private LanguageModel languageModel1;
	private final Locale locale0 = ENGLISH;
	private final Locale locale1 = FRENCH;

	@Mock
	private AttributeDescriptorModel attributeDescriptor0;
	@Mock
	private AttributeDescriptorModel attributeDescriptor1;
	@Mock
	private AttributeDescriptorModel superAttributeDescriptor_0_0;
	@Mock
	private AttributeDescriptorModel superAttributeDescriptor_0_1;

	@Mock
	private TypeModel typeModel0; //for attributeDescriptor0
	@Mock
	private TypeModel mapTypeModel0; //for returntype property of attributeDescriptor0
	@Mock
	private TypeModel collectionTypeModel0; //for element type property of attributeDescriptor0

	@Mock
	private TypeModel typeModel1; //for attributeDescriptor1
	@Mock
	private TypeModel mapTypeModel1; //for returntype property of attributeDescriptor1

	@Mock
	private TypeModel typeModel_0_1; // for superAttributeDescriptor_0_1
	@Mock
	private TypeModel collectionTypeModel_0_1; // for element type property of superAttributeDescriptor_0_1

	@Mock
	private TypeModel typeModel_0_0; // for superAttributeDescriptor_0_0

	@Mock
	private VersionAttributeDescriptor versionAttributeDescriptor0; //for attributeDescriptor0

	@Mock
	private VersionAttributeDescriptor versionAttributeDescriptor1; //for attributeDescriptor1

	@Mock
	private VersionAttributeDescriptor versionAttributeDescriptor_0_0; //for superAttributeDescriptor_0_0

	@Mock
	private VersionAttributeDescriptor versionAttributeDescriptor_0_1; //for superAttributeDescriptor_0_1

	@Mock
	private AttributeContentConverter attributeContentConverter0; //for attributeDescriptor0
	@Mock
	private AttributeContentConverter attributeContentConverter1; //for attributeDescriptor1
	@Mock
	private AttributeContentConverter attributeContentConverter_0_0; //for superAttributeDescriptor_0_0
	@Mock
	private AttributeContentConverter attributeContentConverter_0_1; //for superAttributeDescriptor_0_1

	@Mock
	private CustomAttributeContentConverter customAttributeContentConverter0;
	@Mock
	private CustomAttributeContentConverter customAttributeContentConverter1;
	@Mock
	private Predicate<ItemModel> customAttributeContentConverterPredicate0;
	@Mock
	private Predicate<ItemModel> customAttributeContentConverterPredicate1;

	@Mock
	private TypeService typeService;
	@Mock
	private ComposedTypeModel composedTypeModel;
	@Mock
	private ComposedTypeModel superComposedTypeModel;

	@Mock
	private CMSItemModel itemModel;


	/**
	 * The structure: //
	 * 	superComposedTypeModel -> //
	 * 			|  - superAttributeDescriptor_0_0 qualifier: SUPER_ATT_QUALIFIER_0, type: simple //
	 * 			|  - superAttributeDescriptor_0_1 qualifier: SUPER_ATT_QUALIFIER_1, type: collection //
	 * 			| //
	 * 	composedTypeModel -> //
	 * 				- attributeDescriptor0 qualifier: ATT_QUALIFIER_0, type: localized collection //
	 * 			   - attributeDescriptor1 qualifier: ATT_QUALIFIER_1, type: localized //
	 */
	@Before
	public void setup()
	{
		// superComposedTypeModel
		when(superAttributeDescriptor_0_0.getQualifier()).thenReturn(SUPER_ATT_QUALIFIER_0);
		when(superAttributeDescriptor_0_1.getQualifier()).thenReturn(SUPER_ATT_QUALIFIER_1);
		when(superComposedTypeModel.getCode()).thenReturn(SUPER_COMPOSED_TYPE_CODE_0);
		when(superComposedTypeModel.getDeclaredattributedescriptors())
				.thenReturn(asList(superAttributeDescriptor_0_0, superAttributeDescriptor_0_1));

		// composedTypeModel
		when(attributeDescriptor0.getQualifier()).thenReturn(ATT_QUALIFIER_0);
		when(attributeDescriptor1.getQualifier()).thenReturn(ATT_QUALIFIER_1);
		when(composedTypeModel.getDeclaredattributedescriptors()).thenReturn(asList(attributeDescriptor0, attributeDescriptor1));
		when(composedTypeModel.getAllSuperTypes()).thenReturn(asList(superComposedTypeModel));

		// typeService
		when(typeService.isAssignableFrom(CMSItemModel._TYPECODE, SUPER_COMPOSED_TYPE_CODE_0)).thenReturn(true);
		when(typeService.getComposedTypeForClass(itemModel.getClass())).thenReturn(composedTypeModel);

		// Custom converter provider and converters
		when(customConverterProvider.getConverters(itemModel))
				.thenReturn(Arrays.asList(customAttributeContentConverter0, customAttributeContentConverter1));
		when(customAttributeContentConverter0.getQualifier()).thenReturn(CUSTOM_ATTRIBUTE_QUALIFIER0);
		when(customAttributeContentConverter1.getQualifier()).thenReturn(CUSTOM_ATTRIBUTE_QUALIFIER1);
		when(customAttributeContentConverter0.getConstrainedBy()).thenReturn(customAttributeContentConverterPredicate0);
		when(customAttributeContentConverter1.getConstrainedBy()).thenReturn(customAttributeContentConverterPredicate1);
		when(customAttributeContentConverterPredicate0.test(itemModel)).thenReturn(true);
		when(customAttributeContentConverterPredicate1.test(itemModel)).thenReturn(false);
		when(customAttributeContentConverter0.convertModelToData(itemModel)).thenReturn(CUSTOM_ATTRIBUTE_CONVERTED_VALUE0);
		when(customAttributeContentConverter1.convertModelToData(itemModel)).thenReturn(CUSTOM_ATTRIBUTE_CONVERTED_VALUE1);


		//List of languages
		when(commonI18NService.getAllLanguages()).thenReturn(Arrays.asList(languageModel0, languageModel1));
		when(commonI18NService.getLocaleForLanguage(languageModel0)).thenReturn(locale0);
		when(commonI18NService.getLocaleForLanguage(languageModel1)).thenReturn(locale1);
		when(languageModel0.getPk()).thenReturn(LANGUAGE0_PK);
		when(languageModel1.getPk()).thenReturn(LANGUAGE1_PK);

		// Payload serializer
		when(payloadSerializer.serialize(any())).thenReturn("");

		// Version attribute for attribute descriptors

		// attributeDescriptor0 (localized collection)
		when(attributeDescriptor0.getLocalized()).thenReturn(true); // localized
		when(typeModel0.getItemtype()).thenReturn("CollectionType"); // collection
		when(attributeDescriptor0.getAttributeType()).thenReturn(typeModel0);
		when(typeModel0.getProperty("returntype")).thenReturn(mapTypeModel0);
		when(mapTypeModel0.getProperty("elementtype")).thenReturn(collectionTypeModel0);
		doReturn(versionAttributeDescriptor0).when(versionConverter).buildVersionAttributeDescriptor(collectionTypeModel0,
				attributeDescriptor0);
		// values with locales
		when(modelService.getAttributeValue(itemModel, ATT_QUALIFIER_0, locale0)).thenReturn(Arrays.asList(1, 2, 3));
		when(modelService.getAttributeValue(itemModel, ATT_QUALIFIER_0, locale1)).thenReturn(Arrays.asList(4, 5, 6));
		// converter
		when(converterProvider.getContentConverter(versionAttributeDescriptor0)).thenReturn(attributeContentConverter0);
		when(attributeContentConverter0.convertModelToData(versionAttributeDescriptor0, 1)).thenReturn(1);
		when(attributeContentConverter0.convertModelToData(versionAttributeDescriptor0, 2)).thenReturn(2);
		when(attributeContentConverter0.convertModelToData(versionAttributeDescriptor0, 3)).thenReturn(3);
		when(attributeContentConverter0.convertModelToData(versionAttributeDescriptor0, 4)).thenReturn(4);
		when(attributeContentConverter0.convertModelToData(versionAttributeDescriptor0, 5)).thenReturn(5);
		when(attributeContentConverter0.convertModelToData(versionAttributeDescriptor0, 6)).thenReturn(6);


		// attributeDescriptor1 (simple localized)
		when(attributeDescriptor1.getLocalized()).thenReturn(true); // localized
		when(typeModel1.getItemtype()).thenReturn("AnotherType"); // not collection
		when(attributeDescriptor1.getAttributeType()).thenReturn(typeModel1);
		when(typeModel1.getProperty("returntype")).thenReturn(mapTypeModel1);
		doReturn(versionAttributeDescriptor1).when(versionConverter).buildVersionAttributeDescriptor(mapTypeModel1,
				attributeDescriptor1);
		// values
		when(modelService.getAttributeValue(itemModel, ATT_QUALIFIER_1, locale0)).thenReturn("LOCALIZED VALUE 0");
		when(modelService.getAttributeValue(itemModel, ATT_QUALIFIER_1, locale1)).thenReturn("LOCALIZED VALUE 1");
		// converter
		when(converterProvider.getContentConverter(versionAttributeDescriptor1)).thenReturn(attributeContentConverter1);
		when(attributeContentConverter1.convertModelToData(versionAttributeDescriptor1, "LOCALIZED VALUE 0"))
				.thenReturn("LOCALIZED VALUE 0");
		when(attributeContentConverter1.convertModelToData(versionAttributeDescriptor1, "LOCALIZED VALUE 1"))
				.thenReturn("LOCALIZED VALUE 1");

		// superAttributeDescriptor_0_0 (simple)
		when(superAttributeDescriptor_0_0.getLocalized()).thenReturn(false); // not localized
		when(typeModel_0_0.getItemtype()).thenReturn("AnotherType"); // not collection
		when(superAttributeDescriptor_0_0.getAttributeType()).thenReturn(typeModel_0_0);
		doReturn(versionAttributeDescriptor_0_0).when(versionConverter).buildVersionAttributeDescriptor(typeModel_0_0,
				superAttributeDescriptor_0_0);
		// value
		when(modelService.getAttributeValue(itemModel, SUPER_ATT_QUALIFIER_0)).thenReturn("SIMPLE VALUE");
		// converter
		when(converterProvider.getContentConverter(versionAttributeDescriptor_0_0)).thenReturn(attributeContentConverter_0_0);
		when(attributeContentConverter_0_0.convertModelToData(versionAttributeDescriptor_0_0, "SIMPLE VALUE"))
				.thenReturn("SIMPLE VALUE");

		// superAttributeDescriptor_0_1(collection)
		when(superAttributeDescriptor_0_1.getLocalized()).thenReturn(false); // not localized
		when(typeModel_0_1.getItemtype()).thenReturn("CollectionType"); // collection
		when(superAttributeDescriptor_0_1.getAttributeType()).thenReturn(typeModel_0_1);
		when(typeModel_0_1.getProperty("elementtype")).thenReturn(collectionTypeModel_0_1);
		doReturn(versionAttributeDescriptor_0_1).when(versionConverter).buildVersionAttributeDescriptor(collectionTypeModel_0_1,
				superAttributeDescriptor_0_1);
		// value
		when(modelService.getAttributeValue(itemModel, SUPER_ATT_QUALIFIER_1)).thenReturn(Arrays.asList(7, 8, 9));
		// converter
		when(converterProvider.getContentConverter(versionAttributeDescriptor_0_1)).thenReturn(attributeContentConverter_0_1);
		when(attributeContentConverter_0_1.convertModelToData(versionAttributeDescriptor_0_1, 7)).thenReturn(7);
		when(attributeContentConverter_0_1.convertModelToData(versionAttributeDescriptor_0_1, 8)).thenReturn(8);
		when(attributeContentConverter_0_1.convertModelToData(versionAttributeDescriptor_0_1, 9)).thenReturn(9);

		//CMSVersionUtils
		when(cmsVersionHelper.getSerializableAttributes(itemModel)).thenReturn(Arrays.asList(
				attributeDescriptor0, attributeDescriptor1,
				superAttributeDescriptor_0_0, superAttributeDescriptor_0_1));

		// Is collection predicate
		when(isCollectionPredicate.test(attributeDescriptor1)).thenReturn(false);
		when(isCollectionPredicate.test(superAttributeDescriptor_0_0)).thenReturn(false);
		when(isCollectionPredicate.test(attributeDescriptor0)).thenReturn(true);
		when(isCollectionPredicate.test(superAttributeDescriptor_0_1)).thenReturn(true);
		when(isCollectionPredicate.negate()).thenReturn(isCollectionPredicateNegate);
		when(isCollectionPredicateNegate.test(attributeDescriptor0)).thenReturn(false);
		when(isCollectionPredicateNegate.test(superAttributeDescriptor_0_1)).thenReturn(false);
		when(isCollectionPredicateNegate.test(attributeDescriptor1)).thenReturn(true);
		when(isCollectionPredicateNegate.test(superAttributeDescriptor_0_0)).thenReturn(true);
	}

	protected Map<String, Object> getConvertedItemModelData()
	{
		final Map<String, Object> result = new HashMap<>();
		final LocalizedAttributesList localizedAttributesForCollection = new LocalizedAttributesList();
		final SLDDataContainer.AttributeValue att1 = new SLDDataContainer.AttributeValue(ATT_QUALIFIER_0, Arrays.asList(1, 2, 3),
				LANGUAGE0_PK);
		final SLDDataContainer.AttributeValue att2 = new SLDDataContainer.AttributeValue(ATT_QUALIFIER_0, Arrays.asList(4, 5, 6),
				LANGUAGE1_PK);
		localizedAttributesForCollection.add(att2);
		localizedAttributesForCollection.add(att1);
		result.put(ATT_QUALIFIER_0, localizedAttributesForCollection);

		final LocalizedAttributesList localizedAttributesForSimpleValues = new LocalizedAttributesList();
		final SLDDataContainer.AttributeValue att3 = new SLDDataContainer.AttributeValue(ATT_QUALIFIER_1, "LOCALIZED VALUE 0",
				LANGUAGE0_PK);
		final SLDDataContainer.AttributeValue att4 = new SLDDataContainer.AttributeValue(ATT_QUALIFIER_1, "LOCALIZED VALUE 1",
				LANGUAGE1_PK);
		localizedAttributesForSimpleValues.add(att4);
		localizedAttributesForSimpleValues.add(att3);
		result.put(ATT_QUALIFIER_1, localizedAttributesForSimpleValues);

		final SLDDataContainer.AttributeValue att5 = new SLDDataContainer.AttributeValue(SUPER_ATT_QUALIFIER_0, "SIMPLE VALUE");
		result.put(SUPER_ATT_QUALIFIER_0, att5);

		final SLDDataContainer.AttributeValue att6 = new SLDDataContainer.AttributeValue(SUPER_ATT_QUALIFIER_1,
				Arrays.asList(7, 8, 9));
		result.put(SUPER_ATT_QUALIFIER_1, att6);

		return result;
	}

	protected Map<String, Object> getCustomConvertedItemModelData()
	{
		final Map<String, Object> result = new HashMap<>();
		final SLDDataContainer.AttributeValue attr = new SLDDataContainer.AttributeValue(CUSTOM_ATTRIBUTE_QUALIFIER0,
				CUSTOM_ATTRIBUTE_CONVERTED_VALUE0);
		result.put(CUSTOM_ATTRIBUTE_QUALIFIER0, attr);
		return result;
	}

	@Test
	public void shouldConvertBasicAndCustomAttributeValuesAndMergeThemAndSerialize()
	{
		// WHEN
		versionConverter.convert(itemModel);

		// THEN
		verify(versionConverter).getConvertedAttributeValues(itemModel);
		verify(versionConverter).getCustomConvertedAttributeValues(itemModel);
		verify(collectionHelper).mergeMaps(any(), any(), any());
		verify(payloadSerializer).serialize(any());
	}

	@Test
	public void shouldConvertLocalizedCollectionAttribute()
	{
		// WHEN
		final Map<String, Object> result = versionConverter.getConvertedAttributeValues(itemModel);

		// THEN
		final Map<String, Object> modelResult = getConvertedItemModelData();
		final LocalizedAttributesList attribute0 = (LocalizedAttributesList) result.get(ATT_QUALIFIER_0);
		final LocalizedAttributesList attributeModelResult0 = (LocalizedAttributesList) modelResult.get(ATT_QUALIFIER_0);
		assertEquals(attribute0.size(), attributeModelResult0.size());
		assertTrue(attributeModelResult0.containsAll(attribute0));
	}

	@Test
	public void shouldConvertLocalizedSimpleAttribute()
	{
		// WHEN
		final Map<String, Object> result = versionConverter.getConvertedAttributeValues(itemModel);

		// THEN
		final Map<String, Object> modelResult = getConvertedItemModelData();
		final LocalizedAttributesList attribute1 = (LocalizedAttributesList) result.get(ATT_QUALIFIER_1);
		final LocalizedAttributesList attributeModelResult1 = (LocalizedAttributesList) modelResult.get(ATT_QUALIFIER_1);

		assertEquals(attribute1.size(), attributeModelResult1.size());
		assertTrue(attributeModelResult1.containsAll(attribute1));
	}

	@Test
	public void shouldConvertSimpleAttribute()
	{
		// WHEN
		final Map<String, Object> result = versionConverter.getConvertedAttributeValues(itemModel);

		// THEN
		final Map<String, Object> modelResult = getConvertedItemModelData();
		final SLDDataContainer.AttributeValue superAttribute0 = (SLDDataContainer.AttributeValue) result.get(SUPER_ATT_QUALIFIER_0);
		final SLDDataContainer.AttributeValue superAttributeModelResult0 = (SLDDataContainer.AttributeValue) modelResult
				.get(SUPER_ATT_QUALIFIER_0);
		assertEquals(superAttribute0, superAttributeModelResult0);
	}

	@Test
	public void shouldConvertSimpleCollectionAttribute()
	{
		// WHEN
		final Map<String, Object> result = versionConverter.getConvertedAttributeValues(itemModel);

		// THEN
		final Map<String, Object> modelResult = getConvertedItemModelData();
		final SLDDataContainer.AttributeValue superAttribute1 = (SLDDataContainer.AttributeValue) result.get(SUPER_ATT_QUALIFIER_1);
		final SLDDataContainer.AttributeValue superAttributeModelResult1 = (SLDDataContainer.AttributeValue) modelResult
				.get(SUPER_ATT_QUALIFIER_1);
		assertEquals(superAttribute1, superAttributeModelResult1);
	}

	@Test
	public void shouldConvertCustomItemAttributes()
	{
		// WHEN
		final Map<String, Object> result = versionConverter.getCustomConvertedAttributeValues(itemModel);

		// THEN
		assertEquals(result, getCustomConvertedItemModelData());
	}
}





















