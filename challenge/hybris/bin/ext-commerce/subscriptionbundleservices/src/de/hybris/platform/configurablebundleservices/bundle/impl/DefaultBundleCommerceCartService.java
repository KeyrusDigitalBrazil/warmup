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

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.commerceservices.strategies.ModifiableChecker;
import de.hybris.platform.configurablebundleservices.bundle.AbstractBundleComponentEditableChecker;
import de.hybris.platform.configurablebundleservices.bundle.BundleCommerceCartService;
import de.hybris.platform.configurablebundleservices.bundle.BundleRuleService;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.bundle.RemoveableChecker;
import de.hybris.platform.configurablebundleservices.constants.ConfigurableBundleServicesConstants;
import de.hybris.platform.configurablebundleservices.daos.OrderEntryDao;
import de.hybris.platform.configurablebundleservices.model.BundleSelectionCriteriaModel;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.configurablebundleservices.model.DisableProductBundleRuleModel;
import de.hybris.platform.configurablebundleservices.model.PickExactlyNBundleSelectionCriteriaModel;
import de.hybris.platform.configurablebundleservices.model.PickNToMBundleSelectionCriteriaModel;
import de.hybris.platform.core.enums.GroupType;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.core.order.EntryGroup;
import de.hybris.platform.order.EntryGroupService;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.subscriptionservices.model.BillingTimeModel;
import de.hybris.platform.subscriptionservices.subscription.impl.DefaultSubscriptionCommerceCartService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Preconditions;


/**
 * Default implementation of {@link BundleCommerceCartService}
 *
 * @deprecated since 6.4, see {@link BundleCommerceCartService}
 */
@Deprecated
public class DefaultBundleCommerceCartService extends DefaultSubscriptionCommerceCartService implements BundleCommerceCartService
{
	private static final Logger LOG = Logger.getLogger(DefaultBundleCommerceCartService.class);

	public static final int NO_BUNDLE = ConfigurableBundleServicesConstants.NO_BUNDLE;
	public static final int NEW_BUNDLE = ConfigurableBundleServicesConstants.NEW_BUNDLE;

	private static final long BUNDLE_PRODUCT_QUANTITY = 1;
	private static final String SESSION_ATTRIBUTE_CALCULATE_CART = "CALCULATE_CART";

	private BundleTemplateService bundleTemplateService;
	private BundleRuleService bundleRuleService;
	private OrderEntryDao bundleCartEntryDao;
	private ModifiableChecker<AbstractOrderEntryModel> orderEntryModifiableChecker;
	private AbstractBundleComponentEditableChecker<CartModel> bundleComponentEditableChecker;
	private L10NService l10NService;
	private EntryGroupService entryGroupService;

	private RemoveableChecker<CartEntryModel> removableChecker;

	/**
	 * @deprecated Since ages (at least from 5.1)
	 */
	@Deprecated
	@Override
	@Nonnull
	// NO SONAR
	public List<CommerceCartModification> addToCart(@Nonnull final CartModel masterCartModel,
			@Nonnull final ProductModel productModel, final long quantityToAdd, final UnitModel unit, final boolean forceNewEntry,
			final int bundleNo, final BundleTemplateModel bundleTemplateModel, final boolean removeCurrentProducts)
			throws CommerceCartModificationException
	{
		return addToCart(masterCartModel, productModel, quantityToAdd, unit, forceNewEntry, bundleNo, bundleTemplateModel,
				removeCurrentProducts, null);
	}

	/**
	 * @deprecated Since 6.4 - Use the generic
	 *             {@link de.hybris.platform.commerceservices.order.impl.DefaultCommerceCartService#addToCart(CommerceCartParameter)}
	 */
	@Override
	@Nonnull
	@Deprecated
	// NO SONAR
	public List<CommerceCartModification> addToCart(@Nonnull final CartModel masterCartModel,
			@Nonnull final ProductModel productModel, final long quantityToAdd, final UnitModel unit, final boolean forceNewEntry,
			final int bundleNo, final BundleTemplateModel bundleTemplateModel, final boolean removeCurrentProducts,
			final String xmlProduct) throws CommerceCartModificationException
	{
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(masterCartModel);
		parameter.setProduct(productModel);
		parameter.setBundleTemplate(bundleTemplateModel);
		parameter.setQuantity(quantityToAdd);
		parameter.setUnit(unit);
		parameter.setCreateNewEntry(forceNewEntry);
		parameter.setXmlProduct(xmlProduct);

		final Integer entryGroupNumber = getEntryGroupFor(masterCartModel, bundleNo, bundleTemplateModel);
		if (entryGroupNumber != null)
		{
			parameter.setEntryGroupNumbers(new HashSet<>(Collections.singletonList(entryGroupNumber)));
		}

		if (removeCurrentProducts)
		{
			final List<CartEntryModel> bundleEntries = getBundleCartEntryDao().findEntriesByMasterCartAndBundleNoAndTemplate(
					masterCartModel, bundleNo, bundleTemplateModel);
			checkAndRemoveDependentComponents(masterCartModel, bundleNo, bundleTemplateModel);
			removeEntriesWithChildren(bundleEntries, masterCartModel);
		}

		if (bundleNo != NO_BUNDLE)
		{
			checkAutoPickAddToCart(bundleTemplateModel, productModel);
		}

		final CommerceCartModification modification = addToCartWithoutCalculation(parameter);

		final List<CommerceCartModification> modificationList = new ArrayList<>();
		modificationList.add(modification);
		if (modification.getQuantityAdded() > 0 && modification.getEntry() != null)
		{
			if (bundleNo == NEW_BUNDLE)
			{
				final int newBundleNo = modification.getEntry().getBundleNo().intValue();
				final List<CommerceCartModification> autoPicks = addAutoPickProductsToCart(masterCartModel, newBundleNo,
						bundleTemplateModel, unit);
				modificationList.addAll(autoPicks);
			}

			calculateCart(masterCartModel);
		}

		updateLastModifiedEntriesList(masterCartModel, modificationList);

		return modificationList;
	}

