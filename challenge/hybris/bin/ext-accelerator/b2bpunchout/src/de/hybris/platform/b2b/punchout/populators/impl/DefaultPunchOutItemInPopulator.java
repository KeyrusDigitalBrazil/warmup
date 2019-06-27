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
package de.hybris.platform.b2b.punchout.populators.impl;

import de.hybris.platform.b2b.punchout.enums.PunchOutClassificationDomain;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import org.cxml.Classification;
import org.cxml.Description;
import org.cxml.ItemDetail;
import org.cxml.ItemID;
import org.cxml.ItemIn;
import org.cxml.Money;
import org.cxml.SupplierPartID;
import org.cxml.UnitPrice;
import org.springframework.beans.factory.annotation.Required;


/**
 * Converts a {@link OrderEntryModel} into a {@link ItemIn}.
 */
public class DefaultPunchOutItemInPopulator implements Populator<AbstractOrderEntryModel, ItemIn>
{
	private CommonI18NService commonI18NService;

	@Override
	public void populate(final AbstractOrderEntryModel orderEntry, final ItemIn itemIn) throws ConversionException
	{

		itemIn.setItemID(new ItemID());
		itemIn.getItemID().setSupplierPartID(new SupplierPartID());
		itemIn.setQuantity(String.valueOf(orderEntry.getQuantity()));

		itemIn.getItemID().getSupplierPartID().setvalue(orderEntry.getProduct().getCode());
		final ItemDetail itemDetail = new ItemDetail();
		final UnitPrice unitPrice = new UnitPrice();
		final Money money = new Money();
		money.setCurrency(getCommonI18NService().getCurrentCurrency().getIsocode());
		money.setvalue(String.valueOf(orderEntry.getBasePrice()));

		itemDetail.setUnitPrice(unitPrice);
		unitPrice.setMoney(money);

		final Description description = new Description();
		description.setvalue(orderEntry.getProduct().getDescription());
		description.setXmlLang(getCommonI18NService().getCurrentLanguage().getIsocode());
		itemDetail.getDescription().add(description);

		itemDetail.setUnitOfMeasure(orderEntry.getProduct().getUnitOfMeasure());

		final Classification classification = new Classification();
		classification.setDomain(PunchOutClassificationDomain.UNSPSC.getCode());

		classification.setvalue(orderEntry.getProduct().getUnspcs());
		itemDetail.getClassification().add(classification);

		itemIn.setItemDetail(itemDetail);
	}

	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}


	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

}
