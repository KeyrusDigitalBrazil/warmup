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
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;

import com.google.common.collect.Sets;
import com.hybris.backoffice.attributechooser.Attribute;
import com.hybris.backoffice.attributechooser.AttributeChooserForm;
import com.hybris.backoffice.bulkedit.BulkEditConstants;
import com.hybris.backoffice.bulkedit.BulkEditForm;
import com.hybris.backoffice.bulkedit.BulkEditValidationHelper;
import com.hybris.backoffice.widgets.notificationarea.NotificationService;
import com.hybris.backoffice.widgets.notificationarea.event.NotificationEvent;
import com.hybris.cockpitng.components.Editor;
import com.hybris.cockpitng.components.validation.EditorValidation;
import com.hybris.cockpitng.components.validation.EditorValidationFactory;
import com.hybris.cockpitng.components.validation.ValidatableContainer;
import com.hybris.cockpitng.config.jaxb.wizard.ViewType;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectFacade;
import com.hybris.cockpitng.dataaccess.facades.permissions.PermissionFacade;
import com.hybris.cockpitng.dataaccess.facades.type.DataAttribute;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.dataaccess.facades.type.TypeFacade;
import com.hybris.cockpitng.dataaccess.facades.type.exceptions.TypeNotFoundException;
import com.hybris.cockpitng.editors.EditorRegistry;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.labels.LabelService;
import com.hybris.cockpitng.testing.util.BeanLookup;
import com.hybris.cockpitng.testing.util.BeanLookupFactory;
import com.hybris.cockpitng.testing.util.CockpitTestUtil;
import com.hybris.cockpitng.validation.LocalizedQualifier;
import com.hybris.cockpitng.widgets.configurableflow.validation.ConfigurableFlowValidationRenderer;


@RunWith(MockitoJUnitRunner.class)
public class BulkEditRendererTest
{
	@Mock
	private ConfigurableFlowValidationRenderer validationRenderer;
	@InjectMocks
	@Spy
	private BulkEditRenderer renderer;
	@Mock
	private ValidatableContainer validatableContainer;
	@Mock
	private NotificationService notificationService;
	@Mock
	private BulkEditValidationHelper bulkEditValidationHelper;
	@Mock
	private TypeFacade typeFacade;
	private WidgetInstanceManager wim;

	@Before
	public void setUp()
	{
		wim = CockpitTestUtil.mockWidgetInstanceManager();
		CockpitTestUtil.mockZkEnvironment();

		final EditorValidationFactory editorValidationFactory = mock(EditorValidationFactory.class);
		when(editorValidationFactory.createValidation(any())).thenReturn(mock(EditorValidation.class));
		when(editorValidationFactory.createValidation(any(), any())).thenReturn(mock(EditorValidation.class));
		final BeanLookup beanLookup = BeanLookupFactory.createBeanLookup()
				.registerBean("editorRegistry", mock(EditorRegistry.class))
				.registerBean("labelService", mock(LabelService.class))
				.registerBean("permissionFacade", mock(PermissionFacade.class))
				.registerBean("objectFacade", mock(ObjectFacade.class))
				.registerBean("typeFacade", typeFacade)
				.registerBean("editorValidationFactory", editorValidationFactory).getLookup();
		CockpitTestUtil.mockBeanLookup(beanLookup);
	}

