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

import static de.hybris.platform.configurablebundleservices.constants.ConfigurableBundleServicesConstants.NEW_BUNDLE;
import static de.hybris.platform.configurablebundleservices.constants.ConfigurableBundleServicesConstants.NO_BUNDLE;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.hook.CommerceAddToCartMethodHook;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.configurablebundleservices.bundle.AbstractBundleComponentEditableChecker;
import de.hybris.platform.configurablebundleservices.bundle.BundleRuleService;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.configurablebundleservices.model.DisableProductBundleRuleModel;
import de.hybris.platform.configurablebundleservices.order.BundleCartValidator;
import de.hybris.platform.core.enums.GroupType;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.order.EntryGroup;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.EntryGroupService;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.i18n.L10NService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Bundle-specific extension of the generic addToCart method.
 *
 * @see de.hybris.platform.commerceservices.order.CommerceCartService
 */
public class BundleAddToCartMethodHook implements CommerceAddToCartMethodHook
{
	private L10NService l10NService;
	private AbstractBundleComponentEditableChecker<CartModel> bundleComponentEditableChecker;
	private BundleRuleService bundleRuleService;
	private CartService cartService;
	private BundleCartHookHelper bundleCartHookHelper;
	private BundleTemplateService bundleTemplateService;
	private EntryGroupService entryGroupService;
	private BundleCartValidator bundleCartValidator;

	@Override
	public void beforeAddToCart(final CommerceCartParameter parameter) throws CommerceCartModificationException
	{
		validateParameterNotNullStandardMessage("parameter", parameter);
		if (isBundledEntry(parameter))
		{
			validateParameterNotNullStandardMessage("parameter.cart", parameter.getCart());
			checkBundleParameters(parameter);
			if (parameter.getBundleTemplate() == null)
			{
				parameter.setBundleTemplate(getComponent(parameter));
			}
			checkIsProductInComponentProductList(parameter);
			checkCanBeAddedToComponent(parameter);
			checkIsProductDisabledByRules(parameter);
		}
		else
		{
			validateParameterNotNullStandardMessage("parameter.product", parameter.getProduct());
			checkIsSoldIndividually(parameter);
		}
	}

	@Override
	public void afterAddToCart(final CommerceCartParameter parameter, final CommerceCartModification result)
			throws CommerceCartModificationException
	{
		validateParameterNotNullStandardMessage("parameter", parameter);
		if (result.getQuantityAdded() > 0 && result.getEntry() != null && isBundledEntry(parameter))
		{
			if (startingNewBundle(parameter))
			{
				// Starting new bundle
				final Collection<Integer> oldGroupNumbers = result.getEntry().getEntryGroupNumbers() == null
						? Collections.emptyList()
						: new ArrayList<>(result.getEntry().getEntryGroupNumbers());
				final EntryGroup root = getBundleTemplateService()
						.createBundleTree(parameter.getBundleTemplate(), result.getEntry().getOrder());
				final EntryGroup componentGroup = getGroupForComponent(root, parameter.getBundleTemplate().getId());
				addEntryGroupsToEntry(result.getEntry(), Collections.singletonList(componentGroup.getGroupNumber()));
				final Collection<Integer> newGroupNumbers = result.getEntry().getEntryGroupNumbers() == null
						? Collections.emptyList()
						: CollectionUtils.subtract(result.getEntry().getEntryGroupNumbers(), oldGroupNumbers);

				parameter.setEntryGroupNumbers(getBundleCartHookHelper().union(parameter.getEntryGroupNumbers(), newGroupNumbers));
			}
			addToBundle(parameter, result.getEntry());
			assignBundleNo(result.getEntry());
			getBundleCartHookHelper().invalidateBundleEntries(parameter.getCart(), getBundleEntryGroup(parameter).getGroupNumber());
			result.setEntryGroupNumbers(parameter.getEntryGroupNumbers());
		}
	}

