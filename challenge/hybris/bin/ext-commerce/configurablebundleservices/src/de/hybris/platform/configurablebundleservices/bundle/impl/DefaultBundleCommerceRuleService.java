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

import de.hybris.platform.configurablebundleservices.bundle.BundleRuleService;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.constants.ConfigurableBundleServicesConstants;
import de.hybris.platform.configurablebundleservices.daos.BundleRuleDao;
import de.hybris.platform.configurablebundleservices.daos.ChangeProductPriceBundleRuleDao;
import de.hybris.platform.configurablebundleservices.daos.OrderEntryDao;
import de.hybris.platform.configurablebundleservices.enums.BundleRuleTypeEnum;
import de.hybris.platform.configurablebundleservices.model.AbstractBundleRuleModel;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.configurablebundleservices.model.ChangeProductPriceBundleRuleModel;
import de.hybris.platform.configurablebundleservices.model.DisableProductBundleRuleModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.order.EntryGroup;
import de.hybris.platform.order.EntryGroupService;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the bundle rule service {@link BundleRuleService}. It searches for the the lowest price
 * (based on bundle price rules) for a product that is a part of a bundle.
 */
public class DefaultBundleCommerceRuleService implements BundleRuleService
{
	private ChangeProductPriceBundleRuleDao changeProductPriceBundleRuleDao;
	private BundleRuleDao disableProductBundleRuleDao;
	private BundleTemplateService bundleTemplateService;
	private OrderEntryDao cartEntryDao;
	private SearchRestrictionService searchRestrictionService;
	private ModelService modelService;
	private SessionService sessionService;
	private EntryGroupService entryGroupService;
	private L10NService l10NService;

	@Override
	@Nullable
	public ChangeProductPriceBundleRuleModel getChangePriceBundleRuleForOrderEntry(@Nonnull final AbstractOrderEntryModel entry)
	{
		validateParameterNotNullStandardMessage("entry", entry);
		final EntryGroup bundleEntryGroup = getBundleTemplateService().getBundleEntryGroup(entry);
		validateParameterNotNull(bundleEntryGroup, "Cart entry model does not have a bundle template");

		final AbstractOrderModel order = entry.getOrder();
		final CurrencyModel currency = order.getCurrency();
		final ProductModel targetProduct = entry.getProduct();
		final Set<ProductModel> otherProductsInSameBundle = getCartProductsInSameBundle(order, targetProduct, bundleEntryGroup);

		final BundleTemplateModel bundleTemplate = getBundleTemplateService().getBundleTemplateForCode(
				bundleEntryGroup.getExternalReferenceId());
		return getLowestPriceForTargetProductAndTemplate(bundleTemplate, targetProduct, currency, otherProductsInSameBundle);
	}

	@Override
	public List<DisableProductBundleRuleModel> getDisableProductBundleRules(final ProductModel product,
			final EntryGroup entryGroup, final AbstractOrderModel order)
	{
		final Set<ProductModel> otherProductsInSameBundle = getCartProductsInSameBundle(order, product, entryGroup);
		if (CollectionUtils.isEmpty(otherProductsInSameBundle))
		{
			return Collections.emptyList();
		}

		final BundleTemplateModel bundleTemplate = getBundleTemplateService().getBundleTemplateForCode(
				entryGroup.getExternalReferenceId());
		if (CollectionUtils.isEmpty(bundleTemplate.getDisableProductBundleRules()))
		{
			return Collections.emptyList();
		}
		return bundleTemplate.getDisableProductBundleRules().stream()
				.filter(r -> r.getTargetProducts().contains(product))
				.filter(r -> !Collections.disjoint(r.getConditionalProducts(), otherProductsInSameBundle))
				.collect(Collectors.toList());
	}

	@Override
	@Nullable
	public ChangeProductPriceBundleRuleModel getChangePriceBundleRule(@Nonnull final BundleTemplateModel targetComponent,
			@Nonnull final ProductModel targetProduct, @Nonnull final ProductModel conditionalProduct,
			@Nonnull final CurrencyModel currency)
	{
		validateParameterNotNullStandardMessage("targetComponent", targetComponent);
		validateParameterNotNullStandardMessage("targetProduct", targetProduct);
		validateParameterNotNullStandardMessage("conditionalProduct", conditionalProduct);
		validateParameterNotNullStandardMessage("currency", currency);

		final Set<ProductModel> productInSameBundle = new HashSet<ProductModel>();
		productInSameBundle.add(conditionalProduct);

		return getLowestPriceForTargetProductAndTemplate(targetComponent, targetProduct, currency, productInSameBundle);
	}

