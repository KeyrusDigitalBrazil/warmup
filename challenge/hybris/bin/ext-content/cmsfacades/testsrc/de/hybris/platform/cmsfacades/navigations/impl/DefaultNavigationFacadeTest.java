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
package de.hybris.platform.cmsfacades.navigations.impl;

import static de.hybris.platform.cms2.constants.Cms2Constants.ROOT;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.navigation.CMSNavigationNodeModel;
import de.hybris.platform.cms2.servicelayer.services.CMSNavigationService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminSiteService;
import de.hybris.platform.cmsfacades.common.validator.FacadeValidationService;
import de.hybris.platform.cmsfacades.data.NavigationNodeData;
import de.hybris.platform.cmsfacades.exception.ValidationException;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.validation.Errors;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultNavigationFacadeTest
{

	@Mock
	private CMSNavigationService navigationService;

	@Mock
	private AbstractPopulatingConverter<CMSNavigationNodeModel, NavigationNodeData> navigationModelToDataConverter;

	@Mock
	private FacadeValidationService facadeValidationService;

	@Mock
	private CMSAdminSiteService adminSiteService;

	@Mock
	private Populator<NavigationNodeData, CMSNavigationNodeModel> navigationNodeDataToModelCreatePopulator;

	@Mock
	private ModelService modelService;

	@Mock
	private CMSNavigationNodeModel node1;

	@Mock
	private CMSNavigationNodeModel child1;

	@Mock
	private CMSNavigationNodeModel root;

	@Mock
	private PlatformTransactionManager transactionManager;

	@InjectMocks
	private DefaultNavigationFacade defaultNavigationFacade;

	private static final String ROOT_NODE_1 = "root1";
	private static final String ROOT_NODE_2 = "root2";

	private static final String NODE_1 = "node1";
	private static final String NODE_2 = "node2";
	private static final String NODE_3 = "node3";

	private static final String CHILD_1 = "child1";

	private static final String INVALID_NODE = "invalid";

	private List<CMSNavigationNodeModel> allNavigationNodes;

	@Before
	public void setup() throws CMSItemNotFoundException
	{

		allNavigationNodes = createMockedRootNavigationNodes();

		when(navigationService.getRootNavigationNodes()).thenReturn(allNavigationNodes);
		when(navigationService.getNavigationNodeForId(ROOT_NODE_1)).thenReturn(allNavigationNodes.get(0));
		when(navigationService.getNavigationNodeForId(ROOT_NODE_2)).thenReturn(allNavigationNodes.get(1));
		when(navigationService.getNavigationNodeForId(null)).thenThrow(new CMSItemNotFoundException("Item not found"));
		when(navigationService.getNavigationNodeForId(INVALID_NODE))
		.thenThrow(new CMSItemNotFoundException("No NavigationNode with id INVALID_NODE found."));

		node1 = allNavigationNodes.get(0).getChildren().get(0);
		when(navigationService.getNavigationNodeForId(NODE_1)).thenReturn(node1);

		child1 = allNavigationNodes.get(0).getChildren().get(0).getChildren().get(0);
		when(navigationService.getNavigationNodeForId(CHILD_1)).thenReturn(child1);

	}

	@Test
	public void testFindAllNavigationNodes_will_return_list_of_all_navigation_nodes()
	{
		final List<NavigationNodeData> navigationNodes = defaultNavigationFacade.findAllNavigationNodes();
		assertThat(navigationNodes.size(), is(6));
	}

	@Test
	public void testFindAllNavigationNodes_given_parent_will_return_the_root_nodes_if_parent_is_root()
			throws CMSItemNotFoundException
	{
		final List<NavigationNodeData> navigationNodes = defaultNavigationFacade.findAllNavigationNodes(ROOT);
		assertThat(navigationNodes.size(), is(2));

		verify(navigationService).getRootNavigationNodes();
	}

	@Test
	public void testFindAllNavigationNodes_given_parent_will_return_all_its_children_if_parent_is_not_root()
			throws CMSItemNotFoundException
	{
		final List<NavigationNodeData> navigationNodes = defaultNavigationFacade.findAllNavigationNodes(ROOT_NODE_1);
		assertThat(navigationNodes.size(), is(2));

		verify(navigationService).getNavigationNodeForId(ROOT_NODE_1);
	}

	@Test
	public void testFindAllNavigationNodes_given_parent_will_return_empty_if_parent_is_null() throws CMSItemNotFoundException
	{
		final List<NavigationNodeData> navigationNodes = defaultNavigationFacade.findAllNavigationNodes(null);
		assertThat(navigationNodes.size(), is(0));

		verify(navigationService).getNavigationNodeForId(null);
	}

	@Test
	public void testFindAllNavigationNodes_given_parent_will_return_empty_if_parent_is_not_valid() throws CMSItemNotFoundException
	{
		final List<NavigationNodeData> navigationNodes = defaultNavigationFacade.findAllNavigationNodes(INVALID_NODE);
		assertThat(navigationNodes.size(), is(0));

		verify(navigationService).getNavigationNodeForId(INVALID_NODE);
	}

	@Test
	public void testAddNavigationNode_success() throws CMSItemNotFoundException
	{

		when(navigationService.createNavigationNode(allNavigationNodes.get(0), NODE_1, true, Collections.emptyList()))
		.thenReturn(node1);
		defaultNavigationFacade.addNavigationNode(createNavigationNode(NODE_1, ROOT_NODE_1));

		verify(navigationModelToDataConverter).convert(node1);

	}

	@Test(expected = ValidationException.class)
	public void testAddNavigationNode_throw_validationexception_when_nodeis_is_null() throws CMSItemNotFoundException
	{

		doThrow(new ValidationException((Errors) null)).when(facadeValidationService).validate(any(), any());
		when(navigationService.createNavigationNode(allNavigationNodes.get(0), NODE_1, true, Collections.emptyList()))
		.thenReturn(node1);
		defaultNavigationFacade.addNavigationNode(createNavigationNode(null, ROOT_NODE_1));

		verify(navigationModelToDataConverter).convert(node1);

	}

	@Test
	public void testGetNavigationAncestorsAndSelf_will_return_its_ancestors_will_return_ancestry_if_not_root()
			throws CMSItemNotFoundException
	{

		final List<NavigationNodeData> navigationNodesAncestry = defaultNavigationFacade.getNavigationAncestorsAndSelf(CHILD_1);
		assertThat(navigationNodesAncestry.size(), is(3));
	}

	@Test
	public void testGetNavigationAncestorsAndSelf_will_return_its_ancestors_empty_if_root() throws CMSItemNotFoundException
	{

		final List<NavigationNodeData> navigationNodesAncestry = defaultNavigationFacade
				.getNavigationAncestorsAndSelf(ROOT);
		assertThat(navigationNodesAncestry.size(), is(0));
	}

	@Test(expected = CMSItemNotFoundException.class)
	public void testGetNavigationAncestorsAndSelf_will_return_its_ancestors_empty_if_invalid() throws CMSItemNotFoundException
	{

		defaultNavigationFacade.getNavigationAncestorsAndSelf(INVALID_NODE);
	}


	@Test
	public void testRecursivelyPopulateNavigationNodeAncestryList_success()
	{
		final List<CMSNavigationNodeModel> navigationNodes = new ArrayList<>();
		defaultNavigationFacade.populateParentNavigationNode(navigationNodes, child1);

		assertThat(navigationNodes.size(), is(3));
	}

	@Test
	public void testRecursivelyPopulateNavigationNodeAncestryList_returns_empty_for_invalid_uid()
	{
		final List<CMSNavigationNodeModel> navigationNodes = new ArrayList<>();
		defaultNavigationFacade.populateParentNavigationNode(navigationNodes, null);

		assertThat(navigationNodes.size(), is(0));
	}

	@Test
	public void testRecursivelyPopulateNavigationNodeAncestryList_returns_empty_for_root_uid()
	{
		when(root.getUid()).thenReturn(ROOT);

		final List<CMSNavigationNodeModel> navigationNodes = new ArrayList<>();
		defaultNavigationFacade.populateParentNavigationNode(navigationNodes, root);

		assertThat(navigationNodes.size(), is(0));
	}

	protected CMSNavigationNodeModel createMockedNode(final String nodeUid, final CMSNavigationNodeModel parent)
	{
		final CMSNavigationNodeModel node = mock(CMSNavigationNodeModel.class);
		when(node.getUid()).thenReturn(nodeUid);
		when(node.getParent()).thenReturn(parent);
		return node;
	}

	protected List<CMSNavigationNodeModel> createMockedRootNavigationNodes()
	{
		final List<CMSNavigationNodeModel> rootNavigationNodes = new ArrayList<>();
		final CMSNavigationNodeModel root1 = createMockedNode(ROOT_NODE_1, null);
		final CMSNavigationNodeModel root2 = createMockedNode(ROOT_NODE_2, null);

		final List<CMSNavigationNodeModel> root1Children = new ArrayList<>();
		final CMSNavigationNodeModel root1Child1 = createMockedNode(NODE_1, root1);
		final CMSNavigationNodeModel root1Child2 = createMockedNode(NODE_2, root1);
		root1Children.add(root1Child1);
		root1Children.add(root1Child2);
		when(root1.getChildren()).thenReturn(root1Children);

		final List<CMSNavigationNodeModel> root2Children = new ArrayList<>();
		final CMSNavigationNodeModel root2Child1 = createMockedNode(NODE_3, root1);
		root2Children.add(root2Child1);
		when(root2.getChildren()).thenReturn(root2Children);

		final List<CMSNavigationNodeModel> node1Children = new ArrayList<>();
		final CMSNavigationNodeModel node1Child1 = createMockedNode(CHILD_1, root1Child1);
		node1Children.add(node1Child1);
		when(root1Child1.getChildren()).thenReturn(node1Children);

		rootNavigationNodes.add(root1);
		rootNavigationNodes.add(root2);

		return rootNavigationNodes;
	}

	protected NavigationNodeData createNavigationNode(final String nodeUid, final String parentUid)
	{
		final NavigationNodeData navigationNode = new NavigationNodeData();
		navigationNode.setUid(nodeUid);
		navigationNode.setParentUid(parentUid);

		return navigationNode;
	}
}
