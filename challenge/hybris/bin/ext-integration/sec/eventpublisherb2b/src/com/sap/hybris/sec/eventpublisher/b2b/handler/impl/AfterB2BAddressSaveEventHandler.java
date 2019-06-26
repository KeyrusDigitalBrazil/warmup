package com.sap.hybris.sec.eventpublisher.b2b.handler.impl;

import java.io.IOException;

import com.sap.hybris.sec.eventpublisher.handler.impl.AfterAddressSaveEventHandler;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.tx.AfterSaveEvent;

public class AfterB2BAddressSaveEventHandler extends AfterAddressSaveEventHandler
{


	@Override
	public boolean shouldHandle(final AfterSaveEvent event, final ItemModel model) throws IOException {

		if (model instanceof AddressModel && (model.getOwner() instanceof B2BUnitModel
				|| model.getOwner() instanceof B2BCustomerModel)) {
			return true;
		} else {
			return super.shouldHandle(event, model);
		}

	}



}
