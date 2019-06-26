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
package com.hybris.backoffice.excel.export.wizard.renderer.attributechooser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zul.AbstractTreeModel;
import org.zkoss.zul.Button;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.Div;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Tree;
import org.zkoss.zul.TreeNode;
import org.zkoss.zul.TreeitemRenderer;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hybris.backoffice.excel.data.SelectedAttribute;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.testing.util.CockpitTestUtil;


@RunWith(MockitoJUnitRunner.class)
public class DefaultAttributeChooserRendererTest
{
	@Mock
	private TreeitemRenderer treeitemRenderer;
	@InjectMocks
	@Spy
	private DefaultAttributeChooserRenderer attributePicker;

	private SelectedAttribute code;
	private SelectedAttribute ean;
	private SelectedAttribute name;
	private SelectedAttribute nameEn;
	private SelectedAttribute nameDe;
	private Set<String> locales;
	private List<SelectedAttribute> availableAttributes = new ArrayList<>();
	private WidgetInstanceManager wim;
	@Spy
	private final DefaultNodeOperationsHandler nodeOperationsHandler = new DefaultNodeOperationsHandler();
	@Mock
	private CommonI18NService commonI18NService;

	@Before
	public void setUp()
	{
		CockpitTestUtil.mockZkEnvironment();
		wim = CockpitTestUtil.mockWidgetInstanceManager();


		this.code = new SelectedAttribute(mockAttributeDescriptor("code", "Code", false, true));

		this.ean = new SelectedAttribute(mockAttributeDescriptor("ean", "European Article Number", false, false));

		this.name = new SelectedAttribute(mockAttributeDescriptor("name", "Name", true, false));
		this.nameEn = new SelectedAttribute("en", mockAttributeDescriptor("name", "Name", true, false));
		this.nameDe = new SelectedAttribute("de", mockAttributeDescriptor("name", "Name", true, false));

		final LanguageModel en = mock(LanguageModel.class);
		when(en.getIsocode()).thenReturn("en");
		when(en.getActive()).thenReturn(true);
		final LanguageModel de = mock(LanguageModel.class);
		when(de.getIsocode()).thenReturn("de");
		when(de.getActive()).thenReturn(true);

		final LanguageModel skipped = mock(LanguageModel.class);
		when(skipped.getIsocode()).thenReturn("de");
		when(skipped.getActive()).thenReturn(false);

		when(commonI18NService.getAllLanguages()).thenReturn(Lists.newArrayList(en, de, skipped));
		when(commonI18NService.getCurrentLanguage()).thenReturn(en);
		nodeOperationsHandler.setCommonI18NService(commonI18NService);
	}

	protected AttributeDescriptorModel mockAttributeDescriptor(final String qualifier, final String name, final boolean localized,
			final boolean mandatory)
	{
		final ComposedTypeModel composedTypeModel = mock(ComposedTypeModel.class);
		given(composedTypeModel.getUniqueKeyAttributes()).willReturn(Collections.emptyList());
		final AttributeDescriptorModel descriptor = spy(new AttributeDescriptorModel());
		doReturn(false).when(descriptor).getUnique();
		doReturn(composedTypeModel).when(descriptor).getEnclosingType();
		doReturn(qualifier).when(descriptor).getQualifier();
		doReturn(name).when(descriptor).getName();
		doReturn(localized).when(descriptor).getLocalized();
		doReturn(!mandatory).when(descriptor).getOptional();
		return descriptor;
	}

	@Test
	public void shouldRenderInitialState()
	{
		// given
		availableAttributes = Lists.newArrayList(name, ean, code);

		// when
		final Div parent = renderAttributePicker();

		// then
		final AbstractTreeModel<TreeNode<SelectedAttribute>> availableValuesModel = (AbstractTreeModel) findAvailableValuesTree(
				parent).getModel();

		final AbstractTreeModel<TreeNode<SelectedAttribute>> selectedValuesModel = (AbstractTreeModel) findSelectedValuesTree(
				parent).getModel();

		final List<TreeNode<SelectedAttribute>> availableValues = availableValuesModel.getRoot().getChildren();
		assertThat(availableValues).hasSize(2);
		assertThat(availableValues.get(0).getData()).isEqualTo(ean);
		assertThat(availableValues.get(1).getData()).isEqualTo(name);

		final List<TreeNode<SelectedAttribute>> selectedValues = selectedValuesModel.getRoot().getChildren();
		assertThat(selectedValues).hasSize(1);
		assertThat(selectedValues.get(0).getData()).isEqualTo(code);
	}

