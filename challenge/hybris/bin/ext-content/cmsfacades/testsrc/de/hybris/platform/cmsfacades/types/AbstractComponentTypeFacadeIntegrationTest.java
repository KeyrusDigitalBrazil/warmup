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
package de.hybris.platform.cmsfacades.types;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import de.hybris.platform.cms2.model.pages.CategoryPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.pages.ProductPageModel;
import de.hybris.platform.cms2.model.preview.PreviewDataModel;
import de.hybris.platform.cms2.model.restrictions.CMSCategoryRestrictionModel;
import de.hybris.platform.cms2.model.restrictions.CMSTimeRestrictionModel;
import de.hybris.platform.cmsfacades.data.ComponentTypeAttributeData;
import de.hybris.platform.cmsfacades.data.ComponentTypeData;
import de.hybris.platform.cmsfacades.data.StructureTypeCategory;
import de.hybris.platform.cmsfacades.types.impl.DefaultComponentTypeFacade;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class AbstractComponentTypeFacadeIntegrationTest
{
	// structure types
	private static final String BOOLEAN = "Boolean";
	private static final String DATE = "Date";
	private static final String EDITABLE_DROPDOWN = "EditableDropdown";
	private static final String MULTI_CATEGORY_SELECTOR = "MultiCategorySelector";
	private static final String SHORT_STRING = "ShortString";

	protected DefaultComponentTypeFacade componentTypeFacade;

	protected final Map<String, ComponentTypeData> typeBluePrint = new HashMap<>();

	public void setup()
	{
		addTruthyType(ContentPageModel._TYPECODE, StructureTypeCategory.PAGE.name(),
				Arrays.asList(newAttribute(ContentPageModel.UID, SHORT_STRING), newAttribute(ContentPageModel.NAME, SHORT_STRING),
						newAttribute(ContentPageModel.TITLE, SHORT_STRING), newAttribute(ContentPageModel.CREATIONTIME, DATE),
						newAttribute(ContentPageModel.MODIFIEDTIME, DATE), newAttribute(ContentPageModel.LABEL, SHORT_STRING)));
		addTruthyType(ProductPageModel._TYPECODE, StructureTypeCategory.PAGE.name(),
				Arrays.asList(newAttribute(ProductPageModel.UID, SHORT_STRING), newAttribute(ProductPageModel.NAME, SHORT_STRING),
						newAttribute(ProductPageModel.TITLE, SHORT_STRING), newAttribute(ProductPageModel.CREATIONTIME, DATE),
						newAttribute(ProductPageModel.MODIFIEDTIME, DATE)));
		addTruthyType(CategoryPageModel._TYPECODE, StructureTypeCategory.PAGE.name(),
				Arrays.asList(newAttribute(CategoryPageModel.UID, SHORT_STRING), newAttribute(CategoryPageModel.NAME, SHORT_STRING),
						newAttribute(CategoryPageModel.TITLE, SHORT_STRING), newAttribute(CategoryPageModel.CREATIONTIME, DATE),
						newAttribute(CategoryPageModel.MODIFIEDTIME, DATE)));

		addTruthyType(CMSTimeRestrictionModel._TYPECODE, StructureTypeCategory.RESTRICTION.name(), Arrays.asList(
				newAttribute(CMSTimeRestrictionModel.UID, SHORT_STRING), newAttribute(CMSTimeRestrictionModel.NAME, SHORT_STRING),
				newAttribute(CMSTimeRestrictionModel.DESCRIPTION, SHORT_STRING),
				newAttribute(CMSTimeRestrictionModel.ACTIVEFROM, DATE), newAttribute(CMSTimeRestrictionModel.ACTIVEUNTIL, DATE)));
		addTruthyType(CMSCategoryRestrictionModel._TYPECODE, StructureTypeCategory.RESTRICTION.name(),
				Arrays.asList(newAttribute(CMSCategoryRestrictionModel.UID, SHORT_STRING),
						newAttribute(CMSCategoryRestrictionModel.NAME, SHORT_STRING),
						newAttribute(CMSCategoryRestrictionModel.DESCRIPTION, SHORT_STRING),
						newAttribute(CMSCategoryRestrictionModel.CATEGORIES, MULTI_CATEGORY_SELECTOR),
						newAttribute(CMSCategoryRestrictionModel.RECURSIVE, BOOLEAN)));

		addTruthyType(PreviewDataModel._TYPECODE, StructureTypeCategory.PREVIEW.name(),
				Arrays.asList(newAttribute(PreviewDataModel.PREVIEWCATALOG, EDITABLE_DROPDOWN),
						newAttribute(PreviewDataModel.LANGUAGE, EDITABLE_DROPDOWN), newAttribute(PreviewDataModel.TIME, DATE)));
	}

	protected void addTruthyType(final String typeCode, final String category, final List<ComponentTypeAttributeData> attributes)
	{
		typeBluePrint.put(typeCode, newComponentTypeData(typeCode, category, attributes));
	}

	protected ComponentTypeData newComponentTypeData(final String typeCode, final String category,
			final List<ComponentTypeAttributeData> attributes)
	{
		final ComponentTypeData componentType = new ComponentTypeData();
		componentType.setCategory(category);
		componentType.setCode(typeCode);
		componentType.setI18nKey(getI18nKey(typeCode));
		componentType.setAttributes(attributes);
		return componentType;
	}

	protected ComponentTypeAttributeData newAttribute(final String qualifier, final String cmsStructureType)
	{
		final ComponentTypeAttributeData attribute = new ComponentTypeAttributeData();
		attribute.setQualifier(qualifier);
		attribute.setCmsStructureType(cmsStructureType);
		return attribute;
	}

	public void shouldGetCategoryPageComponentType_FromAllTypes()
	{
		final List<ComponentTypeData> componentTypes = componentTypeFacade.getAllComponentTypes();
		assertThat(componentTypes, hasSize(greaterThan(1)));

		final ComponentTypeData categoryPageType = componentTypes.stream()
				.filter(componentType -> CategoryPageModel._TYPECODE.equals(componentType.getCode())).findFirst().get();

		assertPage(categoryPageType, CategoryPageModel._TYPECODE);
	}

	public void shouldGetCategoryPageComponentType_FromSingleType() throws ComponentTypeNotFoundException
	{
		final ComponentTypeData categoryPageType = componentTypeFacade.getComponentTypeByCode(CategoryPageModel._TYPECODE);

		assertPage(categoryPageType, CategoryPageModel._TYPECODE);
	}

	public void shouldGetContentPageComponentType_FromSingleType() throws ComponentTypeNotFoundException
	{
		final ComponentTypeData categoryPageType = componentTypeFacade.getComponentTypeByCode(ContentPageModel._TYPECODE);

		assertPage(categoryPageType, ContentPageModel._TYPECODE);
	}

	public void testAllComponentTypeStructuresArePresent() throws ComponentTypeNotFoundException
	{
		typeBluePrint.keySet().stream().forEach(typeCode -> {
			try
			{
				final ComponentTypeData componentTypeByCode = componentTypeFacade.getComponentTypeByCode(typeCode);
				assertAvailableComponentTypeData(componentTypeByCode);
			}
			catch (final ComponentTypeNotFoundException e)
			{
				fail();
			}
		});
	}

	protected void assertAvailableComponentTypeData(final ComponentTypeData componentTypeData)
	{
		final ComponentTypeData bluePrint = typeBluePrint.get(componentTypeData.getCode());

		assertThat("Type Code is different than expected for [" + componentTypeData.getCode() + "]", componentTypeData.getCode(),
				is(bluePrint.getCode()));
		assertThat("Category is different than expected for [" + componentTypeData.getCode() + "]", componentTypeData.getCategory(),
				is(bluePrint.getCategory()));
		assertThat("Name is different than expected for [" + componentTypeData.getCode() + "]", componentTypeData.getName(),
				is(bluePrint.getName()));
		assertThat("I18n is different than expected for [" + componentTypeData.getCode() + "]", componentTypeData.getI18nKey(),
				equalTo(bluePrint.getI18nKey()));
		componentTypeData.getAttributes().stream().forEach(componentTypeAttributeData -> {
			bluePrint.getAttributes() //
					.stream() //
					.filter(attr -> attr.getQualifier().equals(componentTypeAttributeData.getQualifier())) //
					.findFirst() //
					.ifPresent(attrBluePrint -> {
						assertThat(
								"Attribute type [" + componentTypeAttributeData.getQualifier() + "] is different than expected for ["
										+ componentTypeData.getCode() + "]",
								componentTypeAttributeData.getCmsStructureType(), is(attrBluePrint.getCmsStructureType()));
					});

		});

	}

	protected void assertPage(final ComponentTypeData pageType, final String typeCode)
	{
		assertThat(pageType.getCode(), equalTo(typeCode));
		assertThat(pageType.getCategory(), equalTo(StructureTypeCategory.PAGE.name()));
		assertThat(pageType.getI18nKey(), equalTo(getI18nKey(typeCode)));
		assertPageTypeAttributes(pageType);
	}

	protected String getI18nKey(final String typeCode)
	{
		return "type." + typeCode.toLowerCase() + ".name";
	}

	/**
	 * Compares the page type attributes with the expected list of attributes defined in the {@code typeBluePrint}.
	 * Assertion passes when the content and sizes for the page type attributes and the expected list of attributes
	 * matches.
	 *
	 * @param pageType
	 *           the page type containing the attributes to be validated
	 */
	protected void assertPageTypeAttributes(final ComponentTypeData pageType)
	{
		final List<String> expectedQualifiers = getComponentTypeAttributeQualifiers(
				typeBluePrint.get(pageType.getCode()).getAttributes());
		final int count = (int) pageType.getAttributes().stream()
				.filter(attribute -> expectedQualifiers.contains(attribute.getQualifier())).count();
		assertThat(expectedQualifiers.size(), is(count));
	}

	protected List<String> getComponentTypeAttributeQualifiers(final List<ComponentTypeAttributeData> attributes)
	{
		return attributes.stream().map(attribute -> attribute.getQualifier()).collect(Collectors.toList());
	}

	public DefaultComponentTypeFacade getComponentTypeFacade()
	{
		return componentTypeFacade;
	}

	public void setComponentTypeFacade(final DefaultComponentTypeFacade componentTypeFacade)
	{
		this.componentTypeFacade = componentTypeFacade;
	}

}