	/**
	 * Assigns {@code bundleNo} to the entry. For new bundles next free bundleNo is used. For existing bundles the value
	 * is taken from another entry of the bundle.
	 *
	 * <p>
	 * The method has been introduced for compatibility to old (bundleNo-based) implementation.
	 * </p>
	 *
	 * @see AbstractOrderEntryModel#getBundleNo()
	 * @param entry entry to set bundle no
	 * @deprecated since 6.5 - bundleNo parameter is deprecated
	 */
	@Deprecated
	protected void assignBundleNo(final AbstractOrderEntryModel entry)
	{
		final List<AbstractOrderEntryModel> allEntriesOfTheBundle = getBundleEntries(entry.getOrder(), entry.getEntryGroupNumbers());
		final Integer bundleNo = allEntriesOfTheBundle.stream()
				.filter(e -> e != entry)
				.filter(e -> e.getBundleNo() != null)
				.filter(e -> e.getBundleNo().intValue() > 0)
				.map(AbstractOrderEntryModel::getBundleNo)
				.findAny()
				.orElseGet(() -> {
					final Integer result = getNextFreeBundleNo(entry.getOrder());
					allEntriesOfTheBundle.forEach(e -> e.setBundleNo(result));
					return result;
				});
		entry.setBundleNo(bundleNo);
	}

	/**
	 * @deprecated since 6.5 - bundleNo parameter is deprecated
	 */
	@Deprecated
	@Nonnull
	protected Integer getNextFreeBundleNo(@Nonnull final AbstractOrderModel order)
	{
		return Integer.valueOf(order.getEntries().stream()
				.filter(e -> e.getBundleNo() != null)
				.filter(e -> e.getBundleNo().intValue() > 0)
				.mapToInt(e -> e.getBundleNo().intValue())
				.max()
				.orElse(0) + 1);
	}

	/**
	 * Collects all entries of a bundle.
	 *
	 * @param cart the order where to take entries from
	 * @param entryGroupNumbers number of any group of the bundle
	 * @return list of bundle entries (may be empty)
	 * @throws IllegalArgumentException if {@code entryGroupNumber} is not found
	 */
	@Nonnull
	protected List<AbstractOrderEntryModel> getBundleEntries(
			@Nonnull final AbstractOrderModel cart, @Nonnull final Collection<Integer> entryGroupNumbers)
	{
		if (cart.getEntryGroups() == null)
		{
			return Collections.emptyList();
		}
		final List<Integer> bundleGroups = cart.getEntryGroups().stream()
				.filter(g -> GroupType.CONFIGURABLEBUNDLE.equals(g.getGroupType()))
				.map(getEntryGroupService()::getLeaves)
				.map(groups -> groups.stream().map(EntryGroup::getGroupNumber).collect(Collectors.toList()))
				.filter(groups -> CollectionUtils.containsAny(groups, entryGroupNumbers))
				.findAny()
				.orElse(Collections.emptyList());
		if (bundleGroups.isEmpty())
		{
			throw new IllegalArgumentException("Entry group list " + entryGroupNumbers + " has no group numbers for bundles.");
		}
		return cart.getEntries().stream()
				.filter(e -> e.getEntryGroupNumbers() != null)
				.filter(e -> CollectionUtils.containsAny(bundleGroups, e.getEntryGroupNumbers()))
				.collect(Collectors.toList());
	}

	protected boolean startingNewBundle(final CommerceCartParameter parameter) throws CommerceCartModificationException
	{
		return parameter.getBundleTemplate() != null && getBundleEntryGroup(parameter) == null;
	}

	/**
	 * Determines whether the entry applies to bundle.
	 *
	 * @param parameter entry definition
	 * @return true is it's a bundled entry
	 */
	protected boolean isBundledEntry(@Nonnull final CommerceCartParameter parameter) throws CommerceCartModificationException
	{
		if (parameter.getBundleTemplate() != null)
		{
			return true;
		}

		final EntryGroup bundleEntryGroup = getBundleEntryGroup(parameter);
		if (bundleEntryGroup == null)
		{
			return false;
		}

		return true;
	}

	protected EntryGroup getGroupForComponent(@Nonnull final EntryGroup root, final String externalReferenceId)
	{
		return getEntryGroupService().getLeaves(root).stream()
				.filter(group -> Objects.equals(group.getExternalReferenceId(), externalReferenceId))
				.findAny()
				.orElseThrow(() -> new IllegalArgumentException("Group tree #" + root.getGroupNumber()
						+ " does not contain leaf with refId '" + externalReferenceId + '\''));
	}

	protected void addEntryGroupsToEntry(
			@Nonnull final AbstractOrderEntryModel entry, @Nonnull final Collection<Integer> entryGroupNumbers)
	{
		final Set<Integer> numbers = new HashSet<>();
		if (entry.getEntryGroupNumbers() != null)
		{
			numbers.addAll(entry.getEntryGroupNumbers());
		}
		numbers.addAll(entryGroupNumbers);
		entry.setEntryGroupNumbers(numbers);
	}