	private Div renderAttributePicker()
	{
		final AttributeChooserForm form = new AttributeChooserForm(
				availableAttributes.stream().map(SelectedAttribute::getAttributeDescriptor).collect(Collectors.toSet()));
		final Div parent = new Div();
		attributePicker.render(parent, null, form, null, wim);
		return parent;
	}

	protected Tree findAvailableValuesTree(final Div parent)
	{
		return (Tree) parent.query(".y-attributepicker-available-values-container .y-attributepicker-values-tree");
	}

	protected Tree findSelectedValuesTree(final Div parent)
	{
		return (Tree) parent.query(".y-attributepicker-selected-values-container .y-attributepicker-values-tree");
	}

	protected Textbox findAvailableValuesFilter(final Div parent)
	{
		return (Textbox) parent.query(".y-attributepicker-available-values-container .y-attributepicker-values-filter");
	}

	protected Textbox findSelectedValuesFilter(final Div parent)
	{
		return (Textbox) parent.query(".y-attributepicker-selected-values-container .y-attributepicker-values-filter");
	}

	protected Button findAddButton(final Div parent)
	{
		return (Button) parent.query(".y-attributepicker-add-btn");
	}

	protected Button findRemoveButton(final Div parent)
	{
		return (Button) parent.query(".y-attributepicker-remove-btn");
	}


	@Test
	public void shouldAddCheckedAttributesWhenAddIsClicked()
	{
		// given
		availableAttributes = Lists.newArrayList(name);
		final Div parent = renderAttributePicker();
		setCheckedAvailableValues(name, parent);

		// when
		click(findAddButton(parent));

		// then
		final AbstractTreeModel<TreeNode<SelectedAttribute>> availableValuesModel = (AbstractTreeModel) findAvailableValuesTree(
				parent).getModel();

		final AbstractTreeModel<TreeNode<SelectedAttribute>> selectedValuesModel = (AbstractTreeModel) findSelectedValuesTree(
				parent).getModel();
		assertThat(availableValuesModel.getRoot().getChildren()).isEmpty();
		assertThat(selectedValuesModel.getRoot().getChildren()).hasSize(1);
		assertThat(selectedValuesModel.getRoot().getChildren().get(0).getData()).isEqualTo(name);
	}

	@Test
	public void shouldRemoveCheckedAttributesWhenRemoveIsClicked()
	{

		// given
		availableAttributes = Lists.newArrayList(code, name, ean);
		wim.getModel().setValue(DefaultAttributeChooserRenderer.MODEL_SELECTED_QUALIFIERS,
				Sets.newHashSet(attributePicker.toModelQualifier(ean)));
		final Div parent = renderAttributePicker();

		final AbstractTreeModel<TreeNode<SelectedAttribute>> availableValuesModel = (AbstractTreeModel) findAvailableValuesTree(
				parent).getModel();

		final AbstractTreeModel<TreeNode<SelectedAttribute>> selectedValuesModel = (AbstractTreeModel) findSelectedValuesTree(
				parent).getModel();
		assertThat(availableValuesModel.getRoot().getChildren()).hasSize(1);
		assertThat(availableValuesModel.getRoot().getChildren().get(0).getData()).isEqualTo(name);
		assertThat(selectedValuesModel.getRoot().getChildren()).hasSize(2);
		assertThat(selectedValuesModel.getRoot().getChildren().stream().map(TreeNode::getData).collect(Collectors.toList()))
				.containsOnly(code, ean);

		setCheckedSelectedValues(ean, parent);

		// when
		click(findRemoveButton(parent));

		// then
		assertThat(selectedValuesModel.getRoot().getChildren()).hasSize(1);
		assertThat(selectedValuesModel.getRoot().getChildren().get(0).getData()).isEqualTo(code);
		assertThat(availableValuesModel.getRoot().getChildren()).hasSize(2);
		assertThat(availableValuesModel.getRoot().getChildren().stream().map(TreeNode::getData).collect(Collectors.toList()))
				.containsOnly(name, ean);
	}