	@Test
	public void testLocalizedAttributeLocales()
	{
		//given
		final DataAttribute name = mockDataAttribute(ProductModel.NAME, DataAttribute.AttributeType.SINGLE);
		when(name.isLocalized()).thenReturn(true);

		final Attribute attributeName = mockAttribute(name);
		attributeName.setSubAttributes(Sets.newHashSet(new Attribute(attributeName, Locale.ENGLISH.toLanguageTag()),
				new Attribute(attributeName, Locale.TRADITIONAL_CHINESE.toLanguageTag())));

		final DataType dataType = mockDataTypeWithAttributes(ProductModel._TYPECODE, Lists.newArrayList(name));

		final ItemModel product = mock(ProductModel.class);

		final AttributeChooserForm attributesForm = new AttributeChooserForm();
		attributesForm.setChosenAttributes(Sets.newHashSet(attributeName));

		final BulkEditForm bulkEditForm = new BulkEditForm();
		bulkEditForm.setItemsToEdit(Lists.newArrayList(product));
		bulkEditForm.setAttributesForm(attributesForm);


		final HashMap<String, String> params = new HashMap<>();
		params.put(BulkEditAttributesSelectorRenderer.PARAM_BULK_EDIT_FORM_MODEL_KEY, "bulkEditForm");
		wim.getModel().setValue("bulkEditForm", bulkEditForm);

		//when
		final Div parent = new Div();
		renderer.render(parent, validatableContainer, new ViewType(), params, dataType, wim);

		//then
		final Optional<Component> approvalLine = getAttributesLineForQualifier(parent, attributeName.getQualifier());
		assertThat(approvalLine.isPresent()).isTrue();
		final Optional<Editor> attributeEditor = getAttributeEditor(approvalLine.get());
		assertThat(attributeEditor.isPresent()).isTrue();
		assertThat(attributeEditor.get().getWritableLocales()).containsOnly(Locale.ENGLISH, Locale.TRADITIONAL_CHINESE);
		assertThat(attributeEditor.get().getReadableLocales()).containsOnly(Locale.ENGLISH, Locale.TRADITIONAL_CHINESE);
	}

	@Test
	public void testCollectionTypeHasOverrideExisting()
	{
		//given
		final DataAttribute categories = mockDataAttribute(ProductModel.SUPERCATEGORIES, DataAttribute.AttributeType.COLLECTION);

		final Attribute attributeCategories = mockAttribute(categories);

		final DataType dataType = mockDataTypeWithAttributes(ProductModel._TYPECODE, Lists.newArrayList(categories));

		final ItemModel product = mock(ProductModel.class);

		final AttributeChooserForm attributesForm = new AttributeChooserForm();
		attributesForm.setChosenAttributes(Sets.newHashSet(attributeCategories));

		final BulkEditForm bulkEditForm = new BulkEditForm();
		bulkEditForm.setItemsToEdit(Lists.newArrayList(product));
		bulkEditForm.setAttributesForm(attributesForm);


		final HashMap<String, String> params = new HashMap<>();
		params.put(BulkEditAttributesSelectorRenderer.PARAM_BULK_EDIT_FORM_MODEL_KEY, "bulkEditForm");
		wim.getModel().setValue("bulkEditForm", bulkEditForm);

		//when
		final Div parent = new Div();
		renderer.render(parent, validatableContainer, new ViewType(), params, dataType, wim);
		//then
		final Optional<Component> categoriesLine = getAttributesLineForQualifier(parent, attributeCategories.getQualifier());
		assertThat(categoriesLine.isPresent()).isTrue();
		assertThat(getAttributesOverrideExisting(categoriesLine.get()).isPresent()).isTrue();
	}

	@Test
	public void testMandatoryAttributeWithoutClearSwitch()
	{
		//given
		final DataAttribute approval = mockDataAttribute(ProductModel.APPROVALSTATUS, DataAttribute.AttributeType.SINGLE);
		when(approval.isMandatory()).thenReturn(true);

		final Attribute attributeApproval = mockAttribute(approval);

		final DataType dataType = mockDataTypeWithAttributes(ProductModel._TYPECODE, Lists.newArrayList(approval));

		final ItemModel product = mock(ProductModel.class);

		final AttributeChooserForm attributesForm = new AttributeChooserForm();
		attributesForm.setChosenAttributes(Sets.newHashSet(attributeApproval));

		final BulkEditForm bulkEditForm = new BulkEditForm();
		bulkEditForm.setItemsToEdit(Lists.newArrayList(product));
		bulkEditForm.setAttributesForm(attributesForm);


		final HashMap<String, String> params = new HashMap<>();
		params.put(BulkEditAttributesSelectorRenderer.PARAM_BULK_EDIT_FORM_MODEL_KEY, "bulkEditForm");
		wim.getModel().setValue("bulkEditForm", bulkEditForm);

		//when
		final Div parent = new Div();
		renderer.render(parent, validatableContainer, new ViewType(), params, dataType, wim);
		//then
		final Optional<Component> approvalLine = getAttributesLineForQualifier(parent, attributeApproval.getQualifier());
		assertThat(approvalLine.isPresent()).isTrue();
		assertThat(getAttributesClearValueSwitch(approvalLine.get()).isPresent()).isFalse();
	}

