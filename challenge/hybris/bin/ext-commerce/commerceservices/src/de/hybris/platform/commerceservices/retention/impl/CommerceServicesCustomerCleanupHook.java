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
package de.hybris.platform.commerceservices.retention.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.comments.model.CommentModel;
import de.hybris.platform.commerceservices.consent.dao.ConsentDao;
import de.hybris.platform.commerceservices.model.consent.ConsentModel;
import de.hybris.platform.commerceservices.model.process.StoreFrontCustomerProcessModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AbstractContactInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.customerreview.model.CustomerReviewModel;
import de.hybris.platform.directpersistence.audit.dao.WriteAuditRecordsDAO;
import de.hybris.platform.retention.hook.ItemCleanupHook;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;


/**
 * This Hook removes customer related objects such as addresses, payment methods, carts and contact infos.
 *
 */
public class CommerceServicesCustomerCleanupHook implements ItemCleanupHook<CustomerModel>
{
	private static final Logger LOG = LoggerFactory.getLogger(CommerceServicesCustomerCleanupHook.class);
	
	private static final String CUSTOMER_PROCESSES_QUERY = "SELECT {" + StoreFrontCustomerProcessModel.PK + "} FROM {" + StoreFrontCustomerProcessModel._TYPECODE + "} "
			+ "WHERE {" + StoreFrontCustomerProcessModel.CUSTOMER + "} = ?user";

	private ConsentDao consentDao;
	private FlexibleSearchService flexibleSearchService;
	private ModelService modelService;
	private WriteAuditRecordsDAO writeAuditRecordsDAO;

	@Override
	public void cleanupRelatedObjects(final CustomerModel customerModel)
	{
		validateParameterNotNullStandardMessage("customerModel", customerModel);

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Cleaning up customer related objects for: {}", customerModel);
		}

		// remove addresses and addresses audit records
		for (final AddressModel address : customerModel.getAddresses())
		{
			getModelService().remove(address);
			getWriteAuditRecordsDAO().removeAuditRecordsForType(AddressModel._TYPECODE, address.getPk());
		}

		// remove payment methods and payment methods audit records
		for (final PaymentInfoModel paymentInfo : customerModel.getPaymentInfos())
		{
			getModelService().remove(paymentInfo);
			getWriteAuditRecordsDAO().removeAuditRecordsForType(PaymentInfoModel._TYPECODE, paymentInfo.getPk());
		}

		// remove carts and carts audit records
		for (final CartModel cart : customerModel.getCarts())
		{
			getModelService().remove(cart);
			getWriteAuditRecordsDAO().removeAuditRecordsForType(CartModel._TYPECODE, cart.getPk());
		}

		// remove contact infos and contact infos audit records
		for (final AbstractContactInfoModel contactInfo : customerModel.getContactInfos())
		{
			getModelService().remove(contactInfo);
			getWriteAuditRecordsDAO().removeAuditRecordsForType(AbstractContactInfoModel._TYPECODE, contactInfo.getPk());
		}

		// remove comments and comments audit records
		for (final CommentModel comment : customerModel.getComments())
		{
			getModelService().remove(comment);
			getWriteAuditRecordsDAO().removeAuditRecordsForType(CommentModel._TYPECODE, comment.getPk());
		}

		// remove product reviews and reviews audit records
		for (final CustomerReviewModel review : customerModel.getCustomerReviews())
		{
			getModelService().remove(review);
			getWriteAuditRecordsDAO().removeAuditRecordsForType(CustomerReviewModel._TYPECODE, review.getPk());
		}

		// remove consents and consents audit records
		for (final ConsentModel consent : getConsentDao().findAllConsentsByCustomer(customerModel))
		{
			getModelService().remove(consent);
			getWriteAuditRecordsDAO().removeAuditRecordsForType(ConsentModel._TYPECODE, consent.getPk());
		}
		
		final FlexibleSearchQuery customerProcessesQuery = new FlexibleSearchQuery(CUSTOMER_PROCESSES_QUERY);
		customerProcessesQuery.addQueryParameter("user", customerModel);
		final SearchResult<StoreFrontCustomerProcessModel> customerProcessSearchResult = flexibleSearchService.search(customerProcessesQuery);
		
		for(final StoreFrontCustomerProcessModel customerProcess : customerProcessSearchResult.getResult())
		{
			getModelService().remove(customerProcess);
			getWriteAuditRecordsDAO().removeAuditRecordsForType(StoreFrontCustomerProcessModel._TYPECODE, customerProcess.getPk());
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

	protected ConsentDao getConsentDao()
	{
		return consentDao;
	}

	@Required
	public void setConsentDao(final ConsentDao consentDao)
	{
		this.consentDao = consentDao;
	}

	protected WriteAuditRecordsDAO getWriteAuditRecordsDAO()
	{
		return writeAuditRecordsDAO;
	}

	@Required
	public void setWriteAuditRecordsDAO(final WriteAuditRecordsDAO writeAuditRecordsDAO)
	{
		this.writeAuditRecordsDAO = writeAuditRecordsDAO;
	}
	
	protected FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	@Required
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}
}