	/**
	 * Assign the new cart entry to an existing bundle or create a new bundle for it
	 */
	protected void addToBundle(@Nonnull CommerceCartParameter parameter, @Nonnull final AbstractOrderEntryModel entry)
			throws CommerceCartModificationException
	{
		addEntryGroupsToEntry(entry, parameter.getEntryGroupNumbers());
		entry.setBundleTemplate(parameter.getBundleTemplate());
		final EntryGroup bundleEntryGroup = getBundleEntryGroup(parameter);
		final EntryGroup rootGroup = getEntryGroupService().getRoot(entry.getOrder(), bundleEntryGroup.getGroupNumber());
		getBundleCartValidator().updateErroneousGroups(getEntryGroupService().getLeaves(rootGroup), entry.getOrder());
		getEntryGroupService().forceOrderSaving(entry.getOrder());
	}

	protected void checkBundleParameters(@Nonnull final CommerceCartParameter parameter) throws CommerceCartModificationException
	{
		final EntryGroup group = getBundleEntryGroup(parameter);

		if (group != null)
		{
			if (group.getExternalReferenceId() == null)
			{
				throw new CommerceCartModificationException(
						"Entry group #" + group.getGroupNumber() + " has null bundle component code");
			}
			try
			{
				final BundleTemplateModel requestedComponent = parameter.getBundleTemplate();
				final BundleTemplateModel storedComponent
						= getBundleTemplateService().getBundleTemplateForCode(group.getExternalReferenceId());
				if (requestedComponent != null && !requestedComponent.equals(storedComponent))
				{
					throw new IllegalArgumentException(
							"The given bundle " + getBundleTemplateService().getBundleTemplateName(requestedComponent)
									+ " is not equal to the component " + group.getExternalReferenceId() + " stored in entry group #"
									+ group.getGroupNumber() + ". Set bundle to null in the request to use the group's component "
									+ "or update the entry group.");
				}
			}
			catch (final ModelNotFoundException mnfx)
			{
				throw new CommerceCartModificationException(
						"Bundle template " + group.getExternalReferenceId() + " was not found. Check entry group #"
								+ group.getGroupNumber(), mnfx);
			}
		}
	}

	protected void checkIsProductInComponentProductList(@Nonnull final CommerceCartParameter parameter)
			throws CommerceCartModificationException
	{
		if (!parameter.getBundleTemplate().getProducts().contains(parameter.getProduct()))
		{
			throw new CommerceCartModificationException(
					"Product '" + parameter.getProduct().getCode()
							+ "' is not in the product list of component (bundle template) "
							+ getBundleTemplateService().getBundleTemplateName(parameter.getBundleTemplate()));
		}
	}

	protected void checkIsSoldIndividually(@Nonnull final CommerceCartParameter parameter)
			throws CommerceCartModificationException
	{
		if (!BooleanUtils.toBoolean(parameter.getProduct().getSoldIndividually()))
		{
			throw new CommerceCartModificationException(
					"The given product '" + parameter.getProduct().getCode() + "' can not be sold individually.");
		}
	}

	protected void checkCanBeAddedToComponent(@Nonnull final CommerceCartParameter parameter)
			throws CommerceCartModificationException
	{
		final EntryGroup entryGroup = getBundleEntryGroup(parameter);
		final BundleTemplateModel bundleTemplate = parameter.getBundleTemplate();
		final CartModel cart = parameter.getCart();
		if (entryGroup == null)
		{
			if (CollectionUtils.isEmpty(bundleTemplate.getRequiredBundleTemplates()))
			{
				return;
			}
			else
			{
				// If entry group is null (does not exist in cart), it can be safely assumed that required component is not added yet
				throw new CommerceCartModificationException("Component '" + bundleTemplate.getId()
						+ "' cannot be modified as its selection dependency to component one of its components is not fulfilled; " +
						"order='" + cart.getCode() + "'; entryGroupNumber='null'");
			}
		}

		if (!getBundleComponentEditableChecker().isRequiredDependencyMet(cart, bundleTemplate, entryGroup.getGroupNumber()))
		{
			throw new CommerceCartModificationException("Component '" + bundleTemplate.getId()
					+ "' cannot be modified as its selection dependency to component one of its components is not fulfilled; " +
					"order='" + cart.getCode() + "'; entryGroupNumber='" + entryGroup.getGroupNumber() + "'");
		}
	}

