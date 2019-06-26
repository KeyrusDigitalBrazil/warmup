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
package de.hybris.platform.configurablebundleservices.order.hook;

import de.hybris.platform.commerceservices.order.CommerceCartCalculationStrategy;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.hook.CommerceUpdateCartEntryHook;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.configurablebundleservices.bundle.AbstractBundleComponentEditableChecker;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.model.BundleSelectionCriteriaModel;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.configurablebundleservices.model.PickExactlyNBundleSelectionCriteriaModel;
import de.hybris.platform.configurablebundleservices.model.PickNToMBundleSelectionCriteriaModel;
import de.hybris.platform.configurablebundleservices.order.BundleCartValidator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.order.EntryGroup;
import de.hybris.platform.order.EntryGroupService;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.util.ServicesUtil;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Hook to include bundle functionality in update cart entry method.
 *
 * @see de.hybris.platform.commerceservices.order.CommerceCartService#updateQuantityForCartEntry(CommerceCartParameter)
 * @see de.hybris.platform.commerceservices.order.CommerceCartService#updatePointOfServiceForCartEntry(CommerceCartParameter)
 * @see de.hybris.platform.commerceservices.order.CommerceCartService#updateToShippingModeForCartEntry(CommerceCartParameter)
 */
public class BundleUpdateCartEntryHook implements CommerceUpdateCartEntryHook
{
	private static final Logger LOG = Logger.getLogger(BundleUpdateCartEntryHook.class);

	private BundleCartHookHelper bundleCartHookHelper;
	private BundleTemplateService bundleTemplateService;
	private EntryGroupService entryGroupService;
	private BundleCartValidator bundleCartValidator;
	private AbstractBundleComponentEditableChecker<CartModel> bundleComponentEditableChecker;
	private CommerceCartCalculationStrategy commerceCartCalculationStrategy;

	@Override
	public void beforeUpdateCartEntry(final CommerceCartParameter parameter)
	{
		ServicesUtil.validateParameterNotNullStandardMessage("parameter", parameter);
		ServicesUtil.validateParameterNotNullStandardMessage("parameter.cart", parameter.getCart());
		final AbstractOrderEntryModel entry = getEntryToUpdate(parameter);
		final EntryGroup bundleGroup = getBundleTemplateService().getBundleEntryGroup(entry);
		if (bundleGroup == null)
		{
			//	not a bundle entry
			return;
		}
		// We could not call getEntryToUpdate in afterUpdateCartEntry, because if quantity is 0,
		// the original entry is already removed.  Fortunately we do not need the entry itself, the group number is enough.
		parameter.setEntryGroupNumbers(getBundleCartHookHelper().union(parameter.getEntryGroupNumbers(),
				Collections.singletonList(bundleGroup.getGroupNumber())));

		final BundleTemplateModel component = getComponent(bundleGroup.getExternalReferenceId());
		if (!getBundleComponentEditableChecker().isRequiredDependencyMet(parameter.getCart(), component, bundleGroup.getGroupNumber()))
		{
			return;
		}
		if (parameter.getQuantity() > 0)
		{
			trimQuantityToAllowedForTheComponent(parameter, component);
		}
	}

	@Override
	public void afterUpdateCartEntry(final CommerceCartParameter parameter, final CommerceCartModification result)
	{
		// The group number has been updated in beforeUpdateCartEntry
		final EntryGroup entryGroup = getBundleTemplateService().getBundleEntryGroup(parameter.getCart(),
				parameter.getEntryGroupNumbers());
		if (entryGroup == null)
		{
			// not a bundle entry
			return;
		}
		final CartModel cart = parameter.getCart();
		final EntryGroup rootGroup = getEntryGroupService().getRoot(cart, entryGroup.getGroupNumber());
		final List<EntryGroup> tree = getEntryGroupService().getNestedGroups(rootGroup);
		if (getBundleCartValidator().updateErroneousGroups(tree, cart))
		{
			getEntryGroupService().forceOrderSaving(cart);
		}
		getBundleCartHookHelper().invalidateBundleEntries(parameter.getCart(), entryGroup.getGroupNumber());

		final CommerceCartParameter calculationParameter = new CommerceCartParameter();
		calculationParameter.setCart(cart);
		calculationParameter.setEnableHooks(true);
		getCommerceCartCalculationStrategy().calculateCart(calculationParameter);
	}


