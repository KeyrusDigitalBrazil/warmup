/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.customerticketingfacades.converters.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.customerticketingfacades.constants.CustomerticketingfacadesConstants;
import de.hybris.platform.customerticketingfacades.data.TicketAssociatedData;


/**
 * This is used to populate the required data to display on the customer request tickets form.
 *
 */
public class DefaultTicketAssociationPopulator<SOURCE extends AbstractOrderModel, TARGET extends TicketAssociatedData>
		implements Populator<SOURCE, TARGET>
{

	@Override
	public void populate(final AbstractOrderModel source, final TicketAssociatedData target)
	{
		target.setCode(source.getCode());
		target.setModifiedtime(source.getModifiedtime());
		if (CartModel._TYPECODE.equals(source.getItemtype()))
		{
			target.setType(
					((CartModel) source).getSaveTime() != null ? CustomerticketingfacadesConstants.SAVED_CART : source.getItemtype());
		}
		else
		{
			target.setType(source.getItemtype());
		}

		if (source.getSite() != null)
		{
			target.setSiteUid(source.getSite().getUid());
		}
	}

}
