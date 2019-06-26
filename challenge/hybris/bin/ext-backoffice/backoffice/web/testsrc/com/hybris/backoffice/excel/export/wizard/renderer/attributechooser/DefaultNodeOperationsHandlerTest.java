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
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zul.AbstractTreeModel;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.TreeNode;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hybris.backoffice.excel.data.SelectedAttribute;


@RunWith(MockitoJUnitRunner.class)
public class DefaultNodeOperationsHandlerTest
{
	@Mock
	private CommonI18NService commonI18NService;
	@InjectMocks
	private DefaultNodeOperationsHandler handler;
	private LanguageModel en;
	private LanguageModel de;
	private LanguageModel fr;
	private AttributeDescriptorModel code;
	private AttributeDescriptorModel name;
	private AttributeDescriptorModel ean;

	@Before
	public void setUp()
	{

		code = mockAttributeDescriptor("code", "Code", false, true);
		name = mockAttributeDescriptor("name", "Name", true, false);
		ean = mockAttributeDescriptor("ean", "Ean", false, false);

		en = mock(LanguageModel.class);
		when(en.getIsocode()).thenReturn("en");
		when(en.getActive()).thenReturn(true);
		de = mock(LanguageModel.class);
		when(de.getIsocode()).thenReturn("de");
		when(de.getActive()).thenReturn(true);
		fr = mock(LanguageModel.class);
		when(fr.getIsocode()).thenReturn("fr");
		when(fr.getActive()).thenReturn(true);

		final LanguageModel skipped = mock(LanguageModel.class);
		when(skipped.getIsocode()).thenReturn("de");
		when(skipped.getActive()).thenReturn(false);

		when(commonI18NService.getAllLanguages()).thenReturn(Lists.newArrayList(en, de, skipped));
		when(commonI18NService.getCurrentLanguage()).thenReturn(fr);
	}

	@Test
	public void testDoNotMoveMandatoryNode()
	{
		final AbstractTreeModel<TreeNode<SelectedAttribute>> srcTree = createTreeModel(code, ean);
		final AbstractTreeModel<TreeNode<SelectedAttribute>> targetTree = createTreeModel(ean);

		srcTree.addToSelection(srcTree.getRoot().getChildren().stream()
				.filter(node -> node.getData().getQualifier().equals(code.getQualifier())).findFirst().get());

		handler.moveNodesBetweenTrees(srcTree, targetTree);

		assertThat(targetTree.getRoot().getChildCount()).isEqualTo(1);
		assertThat(srcTree.getRoot().getChildCount()).isEqualTo(2);
		assertThat(
				targetTree.getRoot().getChildren().stream().map(node -> node.getData().getQualifier()).collect(Collectors.toSet()))
						.containsOnly(ean.getQualifier());
	}

	@Test
	public void testMoveNonLocalizedNode()
	{
		final AbstractTreeModel<TreeNode<SelectedAttribute>> srcTree = createTreeModel(name, ean);
		final AbstractTreeModel<TreeNode<SelectedAttribute>> targetTree = createTreeModel(code);

		srcTree.addToSelection(srcTree.getRoot().getChildren().stream()
				.filter(node -> node.getData().getQualifier().equals(ean.getQualifier())).findFirst().get());

		handler.moveNodesBetweenTrees(srcTree, targetTree);

		assertThat(targetTree.getRoot().getChildCount()).isEqualTo(2);
		assertThat(srcTree.getRoot().getChildCount()).isEqualTo(1);
		assertThat(
				targetTree.getRoot().getChildren().stream().map(node -> node.getData().getQualifier()).collect(Collectors.toSet()))
						.containsOnly(ean.getQualifier(), code.getQualifier());
	}

	@Test
	public void testMoveLocalizedGroupingNode()
	{
		final AbstractTreeModel<TreeNode<SelectedAttribute>> srcTree = createTreeModel(name, ean);
		final AbstractTreeModel<TreeNode<SelectedAttribute>> targetTree = createTreeModel(code);

		srcTree.addToSelection(srcTree.getRoot().getChildren().stream()
				.filter(node -> node.getData().getQualifier().equals(name.getQualifier())).findFirst().get());

		handler.moveNodesBetweenTrees(srcTree, targetTree);

		assertThat(targetTree.getRoot().getChildCount()).isEqualTo(2);
		assertThat(srcTree.getRoot().getChildCount()).isEqualTo(1);
		assertThat(
				targetTree.getRoot().getChildren().stream().map(node -> node.getData().getQualifier()).collect(Collectors.toSet()))
						.containsOnly(name.getQualifier(), code.getQualifier());
		final Optional<TreeNode<SelectedAttribute>> nameNode = targetTree.getRoot().getChildren().stream()
				.filter(node -> node.getData().getQualifier().equals(name.getQualifier())).findFirst();
		assertThat(nameNode.isPresent()).isTrue();
		assertThat(nameNode.get().getChildren().stream().map(node -> node.getData().getIsoCode()).collect(Collectors.toSet()))
				.containsOnly(en.getIsocode(), de.getIsocode());
	}