	@Test
	public void testVariantAttribute() throws TypeNotFoundException
	{
		//given
		final DataAttribute color = mockDataAttribute("color", DataAttribute.AttributeType.SINGLE);
		when(color.isVariantAttribute()).thenReturn(true);

		final Attribute attributeColor = mockAttribute(color);

		final DataType dataType = mockDataTypeWithAttributes(ProductModel._TYPECODE, Lists.newArrayList(color));

		final ItemModel product = mock(ProductModel.class);

		final AttributeChooserForm attributesForm = new AttributeChooserForm();
		attributesForm.setChosenAttributes(Sets.newHashSet(attributeColor));

		final BulkEditForm bulkEditForm = new BulkEditForm();
		bulkEditForm.setItemsToEdit(Lists.newArrayList(product));
		bulkEditForm.setAttributesForm(attributesForm);


		final HashMap<String, String> params = new HashMap<>();
		params.put(BulkEditAttributesSelectorRenderer.PARAM_BULK_EDIT_FORM_MODEL_KEY, "bulkEditForm");
		wim.getModel().setValue("bulkEditForm", bulkEditForm);

		when(typeFacade.getType(any(Map.class))).thenReturn("java.util.HashMap");
		when(typeFacade.load("java.util.HashMap")).thenThrow(TypeNotFoundException.class);

		//when
		final Div parent = new Div();
		renderer.render(parent, validatableContainer, new ViewType(), params, dataType, wim);

		//then
		final Optional<Component> colorLine = getAttributesLineForQualifier(parent, attributeColor.getQualifier());
		assertThat(colorLine.isPresent()).isTrue();
		final Optional<Editor> attributeEditor = getAttributeEditor(colorLine.get());
		assertThat(attributeEditor.isPresent()).isTrue();
		assertThat(attributeEditor.get().getProperty()).isEqualTo(BulkEditConstants.VARIANT_ATTRIBUTES_MAP_MODEL + ".color");
		assertThat(wim.getModel().getValue(BulkEditConstants.VARIANT_ATTRIBUTES_MAP_MODEL, Map.class)).isNotNull();
	}

	protected Optional<Component> getAttributesLineForQualifier(final Component parent, final String qualifier)
	{
		for (final Component attributesLine : getAttributesLines(parent))
		{
			final Optional<Label> label = getAttributesLabel(attributesLine);
			if (label.isPresent() && qualifier.equals(label.get().getValue()))
			{
				return Optional.of(attributesLine);
			}
		}
		return Optional.empty();
	}

	protected List<Component> getAttributesLines(final Component parent)
	{
		return Selectors.find(parent, "." + BulkEditRenderer.SCLASS_ATTR);
	}

	protected Optional<Editor> getAttributeEditor(final Component attributesLine)
	{
		return Selectors.find(attributesLine, "." + BulkEditRenderer.SCLASS_ATTR + "-value ").stream()
				.flatMap(cmp -> cmp.getChildren().stream()).filter(Editor.class::isInstance).findFirst().map(Editor.class::cast);
	}

	protected Optional<Checkbox> getAttributesOverrideExisting(final Component attributesLine)
	{
		return Selectors.find(attributesLine, "." + BulkEditRenderer.SCLASS_ATTR + "-value ").stream()
				.flatMap(cmp -> cmp.getChildren().stream()).filter(Checkbox.class::isInstance).findFirst().map(Checkbox.class::cast);
	}

	protected Optional<Checkbox> getAttributesClearValueSwitch(final Component attributesLine)
	{
		return Selectors.find(attributesLine, "." + BulkEditRenderer.SCLASS_ATTR + "-clear-switch ").stream()
				.flatMap(cmp -> cmp.getChildren().stream()).filter(Checkbox.class::isInstance).findFirst().map(Checkbox.class::cast);
	}