	@Test
	public void shouldMoveLocalizedParentIfAllChildrenAreMoved()
	{
		// given
		availableAttributes = Lists.newArrayList(code, name, ean);
		final Div parent = renderAttributePicker();
		final Tree availableValuesTree = findAvailableValuesTree(parent);
		final AbstractTreeModel<TreeNode<SelectedAttribute>> availableValuesModel = (AbstractTreeModel) availableValuesTree
				.getModel();

		final AbstractTreeModel<TreeNode<SelectedAttribute>> selectedValuesModel = (AbstractTreeModel) findSelectedValuesTree(
				parent).getModel();
		assertThat(availableValuesModel.getRoot().getChildren()).hasSize(2);
		// when
		final Optional<Component> groupingNode = availableValuesTree.getRoot().getChildren().stream()
				.filter(node -> CollectionUtils.isNotEmpty(node.getChildren())).findFirst();
		assertThat(groupingNode.isPresent()).isTrue();
		click(groupingNode.get());
		setCheckedAvailableValues(nameEn, parent);
		setCheckedAvailableValues(nameDe, parent);
		click(findAddButton(parent));

		// then
		assertThat(selectedValuesModel.getRoot().getChildren()).hasSize(2);
		assertThat(selectedValuesModel.getRoot().getChildren().stream().map(TreeNode::getData).collect(Collectors.toList()))
				.containsOnly(code, name);
		final Optional<TreeNode<SelectedAttribute>> nameGrouping = selectedValuesModel.getRoot().getChildren().stream()
				.filter(node -> node.getData().equals(name)).findFirst();
		assertThat(nameGrouping.isPresent()).isTrue();
		assertThat(nameGrouping.get().getChildren()).hasSize(2);
		assertThat(nameGrouping.get().getChildren().stream().map(TreeNode::getData).collect(Collectors.toList()))
				.containsOnly(nameDe, nameEn);
		assertThat(availableValuesModel.getRoot().getChildren()).hasSize(1);
	}

	@Test
	public void shouldFilterAvailableAttributes()
	{
		// given
		availableAttributes = Lists.newArrayList(code, ean, name);
		final Div parent = renderAttributePicker();

		// when
		findAvailableValuesFilter(parent).setValue("an");
		CockpitTestUtil.simulateEvent(findAvailableValuesFilter(parent), new InputEvent(Events.ON_CHANGING, null, "an", null));

		// then
		final AbstractTreeModel<TreeNode<SelectedAttribute>> availableValuesModel = (AbstractTreeModel) findAvailableValuesTree(
				parent).getModel();
		assertThat(availableValuesModel.getChildCount(availableValuesModel.getRoot())).isEqualTo(1);
		assertThat(availableValuesModel.getRoot().getChildren().get(0).getData()).isEqualTo(ean);
	}

	@Test
	public void shouldFilterSelectedAttributes()
	{
		// given
		availableAttributes = Lists.newArrayList(code, ean, name);
		final Div parent = renderAttributePicker();

		// when
		final Textbox selectedValuesFilter = findSelectedValuesFilter(parent);
		selectedValuesFilter.setValue("co");
		CockpitTestUtil.simulateEvent(selectedValuesFilter, new InputEvent(Events.ON_CHANGING, null, "co", null));

		// then
		final AbstractTreeModel<TreeNode<SelectedAttribute>> selectedValuesModel = (AbstractTreeModel) findSelectedValuesTree(
				parent).getModel();
		assertThat(selectedValuesModel.getChildCount(selectedValuesModel.getRoot())).isEqualTo(1);
		assertThat(selectedValuesModel.getRoot().getChildren().get(0).getData()).isEqualTo(code);
	}

	@Test
	public void shouldAddLocalizedAttributeParent()
	{
		// given
		availableAttributes = Lists.newArrayList(name);
		final Div parent = renderAttributePicker();
		setCheckedAvailableValues(name, parent);

		// when
		click(findAddButton(parent));

		// then
		final AbstractTreeModel<TreeNode<SelectedAttribute>> availableValuesModel = (AbstractTreeModel) findAvailableValuesTree(
				parent).getModel();

		final AbstractTreeModel<TreeNode<SelectedAttribute>> selectedValuesModel = (AbstractTreeModel) findSelectedValuesTree(
				parent).getModel();
		final List<TreeNode<SelectedAttribute>> selectedValues = selectedValuesModel.getRoot().getChildren();
		assertThat(selectedValues).hasSize(1);
		final TreeNode<SelectedAttribute> selectedValue = selectedValues.get(0);
		assertThat(selectedValue.getData()).isEqualTo(name);
		assertThat(selectedValue.getChildren()).hasSize(2);

		final SelectedAttribute attribute1 = selectedValue.getChildren().get(0).getData();
		assertThat(attribute1.getQualifier()).isEqualTo(name.getQualifier());
		assertThat(attribute1.getName()).isEqualTo(name.getName());
		assertThat(attribute1.isLocalized()).isEqualTo(name.isLocalized());
		assertThat(attribute1.getIsoCode()).isEqualTo("de");

		final SelectedAttribute attribute2 = selectedValue.getChildren().get(1).getData();
		assertThat(attribute2.getQualifier()).isEqualTo(name.getQualifier());
		assertThat(attribute2.getName()).isEqualTo(name.getName());
		assertThat(attribute2.isLocalized()).isEqualTo(name.isLocalized());
		assertThat(attribute2.getIsoCode()).isEqualTo("en");
	}