	@Test
	public void testMoveLocalizedGroupingNodeWhenInTargetExists()
	{
		//given
		final AbstractTreeModel<TreeNode<SelectedAttribute>> srcTree = createTreeModel(name, ean);
		final AbstractTreeModel<TreeNode<SelectedAttribute>> targetTree = createTreeModel(code);

		final Optional<TreeNode<SelectedAttribute>> nameNodePreConditions = srcTree.getRoot().getChildren().stream()
				.filter(node -> node.getData().getQualifier().equals(name.getQualifier())).findFirst();
		srcTree.addToSelection(nameNodePreConditions.get().getChildren().stream()
				.filter(node -> node.getData().getIsoCode().equals(de.getIsocode())).findFirst().get());

		handler.moveNodesBetweenTrees(srcTree, targetTree);
		//when
		srcTree.addToSelection(srcTree.getRoot().getChildren().stream()
				.filter(node -> node.getData().getQualifier().equals(name.getQualifier())).findFirst().get());

		handler.moveNodesBetweenTrees(srcTree, targetTree);
		//then
		assertThat(targetTree.getRoot().getChildCount()).isEqualTo(2);
		assertThat(srcTree.getRoot().getChildCount()).isEqualTo(1);
		assertThat(
				targetTree.getRoot().getChildren().stream().map(node -> node.getData().getQualifier()).collect(Collectors.toSet()))
						.containsOnly(name.getQualifier(), code.getQualifier());
		final Optional<TreeNode<SelectedAttribute>> nameNode = targetTree.getRoot().getChildren().stream()
				.filter(node -> node.getData().getQualifier().equals(name.getQualifier())).findFirst();
		assertThat(nameNode.isPresent()).isTrue();
		assertThat(nameNode.get().getChildren().stream().map(node -> node.getData().getIsoCode()).collect(Collectors.toSet()))
				.containsOnly(en.getIsocode(), de.getIsocode());
	}

	@Test
	public void testMoveSubNode()
	{
		final AbstractTreeModel<TreeNode<SelectedAttribute>> srcTree = createTreeModel(name, ean);
		final AbstractTreeModel<TreeNode<SelectedAttribute>> targetTree = createTreeModel(code);

		final Optional<TreeNode<SelectedAttribute>> nameNode = srcTree.getRoot().getChildren().stream()
				.filter(node -> node.getData().getQualifier().equals(name.getQualifier())).findFirst();
		srcTree.addToSelection(nameNode.get().getChildren().stream()
				.filter(node -> node.getData().getIsoCode().equals(de.getIsocode())).findFirst().get());

		handler.moveNodesBetweenTrees(srcTree, targetTree);

		assertThat(targetTree.getRoot().getChildCount()).isEqualTo(2);
		assertThat(srcTree.getRoot().getChildCount()).isEqualTo(2);
		assertThat(
				targetTree.getRoot().getChildren().stream().map(node -> node.getData().getQualifier()).collect(Collectors.toSet()))
						.containsOnly(name.getQualifier(), code.getQualifier());
		final Optional<TreeNode<SelectedAttribute>> nameNodeSrc = srcTree.getRoot().getChildren().stream()
				.filter(node -> node.getData().getQualifier().equals(name.getQualifier())).findFirst();
		assertThat(nameNodeSrc.get().getChildCount()).isEqualTo(1);

		final Optional<TreeNode<SelectedAttribute>> nameNodeTarget = targetTree.getRoot().getChildren().stream()
				.filter(node -> node.getData().getQualifier().equals(name.getQualifier())).findFirst();
		assertThat(nameNodeTarget.isPresent()).isTrue();
		assertThat(nameNodeTarget.get().getChildren().stream().map(node -> node.getData().getIsoCode()).collect(Collectors.toSet()))
				.containsOnly(de.getIsocode());
	}