	protected Optional<Label> getAttributesLabel(final Component attributesLine)
	{
		return Selectors.find(attributesLine, "." + BulkEditRenderer.SCLASS_ATTR + "-name ").stream()
				.flatMap(cmp -> cmp.getChildren().stream()).filter(Label.class::isInstance).findFirst().map(Label.class::cast);
	}

	protected Attribute mockAttribute(final DataAttribute approval)
	{
		return new Attribute(approval.getQualifier(), approval.getQualifier(), approval.isMandatory());
	}

	@Test
	public void renderLabelOnMissingForm()
	{
		final HashMap<String, String> params = new HashMap<>();
		params.put(BulkEditAttributesSelectorRenderer.PARAM_BULK_EDIT_FORM_MODEL_KEY, "bulkEditForm");
		wim.getModel().setValue("bulkEditForm", null);
		final DataType dataType = mock(DataType.class);

		final Div parent = new Div();
		renderer.render(parent, validatableContainer, new ViewType(), params, dataType, wim);

		verify(notificationService).notifyUser(BulkEditConstants.NOTIFICATION_SOURCE_BULK_EDIT,
				BulkEditConstants.NOTIFICATION_EVENT_TYPE_MISSING_FORM, NotificationEvent.Level.FAILURE);
	}

	@Test
	public void renderLabelOnMissingSelectedAttributes()
	{
		final HashMap<String, String> params = new HashMap<>();
		params.put(BulkEditAttributesSelectorRenderer.PARAM_BULK_EDIT_FORM_MODEL_KEY, "bulkEditForm");
		final BulkEditForm bulkEditForm = new BulkEditForm();
		wim.getModel().setValue("bulkEditForm", bulkEditForm);
		final DataType dataType = mock(DataType.class);

		final Div parent = new Div();
		renderer.render(parent, validatableContainer, new ViewType(), params, dataType, wim);

		verify(notificationService).notifyUser(BulkEditConstants.NOTIFICATION_SOURCE_BULK_EDIT,
				BulkEditConstants.NOTIFICATION_EVENT_TYPE_MISSING_ATTRIBUTES, NotificationEvent.Level.FAILURE);
	}


	@Test
	public void renderValidateAllAttributesSwitch()
	{
		// given
		final DataAttribute approval = mockDataAttribute(ProductModel.APPROVALSTATUS, DataAttribute.AttributeType.SINGLE);
		final DataType dataType = mockDataTypeWithAttributes(ProductModel._TYPECODE, Lists.newArrayList(approval));

		final Attribute attributeApproval = mockAttribute(approval);
		final ItemModel product = mock(ProductModel.class);

		final AttributeChooserForm attributesForm = new AttributeChooserForm();
		attributesForm.setChosenAttributes(Sets.newHashSet(attributeApproval));

		final BulkEditForm bulkEditForm = new BulkEditForm();
		bulkEditForm.setItemsToEdit(Lists.newArrayList(product));
		bulkEditForm.setAttributesForm(attributesForm);

		final HashMap<String, String> params = new HashMap<>();
		params.put(BulkEditAttributesSelectorRenderer.PARAM_BULK_EDIT_FORM_MODEL_KEY, "bulkEditForm");
		wim.getModel().setValue("bulkEditForm", bulkEditForm);

		// when
		final Div parent = new Div();
		renderer.render(parent, validatableContainer, new ViewType(), params, dataType, wim);

		// then
		final List<Component> components = Selectors.find(parent, "." + BulkEditRenderer.SCLASS_VALIDATE_ALL_ATTRIBUTES +
				"-switch");

		assertThat(components).hasSize(1).as("By default the switch should be rendered");
		assertThat(((Checkbox) components.get(0)).isChecked()).isFalse().as("By default the switch should be turned off");
		assertThat(bulkEditForm.isValidateAllAttributes()).isFalse().as("By default should not validate all attributes");
	}

