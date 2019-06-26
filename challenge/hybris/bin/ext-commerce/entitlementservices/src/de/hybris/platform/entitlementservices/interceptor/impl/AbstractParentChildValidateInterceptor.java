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
package de.hybris.platform.entitlementservices.interceptor.impl;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.entitlementservices.model.ProductEntitlementModel;
import de.hybris.platform.jalo.GenericItem;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Abstract interceptor for {@link de.hybris.platform.core.model.ItemModel}s
 * whose parent objects also need to be modified if they are modified. By
 * implementing the function onValidate as final, one can make sure that
 * {@link AbstractParentChildValidateInterceptor#markParentItemsAsModified} is called
 * after all validations in the sub-classes have successfully passed.
 */
public abstract class AbstractParentChildValidateInterceptor implements ValidateInterceptor
{
	private ModelService modelService;

	@Override
	final public void onValidate(final Object model, final InterceptorContext ctx) throws InterceptorException
	{
		if (model instanceof ItemModel)
		{
			// execute the validations in the sub-classes
			doValidate(model, ctx);

			// put this line to the very end of the method to make sure that the parent objects are only updated
			// if all validations have successfully passed
			markParentItemsAsModified((ItemModel) model);
		}
	}

	/**
	 * This function replaces the onValidate method in sub-classes
	 */
	protected abstract void doValidate(final Object model, final InterceptorContext ctx) throws InterceptorException;

	protected void markParentItemsAsModified(final ItemModel item)
	{
		final List<ItemModel> itemsToUpdate = new ArrayList<>();

		if (item instanceof ProductEntitlementModel)
		{
			final ProductEntitlementModel productEntitlement = (ProductEntitlementModel) item;
			itemsToUpdate.add(productEntitlement.getSubscriptionProduct());
		}

		markItemsAsModified(itemsToUpdate);
	}

	protected void markItemsAsModified(final List<ItemModel> items)
	{
		if (CollectionUtils.isNotEmpty(items))
		{
			final Date date = new Date();
			// Unfortunately the Jalo layer must be used here as there is no setter method
			// for the modification date in the service layer
			items.stream().filter(item -> item != null).forEach(item -> {
				// Unfortunately the Jalo layer must be used here as there is no setter method
				// for the modification date in the service layer
				final GenericItem genericItem = getModelService().getSource(item);
				if (genericItem != null) {
					genericItem.setProperty(GenericItem._MODIFIED_TIME_INTERNAL, date);
				}
			});
		}
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

}
