package com.sap.hybris.sec.eventpublisher.b2b.publisher.impl;

import static com.sap.hybris.sec.eventpublisher.b2b.constants.Eventpublisherb2bConstants.B2BUNIT;
import static com.sap.hybris.sec.eventpublisher.b2b.constants.Eventpublisherb2bConstants.B2B_UNIT_PATH;
import static com.sap.hybris.sec.eventpublisher.constants.EventpublisherConstants.CUSTOMER_PATH;
import static com.sap.hybris.sec.eventpublisher.b2b.constants.Eventpublisherb2bConstants.B2BCUSTOMER;

import org.apache.commons.configuration.Configuration;

import com.sap.hybris.sec.eventpublisher.publisher.impl.HCIPublisher;

public class B2BHCIPublisher extends HCIPublisher {
	
	@Override
	protected String getItemPath(String publishedItemName) {
		Configuration config = getConfigurationService().getConfiguration();
		switch (publishedItemName) {
		    case B2BUNIT:
			    return config.getString(B2B_UNIT_PATH);
		    case B2BCUSTOMER:
		      	return config.getString(CUSTOMER_PATH);
		    default:
			    return super.getItemPath(publishedItemName);
		}
	}
}