	protected void trimQuantityToAllowedForTheComponent(
			@Nonnull final CommerceCartParameter parameter, @Nonnull final BundleTemplateModel component)
	{
		final BundleSelectionCriteriaModel selectionCriteria = component.getBundleSelectionCriteria();
		if (selectionCriteria == null)
		{
			return;
		}
		long maxAllowedQuantity = parameter.getQuantity();
		if (selectionCriteria instanceof PickNToMBundleSelectionCriteriaModel)
		{
			maxAllowedQuantity = ((PickNToMBundleSelectionCriteriaModel) selectionCriteria).getM().longValue();
		}
		else if (selectionCriteria instanceof PickExactlyNBundleSelectionCriteriaModel)
		{
			maxAllowedQuantity = ((PickExactlyNBundleSelectionCriteriaModel) selectionCriteria).getN().longValue();
		}
		if (maxAllowedQuantity < parameter.getQuantity())
		{
			LOG.info("The requested quantity was decreased due to a selection criterion");
			parameter.setQuantity(maxAllowedQuantity);
		}
	}

	@Nonnull
	protected BundleTemplateModel getComponent(@Nonnull final String componentId)
	{
		final BundleTemplateModel component;
		try
		{
			component = getBundleTemplateService().getBundleTemplateForCode(componentId);
		}
		catch (final ModelNotFoundException e)
		{
			throw new IllegalArgumentException("Bundle template " + componentId + " was not found", e);
		}
		return component;
	}

	@Nonnull
	protected AbstractOrderEntryModel getEntryToUpdate(@Nonnull final CommerceCartParameter parameter)
	{
		if (parameter.getCart().getEntries() == null)
		{
			throw new IllegalArgumentException("Cart " + parameter.getCart().getCode() + " has no entries");
		}
		return parameter
				.getCart()
				.getEntries()
				.stream()
				.filter(e -> parameter.getEntryNumber() == e.getEntryNumber().longValue())
				.findAny()
				.orElseThrow(
						() -> new IllegalArgumentException("Entry #" + parameter.getEntryNumber() + " was not found in cart"
								+ parameter.getCart().getCode()));
	}

	protected BundleCartHookHelper getBundleCartHookHelper()
	{
		return bundleCartHookHelper;
	}

	@Required
	public void setBundleCartHookHelper(final BundleCartHookHelper bundleCartHookHelper)
	{
		this.bundleCartHookHelper = bundleCartHookHelper;
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

	protected EntryGroupService getEntryGroupService()
	{
		return entryGroupService;
	}

	@Required
	public void setEntryGroupService(final EntryGroupService entryGroupService)
	{
		this.entryGroupService = entryGroupService;
	}

	protected BundleCartValidator getBundleCartValidator()
	{
		return bundleCartValidator;
	}

	@Required
	public void setBundleCartValidator(final BundleCartValidator bundleCartValidator)
	{
		this.bundleCartValidator = bundleCartValidator;
	}
	
	protected AbstractBundleComponentEditableChecker<CartModel> getBundleComponentEditableChecker()
	{
		return bundleComponentEditableChecker;
	}

	@Required
	public void setBundleComponentEditableChecker(AbstractBundleComponentEditableChecker<CartModel> bundleComponentEditableChecker)
	{
		this.bundleComponentEditableChecker = bundleComponentEditableChecker;
	}

	protected CommerceCartCalculationStrategy getCommerceCartCalculationStrategy()
	{
		return commerceCartCalculationStrategy;
	}

	@Required
	public void setCommerceCartCalculationStrategy(final CommerceCartCalculationStrategy commerceCartCalculationStrategy)
	{
		this.commerceCartCalculationStrategy = commerceCartCalculationStrategy;
	}
}
