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

import static com.hybris.backoffice.widgets.selectivesync.tree.SyncAttributeTreeModelFactory.COMPARE_NODES_BY_TYPE_AND_ATTRIBUTE_NAMES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.SyncAttributeDescriptorConfigModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.cockpitng.labels.LabelService;
import com.hybris.cockpitng.testing.util.BeanLookup;
import com.hybris.cockpitng.testing.util.BeanLookupFactory;
import com.hybris.cockpitng.testing.util.CockpitTestUtil;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SyncAttributeTreeModelFactoryTest
{

	public static final String ATTRIBUTE_1 = "attribute1";
	public static final String ATTRIBUTE_2 = "attribute2";
	public static final String ATTRIBUTE_3 = "attribute3";
	public static final String ATTRIBUTE_4 = "attribute4";
	public static final String ATTRIBUTE_5 = "attribute5";

	@Spy
	private SyncAttributeTreeModelFactory syncAttributeTreeModelFactory;

	private ComposedTypeModel root;
	private ComposedTypeModel rootType;
	private ComposedTypeModel node1;
	private ComposedTypeModel node2;

	@Before
	public void setUp()
	{
		final LabelService labelService = mock(LabelService.class);
		final BeanLookup beanLookup = BeanLookupFactory.createBeanLookup().registerBean("labelService", labelService).getLookup();
		CockpitTestUtil.mockBeanLookup(beanLookup);

		root = createSpy("rootNode");
		rootType = createSpy("rootNode");
		node1 = createSpy("node1");
		node2 = createSpy("node2");

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
	}

	//	/*-
	//	 * (ROOT_NODE)
	//	 * 	+ rootNode
	//	 * 		- attribute1
	//	 *
	//	 */
	@Test
	public void createTree()
	{
		final SyncAttributeDescriptorConfigModel att1 = TreeModelTestUtils.createAndAddSyncAttribute(root, ATTRIBUTE_1);

		final Collection<SyncAttributeDescriptorConfigModel> attributes = new ArrayList<>();
		Collections.addAll(attributes, att1);

		final Map<SyncTypeAttributeDataTreeNode, List<SyncTypeAttributeDataTreeNode>> tree = syncAttributeTreeModelFactory
				.createTree(attributes, root);
		assertNotNull(tree);
		assertThat(tree.size()).isEqualTo(2);

		assertRoot(tree, root);
		assertAttribute(tree, root, att1);
	}

	/*-
	 * (ROOT_NODE)
	 * + rootNode
	 * 		- attribute1
	 * 		- attribute2
	 * 		+ node1
	 * 			- attribute3
	 * 			- attribute4
	 * 		+ node2
	 * 			- attribute5
	 *
	 */
	@Test
	public void shouldCreateTreeWithOneAttributesAndTwoSubNodes()
	{
		//given
		final Collection<SyncAttributeDescriptorConfigModel> attributes = new ArrayList<>();
		addChildNode(rootType, node1);
		addChildNode(rootType, node2);

		final SyncAttributeDescriptorConfigModel att1 = TreeModelTestUtils.createAndAddSyncAttribute(rootType, ATTRIBUTE_1);
		final SyncAttributeDescriptorConfigModel att2 = TreeModelTestUtils.createAndAddSyncAttribute(rootType, ATTRIBUTE_2);
		final SyncAttributeDescriptorConfigModel att3 = TreeModelTestUtils.createAndAddSyncAttribute(node1, ATTRIBUTE_3);
		final SyncAttributeDescriptorConfigModel att4 = TreeModelTestUtils.createAndAddSyncAttribute(node1, ATTRIBUTE_4);
		final SyncAttributeDescriptorConfigModel att5 = TreeModelTestUtils.createAndAddSyncAttribute(node2, ATTRIBUTE_5);

		Collections.addAll(attributes, att1, att2, att3, att4, att5);

		//when
		final Map<SyncTypeAttributeDataTreeNode, List<SyncTypeAttributeDataTreeNode>> tree = syncAttributeTreeModelFactory
				.createTree(attributes, rootType);

		//then
		assertNotNull(tree);
		assertThat(tree.size()).isEqualTo(4);
		assertRoot(tree, rootType);
		assertParent(tree, rootType, node1);
		assertParent(tree, rootType, node2);

		assertChildCount(tree, rootType, 4);
		assertChildCount(tree, node1, 2);
		assertChildCount(tree, node2, 1);

		assertAttribute(tree, rootType, node1);
		assertAttribute(tree, rootType, node2);
		assertAttribute(tree, rootType, att1);
		assertAttribute(tree, rootType, att2);

		assertAttribute(tree, node1, att3);
		assertAttribute(tree, node1, att4);
		assertAttribute(tree, node2, att5);
	}

	/*-
	 * (ROOT_NODE)
	 * + rootNode
	 * 		- attribute1
	 * 		- attribute2
	 * 		+ node1
	 * 			- attribute3
	 * 			- attribute4
	 * 		    + node2
	 * 				- attribute5
	 *
	 */
	@Test
	public void shouldCreateTreeWithSubNodeWithAnotherSubNode()
	{
		//given
		final Collection<SyncAttributeDescriptorConfigModel> attributes = new ArrayList<>();
		addChildNode(rootType, node1);
		addChildNode(node1, node2);

		final SyncAttributeDescriptorConfigModel att1 = TreeModelTestUtils.createAndAddSyncAttribute(rootType, ATTRIBUTE_1);
		final SyncAttributeDescriptorConfigModel att2 = TreeModelTestUtils.createAndAddSyncAttribute(rootType, ATTRIBUTE_2);
		final SyncAttributeDescriptorConfigModel att3 = TreeModelTestUtils.createAndAddSyncAttribute(node1, ATTRIBUTE_3);
		final SyncAttributeDescriptorConfigModel att4 = TreeModelTestUtils.createAndAddSyncAttribute(node1, ATTRIBUTE_4);
		final SyncAttributeDescriptorConfigModel att5 = TreeModelTestUtils.createAndAddSyncAttribute(node2, ATTRIBUTE_5);

		Collections.addAll(attributes, att1, att2, att3, att4, att5);

		//when
		final Map<SyncTypeAttributeDataTreeNode, List<SyncTypeAttributeDataTreeNode>> tree = syncAttributeTreeModelFactory
				.createTree(attributes, rootType);

		//then
		assertNotNull(tree);
		assertThat(tree.size()).isEqualTo(4);

		assertRoot(tree, rootType);
		assertParent(tree, rootType, node1);
		assertParent(tree, node1, node2);

		assertChildCount(tree, rootType, 3);
		assertChildCount(tree, node1, 3);
		assertChildCount(tree, node2, 1);

		assertAttribute(tree, rootType, node1);
		assertAttribute(tree, rootType, att1);
		assertAttribute(tree, rootType, att2);

		assertAttribute(tree, node1, att3);
		assertAttribute(tree, node1, att4);
		assertAttribute(tree, node1, node2);

		assertAttribute(tree, node2, att5);
	}

	/*-
	 * (ROOT_NODE)
	 * + rootNode
	 * 		- attribute1
	 * 		- attribute2
	 * 		+ node1
	 * 			- attribute3
	 * 			- attribute4
	 * 		    + node2
	 * 				- attribute5
	 *
	 *
	 *
	 * types
	 * 	rootNode -> node1 -> shouldBeOmitted1 -> shouldBeOmitted2 -> node2
	 */
	@Test
	public void shouldCreateTreeWithSubNodeWithAnotherSubNodeWhereParentIsNotDirect()
	{
		//given
		final Collection<SyncAttributeDescriptorConfigModel> attributes = new ArrayList<>();

		final ComposedTypeModel nodeTmp1 = createSpy("shouldBeOmitted1");
		final ComposedTypeModel nodeTmp2 = createSpy("shouldBeOmitted2");
		addChildNode(rootType, node1);
		addChildNode(node1, nodeTmp1);
		addChildNode(nodeTmp1, nodeTmp2);
		addChildNode(nodeTmp2, node2);

		final SyncAttributeDescriptorConfigModel att1 = TreeModelTestUtils.createAndAddSyncAttribute(rootType, ATTRIBUTE_1);
		final SyncAttributeDescriptorConfigModel att2 = TreeModelTestUtils.createAndAddSyncAttribute(rootType, ATTRIBUTE_2);
		final SyncAttributeDescriptorConfigModel att3 = TreeModelTestUtils.createAndAddSyncAttribute(node1, ATTRIBUTE_3);
		final SyncAttributeDescriptorConfigModel att4 = TreeModelTestUtils.createAndAddSyncAttribute(node1, ATTRIBUTE_4);
		final SyncAttributeDescriptorConfigModel att5 = TreeModelTestUtils.createAndAddSyncAttribute(node2, ATTRIBUTE_5);

		Collections.addAll(attributes, att1, att2, att3, att4, att5);

		//when
		final Map<SyncTypeAttributeDataTreeNode, List<SyncTypeAttributeDataTreeNode>> tree = syncAttributeTreeModelFactory
				.createTree(attributes, rootType);

		//then
		assertNotNull(tree);
		assertThat(tree.size()).isEqualTo(4);

		assertRoot(tree, rootType);
		assertParent(tree, rootType, node1);
		assertParent(tree, node1, node2);

		assertChildCount(tree, rootType, 3);
		assertChildCount(tree, node1, 3);
		assertChildCount(tree, node2, 1);

		assertAttribute(tree, rootType, node1);
		assertAttribute(tree, rootType, att1);
		assertAttribute(tree, rootType, att2);

		assertAttribute(tree, node1, att3);
		assertAttribute(tree, node1, att4);
		assertAttribute(tree, node1, node2);

		assertAttribute(tree, node2, att5);
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

	private void assertAttribute(final Map<SyncTypeAttributeDataTreeNode, List<SyncTypeAttributeDataTreeNode>> tree,
			final ComposedTypeModel parent, final ComposedTypeModel child)
	{
		assertThat(tree.get(SyncTypeAttributeDataTreeNode.createTypeNode(parent)))
				.contains(SyncTypeAttributeDataTreeNode.createTypeNode(child));
	}

	private void assertAttribute(final Map<SyncTypeAttributeDataTreeNode, List<SyncTypeAttributeDataTreeNode>> tree,
			final ComposedTypeModel parent, final SyncAttributeDescriptorConfigModel child)
	{
		assertThat(tree.get(SyncTypeAttributeDataTreeNode.createTypeNode(parent)))
				.contains(SyncTypeAttributeDataTreeNode.createAttributeNode(child));
	}

	private void assertChildCount(final Map<SyncTypeAttributeDataTreeNode, List<SyncTypeAttributeDataTreeNode>> tree,
			final ComposedTypeModel parent, final int childCount)
	{
		assertThat(tree.get(SyncTypeAttributeDataTreeNode.createTypeNode(parent)).size()).isEqualTo(childCount);
	}


	private void assertRoot(final Map<SyncTypeAttributeDataTreeNode, List<SyncTypeAttributeDataTreeNode>> tree,
			final ComposedTypeModel child)
	{
		final List<SyncTypeAttributeDataTreeNode> childs = tree.get(SyncTypeAttributeDataTreeNode.createRootNode());
		if (!childs.contains(SyncTypeAttributeDataTreeNode.createTypeNode(child)))
		{
			fail("ROOT_NODE not contain child");
		}
	}

	private void assertParent(final Map<SyncTypeAttributeDataTreeNode, List<SyncTypeAttributeDataTreeNode>> tree,
			final ComposedTypeModel parent, final ComposedTypeModel child)
	{
		final List<SyncTypeAttributeDataTreeNode> childs = tree.get(SyncTypeAttributeDataTreeNode.createTypeNode(parent));
		if (!childs.contains(SyncTypeAttributeDataTreeNode.createTypeNode(child)))
		{
			fail("parent not contain child");
		}
	}

	@Test
	public void shouldCompareByTypeAndName()
	{
		assertThat(COMPARE_NODES_BY_TYPE_AND_ATTRIBUTE_NAMES.compare(createMockNode("test", false), createMockNode("test", false)))
				.isEqualTo(0);
		assertThat(COMPARE_NODES_BY_TYPE_AND_ATTRIBUTE_NAMES.compare(createMockNode("test", true), createMockNode("test", false)))
				.isGreaterThan(0);
		assertThat(COMPARE_NODES_BY_TYPE_AND_ATTRIBUTE_NAMES.compare(createMockNode("B", true), createMockNode("A", true)))
				.isGreaterThan(0);
		assertThat(COMPARE_NODES_BY_TYPE_AND_ATTRIBUTE_NAMES.compare(createMockNode("B", false), createMockNode("A", true)))
				.isLessThan(0);
		assertThat(COMPARE_NODES_BY_TYPE_AND_ATTRIBUTE_NAMES.compare(createMockNode("test [test]", false),
				createMockNode("test longer [test]", false))).isLessThan(0);
	}

	@Test
	public void shouldSortMapValuesLists()
	{
		// given
		final SyncTypeAttributeDataTreeNode syncTypeAttributeDataTreeNode1 = createMockNode("1", true);
		final SyncTypeAttributeDataTreeNode syncTypeAttributeDataTreeNode2 = createMockNode("2", false);
		final SyncTypeAttributeDataTreeNode syncTypeAttributeDataTreeNode3 = createMockNode("3", false);

		final SyncTypeAttributeDataTreeNode syncTypeAttributeDataTreeNode4 = createMockNode("6", true);
		final SyncTypeAttributeDataTreeNode syncTypeAttributeDataTreeNode5 = createMockNode("5", true);
		final SyncTypeAttributeDataTreeNode syncTypeAttributeDataTreeNode6 = createMockNode("4", false);

		final SyncTypeAttributeDataTreeNode key1 = mock(SyncTypeAttributeDataTreeNode.class);
		final SyncTypeAttributeDataTreeNode key2 = mock(SyncTypeAttributeDataTreeNode.class);


		final Map<SyncTypeAttributeDataTreeNode, List<SyncTypeAttributeDataTreeNode>> map = new HashMap<>();
		map.put(key1,
				Lists.newArrayList(syncTypeAttributeDataTreeNode1, syncTypeAttributeDataTreeNode2, syncTypeAttributeDataTreeNode3));
		map.put(key2,
				Lists.newArrayList(syncTypeAttributeDataTreeNode4, syncTypeAttributeDataTreeNode5, syncTypeAttributeDataTreeNode6));

		// when
		syncAttributeTreeModelFactory.sortMapValuesLists(map);

		// then
		assertThat(map.get(key1)).containsExactly(syncTypeAttributeDataTreeNode2, syncTypeAttributeDataTreeNode3,
				syncTypeAttributeDataTreeNode1);
		assertThat(map.get(key2)).containsExactly(syncTypeAttributeDataTreeNode6, syncTypeAttributeDataTreeNode5,
				syncTypeAttributeDataTreeNode4);
	}

	private SyncTypeAttributeDataTreeNode createMockNode(final String text, final boolean type)
	{
		final SyncTypeAttributeDataTreeNode node = mock(SyncTypeAttributeDataTreeNode.class);
		doReturn(type).when(node).isType();
		doReturn(text).when(node).getText();
		return node;
	}

}