	/**
	 * Adds a product to the cart, either as standalone product or as part of a bundle. All relevant bundle validations
	 * are checked here. The cart is not calculated in this method, only the new entry + its cart are marked as not
	 * calculated (standalone products). In case of bundles all entries of this bundle + all affected carts are marked as
	 * not calculated. The calculation itself must be triggered by the calling method.
	 */
	// NO SONAR
	protected CommerceCartModification addProductToCart(@Nonnull final CartModel masterCartModel,
			@Nonnull final ProductModel productModel, final long quantityToAdd, final UnitModel unit, final boolean forceNewEntry,
			final int bundleNo, @Nullable final BundleTemplateModel bundleTemplateModel, @Nullable final String xmlProduct,
			final boolean ignoreEmptyBundle) throws CommerceCartModificationException
	{
		if (bundleNo < -1)
		{
			throw new IllegalArgumentException("The bundleNo must not be lower then '-1', given bundleNo: " + bundleNo);
		}

		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(masterCartModel);
		parameter.setProduct(productModel);
		parameter.setBundleTemplate(bundleTemplateModel);
		parameter.setQuantity(quantityToAdd);
		parameter.setUnit(unit);
		parameter.setCreateNewEntry(forceNewEntry);
		parameter.setXmlProduct(xmlProduct);
		try
		{
			final Integer entryGroupNumber = getEntryGroupFor(masterCartModel, bundleNo, bundleTemplateModel);
			if (entryGroupNumber != null)
			{
				parameter.setEntryGroupNumbers(new HashSet<>(Collections.singletonList(entryGroupNumber)));
			}
		}
		catch (final IllegalArgumentException e)
		{
			throw new CommerceCartModificationException(e.getMessage(), e);
		}
		return addToCartWithoutCalculation(parameter);
	}

	@Nonnull
	protected Integer getNextFreeBundleNo(@Nonnull final AbstractOrderModel order)
	{
		return order.getEntries().stream().filter(e -> e.getBundleNo() != null).mapToInt(AbstractOrderEntryModel::getBundleNo)
				.max().orElse(0) + 1;
	}