	@Override
	@Nullable
	public ChangeProductPriceBundleRuleModel getChangePriceBundleRule(@Nonnull final AbstractOrderModel masterAbstractOrder,
			@Nonnull final BundleTemplateModel bundleTemplate, @Nonnull final ProductModel targetProduct, final int bundleNo)
	{
		validateParameterNotNullStandardMessage("masterAbstractOrder", masterAbstractOrder);
		validateParameterNotNullStandardMessage("bundleTemplate", bundleTemplate);
		validateParameterNotNullStandardMessage("targetProduct", targetProduct);

		final Set<ProductModel> otherProductsInSameBundle = getCartProductsInSameBundle(masterAbstractOrder, null, bundleNo);

		return getLowestPriceForTargetProductAndTemplate(bundleTemplate, targetProduct, masterAbstractOrder.getCurrency(),
				otherProductsInSameBundle);
	}


	@Override
	@Nullable
	public ChangeProductPriceBundleRuleModel getChangePriceBundleRuleWithLowestPrice(@Nonnull final ProductModel targetProduct,
			@Nonnull final CurrencyModel currency)
	{
		validateParameterNotNullStandardMessage("targetProduct", targetProduct);
		validateParameterNotNullStandardMessage("currency", currency);

		ChangeProductPriceBundleRuleModel lowestPriceRule = null;

		final List<ChangeProductPriceBundleRuleModel> priceRules = getChangeProductPriceBundleRuleDao()
				.findBundleRulesByTargetProductAndCurrency(targetProduct, currency);

		for (final ChangeProductPriceBundleRuleModel priceRule : priceRules)
		{
			if (priceRule.getBundleTemplate() != null
					&& (lowestPriceRule == null || priceRule.getPrice().compareTo(lowestPriceRule.getPrice()) < 0))
			{
				lowestPriceRule = priceRule;
			}
		}

		return lowestPriceRule;
	}

	/**
	 * Finds the {@link ChangeProductPriceBundleRuleModel} with the lowest price for the given <code>targetProduct</code>
	 * . All {@link ChangeProductPriceBundleRuleModel}s which are assigned to the given <code>bundleTemplate</code> and
	 * have the given <code>targetProduct</code> as target product and meet the requirements for conditional products are
	 * selected. The prices of these rules are then evaluated: The {@link ChangeProductPriceBundleRuleModel} that matches
	 * the given <code>currency</code> and has the lowest price, is returned.
	 *
	 */
	@Nullable
	protected ChangeProductPriceBundleRuleModel getLowestPriceForTargetProductAndTemplate(
			final BundleTemplateModel bundleTemplate, final ProductModel targetProduct, final CurrencyModel currency,
			final Set<ProductModel> otherProductsInSameBundle)
	{
		ChangeProductPriceBundleRuleModel lowestPriceRule = null;

		final List<ChangeProductPriceBundleRuleModel> priceRules = getChangeProductPriceBundleRuleDao()
				.findBundleRulesByTargetProductAndTemplateAndCurrency(targetProduct, bundleTemplate, currency);

		for (final ChangeProductPriceBundleRuleModel priceRule : priceRules)
		{
			final boolean isRuleApplicable = checkBundleRuleForTargetProduct(priceRule, otherProductsInSameBundle);

			if (isRuleApplicable && (lowestPriceRule == null || priceRule.getPrice().compareTo(lowestPriceRule.getPrice()) < 0))
			{
				lowestPriceRule = priceRule;
			}
		}

		return lowestPriceRule;
	}

