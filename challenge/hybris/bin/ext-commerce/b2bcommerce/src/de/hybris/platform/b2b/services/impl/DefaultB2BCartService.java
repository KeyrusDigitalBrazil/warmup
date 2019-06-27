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
package de.hybris.platform.b2b.services.impl;

import de.hybris.platform.b2b.services.B2BCartService;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.impl.DefaultCartService;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.type.TypeService;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the {@link B2BCartService}
 *
 * @spring.bean b2bCartService
 */
public class DefaultB2BCartService extends DefaultCartService implements B2BCartService
{
	private static final long serialVersionUID = -2584723209363524073L;

	private transient TypeService typeService;
	protected transient KeyGenerator keyGenerator;

	@Override
	public CartModel createCartFromAbstractOrder(final AbstractOrderModel order)
	{

		return super.clone(getTypeService().getComposedTypeForClass(CartModel.class),
				getTypeService().getComposedTypeForClass(CartEntryModel.class), order, this.getKeyGenerator().generate().toString());
	}


	protected TypeService getTypeService()
	{
		return typeService;
	}

	@Required
	public void setTypeService(final TypeService typeService)
	{
		this.typeService = typeService;
	}

	protected KeyGenerator getKeyGenerator()
	{
		return keyGenerator;
	}

	@Required
	public void setKeyGenerator(final KeyGenerator keyGenerator)
	{
		this.keyGenerator = keyGenerator;
	}
}