	@Test
	public void testMoveLastSubNode()
	{
		final AbstractTreeModel<TreeNode<SelectedAttribute>> srcTree = createTreeModel(name, ean);
		final AbstractTreeModel<TreeNode<SelectedAttribute>> targetTree = createTreeModel(code);

		final Optional<TreeNode<SelectedAttribute>> nameNode = srcTree.getRoot().getChildren().stream()
				.filter(node -> node.getData().getQualifier().equals(name.getQualifier())).findFirst();
		srcTree.addToSelection(nameNode.get().getChildren().stream()
				.filter(node -> node.getData().getIsoCode().equals(de.getIsocode())).findFirst().get());
		//when
		handler.moveNodesBetweenTrees(srcTree, targetTree);

		srcTree.addToSelection(nameNode.get().getChildren().stream()
				.filter(node -> node.getData().getIsoCode().equals(en.getIsocode())).findFirst().get());
		handler.moveNodesBetweenTrees(srcTree, targetTree);
		//then
		assertThat(targetTree.getRoot().getChildCount()).isEqualTo(2);
		assertThat(srcTree.getRoot().getChildCount()).isEqualTo(1);
		assertThat(srcTree.getRoot().getChildren().stream().map(node -> node.getData().getQualifier()).collect(Collectors.toSet()))
				.containsOnly(ean.getQualifier());

		final Optional<TreeNode<SelectedAttribute>> nameNodeTarget = targetTree.getRoot().getChildren().stream()
				.filter(node -> node.getData().getQualifier().equals(name.getQualifier())).findFirst();
		assertThat(nameNodeTarget.isPresent()).isTrue();
		assertThat(nameNodeTarget.get().getChildren().stream().map(node -> node.getData().getIsoCode()).collect(Collectors.toSet()))
				.containsOnly(de.getIsocode(), en.getIsocode());
	}

	@Test
	public void testSelectGropingNodeWhenAllSubNodesSelected()
	{
		final AbstractTreeModel<TreeNode<SelectedAttribute>> srcTree = createTreeModel(name, ean, code);

		final TreeNode<SelectedAttribute> nameEn = findNode(srcTree.getRoot(), name.getQualifier(), en.getIsocode());
		final TreeNode<SelectedAttribute> nameDe = findNode(srcTree.getRoot(), name.getQualifier(), de.getIsocode());
		final TreeNode<SelectedAttribute> nameGrouping = findNode(srcTree.getRoot(), this.name.getQualifier(), null);


		final SelectEvent selectEvent = new SelectEvent(Events.ON_SELECT, null, null, null, null, Sets.newHashSet(nameEn, nameDe),
				Sets.newHashSet(nameDe), Sets.newHashSet(), null, null, 0);

		handler.updateGroupingNodesSelection(srcTree, selectEvent);

		assertThat(srcTree.getSelection()).contains(nameGrouping);
	}

	@Test
	public void testSelectSubNodesWhenGroupingNodeSelected()
	{
		final AbstractTreeModel<TreeNode<SelectedAttribute>> srcTree = createTreeModel(name, ean, code);

		final TreeNode<SelectedAttribute> nameEn = findNode(srcTree.getRoot(), name.getQualifier(), en.getIsocode());
		final TreeNode<SelectedAttribute> nameDe = findNode(srcTree.getRoot(), name.getQualifier(), de.getIsocode());
		final TreeNode<SelectedAttribute> nameGrouping = findNode(srcTree.getRoot(), this.name.getQualifier(), null);

		srcTree.addToSelection(nameDe);
		srcTree.addToSelection(nameEn);
		srcTree.addToSelection(nameGrouping);

		final SelectEvent selectEvent = new SelectEvent(Events.ON_SELECT, null, null, null, null, Sets.newHashSet(nameGrouping),
				Sets.newHashSet(), Sets.newHashSet(), null, null, 0);

		handler.updateGroupingNodesSelection(srcTree, selectEvent);

		assertThat(srcTree.getSelection()).hasSize(3);
		assertThat(srcTree.getSelection()).contains(nameDe, nameEn, nameGrouping);
	}

