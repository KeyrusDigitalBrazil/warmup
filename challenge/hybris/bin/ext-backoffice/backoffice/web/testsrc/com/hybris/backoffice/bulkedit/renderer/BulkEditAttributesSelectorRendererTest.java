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
package com.hybris.backoffice.bulkedit.renderer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.enums.RelationEndCardinalityEnum;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.RelationDescriptorModel;
import de.hybris.platform.core.model.type.RelationMetaTypeModel;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.zkoss.zul.Div;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hybris.backoffice.attributechooser.Attribute;
import com.hybris.backoffice.attributechooser.AttributeChooserRenderer;
import com.hybris.backoffice.bulkedit.BulkEditForm;
import com.hybris.backoffice.widgets.notificationarea.NotificationService;
import com.hybris.cockpitng.config.jaxb.wizard.ViewType;
import com.hybris.cockpitng.dataaccess.facades.permissions.PermissionFacade;
import com.hybris.cockpitng.dataaccess.facades.type.DataAttribute;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.dataaccess.facades.type.TypeFacade;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.i18n.CockpitLocaleService;
import com.hybris.cockpitng.testing.util.CockpitTestUtil;


@RunWith(MockitoJUnitRunner.class)
public class BulkEditAttributesSelectorRendererTest
{
	@Mock
	private AttributeChooserRenderer attributeChooserRenderer;
	@Mock
	private CockpitLocaleService localeService;
	@Mock
	private PermissionFacade permissionFacade;
	@Mock
	private TypeFacade typeFacade;
	@Mock
	private NotificationService notificationService;
	@Mock
	private TypeService typeService;
	@InjectMocks
	private BulkEditAttributesSelectorRenderer renderer;
	private WidgetInstanceManager wim;

	@Before
	public void setUp()
	{
		wim = CockpitTestUtil.mockWidgetInstanceManager();
	}

	@Test
	public void allowsAttributesWithWritePermissionsForAllTypes()
	{
		final Set<String> allAttributes = Sets.newHashSet(ProductModel.APPROVALSTATUS, ProductModel.CATALOGVERSION,
				ProductModel.ARTICLESTATUS);
		final DataAttribute approval = mockDataAttribute(ProductModel.APPROVALSTATUS);
		final DataAttribute catalogVersion = mockDataAttribute(ProductModel.CATALOGVERSION);
		final DataAttribute articleStatus = mockDataAttribute(ProductModel.ARTICLESTATUS);

		final DataType dataType = mockDataTypeWithAttributes(ProductModel._TYPECODE,
				Lists.newArrayList(approval, catalogVersion, articleStatus));

		final ItemModel product = mockItem(ProductModel._TYPECODE, allAttributes, Sets.newLinkedHashSet());
		final ItemModel variant = mockItem(VariantProductModel._TYPECODE,
				Sets.newHashSet(ProductModel.APPROVALSTATUS, ProductModel.CATALOGVERSION),
				Sets.newHashSet(ProductModel.ARTICLESTATUS));

		final BulkEditForm bulkEditForm = new BulkEditForm();
		bulkEditForm.setItemsToEdit(Lists.newArrayList(product, variant));


		final HashMap<String, String> params = new HashMap<>();
		params.put(BulkEditAttributesSelectorRenderer.PARAM_BULK_EDIT_FORM_MODEL_KEY, "bulkEditForm");
		wim.getModel().setValue("bulkEditForm", bulkEditForm);

		renderer.render(new Div(), new ViewType(), params, dataType, wim);

		assertThat(bulkEditForm.getAttributesForm().getAvailableAttributes().stream().map(Attribute::getQualifier)
				.collect(Collectors.toSet())).containsOnly(ProductModel.CATALOGVERSION, ProductModel.APPROVALSTATUS);
	}

