package de.hybris.platform.subscriptionservices.order.hook;

import de.hybris.platform.commerceservices.order.CommerceCartCalculationStrategy;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.hook.CommerceAddToCartMethodHook;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.subscriptionservices.model.BillingTimeModel;
import de.hybris.platform.subscriptionservices.model.SubscriptionTermModel;
import de.hybris.platform.subscriptionservices.subscription.BillingTimeService;
import de.hybris.platform.subscriptionservices.subscription.SubscriptionCommerceCartService;
import de.hybris.platform.subscriptionservices.subscription.SubscriptionProductService;

import org.springframework.beans.factory.annotation.Required;


public class SubscriptionAddToCartMethodHook implements CommerceAddToCartMethodHook
{
	private BillingTimeService billingTimeService;
	private SubscriptionCommerceCartService subscriptionCommerceCartService;
	private SubscriptionProductService subscriptionProductService;
	private ModelService modelService;
	private CartService cartService;
	private CommerceCartCalculationStrategy commerceCartCalculationStrategy;

	@Override
	public void beforeAddToCart(final CommerceCartParameter parameters) throws CommerceCartModificationException
	{
		ServicesUtil.validateParameterNotNullStandardMessage("parameters", parameters);

		final CartModel cartModel = parameters.getCart();
		ServicesUtil.validateParameterNotNullStandardMessage("parameters.cart", cartModel);
		final BillingTimeModel masterCartBillingTimeModel = getBillingTimeService().getBillingTimeForCode(
				getSubscriptionCommerceCartService().getMasterCartBillingTimeCode());
		getSubscriptionCommerceCartService().checkMasterCart(cartModel, masterCartBillingTimeModel);

		if (getSubscriptionProductService().isSubscription(parameters.getProduct()))
		{
			getSubscriptionCommerceCartService().checkQuantityToAdd(parameters.getQuantity());
			// always create a new entry for Subscription Products
			parameters.setCreateNewEntry(true);
		}
	}

	@Override
	public void afterAddToCart(final CommerceCartParameter parameters, final CommerceCartModification result)
			throws CommerceCartModificationException
	{
		if (result.getQuantityAdded() <= 0 || result.getEntry() == null || parameters.getProduct() == null
				|| !getSubscriptionProductService().isSubscription(parameters.getProduct()))
		{
			return;
		}

		final AbstractOrderEntryModel masterCartEntry = result.getEntry();

		createChildCarts(masterCartEntry, parameters);

		final SubscriptionTermModel subscriptionTerm = parameters.getProduct().getSubscriptionTerm();
		if (subscriptionTerm != null)
		{
			masterCartEntry.setXmlProduct(parameters.getXmlProduct());
			getModelService().save(masterCartEntry);
		}
	}

	protected void createChildCarts(final AbstractOrderEntryModel masterCartEntry, final CommerceCartParameter masterCartParameters)
			throws CommerceCartModificationException
	{
		final CartModel masterCartModel = (CartModel) masterCartEntry.getOrder();
		final BillingTimeModel masterCartBillingTimeModel = getBillingTimeService().getBillingTimeForCode(
				getSubscriptionCommerceCartService().getMasterCartBillingTimeCode());

		for (final BillingTimeModel billingTime : getSubscriptionCommerceCartService().getBillingFrequenciesForMasterEntry(
				masterCartEntry))
		{
			if (!masterCartBillingTimeModel.equals(billingTime) && Boolean.TRUE.equals(billingTime.getCartAware()))
			{
				// Special handling for remaining subscription products
				// (product's billing frequency <> master cart's billing frequency):
				// add the subscription product to the child cart with the same billing frequency
				CartModel childCart = getSubscriptionCommerceCartService().getChildCartForBillingTime(masterCartModel, billingTime);
				if (childCart == null)
				{
					childCart = getSubscriptionCommerceCartService().createChildCartForBillingTime(masterCartModel, billingTime);
				}

				final CartEntryModel childCartEntry = getCartService().addNewEntry(childCart,
						masterCartParameters.getProduct(), masterCartParameters.getQuantity(), masterCartParameters.getUnit(), -1,
						false);

				childCartEntry.setMasterEntry(masterCartEntry);
				getModelService().save(childCartEntry);
				getModelService().refresh(masterCartEntry);

				final CommerceCartParameter childCartParameters = createChildCommerceCartParameter(masterCartParameters, childCart);
				getCommerceCartCalculationStrategy().calculateCart(childCartParameters);
			}
		}
	}

	protected CommerceCartParameter createChildCommerceCartParameter(final CommerceCartParameter masterCartParameters,
			final CartModel childCart)
	{
		final CommerceCartParameter childCartParameters = new CommerceCartParameter();
		childCartParameters.setCart(childCart);
		childCartParameters.setCreateNewEntry(masterCartParameters.isCreateNewEntry());
		childCartParameters.setProduct(masterCartParameters.getProduct());
		childCartParameters.setQuantity(masterCartParameters.getQuantity());
		childCartParameters.setUnit(masterCartParameters.getUnit());
		childCartParameters.setEnableHooks(false);
		return childCartParameters;
	}

	protected SubscriptionCommerceCartService getSubscriptionCommerceCartService()
	{
		return subscriptionCommerceCartService;
	}

	@Required
	public void setSubscriptionCommerceCartService(final SubscriptionCommerceCartService subscriptionCommerceCartService)
	{
		this.subscriptionCommerceCartService = subscriptionCommerceCartService;
	}

	protected BillingTimeService getBillingTimeService()
	{
		return billingTimeService;
	}

	@Required
	public void setBillingTimeService(final BillingTimeService billingTimeService)
	{
		this.billingTimeService = billingTimeService;
	}

	protected SubscriptionProductService getSubscriptionProductService()
	{
		return subscriptionProductService;
	}

	@Required
	public void setSubscriptionProductService(final SubscriptionProductService subscriptionProductService)
	{
		this.subscriptionProductService = subscriptionProductService;
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

	protected CartService getCartService()
	{
		return cartService;
	}

	@Required
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
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
