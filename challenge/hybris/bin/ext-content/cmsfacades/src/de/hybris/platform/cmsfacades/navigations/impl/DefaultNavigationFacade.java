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

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.data.PageableData;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.navigation.CMSNavigationNodeModel;
import de.hybris.platform.cms2.servicelayer.services.CMSNavigationService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminSiteService;
import de.hybris.platform.cmsfacades.common.validator.FacadeValidationService;
import de.hybris.platform.cmsfacades.data.CMSItemSearchData;
import de.hybris.platform.cmsfacades.data.NavigationNodeData;
import de.hybris.platform.cmsfacades.navigations.NavigationFacade;
import de.hybris.platform.cmsfacades.navigations.service.NavigationEntryService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.validation.Validator;

import com.google.common.collect.Lists;


/**
 * Default implementation of the {@link NavigationFacade}
 */
public class DefaultNavigationFacade implements NavigationFacade
{

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultNavigationFacade.class);

	private CMSNavigationService navigationService;
	private ModelService modelService;
	private CMSAdminSiteService adminSiteService;
	private AbstractPopulatingConverter<CMSNavigationNodeModel, NavigationNodeData> navigationModelToDataConverter;
	private Populator<NavigationNodeData, CMSNavigationNodeModel> navigationNodeDataToModelUpdatePopulator;
	private Populator<NavigationNodeData, CMSNavigationNodeModel> navigationNodeDataToModelCreatePopulator;
	private FacadeValidationService facadeValidationService;
	private Validator createNavigationNodeValidator;
	private Validator updateNavigationNodeValidator;
	private PlatformTransactionManager transactionManager;
	private NavigationEntryService navigationEntryService;
	private Validator navigationNodeEntriesValidator;

	@Override
	public NavigationNodeData findNavigationNodeById(final String uid) throws CMSItemNotFoundException
	{
		final CMSNavigationNodeModel navigationNodeModel = getNavigationService().getNavigationNodeForId(uid);
		return getNavigationModelToDataConverter().convert(navigationNodeModel);
	}

	/**
	 * @deprecated since 1811, please use {@link de.hybris.platform.cmsfacades.cmsitems.CMSItemFacade#findCMSItems(CMSItemSearchData, PageableData)} instead.
	 */
	@Override
	@Deprecated
	public List<NavigationNodeData> findAllNavigationNodes()
	{
		return getAllNavigationNodes();
	}

	@Override
	public List<NavigationNodeData> findAllNavigationNodes(final String parentUid)
	{
		if (ROOT.equalsIgnoreCase(parentUid))
		{
			return getRootNavigationNodes();
		}
		final CMSNavigationNodeModel navigationNodeModel;
		try
		{
			navigationNodeModel = getNavigationService().getNavigationNodeForId(parentUid);
		}
		catch (final CMSItemNotFoundException e)
		{
			return Collections.emptyList();
		}
		return navigationNodeModel.getChildren().stream().map(node -> getNavigationModelToDataConverter().convert(node))
				.collect(Collectors.toList());
	}

	/**
	 * Get all Navigation nodes.
	 *
	 * @return a list of all the navigation nodes data.
	 * @deprecated since 1811, please use {@link de.hybris.platform.cmsfacades.cmsitems.CMSItemFacade#findCMSItems(CMSItemSearchData, PageableData)} instead.
	 */
	@Deprecated
	protected List<NavigationNodeData> getAllNavigationNodes()
	{
		final List<CMSNavigationNodeModel> nodeModels = Lists.newArrayList();
		addAllNavigationNodes(nodeModels, getNavigationService().getRootNavigationNodes());
		return nodeModels.stream()
				.map(cmsNavigationNodeModel -> getNavigationModelToDataConverter().convert(cmsNavigationNodeModel))
				.collect(Collectors.toList());
	}

	/**
	 * Recursively adds navigation nodes to the reference list
	 *
	 * @param nodeModels
	 * 		the reference list where all the navigation nodes will be added to.
	 * @param navigationNodes
	 * 		the navigation nodes starting point
	 * @deprecated since 1811, please use {@link de.hybris.platform.cmsfacades.cmsitems.CMSItemFacade#createItem(Map)} instead.
	 */
	@Deprecated
	protected void addAllNavigationNodes(final List<CMSNavigationNodeModel> nodeModels,
			final List<CMSNavigationNodeModel> navigationNodes)
	{
		if (CollectionUtils.isEmpty(navigationNodes))
		{
			return;
		}
		nodeModels.addAll(navigationNodes);
		navigationNodes.stream()
				.forEach(cmsNavigationNodeModel -> addAllNavigationNodes(nodeModels, cmsNavigationNodeModel.getChildren()));
	}

	/**
	 * Get the CatalogVersion's navigation nodes.
	 *
	 * @return the root navigation nodes for the current catalog version.
	 */
	protected List<NavigationNodeData> getRootNavigationNodes()
	{
		final List<CMSNavigationNodeModel> rootNavigationNodes = getNavigationService().getRootNavigationNodes();
		return rootNavigationNodes.stream().map(node -> getNavigationModelToDataConverter().convert(node))
				.collect(Collectors.toList());
	}

	/**
	 * @deprecated since 1811, please use {@link de.hybris.platform.cmsfacades.cmsitems.CMSItemFacade#updateItem(String, Map)} instead.
	 */
	@Override
	@Deprecated
	public NavigationNodeData updateNavigationNode(final String navigationNodeUid, final NavigationNodeData navigationNodeData)
			throws CMSItemNotFoundException
	{
		if (!StringUtils.equals(navigationNodeUid, navigationNodeData.getUid()))
		{
			throw new AmbiguousIdentifierException("Navigation node unique identifier is ambiguous. Please fix the data. ["
					+ navigationNodeUid + "] x [" + navigationNodeData.getUid() + "]");
		}
		getFacadeValidationService().validate(getUpdateNavigationNodeValidator(), navigationNodeData);
		getFacadeValidationService().validate(getNavigationNodeEntriesValidator(), navigationNodeData);

		final CMSNavigationNodeModel navigationNodeModel = getNavigationService()
				.getNavigationNodeForId(navigationNodeData.getUid());

		new TransactionTemplate(getTransactionManager()).execute(new TransactionCallbackWithoutResult()
		{
			@Override
			protected void doInTransactionWithoutResult(final TransactionStatus status)
			{
				try
				{
					getNavigationEntryService().deleteNavigationEntries(navigationNodeUid);
					getNavigationNodeDataToModelUpdatePopulator().populate(navigationNodeData, navigationNodeModel);
					getModelService().saveAll();
				}
				catch (final CMSItemNotFoundException e)
				{
					throw new RuntimeException(e);
				}
			}
		});
		return findNavigationNodeById(navigationNodeData.getUid());
	}

	/**
	 * @deprecated since 1811, please use {@link de.hybris.platform.cmsfacades.cmsitems.CMSItemFacade#createItem(Map)} instead.
	 */
	@Override
	@Deprecated
	public NavigationNodeData addNavigationNode(final NavigationNodeData navigationNodeData) throws CMSItemNotFoundException
	{
		getFacadeValidationService().validate(getCreateNavigationNodeValidator(), navigationNodeData);
		getFacadeValidationService().validate(getNavigationNodeEntriesValidator(), navigationNodeData);

		final CatalogVersionModel catalogVersionModel = getAdminSiteService().getActiveCatalogVersion();
		// if the parent equals {@code ROOT}, then the parent must be the catalogVersionModel
		final ItemModel parentNode = ROOT.equalsIgnoreCase(navigationNodeData.getParentUid()) ? catalogVersionModel
				: getNavigationService().getNavigationNodeForId(navigationNodeData.getParentUid());


		final String navigationNodeUid = new TransactionTemplate(getTransactionManager()).execute(status -> {
			final CMSNavigationNodeModel navigationNodeModel = getNavigationService().createNavigationNode(parentNode,
					navigationNodeData.getUid(), true, Collections.emptyList());

			getNavigationNodeDataToModelCreatePopulator().populate(navigationNodeData, navigationNodeModel);
			getModelService().saveAll();
			return navigationNodeModel.getUid();
		});

		return findNavigationNodeById(navigationNodeUid);
	}

	/**
	 * @deprecated since 1811, please use {@link de.hybris.platform.cmsfacades.cmsitems.CMSItemFacade#deleteCMSItemByUuid(String)} instead.
	 */
	@Override
	@Deprecated
	public void deleteNavigationNode(final String uid) throws CMSItemNotFoundException
	{
		final CMSNavigationNodeModel navigationNodeModel = getNavigationService().getNavigationNodeForId(uid);
		new TransactionTemplate(getTransactionManager()).execute(new TransactionCallbackWithoutResult()
		{
			@Override
			protected void doInTransactionWithoutResult(final TransactionStatus status)
			{
				try
				{
					getNavigationEntryService().deleteNavigationEntries(uid);
					getNavigationService().delete(navigationNodeModel);
				}
				catch (final CMSItemNotFoundException e)
				{
					LOGGER.warn("Error deleting navigation node [ " + uid + " ]");
				}
			}
		});
	}

	@Override
	public List<NavigationNodeData> getNavigationAncestorsAndSelf(final String navigationNodeUid) throws CMSItemNotFoundException
	{
		if (ROOT.equalsIgnoreCase(navigationNodeUid))
		{
			return Collections.emptyList();
		}

		final List<CMSNavigationNodeModel> navigationNodes = new ArrayList<>();

		final CMSNavigationNodeModel navigationNode = getNavigationService().getNavigationNodeForId(navigationNodeUid);

		populateParentNavigationNode(navigationNodes, navigationNode);

		return navigationNodes.stream().map(navigationNodeModel -> getNavigationModelToDataConverter().convert(navigationNodeModel))
				.collect(Collectors.toList());
	}

	/**
	 * Recursively add self and parent nodes to the navigationNodes list.
	 *
	 * @param navigationNodes
	 * 		the list containing all navigation nodes.
	 * @param node
	 * 		the node to be added to the list with its parents.
	 */
	protected void populateParentNavigationNode(final List<CMSNavigationNodeModel> navigationNodes,
			final CMSNavigationNodeModel node)
	{
		if (node == null || ROOT.equalsIgnoreCase(node.getUid()))
		{
			return;
		}
		navigationNodes.add(node);
		populateParentNavigationNode(navigationNodes, node.getParent());
	}

	protected CMSNavigationService getNavigationService()
	{
		return navigationService;
	}

	@Required
	public void setNavigationService(final CMSNavigationService navigationService)
	{
		this.navigationService = navigationService;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @deprecated since 1811 - no longer needed
	 */
	@Deprecated
	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected CMSAdminSiteService getAdminSiteService()
	{
		return adminSiteService;
	}

	/**
	 * @deprecated since 1811 - no longer needed
	 */
	@Deprecated
	@Required
	public void setAdminSiteService(final CMSAdminSiteService adminSiteService)
	{
		this.adminSiteService = adminSiteService;
	}

	protected AbstractPopulatingConverter<CMSNavigationNodeModel, NavigationNodeData> getNavigationModelToDataConverter()
	{
		return navigationModelToDataConverter;
	}

	@Required
	public void setNavigationModelToDataConverter(
			final AbstractPopulatingConverter<CMSNavigationNodeModel, NavigationNodeData> navigationModelToDataConverter)
	{
		this.navigationModelToDataConverter = navigationModelToDataConverter;
	}

	protected Populator<NavigationNodeData, CMSNavigationNodeModel> getNavigationNodeDataToModelUpdatePopulator()
	{
		return navigationNodeDataToModelUpdatePopulator;
	}

	/**
	 * @deprecated since 1811 - no longer needed
	 */
	@Deprecated
	@Required
	public void setNavigationNodeDataToModelUpdatePopulator(
			final Populator<NavigationNodeData, CMSNavigationNodeModel> navigationNodeDataToModelUpdatePopulator)
	{
		this.navigationNodeDataToModelUpdatePopulator = navigationNodeDataToModelUpdatePopulator;
	}

	protected Populator<NavigationNodeData, CMSNavigationNodeModel> getNavigationNodeDataToModelCreatePopulator()
	{
		return navigationNodeDataToModelCreatePopulator;
	}

	/**
	 * @deprecated since 1811 - no longer needed
	 */
	@Deprecated
	@Required
	public void setNavigationNodeDataToModelCreatePopulator(
			final Populator<NavigationNodeData, CMSNavigationNodeModel> navigationNodeDataToModelCreatePopulator)
	{
		this.navigationNodeDataToModelCreatePopulator = navigationNodeDataToModelCreatePopulator;
	}

	protected FacadeValidationService getFacadeValidationService()
	{
		return facadeValidationService;
	}

	/**
	 * @deprecated since 1811 - no longer needed
	 */
	@Deprecated
	@Required
	public void setFacadeValidationService(final FacadeValidationService facadeValidationService)
	{
		this.facadeValidationService = facadeValidationService;
	}

	protected Validator getCreateNavigationNodeValidator()
	{
		return createNavigationNodeValidator;
	}

	/**
	 * @deprecated since 1811 - no longer needed
	 */
	@Deprecated
	@Required
	public void setCreateNavigationNodeValidator(final Validator createNavigationNodeValidator)
	{
		this.createNavigationNodeValidator = createNavigationNodeValidator;
	}

	protected Validator getUpdateNavigationNodeValidator()
	{
		return updateNavigationNodeValidator;
	}

	/**
	 * @deprecated since 1811 - no longer needed
	 */
	@Deprecated
	@Required
	public void setUpdateNavigationNodeValidator(final Validator updateNavigationNodeValidator)
	{
		this.updateNavigationNodeValidator = updateNavigationNodeValidator;
	}

	protected PlatformTransactionManager getTransactionManager()
	{
		return transactionManager;
	}

	/**
	 * @deprecated since 1811 - no longer needed
	 */
	@Deprecated
	@Required
	public void setTransactionManager(final PlatformTransactionManager transactionManager)
	{
		this.transactionManager = transactionManager;
	}

	protected NavigationEntryService getNavigationEntryService()
	{
		return navigationEntryService;
	}

	/**
	 * @deprecated since 1811 - no longer needed
	 */
	@Deprecated
	@Required
	public void setNavigationEntryService(final NavigationEntryService navigationEntryService)
	{
		this.navigationEntryService = navigationEntryService;
	}

	protected Validator getNavigationNodeEntriesValidator()
	{
		return navigationNodeEntriesValidator;
	}

	/**
	 * @deprecated since 1811 - no longer needed
	 */
	@Deprecated
	@Required
	public void setNavigationNodeEntriesValidator(final Validator navigationNodeEntriesValidator)
	{
		this.navigationNodeEntriesValidator = navigationNodeEntriesValidator;
	}
}
