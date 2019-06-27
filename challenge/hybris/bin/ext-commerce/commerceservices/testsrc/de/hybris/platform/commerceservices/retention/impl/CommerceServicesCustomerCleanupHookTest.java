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

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.comments.model.CommentModel;
import de.hybris.platform.commerceservices.consent.dao.ConsentDao;
import de.hybris.platform.commerceservices.model.consent.ConsentModel;
import de.hybris.platform.commerceservices.model.process.StoreFrontCustomerProcessModel;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AbstractContactInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.customerreview.model.CustomerReviewModel;
import de.hybris.platform.directpersistence.audit.dao.WriteAuditRecordsDAO;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class CommerceServicesCustomerCleanupHookTest
{
	@InjectMocks
	private final CommerceServicesCustomerCleanupHook customerCleanupHook = new CommerceServicesCustomerCleanupHook();

	@Mock
	private ModelService modelService;
	@Mock
	private ConsentDao consentDao;
	@Mock
	private FlexibleSearchService flexibleSearchService;
	@Mock
	private WriteAuditRecordsDAO writeAuditRecordsDAO;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldCleanupRelatedObjects()
	{
		final CustomerModel customerModel = mock(CustomerModel.class);
		final AddressModel addressModel = mock(AddressModel.class);
		final Collection<AddressModel> addresses = Collections.singletonList(addressModel);
		given(customerModel.getAddresses()).willReturn(addresses);
		final PK addressPK = PK.parse("1111");
		given(addressModel.getPk()).willReturn(addressPK);

		final PaymentInfoModel paymentInfoModel = mock(PaymentInfoModel.class);
		final Collection<PaymentInfoModel> paymentInfos = Collections.singletonList(paymentInfoModel);
		given(customerModel.getPaymentInfos()).willReturn(paymentInfos);
		final PK paymentInfoPK = PK.parse("2222");
		given(paymentInfoModel.getPk()).willReturn(paymentInfoPK);

		final CartModel cartModel = mock(CartModel.class);
		final Collection<CartModel> carts = Collections.singletonList(cartModel);
		given(customerModel.getCarts()).willReturn(carts);
		final PK cartModelPK = PK.parse("3333");
		given(cartModel.getPk()).willReturn(cartModelPK);

		final AbstractContactInfoModel contactInfoModel = mock(AbstractContactInfoModel.class);
		final Collection<AbstractContactInfoModel> contactInfos = Collections.singletonList(contactInfoModel);
		given(customerModel.getContactInfos()).willReturn(contactInfos);
		final PK contactInfoModelPK = PK.parse("4444");
		given(contactInfoModel.getPk()).willReturn(contactInfoModelPK);

		final CommentModel commentModel = mock(CommentModel.class);
		final List<CommentModel> comments = Collections.singletonList(commentModel);
		given(customerModel.getComments()).willReturn(comments);
		final PK commentModelPK = PK.parse("5555");
		given(commentModel.getPk()).willReturn(commentModelPK);

		final CustomerReviewModel customerReviewModel = mock(CustomerReviewModel.class);
		final List<CustomerReviewModel> customerReviews = Collections.singletonList(customerReviewModel);
		given(customerModel.getCustomerReviews()).willReturn(customerReviews);
		final PK customerReviewModelPK = PK.parse("6666");
		given(customerReviewModel.getPk()).willReturn(customerReviewModelPK);

		final ConsentModel consentModel = mock(ConsentModel.class);
		final List<ConsentModel> consents = Collections.singletonList(consentModel);
		given(consentDao.findAllConsentsByCustomer(customerModel)).willReturn(consents);
		final PK consentModelPK = PK.parse("7777");
		given(consentModel.getPk()).willReturn(consentModelPK);

		final SearchResult customerProcessSearchResult = mock(SearchResult.class);
		given(flexibleSearchService.search(Mockito.any(FlexibleSearchQuery.class))).willReturn(customerProcessSearchResult);
		final StoreFrontCustomerProcessModel customerProcessModel = mock(StoreFrontCustomerProcessModel.class);
		final List<StoreFrontCustomerProcessModel> customerProcesses = Collections.singletonList(customerProcessModel);
		given(customerProcessSearchResult.getResult()).willReturn(customerProcesses);
		final PK customerProcessModelPK = PK.parse("8888");
		given(customerProcessModel.getPk()).willReturn(customerProcessModelPK);
		
		customerCleanupHook.cleanupRelatedObjects(customerModel);
		verify(modelService).remove(addressModel);
		verify(modelService).remove(paymentInfoModel);
		verify(modelService).remove(cartModel);
		verify(modelService).remove(contactInfoModel);
		verify(modelService).remove(commentModel);
		verify(modelService).remove(customerReviewModel);
		verify(modelService).remove(consentModel);
		verify(modelService).remove(customerProcessModel);
		verify(writeAuditRecordsDAO).removeAuditRecordsForType(AddressModel._TYPECODE, addressPK);
		verify(writeAuditRecordsDAO).removeAuditRecordsForType(PaymentInfoModel._TYPECODE, paymentInfoPK);
		verify(writeAuditRecordsDAO).removeAuditRecordsForType(CartModel._TYPECODE, cartModelPK);
		verify(writeAuditRecordsDAO).removeAuditRecordsForType(AbstractContactInfoModel._TYPECODE, contactInfoModelPK);
		verify(writeAuditRecordsDAO).removeAuditRecordsForType(CommentModel._TYPECODE, commentModelPK);
		verify(writeAuditRecordsDAO).removeAuditRecordsForType(CustomerReviewModel._TYPECODE, customerReviewModelPK);
		verify(writeAuditRecordsDAO).removeAuditRecordsForType(ConsentModel._TYPECODE, consentModelPK);
		verify(writeAuditRecordsDAO).removeAuditRecordsForType(StoreFrontCustomerProcessModel._TYPECODE, customerProcessModelPK);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotCleanupRelatedObjectsIfInputIsNull()
	{
		customerCleanupHook.cleanupRelatedObjects(null);
	}
}