	/**
	 * Run the addToCart in a local context where the session parameter CALCULATE_CART is set to false so that the cart
	 * is not yet calculated.
	 *
	 * @param parameter
	 *           product parameters
	 * @return modification info
	 * @throws CommerceCartModificationException
	 *            if the addition failed
	 */
	protected CommerceCartModification addToCartWithoutCalculation(final CommerceCartParameter parameter)
			throws CommerceCartModificationException
	{
		final Object result = getSessionService().executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public Object execute()
			{
				getSessionService().setAttribute(SESSION_ATTRIBUTE_CALCULATE_CART, Boolean.FALSE);
				try
				{
					return addToCart(parameter);
				}
				catch (final CommerceCartModificationException e)
				{
					return e;
				}
			}
		});

		if (result instanceof CommerceCartModificationException)
		{
			throw (CommerceCartModificationException) result;
		}
		else
		{
			return (CommerceCartModification) result;
		}
	}

	/**
	 * Restores entry group number by given component and bundle number.
	 *
	 * <p>
	 * For compatibility to the old version.
	 * </p>
	 *
	 * @param cartModel
	 *           cart to search in
	 * @param bundleNo
	 *           bundle id
	 * @param component
	 *           bundle component (should be a leaf)
	 * @return entry group number (can be null, e.g. for new bundles)
	 */
	protected Integer getEntryGroupFor(@Nonnull final AbstractOrderModel cartModel, final int bundleNo,
			final BundleTemplateModel component)
	{
		if (bundleNo == NO_BUNDLE || bundleNo == NEW_BUNDLE || component == null)
		{
			return null;
		}
		if (bundleNo < -1)
		{
			throw new IllegalArgumentException("The bundleNo must not be lower then '-1', given bundleNo: " + bundleNo);
		}
		final Collection<Integer> refGroupNumbers = cartModel
				.getEntries()
				.stream()
				.filter(e -> e.getBundleNo() != null)
				.filter(e -> e.getBundleNo() == bundleNo)
				.map(AbstractOrderEntryModel::getEntryGroupNumbers)
				.findAny()
				.orElseThrow(
						() -> new IllegalArgumentException("No entry for bundleNo=" + bundleNo + " in cart " + cartModel.getCode()));
		return cartModel
				.getEntryGroups()
				.stream()
				.filter(g -> GroupType.CONFIGURABLEBUNDLE.equals(g.getGroupType()))
				.map(getEntryGroupService()::getLeaves)
				.filter(leaves -> leaves.stream().anyMatch(g -> refGroupNumbers.contains(g.getGroupNumber())))
				.flatMap(Collection::stream)
				.filter(g -> component.getId().equals(g.getExternalReferenceId()))
				.map(EntryGroup::getGroupNumber)
				.findAny()
				.orElseThrow(
						() -> new IllegalArgumentException("Component " + getBundleTemplateService().getBundleTemplateName(component)
								+ " does not belong to any of the bundles of the cart " + cartModel.getCode()));
	}

	/**
	 * @deprecated Since ages (at least from 5.1)
	 */
	@Deprecated
	@Override
	@Nonnull
	public List<CommerceCartModification> addToCart(final CartModel masterCartModel, final UnitModel unit, final int bundleNo,
			final ProductModel productModel1, final BundleTemplateModel bundleTemplateModel1, final ProductModel productModel2,
			final BundleTemplateModel bundleTemplateModel2) throws CommerceCartModificationException
	{
		if (getSubscriptionProductService().isSubscription(productModel1)
				|| getSubscriptionProductService().isSubscription(productModel2))
		{
			throw new CommerceCartModificationException(
					"Method is deprecated for SubscriptionProducts. Use method public CommerceCartModification addToCart(CartModel masterCartModel, "
							+ "ProductModel productModel, long quantityToAdd, UnitModel unit, boolean forceNewEntry, String productXml) throws CommerceCartModificationException instead");
		}

		return addToCart(masterCartModel, unit, bundleNo, productModel1, bundleTemplateModel1, productModel2, bundleTemplateModel2,
				null, null);
	}

	@Override
	@Nonnull
	public List<CommerceCartModification> addToCart(@Nonnull final CartModel masterCartModel, @Nullable final UnitModel unit,
			final int bundleNo, @Nonnull final ProductModel productModel1, @Nonnull final BundleTemplateModel bundleTemplateModel1,
			@Nonnull final ProductModel productModel2, @Nonnull final BundleTemplateModel bundleTemplateModel2,
			@Nullable final String xmlProduct1, @Nullable final String xmlProduct2) throws CommerceCartModificationException
	{
		validateParameterNotNullStandardMessage("masterCartModel", masterCartModel);
		validateParameterNotNullStandardMessage("productModel1", productModel1);
		validateParameterNotNullStandardMessage("productModel2", productModel2);
		validateParameterNotNullStandardMessage("bundleTemplateModel1", bundleTemplateModel1);
		validateParameterNotNullStandardMessage("bundleTemplateModel2", bundleTemplateModel2);
		Preconditions.checkArgument(bundleNo != NO_BUNDLE, "Provided bundleNo '" + NO_BUNDLE
				+ "' is not allowed since the products must be added to a bundle");

		boolean isCartModified = false;
		CommerceCartModification modification1;
		CommerceCartModification modification2;
		List<CommerceCartModification> autoPicks;
		int newBundleNo = 0;
		final List<CommerceCartModification> modificationList = new ArrayList<CommerceCartModification>();

		if (bundleNo != NO_BUNDLE)
		{
			checkAutoPickAddToCart(bundleTemplateModel1, productModel1);
			checkAutoPickAddToCart(bundleTemplateModel2, productModel2);
		}

		// add the first product:
		modification1 = addProductToCart(masterCartModel, productModel1, BUNDLE_PRODUCT_QUANTITY, unit, true, bundleNo,
				bundleTemplateModel1, xmlProduct1, false);
		modificationList.add(modification1);

		if (modification1.getEntry() == null || modification1.getEntry().getBundleNo() == null)
		{
			newBundleNo = NEW_BUNDLE;
		}
		else
		{
			isCartModified = true;
			newBundleNo = modification1.getEntry().getBundleNo();
		}

		// add the second product:
		modification2 = addProductToCart(masterCartModel, productModel2, BUNDLE_PRODUCT_QUANTITY, unit, true, newBundleNo,
				bundleTemplateModel2, xmlProduct2, false);
		modificationList.add(modification2);

		if (modification2.getQuantityAdded() > 0 && modification2.getEntry() != null)
		{
			isCartModified = true;
			newBundleNo = modification2.getEntry().getBundleNo();
		}

		// add auto-pick products:
		if (bundleNo == NEW_BUNDLE && isCartModified)
		{
			autoPicks = addAutoPickProductsToCart(masterCartModel, newBundleNo, bundleTemplateModel1, unit);
			if (CollectionUtils.isNotEmpty(autoPicks))
			{
				modificationList.addAll(autoPicks);
			}
		}

		// Finally re-calculate the cart
		if (isCartModified)
		{
			calculateCart(masterCartModel);
		}

		updateLastModifiedEntriesList(masterCartModel, modificationList);

		return modificationList;
	}

	@Override
	public CommerceCartModification addToCart(final CartModel masterCartModel, final ProductModel productModel,
			final long quantityToAdd, final UnitModel unit, final boolean forceNewEntry) throws CommerceCartModificationException
	{
		return addToCart(masterCartModel, productModel, quantityToAdd, unit, forceNewEntry, null);
	}

	@Override
	public CommerceCartModification addToCart(@Nonnull final CartModel masterCartModel, @Nonnull final ProductModel productModel,
			final long quantityToAdd, final UnitModel unit, final boolean forceNewEntry, final String xmlProduct)
			throws CommerceCartModificationException
	{
		final CommerceCartModification modification = addProductToCart(masterCartModel, productModel, quantityToAdd, unit,
				forceNewEntry, NO_BUNDLE, null, xmlProduct, false);
		if (modification.getEntry() != null)
		{
			calculateCart(masterCartModel);
		}

		updateLastModifiedEntriesList(masterCartModel, Collections.singletonList(modification));

		return modification;
	}

	@Override
	public CommerceCartModification updateQuantityForCartEntry(final CommerceCartParameter parameter)
			throws CommerceCartModificationException
	{
		final CartModel masterCartModel = parameter.getCart();
		final long entryNumber = parameter.getEntryNumber();
		final long newQuantity = parameter.getQuantity();

		validateParameterNotNullStandardMessage("masterCartModel", masterCartModel);

		if (!isMasterCart(masterCartModel))
		{
			throw new IllegalArgumentException("Provided cart '" + masterCartModel.getCode() + "' is not a master cart");
		}

		int bundleNo = NO_BUNDLE;
		final AbstractOrderEntryModel cartEntryModel = getCartEntryToBeUpdated(masterCartModel, entryNumber);
		final BundleTemplateModel bundleTemplate = cartEntryModel.getBundleTemplate();

		// special handling for bundles
		if (bundleTemplate != null)
		{
			bundleNo = cartEntryModel.getBundleNo();

			// only allow updates to a certain quantity
			if (newQuantity < 0 || newQuantity > 1)
			{
				throw new CommerceCartModificationException("Product '" + cartEntryModel.getProduct().getCode()
						+ "' is part of bundle '" + cartEntryModel.getBundleTemplate().getId()
						+ "' and must have a new quantity of 0 or 1, quantity given: " + newQuantity);
			}

			checkAutoPickRemoval((CartEntryModel) cartEntryModel);
			if (newQuantity == 0)
			{
				checkSelectionCriteriaNotUnderThreshold((CartEntryModel) cartEntryModel);
			}
			checkAndRemoveDependentComponents(masterCartModel, bundleNo, bundleTemplate);
			checkIsComponentDependencyMetAfterRemoval(masterCartModel, cartEntryModel.getBundleTemplate(), bundleNo);

			// update the other bundle entries here as this cart entry will be gone after the quantity update
			setCartEntriesInSameBundleToNotCalculated(cartEntryModel);
		}

		final CommerceCartModification commerceCartModification = super.updateQuantityForCartEntry(parameter);

		if (bundleNo != NO_BUNDLE)
		{
			calculateCart(masterCartModel);
		}

		updateLastModifiedEntriesList(masterCartModel, Collections.singletonList(commerceCartModification));

		return commerceCartModification;

	}

	/**
	 * Sets all other cart entries that belong to the same bundle to "not calculated". As the prices within a bundle may
	 * vary dependent on the bundle entries a re-calculation of the whole bundle (and all the carts that contain entries
	 * of the affected bundle) is necessary.
	 */
	protected void setCartEntriesInSameBundleToNotCalculated(final AbstractOrderEntryModel sourceEntry)
	{
		Integer bundleNo;

		if (sourceEntry.getMasterEntry() == null)
		{
			bundleNo = sourceEntry.getBundleNo();
		}
		else
		{
			bundleNo = sourceEntry.getMasterEntry().getBundleNo();
		}

		if (bundleNo.intValue() == NO_BUNDLE)
		{
			return;
		}

		CartModel cart = (CartModel) sourceEntry.getOrder();
		if (!isMasterCart(cart))
		{
			cart = (CartModel) cart.getParent();
		}

		final List<AbstractOrderEntryModel> cartEntries = getBundleCartEntryDao().findEntriesByMasterCartAndBundleNo(cart,
				bundleNo.intValue());

		final List<AbstractOrderEntryModel> cartEntriesToChange = new ArrayList<AbstractOrderEntryModel>(cartEntries);
		for (final AbstractOrderEntryModel entry : cartEntries)
		{
			cartEntriesToChange.addAll(entry.getChildEntries());
		}

		for (final AbstractOrderEntryModel entry : cartEntriesToChange)
		{
			if (!entry.equals(sourceEntry))
			{
				entry.setCalculated(Boolean.FALSE);
				getModelService().save(entry);
				entry.getOrder().setCalculated(Boolean.FALSE);
				getModelService().save(entry.getOrder());
			}
		}
	}

	/**
	 * Checks whether the given <code>product</code> is already added to the given <code>bundleNo</code> and
	 * <code>component</code>. As it is not allowed to add the same product more than once to the same component an
	 * {@link CommerceCartModificationException} is thrown in case the product is already in the component.
	 */
	protected void checkIsProductAlreadyInComponent(final CartModel masterCart, final int bundleNo,
			final BundleTemplateModel bundleTemplate, final ProductModel product) throws CommerceCartModificationException
	{
		if (bundleNo != NO_BUNDLE)
		{

			final List<CartEntryModel> cartEntries = getBundleCartEntryDao().findEntriesByMasterCartAndBundleNoAndTemplate(
					masterCart, bundleNo, bundleTemplate);

			for (final CartEntryModel cartEntry : cartEntries)
			{
				if (cartEntry.getProduct().equals(product))
				{
					throw new CommerceCartModificationException("Product '" + product.getCode()
							+ "' is already in the cart for component (bundle template) '"
							+ (bundleTemplate.getName() == null ? bundleTemplate.getId() : bundleTemplate.getName()) + "' and bundleNo "
							+ bundleNo);
				}
			}
		}
	}

	/**
	 * Checks that not too many products are added to the given <code>bundleNo</code> within the given
	 * <code>component</code> (component) in the given <code>masterCart</code>. The check is based on the
	 * selectionCriteria of the given <code>component</code> which limit the max. possible product selections.
	 */
	protected void checkIsSelectionCriteriaNotExceeded(final CartModel masterCart, final BundleTemplateModel bundleTemplate,
			final int bundleNo) throws CommerceCartModificationException
	{
		if (bundleNo > NO_BUNDLE)
		{
			validateParameterNotNullStandardMessage("masterCart", masterCart);
			validateParameterNotNullStandardMessage("bundleTemplate", bundleTemplate);

			int maxItemsAllowed = 0;
			final BundleSelectionCriteriaModel selectionCriteria = bundleTemplate.getBundleSelectionCriteria();

			if (selectionCriteria instanceof PickNToMBundleSelectionCriteriaModel)
			{
				maxItemsAllowed = ((PickNToMBundleSelectionCriteriaModel) selectionCriteria).getM();
			}
			else if (selectionCriteria instanceof PickExactlyNBundleSelectionCriteriaModel)
			{
				maxItemsAllowed = ((PickExactlyNBundleSelectionCriteriaModel) selectionCriteria).getN().intValue();
			}
			else
			{
				return;
			}

			final List<CartEntryModel> bundleEntries = getBundleCartEntryDao().findEntriesByMasterCartAndBundleNoAndTemplate(
					masterCart, bundleNo, bundleTemplate);

			if (bundleEntries.size() >= maxItemsAllowed)
			{
				final String templateName = "'"
						+ (bundleTemplate.getName() == null ? bundleTemplate.getId() : bundleTemplate.getName()) + "'";
				final String message = getL10NService().getLocalizedString("bundleservices.validation.selectioncriteriaexceeded",
						new Object[]
						{ templateName, String.valueOf(maxItemsAllowed), String.valueOf(bundleEntries.size()) });
				throw new CommerceCartModificationException(message);
			}
		}
	}

	@Override
	public boolean checkIsEntryRemovable(@Nonnull final CartEntryModel cartEntry)
	{
		validateParameterNotNullStandardMessage("cartEntry", cartEntry);

		return getRemovableChecker().canRemove(cartEntry);
	}

	@Override
	public String checkAndGetReasonForNotRemovableEntry(@Nonnull final CartEntryModel cartEntry)
	{
		validateParameterNotNullStandardMessage("cartEntry", cartEntry);
		try
		{
			checkSelectionCriteriaNotUnderThreshold(cartEntry);
		}
		catch (final CommerceCartModificationException e)
		{
			LOG.debug("CommerceCartModificationException", e);
			return e.getLocalizedMessage() == null ? e.getMessage() : e.getLocalizedMessage();
		}
		return null;
	}

	protected void checkSelectionCriteriaNotUnderThreshold(final CartEntryModel cartEntry)
			throws CommerceCartModificationException
	{
		final boolean isEntryRemovable = checkIsEntryRemovable(cartEntry);

		if (!isEntryRemovable)
		{
			int minSelections;
			String message;
			final ProductModel product = cartEntry.getProduct();
			final BundleTemplateModel bundleTemplate = cartEntry.getBundleTemplate();
			final String templateName = "'" + (bundleTemplate.getName() == null ? bundleTemplate.getId() : bundleTemplate.getName())
					+ "'";
			final BundleSelectionCriteriaModel selectionCriteria = bundleTemplate.getBundleSelectionCriteria();

			if (selectionCriteria instanceof PickExactlyNBundleSelectionCriteriaModel)
			{
				minSelections = ((PickExactlyNBundleSelectionCriteriaModel) selectionCriteria).getN().intValue();
				message = getL10NService().getLocalizedString("bundleservices.validation.productnotremovable", new Object[]
				{ "'" + product.getName() + "'", String.valueOf(minSelections), templateName });
			}
			else
			{
				message = getL10NService().getLocalizedString("bundleservices.validation.productnotremovablesimple", new Object[]
				{ "'" + product.getName() + "'", templateName });
			}

			throw new CommerceCartModificationException(message);
		}
	}

	/**
	 * Checks whether the given <code>cartEntry</code> is an auto-pick item and throws an
	 * {@link CommerceCartModificationException} in that case
	 */
	protected void checkAutoPickRemoval(final CartEntryModel cartEntry) throws CommerceCartModificationException
	{
		validateParameterNotNullStandardMessage("cartEntry", cartEntry);
		final BundleTemplateModel bundleTemplate = cartEntry.getBundleTemplate();

		if (getBundleTemplateService().isAutoPickComponent(bundleTemplate))
		{
			throw new CommerceCartModificationException("Auto-pick product '" + cartEntry.getProduct().getCode()
					+ "' cannot be removed from bundle/cart via API call.");
		}
	}

	/**
	 * Checks whether the given <code>cartEntry</code> is an auto-pick item and throws an
	 * {@link CommerceCartModificationException} in that case
	 */
	protected void checkAutoPickAddToCart(final BundleTemplateModel bundleTemplate, final ProductModel product)
			throws CommerceCartModificationException
	{
		validateParameterNotNullStandardMessage("bundleTemplate", bundleTemplate);
		validateParameterNotNullStandardMessage("product", product);

		if (getBundleTemplateService().isAutoPickComponent(bundleTemplate))
		{
			throw new CommerceCartModificationException("Auto-pick product '" + product.getCode()
					+ "' cannot be added to bundle/cart via API call.");
		}
	}

	/**
	 * Add auto-pick products to the bundle in the cart. The root bundle template's components (= child templates) are
	 * investigated if their selection criteria type is auto-pick. If so, the component's products are automatically
	 * added to the bundle. The cart is not calculated here. Assumption is that there are no dependencies of the
	 * auto-pick components to each other or to other components in the bundle.
	 */
	protected List<CommerceCartModification> addAutoPickProductsToCart(final CartModel masterCartModel, final int bundleNo,
			final BundleTemplateModel bundleTemplate, final UnitModel unit) throws CommerceCartModificationException
	{
		final List<CommerceCartModification> modificationList = new ArrayList<>();
		for (final BundleTemplateModel leafComponent : getBundleTemplateService().getLeafComponents(bundleTemplate))
		{
			if (getBundleTemplateService().isAutoPickComponent(leafComponent))
			{
				for (final ProductModel autoPickProduct : leafComponent.getProducts())
				{
					final CommerceCartModification modification = addProductToCart(masterCartModel, autoPickProduct,
							BUNDLE_PRODUCT_QUANTITY, unit, true, bundleNo, leafComponent, null, false);
					modificationList.add(modification);
				}
			}
		}
		return modificationList;
	}

	@Override
	public void removeAllEntries(@Nonnull final CartModel masterCartModel, final int bundleNo)
			throws CommerceCartModificationException
	{
		validateParameterNotNullStandardMessage("masterCartModel", masterCartModel);

		int counter = 0;
		final BillingTimeModel masterCartBillFreqModel = getBillingTimeForCode(getMasterCartBillingTimeCode());

		checkMasterCart(masterCartModel, masterCartBillFreqModel);

		final List<CartEntryModel> cartEntries = getBundleCartEntryDao().findEntriesByMasterCartAndBundleNo(masterCartModel,
				bundleNo);

		final List<AbstractOrderEntryModel> cartEntriesToChange = new ArrayList<AbstractOrderEntryModel>(cartEntries);
		for (final AbstractOrderEntryModel entry : cartEntries)
		{
			cartEntriesToChange.addAll(entry.getChildEntries());
		}

		for (final AbstractOrderEntryModel entry : cartEntriesToChange)
		{
			removeCartEntry(masterCartModel, (CartEntryModel) entry);
			counter++;
		}

		if (counter == 0)
		{
			throw new CommerceCartModificationException("BundleNo " + bundleNo + " does not exist in multi-cart '"
					+ masterCartModel.getCode() + "'");
		}

		calculateCart(masterCartModel);

		updateLastModifiedEntriesList(masterCartModel, null);

	}

	/**
	 * If the last item of the given <code>bundleTemplate</code> is deleted all items that belong to components which are
	 * dependent on the given <code>bundleTemplate</code> are also removed from the cart. Only components that have a
	 * direct dependency on the given <code>bundleTemplate</code> are deleted. Components that have an indirect
	 * dependency (via a 3rd component) are not removed (this method has to be overridden to implement this feature).
	 */
	protected void checkAndRemoveDependentComponents(final CartModel masterCartModel, final int bundleNo,
			final BundleTemplateModel bundleTemplate)
	{
		validateParameterNotNullStandardMessage("masterCartModel", masterCartModel);
		validateParameterNotNullStandardMessage("bundleTemplate", bundleTemplate);

		final List<CartEntryModel> cartEntriesTemplate = getBundleCartEntryDao().findEntriesByMasterCartAndBundleNoAndTemplate(
				masterCartModel, bundleNo, bundleTemplate);
		if (cartEntriesTemplate.size() != 1)
		{
			return;
		}

		final List<BundleTemplateModel> bundleTemplates = getBundleTemplateService().getTemplatesForMasterOrderAndBundleNo(
				masterCartModel, bundleNo);
		bundleTemplates
				.stream()
				.filter(template -> template.getRequiredBundleTemplates().contains(bundleTemplate))
				.forEach(
						template -> {
							final List<CartEntryModel> cartEntries = getBundleCartEntryDao()
									.findEntriesByMasterCartAndBundleNoAndTemplate(masterCartModel, bundleNo, template);
							removeEntriesWithChildren(cartEntries, masterCartModel);
						});
	}

	protected void removeEntriesWithChildren(final List<CartEntryModel> cartEntries, final CartModel masterCartModel)
	{
		for (final CartEntryModel cartEntry : cartEntries)
		{
			if (CollectionUtils.isNotEmpty(cartEntry.getChildEntries()))
			{
				for (final AbstractOrderEntryModel childEntry : cartEntry.getChildEntries())
				{
					removeCartEntry(masterCartModel, (CartEntryModel) childEntry);
				}
			}
			removeCartEntry(masterCartModel, cartEntry);
		}

	}

	/**
	 * Removes a cart entry without performing any validation checks. The cart from which the entry is removed is not
	 * recalculated but flagged as not-calculated. In case the last entry of a child cart is removed the empty child cart
	 * is also deleted.
	 */
	protected void removeCartEntry(final CartModel masterCartModel, final CartEntryModel cartEntry)
	{
		validateParameterNotNullStandardMessage("masterCartModel", masterCartModel);
		validateParameterNotNullStandardMessage("cartEntry", cartEntry);

		final CartModel cartModel = cartEntry.getOrder();
		getModelService().remove(cartEntry);
		getModelService().refresh(cartModel);

		if (CollectionUtils.isNotEmpty(cartModel.getEntries()))
		{
			getSubscriptionCommerceCartStrategy().normalizeEntryNumbers(cartModel);
			if (Boolean.TRUE.equals(cartModel.getCalculated()))
			{
				cartModel.setCalculated(Boolean.FALSE);
				getModelService().save(cartModel);
			}
		}

		if (CollectionUtils.isEmpty(cartModel.getEntries()) && !isMasterCart(cartModel))
		{
			getModelService().remove(cartModel);
			getModelService().refresh(masterCartModel);
		}
	}

	/**
	 * Overrides the super class' method to make sure that in a multi-cart system the master cart and the child carts are
	 * calculated. The calculation is not triggered if the session attribute CALCULATE_CART exists and is set to
	 * Boolean.False
	 */
	@Override
	public boolean calculateCart(final CartModel cartModel)
	{
		validateParameterNotNullStandardMessage("cartModel", cartModel);
		final Boolean isCalculationRequired = getSessionService().getAttribute(SESSION_ATTRIBUTE_CALCULATE_CART);

		if (BooleanUtils.isNotFalse(isCalculationRequired))
		{
			super.calculateCart(cartModel);

			for (final AbstractOrderModel childCart : cartModel.getChildren())
			{
				super.calculateCart((CartModel) childCart);
			}
			return true;
		}
		else
		{
			return true;
		}
	}

	@Override
	public boolean checkIsEntryUpdateable(@Nonnull final CartEntryModel cartEntry)
	{
		validateParameterNotNullStandardMessage("cartEntry", cartEntry);
		return getOrderEntryModifiableChecker().canModify(cartEntry);
	}

	@Override
	public boolean checkIsComponentEditable(@Nonnull final CartModel masterCart,
			@Nullable final BundleTemplateModel bundleTemplate, final int bundleNo)
	{
		validateParameterNotNullStandardMessage("masterCart", masterCart);
		return getBundleComponentEditableChecker().canEdit(masterCart, bundleTemplate, bundleNo);
	}

	@Override
	public boolean checkIsComponentSelectionCriteriaMet(@Nonnull final CartModel masterCart,
			@Nullable final BundleTemplateModel bundleTemplate, final int bundleNo)
	{
		validateParameterNotNullStandardMessage("masterCart", masterCart);
		return getBundleComponentEditableChecker().isComponentSelectionCriteriaMet(masterCart, bundleTemplate, bundleNo);
	}

	/**
	 * Checks if an item can be removed from the given <code>component</code> (component). If the given
	 * <code>component</code> becomes invalid or would be removed completely (if last item is deleted), other components
	 * in the same bundle would also become invalid if they have a dependency on the given component. In that case an
	 * exception is thrown
	 */
	// NO SONAR
	protected void checkIsComponentDependencyMetAfterRemoval(final CartModel masterCart, final BundleTemplateModel bundleTemplate,
			final int bundleNo) throws CommerceCartModificationException
	{
		if (bundleTemplate == null || bundleNo <= NO_BUNDLE)
		{
			return;
		}
		// check if there are other components in the same bundle that are dependent on the given component
		final BundleTemplateModel dependentTemplate = findAnyRequiredTemplate(masterCart, bundleTemplate, bundleNo);
		if (dependentTemplate == null)
		{
			return;
		}

		// As there are dependencies to other components, check that the given component does not become invalid or
		// empty if an item is removed
		final List<CartEntryModel> entries = getBundleCartEntryDao().findEntriesByMasterCartAndBundleNoAndTemplate(masterCart,
				bundleNo, bundleTemplate);

		if (entries.size() == 1)
		{
			throw new CommerceCartModificationException("Cannot remove last product from given component '" + bundleTemplate.getId()
					+ "' as there is a dependency to component '" + dependentTemplate.getId() + "'.");
		}

		if (entries.size() > 1)
		{
			checkSelectionCriteriaNotUnderThreshold(entries.iterator().next());
		}

		// This should never happen
		if (entries.isEmpty())
		{
			throw new CommerceCartModificationException("Cannot find any entries for given component '" + bundleTemplate.getId()
					+ "'");
		}
	}

	protected BundleTemplateModel findAnyRequiredTemplate(final CartModel masterCart, final BundleTemplateModel bundleTemplate,
			final int bundleNo)
	{
		final List<BundleTemplateModel> templates = getBundleTemplateService().getTemplatesForMasterOrderAndBundleNo(masterCart,
				bundleNo);
		for (final BundleTemplateModel template : templates)
		{
			if (template.getRequiredBundleTemplates().contains(bundleTemplate))
			{
				return template;
			}
		}
		return null;
	}

	@Override
	@Nullable
	public String checkAndGetReasonForDisabledProductInComponent(@Nonnull final CartModel masterCart,
			@Nonnull final ProductModel product, @Nullable final BundleTemplateModel bundleTemplate, final int bundleNo,
			final boolean ignoreCurrentProducts)
	{

		if (bundleTemplate == null)
		{
			return null;
		}

		final String message = getDisablingReason(masterCart, product, bundleTemplate, bundleNo, ignoreCurrentProducts);
		if (message != null)
		{
			return message;
		}

		if (ignoreCurrentProducts)
		{
			return null;
		}

		return getSelectionCriterionMessage(masterCart, bundleTemplate, bundleNo);
	}

	protected String getSelectionCriterionMessage(@Nonnull final CartModel masterCart,
			@Nullable final BundleTemplateModel bundleTemplate, final int bundleNo)
	{
		try
		{
			checkIsSelectionCriteriaNotExceeded(masterCart, bundleTemplate, bundleNo);
			final boolean isDisabled = !getBundleComponentEditableChecker().canEdit(masterCart, bundleTemplate, bundleNo);
			if (isDisabled)
			{
				return getL10NService().getLocalizedString("bundleservices.validation.componentnoteditable",
						new Object[]
						{ getBundleTemplateService().getBundleTemplateName(bundleTemplate) });
			}
		}
		catch (final CommerceCartModificationException e)
		{
			LOG.debug("CommerceCartModificationException", e);
			return e.getLocalizedMessage() == null ? e.getMessage() : e.getLocalizedMessage();
		}
		return null;
	}

	protected String getDisablingReason(@Nonnull final CartModel masterCart, @Nonnull final ProductModel product,
			@Nonnull final BundleTemplateModel bundleTemplate, final int bundleNo, final boolean ignoreCurrentProducts)
	{
		final DisableProductBundleRuleModel disableRule = getBundleRuleService().getDisableRuleForBundleProduct(masterCart,
				product, bundleTemplate, bundleNo, ignoreCurrentProducts);

		if (disableRule != null)
		{
			return getBundleRuleService().createMessageForDisableRule(disableRule, product);
		}
		return null;
	}

	@Override
	@Nonnull
	public List<CartEntryModel> getCartEntriesForProductInBundle(@Nonnull final CartModel masterCart,
			@Nonnull final ProductModel product, final int bundleNo)
	{
		validateParameterNotNullStandardMessage("masterCart", masterCart);
		validateParameterNotNullStandardMessage("product", product);

		return getBundleCartEntryDao().findEntriesByMasterCartAndBundleNoAndProduct(masterCart, bundleNo, product);
	}

	@Override
	public List<CartEntryModel> getCartEntriesForComponentInBundle(@Nonnull final CartModel masterCart,
			@Nonnull final BundleTemplateModel component, final int bundleNo)
	{
		validateParameterNotNullStandardMessage("masterCart", masterCart);
		validateParameterNotNullStandardMessage("component", component);

		return getBundleCartEntryDao().findEntriesByMasterCartAndBundleNoAndTemplate(masterCart, bundleNo, component);
	}

	@Override
	public List<CartEntryModel> getCartEntriesForBundle(@Nonnull final CartModel masterCart, final int bundleNo)
	{
		validateParameterNotNullStandardMessage("masterCart", masterCart);

		return getBundleCartEntryDao().findEntriesByMasterCartAndBundleNo(masterCart, bundleNo);
	}

	@Override
	@Nullable
	public BundleTemplateModel getFirstInvalidComponentInCart(@Nonnull final CartModel masterCart)
	{
		validateParameterNotNullStandardMessage("masterCart", masterCart);

		final HashMap<Integer, BundleTemplateModel> rootTemplates = new HashMap<>();
		for (final AbstractOrderEntryModel entry : masterCart.getEntries())
		{
			if (entry.getBundleTemplate() != null && entry.getBundleTemplate().getParentTemplate() != null
					&& !rootTemplates.containsKey(entry.getBundleNo()))
			{
				rootTemplates.put(entry.getBundleNo(), getBundleTemplateService().getRootBundleTemplate(entry.getBundleTemplate()));
			}
		}

		for (final Entry<Integer, BundleTemplateModel> mapEntry : rootTemplates.entrySet())
		{
			final BundleTemplateModel parentTemplate = mapEntry.getValue();
			final int bundleNo = mapEntry.getKey().intValue();
			for (final BundleTemplateModel childTemplate : getBundleTemplateService().getLeafComponents(parentTemplate))
			{

				final boolean isDependencyMet = getBundleComponentEditableChecker().isComponentDependencyMet(masterCart,
						childTemplate, bundleNo);

				if (!isDependencyMet)
				{
					// ignore the component if the dependency on another component is not fulfilled,
					// which means that the component is not relevant
					continue;
				}

				final boolean isValid = getBundleComponentEditableChecker().isComponentSelectionCriteriaMet(masterCart,
						childTemplate, bundleNo);

				if (!isValid)
				{
					return childTemplate;
				}
			}
		}

		return null;
	}

	protected void updateLastModifiedEntriesList(final CartModel masterCart, final List<CommerceCartModification> modifications)
	{
		if (CollectionUtils.isNotEmpty(masterCart.getLastModifiedEntries()))
		{
			masterCart.setLastModifiedEntries(Collections.emptyList());
		}

		if (CollectionUtils.isNotEmpty(modifications))
		{
			final List<CartEntryModel> modifiedEntries = new ArrayList<CartEntryModel>();

			for (final CommerceCartModification mod : modifications)
			{
				if (mod.getQuantityAdded() != 0 && mod.getEntry() instanceof CartEntryModel && mod.getEntry().getQuantity() != null
						&& mod.getEntry().getQuantity() != 0)
				{
					modifiedEntries.add((CartEntryModel) mod.getEntry());
				}
			}

			masterCart.setLastModifiedEntries(modifiedEntries);
		}

		getModelService().save(masterCart);
	}

	@Required
	public void setBundleTemplateService(final BundleTemplateService bundleTemplateService)
	{
		this.bundleTemplateService = bundleTemplateService;
	}

	protected BundleTemplateService getBundleTemplateService()
	{
		return bundleTemplateService;
	}

	@Required
	public void setBundleCartEntryDao(final OrderEntryDao cartEntryDao)
	{
		this.bundleCartEntryDao = cartEntryDao;
	}

	protected OrderEntryDao getBundleCartEntryDao()
	{
		return bundleCartEntryDao;
	}

	protected RemoveableChecker<CartEntryModel> getRemovableChecker()
	{
		return removableChecker;
	}

	@Required
	public void setRemovableChecker(final RemoveableChecker<CartEntryModel> removableChecker)
	{
		this.removableChecker = removableChecker;
	}

	@Required
	public void setOrderEntryModifiableChecker(final ModifiableChecker<AbstractOrderEntryModel> orderEntryModifiableChecker)
	{
		this.orderEntryModifiableChecker = orderEntryModifiableChecker;
	}

	protected ModifiableChecker<AbstractOrderEntryModel> getOrderEntryModifiableChecker()
	{
		return orderEntryModifiableChecker;
	}

	@Required
	public void setBundleComponentEditableChecker(
			final AbstractBundleComponentEditableChecker<CartModel> bundleComponentEditableChecker)
	{
		this.bundleComponentEditableChecker = bundleComponentEditableChecker;
	}

	protected AbstractBundleComponentEditableChecker<CartModel> getBundleComponentEditableChecker()
	{
		return bundleComponentEditableChecker;
	}

	@Required
	public void setBundleRuleService(final BundleRuleService bundleRuleService)
	{
		this.bundleRuleService = bundleRuleService;
	}

	protected BundleRuleService getBundleRuleService()
	{
		return bundleRuleService;
	}

	@Override
	public BillingTimeModel getMasterBillingTime()
	{
		return this.getBillingTimeForCode(getMasterCartBillingTimeCode());
	}

	@Required
	public void setL10NService(final L10NService l10NService)
	{
		this.l10NService = l10NService;
	}

	protected L10NService getL10NService()
	{
		return l10NService;
	}

	@Override
	// NO SONAR - make it public again
	public SessionService getSessionService()
	{
		return super.getSessionService();
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