	@Test
	public void testDeselectGroupingNodeWhenSubNodeDeselected()
	{
		final AbstractTreeModel<TreeNode<SelectedAttribute>> srcTree = createTreeModel(name, ean, code);

		final TreeNode<SelectedAttribute> nameEn = findNode(srcTree.getRoot(), name.getQualifier(), en.getIsocode());
		final TreeNode<SelectedAttribute> nameDe = findNode(srcTree.getRoot(), name.getQualifier(), de.getIsocode());
		final TreeNode<SelectedAttribute> nameGrouping = findNode(srcTree.getRoot(), this.name.getQualifier(), null);

		srcTree.setSelection(Sets.newHashSet(nameDe, nameGrouping));

		final SelectEvent selectEvent = new SelectEvent(Events.ON_SELECT, null, null, null, null,
				Sets.newHashSet(nameDe, nameGrouping), Sets.newHashSet(nameDe, nameEn, nameGrouping), Sets.newHashSet(nameEn), null,
				null, 0);

		handler.updateGroupingNodesSelection(srcTree, selectEvent);

		assertThat(srcTree.getSelection()).containsOnly(nameDe);
	}

	@Test
	public void testDeselectSubNodesWhenGroupingDeselected()
	{
		final AbstractTreeModel<TreeNode<SelectedAttribute>> srcTree = createTreeModel(name, ean, code);

		final TreeNode<SelectedAttribute> nameEn = findNode(srcTree.getRoot(), name.getQualifier(), en.getIsocode());
		final TreeNode<SelectedAttribute> nameDe = findNode(srcTree.getRoot(), name.getQualifier(), de.getIsocode());
		final TreeNode<SelectedAttribute> nameGrouping = findNode(srcTree.getRoot(), this.name.getQualifier(), null);

		srcTree.setSelection(Sets.newHashSet(nameDe, nameEn));

		final SelectEvent selectEvent = new SelectEvent(Events.ON_SELECT, null, null, null, null, Sets.newHashSet(nameDe, nameEn),
				Sets.newHashSet(nameDe, nameEn, nameGrouping), Sets.newHashSet(nameGrouping), null, null, 0);

		handler.updateGroupingNodesSelection(srcTree, selectEvent);

		assertThat(srcTree.getSelection()).isEmpty();
	}

	private TreeNode<SelectedAttribute> findNode(final TreeNode<SelectedAttribute> root, final String qualifier,
			final String isoCode)
	{

		for (final TreeNode<SelectedAttribute> node : root.getChildren())
		{
			final SelectedAttribute data = node.getData();
			if (data.getQualifier().equals(qualifier) && Objects.equals(data.getIsoCode(), isoCode))
			{
				return node;
			}

			if (!node.isLeaf())
			{
				final TreeNode<SelectedAttribute> subSearch = findNode(node, qualifier, isoCode);
				if (subSearch != null)
				{
					return subSearch;
				}
			}
		}
		return null;
	}

	@Test
	public void testSortTree()
	{
		final AbstractTreeModel<TreeNode<SelectedAttribute>> treeModel = createTreeModel(name, code, ean);

		handler.sort(treeModel, true);

		final TreeNode<SelectedAttribute> root = treeModel.getRoot();
		assertThat(treeModel.getChild(root, 0).getData().getQualifier()).isEqualTo(code.getQualifier());
		assertThat(treeModel.getChild(root, 1).getData().getQualifier()).isEqualTo(ean.getQualifier());
		assertThat(treeModel.getChild(root, 2).getData().getQualifier()).isEqualTo(name.getQualifier());
	}

	@Test
	public void testSortFilteredTree()
	{
		final AbstractTreeModel<TreeNode<SelectedAttribute>> treeModel = handler.filterTreeModel(createTreeModel(name, code, ean),
				"e");

		handler.sort(treeModel, true);

		final TreeNode<SelectedAttribute> root = treeModel.getRoot();
		assertThat(treeModel.getChild(root, 0).getData().getQualifier()).isEqualTo(code.getQualifier());
		assertThat(treeModel.getChild(root, 1).getData().getQualifier()).isEqualTo(ean.getQualifier());
		assertThat(treeModel.getChild(root, 2).getData().getQualifier()).isEqualTo(name.getQualifier());
	}

	@Test
	public void testFilterTree()
	{
		final AbstractTreeModel<TreeNode<SelectedAttribute>> filtered = handler.filterTreeModel(createTreeModel(name, code, ean),
				"ea");

		assertThat(filtered.getChildCount(filtered.getRoot())).isEqualTo(1);
		assertThat(filtered.getChild(filtered.getRoot(), 0).getData().getQualifier()).isEqualTo(ean.getQualifier());
	}

	@Test
	public void testFilterTreeWithEmptyText()
	{
		final AbstractTreeModel<TreeNode<SelectedAttribute>> filtered = handler.filterTreeModel(createTreeModel(name, code, ean),
				"");

		assertThat(filtered.getChildCount(filtered.getRoot())).isEqualTo(3);
	}

