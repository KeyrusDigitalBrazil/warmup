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
package de.hybris.platform.configurablebundleservices.order;

import de.hybris.platform.configurablebundleservices.bundle.AbstractBundleComponentEditableChecker;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.model.BundleSelectionCriteriaModel;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.configurablebundleservices.model.PickExactlyNBundleSelectionCriteriaModel;
import de.hybris.platform.configurablebundleservices.model.PickNToMBundleSelectionCriteriaModel;
import de.hybris.platform.core.enums.GroupType;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.order.EntryGroup;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.util.ServicesUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.Nonnull;


/**
 * Class that handles updating {@link EntryGroup#getErroneous()} status for bundle entryGroups
 * which entries that were changed.
 */
public class BundleCartValidator
{
	private static final Logger LOG = Logger.getLogger(BundleCartValidator.class);

	private ModelService modelService;
	private BundleTemplateService bundleTemplateService;
	private AbstractBundleComponentEditableChecker<AbstractOrderModel> bundleComponentEditableChecker;


	/**
	 * Updates {@code erroneous} flag of entries in {@code order} according to current state of bundle.
	 *
	 * @param groups
	 * 			entryGroups to check/update erroneous flag for
	 * @param order
	 * 			order with entryGroups
	 * @return true if any of the entry groups were updated, and false otherwise
	 */
	public boolean updateErroneousGroups(@Nonnull final List<EntryGroup> groups, @Nonnull final AbstractOrderModel order)
	{
		ServicesUtil.validateParameterNotNullStandardMessage("groups", groups);
		ServicesUtil.validateParameterNotNullStandardMessage("order", order);
		final AtomicBoolean changed = new AtomicBoolean(false);
		groups.stream()
				.filter(g -> StringUtils.isNotEmpty(g.getExternalReferenceId()))
				.filter(g -> GroupType.CONFIGURABLEBUNDLE.equals(g.getGroupType()))
				.forEach(bundleEntryGroup -> updateEntryGroupErroneousStatus(order, bundleEntryGroup, changed));
		return changed.get();
	}


	protected void updateEntryGroupErroneousStatus(
			@Nonnull final AbstractOrderModel order, @Nonnull final EntryGroup bundleEntryGroup, @Nonnull AtomicBoolean changed)
	{
		final BundleTemplateModel bundleTemplate
				= getBundleTemplateService().getBundleTemplateForCode(bundleEntryGroup.getExternalReferenceId());
		final boolean selectionCriteriaAreSatisfied
				= areSelectionCriteriaSatisfied(order, bundleEntryGroup.getGroupNumber(), bundleTemplate);
		final boolean externalDependenciesAreSatisfied = getBundleComponentEditableChecker().isRequiredDependencyMet(
				order, bundleTemplate, bundleEntryGroup.getGroupNumber());

		Boolean erroneous = Boolean.FALSE;
		if (externalDependenciesAreSatisfied)
		{
			if (!selectionCriteriaAreSatisfied)
			{
				erroneous = Boolean.TRUE;
			}
		}
		else if (getEntryGroupItemsQuantity(order, bundleEntryGroup.getGroupNumber()) > 0)
		{
			// There is a product in component, but not all required components are satisfied
			erroneous = Boolean.TRUE;
		}
		if (!bundleEntryGroup.getErroneous().equals(erroneous))
		{
			bundleEntryGroup.setErroneous(erroneous);
			changed.set(true);
		}
	}

	protected boolean areSelectionCriteriaSatisfied(
			@Nonnull final AbstractOrderModel order, @Nonnull final Integer groupNumber,
			@Nonnull final BundleTemplateModel bundleTemplate)
	{
		final BundleSelectionCriteriaModel selectionCriteria = bundleTemplate.getBundleSelectionCriteria();
		if (selectionCriteria == null)
		{
			return true;
		}

		final Long groupItemsQuantity = getEntryGroupItemsQuantity(order, groupNumber);
		Integer maxItemsAllowed = null;
		Integer minItemsAllowed = null;

		if (selectionCriteria instanceof PickNToMBundleSelectionCriteriaModel)
		{
			final PickNToMBundleSelectionCriteriaModel rangeSelectionCriteria
					= (PickNToMBundleSelectionCriteriaModel) selectionCriteria;
			maxItemsAllowed = rangeSelectionCriteria.getM();
			minItemsAllowed = rangeSelectionCriteria.getN();
		}
		else if (selectionCriteria instanceof PickExactlyNBundleSelectionCriteriaModel)
		{
			final Integer n = ((PickExactlyNBundleSelectionCriteriaModel) selectionCriteria).getN();
			maxItemsAllowed = n;
			minItemsAllowed = n;
		}
		else
		{
			LOG.debug("Selection criterion class '" + selectionCriteria.getClass().getName() + "' is not supported.");
		}

		return (maxItemsAllowed == null || groupItemsQuantity.longValue() <= maxItemsAllowed.longValue())
				&& (minItemsAllowed == null || groupItemsQuantity.longValue() >= minItemsAllowed.longValue());
	}

	@Nonnull
	protected Long getEntryGroupItemsQuantity(
			@Nonnull final AbstractOrderModel order, @Nonnull final Integer bundleEntryGroupNumber)
	{
		if (order.getEntries() == null)
		{
			return Long.valueOf(0L);
		}
		return Long.valueOf(order.getEntries().stream()
				.filter(e -> e.getEntryGroupNumbers() != null)
				.filter(e -> e.getEntryGroupNumbers().contains(bundleEntryGroupNumber))
				.filter(e -> e.getQuantity() != null)
				.map(AbstractOrderEntryModel::getQuantity)
				.mapToLong(Long::longValue)
				.sum());
	}

	protected BundleTemplateService getBundleTemplateService()
	{
		return bundleTemplateService;
	}

	@Required
	public void setBundleTemplateService(final BundleTemplateService bundleTemplateService)
	{
		this.bundleTemplateService = bundleTemplateService;
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

	protected AbstractBundleComponentEditableChecker<AbstractOrderModel> getBundleComponentEditableChecker()
	{
		return bundleComponentEditableChecker;
	}

	@Required
	public void setBundleComponentEditableChecker(AbstractBundleComponentEditableChecker<AbstractOrderModel> bundleComponentEditableChecker)
	{
		this.bundleComponentEditableChecker = bundleComponentEditableChecker;
	}
}
