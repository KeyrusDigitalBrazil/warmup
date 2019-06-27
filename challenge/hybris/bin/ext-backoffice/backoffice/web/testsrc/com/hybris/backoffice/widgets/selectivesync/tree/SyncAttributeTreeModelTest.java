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
package com.hybris.backoffice.widgets.selectivesync.tree;

import static com.hybris.backoffice.widgets.selectivesync.tree.FilteredTreeModel.IncludedMode.resolveIncludeMode;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.SyncAttributeDescriptorConfigModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.zkoss.zul.TreeModel;

import com.hybris.cockpitng.labels.LabelService;
import com.hybris.cockpitng.testing.util.BeanLookup;
import com.hybris.cockpitng.testing.util.BeanLookupFactory;
import com.hybris.cockpitng.testing.util.CockpitTestUtil;
import com.hybris.cockpitng.tree.util.TreeUtils;



@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SyncAttributeTreeModelTest
{
	private static final String TEST_KEYWORD = "keyword";
	public static final String ATTRIBUTE_1 = "attribute1";
	public static final String ATTRIBUTE_2 = "attribute2";
	public static final String ATTRIBUTE_3 = "attribute3";
	public static final String ATTRIBUTE_4 = "attribute4";
	public static final String ATTRIBUTE_5 = "attribute5";
	private SyncAttributeTreeModel treeModel;

	@Mock
	private SyncTypeAttributeDataTreeNode node;

	@Mock
	private FilteredTreeModel<SyncTypeAttributeDataTreeNode> filteredTreeModel;

	@Mock
	private FilterExecutionListener filterExecutionListener;

	@Mock
	private LabelService labelService;
	
	@Spy
	private SyncAttributeTreeModelFactory syncAttributeTreeModelFactory;

	ComposedTypeModel rootType;
	ComposedTypeModel node1;
	ComposedTypeModel node2;

	List<SyncAttributeDescriptorConfigModel> attributes;

	/*-
	* (ROOT_NODE)
	* + rootNode
	* 		- attribute1
	* 		- attribute2
	* 		+ node1
	* 			- attribute3
	* 			- attribute4
	* 			- attribute5
	* 		    + node2
	* 				- attribute5
	* 				- attribute5
	*
	* types
	* 	rootNode -> node1 -> shouldBeOmitted1 -> shouldBeOmitted2 -> node2
	*/
	@Before
	public void setUp()
	{
		CockpitTestUtil.mockZkEnvironment();
		final BeanLookup beanLookup = BeanLookupFactory.createBeanLookup().registerBean("labelService", labelService).getLookup();
		CockpitTestUtil.mockBeanLookup(beanLookup);

		when(labelService.getObjectLabel(any())).thenAnswer(answer -> {
			if (answer.getArguments().length > 0)
			{
				final Object argument = answer.getArguments()[0];
				if (argument instanceof ComposedTypeModel)
				{
					return ((ComposedTypeModel) argument).getName();
				}
				else if (argument instanceof SyncAttributeDescriptorConfigModel)
				{
					return ((SyncAttributeDescriptorConfigModel) argument).getAttributeDescriptor().getName();
				}
			}
			return "label";
		});

		rootType = createSpy("root");
		node1 = createSpy("node1");
		final ComposedTypeModel nodeTmp1 = createSpy("shouldBeOmitted1");
		final ComposedTypeModel nodeTmp2 = createSpy("shouldBeOmitted2");
		node2 = createSpy("node2");
		addChildNode(rootType, node1);
		addChildNode(node1, nodeTmp1);
		addChildNode(nodeTmp1, nodeTmp2);
		addChildNode(nodeTmp2, node2);

		attributes = new ArrayList<>();

		attributes.add(TreeModelTestUtils.createAndAddSyncAttribute(rootType, ATTRIBUTE_1));
		attributes.add(TreeModelTestUtils.createAndAddSyncAttribute(rootType, ATTRIBUTE_2));
		attributes.add(TreeModelTestUtils.createAndAddSyncAttribute(node1, ATTRIBUTE_3));
		attributes.add(TreeModelTestUtils.createAndAddSyncAttribute(node1, ATTRIBUTE_4));
		attributes.add(TreeModelTestUtils.createAndAddSyncAttribute(node2, ATTRIBUTE_5));
		attributes.add(TreeModelTestUtils.createAndAddSyncAttribute(node2, ATTRIBUTE_5));//second attribute with same name on this node
		attributes.add(TreeModelTestUtils.createAndAddSyncAttribute(node1, ATTRIBUTE_5));//same attribute name but different node

		treeModel = spy(syncAttributeTreeModelFactory.create(attributes, rootType));
	}

	@Test
	public void shouldBeProperlyCreatedModel()
	{
		attributes.forEach(
				attribute -> assertThat(treeModel.isLeaf(SyncTypeAttributeDataTreeNode.createAttributeNode(attribute))).isTrue());

		assertThat(treeModel.isLeaf(SyncTypeAttributeDataTreeNode.createTypeNode(rootType))).isFalse();
		assertThat(treeModel.isLeaf(SyncTypeAttributeDataTreeNode.createTypeNode(node1))).isFalse();
		assertThat(treeModel.isLeaf(SyncTypeAttributeDataTreeNode.createTypeNode(node2))).isFalse();
	}

	@Test
	public void shouldFilterTree()
	{
		TreeModel<SyncTypeAttributeDataTreeNode> filtered = treeModel.filter(new FilterContext("node1", true, true));
		assertThat(filtered.getChildCount(SyncTypeAttributeDataTreeNode.createTypeNode(rootType))).isEqualTo(1);
		assertThat(filtered.getChildCount(SyncTypeAttributeDataTreeNode.createTypeNode(node1))).isEqualTo(0);

		filtered = treeModel.filter(new FilterContext("node2", true, true));
		assertThat(filtered.getChildCount(SyncTypeAttributeDataTreeNode.createTypeNode(rootType))).isEqualTo(1);
		assertThat(filtered.getChildCount(SyncTypeAttributeDataTreeNode.createTypeNode(node1))).isEqualTo(1);
		assertThat(filtered.getChildCount(SyncTypeAttributeDataTreeNode.createTypeNode(node2))).isEqualTo(0);

		filtered = treeModel.filter(new FilterContext(ATTRIBUTE_1, true, true));
		assertThat(filtered.getChildCount(SyncTypeAttributeDataTreeNode.createTypeNode(rootType))).isEqualTo(1);
		assertThat(filtered.getChildCount(SyncTypeAttributeDataTreeNode.createTypeNode(node1))).isEqualTo(0);
		assertThat(filtered.getChildCount(SyncTypeAttributeDataTreeNode.createTypeNode(node2))).isEqualTo(0);

		filtered = treeModel.filter(new FilterContext(ATTRIBUTE_3, true, true));
		assertThat(filtered.getChildCount(SyncTypeAttributeDataTreeNode.createTypeNode(rootType))).isEqualTo(1);
		assertThat(filtered.getChildCount(SyncTypeAttributeDataTreeNode.createTypeNode(node1))).isEqualTo(1);
		assertThat(filtered.getChildCount(SyncTypeAttributeDataTreeNode.createTypeNode(node2))).isEqualTo(0);

		filtered = treeModel.filter(new FilterContext(ATTRIBUTE_5, true, true));
		assertThat(filtered.getChildCount(SyncTypeAttributeDataTreeNode.createTypeNode(rootType))).isEqualTo(1);
		assertThat(filtered.getChildCount(SyncTypeAttributeDataTreeNode.createTypeNode(node1))).isEqualTo(2);
		assertThat(filtered.getChildCount(SyncTypeAttributeDataTreeNode.createTypeNode(node2))).isEqualTo(2);
	}

	@Test
	public void shouldDeselectAllNodesWhenRootDeselected()
	{
		final SyncTypeAttributeDataTreeNode root = getRoot();

		treeModel.nodeOnCheck(root, false);

		attributes.forEach(attribute -> verify(attribute).setIncludedInSync(false));

		assertThat(root.getSelection()).isEqualTo(TreeItemSelectable.SelectionType.NONE);
		treeModel.getChildren(root)
				.forEach(child -> assertThat(child.getSelection()).isEqualTo(TreeItemSelectable.SelectionType.NONE));
	}

	@Test
	public void shouldSelectAllNodesWhenRootSelected()
	{
		final SyncTypeAttributeDataTreeNode root = getRoot();

		treeModel.nodeOnCheck(root, true);

		attributes.forEach(attribute -> verify(attribute).setIncludedInSync(true));

		treeModel.getChildren(root)
				.forEach(child -> assertThat(child.getSelection()).isEqualTo(TreeItemSelectable.SelectionType.ALL));
	}

	@Test
	public void shouldPartiallySelect()
	{
		//given
		final SyncTypeAttributeDataTreeNode root = getRoot();
		treeModel.nodeOnCheck(root, false);

		final SyncTypeAttributeDataTreeNode attribute5Node = getAttribute5Node();
		//when
		treeModel.nodeOnCheck(attribute5Node, true);
		//then
		verify(attributes.get(4)).setIncludedInSync(true);
		assertThat(root.getSelection()).isEqualTo(TreeItemSelectable.SelectionType.PARTIALLY);
	}

	private ComposedTypeModel createSpy(final String name)
	{
		final ComposedTypeModel type = spy(new ComposedTypeModel());
		doReturn(name).when(type).getName();
		return type;
	}

	private void addChildNode(final ComposedTypeModel parent, final ComposedTypeModel childNode)
	{
		doReturn(parent).when(childNode).getSuperType();
	}

	private SyncTypeAttributeDataTreeNode getRoot()
	{
		final List<SyncTypeAttributeDataTreeNode> childs = treeModel.getChildren(treeModel.getRoot());
		assertThat(childs.size()).isEqualTo(1);
		return childs.get(0);
	}

	private SyncTypeAttributeDataTreeNode getAttribute5Node()
	{
		SyncTypeAttributeDataTreeNode attribute5Node = null;
		for (final SyncTypeAttributeDataTreeNode children : treeModel
				.getChildren(SyncTypeAttributeDataTreeNode.createTypeNode(node2)))
		{
			if (children.equals(SyncTypeAttributeDataTreeNode.createAttributeNode(attributes.get(4))))
			{
				attribute5Node = children;
				break;
			}
		}
		assertNotNull(attribute5Node);
		return attribute5Node;
	}

	@Test
	public void shouldExpandFilteredNodesDuringFilter()
	{
		//given
		final FilterContext context = new FilterContext(TEST_KEYWORD, true, false);

		// when
		treeModel.filter(context);

		//then
		verify(treeModel).expandFilteredNodes(any(FilteredTreeModel.class));
	}

	@Test
	public void shouldNotExpandNodesWhenFilterIsEmpty()
	{
		//given
		final FilterContext context = new FilterContext(StringUtils.EMPTY, true, false);

		// when
		treeModel.filter(context);

		//then
		verify(treeModel, times(0)).expandFilteredNodes(any(FilteredTreeModel.class));
	}

	@Test
	public void shouldExpandMainNodeDuringFilterWhenShowingIncludedItems()
	{
		//given
		final FilterContext context = new FilterContext(TEST_KEYWORD, true, false);

		// when
		treeModel.filter(context);

		//then
		verify(treeModel).expandMainNode(any(FilteredTreeModel.class));
	}

	@Test
	public void shouldExpandMainNodeDuringFilterWhenShowingNotIncludedItems()
	{
		//given
		final FilterContext context = new FilterContext(TEST_KEYWORD, false, true);

		// when
		treeModel.filter(context);

		//then
		verify(treeModel).expandMainNode(any(FilteredTreeModel.class));
	}

	@Test
	public void shouldExpandMainNodeDuringFilterWhenShowingIncludedAndNotIncludedItems()
	{
		//given
		final FilterContext context = new FilterContext(TEST_KEYWORD, true, true);

		// when
		treeModel.filter(context);

		//then
		verify(treeModel).expandMainNode(any(FilteredTreeModel.class));
	}

	@Test
	public void shouldNotExpandMainNodeDuringFilterWhenNotShowingIncludedAndNotIncludedItems()
	{
		//given
		final FilterContext context = new FilterContext(TEST_KEYWORD, false, false);

		// when
		treeModel.filter(context);

		//then
		verify(treeModel, times(0)).expandMainNode(any(FilteredTreeModel.class));
	}

	@Test
	public void shouldNotExpandNodesWhenFilterIsNull()
	{
		// given
		final FilterContext context = new FilterContext(null, false, false);

		// when
		treeModel.filter(context);

		//then
		verify(treeModel, times(0)).expandFilteredNodes(any(FilteredTreeModel.class));
	}

	@Test
	public void shouldClearSelectionDuringFilter()
	{
		//given
		final FilterContext context = new FilterContext(TEST_KEYWORD, true, false);

		// when
		treeModel.filter(context);

		//then
		verify(treeModel).clearSelection(any());
	}

	@Test
	public void shouldClearSelectionAfterExpandingMainNodeAndFilteredNodes()
	{
		//given
		final FilterContext context = new FilterContext(TEST_KEYWORD, true, false);
		final InOrder orderVerifier = inOrder(treeModel);

		// when
		treeModel.filter(context);

		//then
		orderVerifier.verify(treeModel).expandMainNode(any(FilteredTreeModel.class));
		orderVerifier.verify(treeModel).expandFilteredNodes(any(FilteredTreeModel.class));
		orderVerifier.verify(treeModel).clearSelection(any());
	}

	@Test
	public void shouldInvokeOnFilterExecutedListener()
	{
		// given
		treeModel.setOnFilterExecutedListener(filterExecutionListener);
		final FilterContext context = new FilterContext(TEST_KEYWORD, true, false);

		// when
		treeModel.filter(context);

		//then
		verify(treeModel).onFilterExecuted();
		verify(filterExecutionListener).onFilterExecuted();
	}

	@Test
	public void shouldHandleOnFilterExecutedListenerGracefullyWhenItIsNull()
	{
		// given
		treeModel.setOnFilterExecutedListener(null);
		final FilterContext context = new FilterContext(TEST_KEYWORD, true, false);

		// when
		treeModel.filter(context);

		//then no NPE
		verify(treeModel).onFilterExecuted();
	}

	@Test
	public void shouldExpandNodes()
	{
		// given
		final FilteredTreeModel<SyncTypeAttributeDataTreeNode> model = createFilteredTreeModelStub();

		// when
		treeModel.expandFilteredNodes(model);
		final List<SyncTypeAttributeDataTreeNode> nodes = new ArrayList<>();
		model.getChildrenMap().forEach((key, value) -> nodes.addAll(value));

		// then
		verify(treeModel, times(nodes.size())).expandNode(eq(model), any(SyncTypeAttributeDataTreeNode.class));
	}

	private FilteredTreeModel<SyncTypeAttributeDataTreeNode> createFilteredTreeModelStub()
	{
		final String filter = "attribute";
		final FilteredTreeModel.IncludedMode includeMode = resolveIncludeMode(true, false);
		return new FilteredTreeModel<>(treeModel, filter, SyncTypeAttributeDataTreeNode::getText, TreeUtils.MatchMode.CONTAINS,
				includeMode);
	}

}