	@Test
	public void testExtractAttributesWithoutGroupingNodes()
	{
		//given
		final AbstractTreeModel<TreeNode<SelectedAttribute>> treeModel = createTreeModel(code, name, ean);
		//when
		final List<SelectedAttribute> selectedAttributes = handler.extractAttributes(treeModel);
		//then
		assertThat(selectedAttributes).hasSize(4);
		assertThat(selectedAttributes.stream().filter(sa -> sa.getQualifier().equals(name.getQualifier()))
				.map(SelectedAttribute::getIsoCode).collect(Collectors.toList())).containsOnly(en.getIsocode(), de.getIsocode());
		assertThat(selectedAttributes.stream().map(SelectedAttribute::getQualifier).collect(Collectors.toList()))
				.contains(code.getQualifier(), name.getQualifier(), ean.getQualifier());
		assertThat(
				selectedAttributes.stream().anyMatch(sa -> sa.getQualifier().equals(name.getQualifier()) && sa.getIsoCode() == null))
						.isFalse();
	}

	@Test
	public void testCreateNodesWithLocalizedChildren()
	{
		//given
		final Predicate<SelectedAttribute> skipEan = attr -> !attr.getQualifier().equals(ean.getQualifier());

		//when
		final List<DefaultTreeNode<SelectedAttribute>> treeNodes = handler.createTreeNodes(Lists.newArrayList(code, name, ean),
				skipEan);

		//then
		assertThat(treeNodes).hasSize(2);
		assertThat(treeNodes.stream().filter(node -> node.getData().getIsoCode() != null).collect(Collectors.toSet())).isEmpty();
		assertThat(treeNodes.stream().map(node -> node.getData().getQualifier()).collect(Collectors.toSet()))
				.containsOnly(name.getQualifier(), code.getQualifier());
		final Optional<DefaultTreeNode<SelectedAttribute>> nameNode = getAttributeByQualifier(name.getQualifier(), treeNodes);
		assertThat(nameNode.isPresent()).isTrue();
		assertThat(nameNode.get().getChildren()).hasSize(2);
		assertThat(nameNode.get().getChildren().stream().map(node -> node.getData().getIsoCode()).collect(Collectors.toSet()))
				.containsOnly(en.getIsocode(), de.getIsocode());
		assertThat(nameNode.get().getChildren().stream().map(node -> node.getData().getQualifier()).collect(Collectors.toSet()))
				.containsOnly(name.getQualifier());
	}

	@Test
	public void shouldIndicateNodeAsSelectableWhenAttributeIsNotMandatory()
	{
		// given
		final TreeNode<SelectedAttribute> node = mock(TreeNode.class);
		final AttributeDescriptorModel attributeDescriptorModel = mockAttributeDescriptor("any", "any", false, false);
		final SelectedAttribute selectedAttribute = new SelectedAttribute(attributeDescriptorModel);

		when(attributeDescriptorModel.getOptional()).thenReturn(true);
		when(node.getData()).thenReturn(selectedAttribute);

		// when
		final boolean isSelectable = handler.isNodeSelectable(node);

		// then
		assertThat(isSelectable).isTrue();
	}

	@Test
	public void shouldIndicateNodeAsSelectableWhenAttributeIsMandatoryButIsLocalizedForNotCurrentLanguage()
	{
		// given
		final TreeNode<SelectedAttribute> node = mock(TreeNode.class);
		final AttributeDescriptorModel attributeDescriptorModel = mock(AttributeDescriptorModel.class);
		final SelectedAttribute selectedAttribute = new SelectedAttribute("fr", attributeDescriptorModel);

		when(attributeDescriptorModel.getOptional()).thenReturn(false);
		when(attributeDescriptorModel.getLocalized()).thenReturn(true);
		when(commonI18NService.getCurrentLanguage()).thenReturn(en);
		when(node.getData()).thenReturn(selectedAttribute);

		// when
		final boolean isSelectable = handler.isNodeSelectable(node);

		// then
		assertThat(isSelectable).isTrue();
	}

	@Test
	public void shouldNotIndicateNodeAsSelectableWhenAttributeIsMandatory()
	{
		// given
		final TreeNode<SelectedAttribute> node = mock(TreeNode.class);
		final AttributeDescriptorModel attributeDescriptorModel = mock(AttributeDescriptorModel.class);
		final SelectedAttribute selectedAttribute = new SelectedAttribute(attributeDescriptorModel);

		when(attributeDescriptorModel.getOptional()).thenReturn(false);
		when(attributeDescriptorModel.getLocalized()).thenReturn(false);
		when(node.getData()).thenReturn(selectedAttribute);

		// when
		final boolean isSelectable = handler.isNodeSelectable(node);

		// then
		assertThat(isSelectable).isFalse();
	}