	@Test
	public void shouldAddLocalizedAttributeChild()
	{
		// given
		availableAttributes = Lists.newArrayList(name);
		final Div parent = renderAttributePicker();
		setCheckedAvailableValues(nameEn, parent);

		// when
		click(findAddButton(parent));

		// then
		final AbstractTreeModel<TreeNode<SelectedAttribute>> availableValuesModel = (AbstractTreeModel) findAvailableValuesTree(
				parent).getModel();

		final AbstractTreeModel<TreeNode<SelectedAttribute>> selectedValuesModel = (AbstractTreeModel) findSelectedValuesTree(
				parent).getModel();

		final List<TreeNode<SelectedAttribute>> availableValues = availableValuesModel.getRoot().getChildren();
		assertThat(availableValues).hasSize(1);
		assertThat(availableValues.get(0).getData()).isEqualTo(name);
		assertThat(availableValues.get(0).getChildren()).hasSize(1);
		assertThat(availableValues.get(0).getChildren().get(0).getData()).isEqualTo(nameDe);

		final List<TreeNode<SelectedAttribute>> selectedValues = selectedValuesModel.getRoot().getChildren();
		assertThat(selectedValues).hasSize(1);
		assertThat(selectedValues.get(0).getData()).isEqualTo(name);
		assertThat(selectedValues.get(0).getChildren()).hasSize(1);
		assertThat(selectedValues.get(0).getChildren().get(0).getData()).isEqualTo(nameEn);

	}

	private static void click(final Component button)
	{
		CockpitTestUtil.simulateEvent(button, Events.ON_CLICK, null);
	}

	private void setCheckedSelectedValues(final SelectedAttribute attribute, final Div parent)
	{
		final Tree selectedValuesTree = findSelectedValuesTree(parent);
		final AbstractTreeModel model = (AbstractTreeModel) selectedValuesTree.getModel();
		final DefaultTreeNode<SelectedAttribute> root = (DefaultTreeNode<SelectedAttribute>) model.getRoot();

		final List<TreeNode<SelectedAttribute>> flat = getFlatListOfNodes(root);

		final Optional<TreeNode<SelectedAttribute>> node = findNodeWithAttribute(attribute, flat);

		model.addToSelection(node.get());

	}

	private void setCheckedAvailableValues(final SelectedAttribute attribute, final Div parent)
	{
		final Tree availableValuesTree = findAvailableValuesTree(parent);
		final AbstractTreeModel model = (AbstractTreeModel) availableValuesTree.getModel();
		final DefaultTreeNode<SelectedAttribute> root = (DefaultTreeNode<SelectedAttribute>) model.getRoot();

		final List<TreeNode<SelectedAttribute>> flat = getFlatListOfNodes(root);

		final Optional<TreeNode<SelectedAttribute>> node = findNodeWithAttribute(attribute, flat);

		model.addToSelection(node.get());
	}

	private Optional<TreeNode<SelectedAttribute>> findNodeWithAttribute(final SelectedAttribute attribute,
			final List<TreeNode<SelectedAttribute>> flat)
	{
		return flat.stream().filter(node -> node.getData().equals(attribute)).findFirst();
	}

	private List<TreeNode<SelectedAttribute>> getFlatListOfNodes(final DefaultTreeNode<SelectedAttribute> root)
	{
		return root.getChildren().stream().flatMap(node -> {
			final boolean hasChildren = node.getChildCount() > 0;
			if (!hasChildren)
			{
				return Stream.of(node);
			}
			else
			{
				final List<TreeNode<SelectedAttribute>> f = Lists.newArrayList(node);
				f.addAll(node.getChildren());
				return f.stream();
			}
		}).collect(Collectors.toList());
	}

}
