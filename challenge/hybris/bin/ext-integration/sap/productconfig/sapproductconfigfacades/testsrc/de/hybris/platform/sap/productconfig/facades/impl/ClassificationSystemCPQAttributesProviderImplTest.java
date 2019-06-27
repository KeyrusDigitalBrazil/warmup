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
package de.hybris.platform.sap.productconfig.facades.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeValueModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemVersionModel;
import de.hybris.platform.classification.ClassificationSystemService;
import de.hybris.platform.commercefacades.product.converters.populator.ImagePopulator;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.sap.productconfig.facades.CPQImageFormatMapping;
import de.hybris.platform.sap.productconfig.facades.CPQImageType;
import de.hybris.platform.sap.productconfig.facades.UiType;
import de.hybris.platform.sap.productconfig.facades.UiTypeFinder;
import de.hybris.platform.sap.productconfig.facades.ValueFormatTranslator;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticValueModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.services.impl.ClassificationSystemCPQAttributesContainer;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ClassificationSystemCPQAttributesProviderImplTest
{
	private static final String CLASSIFICATION_EXPECTED = "Classification expected";
	private static final String NO_CLASSIFICATION_EXPECTED = "No classification expected";
	private static final String MODEL_LONG_TEXT = "model long text 123";
	private static final String HYBRIS_LONG_TEXT = "hybris long text 123";

	private final ClassificationSystemCPQAttributesProviderImpl classUnderTest = new ClassificationSystemCPQAttributesProviderImpl();
	private final List<CatalogModel> catalogs = new ArrayList<CatalogModel>();
	private final List<ClassificationSystemModel> availableClassificationSystems = new ArrayList<ClassificationSystemModel>();
	private final List<ClassificationSystemModel> availableClassificationSystemsMatchingPattern = new ArrayList<ClassificationSystemModel>();
	private final ClassificationSystemModel classificationSystem200 = new ClassificationSystemModel();
	private final ClassificationSystemModel classificationSystem300 = new ClassificationSystemModel();
	private final ClassificationSystemModel powertools = new ClassificationSystemModel();
	private final CatalogModel productCat = new CatalogModel();
	private final ClassificationSystemVersionModel classificationVersionModel200 = new ClassificationSystemVersionModel();
	private final ClassificationSystemVersionModel classificationVersionModel300 = new ClassificationSystemVersionModel();
	private final CsticValueModel csticValue = new CsticValueModelImpl();
	private final CsticModel cstic = new CsticModelImpl();
	private final ValueFormatTranslator valueFormatTranslator = new ValueFormatTranslatorImpl();
	private final ClassificationSystemVersionModel systemVersion = new ClassificationSystemVersionModel();
	private final Map<String, CPQImageType> mapping = new HashMap<>();

	@Mock
	private BaseStoreService mockBaseStoreService;
	@Mock
	private BaseStoreModel mockBaseStore;
	@Mock
	private ClassificationSystemService mockClassificationService;
	@Mock
	private ClassificationSystemCPQAttributesContainer mockHybrisNames;
	@Mock
	private UiTypeFinder mockUiTypeFinder;
	@Mock
	private CPQImageFormatMapping mockCPQCsticImageFormatMapping;
	@Mock
	private CPQImageFormatMapping mockCPQCsticValueImageFormatMapping;
	@Mock
	private ImagePopulator mockImagePopulator;
	@Mock
	private FlexibleSearchService mockFlexibleSearchService;
	@Mock
	private SearchResult<Object> mockSearchResult;
	@Mock
	private List<Object> attributes;
	@Mock
	private Iterator<Object> iterator;
	@Mock
	private ClassificationAttributeModel attribute;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		classificationSystem200.setId("ERP Classification 200");
		classificationSystem300.setId("ERP Classification 300");
		powertools.setId("Powertools Classification");
		productCat.setId("Product Cat");
		classUnderTest.setBaseStoreService(mockBaseStoreService);
		classUnderTest.setUiTypeFinder(mockUiTypeFinder);
		classUnderTest.setValueFormatTranslator(valueFormatTranslator);
		classUnderTest.setImagePopulator(mockImagePopulator);
		classUnderTest.setFlexibleSearchService(mockFlexibleSearchService);

		csticValue.setName("valueModel");
		csticValue.setLongText(MODEL_LONG_TEXT);
		cstic.setName("csticModel");

		when(mockBaseStoreService.getCurrentBaseStore()).thenReturn(mockBaseStore);
		when(mockBaseStore.getCatalogs()).thenReturn(catalogs);
		classUnderTest.setClassificationService(mockClassificationService);
		classUnderTest.setCpqCsticImageFormatMapping(mockCPQCsticImageFormatMapping);
		classUnderTest.setCpqCsticValueImageFormatMapping(mockCPQCsticValueImageFormatMapping);

		when(mockCPQCsticImageFormatMapping.getCPQMediaFormatQualifiers()).thenReturn(mapping);
		when(mockCPQCsticValueImageFormatMapping.getCPQMediaFormatQualifiers()).thenReturn(mapping);
		when(mockClassificationService.getSystemVersion(classificationSystem200.getId(), null))
				.thenReturn(classificationVersionModel200);
		when(mockClassificationService.getSystemVersion(classificationSystem300.getId(), null))
				.thenReturn(classificationVersionModel300);
		when(mockFlexibleSearchService.search(Mockito.any(FlexibleSearchQuery.class))).thenReturn(mockSearchResult);
		given(mockSearchResult.getResult()).willReturn(attributes);

		when(attributes.iterator()).thenReturn(iterator);
		when(iterator.next()).thenReturn(attribute);
		when(attribute.getCode()).thenReturn("code");
		when(attribute.getName()).thenReturn("name");
		when(attribute.getDescription()).thenReturn("description");
	}

	@Test
	public void testDetermineAvailableClassificationSystemsNoClassification()
	{
		catalogs.add(productCat);
		classUnderTest.determineAvailableClassificationSystems(catalogs, availableClassificationSystems,
				availableClassificationSystemsMatchingPattern);
		assertTrue(NO_CLASSIFICATION_EXPECTED, availableClassificationSystems.isEmpty());
		assertTrue(NO_CLASSIFICATION_EXPECTED, availableClassificationSystemsMatchingPattern.isEmpty());
	}

	@Test
	public void testDetermineAvailableClassificationSystemsClassification200()
	{
		catalogs.add(productCat);
		catalogs.add(classificationSystem200);
		classUnderTest.determineAvailableClassificationSystems(catalogs, availableClassificationSystems,
				availableClassificationSystemsMatchingPattern);
		assertFalse(CLASSIFICATION_EXPECTED, availableClassificationSystems.isEmpty());
		assertTrue("No classification expected matching pattern", availableClassificationSystemsMatchingPattern.isEmpty());
	}

	@Test
	public void testDetermineAvailableClassificationSystemsClassification300()
	{
		catalogs.add(productCat);
		catalogs.add(classificationSystem300);
		classUnderTest.determineAvailableClassificationSystems(catalogs, availableClassificationSystems,
				availableClassificationSystemsMatchingPattern);
		assertFalse(CLASSIFICATION_EXPECTED, availableClassificationSystems.isEmpty());
		assertFalse("Classification expected matching pattern", availableClassificationSystemsMatchingPattern.isEmpty());
	}

	@Test
	public void testPattern()
	{
		final String substring = "300";
		assertEquals("Expected substring is " + substring, substring, classUnderTest.getClassificationSystemSubString());
	}

	@Test
	public void testGetSystemVersion()
	{
		catalogs.add(productCat);
		final ClassificationSystemVersionModel systemVersion = classUnderTest.getSystemVersion();
		assertNull(NO_CLASSIFICATION_EXPECTED, systemVersion);
	}

	@Test
	public void testGetSystemVersionClassification200()
	{
		catalogs.add(productCat);
		catalogs.add(classificationSystem200);
		final ClassificationSystemVersionModel systemVersion = classUnderTest.getSystemVersion();
		assertEquals("We expect a classification version", classificationVersionModel200, systemVersion);
	}

	@Test
	public void testGetSystemVersionClassification300()
	{
		catalogs.add(productCat);
		catalogs.add(classificationSystem200);
		catalogs.add(classificationSystem300);
		final ClassificationSystemVersionModel systemVersion = classUnderTest.getSystemVersion();
		assertEquals("We expect a classification version 300 since the corresponding catalog matches the pattern",
				classificationVersionModel300, systemVersion);
	}

	@Test
	public void testGetValueLongText()
	{
		assertEquals(MODEL_LONG_TEXT, classUnderTest.getValueLongText(csticValue, cstic, mockHybrisNames, true));
	}

	@Test
	public void testGetValueLongTextEmpty()
	{
		csticValue.setLongText("");
		assertNull(classUnderTest.getValueLongText(csticValue, cstic, mockHybrisNames, true));
	}

	@Test
	public void testGetValueLongTextFromHybris()
	{
		final String valueKey = classUnderTest.getValueKey(csticValue, cstic);
		given(mockHybrisNames.getValueDescriptions()).willReturn(Collections.singletonMap(valueKey, HYBRIS_LONG_TEXT));
		assertEquals(HYBRIS_LONG_TEXT, classUnderTest.getValueLongText(csticValue, cstic, mockHybrisNames, true));
	}

	@Test
	public void testGetValueKey()
	{
		assertEquals("csticModel_valueModel", classUnderTest.getValueKey(csticValue, cstic));
	}

	@Test
	public void testExtractValueDescriptionsFromAttributeModelWithEmptyAttributeValues()
	{
		final Collection<ClassificationAttributeValueModel> attrValues = Collections.EMPTY_LIST;
		final Map<String, String> result = classUnderTest.extractValueDescriptionsFromAttributeModel(attrValues);
		assertNotNull(result);
		assertTrue("The map should be empty: ", result.isEmpty());
	}

	@Test
	public void testExtractValueDescriptionsFromAttributeModelWithNullAttributeValues()
	{
		final Collection<ClassificationAttributeValueModel> attrValues = null;
		final Map<String, String> result = classUnderTest.extractValueDescriptionsFromAttributeModel(attrValues);
		assertNotNull(result);
		assertTrue("The map should be empty: ", result.isEmpty());
	}

	@Test
	public void testExtractValueDescriptionsFromAttributeModel()
	{
		final String code = "code";
		final String description = "description";
		final Collection<ClassificationAttributeValueModel> attrValues = new ArrayList<>();
		final MediaContainerModel mockMediaContainer = mock(MediaContainerModel.class);
		final Collection<MediaModel> media = Collections.EMPTY_LIST;
		attrValues.add(prepareClassificationAttributeValueModelData(code, description, mockMediaContainer, media));
		final Map<String, String> result = classUnderTest.extractValueDescriptionsFromAttributeModel(attrValues);
		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(description, result.get(code));
	}

	@Test
	public void testOverviewValueNameSupportedType()
	{
		final ClassificationSystemCPQAttributesContainer hybrisNames = ClassificationSystemCPQAttributesContainer.NULL_OBJ;
		when(mockUiTypeFinder.findUiTypeForCstic(cstic)).thenReturn(UiType.STRING);

		final String displayName = classUnderTest.getOverviewValueName(csticValue, cstic, hybrisNames, false);
		assertNotNull(displayName);
		assertEquals(csticValue.getName(), displayName);
	}

	@Test
	public void testOverviewValueNameNotYetSupportedType()
	{
		final ClassificationSystemCPQAttributesContainer hybrisNames = ClassificationSystemCPQAttributesContainer.NULL_OBJ;
		when(mockUiTypeFinder.findUiTypeForCstic(cstic)).thenReturn(UiType.NOT_IMPLEMENTED);

		final String displayName = classUnderTest.getOverviewValueName(csticValue, cstic, hybrisNames, false);
		assertNotNull(displayName);
		assertEquals("NOT_IMPLEMENTED", displayName);
	}

	@Test
	public void testgetOverviewValueName()
	{
		final ClassificationSystemCPQAttributesContainer hybrisNames = ClassificationSystemCPQAttributesContainer.NULL_OBJ;
		when(mockUiTypeFinder.findUiTypeForCstic(cstic)).thenReturn(UiType.STRING);
		final String name = "displayName";
		csticValue.setLanguageDependentName(name);

		final String result = classUnderTest.getOverviewValueName(csticValue, cstic, hybrisNames, false);
		assertNotNull(result);
		assertEquals(name, result);
	}

	@Test
	public void testExtractCsticValueMediaFromAttributeModelWithEmptyAttributeValues()
	{
		Collection<ClassificationAttributeValueModel> attrValues = Collections.EMPTY_LIST;
		Map<String, Collection<MediaModel>> result = classUnderTest.extractCsticValueMediaFromAttributeModel(attrValues);
		assertNotNull(result);
		assertTrue("The map should be empty: ", result.isEmpty());

		attrValues = null;
		result = classUnderTest.extractCsticValueMediaFromAttributeModel(attrValues);
		assertNotNull(result);
		assertTrue("The map should be empty: ", result.isEmpty());
	}

	@Test
	public void testExtractCsticValueMediaFromAttributeModelWithAttributeValues()
	{
		final String code = "code";
		final String description = "description";
		final Collection<ClassificationAttributeValueModel> attrValues = new ArrayList<>();
		final MediaContainerModel mockMediaContainer = mock(MediaContainerModel.class);
		final Collection<MediaModel> medias = new ArrayList<>();
		final MediaModel media = mock(MediaModel.class);
		medias.add(media);
		attrValues.add(prepareClassificationAttributeValueModelData(code, description, mockMediaContainer, medias));

		final Map<String, Collection<MediaModel>> result = classUnderTest.extractCsticValueMediaFromAttributeModel(attrValues);
		assertNotNull(result);
		assertEquals(1, result.size());
	}

	@Test
	public void testAddMediaEntriesReturnWithEmptyMap()
	{
		final String code = "code";
		final String description = "description";
		final Collection<ClassificationAttributeValueModel> attrValues = new ArrayList<>();
		final MediaContainerModel mockMediaContainer = mock(MediaContainerModel.class);
		final Collection<MediaModel> medias = Collections.EMPTY_LIST;
		attrValues.add(prepareClassificationAttributeValueModelData(code, description, mockMediaContainer, medias));

		final Map<String, Collection<MediaModel>> result = classUnderTest.addMediaEntries(attrValues);
		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void testAddMediaEntriesWithNoMediaContainer()
	{
		final String code = "code";
		final String description = "description";
		final Collection<ClassificationAttributeValueModel> attrValues = new ArrayList<>();
		final MediaContainerModel mockMediaContainer = mock(MediaContainerModel.class);
		final Collection<MediaModel> medias = Collections.EMPTY_LIST;
		attrValues.add(prepareClassificationAttributeValueModelData(code, description, mockMediaContainer, medias));

		when(attrValues.iterator().next().getCpqMedia()).thenReturn(null);
		final Map<String, Collection<MediaModel>> result = classUnderTest.addMediaEntries(attrValues);
		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void testAddMediaEntriesReturnMediaMap()
	{
		final String code = "code";
		final String description = "description";
		final Collection<ClassificationAttributeValueModel> attrValues = new ArrayList<>();
		final MediaContainerModel mockMediaContainer = mock(MediaContainerModel.class);
		final Collection<MediaModel> medias = new ArrayList<>();
		attrValues.add(prepareClassificationAttributeValueModelData(code, description, mockMediaContainer, medias));

		final Map<String, Collection<MediaModel>> result = classUnderTest.addMediaEntries(attrValues);
		assertNotNull(result);
		assertEquals(0, result.size());
	}

	protected ClassificationAttributeValueModel prepareClassificationAttributeValueModelData(final String code,
			final String description, final MediaContainerModel mockMediaContainer, final Collection<MediaModel> media)
	{
		final ClassificationAttributeValueModel mockAttrValue = mock(ClassificationAttributeValueModel.class);
		when(mockAttrValue.getCode()).thenReturn(code);
		when(mockAttrValue.getDescription()).thenReturn(description);
		when(mockAttrValue.getCpqMedia()).thenReturn(mockMediaContainer);
		when(mockMediaContainer.getMedias()).thenReturn(media);

		return mockAttrValue;
	}

	@Test
	public void testExtractCsticMediaFromAttributeModelWithNoMediaContainer()
	{
		final ClassificationAttributeModel mockAttr = mock(ClassificationAttributeModel.class);
		when(mockAttr.getCpqMedia()).thenReturn(null);
		final Collection<MediaModel> result = classUnderTest.extractCsticMediaFromAttributeModel(mockAttr);
		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void testExtractCsticMediaFromAttributeModelWithMediaContainerAndEmptyMediaList()
	{
		final ClassificationAttributeModel mockAttr = mock(ClassificationAttributeModel.class);
		final MediaContainerModel mockMediaContainer = mock(MediaContainerModel.class);
		when(mockAttr.getCpqMedia()).thenReturn(mockMediaContainer);

		Collection<MediaModel> media = Collections.EMPTY_LIST;
		when(mockMediaContainer.getMedias()).thenReturn(media);
		Collection<MediaModel> result = classUnderTest.extractCsticMediaFromAttributeModel(mockAttr);
		assertNotNull(result);
		assertEquals(0, result.size());

		media = null;
		result = classUnderTest.extractCsticMediaFromAttributeModel(mockAttr);
		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void testExtractCsticMediaFromAttributeModelWithMediaContainerAndMediaList()
	{
		final ClassificationAttributeModel mockAttr = mock(ClassificationAttributeModel.class);
		final MediaContainerModel mockMediaContainer = mock(MediaContainerModel.class);
		when(mockAttr.getCpqMedia()).thenReturn(mockMediaContainer);
		final Collection<MediaModel> media = new ArrayList<>();
		final MediaModel medium = new MediaModel();
		media.add(medium);
		when(mockMediaContainer.getMedias()).thenReturn(media);
		final Collection<MediaModel> result = classUnderTest.extractCsticMediaFromAttributeModel(mockAttr);
		assertNotNull(result);
		assertEquals(1, result.size());
	}

	@Test
	public void testExtractValueNamesFromAttributeModelWithEmptyAttributeValueList()
	{
		Collection<ClassificationAttributeValueModel> attrValues = Collections.EMPTY_LIST;
		Map<String, String> result = classUnderTest.extractValueNamesFromAttributeModel(attrValues);
		assertNotNull(result);
		assertEquals(0, result.size());

		attrValues = null;
		result = classUnderTest.extractValueNamesFromAttributeModel(attrValues);
		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void testExtractValueNamesFromAttributeModel()
	{
		final Collection<ClassificationAttributeValueModel> attrValues = new ArrayList<>();
		final ClassificationAttributeValueModel mockAttrValue = mock(ClassificationAttributeValueModel.class);
		when(mockAttrValue.getCode()).thenReturn("code");
		when(mockAttrValue.getDescription()).thenReturn("description");
		attrValues.add(mockAttrValue);
		final Map<String, String> result = classUnderTest.extractValueNamesFromAttributeModel(attrValues);
		assertNotNull(result);
		assertEquals(1, result.size());
	}

	@Test(expected = IllegalStateException.class)
	public void testGetBaseStore()
	{
		when(mockBaseStoreService.getCurrentBaseStore()).thenReturn(null);
		classUnderTest.getBaseStore();
	}

	@Test
	public void testGetDisplayValueName()
	{
		final CsticModel csticModel = new CsticModelImpl();
		final CsticValueModel valueModel = new CsticValueModelImpl();
		final String languageDependentName = "language dependent name";
		valueModel.setLanguageDependentName(languageDependentName);
		final ClassificationSystemCPQAttributesContainer cpqAttributes = new ClassificationSystemCPQAttributesContainer("test",
				null, null, Collections.emptyMap(), Collections.emptyMap(), Collections.emptyList(), Collections.emptyMap());
		final String result = classUnderTest.getDisplayValueName(valueModel, csticModel, cpqAttributes, false);
		assertNotNull(result);
		assertEquals(languageDependentName, result);
	}

	@Test
	public void testGetDisplayValueNameWithNullName()
	{
		final CsticModel csticModel = new CsticModelImpl();
		final CsticValueModel valueModel = new CsticValueModelImpl();
		final String name = "csticValueName";
		valueModel.setName(name);
		valueModel.setLanguageDependentName(null);
		final ClassificationSystemCPQAttributesContainer cpqAttributes = new ClassificationSystemCPQAttributesContainer("test",
				null, null, Collections.emptyMap(), Collections.emptyMap(), Collections.emptyList(), Collections.emptyMap());
		final String result = classUnderTest.getDisplayValueName(valueModel, csticModel, cpqAttributes, false);
		assertNotNull(result);
		assertEquals("[" + name + "]", result);
	}

	@Test
	public void testGetCsticMediaWithoutMedia()
	{
		final ClassificationSystemCPQAttributesContainer cpqAttributes = new ClassificationSystemCPQAttributesContainer("code",
				null, null, Collections.emptyMap(), Collections.emptyMap(), Collections.emptyList(), Collections.emptyMap());
		final List<ImageData> result = classUnderTest.getCsticMedia(cpqAttributes);

		assertNotNull(result);
		assertTrue("Result list should be empty: ", result.isEmpty());
	}

	@Test
	public void testGetCsticMedia()
	{
		mapping.put("key", CPQImageType.CSTIC_IMAGE);
		final Collection<MediaModel> media = prepareDataForConvertMediaToImages(mapping);
		final ClassificationSystemCPQAttributesContainer cpqAttributes = new ClassificationSystemCPQAttributesContainer("code",
				"name", null, Collections.emptyMap(), Collections.emptyMap(), media, Collections.emptyMap());

		final List<ImageData> results = classUnderTest.getCsticMedia(cpqAttributes);
		assertNotNull(results);
		assertEquals("Result list should contains 1 media: ", 1, results.size());
		assertEquals(CPQImageType.CSTIC_IMAGE.toString(), results.get(0).getFormat());
	}

	@Test
	public void testGetCsticValueMediaWithoutCsticValueMedia()
	{
		final ClassificationSystemCPQAttributesContainer cpqAttributes = new ClassificationSystemCPQAttributesContainer("code",
				null, null, Collections.emptyMap(), Collections.emptyMap(), Collections.emptyList(), Collections.emptyMap());
		final List<ImageData> result = classUnderTest.getCsticValueMedia("csticValueKey", cpqAttributes);

		assertNotNull(result);
		assertTrue("Result list should be empty: ", result.isEmpty());
	}

	@Test
	public void testGetCsticValueMediaWithoutMedia()
	{
		final Map<String, Collection<MediaModel>> csticValueMedia = new HashMap<>();
		final Collection<MediaModel> media = new ArrayList<>();
		final MediaModel medium = new MediaModel();
		media.add(medium);
		csticValueMedia.put("key", media);
		final ClassificationSystemCPQAttributesContainer cpqAttributes = new ClassificationSystemCPQAttributesContainer("code",
				null, null, Collections.emptyMap(), Collections.emptyMap(), Collections.emptyList(), csticValueMedia);
		final List<ImageData> results = classUnderTest.getCsticValueMedia("csticValueKey", cpqAttributes);

		assertNotNull(results);
		assertTrue("Result list should be empty: ", results.isEmpty());
	}

	@Test
	public void testGetCsticValueMedia()
	{
		mapping.put("key", CPQImageType.VALUE_IMAGE);
		final Collection<MediaModel> media = prepareDataForConvertMediaToImages(mapping);

		final Map<String, Collection<MediaModel>> csticValueMedia = new HashMap<>();
		csticValueMedia.put("csticValueKey", media);
		final ClassificationSystemCPQAttributesContainer cpqAttributes = new ClassificationSystemCPQAttributesContainer("code",
				null, null, Collections.emptyMap(), Collections.emptyMap(), Collections.emptyList(), csticValueMedia);

		final List<ImageData> results = classUnderTest.getCsticValueMedia("csticValueKey", cpqAttributes);
		assertNotNull(results);
		assertEquals("Result list should contains 1 media: ", 1, results.size());
		assertEquals(CPQImageType.VALUE_IMAGE.toString(), results.get(0).getFormat());
	}

	@Test
	public void testConvertMediaToImagesWithEmptyMedia()
	{
		final Collection<MediaModel> media = Collections.EMPTY_LIST;
		final Map<String, CPQImageType> cpqMediaFormatQualifiers = new HashMap<>();

		final List<ImageData> results = classUnderTest.convertMediaToImages(media, cpqMediaFormatQualifiers);
		assertNotNull(results);
		assertTrue("Result list should be empty: ", results.isEmpty());
	}

	@Test
	public void testConvertMediaToImagesWithWrongKey()
	{
		final Collection<MediaModel> media = new ArrayList<>();
		final MediaModel medium = new MediaModel();
		final MediaFormatModel format = new MediaFormatModel();
		format.setQualifier("key");
		medium.setMediaFormat(format);
		media.add(medium);
		final Map<String, CPQImageType> cpqMediaFormatQualifiers = new HashMap<>();
		cpqMediaFormatQualifiers.put("csticKey", CPQImageType.CSTIC_IMAGE);

		final List<ImageData> result = classUnderTest.convertMediaToImages(media, cpqMediaFormatQualifiers);
		assertNotNull(result);
		assertTrue("Result list should be empty: ", result.isEmpty());
	}

	@Test
	public void testConvertMediaToImages()
	{
		mapping.put("key", CPQImageType.CSTIC_IMAGE);
		final Collection<MediaModel> media = prepareDataForConvertMediaToImages(mapping);

		final List<ImageData> result = classUnderTest.convertMediaToImages(media, mapping);
		assertNotNull(result);
		assertEquals(CPQImageType.CSTIC_IMAGE.toString(), result.get(0).getFormat());
	}

	protected Collection<MediaModel> prepareDataForConvertMediaToImages(final Map<String, CPQImageType> cpqMediaFormatQualifiers)
	{
		final Collection<MediaModel> media = new ArrayList<>();
		final MediaModel medium = new MediaModel();
		final MediaFormatModel format = new MediaFormatModel();
		format.setQualifier("key");
		medium.setMediaFormat(format);
		media.add(medium);

		final MediaModel source = medium;
		final ImageData target = new ImageData();
		doNothing().when(mockImagePopulator).populate(source, target);

		return media;
	}

	@Test
	public void testGetNullLongText()
	{
		final ClassificationSystemCPQAttributesContainer cpqAttributes = new ClassificationSystemCPQAttributesContainer("code",
				null, null, Collections.emptyMap(), Collections.emptyMap(), Collections.emptyList(), Collections.emptyMap());
		final CsticModel model = new CsticModelImpl();
		model.setLongText("");
		final boolean isDebugEnabled = false;
		final String result = classUnderTest.getLongText(model, cpqAttributes, isDebugEnabled);
		assertNull(result);
	}

	@Test
	public void testGetHybrisLongText()
	{
		final String description = "description";
		final ClassificationSystemCPQAttributesContainer cpqAttributes = new ClassificationSystemCPQAttributesContainer("code",
				null, description, Collections.emptyMap(), Collections.emptyMap(), Collections.emptyList(), Collections.emptyMap());
		final CsticModel model = new CsticModelImpl();
		model.setLongText("");
		final boolean isDebugEnabled = false;
		final String result = classUnderTest.getLongText(model, cpqAttributes, isDebugEnabled);
		assertEquals(description, result);
	}

	@Test
	public void testGetLongText()
	{
		final String description = "modelLongTest";
		final ClassificationSystemCPQAttributesContainer cpqAttributes = new ClassificationSystemCPQAttributesContainer("code",
				null, null, Collections.emptyMap(), Collections.emptyMap(), Collections.emptyList(), Collections.emptyMap());
		final CsticModel model = new CsticModelImpl();
		model.setLongText(description);
		final boolean isDebugEnabled = false;
		final String result = classUnderTest.getLongText(model, cpqAttributes, isDebugEnabled);
		assertEquals(description, result);
	}

	@Test
	public void testGetDisplayName()
	{
		final String name = "csticName";
		final CsticModel csticModel = new CsticModelImpl();
		csticModel.setLanguageDependentName("");
		csticModel.setName(name);
		final ClassificationSystemCPQAttributesContainer cpqAttributes = new ClassificationSystemCPQAttributesContainer("code",
				null, null, Collections.emptyMap(), Collections.emptyMap(), Collections.emptyList(), Collections.emptyMap());
		final boolean isDebugEnabled = false;
		final String result = classUnderTest.getDisplayName(csticModel, cpqAttributes, isDebugEnabled);
		assertEquals("[" + name + "]", result);
	}

	@Test
	public void testGetDisplayLangDepName()
	{
		final String name = "languageDependentName";
		final CsticModel csticModel = new CsticModelImpl();
		csticModel.setLanguageDependentName(name);
		final ClassificationSystemCPQAttributesContainer cpqAttributes = new ClassificationSystemCPQAttributesContainer("code",
				null, null, Collections.emptyMap(), Collections.emptyMap(), Collections.emptyList(), Collections.emptyMap());
		final boolean isDebugEnabled = false;
		final String result = classUnderTest.getDisplayName(csticModel, cpqAttributes, isDebugEnabled);
		assertEquals(name, result);
	}

	@Test
	public void testGetDisplayHybrisName()
	{
		final String name = "hybrisName";
		final CsticModel csticModel = new CsticModelImpl();
		csticModel.setLanguageDependentName(name);
		final ClassificationSystemCPQAttributesContainer cpqAttributes = new ClassificationSystemCPQAttributesContainer("code",
				name, null, Collections.emptyMap(), Collections.emptyMap(), Collections.emptyList(), Collections.emptyMap());
		final boolean isDebugEnabled = false;
		final String result = classUnderTest.getDisplayName(csticModel, cpqAttributes, isDebugEnabled);
		assertEquals(name, result);
	}

	@Test
	public void testGetClassificationAttributeWithEmptyHybrisAttributes()
	{
		given(mockSearchResult.getResult()).willReturn(Collections.EMPTY_LIST);
		final ClassificationAttributeModel result = classUnderTest.getClassificationAttribute("name", systemVersion);
		assertNull(result);
	}

	@Test
	public void testGetClassificationAttributeWithNullSystemVersion()
	{
		final ClassificationSystemVersionModel systemVersion = null;
		final ClassificationAttributeModel result = classUnderTest.getClassificationAttribute("name", systemVersion);
		assertNull(result);
	}

	@Test
	public void testGetClassificationAttributeWithMoreThanOneAttribute()
	{
		when(attributes.size()).thenReturn(2);
		final ClassificationAttributeModel result = classUnderTest.getClassificationAttribute("name", systemVersion);
		assertNull(result);
	}

	@Test
	public void testGetClassificationAttribute()
	{
		final ClassificationAttributeModel result = classUnderTest.getClassificationAttribute("name", systemVersion);
		assertNotNull(result);
		assertEquals(attribute, result);
	}

	@Test
	public void testGetValueName()
	{
		final Map<String, String> valueNames = new HashMap<>();
		final String key = cstic.getName() + "_" + csticValue.getName();
		final String value = "value";
		valueNames.put(key, value);

		final ClassificationSystemCPQAttributesContainer cpqAttributes = new ClassificationSystemCPQAttributesContainer("code",
				null, null, valueNames, Collections.emptyMap(), Collections.emptyList(), Collections.emptyMap());
		final String result = classUnderTest.getValueName(csticValue, cstic, cpqAttributes, false);
		assertNotNull(result);
		assertEquals(value, result);
	}

	@Test
	public void testGetCPQAttributesWithNameMap()
	{
		final Map<String, ClassificationSystemCPQAttributesContainer> nameMap = new HashMap<>();
		final ClassificationSystemCPQAttributesContainer cpqAttributes = new ClassificationSystemCPQAttributesContainer("code",
				null, null, Collections.emptyMap(), Collections.emptyMap(), Collections.emptyList(), Collections.emptyMap());
		nameMap.put("name", cpqAttributes);

		final ClassificationSystemCPQAttributesContainer result = classUnderTest.getCPQAttributes("name", nameMap);
		assertNotNull(result);
		assertEquals(cpqAttributes, result);
	}

	@Test
	public void testGetCPQAttributesWithoutNameMap()
	{
		final Map<String, ClassificationSystemCPQAttributesContainer> nameMap = null;

		final ClassificationSystemCPQAttributesContainer result = classUnderTest.getCPQAttributes("name", nameMap);
		assertNotNull(result);
	}

	@Test
	public void testGetCPQAttributes()
	{
		catalogs.add(classificationSystem200);
		final String name = "name";
		final Map<String, ClassificationSystemCPQAttributesContainer> nameMap = new HashMap<>();
		final ClassificationSystemCPQAttributesContainer cpqAttributes = new ClassificationSystemCPQAttributesContainer("code",
				name, null, Collections.emptyMap(), Collections.emptyMap(), Collections.emptyList(), Collections.emptyMap());

		final ClassificationSystemCPQAttributesContainer result = classUnderTest.getCPQAttributes(name, nameMap);
		assertNotNull(result);
		assertEquals(cpqAttributes, result);
		assertEquals(cpqAttributes, nameMap.get(name));
	}

	@Test
	public void testGetNameFromAttributeWithoutAttribute()
	{
		final ClassificationAttributeModel attr = null;

		final ClassificationSystemCPQAttributesContainer result = classUnderTest.getNameFromAttribute(attr, systemVersion);
		assertNotNull(result);
		assertEquals(ClassificationSystemCPQAttributesContainer.NULL_OBJ, result);
	}

	@Test
	public void testGetNameFromAttribute()
	{
		final ClassificationSystemCPQAttributesContainer result = classUnderTest.getNameFromAttribute(attribute, systemVersion);
		assertNotNull(result);
	}

	@Test
	public void testGetNameFromAttributeWithNullAttributeValues()
	{
		given(mockSearchResult.getResult()).willReturn(null);
		final ClassificationSystemCPQAttributesContainer result = classUnderTest.getNameFromAttribute(attribute, systemVersion);
		assertNotNull(result);
	}
}

