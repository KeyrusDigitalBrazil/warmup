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
package de.hybris.platform.cms2.version.populator;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.cmsitems.converter.AttributeContentConverter;
import de.hybris.platform.cms2.cmsitems.converter.AttributeStrategyConverterProvider;
import de.hybris.platform.cms2.common.service.impl.DefaultCollectionHelper;
import de.hybris.platform.cms2.model.CMSVersionModel;
import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cms2.servicelayer.daos.CMSItemDao;
import de.hybris.platform.cms2.version.converter.attribute.data.VersionPayloadDescriptor;
import de.hybris.platform.cms2.version.converter.customattribute.CustomAttributeContentConverter;
import de.hybris.platform.cms2.version.converter.customattribute.CustomAttributeStrategyConverterProvider;
import de.hybris.platform.cms2.version.service.CMSVersionHelper;
import de.hybris.platform.cms2.version.service.CMSVersionSessionContextProvider;
import de.hybris.platform.core.enums.TypeOfCollectionEnum;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.CollectionTypeModel;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.persistence.audit.payload.PayloadDeserializer;
import de.hybris.platform.persistence.audit.payload.json.AuditPayload;
import de.hybris.platform.persistence.audit.payload.json.LocalizedTypedValue;
import de.hybris.platform.persistence.audit.payload.json.LocalizedValue;
import de.hybris.platform.persistence.audit.payload.json.TypedValue;
import de.hybris.platform.persistence.audit.payload.json.ValueType;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CMSVersionToItemModelPopulatorTest
{
	private final String VERSION_ITEM_UID = "version_item_uid";
	private final String VERSION_ITEM_TYPE_CODE = "version_item_type";
	private final String VERSION_PAYLOAD = "";

	private final String ATTR_DEFAULT_TYPE = "defaultType";
	private final String ATTR_CUSTOM_TYPE = "de.hybris.platform.cms2.version.converter.customattribute.CMSVersionCustomAttribute";
	private final String ATTR_EN_LANGUAGE = "en";
	private final String ATTR_DE_LANGUAGE = "de";

	//Simple attribute
	private final String ATTR1_NAME = "attr1";
	private final String ATTR1_VALUE = "1";
	private final String ATTR1_COLLECTION_TYPE = "";

	//List attribute
	private final String ATTR2_NAME = "attr2";
	private final String ATTR2_COLLECTION_TYPE = "1";
	private final String ATTR2_VALUE_1 = "1";
	private final String ATTR2_VALUE_2 = "2";
	private final String ATTR2_VALUE_3 = "3";

	//Set attribute
	private final String ATTR3_NAME = "attr3";
	private final String ATTR3_COLLECTION_TYPE = "2";
	private final String ATTR3_VALUE_1 = "4";
	private final String ATTR3_VALUE_2 = "5";
	private final String ATTR3_VALUE_3 = "4";

	//Sorted set attribute
	private final String ATTR4_NAME = "attr4";
	private final String ATTR4_COLLECTION_TYPE = "3";
	private final String ATTR4_VALUE_1 = "8";
	private final String ATTR4_VALUE_2 = "7";
	private final String ATTR4_VALUE_3 = "8";

	//Custom simple attribute
	private final String ATTR5_NAME = "attr5";
	private final String ATTR5_VALUE = "123";
	private final String ATTR5_COLLECTION_TYPE = "";

	//Simple localized attribute
	private final String ATTR6_NAME = "attr6";
	private final String ATTR6_COLLECTION_TYPE = "";
	private final String ATTR6_EN_VALUE = "cde";
	private final String ATTR6_DE_VALUE = "abc";

	//Localized collection attribute
	private final String ATTR7_NAME = "attr7";
	private final String ATTR7_COLLECTION_TYPE = "1";
	private final String ATTR7_EN_VALUE_1 = "cde";
	private final String ATTR7_EN_VALUE_2 = "def";
	private final String ATTR7_DE_VALUE_1 = "abc";
	private final String ATTR7_DE_VALUE_2 = "cfg";

	//Custom collection attribute
	private final String ATTR8_NAME = "attr8";
	private final String ATTR8_VALUE_1 = "123";
	private final String ATTR8_VALUE_2 = "234";
	private final String ATTR8_COLLECTION_TYPE = "1";

	//Collection attribute that is NULL
	private final String ATTR_NULL_NAME = "attrNull";
	private final Locale enLocale = Locale.ENGLISH;
	private final Locale deLocale = Locale.GERMAN;
	@InjectMocks
	@Spy
	private CMSVersionToItemModelPopulator modelPopulator;
	@Mock
	private CMSVersionModel version;
	@Mock
	private AuditPayload auditPayload;
	@Mock
	private ModelService modelService;
	@Mock
	private CMSItemDao itemDao;
	@Mock
	private LanguageModel enLanguageModel;
	@Mock
	private CustomAttributeContentConverter customSimpleAttributeContentConverter;
	@Mock
	private CustomAttributeContentConverter customCollectionAttributeContentConverter;
	@Mock
	private DefaultCollectionHelper collectionHelper;
	@Mock
	private CMSVersionSessionContextProvider cmsVersionSessionContextProvider;
	@Mock
	private CommonI18NService commonI18NService;
	@Mock
	private AttributeStrategyConverterProvider<String> converterProvider;
	@Mock
	private AttributeContentConverter<String> defaultAttributeConverter;
	@Mock
	private CMSVersionHelper cmsVersionHelper;
	@Mock
	private LanguageModel deLanguageModel;
	@Mock
	private CMSItemModel resultCMSItemModel;
	@Mock
	private AttributeDescriptorModel nullifiedCollectionAttribute;
	@Mock
	private TypeModel nullifiedCollectionAttributeTypeModel;
	@Mock
	private Predicate<AttributeDescriptorModel> isCollectionPredicate;
	@Mock
	private Predicate<AttributeDescriptorModel> isCollectionPredicateNegate;

	@Mock
	private CustomAttributeStrategyConverterProvider customConverterProvider;

	@Mock
	private PayloadDeserializer payloadDeserializer;



	@Before
	public void setup()
	{
		/*
		 * Please see the payload structure in ExampleVersionToModelConverterPayload.json
		 * and update the json file when you change the test.
		 */

		// Version
		when(version.getItemUid()).thenReturn(VERSION_ITEM_UID);
		when(version.getItemTypeCode()).thenReturn(VERSION_ITEM_TYPE_CODE);
		when(version.getPayload()).thenReturn(VERSION_PAYLOAD);

		// ModelService
		when(modelService.create(VERSION_ITEM_TYPE_CODE)).thenReturn(resultCMSItemModel);

		//Session Context Provider
		doNothing().when(cmsVersionSessionContextProvider).addGeneratedItemToCache(resultCMSItemModel, version);

		//Collection helper
		when(collectionHelper.mergeMaps(any(), any(), any())).thenCallRealMethod();

		//Attribute Descriptor Helper
		when(cmsVersionHelper.getSerializableAttributes(resultCMSItemModel)).thenReturn(Arrays.asList(
				nullifiedCollectionAttribute));
		//Is collection predicate
		when(isCollectionPredicate.negate()).thenReturn(isCollectionPredicateNegate);
		when(isCollectionPredicate.test(nullifiedCollectionAttribute)).thenReturn(true);
		when(isCollectionPredicateNegate.test(nullifiedCollectionAttribute)).thenReturn(false);

		//Nullified collection attribute
		when(nullifiedCollectionAttribute.getQualifier()).thenReturn(ATTR_NULL_NAME);
		when(nullifiedCollectionAttribute.getAttributeType()).thenReturn(nullifiedCollectionAttributeTypeModel);
		when(nullifiedCollectionAttributeTypeModel.getProperty(CollectionTypeModel.TYPEOFCOLLECTION)).thenReturn(
				TypeOfCollectionEnum.LIST);
		when(modelService.getAttributeValue(resultCMSItemModel, ATTR_NULL_NAME)).thenReturn(null);

		//AuditPayload
		when(payloadDeserializer.deserialize(VERSION_PAYLOAD)).thenReturn(auditPayload);
		when(auditPayload.getAttributes()).thenReturn(payloadAttributes());
		when(auditPayload.getLocAttributes()).thenReturn(getLocPayloadAttributes());

		//ConverterProvider
		when(converterProvider.getContentConverter(ATTR_DEFAULT_TYPE)).thenReturn(defaultAttributeConverter);

		//Default Attribute converter
		doAnswer(answer ->
		{
			final Object[] args = answer.getArguments();
			final VersionPayloadDescriptor versionPayloadDescriptor = (VersionPayloadDescriptor) args[1];
			return versionPayloadDescriptor.getValue();
		}).when(defaultAttributeConverter).convertDataToModel(any(), any(VersionPayloadDescriptor.class));

		//Is attribute custom or not
		doReturn(false).when(modelPopulator).isCustomAttributeDataType(ATTR_DEFAULT_TYPE);
		doReturn(true).when(modelPopulator).isCustomAttributeDataType(ATTR_CUSTOM_TYPE);

		//Language
		when(commonI18NService.getLanguage(ATTR_EN_LANGUAGE)).thenReturn(enLanguageModel);
		when(commonI18NService.getLanguage(ATTR_DE_LANGUAGE)).thenReturn(deLanguageModel);
		when(commonI18NService.getLocaleForLanguage(enLanguageModel)).thenReturn(enLocale);
		when(commonI18NService.getLocaleForLanguage(deLanguageModel)).thenReturn(deLocale);

		//Custom converters
		when(customConverterProvider.getConverters(resultCMSItemModel))
				.thenReturn(Arrays.asList(customSimpleAttributeContentConverter, customCollectionAttributeContentConverter));
		when(customSimpleAttributeContentConverter.getQualifier()).thenReturn(ATTR5_NAME);
		when(customCollectionAttributeContentConverter.getQualifier()).thenReturn(ATTR8_NAME);

		doAnswer(answer ->
		{
			final Object[] args = answer.getArguments();
			return args[1];
		}).when(customSimpleAttributeContentConverter).populateItemModel(any(ItemModel.class), any(String.class));

		doAnswer(answer ->
		{
			final Object[] args = answer.getArguments();
			return args[1];
		}).when(customCollectionAttributeContentConverter).populateItemModel(any(ItemModel.class), any(String.class));
	}

	protected Map<String, TypedValue> payloadAttributes()
	{
		final Map<String, TypedValue> result = new HashMap<>();

		result.put(ATTR1_NAME, getAttributeTypedValue(ATTR_DEFAULT_TYPE, ATTR1_COLLECTION_TYPE, Arrays.asList(ATTR1_VALUE)));
		result.put(ATTR2_NAME, getAttributeTypedValue(ATTR_DEFAULT_TYPE, ATTR2_COLLECTION_TYPE,
				Arrays.asList(ATTR2_VALUE_1, ATTR2_VALUE_2, ATTR2_VALUE_3)));
		result.put(ATTR3_NAME, getAttributeTypedValue(ATTR_DEFAULT_TYPE, ATTR3_COLLECTION_TYPE,
				Arrays.asList(ATTR3_VALUE_1, ATTR3_VALUE_2, ATTR3_VALUE_3)));
		result.put(ATTR4_NAME, getAttributeTypedValue(ATTR_DEFAULT_TYPE, ATTR4_COLLECTION_TYPE,
				Arrays.asList(ATTR4_VALUE_1, ATTR4_VALUE_2, ATTR4_VALUE_3)));
		result.put(ATTR5_NAME, getAttributeTypedValue(ATTR_CUSTOM_TYPE, ATTR5_COLLECTION_TYPE, Arrays.asList(ATTR5_VALUE)));
		result.put(ATTR8_NAME,
				getAttributeTypedValue(ATTR_CUSTOM_TYPE, ATTR8_COLLECTION_TYPE, Arrays.asList(ATTR8_VALUE_1, ATTR8_VALUE_2)));

		return result;
	}

	protected Map<String, LocalizedTypedValue> getLocPayloadAttributes()
	{
		final Map<String, LocalizedTypedValue> result = new HashMap<>();

		result.put(ATTR6_NAME,
				getLocAttributeTypedValue(ATTR_DEFAULT_TYPE, ATTR6_COLLECTION_TYPE, new HashMap<String, List<String>>()
				{
					{
						put(ATTR_EN_LANGUAGE, Arrays.asList(ATTR6_EN_VALUE));
						put(ATTR_DE_LANGUAGE, Arrays.asList(ATTR6_DE_VALUE));
					}
				}));

		result.put(ATTR7_NAME,
				getLocAttributeTypedValue(ATTR_DEFAULT_TYPE, ATTR7_COLLECTION_TYPE, new HashMap<String, List<String>>()
				{
					{
						put(ATTR_EN_LANGUAGE, Arrays.asList(ATTR7_EN_VALUE_1, ATTR7_EN_VALUE_2));
						put(ATTR_DE_LANGUAGE, Arrays.asList(ATTR7_DE_VALUE_1, ATTR7_DE_VALUE_2));
					}
				}));

		return result;
	}

	protected LocalizedTypedValue getLocAttributeTypedValue(final String attrType, final String collectionType,
			final Map<String, List<String>> locValues)
	{
		final ValueType vt1;
		if (collectionType.isEmpty())
		{
			vt1 = ValueType.newType(attrType);
		}
		else
		{
			vt1 = ValueType.newCollectionType(attrType, collectionType);
		}

		final List<LocalizedValue> values = locValues.entrySet().stream().map(entry ->
		{
			return new LocalizedValue(entry.getKey(), entry.getValue());
		}).collect(toList());

		return new LocalizedTypedValue(vt1, values);
	}


	protected TypedValue getAttributeTypedValue(final String attrType, final String collectionType, final List<String> values)
	{
		final ValueType vt1;
		if (collectionType.isEmpty())
		{
			vt1 = ValueType.newType(attrType);
		}
		else
		{
			vt1 = ValueType.newCollectionType(attrType, collectionType);
		}

		return new TypedValue(vt1, values);
	}

	@Test
	public void shouldPopulateSimpleAttribute()
	{
		// WHEN
		modelPopulator.populate(auditPayload, resultCMSItemModel);

		// THEN
		// - attr 1
		verify(modelService).setAttributeValue(resultCMSItemModel, ATTR1_NAME, ATTR1_VALUE);
	}

	@Test
	public void shouldPopulateListAttribute()
	{
		// WHEN
		modelPopulator.populate(auditPayload, resultCMSItemModel);

		// THEN
		// - attr 2
		verify(modelService)
				.setAttributeValue(resultCMSItemModel, ATTR2_NAME, Arrays.asList(ATTR2_VALUE_1, ATTR2_VALUE_2, ATTR2_VALUE_3));
	}

	@Test
	public void shouldPopulateSetAttribute()
	{
		// WHEN
		modelPopulator.populate(auditPayload, resultCMSItemModel);

		// THEN
		// - attr 3
		verify(modelService).setAttributeValue(eq(resultCMSItemModel), eq(ATTR3_NAME), argThat(
				Matchers.containsInAnyOrder(ATTR3_VALUE_1, ATTR3_VALUE_2))); // ATTR3_VALUE_3 is removed due to collection type set
	}

	@Test
	public void shouldPopulateSortedSetAttribute()
	{
		// WHEN
		modelPopulator.populate(auditPayload, resultCMSItemModel);

		// THEN
		// - attr 4
		verify(modelService).setAttributeValue(eq(resultCMSItemModel), eq(ATTR4_NAME), argThat(
				Matchers.contains(ATTR4_VALUE_2, ATTR4_VALUE_1))); // ATTR4_VALUE_3 is removed due to collection type sorted set
	}

	@Test
	public void shouldPopulateCustomSimpleAttribute()
	{
		// WHEN
		modelPopulator.populate(auditPayload, resultCMSItemModel);

		// THEN
		// - attr 5
		verify(customSimpleAttributeContentConverter).populateItemModel(resultCMSItemModel, ATTR5_VALUE);
	}

	@Test
	public void shouldPopulateCustomCollectionAttribute()
	{
		// WHEN
		modelPopulator.populate(auditPayload, resultCMSItemModel);

		// THEN
		// - attr 8
		verify(customCollectionAttributeContentConverter, times(2)).populateItemModel(eq(resultCMSItemModel), argThat(Matchers
				.anyOf(containsString(ATTR8_VALUE_1), containsString(ATTR8_VALUE_2))));
	}

	@Test
	public void shouldPopulateLocalizedSimpleAttribute()
	{
		// WHEN
		modelPopulator.populate(auditPayload, resultCMSItemModel);

		// THEN
		// - attr 6
		verify(modelService)
				.setAttributeValue(eq(resultCMSItemModel), eq(ATTR6_NAME), argThat(Matchers.hasEntry(enLocale, ATTR6_EN_VALUE)));
		verify(modelService)
				.setAttributeValue(eq(resultCMSItemModel), eq(ATTR6_NAME), argThat(Matchers.hasEntry(deLocale, ATTR6_DE_VALUE)));
	}

	@Test
	public void shouldPopulateLocalizedCollectionAttribute()
	{
		// WHEN
		modelPopulator.populate(auditPayload, resultCMSItemModel);

		// THEN
		// - attr 7
		verify(modelService).setAttributeValue(eq(resultCMSItemModel), eq(ATTR7_NAME), argThat(Matchers.hasEntry(enLocale,
				Arrays.asList(ATTR7_EN_VALUE_1, ATTR7_EN_VALUE_2))));
		verify(modelService).setAttributeValue(eq(resultCMSItemModel), eq(ATTR7_NAME), argThat(Matchers.hasEntry(deLocale,
				Arrays.asList(ATTR7_DE_VALUE_1, ATTR7_DE_VALUE_2))));
	}

	@Test
	public void shouldPopulateEmptyCollectionAttributeThatDoesNotExistsInAuditPayload()
	{
		// WHEN
		modelPopulator.populate(auditPayload, resultCMSItemModel);

		// THEN
		// - attrNull
		assertThat(auditPayload.getAttributes().containsKey(ATTR_NULL_NAME), is(false));
		verify(modelService).setAttributeValue(eq(resultCMSItemModel), eq(ATTR_NULL_NAME), argThat(Matchers.emptyCollectionOf(ArrayList.class)));
	}
}
