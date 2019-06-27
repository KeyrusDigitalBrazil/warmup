package com.sap.hybris.sec.eventpublisher.b2b.interceptor;

import com.sap.hybris.sec.eventpublisher.interceptor.DefaultSECCustomerRemoveInterceptor;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.core.model.user.CustomerModel;

public class DefaultSECB2BCustomerRemoveInterceptor extends DefaultSECCustomerRemoveInterceptor{
	
	/*
	 * (non-Javadoc)
	 * @see com.sap.hybris.sec.eventpublisher.interceptor.DefaultSECCustomerRemoveInterceptor#getCustomerId(de.hybris.platform.core.model.user.CustomerModel)
	 * 
	 * Setting Customer PK in customerId field, if customer is a B2BCustomer as PK is the unique field in B2BCustomer replication to CEC.
	 */
	@Override
	public String getCustomerId(CustomerModel customer){
		if(customer instanceof B2BCustomerModel) {
			return customer.getPk().toString();
		}else {
			return super.getCustomerId(customer);
		}
	}

}
