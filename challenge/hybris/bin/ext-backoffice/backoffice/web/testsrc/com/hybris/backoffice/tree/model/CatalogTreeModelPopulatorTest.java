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
package com.hybris.backoffice.tree.model;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.catalog.CatalogService;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.zkoss.zul.TreeNode;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hybris.backoffice.navigation.NavigationNode;
import com.hybris.cockpitng.core.context.CockpitContext;
import com.hybris.cockpitng.core.context.impl.DefaultCockpitContext;
import com.hybris.cockpitng.core.user.CockpitUserService;
import com.hybris.cockpitng.dataaccess.facades.permissions.PermissionFacade;
import com.hybris.cockpitng.dataaccess.facades.type.TypeFacade;
import com.hybris.cockpitng.labels.LabelService;
import com.hybris.cockpitng.testing.AbstractCockpitngUnitTest;
import com.hybris.cockpitng.testing.annotation.ExtensibleWidget;
import com.hybris.cockpitng.tree.factory.NavigationTreeFactory;
import com.hybris.cockpitng.tree.node.DynamicNode;
import com.hybris.cockpitng.tree.node.TypeNode;
import com.hybris.cockpitng.widgets.common.explorertree.ExplorerTreeController;
import com.hybris.cockpitng.widgets.common.explorertree.data.PartitionNodeData;


@ExtensibleWidget(level = ExtensibleWidget.ALL)
@RunWith(MockitoJUnitRunner.class)
public class CatalogTreeModelPopulatorTest extends AbstractCockpitngUnitTest<CatalogTreeModelPopulator>
{
	public static final String TYPE_A = "TypeA";
	public static final String TYPE_B = "TypeB";
	public static final String TYPE_UNKNOWN = "UnknownType";
	public static final String CATALOG_ID_DEFAULT = "default";

	@Spy
	@InjectMocks
	private CatalogTreeModelPopulator populator;

	@Mock
	private TypeService typeService;
	@Mock
	private CockpitUserService cockpitUserService;
	@Mock
	private UserService userService;
	@Mock
	private CatalogService catalogService;
	@Mock
	private PermissionFacade permissionFacade;
	@Mock
	private TypeFacade typeFacade;
	@Mock
	private UserModel admin;
	@Mock
	private LabelService labelService;
	@Mock
	private CatalogVersionService catalogVersionService;

	private static final int PARTITION_THRESHOLD = 100;

	@Override
	protected Class<? extends CatalogTreeModelPopulator> getWidgetType()
	{
		return CatalogTreeModelPopulator.class;
	}

	@Before
	public void setUp()
	{
		when(typeService.getTypeForCode(eq(TYPE_UNKNOWN))).thenThrow(UnknownIdentifierException.class);
		when(userService.getCurrentUser()).thenReturn(admin);
		when(Boolean.valueOf(userService.isAdmin(admin))).thenReturn(Boolean.TRUE);
		when(Boolean.valueOf(permissionFacade.canReadInstance(any()))).thenReturn(Boolean.TRUE);
		when(Boolean.valueOf(permissionFacade.canReadType(any()))).thenReturn(Boolean.TRUE);
		doReturn(true).when(populator).isSupportedType(any());
		getPopulator().setExcludedTypes(null);
		getPopulator().setPartitionThreshold(PARTITION_THRESHOLD);
	}

	@Test
	public void testExcludeUnknownTypes()
	{
		// given
		getPopulator().setExcludedTypes(Sets.newHashSet(TYPE_A, TYPE_UNKNOWN, TYPE_B));
		getPopulator().postConstruct();

		// when
		final Set<String> types = getPopulator().getExcludedTypes();

		// then
		assertThat(types).containsOnly(TYPE_A, TYPE_B);
	}