	@Override
	@Nullable
	public DisableProductBundleRuleModel getDisableRuleForBundleProduct(@Nonnull final AbstractOrderModel masterAbstractOrder,
			@Nonnull final ProductModel product, @Nonnull final BundleTemplateModel bundleTemplate, final int bundleNo,
			final boolean ignoreCurrentProducts)
	{
		validateParameterNotNullStandardMessage("masterAbstractOrder", masterAbstractOrder);
		validateParameterNotNullStandardMessage("product", product);
		validateParameterNotNullStandardMessage("bundleTemplate", bundleTemplate);

		final BundleTemplateModel rootTemplate = getBundleTemplateService().getRootBundleTemplate(bundleTemplate);
		final List<BundleTemplateModel> leafTemplates = getBundleTemplateService().getLeafComponents(rootTemplate);
		final List<AbstractBundleRuleModel> disableRules = leafTemplates.stream()
				.map(BundleTemplateModel::getParentTemplate)
				.filter(item -> item != null)
				.map(parent -> getDisableProductBundleRuleDao().findBundleRulesByProductAndRootTemplate(product, parent))
				.map(rule -> (List<AbstractBundleRuleModel>) rule)
				.flatMap(Collection::<DisableProductBundleRuleModel>stream)
				.collect(Collectors.toList());

		if (disableRules.isEmpty())
		{
			return null;
		}

		final Set<ProductModel> otherProductsInSameBundle = getCartProductsInSameBundle(masterAbstractOrder, product, bundleNo);

		if (ignoreCurrentProducts)
		{
			final List<CartEntryModel> ignoreCartEntries = getCartEntryDao().findEntriesByMasterCartAndBundleNoAndTemplate(
					masterAbstractOrder, bundleNo, bundleTemplate);
			for (final CartEntryModel ignoreCartEntry : ignoreCartEntries)
			{
				otherProductsInSameBundle.remove(ignoreCartEntry.getProduct());
			}
		}

		final AbstractBundleRuleModel bundleRule = evaluateBundleRules(disableRules, product, otherProductsInSameBundle);
		return (DisableProductBundleRuleModel) bundleRule;
	}

	@Override
	@Nullable
	public DisableProductBundleRuleModel getDisableRuleForBundleProduct(@Nonnull final BundleTemplateModel bundleTemplate,
			@Nonnull final ProductModel product1,@Nonnull final ProductModel product2)
	{
		validateParameterNotNullStandardMessage("targetComponent", bundleTemplate);
		validateParameterNotNullStandardMessage("product1", product1);
		validateParameterNotNullStandardMessage("product2", product2);

		final BundleTemplateModel rootTemplate = getBundleTemplateService().getRootBundleTemplate(bundleTemplate);
		final List<BundleTemplateModel> leafTemplates = getBundleTemplateService().getLeafComponents(rootTemplate);
		final List<AbstractBundleRuleModel> disableRules = leafTemplates.stream()
				.map(BundleTemplateModel::getParentTemplate)
				.filter(parent -> parent != null)
				.distinct()
				.map(parent -> getDisableProductBundleRuleDao().findBundleRulesByProductAndRootTemplate(product1, parent))
				.map(rule -> (List<AbstractBundleRuleModel>) rule)
				.flatMap(Collection::stream)
				.collect(Collectors.toList());
		if (disableRules.isEmpty())
		{
			return null;
		}

		final Set<ProductModel> productsInSameBundle = new HashSet<>();
		productsInSameBundle.add(product2);

		final AbstractBundleRuleModel bundleRule = evaluateBundleRules(disableRules, product1, productsInSameBundle);
		return (DisableProductBundleRuleModel) bundleRule;
	}

	/**
	 * Evaluates if any of the given <code>bundleRules</bundleRules> is applicable for the given <code>product</code> in
	 * the context of the list of other products that are already in the same bundle. Depending on whether the given
	 * <code>product</code> is a target or a conditional product of the bundleRule the check itself is done either in
	 * method checkBundleRuleForTargetProduct or method checkBundleRuleForConditionalProduct.
	 */
	protected AbstractBundleRuleModel evaluateBundleRules(final List<AbstractBundleRuleModel> bundleRules,
			final ProductModel product, final Set<ProductModel> otherProductsInSameBundle)
	{
		for (final AbstractBundleRuleModel disableRule : bundleRules)
		{
			if (disableRule.getTargetProducts() != null && disableRule.getTargetProducts().contains(product))
			{
				final boolean isRuleApplicable = checkBundleRuleForTargetProduct(disableRule, otherProductsInSameBundle);
				if (isRuleApplicable)
				{
					return disableRule;
				}
			}
			else if (disableRule.getConditionalProducts() != null && disableRule.getConditionalProducts().contains(product))
			{
				final boolean isRuleApplicable = checkBundleRuleForConditionalProduct(disableRule, otherProductsInSameBundle, product);
				if (isRuleApplicable)
				{
					return disableRule;
				}
			}
		}

		return null;
	}

