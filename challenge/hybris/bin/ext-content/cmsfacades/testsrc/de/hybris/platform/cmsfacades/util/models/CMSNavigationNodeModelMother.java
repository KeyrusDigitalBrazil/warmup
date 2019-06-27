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
package de.hybris.platform.cmsfacades.util.models;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.components.CMSLinkComponentModel;
import de.hybris.platform.cms2.model.navigation.CMSNavigationEntryModel;
import de.hybris.platform.cms2.model.navigation.CMSNavigationNodeModel;
import de.hybris.platform.cms2.servicelayer.daos.CMSNavigationDao;
import de.hybris.platform.cms2.servicelayer.services.CMSNavigationService;
import de.hybris.platform.cmsfacades.util.builder.NavigationNodeModelBuilder;

import java.util.Arrays;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Required;


public class CMSNavigationNodeModelMother extends AbstractModelMother<CMSNavigationNodeModel>
{
	public static final String NAVIGATION_NODE_ROOT_UID = "root-node-uid";
	public static final String NAVIGATION_NODE_ROOT_NAME = "root-node-name";
	public static final String NAVIGATION_NODE_ROOT_TITLE = "Root Navigation Node";
	public static final String NAVIGATION_NODE_CHILD_UID = "child-node-uid";
	public static final String NAVIGATION_NODE_CHILD_NAME = "child-node-name";
	public static final String NAVIGATION_NODE_CHILD_TITLE = "Child Navigation Node";

	public static final String NAVIGATION_ENTRY_LINK_UID = "login-link";
	public static final String NAVIGATION_ENTRY_LINK_NAME = "User Login Link";

	private CMSNavigationDao navigationDao;
	private CMSNavigationService navigationService;
	private LinkComponentModelMother linkComponentModelMother;

	//	  Navigation node tree structure:
	//		root
	//		|_ rootNode
	//			|_ childNode
	//				|_ entry: login-link
	public CMSNavigationNodeModel createNavigationNodeTree(final CatalogVersionModel catalogVersion)
	{
		final CMSNavigationNodeModel root = createNavigationRootNode(catalogVersion);
		final CMSLinkComponentModel linkComponent = getLinkComponentModelMother().createExternalLinkComponentModel(catalogVersion);

		final CMSNavigationEntryModel entryModel = new CMSNavigationEntryModel();
		entryModel.setItem(linkComponent);
		entryModel.setName(NAVIGATION_ENTRY_LINK_NAME);
		entryModel.setUid(NAVIGATION_ENTRY_LINK_UID);
		entryModel.setCatalogVersion(catalogVersion);
		getModelService().save(entryModel);

		final CMSNavigationNodeModel rootNodeModel = createNavigationNode(NAVIGATION_NODE_ROOT_NAME, NAVIGATION_NODE_ROOT_UID, root,
				NAVIGATION_NODE_ROOT_TITLE, catalogVersion);
		final CMSNavigationNodeModel childNodeModel = createNavigationNodeWithEntry(NAVIGATION_NODE_CHILD_NAME,
				NAVIGATION_NODE_CHILD_UID, rootNodeModel, NAVIGATION_NODE_CHILD_TITLE, catalogVersion, entryModel);

		rootNodeModel.setChildren(Arrays.asList(childNodeModel));
		getModelService().save(rootNodeModel);

		return rootNodeModel;
	}

	public CMSNavigationNodeModel createNavigationNode(final String name, final String uid, final CMSNavigationNodeModel parent,
			final String englishTitle, final CatalogVersionModel catalogVersion)
	{
		return getOrSaveAndReturn(
				() -> getNavigationDao().findNavigationNodesById(uid, Arrays.asList(catalogVersion)).stream().findFirst()
						.orElse(null),
				() -> NavigationNodeModelBuilder.aModel().withUid(uid).withName(name).withTitle(englishTitle, Locale.ENGLISH)
						.withCatalogVersion(catalogVersion).withParent(parent).build());
	}

	public CMSNavigationNodeModel createNavigationNodeWithEntry(final String name, final String uid,
			final CMSNavigationNodeModel parent, final String englishTitle, final CatalogVersionModel catalogVersion,
			final CMSNavigationEntryModel entryModel)
	{
		return getOrSaveAndReturn(
				() -> getNavigationDao().findNavigationNodesById(uid, Arrays.asList(catalogVersion)).stream().findFirst()
						.orElse(null),
				() -> NavigationNodeModelBuilder.aModel().withUid(uid).withName(name).withTitle(englishTitle, Locale.ENGLISH)
						.withCatalogVersion(catalogVersion).withParent(parent).withEntry(entryModel).build());
	}

	public CMSNavigationDao getNavigationDao()
	{
		return navigationDao;
	}

	@Required
	public void setNavigationDao(final CMSNavigationDao navigationDao)
	{
		this.navigationDao = navigationDao;
	}

	public CMSNavigationNodeModel createNavigationRootNode(final CatalogVersionModel catalogVersion)
	{
		return saveModel(() -> navigationService.createSuperRootNavigationNode(catalogVersion));
	}

	public CMSNavigationService getNavigationService()
	{
		return navigationService;
	}

	@Required
	public void setNavigationService(final CMSNavigationService navigationService)
	{
		this.navigationService = navigationService;
	}

	protected LinkComponentModelMother getLinkComponentModelMother()
	{
		return linkComponentModelMother;
	}

	@Required
	public void setLinkComponentModelMother(final LinkComponentModelMother linkComponentModelMother)
	{
		this.linkComponentModelMother = linkComponentModelMother;
	}

}