	@Test
	public void shouldNotIndicateNodeAsSelectableWhenAttributeIsMandatoryAndIsLocalizedForCurrentLanguage()
	{
		// given
		final TreeNode<SelectedAttribute> node = mock(TreeNode.class);
		final AttributeDescriptorModel attributeDescriptorModel = mock(AttributeDescriptorModel.class);
		final SelectedAttribute selectedAttribute = new SelectedAttribute("en", attributeDescriptorModel);

		when(attributeDescriptorModel.getOptional()).thenReturn(false);
		when(attributeDescriptorModel.getLocalized()).thenReturn(true);
		when(commonI18NService.getCurrentLanguage()).thenReturn(en);
		when(node.getData()).thenReturn(selectedAttribute);

		// when
		final boolean isSelectable = handler.isNodeSelectable(node);

		// then
		assertThat(isSelectable).isFalse();
	}

	@Test
	public void shouldNotIndicateNodeAsSelectableWhenItsLocalizedChildIsNotSelectable()
	{
		// given
		final TreeNode<SelectedAttribute> node = mock(TreeNode.class);
		final AttributeDescriptorModel attributeDescriptorModel = mock(AttributeDescriptorModel.class);
		final SelectedAttribute selectedAttribute = new SelectedAttribute(attributeDescriptorModel);

		when(attributeDescriptorModel.getOptional()).thenReturn(false);
		when(attributeDescriptorModel.getLocalized()).thenReturn(true);
		when(commonI18NService.getCurrentLanguage()).thenReturn(en);
		when(node.getData()).thenReturn(selectedAttribute);

		final List<TreeNode<SelectedAttribute>> children = new ArrayList<>();
		final TreeNode<SelectedAttribute> child = mock(TreeNode.class);
		children.add(child);

		final AttributeDescriptorModel childAttributeDescriptor = mock(AttributeDescriptorModel.class);
		final SelectedAttribute childSelectedAttribute = new SelectedAttribute("en", attributeDescriptorModel);
		when(childAttributeDescriptor.getOptional()).thenReturn(false);
		when(childAttributeDescriptor.getLocalized()).thenReturn(true);
		when(child.getData()).thenReturn(childSelectedAttribute);
		when(node.getChildren()).thenReturn(children);

		// when
		final boolean isSelectable = handler.isNodeSelectable(node);

		// then
		assertThat(isSelectable).isFalse();
	}

	private Optional<DefaultTreeNode<SelectedAttribute>> getAttributeByQualifier(final String qualifier,
			final List<DefaultTreeNode<SelectedAttribute>> treeNodes)
	{
		return treeNodes.stream().filter(node -> node.getData().getQualifier().equals(qualifier)).findFirst();
	}


	private AttributeDescriptorModel mockAttributeDescriptor(final String qualifier, final String name, final boolean localized,
			final boolean mandatory)
	{
		final ComposedTypeModel composedTypeModel = mock(ComposedTypeModel.class);
		final AttributeDescriptorModel descriptor = spy(new AttributeDescriptorModel());
		given(composedTypeModel.getUniqueKeyAttributes()).willReturn(Collections.emptyList());
		doReturn(composedTypeModel).when(descriptor).getEnclosingType();
		doReturn(false).when(descriptor).getUnique();
		doReturn(qualifier).when(descriptor).getQualifier();
		doReturn(name).when(descriptor).getName();
		doReturn(localized).when(descriptor).getLocalized();
		doReturn(!mandatory).when(descriptor).getOptional();
		return descriptor;
	}

	private AbstractTreeModel<TreeNode<SelectedAttribute>> createTreeModel(final AttributeDescriptorModel... descriptors)
	{
		final Predicate<SelectedAttribute> acceptAll = attr -> true;
		final List<DefaultTreeNode<SelectedAttribute>> treeNodes = handler.createTreeNodes(Lists.newArrayList(descriptors),
				acceptAll);

		final DefaultTreeModel<SelectedAttribute> selectedAttributeDefaultTreeModel = new DefaultTreeModel<>(
				new DefaultTreeNode<>(null, treeNodes));
		selectedAttributeDefaultTreeModel.setMultiple(true);
		return selectedAttributeDefaultTreeModel;
	}
}