	/**
	 * Returns a list of of products that belong to the same bundle in the given multi-cart as the given
	 * <code>product</code>
	 *
	 * @deprecated since 6.5 - bundleNo is deprecated, use
	 *             {@link #getCartProductsInSameBundle(AbstractOrderModel, ProductModel, EntryGroup)}
	 */
	@Deprecated
	protected Set<ProductModel> getCartProductsInSameBundle(final AbstractOrderModel masterAbstractOrder,
			final ProductModel product, final int bundleNo)
	{
		final Set<ProductModel> productInSameBundle = new HashSet<ProductModel>();

		if (bundleNo != ConfigurableBundleServicesConstants.NO_BUNDLE)
		{
			final List<AbstractOrderEntryModel> abstractOrderEntries = getCartEntryDao().findEntriesByMasterCartAndBundleNo(
					masterAbstractOrder, bundleNo);

			for (final AbstractOrderEntryModel entry : abstractOrderEntries)
			{
				if (!entry.getProduct().equals(product))
				{
					productInSameBundle.add(entry.getProduct());
				}
			}
		}
		return productInSameBundle;
	}

	/**
	 * Returns a list of of products that belong to the same bundle entry group in the given multi-cart as the given
	 * <code>product</code>
	 */
	protected Set<ProductModel> getCartProductsInSameBundle(final AbstractOrderModel order,
		final ProductModel product, final EntryGroup bundleEntryGroup)
	{
		final EntryGroup rootEntryGroup = getEntryGroupService().getRoot(order, bundleEntryGroup.getGroupNumber());
		final List<Integer> entryGroupNumbers = getEntryGroupService().getLeaves(rootEntryGroup).stream()
				.map(EntryGroup::getGroupNumber)
				.collect(Collectors.toList());
		final List<AbstractOrderEntryModel> abstractOrderEntries = order.getEntries().stream()
				.filter(entry -> CollectionUtils.containsAny(entryGroupNumbers, entry.getEntryGroupNumbers()))
				.collect(Collectors.toList());

		return abstractOrderEntries.stream()
				.filter(entry -> !entry.getProduct().equals(product))
				.map(AbstractOrderEntryModel::getProduct)
				.collect(Collectors.toSet());
	}

	/**
	 * Applies the rules for conditional products to check if the given <code>rule</code> meets the requirements. Returns
	 * <code>true</code> if the given list of products <code>otherProductsInSameBundle</code> matches the list of
	 * conditional products of the given <code>rule</code>, otherwise <code>false</code>.
	 *
	 */
	protected boolean checkBundleRuleForTargetProduct(final AbstractBundleRuleModel rule,
			final Set<ProductModel> otherProductsInSameBundle)
	{
		boolean isRuleApplicable = false;

		// Search restrictions may hide some entries on the rule's list of conditional products.
		// Here we temporary switch them off to get the list originally expected.
		final Collection<ProductModel> fullConditionalProductList = unrestricted(
				rule,
				model -> Collections.unmodifiableCollection(model.getConditionalProducts())
		);

		// if it is a price rule which has no conditional products it shall always fire
		if (rule instanceof ChangeProductPriceBundleRuleModel && fullConditionalProductList.isEmpty())
		{
			isRuleApplicable = true;
		}
		// ANY is the default rule type: so if the rule has no rule type, process it like ANY
		else if (rule.getRuleType() == null || BundleRuleTypeEnum.ANY.equals(rule.getRuleType()))
		{
			final Set<ProductModel> intersection = new HashSet<>(otherProductsInSameBundle);
			intersection.retainAll(fullConditionalProductList);
			if (!intersection.isEmpty())
			{
				isRuleApplicable = true;
			}
		}
		else if (BundleRuleTypeEnum.ALL.equals(rule.getRuleType())
				&& otherProductsInSameBundle.containsAll(fullConditionalProductList))
		{
			isRuleApplicable = true;
		}

		return isRuleApplicable;
	}

	/**
	 * Applies the check logic for disable product rules to test if the given <code>bundleRule</code> meets the
	 * requirements. Returns <code>true</code> if the given <code>bundleRule</code> is of type "ALL" and the given
	 * <code>product</code> is listed as a conditional product, all other conditional products and at least one of the
	 * target products are already added to the bundle in the cart. It also returns <code>true</code> if the given
	 * <code>bundleRule</code> is of type "ANY" and the given <code>product</code> is listed as a conditional product and
	 * at least one of the target products is already added to the bundle in the cart. In all other cases it returns
	 * <code>false</code>. It is assumed that the given <code>product</code> is in the list of conditional products of
	 * the given <code>bundleRule</code>.
	 *
	 */
	protected boolean checkBundleRuleForConditionalProduct(final AbstractBundleRuleModel bundleRule,
			final Set<ProductModel> otherProductsInSameBundle, final ProductModel product)
	{
		if (bundleRule.getConditionalProducts() == null || !bundleRule.getConditionalProducts().contains(product))
		{
			return false;
		}
		// check that at least 1 of the rule's target products is in the cart already
		final Set<ProductModel> targetIntersection = new HashSet(otherProductsInSameBundle);
		targetIntersection.retainAll(bundleRule.getTargetProducts());

		return !targetIntersection.isEmpty() && checkRuleIsFulfilled(bundleRule, product, otherProductsInSameBundle);
	}