	@Test
	public void testValidateAllAttributesParameter()
	{
		// given
		final DataAttribute approval = mockDataAttribute(ProductModel.APPROVALSTATUS, DataAttribute.AttributeType.SINGLE);
		final DataType dataType = mockDataTypeWithAttributes(ProductModel._TYPECODE, Lists.newArrayList(approval));

		final Attribute attributeApproval = mockAttribute(approval);
		final ItemModel product = mock(ProductModel.class);

		final AttributeChooserForm attributesForm = new AttributeChooserForm();
		attributesForm.setChosenAttributes(Sets.newHashSet(attributeApproval));

		final BulkEditForm bulkEditForm = new BulkEditForm();
		bulkEditForm.setItemsToEdit(Lists.newArrayList(product));
		bulkEditForm.setAttributesForm(attributesForm);
		bulkEditForm.setValidateAllAttributes(true);

		final HashMap<String, String> params = new HashMap<>();
		params.put(BulkEditAttributesSelectorRenderer.PARAM_BULK_EDIT_FORM_MODEL_KEY, "bulkEditForm");
		wim.getModel().setValue("bulkEditForm", bulkEditForm);

		// when
		final Div parent = new Div();
		renderer.render(parent, validatableContainer, new ViewType(), params, dataType, wim);

		// then
		final List<Component> components = Selectors.find(parent, "." + BulkEditRenderer.SCLASS_VALIDATE_ALL_ATTRIBUTES +
				"-switch");

		assertThat(((Checkbox) components.get(0)).isChecked()).isTrue();
		assertThat(bulkEditForm.isValidateAllAttributes()).isTrue();
	}

	private DataType mockDataTypeWithAttributes(final String typeCode, final Collection<DataAttribute> attributes)
	{
		final DataType dataType = mock(DataType.class);
		when(dataType.getCode()).thenReturn(typeCode);
		when(dataType.getAttributes()).thenReturn(attributes);
		attributes.forEach(attr -> when(dataType.getAttribute(attr.getQualifier())).thenReturn(attr));
		return dataType;
	}

	private DataAttribute mockDataAttribute(final String attribute, final DataAttribute.AttributeType attributeType)
	{
		final DataAttribute da = mock(DataAttribute.class);
		when(da.getQualifier()).thenReturn(attribute);
		when(da.getDefinedType()).thenReturn(DataType.STRING);
		when(da.getValueType()).thenReturn(DataType.STRING);
		when(da.getAttributeType()).thenReturn(attributeType);
		return da;
	}

	@Test
	public void shouldGetValidationPropertiesWithLocalesAppendingPrefixToTheQualifierName()
	{
		// given
		final Attribute firstAttribute = mock(Attribute.class, "firstAttribute");
		final Attribute secondAttribute = mock(Attribute.class, "secondAttribute");

		final AttributeChooserForm attributesForm = mock(AttributeChooserForm.class);
		given(attributesForm.getChosenAttributes()).willReturn(Sets.newHashSet(firstAttribute, secondAttribute));

		final BulkEditForm bulkEditForm = new BulkEditForm();
		bulkEditForm.setAttributesForm(attributesForm);

		final Map<String, String> params = new HashMap<>();
		params.put(BulkEditAttributesSelectorRenderer.PARAM_BULK_EDIT_FORM_MODEL_KEY, "bulkEditForm");
		wim.getModel().setValue("bulkEditForm", bulkEditForm);

		final LocalizedQualifier firstLocalizedQualifier = new LocalizedQualifier("firstLocalizedQualifier");
		final LocalizedQualifier secondLocalizedQualifier = new LocalizedQualifier("secondLocalizedQualifier",
				Collections.singleton(Locale.ENGLISH));
		given(bulkEditValidationHelper.getValidatablePropertiesWithLocales(bulkEditForm))
				.willReturn(Arrays.asList(firstLocalizedQualifier, secondLocalizedQualifier));

		// when
		final Collection<LocalizedQualifier> results = renderer.getValidationPropertiesWithLocales(wim, params);

		// then: localized qualifiers are obtained with prefixed names and untouched locales
		assertThat(results).contains(new LocalizedQualifier("bulkEditForm.templateObject.firstLocalizedQualifier"))
				.contains(new LocalizedQualifier("bulkEditForm.templateObject.secondLocalizedQualifier",
						Collections.singleton(Locale.ENGLISH)));
	}
}
