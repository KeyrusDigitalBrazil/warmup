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

import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.configurablebundleservices.constants.ConfigurableBundleServicesConstants;
import de.hybris.platform.core.enums.GroupType;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.configurablebundleservices.bundle.AbstractBundleComponentEditableChecker;
import de.hybris.platform.configurablebundleservices.daos.OrderEntryDao;
import de.hybris.platform.configurablebundleservices.model.BundleSelectionCriteriaModel;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.configurablebundleservices.model.PickExactlyNBundleSelectionCriteriaModel;
import de.hybris.platform.configurablebundleservices.model.PickNToMBundleSelectionCriteriaModel;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.hybris.platform.core.order.EntryGroup;
import de.hybris.platform.order.EntryGroupService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nullable;
import javax.annotation.Nonnull;


/**
 * Default implementation of the {@link AbstractBundleComponentEditableChecker}
 */
public abstract class DefaultAbstractBundleComponentEditableChecker<O extends AbstractOrderModel, E extends AbstractOrderEntryModel>
		implements AbstractBundleComponentEditableChecker<O>
{
	private static final Logger LOG = Logger.getLogger(DefaultAbstractBundleComponentEditableChecker.class);

	private OrderEntryDao orderEntryDao;
	private EntryGroupService entryGroupService;

	@Override
	public boolean canEdit(@Nonnull final O masterAbstractOrder, @Nullable final BundleTemplateModel bundleTemplate,
			final int bundleNo)
	{
		try
		{
			checkIsComponentDependencyMet(masterAbstractOrder, bundleTemplate, bundleNo);
			return true;
		}
		catch (final CommerceCartModificationException e)
		{
			LOG.debug("Error checking for editable", e);
			return false;
		}
	}

	@Override
	public void checkIsComponentDependencyMet(@Nonnull final O masterAbstractOrder,@Nullable final BundleTemplateModel bundleTemplate,
			final int bundleNo) throws CommerceCartModificationException
	{
		if (bundleTemplate != null && bundleNo != ConfigurableBundleServicesConstants.NO_BUNDLE)
		{
			checkComponentIsLeaf(bundleTemplate);

			final Collection<BundleTemplateModel> requiredTemplates = bundleTemplate.getRequiredBundleTemplates();

			for (final BundleTemplateModel requiredTemplate : requiredTemplates)
			{
				final List<E> entries = getOrderEntryDao().findEntriesByMasterCartAndBundleNoAndTemplate(masterAbstractOrder,
						bundleNo, requiredTemplate);
				if (entries.isEmpty())
				{
					// no entries -> required component not in bundle
					throw new CommerceCartModificationException("Component '" + bundleTemplate.getId()
							+ "' cannot be modified as its selection dependency to component '" + requiredTemplate.getId()
							+ "' is not fulfilled (no entries yet); masterOrder='" + masterAbstractOrder.getCode() + "'; bundleNo='"
							+ bundleNo + "'");
				}
				else
				{
					// check that the existing component's selection criteria are fulfilled
					checkIsComponentSelectionCriteriaMet(masterAbstractOrder, requiredTemplate, bundleNo);
				}
			}
		}
	}

	@Override
	public boolean isAutoPickComponent(final BundleTemplateModel bundleTemplate)
	{
		return false;
	}

	@Override
	public boolean isComponentSelectionCriteriaMet(@Nonnull final O masterAbstractOrder,
			@Nullable final BundleTemplateModel bundleTemplate, final int bundleNo)
	{
		try
		{
			checkIsComponentSelectionCriteriaMet(masterAbstractOrder, bundleTemplate, bundleNo);
			return true;
		}
		catch (final CommerceCartModificationException e)
		{
			LOG.debug("Error checking selection criteria", e);
			return false;
		}
	}

	@Override
	// NOSONAR
	public void checkIsComponentSelectionCriteriaMet(@Nonnull final O masterAbstractOrder, final BundleTemplateModel bundleTemplate,
			final int bundleNo) throws CommerceCartModificationException
	{
		if (bundleTemplate != null && bundleNo != ConfigurableBundleServicesConstants.NO_BUNDLE)
		{
			checkComponentIsLeaf(bundleTemplate);

			// check that the existing component's selection criteria are fulfilled
			final BundleSelectionCriteriaModel selCrit = bundleTemplate.getBundleSelectionCriteria();
			int minRequiredItems = 0;
			int maxAllowedItems = 0;

			if (selCrit == null)
			{
				return;
			}
			else if (selCrit instanceof PickNToMBundleSelectionCriteriaModel)
			{
				minRequiredItems = ((PickNToMBundleSelectionCriteriaModel) selCrit).getN().intValue();
				maxAllowedItems = ((PickNToMBundleSelectionCriteriaModel) selCrit).getM().intValue();
			}
			else if (selCrit instanceof PickExactlyNBundleSelectionCriteriaModel)
			{
				minRequiredItems = ((PickExactlyNBundleSelectionCriteriaModel) selCrit).getN().intValue();
				maxAllowedItems = minRequiredItems;
			}

			final List<E> entries = getOrderEntryDao().findEntriesByMasterCartAndBundleNoAndTemplate(masterAbstractOrder, bundleNo,
					bundleTemplate);

			if (entries.size() >= minRequiredItems && entries.size() <= maxAllowedItems)
			{
				return;
			}
			else
			{
				throw new CommerceCartModificationException("Selection criteria of component '" + bundleTemplate.getId()
						+ "' is not fulfilled (minQ='" + minRequiredItems + "', maxQ='" + maxAllowedItems + "', cartQ='"
						+ entries.size() + "'); masterOrder='" + masterAbstractOrder.getCode() + "'; bundleNo='" + bundleNo + "'");
			}
		}
	}

	@Override
	public boolean isComponentDependencyMet(final O masterAbstractOrder,@Nullable final BundleTemplateModel bundleTemplate,
			final int bundleNo)
	{
		try
		{
			checkIsComponentDependencyMet(masterAbstractOrder, bundleTemplate, bundleNo);
			return true;
		}
		catch (final CommerceCartModificationException e)
		{
			LOG.debug("Error checking component dependencies", e);
			return false;
		}
	}

	@Override
	public boolean isRequiredDependencyMet(@Nonnull final O order, @Nonnull final BundleTemplateModel bundleTemplate,
			@Nonnull final Integer entryGroupNumber)
	{
		final Collection<BundleTemplateModel> requiredComponents = bundleTemplate.getRequiredBundleTemplates();
		if (CollectionUtils.isEmpty(requiredComponents))
		{
			return true;
		}
		final EntryGroup rootGroup = getEntryGroupService().getRoot(order, entryGroupNumber);
		final Set<Integer> requiredGroupNumbers = bundleTemplatesToGroupNumbers(rootGroup, requiredComponents);
		final Set<Integer> notEmptyGroupNumbers = getPopulatedGroupNumbers(order);
		return CollectionUtils.isSubCollection(requiredGroupNumbers, notEmptyGroupNumbers);
	}

	@Nonnull
	protected Set<Integer> bundleTemplatesToGroupNumbers(
			@Nonnull final EntryGroup rootGroup, @Nonnull final Collection<BundleTemplateModel> components)
	{
		final Set<String> requiredComponentIds = components.stream()
				.map(BundleTemplateModel::getId)
				.collect(Collectors.toSet());
		return getEntryGroupService().getLeaves(rootGroup).stream()
				.filter(group -> GroupType.CONFIGURABLEBUNDLE.equals(group.getGroupType()))
				.filter(group -> requiredComponentIds.contains(group.getExternalReferenceId()))
				.map(EntryGroup::getGroupNumber)
				.collect(Collectors.toSet());
	}

	@Nonnull
	protected Set<Integer> getPopulatedGroupNumbers(@Nonnull final AbstractOrderModel order)
	{
		return order.getEntries().stream()
				.map(AbstractOrderEntryModel::getEntryGroupNumbers)
				.flatMap(Collection::stream)
				.collect(Collectors.toSet());
	}

	protected void checkComponentIsLeaf(@Nonnull final BundleTemplateModel bundleTemplate) throws CommerceCartModificationException
	{
		if(bundleTemplate.getChildTemplates() != null && !bundleTemplate.getChildTemplates().isEmpty())
		{
			throw new CommerceCartModificationException("Component '" + bundleTemplate.getId()
					+ "' cannot be modified as it has non-emptpy list of child components");
		}
	}

	@Required
	public void setOrderEntryDao(final OrderEntryDao orderEntryDao)
	{
		this.orderEntryDao = orderEntryDao;
	}

	protected OrderEntryDao getOrderEntryDao()
	{
		return orderEntryDao;
	}

	protected EntryGroupService getEntryGroupService()
	{
		return entryGroupService;
	}

	@Required
	public void setEntryGroupService(EntryGroupService entryGroupService)
	{
		this.entryGroupService = entryGroupService;
	}
}