	protected boolean checkRuleIsFulfilled(final AbstractBundleRuleModel bundleRule, final ProductModel product,
			final Set<ProductModel> otherProductsInSameBundle)
	{
		if (bundleRule.getRuleType() == null || BundleRuleTypeEnum.ANY.equals(bundleRule.getRuleType()))
		{
			return true;
		}
		else if (BundleRuleTypeEnum.ALL.equals(bundleRule.getRuleType()))
		{
			final List<ProductModel> conditionalProducts = new ArrayList<>(bundleRule.getConditionalProducts());
			conditionalProducts.remove(product);
			if (otherProductsInSameBundle.containsAll(conditionalProducts))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Executes {@code getter} in search-restrictions-off context.
	 *
	 * @param model
	 *           persistent object to get data from
	 * @param getter
	 *           data obtaining logic
	 * @param <A>
	 *           type of the object
	 * @param <R>
	 *           type of the data
	 * @return data collected in restriction-free content
	 */
	protected <A extends ItemModel, R> R unrestricted(final A model, final Function<A, R> getter)
	{
		final R result;
		if (getSearchRestrictionService().isSearchRestrictionsEnabled())
		{
			result = getSessionService().executeInLocalView(new SessionExecutionBody()
			{
				@Override
				public Object execute()
				{
					try
					{
						getSearchRestrictionService().disableSearchRestrictions();
						getModelService().refresh(model);
						return getter.apply(model);
					}
					finally
					{
						getSearchRestrictionService().enableSearchRestrictions();
						getModelService().refresh(model);
					}
				}
			});
		}
		else
		{
			result = getter.apply(model);
		}
		return result;
	}

	@Override
	public String createMessageForDisableRule(final DisableProductBundleRuleModel disableRule, final ProductModel product)
	{
		if (disableRule == null || product == null)
		{
			return "";
		}
		
		final StringBuilder productsBuffer = new StringBuilder();
		for (final ProductModel curProduct : disableRule.getConditionalProducts())
		{
			if (productsBuffer.length() == 0)
			{
				productsBuffer.append("'").append(curProduct.getName()).append("'");
			}
			else
			{
				productsBuffer.append(", '").append(curProduct.getName()).append("'");
			}
		}

		return getL10NService().getLocalizedString("bundleservices.validation.disableruleexists", new Object[]
		{ "'" + product.getName() + "'", productsBuffer.toString() });
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

	protected ChangeProductPriceBundleRuleDao getChangeProductPriceBundleRuleDao()
	{
		return changeProductPriceBundleRuleDao;
	}

	@Required
	public void setChangeProductPriceBundleRuleDao(final ChangeProductPriceBundleRuleDao changeProductPriceBundleRuleDao)
	{
		this.changeProductPriceBundleRuleDao = changeProductPriceBundleRuleDao;
	}

	protected BundleRuleDao getDisableProductBundleRuleDao()
	{
		return disableProductBundleRuleDao;
	}

	@Required
	public void setDisableProductBundleRuleDao(final BundleRuleDao disableProductBundleRuleDao)
	{
		this.disableProductBundleRuleDao = disableProductBundleRuleDao;
	}

	protected OrderEntryDao getCartEntryDao()
	{
		return cartEntryDao;
	}

	@Required
	public void setCartEntryDao(final OrderEntryDao cartEntryDao)
	{
		this.cartEntryDao = cartEntryDao;
	}

	protected SearchRestrictionService getSearchRestrictionService()
	{
		return searchRestrictionService;
	}

	@Required
	public void setSearchRestrictionService(final SearchRestrictionService searchRestrictionService)
	{
		this.searchRestrictionService = searchRestrictionService;
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

	protected SessionService getSessionService()
	{
		return sessionService;
	}

	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
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

	protected L10NService getL10NService()
	{
		return l10NService;
	}

	@Required
	public void setL10NService(final L10NService l10nService)
	{
		l10NService = l10nService;
	}
}
