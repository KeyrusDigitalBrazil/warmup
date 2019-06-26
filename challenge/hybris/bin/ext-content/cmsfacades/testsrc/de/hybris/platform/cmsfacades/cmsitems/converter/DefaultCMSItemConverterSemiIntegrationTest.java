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
package de.hybris.platform.cmsfacades.cmsitems.converter;

import static de.hybris.platform.cms2.model.contents.CMSItemModel._TYPECODE;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_UUID;
import static de.hybris.platform.core.PK.fromLong;
import static java.lang.Boolean.FALSE;
import static java.util.Arrays.asList;
import static java.util.Locale.ENGLISH;
import static java.util.Locale.FRENCH;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.cloning.strategy.impl.ComponentCloningStrategy;
import de.hybris.platform.cms2.cmsitems.converter.AttributeContentConverter;
import de.hybris.platform.cms2.cmsitems.converter.AttributeStrategyConverterProvider;
import de.hybris.platform.cms2.common.functions.Converter;
import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cms2.servicelayer.services.AttributeDescriptorModelHelperService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminItemService;
import de.hybris.platform.cmsfacades.cmsitems.AttributeContentValidator;
import de.hybris.platform.cmsfacades.cmsitems.CMSItemValidator;
import de.hybris.platform.cmsfacades.cmsitems.CloneComponentContextProvider;
import de.hybris.platform.cmsfacades.cmsitems.OriginalClonedItemProvider;
import de.hybris.platform.cmsfacades.cmsitems.attributeconverters.CollectionToRepresentationConverter;
import de.hybris.platform.cmsfacades.cmsitems.populators.CMSItemUniqueIdentifierAttributePopulator;
import de.hybris.platform.cmsfacades.cmsitems.populators.CmsItemDefaultAttributesPopulator;
import de.hybris.platform.cmsfacades.common.populator.impl.DefaultLocalizedPopulator;
import de.hybris.platform.cmsfacades.common.predicate.attributes.NestedOrPartOfAttributePredicate;
import de.hybris.platform.cmsfacades.common.validator.ValidatableService;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrors;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrorsProvider;
import de.hybris.platform.cmsfacades.data.ItemData;
import de.hybris.platform.cmsfacades.languages.LanguageFacade;
import de.hybris.platform.cmsfacades.types.service.CMSPermissionChecker;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.cmsfacades.users.services.CMSUserService;
import de.hybris.platform.cmsfacades.util.JSONMatcher;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.security.permissions.PermissionCRUDService;
import de.hybris.platform.servicelayer.security.permissions.PermissionsConstants;
import de.hybris.platform.servicelayer.type.TypeService;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.fest.util.Collections;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCMSItemConverterSemiIntegrationTest
{
	private static final String TEST_FILE_JSON = "/cmsfacades/test/expectedSerializedItemForContentItemConverter.json";

	private static final String EN = "en";
	private static final String FR = "fr";

	private static final String QUALIFIER_0 = "qualifier0";
	private static final String QUALIFIER_1 = "qualifier1";
	private static final String QUALIFIER_2 = "qualifier2";
	private static final String QUALIFIER_3 = "qualifier3";
	private static final String QUALIFIER_4 = "qualifier4";
	private static final String QUALIFIER_5 = "qualifier5";
	private static final String QUALIFIER_6 = "qualifier6";
	private static final String QUALIFIER_7 = "qualifier7";
	private static final String QUALIFIER_8 = "qualifier8";
	private static final String QUALIFIER = "qualifier9";
	private static final String QUALIFIER_10 = "qualifier10";

	private static final String QUALIFIER_NOT_WRITABLE = "qualifierInvalid1";
	private static final String QUALIFIER_DYNAMIC = "qualifierInvalid2";

	private static final String QUALIFIER_PART_OF = "qualifierPartOf";
	private static final String SUB_TYPE_TYPE = "subType";
	private static final String CMS_SUB_TYPE_TYPE = "cmsSubType";
	private static final String UUID1 = "uuid1";
	private static final String UUID2 = "uuid2";
	private static final String UUID3 = "uuid3";
	private static final String UUID_MAIN = "uuidMain";
	private static final String UUID_PART_OF = "uuidPartOf";

	public static class MainClass extends ItemModel
	{
		@Override
		public String getItemtype()
		{
			return CMSItemModel._TYPECODE;
		}
	}

	public static class SubClass extends ItemModel
	{
		@Override
		public String getItemtype()
		{
			return SUB_TYPE_TYPE;
		}
	}

	public static class CMSItemSubClass extends CMSItemModel
	{
		@Override
		public String getItemtype()
		{
			return CMS_SUB_TYPE_TYPE;
		}
	}

	//----------------------------------------------------
	// DefaultLocalizedPopulator would be difficult to mock because for the populateAsMap signature.
	// Will use a real one here but with its own dependencies mocked
	@Mock
	private LanguageFacade languageFacade;
	@Mock
	private CommonI18NService commonI18NService;
	@InjectMocks
	private DefaultLocalizedPopulator localizedPopulator;
	//----------------------------------------------------

	@InjectMocks
	private DefaultCMSItemConverter cmsItemConverter;

	@Mock
	private Predicate<AttributeDescriptorModel> nestedAttributePredicate;
	@InjectMocks
	private NestedOrPartOfAttributePredicate nestedOrPartOfAttributePredicate;
	@InjectMocks
	private CmsItemDefaultAttributesPopulator cmsItemDefaultAttributesPopulator;
	@InjectMocks
	private ComposedTypeToAttributeCollectionConverter composedTypeToAttributeCollectionConverter;
	@InjectMocks
	private CollectionToRepresentationConverter collectionToRepresentationConverter;
	@InjectMocks
	private DefaultAttributeValueToRepresentationStrategy attributeValueToRepresentationStrategy;

	@Mock
	private CMSAdminItemService cmsAdminItemService;
	@Mock
	private TypeService typeService;
	@Mock
	private ModelService modelService;
	@Mock
	private AttributeStrategyConverterProvider attributeStrategyConverter;
	@Mock
	private Converter<Date, String> dateConverter;
	@Mock
	private UniqueItemIdentifierService uniqueItemIdentifierService;
	@Mock
	private AttributeContentValidator baseAttributeContentValidator;
	@Mock
	private AttributeContentValidator extendedAttributeContentValidator;
	@Mock
	private AttributeDescriptorModelHelperService attributeDescriptorModelHelperService;
	@Mock
	private ValidatableService validatableService;
	@Mock
	private ValidationErrorsProvider validationErrorsProvider;
	@Mock
	private CMSPermissionChecker cmsPermissionChecker;
	@Mock
	private Predicate<AttributeDescriptorModel> isCollectionPredicate;
	@Mock
	private Predicate<AttributeDescriptorModel> isCollectionPredicateNegate;

	@Mock
	private MainClass cmsItem;
	@Mock
	private SubClass cmsItemProperty1;
	@Mock
	private CMSItemSubClass cmsItemProperty2;
	@Mock
	private SubClass cmsItemProperty3;
	@Mock
	private SubClass modifiedCmsItemPartOfProperty;
	@Mock
	private SubClass modifiedCmsItemPartOfProperty3;
	@Mock
	private SubClass newCmsItemPartOfProperty1;
	@Mock
	private CMSItemSubClass newCmsItemPartOfProperty2;
	@Mock
	private SubClass modifiedEmbeddedProperty;
	@Mock
	private CMSItemSubClass newEmbeddedProperty;
	@Mock
	private OriginalClonedItemProvider originalClonedItemProvider;
	@Mock
	private ComponentCloningStrategy componentCloningStrategy;
	@Mock
	private CloneComponentContextProvider cloneComponentContextProvider;
	@Mock
	private AttributeStrategyConverterProvider cloneAttributeStrategyConverter;

	@Mock
	private ComposedTypeModel mainComposedType;
	@Mock
	private ComposedTypeModel subClassComposedType;
	@Mock
	private ComposedTypeModel cmsSubClassComposedType;

	@Mock
	private AttributeDescriptorModel partOfAttributeDescriptor;
	@Mock
	private AttributeDescriptorModel attributeDescriptor0;
	@Mock
	private AttributeDescriptorModel attributeDescriptor1;
	@Mock
	private AttributeDescriptorModel attributeDescriptor2;
	@Mock
	private AttributeDescriptorModel attributeDescriptor3;
	@Mock
	private AttributeDescriptorModel attributeDescriptor4;
	@Mock
	private AttributeDescriptorModel attributeDescriptor5;
	@Mock
	private AttributeDescriptorModel attributeDescriptor6;
	@Mock
	private AttributeDescriptorModel attributeDescriptor7;
	@Mock
	private AttributeDescriptorModel attributeDescriptor8;
	@Mock
	private AttributeDescriptorModel attributeDescriptor;
	@Mock
	private AttributeDescriptorModel attributeDescriptor10;

	@Mock
	private AttributeDescriptorModel nonWritableAttributeDescriptor;
	@Mock
	private AttributeDescriptorModel dynamicAttributeDescriptor;

	@Mock
	private AttributeDescriptorModel partOfAttributeDescriptor1;
	@Mock
	private AttributeDescriptorModel partOfAttributeDescriptor2;

	@Mock
	private Map<AttributeDescriptorModel, Object> resultOfConversion;
	@Mock
	private TypeModel collectionTypeModel;
	@Mock
	private TypeModel simpleTypeModel;

	@Mock
	private AttributeContentConverter partOfAttributeContentConverter;
	@Mock
	private AttributeContentConverter attributeContentConverter0;
	@Mock
	private AttributeContentConverter attributeContentConverter2;
	@Mock
	private AttributeContentConverter attributeContentConverter4;
	@Mock
	private AttributeContentConverter attributeContentConverter6;
	@Mock
	private AttributeContentConverter attributeContentConverter8;
	@Mock
	private AttributeContentConverter attributeContentConverter9;
	@Mock
	private AttributeContentConverter dynamicAttributeContentConverter;
	@Mock
	private AttributeContentConverter nonWritableAttributeConverter;
	@Mock
	private CMSItemValidator<ItemModel> cmsItemValidatorCreate;
	@Mock
	private CMSItemValidator<ItemModel> cmsItemValidatorUpdate;
	@Mock
	private CMSItemUniqueIdentifierAttributePopulator uniqueIdentifierAttributePopulator;
	@Mock
	private PermissionCRUDService permissionCRUDService;
	@Mock
	private CMSUserService cmsUserService;

	@Captor
	private ArgumentCaptor<String> propertyCator;
	@Captor
	private ArgumentCaptor<Object> valueCaptor;

	private final List<AttributeDescriptorModel> mainComposedTypeAttributeDescriptorsList = new ArrayList<>();

	/**
	 * Test data structure:
	 * <ol>
	 * <li>qualifier0 - standard uuid
	 * <li>qualifier1 - partOf attribute
	 * <li>qualifier2 - localized attribute
	 * <li>qualifier3 - localized partOf attribute
	 * <li>qualifier4 - collection of uuids
	 * <li>qualifier5 - collection of partOf attribute
	 * <li>qualifier6 - localized collection of uuids
	 * <li>qualifier7 - localized collection of partOf attribute
	 * <li>qualifier8 - collection of Map<String, String> attributes
	 * <li>qualifier_not_writable - non writable attribute
	 * <li>qualifier_dynamic - dynamic attribute (not stored in database) -- only read, never written
	 * </ol>
	 */
	@Before
	public void setup() throws IllegalAccessException, InstantiationException
	{
		mockBlacklistedTypes();
		mockLanguages();
		mockNestedAttributePredicates();
		mockTypeModels();
		mockUniqueItemIdentifierServiceAndPopulator();
		mockValidationServiceAndErrors();

		// Wiring mocks
		attributeValueToRepresentationStrategy.setModelService(modelService);
		attributeValueToRepresentationStrategy.setLocalizedPopulator(localizedPopulator);
		attributeValueToRepresentationStrategy.setCollectionToRepresentationConverter(collectionToRepresentationConverter);
		cmsItemConverter.setAttributeValueToRepresentationStrategy(attributeValueToRepresentationStrategy);
		cmsItemConverter.setCustomPopulators(Arrays.asList(uniqueIdentifierAttributePopulator, cmsItemDefaultAttributesPopulator));

		when(dateConverter.convert(any())).thenReturn("some-formatted-date");
		when(modelService.create(SubClass.class)).thenReturn(new SubClass());
		when(cmsAdminItemService.createItem(anyObject())) //
				.thenAnswer(answer -> {
					final Class<?> modelClass = (Class<?>) answer.getArguments()[0];
					return modelClass.newInstance();
				});

		// Setup test data
		setupMockData();

		// Permissions
		when(permissionCRUDService.canChangeAttribute(any(), any())).thenReturn(true);
		when(permissionCRUDService.canReadAttribute(any(), any())).thenReturn(true);
		when(cmsPermissionChecker.hasPermissionForContainedType(any(), any())).thenReturn(true);

		// Languages
		when(cmsUserService.getReadableLanguagesForCurrentUser()).thenReturn(new HashSet<>(Arrays.asList(EN, FR)));
		when(cmsUserService.getWriteableLanguagesForCurrentUser()).thenReturn(new HashSet<>(Arrays.asList(EN, FR)));
	}

	protected void setupMockData()
	{
		final ComposedTypeModel superComposedType = mock(ComposedTypeModel.class);
		when(superComposedType.getDeclaredattributedescriptors()).thenReturn(
				asList(attributeDescriptor4, attributeDescriptor5, attributeDescriptor6, attributeDescriptor7, attributeDescriptor8));
		mainComposedTypeAttributeDescriptorsList
				.addAll(asList(attributeDescriptor0, attributeDescriptor1, attributeDescriptor2, attributeDescriptor3));
		when(mainComposedType.getDeclaredattributedescriptors()).thenReturn(mainComposedTypeAttributeDescriptorsList);
		when(mainComposedType.getCode()).thenReturn(CMSItemModel._TYPECODE);
		when(mainComposedType.getAllSuperTypes()).thenReturn(Arrays.asList(superComposedType));

		when(subClassComposedType.getDeclaredattributedescriptors()).thenReturn(asList(partOfAttributeDescriptor));
		when(subClassComposedType.getCode()).thenReturn(SUB_TYPE_TYPE);
		when(cmsSubClassComposedType.getDeclaredattributedescriptors()).thenReturn(asList(partOfAttributeDescriptor));
		when(cmsSubClassComposedType.getCode()).thenReturn(CMS_SUB_TYPE_TYPE);

		when(collectionTypeModel.getItemtype()).thenReturn("CollectionType");
		when(simpleTypeModel.getItemtype()).thenReturn("someType");

		// qualifierPart : standard partOf qualifier
		mockPartOfQualifier();

		when(isCollectionPredicate.negate()).thenReturn(isCollectionPredicateNegate);

		// qualifier0: standard uuid
		when(attributeDescriptor0.getQualifier()).thenReturn(QUALIFIER_0);
		when(attributeDescriptor0.getAttributeType()).thenReturn(simpleTypeModel);
		when(attributeDescriptor0.getWritable()).thenReturn(true);
		when(attributeDescriptor0.getProperty()).thenReturn(true);
		when(attributeDescriptor0.getItemtype()).thenReturn(AttributeDescriptorModel._TYPECODE);
		when(attributeDescriptorModelHelperService.getAttributeClass(attributeDescriptor0))
				.thenReturn((Class) attributeDescriptor0.getClass());
		setupMockIsCollection(attributeDescriptor0, false);

		// qualifier1: partOf attribute
		when(attributeDescriptor1.getQualifier()).thenReturn(QUALIFIER_1);
		when(attributeDescriptor1.getAttributeType()).thenReturn(simpleTypeModel);
		when(attributeDescriptor1.getPartOf()).thenReturn(true);
		when(attributeDescriptor1.getWritable()).thenReturn(true);
		when(attributeDescriptor1.getProperty()).thenReturn(true);
		when(attributeDescriptor1.getItemtype()).thenReturn(AttributeDescriptorModel._TYPECODE);
		when(attributeDescriptorModelHelperService.getAttributeClass(attributeDescriptor1))
				.thenReturn((Class) attributeDescriptor1.getClass());
		setupMockIsCollection(attributeDescriptor1, false);

		// qualifier2: localized attribute
		when(attributeDescriptor2.getQualifier()).thenReturn(QUALIFIER_2);
		when(attributeDescriptor2.getAttributeType()).thenReturn(simpleTypeModel);
		when(attributeDescriptor2.getLocalized()).thenReturn(true);
		when(attributeDescriptor2.getWritable()).thenReturn(true);
		when(attributeDescriptor2.getProperty()).thenReturn(true);
		when(attributeDescriptor2.getItemtype()).thenReturn(AttributeDescriptorModel._TYPECODE);
		when(attributeDescriptorModelHelperService.getAttributeClass(attributeDescriptor2))
				.thenReturn((Class) attributeDescriptor2.getClass());
		setupMockIsCollection(attributeDescriptor2, false);

		// qualifier3: localized partOf attribute
		when(attributeDescriptor3.getQualifier()).thenReturn(QUALIFIER_3);
		when(attributeDescriptor3.getAttributeType()).thenReturn(simpleTypeModel);
		when(attributeDescriptor3.getPartOf()).thenReturn(true);
		when(attributeDescriptor3.getLocalized()).thenReturn(true);
		when(attributeDescriptor3.getWritable()).thenReturn(true);
		when(attributeDescriptor3.getProperty()).thenReturn(true);
		when(attributeDescriptor3.getItemtype()).thenReturn(AttributeDescriptorModel._TYPECODE);
		setupMockIsCollection(attributeDescriptor3, false);

		// qualifier4: collection of uuids
		when(attributeDescriptor4.getQualifier()).thenReturn(QUALIFIER_4);
		when(attributeDescriptor4.getAttributeType()).thenReturn(collectionTypeModel);
		when(attributeDescriptor4.getWritable()).thenReturn(true);
		when(attributeDescriptor4.getProperty()).thenReturn(true);
		when(attributeDescriptor4.getItemtype()).thenReturn(AttributeDescriptorModel._TYPECODE);
		when(attributeDescriptorModelHelperService.getAttributeClass(attributeDescriptor4)).thenReturn((Class) LinkedList.class);
		setupMockIsCollection(attributeDescriptor4, true);

		// qualifier5: collection of partOf attributes
		when(attributeDescriptor5.getQualifier()).thenReturn(QUALIFIER_5);
		when(attributeDescriptor5.getAttributeType()).thenReturn(collectionTypeModel);
		when(attributeDescriptor5.getPartOf()).thenReturn(true);
		when(attributeDescriptor5.getWritable()).thenReturn(true);
		when(attributeDescriptor5.getProperty()).thenReturn(true);
		when(attributeDescriptor5.getItemtype()).thenReturn(AttributeDescriptorModel._TYPECODE);
		setupMockIsCollection(attributeDescriptor5, true);

		// qualifier6: localized collection of uuids
		when(attributeDescriptor6.getQualifier()).thenReturn(QUALIFIER_6);
		when(attributeDescriptor6.getAttributeType()).thenReturn(collectionTypeModel);
		when(attributeDescriptor6.getLocalized()).thenReturn(true);
		when(attributeDescriptor6.getWritable()).thenReturn(true);
		when(attributeDescriptor6.getProperty()).thenReturn(true);
		when(attributeDescriptor6.getItemtype()).thenReturn(AttributeDescriptorModel._TYPECODE);
		when(attributeDescriptorModelHelperService.getAttributeClass(attributeDescriptor6)).thenReturn((Class) LinkedList.class);
		setupMockIsCollection(attributeDescriptor6, true);

		//qualifier7: localized collection of partOf attributes
		when(attributeDescriptor7.getQualifier()).thenReturn(QUALIFIER_7);
		when(attributeDescriptor7.getAttributeType()).thenReturn(collectionTypeModel);
		when(attributeDescriptor7.getPartOf()).thenReturn(true);
		when(attributeDescriptor7.getLocalized()).thenReturn(true);
		when(attributeDescriptor7.getWritable()).thenReturn(true);
		when(attributeDescriptor7.getProperty()).thenReturn(true);
		when(attributeDescriptor7.getItemtype()).thenReturn(AttributeDescriptorModel._TYPECODE);
		setupMockIsCollection(attributeDescriptor7, true);

		//qualifer8: collection of Map<String, String> attributes
		when(attributeDescriptor8.getQualifier()).thenReturn(QUALIFIER_8);
		when(attributeDescriptor8.getAttributeType()).thenReturn(collectionTypeModel);
		when(attributeDescriptor8.getWritable()).thenReturn(true);
		when(attributeDescriptor8.getProperty()).thenReturn(true);
		when(attributeDescriptor8.getItemtype()).thenReturn(AttributeDescriptorModel._TYPECODE);
		when(attributeDescriptorModelHelperService.getAttributeClass(attributeDescriptor8)).thenReturn((Class) LinkedList.class);
		setupMockIsCollection(attributeDescriptor8, true);

		//qualifier_not_writable: non-writable attribute -> only important when converting from Data to model.
		when(nonWritableAttributeDescriptor.getQualifier()).thenReturn(QUALIFIER_NOT_WRITABLE);
		when(nonWritableAttributeDescriptor.getAttributeType()).thenReturn(simpleTypeModel);
		when(nonWritableAttributeDescriptor.getWritable()).thenReturn(false);
		when(nonWritableAttributeDescriptor.getProperty()).thenReturn(true);
		when(nonWritableAttributeDescriptor.getItemtype()).thenReturn(AttributeDescriptorModel._TYPECODE);
		setupMockIsCollection(nonWritableAttributeDescriptor, false);

		//qualifier_dynamic: dynamic attribute - not stored in database -> only important when converting from Data to model.
		when(dynamicAttributeDescriptor.getQualifier()).thenReturn(QUALIFIER_DYNAMIC);
		when(dynamicAttributeDescriptor.getWritable()).thenReturn(true);
		when(dynamicAttributeDescriptor.getProperty()).thenReturn(false);
		when(dynamicAttributeDescriptor.getItemtype()).thenReturn(AttributeDescriptorModel._TYPECODE);
		setupMockIsCollection(dynamicAttributeDescriptor, false);

		when(cmsItemProperty1.getPk()).thenReturn(fromLong(1));
		when(cmsItemProperty2.getPk()).thenReturn(fromLong(12));
		when(cmsItemProperty3.getPk()).thenReturn(fromLong(89));
		when(modifiedEmbeddedProperty.getPk()).thenReturn(fromLong(123));

		when(modelService.getAttributeValue(cmsItem, QUALIFIER_0)).thenReturn(cmsItemProperty1);
		when(modelService.getAttributeValue(cmsItem, QUALIFIER_1)).thenReturn(modifiedCmsItemPartOfProperty);
		when(modelService.getAttributeValue(cmsItem, QUALIFIER_2, ENGLISH)).thenReturn(cmsItemProperty1);
		when(modelService.getAttributeValue(cmsItem, QUALIFIER_2, FRENCH)).thenReturn(cmsItemProperty2);
		when(modelService.getAttributeValue(cmsItem, QUALIFIER_3, ENGLISH)).thenReturn(modifiedCmsItemPartOfProperty);
		when(modelService.getAttributeValue(cmsItem, QUALIFIER_3, FRENCH)).thenReturn(null);
		when(modelService.getAttributeValue(cmsItem, QUALIFIER_4)).thenReturn(asList(cmsItemProperty1, cmsItemProperty2));
		when(modelService.getAttributeValue(cmsItem, QUALIFIER_5)).thenReturn(asList(newCmsItemPartOfProperty1));
		when(modelService.getAttributeValue(cmsItem, QUALIFIER_6, ENGLISH)).thenReturn(asList(cmsItemProperty1));
		when(modelService.getAttributeValue(cmsItem, QUALIFIER_6, FRENCH)).thenReturn(asList(cmsItemProperty2));
		when(modelService.getAttributeValue(cmsItem, QUALIFIER_7, ENGLISH)).thenReturn(asList(newCmsItemPartOfProperty2));
		when(modelService.getAttributeValue(cmsItem, QUALIFIER_7, FRENCH)).thenReturn(null);
		when(modelService.getAttributeValue(cmsItem, QUALIFIER_8))
				.thenReturn(asList(modifiedEmbeddedProperty, newEmbeddedProperty));

		when(attributeStrategyConverter.getContentConverter(partOfAttributeDescriptor)).thenReturn(partOfAttributeContentConverter);
		when(attributeStrategyConverter.getContentConverter(attributeDescriptor0)).thenReturn(attributeContentConverter0);
		when(attributeStrategyConverter.getContentConverter(attributeDescriptor2)).thenReturn(attributeContentConverter2);
		when(attributeStrategyConverter.getContentConverter(attributeDescriptor4)).thenReturn(attributeContentConverter4);
		when(attributeStrategyConverter.getContentConverter(attributeDescriptor6)).thenReturn(attributeContentConverter6);
		when(attributeStrategyConverter.getContentConverter(attributeDescriptor8)).thenReturn(attributeContentConverter8);

		// These two should never even be executed. They will be filtered out before they can get executed.
		when(attributeStrategyConverter.getContentConverter(dynamicAttributeDescriptor))
				.thenReturn(dynamicAttributeContentConverter);
		when(attributeStrategyConverter.getContentConverter(nonWritableAttributeDescriptor))
				.thenReturn(nonWritableAttributeConverter);

		when(partOfAttributeContentConverter.convertModelToData(Mockito.eq(partOfAttributeDescriptor), Mockito.anyString()))
				.thenAnswer(answer -> {
					return answer.getArguments()[1];
				});
		when(partOfAttributeContentConverter.convertDataToModel(Mockito.eq(partOfAttributeDescriptor), Mockito.anyString()))
				.thenAnswer(answer -> {
					return answer.getArguments()[1];
				});

		when(attributeContentConverter0.convertModelToData(attributeDescriptor0, cmsItemProperty1)).thenReturn(UUID1);
		when(attributeContentConverter0.convertDataToModel(attributeDescriptor0, UUID1)).thenReturn(cmsItemProperty1);

		when(attributeContentConverter2.convertModelToData(attributeDescriptor2, cmsItemProperty1)).thenReturn(UUID1);
		when(attributeContentConverter2.convertDataToModel(attributeDescriptor2, UUID1)).thenReturn(cmsItemProperty1);

		when(attributeContentConverter2.convertModelToData(attributeDescriptor2, cmsItemProperty2)).thenReturn(UUID2);
		when(attributeContentConverter2.convertDataToModel(attributeDescriptor2, UUID2)).thenReturn(cmsItemProperty2);

		when(attributeContentConverter2.convertDataToModel(attributeDescriptor2, UUID3)).thenReturn(cmsItemProperty3);

		when(attributeContentConverter4.convertModelToData(attributeDescriptor4, cmsItemProperty1)).thenReturn(UUID1);
		when(attributeContentConverter4.convertDataToModel(attributeDescriptor4, UUID1)).thenReturn(cmsItemProperty1);

		when(attributeContentConverter4.convertModelToData(attributeDescriptor4, cmsItemProperty2)).thenReturn(UUID2);
		when(attributeContentConverter4.convertDataToModel(attributeDescriptor4, UUID2)).thenReturn(cmsItemProperty2);

		when(attributeContentConverter6.convertModelToData(attributeDescriptor6, cmsItemProperty1)).thenReturn(UUID1);
		when(attributeContentConverter6.convertDataToModel(attributeDescriptor6, UUID1)).thenReturn(cmsItemProperty1);

		when(attributeContentConverter6.convertModelToData(attributeDescriptor6, cmsItemProperty2)).thenReturn(UUID2);
		when(attributeContentConverter6.convertDataToModel(attributeDescriptor6, UUID2)).thenReturn(cmsItemProperty2);

		when(attributeContentConverter6.convertDataToModel(attributeDescriptor6, UUID3)).thenReturn(cmsItemProperty3);

		final Map<String, Object> modifiedEmbeddedPropertyRepresentation = new HashMap<>();
		modifiedEmbeddedPropertyRepresentation.put("someIdentifierKey", "someIdentifierValue");
		modifiedEmbeddedPropertyRepresentation.put("key1", "value1");
		modifiedEmbeddedPropertyRepresentation.put("key2", "value2");
		when(attributeContentConverter8.convertModelToData(attributeDescriptor8, modifiedEmbeddedProperty))
				.thenReturn(modifiedEmbeddedPropertyRepresentation);
		when(attributeContentConverter8.convertDataToModel(attributeDescriptor8, modifiedEmbeddedPropertyRepresentation))
				.thenReturn(modifiedEmbeddedProperty);

		final Map<String, Object> newEmbeddedPropertyRepresentation = new HashMap<>();
		newEmbeddedPropertyRepresentation.put("key1", "value3");
		newEmbeddedPropertyRepresentation.put("key2", "value4");
		when(attributeContentConverter8.convertModelToData(attributeDescriptor8, newEmbeddedProperty))
				.thenReturn(newEmbeddedPropertyRepresentation);
		when(attributeContentConverter8.convertDataToModel(attributeDescriptor8, newEmbeddedPropertyRepresentation))
				.thenReturn(newEmbeddedProperty);
	}

	protected void setupMockIsCollection(final AttributeDescriptorModel attributeDescriptor, final boolean isCollection) {
		when(isCollectionPredicate.test(attributeDescriptor)).thenReturn(isCollection);
		when(isCollectionPredicateNegate.test(attributeDescriptor)).thenReturn(!isCollection);
	}

	@Test
	public void shouldConvertItemContainingAllCombinationsOfLocalizedCollectionAndPartOf() throws IOException
	{
		final Map<String, Object> result = cmsItemConverter.convert(cmsItem);

		assertThat(result, new JSONMatcher<Map<String, Object>>(TEST_FILE_JSON));
	}

	@Test
	public void shouldNotPopulateAttributesWithoutReadPermission() throws IOException
	{
		// GIVEN
		denyReadPermissionToPrincipal(CMSItemModel._TYPECODE, QUALIFIER_0);

		// WHEN
		final Map<String, Object> result = cmsItemConverter.convert(cmsItem);

		// THEN
		assertThat(result, not(empty()));
		assertFalse(result.containsKey(QUALIFIER_0));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldOnlyPopulateLanguagesWithReadPermission()
	{
		// GIVEN -- Only French is readable for this test.
		when(cmsUserService.getReadableLanguagesForCurrentUser()).thenReturn(new HashSet<>(java.util.Collections.singletonList(FR)));

		// WHEN
		final Map<String, Object> result = cmsItemConverter.convert(cmsItem);

		// THEN
		// --> Qualifier 2
		final HashMap<String, String> qualifier2Map = (HashMap<String, String>) result.get(QUALIFIER_2);
		assertFalse(qualifier2Map.containsKey(EN));
		assertThat(qualifier2Map.get(FR), is(UUID2));

		// --> Qualifier 3
		final HashMap<String, String> qualifier3Map = (HashMap<String, String>) result.get(QUALIFIER_3);
		assertFalse(qualifier3Map.containsKey(EN));
		assertThat(qualifier3Map.get(FR), nullValue());

		// --> Qualifier 6
		final HashMap<String, List<String>> qualifier6Map = (HashMap<String, List<String>>) result.get(QUALIFIER_6);
		assertFalse(qualifier6Map.containsKey(EN));
		assertThat(qualifier6Map.get(FR), contains(UUID2));

		// --> Qualifier 7
		final HashMap<String, List<String>> qualifier7Map = (HashMap<String, List<String>>) result.get(QUALIFIER_7);
		assertFalse(qualifier7Map.containsKey(EN));
		assertThat(qualifier7Map.get(FR), nullValue());
	}

	@Test
	public void shouldNotPopulateAttributesWithoutReadTypePermission() throws IOException
	{
		// GIVEN
		denyReadTypePermissionToPrincipal(attributeDescriptor1, PermissionsConstants.READ);

		// WHEN
		final Map<String, Object> result = cmsItemConverter.convert(cmsItem);

		// THEN
		assertThat(result, not(empty()));
		assertFalse(result.containsKey(QUALIFIER_1));
	}

	@Test
	public void shouldConvertItemModelToNullWhenAttributeContentConverterReturnsNull()
	{
		final ComposedTypeModel superComposedType = mock(ComposedTypeModel.class);
		when(superComposedType.getDeclaredattributedescriptors()).thenReturn(asList(attributeDescriptor));
		when(attributeDescriptor.getQualifier()).thenReturn(QUALIFIER);
		when(attributeDescriptor.getAttributeType()).thenReturn(simpleTypeModel);
		when(attributeDescriptor.getWritable()).thenReturn(true);
		when(attributeDescriptor.getProperty()).thenReturn(true);
		when(attributeDescriptor.getItemtype()).thenReturn(AttributeDescriptorModel._TYPECODE);
		when(attributeDescriptorModelHelperService.getAttributeClass(attributeDescriptor)).thenReturn((Class) LinkedList.class);
		when(modelService.getAttributeValue(cmsItem, QUALIFIER)).thenReturn(cmsItemProperty1);
		when(attributeStrategyConverter.getContentConverter(attributeDescriptor)).thenReturn(attributeContentConverter9);
		when(attributeContentConverter9.convertModelToData(attributeDescriptor, cmsItemProperty1)).thenReturn(null);

		final Map<String, Object> result = cmsItemConverter.convert(cmsItem);

		assertThat(result.values(), not(nullValue()));
		assertThat(result.get(QUALIFIER), nullValue());
	}

	@Test
	public void shouldConvertItemModelToNullWhenAttributeContentConverterNotFound()
	{
		final ComposedTypeModel superComposedType = mock(ComposedTypeModel.class);
		when(superComposedType.getDeclaredattributedescriptors()).thenReturn(asList(attributeDescriptor));
		when(attributeDescriptor.getQualifier()).thenReturn(QUALIFIER);
		when(attributeDescriptor.getAttributeType()).thenReturn(simpleTypeModel);
		when(attributeDescriptor.getWritable()).thenReturn(true);
		when(attributeDescriptor.getProperty()).thenReturn(true);
		when(attributeDescriptor.getItemtype()).thenReturn(AttributeDescriptorModel._TYPECODE);
		when(attributeDescriptorModelHelperService.getAttributeClass(attributeDescriptor)).thenReturn((Class) LinkedList.class);
		when(attributeStrategyConverter.getContentConverter(attributeDescriptor)).thenReturn(null);

		final Map<String, Object> result = cmsItemConverter.convert(cmsItem);

		assertThat(result.values(), not(nullValue()));
		assertThat(result.get(QUALIFIER), nullValue());
	}

	@Test
	public void shouldConvertMapContainingAllCombinationsOfLocalizedCollectionAndPartOf() throws IOException
	{
		// Adds two invalid attribute descriptors
		mainComposedTypeAttributeDescriptorsList.addAll(Arrays.asList(dynamicAttributeDescriptor, nonWritableAttributeDescriptor));
		when(cloneComponentContextProvider.isInitialized()).thenReturn(FALSE);

		final Map<String, Object> map = readJsonFromFile();
		final ItemModel converted = cmsItemConverter.convert(map);

		assertThat("the returned converted map was expected to be cmsITEM", converted, is(cmsItem));

		//preparing a map of key/Values of the root object
		verify(modelService, times(9)).setAttributeValue(eq(converted), propertyCator.capture(), valueCaptor.capture());
		final List<String> propertyValues = propertyCator.getAllValues();
		final List<Object> valueValues = valueCaptor.getAllValues();
		final Map<String, Object> captorMap = new HashMap<>();
		final Iterator<Object> valueIterator = valueValues.iterator();
		propertyValues.forEach(property -> {
			captorMap.put(property, valueIterator.next());
		});

		//assert on final POJO content

		assertThat(captorMap.get(QUALIFIER_0), is(cmsItemProperty1));
		assertThat(captorMap.get(QUALIFIER_1), is(modifiedCmsItemPartOfProperty));

		final Map<Locale, ItemModel> localized = (Map<Locale, ItemModel>) captorMap.get(QUALIFIER_2);
		assertThat("localizedMap was expected to have 2 entries", localized.size(), is(2));
		assertThat(localized.get(ENGLISH), is(cmsItemProperty1));
		assertThat(localized.get(FRENCH), is(cmsItemProperty2));

		final Map<Locale, ItemModel> localizedOfPartOf = (Map<Locale, ItemModel>) captorMap.get(QUALIFIER_3);
		assertThat("localizedOfpartOf was expected to have 2 entries", localized.size(), is(2));
		assertThat(localizedOfPartOf.get(FRENCH), is((ItemModel) null));
		assertThat(localizedOfPartOf.get(ENGLISH), is(modifiedCmsItemPartOfProperty));

		assertThat("collection was expected to contain cmsItemProperty1 and cmsItemProperty2",
				(Collection<ItemModel>) captorMap.get(QUALIFIER_4), contains(cmsItemProperty1, cmsItemProperty2));

		final Collection<ItemModel> collectionPartOf = (Collection<ItemModel>) captorMap.get(QUALIFIER_5);
		assertThat("collectionPartOf was expected to have 1 entry", collectionPartOf.size(), is(1));
		final ItemModel collectionPartOf1 = collectionPartOf.iterator().next();
		assertThat("collectionPartOf1 should be of type SubClass", SubClass.class.isAssignableFrom(collectionPartOf1.getClass()),
				is(true));
		verify(modelService, times(1)).setAttributeValue(collectionPartOf1, QUALIFIER_PART_OF, "someString1");

		final Map<Locale, Collection<ItemModel>> localizedCollection = (Map<Locale, Collection<ItemModel>>) captorMap
				.get(QUALIFIER_6);
		assertThat("localizedCollection was expected to have 2 entries", localizedCollection.size(), is(2));
		assertThat(localizedCollection.get(ENGLISH), containsInAnyOrder(cmsItemProperty1));
		assertThat(localizedCollection.get(FRENCH), containsInAnyOrder(cmsItemProperty2));

		final Map<Locale, Collection<ItemModel>> localizedCollectionOfPartOf = (Map<Locale, Collection<ItemModel>>) captorMap
				.get(QUALIFIER_7);
		assertThat("localizedCollectionOfPartOf was expected to have 2 entries", localizedCollectionOfPartOf.size(), is(2));
		assertThat(localizedCollectionOfPartOf.get(FRENCH), is((Collection<ItemModel>) null));
		final Collection<ItemModel> localizedCollectionOfPartOfEnglish = localizedCollectionOfPartOf.get(ENGLISH);
		assertThat("localizedCollectionOfPartOfEnglish was expected to have 1 entry", localizedCollectionOfPartOfEnglish.size(),
				is(1));
		final ItemModel englishPartOf = localizedCollectionOfPartOfEnglish.iterator().next();
		assertThat("localizedCollectionOfPartOfEnglish should be of type SubClass",
				CMSItemSubClass.class.isAssignableFrom(englishPartOf.getClass()), is(true));
		//englishPartOf should be populated with content from map
		verify(modelService).setAttributeValue(englishPartOf, QUALIFIER_PART_OF, "someString2");

		assertThat((Collection<ItemModel>) captorMap.get(QUALIFIER_8), contains(modifiedEmbeddedProperty, newEmbeddedProperty));
		verify(modelService, times(2)).setAttributeValue(modifiedCmsItemPartOfProperty, QUALIFIER_PART_OF, "someString0");
		verify(modelService, never()).setAttributeValue(eq(cmsItem), eq(QUALIFIER_10), anyObject());

		//ASSERT on persistence calls from BOTTOM to TOP
		verify(modelService, never()).save(Matchers.any(ItemModel.class));
		verify(cmsAdminItemService).createItem(CMSItemSubClass.class);
		verify(cmsItemValidatorCreate, times(2)).validate(any(ItemModel.class));

		// ASSERT invalid parameters were evaluated
		verify(nonWritableAttributeDescriptor).getWritable();
		verify(dynamicAttributeDescriptor).getProperty();
	}

	@Test
	@SuppressWarnings("unchecked")
	public void givenUserHasWritePermissionToSomeLanguages_WhenConvertingFromMapToModel_ThenItShouldConvertAllValuesButIgnoreLanguagesWithoutWritePermission() throws IOException
	{
		// GIVEN
		final Map<String, Object> rawMap = readJsonFromFile();

		when(cmsUserService.getWriteableLanguagesForCurrentUser()).thenReturn(new HashSet<>(java.util.Collections.singletonList(EN)));

		/*
			When a user doesn't have write permissions an all languages, the system only sets the values for which the user does have
			permissions. For the rest, the system uses the existing values.
			Since this test suite is used for both, model-to-data and data-to-model, it'd be easy to confuse the values that were
			actually updated with the original mock values. Thus, we set new values here to avoid any confusion.
		 */
		// --> Qualifier 2
		final HashMap<String, String> qualifier2Map = (HashMap<String, String>) rawMap.get(QUALIFIER_2);
		qualifier2Map.put(EN, UUID3);
		qualifier2Map.put(FR, UUID3);

		// --> Qualifier 3
		final HashMap<String, Object> qualifier3Map = (HashMap<String, Object>) rawMap.get(QUALIFIER_3);
		final Object qualifier3OriginalFrenchValue = qualifier3Map.get(FR);
		qualifier3Map.put(FR, qualifier3Map.get(EN)); // Swap values
		qualifier3Map.put(EN, qualifier3OriginalFrenchValue);

		// --> Qualifier 6
		final HashMap<String, List<String>> qualifier6Map = (HashMap<String, List<String>>) rawMap.get(QUALIFIER_6);
		qualifier6Map.put(FR, java.util.Collections.singletonList(UUID3));
		qualifier6Map.put(EN, java.util.Collections.singletonList(UUID3));

		// --> Qualifier 7
		final HashMap<String, List<Object>> qualifier7Map = (HashMap<String, List<Object>>) rawMap.get(QUALIFIER_7);
		final List<Object> qualifier7OriginalFrenchValue = qualifier7Map.get(FR);
		qualifier7Map.put(FR, qualifier7Map.get(EN)); // Swap values
		qualifier7Map.put(EN, qualifier7OriginalFrenchValue);

		// WHEN
		final ItemModel converted = cmsItemConverter.convert(rawMap);

		// THEN
		assertThat("the returned converted map was expected to be cmsITEM", converted, is(cmsItem));

		verify(modelService, times(9)).setAttributeValue(eq(converted), propertyCator.capture(), valueCaptor.capture());
		final List<String> propertyValues = propertyCator.getAllValues();
		final List<Object> valueValues = valueCaptor.getAllValues();
		final Map<String, Object> captorMap = new HashMap<>();
		final Iterator<Object> valueIterator = valueValues.iterator();
		propertyValues.forEach(property -> {
			captorMap.put(property, valueIterator.next());
		});

		// --> Qualifier 2
		final HashMap<Locale, Object> qualifier2ConvertedMap = (HashMap<Locale, Object>)captorMap.get(QUALIFIER_2);
		assertThat(qualifier2ConvertedMap.get(Locale.FRENCH), is(cmsItemProperty2)); // Not changed
		assertThat(qualifier2ConvertedMap.get(Locale.ENGLISH), is(cmsItemProperty3));

		// --> Qualifier 3
		final HashMap<Locale, Object> qualifier3ConvertedMap = (HashMap<Locale, Object>)captorMap.get(QUALIFIER_3);
		assertNull(qualifier3ConvertedMap.get(Locale.FRENCH)); // Not changed
		assertNull(qualifier3ConvertedMap.get(Locale.ENGLISH));

		// --> Qualifier 6
		final HashMap<Locale, List<Object>> qualifier6ConvertedMap = (HashMap<Locale, List<Object>>)captorMap.get(QUALIFIER_6);
		assertThat(qualifier6ConvertedMap.get(Locale.FRENCH).size(), is(1));
		assertThat(qualifier6ConvertedMap.get(Locale.ENGLISH).size(), is(1));
		assertThat(qualifier6ConvertedMap.get(Locale.FRENCH).get(0), is(cmsItemProperty2)); // Not changed
		assertThat(qualifier6ConvertedMap.get(Locale.ENGLISH).get(0), is(cmsItemProperty3));

		// --> Qualifier 7
		final HashMap<Locale, List<Object>> qualifier7ConvertedMap = (HashMap<Locale, List<Object>>)captorMap.get(QUALIFIER_7);
		assertNull(qualifier7ConvertedMap.get(Locale.FRENCH)); // Not changed
		assertNull(qualifier7ConvertedMap.get(Locale.ENGLISH));
	}

	// Helper methods
	protected ItemData getItemData(final String itemId)
	{
		final ItemData itemData = new ItemData();
		itemData.setItemId(itemId);
		return itemData;
	}

	protected Map<String, Object> readJsonFromFile() throws IOException
	{
		final ObjectMapper mapper = new ObjectMapper();
		try (final InputStream inputStream = getClass().getResourceAsStream(TEST_FILE_JSON))
		{
			final Map<String, Object> map = mapper.readValue(inputStream, HashMap.class);
			return map;
		}
	}


	// Mock helper methods
	protected void mockLanguages()
	{
		final LanguageData languageEN = new LanguageData();
		languageEN.setIsocode(EN);
		final LanguageData languageFR = new LanguageData();
		languageFR.setIsocode(FR);
		when(languageFacade.getLanguages()).thenReturn(Lists.newArrayList(languageEN, languageFR));
		when(commonI18NService.getLocaleForIsoCode(EN)).thenReturn(ENGLISH);
		when(commonI18NService.getLocaleForIsoCode(FR)).thenReturn(FRENCH);
	}

	protected void mockBlacklistedTypes()
	{
		final Map<String, String> blacklistedTypesMap = new HashMap<>();
		blacklistedTypesMap.put("Item", "pk");
		composedTypeToAttributeCollectionConverter.setBlacklistedTypes(
				Collections.set("GenericItem", "ExtensibleItem", "LocalizableItem", "BridgeAbstraction", "Item"));
		composedTypeToAttributeCollectionConverter.setTypeBlacklistedAttributeMap(blacklistedTypesMap);
		cmsItemConverter.setComposedTypeToAttributeCollectionConverter(composedTypeToAttributeCollectionConverter);
	}

	protected void mockNestedAttributePredicates()
	{
		final Set<Predicate<AttributeDescriptorModel>> nestedAttributePredicateSet = new HashSet<>();
		nestedAttributePredicateSet.add(nestedAttributePredicate);
		nestedOrPartOfAttributePredicate.setNestedAttributePredicates(nestedAttributePredicateSet);
		cmsItemConverter.setLocalizedPopulator(localizedPopulator);
		cmsItemConverter.setNestedOrPartOfAttributePredicate(nestedOrPartOfAttributePredicate);
	}

	protected void mockTypeModels()
	{
		when(cmsItem.getItemtype()).thenReturn(_TYPECODE);
		when(cmsItemProperty1.getItemtype()).thenReturn(SUB_TYPE_TYPE);
		when(cmsItemProperty2.getItemtype()).thenReturn(CMS_SUB_TYPE_TYPE);
		when(modifiedCmsItemPartOfProperty.getItemtype()).thenReturn(SUB_TYPE_TYPE);
		when(newCmsItemPartOfProperty1.getItemtype()).thenReturn(SUB_TYPE_TYPE);
		when(newCmsItemPartOfProperty2.getItemtype()).thenReturn(CMS_SUB_TYPE_TYPE);

		when(typeService.getComposedTypeForCode(_TYPECODE)).thenReturn(mainComposedType);
		when(typeService.getComposedTypeForCode(SUB_TYPE_TYPE)).thenReturn(subClassComposedType);
		when(typeService.getComposedTypeForCode(CMS_SUB_TYPE_TYPE)).thenReturn(cmsSubClassComposedType);

		doReturn(MainClass.class).when(typeService).getModelClass(mainComposedType);
		doReturn(SubClass.class).when(typeService).getModelClass(subClassComposedType);
		doReturn(CMSItemSubClass.class).when(typeService).getModelClass(cmsSubClassComposedType);
	}

	protected void mockPartOfQualifier()
	{
		when(partOfAttributeDescriptor.getQualifier()).thenReturn(QUALIFIER_PART_OF);
		when(partOfAttributeDescriptor.getAttributeType()).thenReturn(simpleTypeModel);
		when(partOfAttributeDescriptor.getWritable()).thenReturn(true);
		when(partOfAttributeDescriptor.getProperty()).thenReturn(true);
		when(partOfAttributeDescriptor.getItemtype()).thenReturn(AttributeDescriptorModel._TYPECODE);
		when(attributeDescriptorModelHelperService.getAttributeClass(partOfAttributeDescriptor)).thenReturn((Class) String.class);
		setupMockIsCollection(partOfAttributeDescriptor, false);

		when(modelService.getAttributeValue(modifiedCmsItemPartOfProperty, QUALIFIER_PART_OF)).thenReturn("someString0");
		when(modelService.getAttributeValue(newCmsItemPartOfProperty1, QUALIFIER_PART_OF)).thenReturn("someString1");
		when(modelService.getAttributeValue(newCmsItemPartOfProperty2, QUALIFIER_PART_OF)).thenReturn("someString2");
	}

	protected void mockValidationServiceAndErrors()
	{
		when(validationErrorsProvider.getCurrentValidationErrors()).thenReturn(mock(ValidationErrors.class));

		doAnswer((invocationOnMock) -> {
			final Object[] args = invocationOnMock.getArguments();
			return ((Supplier<?>) args[0]).get();
		}).when(validatableService).execute(any());
	}

	protected void mockUniqueItemIdentifierServiceAndPopulator()
	{
		when(uniqueItemIdentifierService.getItemData(any(ItemModel.class))).then(answer -> {
			final ItemModel item = (ItemModel) answer.getArguments()[0];
			if (item == cmsItem)
			{
				return ofNullable(getItemData(UUID_MAIN));
			}
			else if (item == modifiedCmsItemPartOfProperty)
			{
				return ofNullable(getItemData(UUID_PART_OF));
			}
			else
			{
				return empty();
			}
		});

		when(uniqueItemIdentifierService.getItemModel(anyString(), anyObject())).then(answer -> {
			final String uuid = (String) answer.getArguments()[0];
			final Class<? extends ItemModel> modelClass = (Class<? extends ItemModel>) answer.getArguments()[1];

			if (uuid.equals(UUID_MAIN) && modelClass == MainClass.class)
			{
				return ofNullable(cmsItem);
			}
			else if (uuid.equals(UUID_PART_OF) && modelClass == SubClass.class)
			{
				return ofNullable(modifiedCmsItemPartOfProperty);
			}
			else
			{
				return empty();
			}
		});

		doAnswer(params -> {
			final ItemModel source = params.getArgumentAt(0, ItemModel.class);
			final Map<String, Object> objectMap = params.getArgumentAt(1, Map.class);

			uniqueItemIdentifierService.getItemData(source) //
					.ifPresent(itemData -> objectMap.put(FIELD_UUID, itemData.getItemId()));

			return null;
		}).when(uniqueIdentifierAttributePopulator).populate(any(), any());
	}

	protected void denyReadPermissionToPrincipal(final String typeCode, final String qualifier)
	{
		when(permissionCRUDService.canReadAttribute(typeCode, qualifier)).thenReturn(false);
	}

	protected void denyReadTypePermissionToPrincipal(final AttributeDescriptorModel attributeDescriptorModel,
			final String permissionName)
	{
		when(cmsPermissionChecker.hasPermissionForContainedType(attributeDescriptorModel, permissionName)).thenReturn(false);
	}

}
