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

package de.hybris.platform.configurablebundleservices.bundle.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.configurablebundleservices.bundle.AbstractBundleComponentEditableChecker;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.daos.BundleTemplateDao;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.enums.GroupType;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.order.EntryGroup;
import de.hybris.platform.order.EntryGroupService;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the BundleTemplate service {@link BundleTemplateService}
 *
 */
public class DefaultBundleTemplateService implements BundleTemplateService
{
	private static final Logger LOG = Logger.getLogger(DefaultBundleTemplateService.class);

	private BundleTemplateDao bundleTemplateDao;
	private AbstractBundleComponentEditableChecker<CartModel> bundleComponentEditableChecker;
	private ModelService modelService;
	private EntryGroupService entryGroupService;

	@Override
	@Nonnull
	public BundleTemplateModel getRootBundleTemplate(@Nonnull final BundleTemplateModel bundleTemplateModel)
	{
		validateParameterNotNull(bundleTemplateModel, "Bundle template cannot be null");

		BundleTemplateModel parentTemplate;
		for (parentTemplate = bundleTemplateModel; parentTemplate.getParentTemplate() != null;)
		{
			parentTemplate = parentTemplate.getParentTemplate();
		}

		return parentTemplate;
	}


	@Override
	@Nonnull
	public BundleTemplateModel getBundleTemplateForCode(@Nonnull final String bundleId)
	{
		return getBundleTemplateDao().findBundleTemplateById(bundleId);
	}

	@Override
	@Nonnull
	public List<BundleTemplateModel> getBundleTemplatesByProduct(@Nonnull final ProductModel model)
	{
		return getBundleTemplateDao().findBundleTemplatesByProduct(model);
	}

	@Override
	@Nullable
	public BundleTemplateModel getSubsequentBundleTemplate(@Nonnull final BundleTemplateModel bundleTemplate)
	{
		return getRelativeBundleTemplate(bundleTemplate, 1);
	}

	@Override
	@Nullable
	public BundleTemplateModel getPreviousBundleTemplate(@Nonnull final BundleTemplateModel bundleTemplate)
	{
		return getRelativeBundleTemplate(bundleTemplate, -1);
	}

	@Override
	@Nullable
	public BundleTemplateModel getRelativeBundleTemplate(@Nonnull final BundleTemplateModel bundleTemplate, final int relativePosition)
	{
		final List<BundleTemplateModel> leafs = getLeafComponents(bundleTemplate);
		final int referencePosition = leafs.indexOf(bundleTemplate);

		if (referencePosition == -1)
		{
			// bundleTemplate is not a leaf
			return null;
		}
		final int position = referencePosition + relativePosition;
		if (position < 0 || position >= leafs.size())
		{
			// out of bounds
			return null;
		}
		return leafs.get(position);
	}

	@Override
	@Nonnull
	public List<BundleTemplateModel> getLeafComponents(@Nonnull final BundleTemplateModel anyComponent)
	{
		validateParameterNotNullStandardMessage("bundleTemplate", anyComponent);
		final List<BundleTemplateModel> leafs = new ArrayList<>();
		leafs.add(getRootBundleTemplate(anyComponent));
		int i = 0;
		while (i < leafs.size())
		{
			final List<BundleTemplateModel> children = leafs.get(i).getChildTemplates();
			if (CollectionUtils.isEmpty(children))
			{
				i++;
			}
			else
			{
				leafs.remove(i);
				leafs.addAll(i, children);
			}
		}
		return leafs;
	}

	@Override
	public int getPositionInParent(@Nonnull final BundleTemplateModel bundleTemplate)
	{
		validateParameterNotNullStandardMessage("bundleTemplate", bundleTemplate);

		if (bundleTemplate.getParentTemplate() == null)
		{
			return -1;
		}

		final List<BundleTemplateModel> leafs = getLeafComponents(bundleTemplate);
		return leafs.indexOf(bundleTemplate);
	}