	@Test
	public void allowAttributeWhichAreEditAble()
	{

		final DataAttribute notWritable = mockDataAttribute("notWritable", false, false, false, false);
		final DataAttribute partOf = mockDataAttribute("partOf", true, true, false, false);
		final DataAttribute writeThrough = mockDataAttribute("writeThrough", true, false, true, false);
		final DataAttribute writeThroughOnCreation = mockDataAttribute("writeThroughOnCreation", true, false, false, true);
		final DataAttribute articleStatus = mockDataAttribute(ProductModel.ARTICLESTATUS);

		final ArrayList<DataAttribute> attributes = Lists.newArrayList(notWritable, partOf, writeThrough, writeThroughOnCreation,
				articleStatus);
		final DataType dataType = mockDataTypeWithAttributes(ProductModel._TYPECODE, attributes);

		final ItemModel product = mockItem(ProductModel._TYPECODE, attributes, Sets.newLinkedHashSet());

		final BulkEditForm bulkEditForm = new BulkEditForm();
		bulkEditForm.setItemsToEdit(Lists.newArrayList(product));


		final HashMap<String, String> params = new HashMap<>();
		params.put(BulkEditAttributesSelectorRenderer.PARAM_BULK_EDIT_FORM_MODEL_KEY, "bulkEditForm");
		wim.getModel().setValue("bulkEditForm", bulkEditForm);

		renderer.render(new Div(), new ViewType(), params, dataType, wim);

		assertThat(bulkEditForm.getAttributesForm().getAvailableAttributes().stream().map(Attribute::getQualifier)
				.collect(Collectors.toSet())).containsOnly(ProductModel.ARTICLESTATUS, "writeThrough", "writeThroughOnCreation");
	}

	@Test
	public void doNotAllowManyToOne()
	{
		final String manyToOneAttrName = "manyToOne";
		final RelationDescriptorModel attributeDescriptor = mock(RelationDescriptorModel.class);
		final RelationMetaTypeModel relationMetaType = mock(RelationMetaTypeModel.class);
		when(attributeDescriptor.getRelationType()).thenReturn(relationMetaType);
		when(relationMetaType.getSourceTypeRole()).thenReturn(manyToOneAttrName);
		when(relationMetaType.getSourceTypeCardinality()).thenReturn(RelationEndCardinalityEnum.MANY);
		when(relationMetaType.getTargetTypeCardinality()).thenReturn(RelationEndCardinalityEnum.ONE);

		when(typeService.getAttributeDescriptor(ProductModel._TYPECODE, manyToOneAttrName)).thenReturn(attributeDescriptor);

		final DataAttribute manyToOne = mockDataAttribute(manyToOneAttrName, true, false, false, false);
		final DataAttribute articleStatus = mockDataAttribute(ProductModel.ARTICLESTATUS);

		final ArrayList<DataAttribute> attributes = Lists.newArrayList(manyToOne, articleStatus);
		final DataType dataType = mockDataTypeWithAttributes(ProductModel._TYPECODE, attributes);

		final ItemModel product = mockItem(ProductModel._TYPECODE, attributes, Sets.newLinkedHashSet());

		final BulkEditForm bulkEditForm = new BulkEditForm();
		bulkEditForm.setItemsToEdit(Lists.newArrayList(product));

		final HashMap<String, String> params = new HashMap<>();
		params.put(BulkEditAttributesSelectorRenderer.PARAM_BULK_EDIT_FORM_MODEL_KEY, "bulkEditForm");
		wim.getModel().setValue("bulkEditForm", bulkEditForm);

		renderer.render(new Div(), new ViewType(), params, dataType, wim);

		assertThat(bulkEditForm.getAttributesForm().getAvailableAttributes().stream().map(Attribute::getQualifier)
				.collect(Collectors.toSet())).containsOnly(ProductModel.ARTICLESTATUS);
	}

	@Test
	public void allowOneToMany()
	{
		final String oneToMany = "oneToMany";
		final RelationDescriptorModel attributeDescriptor = mock(RelationDescriptorModel.class);
		final RelationMetaTypeModel relationMetaType = mock(RelationMetaTypeModel.class);
		when(attributeDescriptor.getRelationType()).thenReturn(relationMetaType);
		when(relationMetaType.getSourceTypeRole()).thenReturn(oneToMany);
		when(relationMetaType.getSourceTypeCardinality()).thenReturn(RelationEndCardinalityEnum.ONE);
		when(relationMetaType.getTargetTypeCardinality()).thenReturn(RelationEndCardinalityEnum.MANY);

		when(typeService.getAttributeDescriptor(ProductModel._TYPECODE, oneToMany)).thenReturn(attributeDescriptor);

		final DataAttribute manyToOne = mockDataAttribute(oneToMany, true, false, false, false);
		final DataAttribute articleStatus = mockDataAttribute(ProductModel.ARTICLESTATUS);

		final ArrayList<DataAttribute> attributes = Lists.newArrayList(manyToOne, articleStatus);
		final DataType dataType = mockDataTypeWithAttributes(ProductModel._TYPECODE, attributes);

		final ItemModel product = mockItem(ProductModel._TYPECODE, attributes, Sets.newLinkedHashSet());

		final BulkEditForm bulkEditForm = new BulkEditForm();
		bulkEditForm.setItemsToEdit(Lists.newArrayList(product));

		final HashMap<String, String> params = new HashMap<>();
		params.put(BulkEditAttributesSelectorRenderer.PARAM_BULK_EDIT_FORM_MODEL_KEY, "bulkEditForm");
		wim.getModel().setValue("bulkEditForm", bulkEditForm);

		renderer.render(new Div(), new ViewType(), params, dataType, wim);

		assertThat(bulkEditForm.getAttributesForm().getAvailableAttributes().stream().map(Attribute::getQualifier)
				.collect(Collectors.toSet())).containsOnly(ProductModel.ARTICLESTATUS, oneToMany);
	}