	@Test
	public void testFilterCatalogs()
	{
		// given
		doCallRealMethod().when(populator).isSupportedType(any());
		final CatalogModel readable = mockCatalogModel(CATALOG_ID_DEFAULT);
		final CatalogModel notReadable = mock(CatalogModel.class);
		when(notReadable.getItemtype()).thenReturn(TYPE_B);

		getPopulator().setExcludedTypes(Sets.newHashSet(TYPE_A));

		when(Boolean.valueOf(typeService.isAssignableFrom(TYPE_A, TYPE_B))).thenReturn(Boolean.TRUE);
		when(catalogService.getAllCatalogs()).thenReturn(Arrays.asList(readable, notReadable));

		// when
		final Collection<CatalogModel> catalogs = getPopulator().getAllReadableCatalogs(new DefaultCockpitContext());

		// then
		assertThat(catalogs).containsOnly(readable);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetChildrenForUnsupportedParentNode()
	{
		getPopulator().getChildren(new TypeNode(null, null));
	}

	@Test
	public void testGetChildrenForRootWhenContextDoesNotContainAnyShowAttributes()
	{
		// given
		final CatalogModel catalog1 = mock(CatalogModel.class);
		final CatalogModel catalog2 = mock(CatalogModel.class);

		when(catalogService.getAllCatalogs()).thenReturn(Arrays.asList(catalog1, catalog2));

		final DynamicNode rootNode = new DynamicNode(null, null);
		rootNode.setContext(new DefaultCockpitContext());

		// when
		final List<NavigationNode> rootNodes = getPopulator().getChildren(rootNode);

		// then
		assertThat(rootNodes).hasSize(3);
		assertThat(rootNodes.get(0).getData()).isEqualTo("allCatalogs");
		assertThat(rootNodes.get(1).getData()).isSameAs(catalog1);
		assertThat(rootNodes.get(2).getData()).isSameAs(catalog2);
	}

	@Test
	public void testGetChildrenForRootWhenShowUncategorizedRootNodeIsTrue()
	{
		// given
		final CatalogModel catalog1 = mock(CatalogModel.class);
		final CatalogModel catalog2 = mock(CatalogModel.class);

		when(catalogService.getAllCatalogs()).thenReturn(Arrays.asList(catalog1, catalog2));

		final DynamicNode rootNode = new DynamicNode(null, null);
		final CockpitContext context = new DefaultCockpitContext();
		context.setParameter(CatalogTreeModelPopulator.SHOW_UNCATEGORIZED_ROOT_NODE, Boolean.TRUE);
		rootNode.setContext(context);

		// when
		final List<NavigationNode> rootNodes = getPopulator().getChildren(rootNode);

		// then
		assertThat(rootNodes).hasSize(4);
		assertThat(rootNodes.get(0).getData()).isEqualTo("allCatalogs");
		assertThat(rootNodes.get(1).getData()).isInstanceOf(UncategorizedNode.class);
		assertThat(rootNodes.get(2).getData()).isSameAs(catalog1);
		assertThat(rootNodes.get(3).getData()).isSameAs(catalog2);
	}

	@Test
	public void testGetChildrenForRootWhenShowUncategorizedRootNodeIsTrueAndShowAllCatalogsIsFalse()
	{
		// given
		final CatalogModel catalog1 = mock(CatalogModel.class);
		final CatalogModel catalog2 = mock(CatalogModel.class);

		when(catalogService.getAllCatalogs()).thenReturn(Arrays.asList(catalog1, catalog2));

		final DynamicNode rootNode = new DynamicNode(null, null);
		final CockpitContext context = new DefaultCockpitContext();
		context.setParameter(CatalogTreeModelPopulator.SHOW_ALL_CATALOGS_NODE, Boolean.FALSE);
		context.setParameter(CatalogTreeModelPopulator.SHOW_UNCATEGORIZED_ROOT_NODE, Boolean.TRUE);
		context.setParameter(ExplorerTreeController.DYNAMIC_NODE_SELECTION_CONTEXT, Arrays.asList(catalog1));
		rootNode.setContext(context);

		// when
		final List<NavigationNode> rootNodes = getPopulator().getChildren(rootNode);

		// then
		assertThat(rootNodes).hasSize(2);
		assertThat(rootNodes.get(0).getData()).isInstanceOf(UncategorizedNode.class);
		assertThat(rootNodes.get(1).getData()).isSameAs(catalog1);
	}

	@Test
	public void testGetChildrenForCatalogWhenShowUncategorizedNodeIsNotSet()
	{
		// given
		final CatalogModel catalog = mockCatalogModel(CATALOG_ID_DEFAULT);
		final CatalogVersionModel catalogVersion1 = mock(CatalogVersionModel.class);
		when(catalogVersion1.getVersion()).thenReturn("v1");
		final CatalogVersionModel catalogVersion2 = mock(CatalogVersionModel.class);
		when(catalogVersion2.getVersion()).thenReturn("v2");
		final CatalogVersionModel catalogVersion3 = mock(CatalogVersionModel.class);
		when(catalogVersion3.getVersion()).thenReturn("v3");
		when(catalog.getCatalogVersions()).thenReturn(Sets.newHashSet(catalogVersion1, catalogVersion2, catalogVersion3));

		final DynamicNode rootNode = new DynamicNode(null, getPopulator(), 1);
		final CockpitContext context = new DefaultCockpitContext();
		context.setParameter(CatalogTreeModelPopulator.SHOW_ALL_CATALOGS_NODE, Boolean.FALSE);
		context.setParameter(ExplorerTreeController.DYNAMIC_NODE_SELECTION_CONTEXT,
				Arrays.asList(catalogVersion1, catalogVersion2));
		rootNode.setContext(context);

		when(catalogService.getAllCatalogs()).thenReturn(Arrays.asList(catalog));
		when(catalogVersionService.getAllCatalogVersions())
				.thenReturn(Arrays.asList(catalogVersion1, catalogVersion2, catalogVersion3));

		// when
		final List<NavigationNode> catalogNodes = getPopulator().getChildren(rootNode);
		final List<NavigationNode> catalogVersionNodes = getPopulator().getChildren(catalogNodes.get(0));

		// then
		assertThat(catalogVersionNodes).hasSize(2);
		assertThat(catalogVersionNodes).onProperty("data").containsOnly(catalogVersion1, catalogVersion2);
	}

	@Test
	public void getChildrenForCatalogWhenShowUncategorizedNodeIsTrue()
	{
		// given
		final CatalogModel catalog = mockCatalogModel(CATALOG_ID_DEFAULT);
		final CatalogVersionModel catalogVersion1 = mockCatalogVersion();
		when(catalogVersion1.getVersion()).thenReturn("v1");
		final CatalogVersionModel catalogVersion2 = mockCatalogVersion();
		when(catalogVersion2.getVersion()).thenReturn("v2");
		final CatalogVersionModel catalogVersion3 = mockCatalogVersion();
		when(catalogVersion3.getVersion()).thenReturn("v3");
		when(catalog.getCatalogVersions()).thenReturn(Sets.newHashSet(catalogVersion1, catalogVersion2, catalogVersion3));

		final DynamicNode rootNode = new DynamicNode(null, getPopulator(), 1);
		final CockpitContext context = new DefaultCockpitContext();
		context.setParameter(CatalogTreeModelPopulator.SHOW_ALL_CATALOGS_NODE, Boolean.FALSE);
		context.setParameter(CatalogTreeModelPopulator.SHOW_UNCATEGORIZED_CATALOG_NODE, Boolean.TRUE);
		context.setParameter(ExplorerTreeController.DYNAMIC_NODE_SELECTION_CONTEXT,
				Arrays.asList(catalogVersion1, catalogVersion2));
		rootNode.setContext(context);

		when(catalogService.getAllCatalogs()).thenReturn(Arrays.asList(catalog));
		when(catalogVersionService.getAllCatalogVersions())
				.thenReturn(Arrays.asList(catalogVersion1, catalogVersion2, catalogVersion3));

		// when
		final List<NavigationNode> catalogNodes = getPopulator().getChildren(rootNode);
		final List<NavigationNode> catalogVersionNodes = getPopulator().getChildren(catalogNodes.get(0));

		// then
		assertThat(catalogVersionNodes).hasSize(3);
		assertThat(catalogVersionNodes.get(0).getData()).isInstanceOf(UncategorizedNode.class);
		assertThat(catalogVersionNodes.subList(1, catalogVersionNodes.size())).onProperty("data").containsOnly(catalogVersion1,
				catalogVersion2);
	}

	@Test
	public void testGetChildrenForCatalogVersionWhenShowUncategorizedNodeIsDefault()
	{
		// given
		final CatalogModel catalog = mockCatalogModel(CATALOG_ID_DEFAULT);
		final CatalogVersionModel catalogVersion = mockCatalogVersion();
		final CategoryModel category1 = mock(CategoryModel.class);
		final CategoryModel category2 = mock(CategoryModel.class);

		when(category1.getCatalogVersion()).thenReturn(catalogVersion);
		when(category2.getCatalogVersion()).thenReturn(catalogVersion);
		when(catalog.getCatalogVersions()).thenReturn(Sets.newHashSet(catalogVersion));
		when(catalogVersion.getRootCategories()).thenReturn(Arrays.asList(category1, category2));
		when(catalogService.getAllCatalogs()).thenReturn(Arrays.asList(catalog));
		when(catalogVersionService.getAllCatalogVersions()).thenReturn(Arrays.asList(catalogVersion));
		when(catalogVersionService.getAllReadableCatalogVersions(admin)).thenReturn(Arrays.asList(catalogVersion));
		when(permissionFacade.canReadType(any())).thenReturn(true);

		final DynamicNode rootNode = new DynamicNode(null, getPopulator(), 4);
		final CockpitContext context = new DefaultCockpitContext();
		context.setParameter(CatalogTreeModelPopulator.SHOW_ALL_CATALOGS_NODE, Boolean.FALSE);
		rootNode.setContext(context);

		// when
		final List<NavigationNode> catalogNodes = getPopulator().getChildren(rootNode);
		final List<NavigationNode> catalogVersionNodes = getPopulator().getChildren(catalogNodes.get(0));
		final List<NavigationNode> categoryNodes = getPopulator().getChildren(catalogVersionNodes.get(0));

		// then
		assertThat(categoryNodes).hasSize(3);
		assertThat(categoryNodes.get(0).getData()).isInstanceOf(UncategorizedNode.class);
		assertThat(categoryNodes.subList(1, categoryNodes.size())).onProperty("data").containsOnly(category1, category2);
	}

	@Test
	public void testGetChildrenForCatalogVersionWhenShowUncategorizedNodeIsFalse()
	{
		// given
		final CatalogModel catalog = mockCatalogModel(CATALOG_ID_DEFAULT);
		final CatalogVersionModel catalogVersion = mockCatalogVersion();
		final CategoryModel category1 = mock(CategoryModel.class);
		when(category1.getItemtype()).thenReturn(CategoryModel._TYPECODE);
		final CategoryModel category2 = mock(CategoryModel.class);
		when(category2.getItemtype()).thenReturn(CategoryModel._TYPECODE);

		when(category1.getCatalogVersion()).thenReturn(catalogVersion);
		when(category2.getCatalogVersion()).thenReturn(catalogVersion);
		when(catalog.getCatalogVersions()).thenReturn(Sets.newHashSet(catalogVersion));
		when(catalogVersion.getRootCategories()).thenReturn(Arrays.asList(category1, category2));
		when(catalogVersionService.getAllReadableCatalogVersions(admin)).thenReturn(Arrays.asList(catalogVersion));
		when(catalogService.getAllCatalogs()).thenReturn(Arrays.asList(catalog));
		when(catalogVersionService.getAllCatalogVersions()).thenReturn(Arrays.asList(catalogVersion));
		when(permissionFacade.canReadType(any())).thenReturn(true);

		final DynamicNode rootNode = new DynamicNode(null, getPopulator(), 4);
		final CockpitContext context = new DefaultCockpitContext();
		context.setParameter(CatalogTreeModelPopulator.SHOW_ALL_CATALOGS_NODE, Boolean.FALSE);
		context.setParameter(CatalogTreeModelPopulator.SHOW_UNCATEGORIZED_CATALOG_VERSION_NODE, Boolean.FALSE);
		rootNode.setContext(context);

		// when
		final List<NavigationNode> catalogNodes = getPopulator().getChildren(rootNode);
		final List<NavigationNode> catalogVersionNodes = getPopulator().getChildren(catalogNodes.get(0));
		final List<NavigationNode> categoryNodes = getPopulator().getChildren(catalogVersionNodes.get(0));

		// then
		assertThat(categoryNodes).hasSize(2);
		assertThat(categoryNodes).onProperty("data").containsOnly(category1, category2);
	}

	@Test
	public void testGetChildrenForSupportedCategoriesOnly()
	{
		// given
		final CatalogModel catalog = mockCatalogModel(CATALOG_ID_DEFAULT);
		final CatalogVersionModel catalogVersion = mockCatalogVersion();
		final CategoryModel category1 = mock(CategoryModel.class);
		final CategoryModel category2 = mock(CategoryModel.class);
		final CategoryModel subCategory1 = mock(CategoryModel.class);
		final CategoryModel subCategory2 = mock(CategoryModel.class);

		when(category1.getCatalogVersion()).thenReturn(catalogVersion);
		when(category2.getCatalogVersion()).thenReturn(catalogVersion);
		when(subCategory1.getCatalogVersion()).thenReturn(catalogVersion);
		when(subCategory2.getCatalogVersion()).thenReturn(catalogVersion);
		when(catalog.getCatalogVersions()).thenReturn(Sets.newHashSet(catalogVersion));
		when(catalogVersion.getRootCategories()).thenReturn(Arrays.asList(category1, category2));
		when(catalogService.getAllCatalogs()).thenReturn(Arrays.asList(catalog));
		when(catalogVersionService.getAllReadableCatalogVersions(admin)).thenReturn(Arrays.asList(catalogVersion));
		when(catalogVersionService.getAllCatalogVersions()).thenReturn(Arrays.asList(catalogVersion));
		when(category2.getCategories()).thenReturn(Arrays.asList(subCategory1, subCategory2));
		doCallRealMethod().when(populator).isSupportedType(any());

		final DynamicNode rootNode = new DynamicNode(null, getPopulator(), 4);
		getPopulator().setExcludedTypes(Sets.newHashSet(TYPE_A));
		doReturn(Boolean.TRUE).when(typeService).isAssignableFrom(TYPE_A, TYPE_A);
		when(category1.getItemtype()).thenReturn(TYPE_A);
		when(category2.getItemtype()).thenReturn(TYPE_B);
		when(subCategory1.getItemtype()).thenReturn(TYPE_A);
		when(subCategory2.getItemtype()).thenReturn(TYPE_B);
		when(permissionFacade.canReadType(any())).thenReturn(true);

		final CockpitContext context = new DefaultCockpitContext();
		context.setParameter(CatalogTreeModelPopulator.SHOW_ALL_CATALOGS_NODE, Boolean.FALSE);
		context.setParameter(CatalogTreeModelPopulator.SHOW_UNCATEGORIZED_CATALOG_VERSION_NODE, Boolean.FALSE);
		rootNode.setContext(context);

		// when
		final List<NavigationNode> catalogNodes = getPopulator().getChildren(rootNode);
		final List<NavigationNode> catalogVersionNodes = getPopulator().getChildren(catalogNodes.get(0));
		final List<NavigationNode> categoryNodes = getPopulator().getChildren(catalogVersionNodes.get(0));
		final List<NavigationNode> subCategoryNodes = getPopulator().getChildren(categoryNodes.get(0));

		// then
		assertThat(categoryNodes).hasSize(1);
		assertThat(categoryNodes).onProperty("data").contains(category2);
		assertThat(subCategoryNodes).hasSize(1);
		assertThat(subCategoryNodes).onProperty("data").contains(subCategory2);
	}

	@Test
	public void testCheckShortLabelIsUsedForCategories()
	{
		//given
		final CatalogModel catalog = mockCatalogModel(CATALOG_ID_DEFAULT);
		final CatalogVersionModel catalogVersion = mock(CatalogVersionModel.class);
		final CategoryModel category1 = mock(CategoryModel.class);
		final CategoryModel category2 = mock(CategoryModel.class);
		final CategoryModel subCategory1 = mock(CategoryModel.class);
		final CategoryModel subCategory2 = mock(CategoryModel.class);

		when(category1.getCatalogVersion()).thenReturn(catalogVersion);
		when(category2.getCatalogVersion()).thenReturn(catalogVersion);
		when(subCategory1.getCatalogVersion()).thenReturn(catalogVersion);
		when(subCategory2.getCatalogVersion()).thenReturn(catalogVersion);
		when(catalogVersionService.getAllReadableCatalogVersions(admin)).thenReturn(Arrays.asList(catalogVersion));
		when(catalog.getCatalogVersions()).thenReturn(Sets.newHashSet(catalogVersion));
		when(catalogVersion.getRootCategories()).thenReturn(Arrays.asList(category1, category2));
		when(catalogService.getAllCatalogs()).thenReturn(Arrays.asList(catalog));
		when(catalogVersionService.getSessionCatalogVersions()).thenReturn(Arrays.asList(catalogVersion));
		when(catalogVersionService.getAllCatalogVersions()).thenReturn(Arrays.asList(catalogVersion));
		when(permissionFacade.canReadType(any())).thenReturn(true);

		when(category2.getCategories()).thenReturn(Arrays.asList(subCategory1, subCategory2));

		final DynamicNode rootNode = new DynamicNode(null, getPopulator(), 4);
		final CockpitContext context = new DefaultCockpitContext();
		context.setParameter(CatalogTreeModelPopulator.SHOW_ALL_CATALOGS_NODE, Boolean.FALSE);
		context.setParameter(CatalogTreeModelPopulator.SHOW_UNCATEGORIZED_CATALOG_VERSION_NODE, Boolean.FALSE);
		rootNode.setContext(context);

		getPopulator().setExcludedTypes(Sets.newHashSet(TYPE_A));
		when(Boolean.valueOf(typeService.isAssignableFrom(TYPE_A, TYPE_A))).thenReturn(Boolean.TRUE);
		when(typeFacade.getType(category1)).thenReturn(TYPE_A);
		when(typeFacade.getType(subCategory1)).thenReturn(TYPE_A);

		//when
		final List<NavigationNode> catalogNodes = getPopulator().getChildren(rootNode);
		final List<NavigationNode> catalogVersionNodes = getPopulator().getChildren(catalogNodes.get(0));

		//then
		verify(labelService, times(2)).getObjectLabel(any(Object.class));
		verify(labelService, never()).getShortObjectLabel(any(Object.class));
		reset(labelService);

		//when
		final List<NavigationNode> categoryNodes = getPopulator().getChildren(catalogVersionNodes.get(0));
		getPopulator().getChildren(categoryNodes.get(0));

		//then
		verify(labelService, never()).getObjectLabel(any(Object.class));
		verify(labelService, times(2)).getShortObjectLabel(any(Object.class));
	}

	@Test
	public void testGetChildrenForCatalogVersionShouldReturnEmptyCollectionWhenNoReadAccess()
	{
		// given
		final CatalogModel catalog = mockCatalogModel(CATALOG_ID_DEFAULT);
		final CatalogVersionModel catalogVersion = mockCatalogVersion();
		final CategoryModel category = mock(CategoryModel.class);

		when(catalog.getCatalogVersions()).thenReturn(Sets.newHashSet(catalogVersion));
		when(catalogVersion.getRootCategories()).thenReturn(Arrays.asList(category));
		when(catalogService.getAllCatalogs()).thenReturn(Lists.newArrayList(catalog));
		when(catalogVersionService.getAllCatalogVersions()).thenReturn(Arrays.asList(catalogVersion));

		final DynamicNode rootNode = new DynamicNode(null, getPopulator(), 4);
		final CockpitContext context = new DefaultCockpitContext();
		context.setParameter(CatalogTreeModelPopulator.SHOW_ALL_CATALOGS_NODE, Boolean.FALSE);
		context.setParameter(CatalogTreeModelPopulator.SHOW_UNCATEGORIZED_CATALOG_VERSION_NODE, Boolean.FALSE);
		rootNode.setContext(context);

		// when
		final List<NavigationNode> catalogNodes = getPopulator().getChildren(rootNode);
		final List<NavigationNode> catalogVersionNodes = getPopulator().getChildren(catalogNodes.get(0));
		when(Boolean.valueOf(permissionFacade.canReadInstance(any()))).thenReturn(Boolean.FALSE);
		final List<NavigationNode> categoryNodes = getPopulator().getChildren(catalogVersionNodes.get(0));

		// then
		assertThat(categoryNodes).isEmpty();
	}

	protected CatalogVersionModel mockCatalogVersion()
	{
		final CatalogVersionModel catalogVersion = mock(CatalogVersionModel.class);
		when(catalogVersion.getItemtype()).thenReturn(CatalogVersionModel._TYPECODE);
		return catalogVersion;
	}

	protected CatalogModel mockCatalogModel(final String catalogId)
	{
		final CatalogModel catalog = mock(CatalogModel.class);
		when(catalog.getId()).thenReturn(catalogId);
		when(catalog.getItemtype()).thenReturn(CatalogModel._TYPECODE);
		return catalog;
	}

	@Test
	public void testCreateDynamicNodeId()
	{
		// given
		final NavigationNode node = mock(NavigationNode.class);
		when(node.getId()).thenReturn("id");

		final NavigationNode parent3 = mock(NavigationNode.class);
		when(parent3.getId()).thenReturn("par3");
		when(node.getParent()).thenReturn(parent3);

		final NavigationNode parent2 = mock(NavigationNode.class);
		when(parent3.getParent()).thenReturn(parent2);

		final NavigationNode parent1 = mock(NavigationNode.class);
		when(parent1.getId()).thenReturn("par1");
		when(parent2.getParent()).thenReturn(parent1);

		// when
		final String id = getPopulator().createDynamicNodeId(node, "suffix");

		// then
		assertThat(id).isEqualTo("par1_par3_id_suffix");
	}

	@Test
	public void testGeneratedNodeIdWhenParentContainsRootPrefix()
	{
		// given
		final String idSuffix = "mycatalogVersion";
		final String parentId = "root_mycatalog";
		final String expectedId = String.format("%s_%s", parentId, idSuffix);
		final NavigationNode parent = Mockito.mock(NavigationNode.class);
		when(parent.getId()).thenReturn(parentId);

		// when
		final String generatedId = getPopulator().createDynamicNodeId(parent, idSuffix);

		// then
		assertThat(generatedId).isEqualTo(expectedId);
	}

	@Test
	public void testGeneratedNodeIdWhenParentDoesNotContainRootPrefix()
	{
		// given
		final String idSuffix = "mycatalogversion";
		final String parentId = "mycatalog";
		final String expectedId = String.format("%s_%s_%s", NavigationTreeFactory.ROOT_NODE_ID, parentId, idSuffix);
		final NavigationNode parent = mock(NavigationNode.class);
		final NavigationNode root = mock(NavigationNode.class);
		when(parent.getParent()).thenReturn(root);
		when(parent.getId()).thenReturn(parentId);
		when(root.getId()).thenReturn(NavigationTreeFactory.ROOT_NODE_ID);

		// when
		final String generatedId = populator.createDynamicNodeId(parent, idSuffix);

		// then
		assertThat(generatedId).isEqualTo(expectedId);
	}

	@Test
	public void shouldPartitionChildrenNodesWhenThresholdIsExceeded()
	{
		// given
		final NavigationNode parent = mock(DynamicNode.class);
		final List<NavigationNode> children = new ArrayList<>();
		for (int i = 1; i <= 13; i++)
		{
			final NavigationNode child = mock(DynamicNode.class);
			when(child.getId()).thenReturn("id_" + i);
			children.add(child);
		}
		doReturn(children).when(populator).findChildrenNavigationNodes(parent);

		// when
		populator.setPartitionThreshold(5);
		final List<NavigationNode> partitions = populator.getChildren(parent);

		// then
		final NavigationNode firstPartition = partitions.get(0);
		final NavigationNode secondPartition = partitions.get(1);
		final NavigationNode thirdPartition = partitions.get(2);

		assertThat(firstPartition.getId()).isEqualTo("1 ... 5");
		assertThat(secondPartition.getId()).isEqualTo("6 ... 10");
		assertThat(thirdPartition.getId()).isEqualTo("11 ... 13");
		assertThat(firstPartition.getData()).isInstanceOf(PartitionNodeData.class);
		assertThat(secondPartition.getData()).isInstanceOf(PartitionNodeData.class);
		assertThat(thirdPartition.getData()).isInstanceOf(PartitionNodeData.class);
		assertThat(((PartitionNodeData) (firstPartition.getData())).getChildren()).onProperty("id").containsExactly("id_1", "id_2",
				"id_3", "id_4", "id_5");
		assertThat(((PartitionNodeData) (secondPartition.getData())).getChildren()).onProperty("id").containsExactly("id_6", "id_7",
				"id_8", "id_9", "id_10");
		assertThat(((PartitionNodeData) (thirdPartition.getData())).getChildren()).onProperty("id").containsExactly("id_11",
				"id_12", "id_13");
	}

	@Test
	public void shouldCalculatePathForRootNode()
	{
		// given
		final TreeNode<ItemModel> rootNode = mock(TreeNode.class);
		final CatalogTreeModelPopulator.CatalogTreeModel catalogTreeModel = populator.new CatalogTreeModel(rootNode);

		// when
		final int[] path = catalogTreeModel.getPath(rootNode);

		// then
		assertThat(path).isEmpty();
	}

	@Test
	public void shouldCalculatePathForFirstLevelNode()
	{
		// given
		final TreeNode<ItemModel> rootNode = mock(TreeNode.class);
		final TreeNode<ItemModel> firstLevelNode = mock(TreeNode.class);
		when(firstLevelNode.getParent()).thenReturn(rootNode);
		when(rootNode.getChildren())
				.thenReturn(Arrays.asList(mock(TreeNode.class), mock(TreeNode.class), firstLevelNode, mock(TreeNode.class)));
		final CatalogTreeModelPopulator.CatalogTreeModel catalogTreeModel = populator.new CatalogTreeModel(rootNode);

		// when
		final int[] path = catalogTreeModel.getPath(firstLevelNode);

		// then
		assertThat(path).isEqualTo(new int[]
		{ 2 });
	}

	@Test
	public void shouldCalculatePathForNLevelNode()
	{
		// given
		final TreeNode<ItemModel> rootNode = mock(TreeNode.class);
		final TreeNode<ItemModel> firstLevelNode = mock(TreeNode.class);
		final TreeNode<ItemModel> secondLevelNode = mock(TreeNode.class);
		final TreeNode<ItemModel> thirdLevelNode = mock(TreeNode.class);
		when(firstLevelNode.getParent()).thenReturn(rootNode);
		when(secondLevelNode.getParent()).thenReturn(firstLevelNode);
		when(thirdLevelNode.getParent()).thenReturn(secondLevelNode);

		when(rootNode.getChildren())
				.thenReturn(Arrays.asList(mock(TreeNode.class), firstLevelNode, mock(TreeNode.class), mock(TreeNode.class)));
		when(firstLevelNode.getChildren()).thenReturn(Arrays.asList(mock(TreeNode.class), mock(TreeNode.class), secondLevelNode));
		when(secondLevelNode.getChildren()).thenReturn(Arrays.asList(thirdLevelNode));

		final CatalogTreeModelPopulator.CatalogTreeModel catalogTreeModel = populator.new CatalogTreeModel(rootNode);

		// when
		final int[] path = catalogTreeModel.getPath(thirdLevelNode);

		// then
		assertThat(path).isEqualTo(new int[]
		{ 1, 2, 0 });
	}

	@Test
	public void shouldNotReturnCatalogVersionWhenUserDoesNotHavePermission()
	{
		// given
		final DynamicNode rootNode = new DynamicNode(null, getPopulator(), 4);
		final CockpitContext context = new DefaultCockpitContext();
		context.setParameter(CatalogTreeModelPopulator.SHOW_ALL_CATALOGS_NODE, Boolean.FALSE);
		context.setParameter(CatalogTreeModelPopulator.SHOW_UNCATEGORIZED_CATALOG_VERSION_NODE, Boolean.FALSE);
		rootNode.setContext(context);
		final CatalogModel rootData = mockCatalogModel(CATALOG_ID_DEFAULT);
		rootNode.setData(rootData);
		final CatalogVersionModel firstCatalogVersion = new CatalogVersionModel();
		firstCatalogVersion.setVersion("First version");
		final CatalogVersionModel secondCatalogVersion = new CatalogVersionModel();
		secondCatalogVersion.setVersion("Second version");
		final Set<CatalogVersionModel> catalogVersions = new HashSet<>();
		catalogVersions.add(firstCatalogVersion);
		catalogVersions.add(secondCatalogVersion);
		when(rootData.getCatalogVersions()).thenReturn(catalogVersions);
		when(catalogVersionService.getAllCatalogVersions()).thenReturn(Arrays.asList(firstCatalogVersion));
		when(catalogVersionService.getAllReadableCatalogVersions(admin)).thenReturn(Arrays.asList(firstCatalogVersion));
		when(userService.isAdmin(admin)).thenReturn(Boolean.FALSE);

		// when
		final List<NavigationNode> foundCatalogVersions = populator.findChildrenNavigationNodes(rootNode);

		// then
		assertThat(foundCatalogVersions).hasSize(1);
		assertThat(foundCatalogVersions.get(0).getData()).isSameAs(firstCatalogVersion);
	}

	@Test
	public void shouldNotReturnCategoriesWhenUserDoesNotHavePermissionToType()
	{
		// given
		final DynamicNode rootNode = new DynamicNode(null, getPopulator(), 4);
		final CockpitContext context = new DefaultCockpitContext();
		context.setParameter(CatalogTreeModelPopulator.SHOW_ALL_CATALOGS_NODE, Boolean.FALSE);
		context.setParameter(CatalogTreeModelPopulator.SHOW_UNCATEGORIZED_CATALOG_VERSION_NODE, Boolean.FALSE);
		rootNode.setContext(context);
		final CatalogVersionModel rootData = mock(CatalogVersionModel.class);
		rootNode.setData(rootData);
		final CategoryModel firstCategory = mock(CategoryModel.class);
		final CategoryModel secondCategory = mock(CategoryModel.class);
		when(firstCategory.getItemtype()).thenReturn(CategoryModel._TYPECODE);
		when(secondCategory.getItemtype()).thenReturn(CategoryModel._TYPECODE);
		when(rootData.getRootCategories()).thenReturn(Arrays.asList(firstCategory, secondCategory));
		when(permissionFacade.canReadType(CategoryModel._TYPECODE)).thenReturn(false);

		// when
		final List<NavigationNode> categories = populator.findChildrenNavigationNodes(rootNode);

		// then
		assertThat(categories).isEmpty();
	}

	public CatalogTreeModelPopulator getPopulator()
	{
		return populator;
	}
}
