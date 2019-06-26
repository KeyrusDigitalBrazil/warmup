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
package de.hybris.platform.selectivecartaddon.renderer.impl;

import de.hybris.platform.acceleratorcms.component.renderer.impl.CMSParagraphComponentRenderer;
import de.hybris.platform.cms2.model.contents.components.CMSParagraphComponentModel;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.selectivecartfacades.SelectiveCartFacade;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.jsp.PageContext;

import org.springframework.beans.factory.annotation.Required;


/**
 * Renders component when both cart and wish list are not null
 */
public class SelectiveCartParagraphComponentRenderer extends CMSParagraphComponentRenderer
{

	private static final String EMPTY_CART_PARAGRAPH_COMPONENT = "EmptyCartParagraphComponent";

	private CartFacade cartFacade;
	private SelectiveCartFacade selectiveCartFacade;

	@Override
	public void renderComponent(final PageContext pageContext, final CMSParagraphComponentModel component)
			throws ServletException, IOException
	{
		if (EMPTY_CART_PARAGRAPH_COMPONENT.equals(component.getUid()))
		{
			if (!getCartFacade().hasEntries() && getSelectiveCartFacade().getWishlistForSelectiveCart() == null)
			{
				super.renderComponent(pageContext, component);
			}
		}
		else
		{
			super.renderComponent(pageContext, component);
		}
	}


	protected CartFacade getCartFacade()
	{
		return cartFacade;
	}

	@Required
	public void setCartFacade(final CartFacade cartFacade)
	{
		this.cartFacade = cartFacade;
	}

	protected SelectiveCartFacade getSelectiveCartFacade()
	{
		return selectiveCartFacade;
	}

	@Required
	public void setSelectiveCartFacade(final SelectiveCartFacade selectiveCartFacade)
	{
		this.selectiveCartFacade = selectiveCartFacade;
	}

}