	@Test
	public void localizedAttributeWithSubLanguages()
	{

		when(permissionFacade.getAllWritableLocalesForCurrentUser())
				.thenReturn(Sets.newHashSet(Locale.ENGLISH, Locale.TRADITIONAL_CHINESE));

		final DataAttribute articleStatus = mockDataAttribute(ProductModel.ARTICLESTATUS);
		when(articleStatus.isLocalized()).thenReturn(true);

		final ArrayList<DataAttribute> attributes = Lists.newArrayList(articleStatus);
		final DataType dataType = mockDataTypeWithAttributes(ProductModel._TYPECODE, attributes);

		final ItemModel product = mockItem(ProductModel._TYPECODE, attributes, Sets.newLinkedHashSet());

		final BulkEditForm bulkEditForm = new BulkEditForm();
		bulkEditForm.setItemsToEdit(Lists.newArrayList(product));


		final HashMap<String, String> params = new HashMap<>();
		params.put(BulkEditAttributesSelectorRenderer.PARAM_BULK_EDIT_FORM_MODEL_KEY, "bulkEditForm");
		wim.getModel().setValue("bulkEditForm", bulkEditForm);

		renderer.render(new Div(), new ViewType(), params, dataType, wim);

		assertThat(bulkEditForm.getAttributesForm().getAvailableAttributes()).hasSize(1);
		final Attribute attribute = bulkEditForm.getAttributesForm().getAvailableAttributes().iterator().next();
		assertThat(attribute.getQualifier()).isEqualTo(ProductModel.ARTICLESTATUS);
		assertThat(attribute.hasSubAttributes()).isTrue();
		assertThat(attribute.getSubAttributes()).hasSize(2);
		assertThat(attribute.getSubAttributes().stream().map(Attribute::getIsoCode).collect(Collectors.toSet()))
				.containsOnly(Locale.ENGLISH.getLanguage(), Locale.TRADITIONAL_CHINESE.toLanguageTag());
	}

	private DataType mockDataTypeWithAttributes(final String typeCode, final Collection<DataAttribute> attributes)
	{
		final DataType dataType = mock(DataType.class);
		when(dataType.getCode()).thenReturn(typeCode);
		when(dataType.getAttributes()).thenReturn(attributes);
		return dataType;
	}

	private DataAttribute mockDataAttribute(final String attribute)
	{
		return mockDataAttribute(attribute, true, false, false, false);
	}

	private DataAttribute mockDataAttribute(final String attribute, final boolean writable, final boolean partOf,
			final boolean writeThrough, final boolean writeThroughOnCreation)
	{
		final DataAttribute da = mock(DataAttribute.class);
		when(da.getQualifier()).thenReturn(attribute);
		when(da.isWritable()).thenReturn(writable);
		when(da.isWritableOnCreation()).thenReturn(writeThroughOnCreation);
		when(da.isPartOf()).thenReturn(partOf);
		when(da.isWriteThrough()).thenReturn(writeThrough);
		return da;
	}

	private ItemModel mockItem(final String typeCode, final Collection<DataAttribute> writeTrueProperties,
			final Collection<DataAttribute> writeFalseProperties)
	{
		return mockItem(typeCode, writeTrueProperties.stream().map(DataAttribute::getQualifier).collect(Collectors.toSet()),
				writeFalseProperties.stream().map(DataAttribute::getQualifier).collect(Collectors.toSet()));
	}

	private ItemModel mockItem(final String typeCode, final Set<String> writeTrueProperties,
			final Set<String> writeFalseProperties)
	{
		final ItemModel item = mock(ItemModel.class);
		when(typeFacade.getType(item)).thenReturn(typeCode);
		writeTrueProperties.forEach(property -> when(permissionFacade.canChangeProperty(typeCode, property)).thenReturn(true));
		writeFalseProperties.forEach(property -> when(permissionFacade.canChangeProperty(typeCode, property)).thenReturn(false));
		return item;
	}
}