	protected void checkIsProductDisabledByRules(@Nonnull final CommerceCartParameter parameter)
			throws CommerceCartModificationException
	{
		final DisableProductBundleRuleModel disableRule = getBundleRuleService().getDisableRuleForBundleProduct(
				parameter.getCart(), parameter.getProduct(), parameter.getBundleTemplate(),
				getBundleNo(parameter), false);
		if (disableRule != null)
		{
			throw new CommerceCartModificationException("Product " + parameter.getProduct().getCode()
					+ " cannot be added as disable rule '" + disableRule.getId() + "' of component "
					+ getBundleTemplateService().getBundleTemplateName(disableRule.getBundleTemplate()) + " is violated");
		}
	}

	/**
	 * @deprecated since 6.5 - bundleNo parameter is deprecated
	 */
	@Deprecated
	protected int getBundleNo(@Nonnull final CommerceCartParameter parameter) throws CommerceCartModificationException
	{
		final EntryGroup bundleEntryGroup = getBundleEntryGroup(parameter);
		if (bundleEntryGroup == null)
		{
			return parameter.getBundleTemplate() == null ? NO_BUNDLE : NEW_BUNDLE;
		}
		final EntryGroup bundleRoot = getEntryGroupService().getRoot(parameter.getCart(), bundleEntryGroup.getGroupNumber());
		final List<Integer> bundleGroupNumbers = getEntryGroupService().getLeaves(bundleRoot).stream()
				.map(EntryGroup::getGroupNumber)
				.collect(Collectors.toList());
		return parameter.getCart().getEntries().stream()
				.filter(e -> e.getBundleNo() != null)
				.filter(e -> e.getBundleNo().intValue() > 0)
				.filter(e -> e.getEntryGroupNumbers() != null)
				.filter(e -> CollectionUtils.containsAny(bundleGroupNumbers, e.getEntryGroupNumbers()))
				.map(AbstractOrderEntryModel::getBundleNo)
				.findAny()
				.orElse(Integer.valueOf(NO_BUNDLE))
				.intValue();
	}
	
	protected EntryGroup getBundleEntryGroup(final CommerceCartParameter parameter) throws CommerceCartModificationException
	{
		if (CollectionUtils.isEmpty(parameter.getEntryGroupNumbers()))
		{
			return null;
		}

		EntryGroup bundleEntryGroup = null;
		try
		{
			bundleEntryGroup = getBundleTemplateService().getBundleEntryGroup(parameter.getCart(), parameter.getEntryGroupNumbers());
		}
		catch (final IllegalArgumentException e)
		{
			throw new CommerceCartModificationException(e.getMessage(), e);
		}
		return bundleEntryGroup;
	}

	protected BundleTemplateModel getComponent(@Nonnull final CommerceCartParameter parameter) 
			throws CommerceCartModificationException
	{
		if (parameter.getBundleTemplate() != null)
		{
			return parameter.getBundleTemplate();
		}

		final EntryGroup bundleEntryGroup = getBundleEntryGroup(parameter);
		if (bundleEntryGroup == null)
		{
			return null;
		}
		return getBundleTemplateService().getBundleTemplateForCode(bundleEntryGroup.getExternalReferenceId());
	}

	protected L10NService getL10NService()
	{
		return l10NService;
	}

	@Required
	public void setL10NService(final L10NService l10NService)
	{
		this.l10NService = l10NService;
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

	protected BundleRuleService getBundleRuleService()
	{
		return bundleRuleService;
	}

	@Required
	public void setBundleRuleService(final BundleRuleService bundleRuleService)
	{
		this.bundleRuleService = bundleRuleService;
	}

	protected CartService getCartService()
	{
		return cartService;
	}

	@Required
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

	protected BundleCartHookHelper getBundleCartHookHelper()
	{
		return bundleCartHookHelper;
	}

	@Required
	public void setBundleCartHookHelper(BundleCartHookHelper bundleCartHookHelper)
	{
		this.bundleCartHookHelper = bundleCartHookHelper;
	}

	protected BundleTemplateService getBundleTemplateService()
	{
		return bundleTemplateService;
	}

	@Required
	public void setBundleTemplateService(BundleTemplateService bundleTemplateService)
	{
		this.bundleTemplateService = bundleTemplateService;
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

	protected BundleCartValidator getBundleCartValidator()
	{
		return bundleCartValidator;
	}

	@Required
	public void setBundleCartValidator(BundleCartValidator bundleCartValidator)
	{
		this.bundleCartValidator = bundleCartValidator;
	}
}