	@Override
	@Nonnull
	public List<BundleTemplateModel> getTemplatesForMasterOrderAndBundleNo(@Nonnull final AbstractOrderModel masterAbstractOrder,
			final int bundleNo)
	{
		return getBundleTemplateDao().findTemplatesByMasterOrderAndBundleNo(masterAbstractOrder, bundleNo);
	}

	@Override
	public boolean containsComponenentProductsOfType(@Nonnull final BundleTemplateModel bundleTemplate,
			@Nonnull final Class<? extends ProductModel>... clazzes)
	{
		validateParameterNotNullStandardMessage("bundleTemplate", bundleTemplate);
		validateParameterNotNullStandardMessage("clazz", clazzes);

		final List<ProductModel> products = bundleTemplate.getProducts();
		if (products.isEmpty())
		{
			LOG.info("BundleTemplate" + bundleTemplate.getId() + " has no products assigned");
			return false;
		}

		final ProductModel product = products.iterator().next();

		for (final Class clazz : clazzes)
		{
			if (clazz.isInstance(product))
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isAutoPickComponent(@Nullable final BundleTemplateModel bundleTemplate)
	{
		return getBundleComponentEditableChecker().isAutoPickComponent(bundleTemplate);
	}

	@Override
	@Nonnull
	public List<BundleTemplateModel> getAllComponentsOfType(@Nonnull final BundleTemplateModel bundleTemplate,
			@Nonnull final Class<? extends ProductModel>... clazzes)
	{
		validateParameterNotNullStandardMessage("bundleTemplate", bundleTemplate);
		validateParameterNotNullStandardMessage("clazzes", clazzes);

		final List<BundleTemplateModel> components = new ArrayList<>();
		for (final BundleTemplateModel component : bundleTemplate.getChildTemplates())
		{
			if (containsComponenentProductsOfType(component, clazzes))
			{
				components.add(component);
			}
		}

		return components;
	}

	@Override
	@Nonnull
	public BundleTemplateModel getBundleTemplateForCode(@Nonnull final String bundleId,@Nonnull  final String version)
	{
		return getBundleTemplateDao().findBundleTemplateByIdAndVersion(bundleId, version);
	}

	@Override
	public boolean isBundleTemplateUsed(@Nonnull final BundleTemplateModel bundleTemplate)
	{
		final List<AbstractOrderEntryModel> entries = getBundleTemplateDao().findAbstractOrderEntriesByBundleTemplate(
				bundleTemplate);
		return CollectionUtils.isNotEmpty(entries);
	}

	@Override
	@Nonnull
	public String getBundleTemplateName(final BundleTemplateModel bundleTemplate)
	{
		if (bundleTemplate == null)
		{
			return "<null>";
		}
		if (bundleTemplate.getName() != null)
		{
			return "'" + bundleTemplate.getName() + "'";
		}
		return "#" + bundleTemplate.getId();
	}

	@Override
	public EntryGroup getBundleEntryGroup(@Nonnull final AbstractOrderEntryModel entry)
	{
		validateParameterNotNullStandardMessage("entry", entry);
		if (CollectionUtils.isEmpty(entry.getEntryGroupNumbers()))
		{
			return null;
		}
		validateParameterNotNullStandardMessage("entry.order", entry.getOrder());
		if (CollectionUtils.isEmpty(entry.getOrder().getEntryGroups()))
		{
			throw new IllegalArgumentException("The order " + entry.getOrder().getCode() + " does not contain any groups.");
		}
		return  getEntryGroupService().getGroupOfType(entry.getOrder(), entry.getEntryGroupNumbers(), GroupType.CONFIGURABLEBUNDLE);
	}

	@Override
	public EntryGroup getBundleEntryGroup(final AbstractOrderModel order, final Set<Integer> entryGroupNumbers)
	{
		validateParameterNotNullStandardMessage("order", order);
		if (CollectionUtils.isEmpty(entryGroupNumbers))
		{
			return null;
		}

		return getEntryGroupService().getGroupOfType(order, entryGroupNumbers, GroupType.CONFIGURABLEBUNDLE);
	}

	@Override
	@Nonnull
	public EntryGroup createBundleTree(@Nonnull final BundleTemplateModel bundleTemplate, @Nonnull final AbstractOrderModel order)
	{
		final BundleTemplateModel rootTemplate = getRootBundleTemplate(bundleTemplate);
		final List<EntryGroup> treeGroups = createEntryGroupTree(rootTemplate, null);
		addGroupNumbers(treeGroups, order);
		addTreeToOrder(order, treeGroups.get(0));
		getEntryGroupService().forceOrderSaving(order);
		return treeGroups.get(0);
	}

	protected List<EntryGroup> createEntryGroupTree(@Nonnull final BundleTemplateModel currentBundleTemplate,
			final EntryGroup parentEntryGroup)
	{
		final List<EntryGroup> entryGroups = new ArrayList<>();

		final EntryGroup entryGroup = new EntryGroup();
		entryGroup.setErroneous(Boolean.FALSE);
		entryGroup.setExternalReferenceId(currentBundleTemplate.getId());
		entryGroup.setGroupType(GroupType.CONFIGURABLEBUNDLE);
		entryGroup.setLabel(currentBundleTemplate.getName());
		entryGroup.setChildren(new ArrayList<>());
		entryGroups.add(entryGroup);

		if (parentEntryGroup != null)
		{
			parentEntryGroup.getChildren().add(entryGroup);
		}

		if (CollectionUtils.isNotEmpty(currentBundleTemplate.getChildTemplates()))
		{
			currentBundleTemplate.getChildTemplates().forEach(
					childBundleTemplate -> entryGroups.addAll(createEntryGroupTree(childBundleTemplate, entryGroup))
			);
		}

		return entryGroups;
	}

	protected void addGroupNumbers(@Nonnull final List<EntryGroup> bundleEntryGroups, @Nonnull final AbstractOrderModel order)
	{
		int groupNumber = getEntryGroupService().findMaxGroupNumber(order.getEntryGroups()) + 1;
		for (final EntryGroup bundleEntryGroup : bundleEntryGroups)
		{
			bundleEntryGroup.setGroupNumber(Integer.valueOf(groupNumber++));
		}
	}

	protected void addTreeToOrder(@Nonnull final AbstractOrderModel order, @Nonnull final EntryGroup rootGroup)
	{
		final List<EntryGroup> entryGroups = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(order.getEntryGroups()))
		{
			entryGroups.addAll(order.getEntryGroups());
		}
		entryGroups.add(rootGroup);

		order.setEntryGroups(entryGroups);
	}

	protected BundleTemplateDao getBundleTemplateDao()
	{
		return bundleTemplateDao;
	}

	@Required
	public void setBundleTemplateDao(final BundleTemplateDao bundleTemplateDao)
	{
		this.bundleTemplateDao = bundleTemplateDao;
	}

	protected AbstractBundleComponentEditableChecker<CartModel> getBundleComponentEditableChecker()
	{
		return bundleComponentEditableChecker;
	}

	@Required
	public void setBundleComponentEditableChecker(
			final AbstractBundleComponentEditableChecker<CartModel> bundleComponentEditableChecker)
	{
		this.bundleComponentEditableChecker = bundleComponentEditableChecker;
	}

	@Override
	@Nonnull
	public List<BundleTemplateModel> getAllRootBundleTemplates(final CatalogVersionModel catalogVersion)
	{
		return getBundleTemplateDao().findAllRootBundleTemplates(catalogVersion);
	}

	@Override
	@Nonnull
	public List<BundleTemplateModel> getAllApprovedRootBundleTemplates(final CatalogVersionModel catalogVersion)
	{
		return getBundleTemplateDao().findAllApprovedRootBundleTemplates(catalogVersion);
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected EntryGroupService getEntryGroupService()
	{
		return entryGroupService;
	}

	@Required
	public void setEntryGroupService(final EntryGroupService entryGroupService)
	{
		this.entryGroupService = entryGroupService;
	}
}
