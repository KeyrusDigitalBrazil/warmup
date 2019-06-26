package com.hybris.yprofile.services.impl;

import com.hybris.charon.RawResponse;
import com.hybris.yprofile.dto.AbstractProfileEvent;
import com.hybris.yprofile.dto.Order;
import com.hybris.yprofile.dto.UID;
import com.hybris.yprofile.dto.User;
import com.hybris.yprofile.rest.clients.ProfileClient;
import com.hybris.yprofile.services.ProfileConfigurationService;
import com.hybris.yprofile.services.RetrieveRestClientStrategy;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.event.ChangeUIDEvent;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.store.BaseStoreModel;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import rx.Observable;

import java.util.Optional;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@UnitTest
public class DefaultProfileTransactionServiceTest {


    private static final String CONSENT_REFERENCE = "myConsentReference";
    private static final String SITE_ID = "mySite";
    private static final String SESSION_ID = "sessionId";
    private static final String HYBRIS_CONTEXT_TRACE_ID_HEADER = "hybris-context-trace-id";

    private DefaultProfileTransactionService defaultProfileTransactionService;

    @Mock
    private RetrieveRestClientStrategy retrieveRestClientStrategy;

    @Mock
    private ProfileConfigurationService profileConfigurationService;

    @Mock
    private Converter<OrderModel, Order> profileOrderEventConverter;

    @Mock
    private Converter<ConsignmentModel, Order> profileConsignmentEventConverter;

    @Mock
    private Converter<ReturnRequestModel, Order> profileReturnEventConverter;

    @Mock
    private Converter<UserModel, User> profileUserEventConverter;

    @Mock
    private Converter<ChangeUIDEvent, UID> profileUIDConverter;

    @Mock
    private ProfileClient profileClient;

    @Mock
    private RawResponse rawResponse;


    @Before
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);
        defaultProfileTransactionService = new DefaultProfileTransactionService();
        defaultProfileTransactionService.setProfileConfigurationService(profileConfigurationService);
        defaultProfileTransactionService.setProfileConsignmentEventConverter(profileConsignmentEventConverter);
        defaultProfileTransactionService.setProfileOrderEventConverter(profileOrderEventConverter);
        defaultProfileTransactionService.setProfileReturnEventConverter(profileReturnEventConverter);
        defaultProfileTransactionService.setRetrieveRestClientStrategy(retrieveRestClientStrategy);
        defaultProfileTransactionService.setProfileUIDConverter(profileUIDConverter);
        defaultProfileTransactionService.setProfileUserEventConverter(profileUserEventConverter);

        when(retrieveRestClientStrategy.getProfileRestClient()).thenReturn(profileClient);

    }

    @Test
    public void verifyOrderIsSentToProfile() {

        OrderModel orderModel = mock(OrderModel.class);
        Order order = mock(Order.class);
        UserModel userModel = mock(UserModel.class);

        when(order.getChannelRef()).thenReturn(SITE_ID);
        when(orderModel.getUser()).thenReturn(userModel);
        when(orderModel.getConsentReference()).thenReturn(CONSENT_REFERENCE);
        when(userModel.getProfileTagDebug()).thenReturn(true);

        when(profileConfigurationService.isProfileTrackingPaused()).thenReturn(false);
        when(profileConfigurationService.isConfigurationPresent()).thenReturn(true);
        when(profileOrderEventConverter.convert(any(OrderModel.class))).thenReturn(order);
        when(profileClient.sendTransaction(anyString(), anyString(),anyString(),any(AbstractProfileEvent.class))).thenReturn(Observable.just(rawResponse));
        when(rawResponse.getStatusCode()).thenReturn(201);
        when(rawResponse.header(HYBRIS_CONTEXT_TRACE_ID_HEADER)).thenReturn(Optional.of("hybris-context-trace-id"));

        defaultProfileTransactionService.sendSubmitOrderEvent(orderModel);

        verify(profileClient, times(1)).sendTransaction(matches("profile-commerce-order"),
                matches(CONSENT_REFERENCE), matches("1"), any(Order.class));

    }

    @Test
    public void verifyOrderIsNotSentToProfileWhenProfileTrackingIsPaused() {
        OrderModel orderModel = mock(OrderModel.class);
        Order order = mock(Order.class);
        UserModel userModel = mock(UserModel.class);

        when(order.getChannelRef()).thenReturn(SITE_ID);
        when(orderModel.getUser()).thenReturn(userModel);
        when(orderModel.getConsentReference()).thenReturn(CONSENT_REFERENCE);
        when(userModel.getProfileTagDebug()).thenReturn(true);

        when(profileOrderEventConverter.convert(any(OrderModel.class))).thenReturn(order);
        when(profileConfigurationService.isProfileTrackingPaused()).thenReturn(true);

        defaultProfileTransactionService.sendSubmitOrderEvent(orderModel);

        verify(profileClient, never()).sendTransaction(anyString(), anyString(), anyString(), any(Order.class));
    }

    @Test
    public void verifyOrderIsNotSentToProfileWhenYaasConfigurationIsNotPresent() {
        OrderModel orderModel = mock(OrderModel.class);
        Order order = mock(Order.class);
        UserModel userModel = mock(UserModel.class);

        when(order.getChannelRef()).thenReturn(SITE_ID);
        when(orderModel.getUser()).thenReturn(userModel);
        when(orderModel.getConsentReference()).thenReturn(CONSENT_REFERENCE);
        when(userModel.getProfileTagDebug()).thenReturn(true);

        when(profileConfigurationService.isProfileTrackingPaused()).thenReturn(false);
        when(profileConfigurationService.isConfigurationPresent()).thenReturn(false);
        when(profileOrderEventConverter.convert(any(OrderModel.class))).thenReturn(order);
        defaultProfileTransactionService.sendSubmitOrderEvent(orderModel);

        verify(profileClient, never()).sendTransaction(anyString(), anyString(), anyString(), any(Order.class));
    }



    @Test
    public void verifyOrderIsNotSentToProfileWhenConsentReferenceIsNotPresent() {
        OrderModel orderModel = mock(OrderModel.class);
        Order order = mock(Order.class);
        UserModel userModel = mock(UserModel.class);

        when(order.getChannelRef()).thenReturn(SITE_ID);
        when(orderModel.getUser()).thenReturn(userModel);
        when(orderModel.getConsentReference()).thenReturn(null);
        when(userModel.getProfileTagDebug()).thenReturn(true);

        when(profileConfigurationService.isProfileTrackingPaused()).thenReturn(false);
        when(profileConfigurationService.isConfigurationPresent()).thenReturn(true);
        when(profileOrderEventConverter.convert(any(OrderModel.class))).thenReturn(order);

        defaultProfileTransactionService.sendSubmitOrderEvent(orderModel);

        verify(profileClient, never()).sendTransaction(anyString(), anyString(), anyString(), any(Order.class));
    }

    @Test
    public void verifyConsignmentIsSentToProfile() {
        ConsignmentModel consignmentModel = mock(ConsignmentModel.class);
        OrderModel orderModel = mock(OrderModel.class);
        Order order = mock(Order.class);
        UserModel userModel = mock(UserModel.class);

        when(order.getChannelRef()).thenReturn(SITE_ID);
        when(consignmentModel.getOrder()).thenReturn(orderModel);
        when(orderModel.getUser()).thenReturn(userModel);
        when(orderModel.getConsentReference()).thenReturn(CONSENT_REFERENCE);
        when(userModel.getProfileTagDebug()).thenReturn(true);

        when(profileConfigurationService.isProfileTrackingPaused()).thenReturn(false);
        when(profileConfigurationService.isConfigurationPresent()).thenReturn(true);
        when(profileConsignmentEventConverter.convert(any(ConsignmentModel.class))).thenReturn(order);
        when(profileClient.sendTransaction(anyString(), anyString(),anyString(),any(AbstractProfileEvent.class))).thenReturn(Observable.just(rawResponse));
        when(rawResponse.getStatusCode()).thenReturn(201);
        when(rawResponse.header(HYBRIS_CONTEXT_TRACE_ID_HEADER)).thenReturn(Optional.of("hybris-context-trace-id"));

        defaultProfileTransactionService.sendConsignmentEvent(consignmentModel);

        verify(profileClient, times(1)).sendTransaction(matches("profile-commerce-shipment"),
                matches(CONSENT_REFERENCE), matches("1"), any(Order.class));

    }

    @Test
    public void verifyConsignmentIsNotSentToProfileWhenProfileTrackingIsPaused() {

        ConsignmentModel consignmentModel = mock(ConsignmentModel.class);
        OrderModel orderModel = mock(OrderModel.class);
        Order order = mock(Order.class);
        UserModel userModel = mock(UserModel.class);

        when(order.getChannelRef()).thenReturn(SITE_ID);
        when(consignmentModel.getOrder()).thenReturn(orderModel);
        when(orderModel.getUser()).thenReturn(userModel);
        when(orderModel.getConsentReference()).thenReturn(CONSENT_REFERENCE);
        when(userModel.getProfileTagDebug()).thenReturn(true);

        when(profileConfigurationService.isProfileTrackingPaused()).thenReturn(true);
        when(profileConfigurationService.isConfigurationPresent()).thenReturn(true);
        when(profileConsignmentEventConverter.convert(any(ConsignmentModel.class))).thenReturn(order);
        when(profileClient.sendTransaction(anyString(), anyString(),anyString(),any(AbstractProfileEvent.class))).thenReturn(Observable.just(rawResponse));


        defaultProfileTransactionService.sendConsignmentEvent(consignmentModel);

        verify(profileClient, never()).sendTransaction(anyString(), anyString(), anyString(), any(Order.class));
    }

    @Test
    public void verifyConsignmentIsNotSentToProfileWhenYaasConfigurationIsNotPresent() {

        ConsignmentModel consignmentModel = mock(ConsignmentModel.class);
        OrderModel orderModel = mock(OrderModel.class);
        Order order = mock(Order.class);
        UserModel userModel = mock(UserModel.class);

        when(order.getChannelRef()).thenReturn(SITE_ID);
        when(consignmentModel.getOrder()).thenReturn(orderModel);
        when(orderModel.getUser()).thenReturn(userModel);
        when(orderModel.getConsentReference()).thenReturn(CONSENT_REFERENCE);
        when(userModel.getProfileTagDebug()).thenReturn(true);

        when(profileConfigurationService.isProfileTrackingPaused()).thenReturn(false);
        when(profileConfigurationService.isConfigurationPresent()).thenReturn(false);
        when(profileConsignmentEventConverter.convert(any(ConsignmentModel.class))).thenReturn(order);
        when(profileClient.sendTransaction(anyString(), anyString(),anyString(),any(AbstractProfileEvent.class))).thenReturn(Observable.just(rawResponse));

        defaultProfileTransactionService.sendConsignmentEvent(consignmentModel);

        verify(profileClient, never()).sendTransaction(anyString(), anyString(), anyString(), any(Order.class));
    }

    @Test
    public void verifyConsignmentIsNotSentToProfileWhenConsentReferenceIsNotPresent(){

        ConsignmentModel consignmentModel = mock(ConsignmentModel.class);
        OrderModel orderModel = mock(OrderModel.class);
        Order order = mock(Order.class);
        UserModel userModel = mock(UserModel.class);

        when(order.getChannelRef()).thenReturn(SITE_ID);
        when(consignmentModel.getOrder()).thenReturn(orderModel);
        when(orderModel.getUser()).thenReturn(userModel);
        when(orderModel.getConsentReference()).thenReturn(null);
        when(userModel.getProfileTagDebug()).thenReturn(true);

        when(profileConfigurationService.isProfileTrackingPaused()).thenReturn(false);
        when(profileConfigurationService.isConfigurationPresent()).thenReturn(true);
        when(profileConsignmentEventConverter.convert(any(ConsignmentModel.class))).thenReturn(order);
        when(profileClient.sendTransaction(anyString(), anyString(),anyString(),any(AbstractProfileEvent.class))).thenReturn(Observable.just(rawResponse));

        defaultProfileTransactionService.sendConsignmentEvent(consignmentModel);

        verify(profileClient, never()).sendTransaction(anyString(), anyString(), anyString(), any(Order.class));

    }

    @Test
    public void verifyReturnIsSentToProfile() {
        ReturnRequestModel returnRequestModel = mock(ReturnRequestModel.class);
        OrderModel orderModel = mock(OrderModel.class);
        Order order = mock(Order.class);
        UserModel userModel = mock(UserModel.class);

        when(order.getChannelRef()).thenReturn(SITE_ID);
        when(returnRequestModel.getOrder()).thenReturn(orderModel);
        when(orderModel.getUser()).thenReturn(userModel);
        when(orderModel.getConsentReference()).thenReturn(CONSENT_REFERENCE);
        when(userModel.getProfileTagDebug()).thenReturn(true);

        when(profileConfigurationService.isProfileTrackingPaused()).thenReturn(false);
        when(profileConfigurationService.isConfigurationPresent()).thenReturn(true);
        when(profileReturnEventConverter.convert(any(ReturnRequestModel.class))).thenReturn(order);
        when(profileClient.sendTransaction(anyString(), anyString(),anyString(),any(AbstractProfileEvent.class))).thenReturn(Observable.just(rawResponse));
        when(rawResponse.getStatusCode()).thenReturn(201);
        when(rawResponse.header(HYBRIS_CONTEXT_TRACE_ID_HEADER)).thenReturn(Optional.of("hybris-context-trace-id"));

        defaultProfileTransactionService.sendReturnOrderEvent(returnRequestModel);

        verify(profileClient, times(1)).sendTransaction(matches("profile-commerce-return"),
                matches(CONSENT_REFERENCE), matches("1"), any(Order.class));
    }

    @Test
    public void verifyReturnIsNotSentToProfileWhenProfileTrackingIsPaused() {

        ReturnRequestModel returnRequestModel = mock(ReturnRequestModel.class);
        OrderModel orderModel = mock(OrderModel.class);
        Order order = mock(Order.class);
        UserModel userModel = mock(UserModel.class);

        when(order.getChannelRef()).thenReturn(SITE_ID);
        when(returnRequestModel.getOrder()).thenReturn(orderModel);
        when(orderModel.getUser()).thenReturn(userModel);
        when(orderModel.getConsentReference()).thenReturn(CONSENT_REFERENCE);
        when(userModel.getProfileTagDebug()).thenReturn(true);

        when(profileConfigurationService.isProfileTrackingPaused()).thenReturn(true);
        when(profileConfigurationService.isConfigurationPresent()).thenReturn(true);
        when(profileReturnEventConverter.convert(any(ReturnRequestModel.class))).thenReturn(order);
        when(profileClient.sendTransaction(anyString(), anyString(),anyString(),any(AbstractProfileEvent.class))).thenReturn(Observable.just(rawResponse));

        defaultProfileTransactionService.sendReturnOrderEvent(returnRequestModel);

        verify(profileClient, never()).sendTransaction(anyString(), anyString(), anyString(), any(Order.class));

    }

    @Test
    public void verifyReturnIsNotSentToProfileWhenYaasConfigurationIsNotPresent() {

        ReturnRequestModel returnRequestModel = mock(ReturnRequestModel.class);
        OrderModel orderModel = mock(OrderModel.class);
        Order order = mock(Order.class);
        UserModel userModel = mock(UserModel.class);

        when(order.getChannelRef()).thenReturn(SITE_ID);
        when(returnRequestModel.getOrder()).thenReturn(orderModel);
        when(orderModel.getUser()).thenReturn(userModel);
        when(orderModel.getConsentReference()).thenReturn(CONSENT_REFERENCE);
        when(userModel.getProfileTagDebug()).thenReturn(true);

        when(profileConfigurationService.isProfileTrackingPaused()).thenReturn(false);
        when(profileConfigurationService.isConfigurationPresent()).thenReturn(false);
        when(profileReturnEventConverter.convert(any(ReturnRequestModel.class))).thenReturn(order);
        when(profileClient.sendTransaction(anyString(), anyString(),anyString(),any(AbstractProfileEvent.class))).thenReturn(Observable.just(rawResponse));

        defaultProfileTransactionService.sendReturnOrderEvent(returnRequestModel);

        verify(profileClient, never()).sendTransaction(anyString(), anyString(), anyString(), any(Order.class));

    }

    @Test
    public void verifyReturnIsNotSentToProfileWhenConsentReferenceIsNotPresent(){

        ReturnRequestModel returnRequestModel = mock(ReturnRequestModel.class);
        OrderModel orderModel = mock(OrderModel.class);
        Order order = mock(Order.class);
        UserModel userModel = mock(UserModel.class);

        when(order.getChannelRef()).thenReturn(SITE_ID);
        when(returnRequestModel.getOrder()).thenReturn(orderModel);
        when(orderModel.getUser()).thenReturn(userModel);
        when(orderModel.getConsentReference()).thenReturn(null);
        when(userModel.getProfileTagDebug()).thenReturn(true);

        when(profileConfigurationService.isProfileTrackingPaused()).thenReturn(false);
        when(profileConfigurationService.isConfigurationPresent()).thenReturn(true);
        when(profileReturnEventConverter.convert(any(ReturnRequestModel.class))).thenReturn(order);
        when(profileClient.sendTransaction(anyString(), anyString(),anyString(),any(AbstractProfileEvent.class))).thenReturn(Observable.just(rawResponse));

        defaultProfileTransactionService.sendReturnOrderEvent(returnRequestModel);

        verify(profileClient, never()).sendTransaction(anyString(), anyString(), anyString(), any(Order.class));

    }

    @Test
    public void verifyUserRegistrationIsSentProfile() {

        UserModel userModel = mock(UserModel.class);
        when(userModel.getConsentReference()).thenReturn(CONSENT_REFERENCE);
        when(userModel.getProfileTagDebug()).thenReturn(true);
        User user = mock(User.class);

        when(profileConfigurationService.isProfileTrackingPaused()).thenReturn(false);
        when(profileConfigurationService.isConfigurationPresent()).thenReturn(true);
        when(profileUserEventConverter.convert(any(UserModel.class))).thenReturn(user);
        when(profileClient.sendTransaction(anyString(), anyString(),anyString(),any(AbstractProfileEvent.class))).thenReturn(Observable.just(rawResponse));
        when(rawResponse.getStatusCode()).thenReturn(201);
        when(rawResponse.header(HYBRIS_CONTEXT_TRACE_ID_HEADER)).thenReturn(Optional.of("hybris-context-trace-id"));

        defaultProfileTransactionService.sendUserRegistrationEvent(userModel, CONSENT_REFERENCE, SESSION_ID, SITE_ID);


        verify(profileClient, times(1)).sendTransaction(matches("account-registration"),
                matches(CONSENT_REFERENCE), matches("1"), any(User.class));
    }

    @Test
    public void verifyUserRegistrationIsNotSentToProfileWhenProfileTrackingIsPaused() {

        UserModel userModel = mock(UserModel.class);
        when(userModel.getConsentReference()).thenReturn(CONSENT_REFERENCE);
        when(userModel.getProfileTagDebug()).thenReturn(true);
        User user = mock(User.class);

        when(profileConfigurationService.isProfileTrackingPaused()).thenReturn(true);
        when(profileConfigurationService.isConfigurationPresent()).thenReturn(true);
        when(profileUserEventConverter.convert(any(UserModel.class))).thenReturn(user);
        when(profileClient.sendTransaction(anyString(), anyString(),anyString(),any(AbstractProfileEvent.class))).thenReturn(Observable.just(rawResponse));

        defaultProfileTransactionService.sendUserRegistrationEvent(userModel, CONSENT_REFERENCE, SESSION_ID, SITE_ID);

        verify(profileClient, never()).sendTransaction(anyString(), anyString(), anyString(), any(Order.class));
    }

    @Test
    public void verifyUserRegistrationIsNotSentToProfileWhenYaasConfigurationIsNotPresent() {

        UserModel userModel = mock(UserModel.class);
        when(userModel.getConsentReference()).thenReturn(CONSENT_REFERENCE);
        when(userModel.getProfileTagDebug()).thenReturn(true);
        User user = mock(User.class);

        when(profileConfigurationService.isProfileTrackingPaused()).thenReturn(false);
        when(profileConfigurationService.isConfigurationPresent()).thenReturn(false);
        when(profileUserEventConverter.convert(any(UserModel.class))).thenReturn(user);
        when(profileClient.sendTransaction(anyString(), anyString(),anyString(),any(AbstractProfileEvent.class))).thenReturn(Observable.just(rawResponse));

        defaultProfileTransactionService.sendUserRegistrationEvent(userModel, CONSENT_REFERENCE, SESSION_ID, SITE_ID);

        verify(profileClient, never()).sendTransaction(anyString(), anyString(), anyString(), any(Order.class));
    }

    @Test
    public void verifyUserRegistrationIsNotSentToProfileWhenConsentReferenceIsNotPresent(){

        UserModel userModel = mock(UserModel.class);
        when(userModel.getConsentReference()).thenReturn(null);
        when(userModel.getProfileTagDebug()).thenReturn(true);
        User user = mock(User.class);

        when(profileConfigurationService.isProfileTrackingPaused()).thenReturn(false);
        when(profileConfigurationService.isConfigurationPresent()).thenReturn(true);
        when(profileUserEventConverter.convert(any(UserModel.class))).thenReturn(user);
        when(profileClient.sendTransaction(anyString(), anyString(),anyString(),any(AbstractProfileEvent.class))).thenReturn(Observable.just(rawResponse));

        defaultProfileTransactionService.sendUserRegistrationEvent(userModel, null, SESSION_ID, SITE_ID);

        verify(profileClient, never()).sendTransaction(anyString(), anyString(), anyString(), any(Order.class));

    }

    @Test
    public void verifyLoginIsSentToProfile() {

        UserModel userModel = mock(UserModel.class);
        when(userModel.getConsentReference()).thenReturn(CONSENT_REFERENCE);
        when(userModel.getProfileTagDebug()).thenReturn(true);
        User user = mock(User.class);

        when(profileConfigurationService.isProfileTrackingPaused()).thenReturn(false);
        when(profileConfigurationService.isConfigurationPresent()).thenReturn(true);
        when(profileUserEventConverter.convert(any(UserModel.class))).thenReturn(user);
        when(profileClient.sendTransaction(anyString(), anyString(),anyString(),any(AbstractProfileEvent.class))).thenReturn(Observable.just(rawResponse));
        when(rawResponse.getStatusCode()).thenReturn(201);
        when(rawResponse.header(HYBRIS_CONTEXT_TRACE_ID_HEADER)).thenReturn(Optional.of("hybris-context-trace-id"));

        defaultProfileTransactionService.sendLoginEvent(userModel, CONSENT_REFERENCE, SESSION_ID, SITE_ID);

        verify(profileClient, times(1)).sendTransaction(matches("login"),
                matches(CONSENT_REFERENCE), matches("1"), any(User.class));
    }

    @Test
    public void verifyLoginIsNotSentToProfileWhenProfileTrackingIsPaused() {

        UserModel userModel = mock(UserModel.class);
        when(userModel.getConsentReference()).thenReturn(CONSENT_REFERENCE);
        when(userModel.getProfileTagDebug()).thenReturn(true);
        User user = mock(User.class);

        when(profileConfigurationService.isProfileTrackingPaused()).thenReturn(true);
        when(profileConfigurationService.isConfigurationPresent()).thenReturn(true);
        when(profileUserEventConverter.convert(any(UserModel.class))).thenReturn(user);
        when(profileClient.sendTransaction(anyString(), anyString(),anyString(),any(AbstractProfileEvent.class))).thenReturn(Observable.just(rawResponse));

        defaultProfileTransactionService.sendLoginEvent(userModel, CONSENT_REFERENCE, SESSION_ID, SITE_ID);

        verify(profileClient, never()).sendTransaction(anyString(), anyString(), anyString(), any(Order.class));
    }

    @Test
    public void verifyLoginIsNotSentToProfileWhenYaasConfigurationIsNotPresent() {

        UserModel userModel = mock(UserModel.class);
        when(userModel.getConsentReference()).thenReturn(CONSENT_REFERENCE);
        when(userModel.getProfileTagDebug()).thenReturn(true);
        User user = mock(User.class);

        when(profileConfigurationService.isProfileTrackingPaused()).thenReturn(false);
        when(profileConfigurationService.isConfigurationPresent()).thenReturn(false);
        when(profileUserEventConverter.convert(any(UserModel.class))).thenReturn(user);
        when(profileClient.sendTransaction(anyString(), anyString(),anyString(),any(AbstractProfileEvent.class))).thenReturn(Observable.just(rawResponse));

        defaultProfileTransactionService.sendLoginEvent(userModel, CONSENT_REFERENCE, SESSION_ID, SITE_ID);

        verify(profileClient, never()).sendTransaction(anyString(), anyString(), anyString(), any(Order.class));

    }

    @Test
    public void verifyLoginIsNotSentToProfileWhenConsentReferenceIsNotPresent(){

        UserModel userModel = mock(UserModel.class);
        when(userModel.getConsentReference()).thenReturn(null);
        when(userModel.getProfileTagDebug()).thenReturn(true);
        User user = mock(User.class);

        when(profileConfigurationService.isProfileTrackingPaused()).thenReturn(false);
        when(profileConfigurationService.isConfigurationPresent()).thenReturn(true);
        when(profileUserEventConverter.convert(any(UserModel.class))).thenReturn(user);
        when(profileClient.sendTransaction(anyString(), anyString(),anyString(),any(AbstractProfileEvent.class))).thenReturn(Observable.just(rawResponse));

        defaultProfileTransactionService.sendLoginEvent(userModel, null, SESSION_ID, SITE_ID);

        verify(profileClient, never()).sendTransaction(anyString(), anyString(), anyString(), any(Order.class));

    }

    @Test
    public void verifyUidChangedIsSentToProfile() {

        CustomerModel customerModel = mock(CustomerModel.class);
        when(customerModel.getConsentReference()).thenReturn(CONSENT_REFERENCE);
        when(customerModel.getProfileTagDebug()).thenReturn(true);
        ChangeUIDEvent event = mock(ChangeUIDEvent.class);
        BaseStoreModel baseStoreModel = mock(BaseStoreModel.class);
        UID uid = mock(UID.class);

        when(event.getCustomer()).thenReturn(customerModel);
        when(event.getBaseStore()).thenReturn(baseStoreModel);
        when(baseStoreModel.getUid()).thenReturn(SITE_ID);

        when(profileConfigurationService.isProfileTrackingPaused()).thenReturn(false);
        when(profileConfigurationService.isConfigurationPresent()).thenReturn(true);
        when(profileUIDConverter.convert(any(ChangeUIDEvent.class))).thenReturn(uid);
        when(profileClient.sendSlimEvent(anyString(), anyString(),anyString(),any(AbstractProfileEvent.class))).thenReturn(Observable.just(rawResponse));
        when(rawResponse.getStatusCode()).thenReturn(201);
        when(rawResponse.header(HYBRIS_CONTEXT_TRACE_ID_HEADER)).thenReturn(Optional.of("hybris-context-trace-id"));

        defaultProfileTransactionService.sendUidChangedEvent(event, CONSENT_REFERENCE);

        verify(profileClient, times(1)).sendSlimEvent(matches("context/core/uidChanged"),
                matches(CONSENT_REFERENCE), matches("1"), any(User.class));

    }


    @Test
    public void verifyUidChangedIsNotSentToProfileWhenProfileTrackingIsPaused() {

        CustomerModel customerModel = mock(CustomerModel.class);
        when(customerModel.getConsentReference()).thenReturn(CONSENT_REFERENCE);
        when(customerModel.getProfileTagDebug()).thenReturn(true);
        ChangeUIDEvent event = mock(ChangeUIDEvent.class);
        BaseStoreModel baseStoreModel = mock(BaseStoreModel.class);
        UID uid = mock(UID.class);

        when(event.getCustomer()).thenReturn(customerModel);
        when(event.getBaseStore()).thenReturn(baseStoreModel);
        when(baseStoreModel.getUid()).thenReturn(SITE_ID);

        when(profileConfigurationService.isProfileTrackingPaused()).thenReturn(true);
        when(profileConfigurationService.isConfigurationPresent()).thenReturn(true);
        when(profileUIDConverter.convert(any(ChangeUIDEvent.class))).thenReturn(uid);
        when(profileClient.sendSlimEvent(anyString(), anyString(),anyString(),any(AbstractProfileEvent.class))).thenReturn(Observable.just(rawResponse));

        defaultProfileTransactionService.sendUidChangedEvent(event, CONSENT_REFERENCE);

        verify(profileClient, never()).sendSlimEvent(anyString(), anyString(), anyString(), any(Order.class));

    }

    @Test
    public void verifyUidChangedIsNotSentToProfileWhenYaasConfigurationIsNotPresent() {

        CustomerModel customerModel = mock(CustomerModel.class);
        when(customerModel.getConsentReference()).thenReturn(CONSENT_REFERENCE);
        when(customerModel.getProfileTagDebug()).thenReturn(true);
        ChangeUIDEvent event = mock(ChangeUIDEvent.class);
        BaseStoreModel baseStoreModel = mock(BaseStoreModel.class);
        UID uid = mock(UID.class);

        when(event.getCustomer()).thenReturn(customerModel);
        when(event.getBaseStore()).thenReturn(baseStoreModel);
        when(baseStoreModel.getUid()).thenReturn(SITE_ID);

        when(profileConfigurationService.isProfileTrackingPaused()).thenReturn(false);
        when(profileConfigurationService.isConfigurationPresent()).thenReturn(false);
        when(profileUIDConverter.convert(any(ChangeUIDEvent.class))).thenReturn(uid);
        when(profileClient.sendSlimEvent(anyString(), anyString(),anyString(),any(AbstractProfileEvent.class))).thenReturn(Observable.just(rawResponse));

        defaultProfileTransactionService.sendUidChangedEvent(event, CONSENT_REFERENCE);

        verify(profileClient, never()).sendSlimEvent(anyString(), anyString(), anyString(), any(Order.class));

    }

    @Test
    public void verifyUidChangedIsNotSentToProfileWhenConsentReferenceIsNotPresent(){

        CustomerModel customerModel = mock(CustomerModel.class);
        when(customerModel.getConsentReference()).thenReturn(null);
        when(customerModel.getProfileTagDebug()).thenReturn(true);
        ChangeUIDEvent event = mock(ChangeUIDEvent.class);
        BaseStoreModel baseStoreModel = mock(BaseStoreModel.class);
        UID uid = mock(UID.class);

        when(event.getCustomer()).thenReturn(customerModel);
        when(event.getBaseStore()).thenReturn(baseStoreModel);
        when(baseStoreModel.getUid()).thenReturn(SITE_ID);

        when(profileConfigurationService.isProfileTrackingPaused()).thenReturn(false);
        when(profileConfigurationService.isConfigurationPresent()).thenReturn(true);
        when(profileUIDConverter.convert(any(ChangeUIDEvent.class))).thenReturn(uid);
        when(profileClient.sendSlimEvent(anyString(), anyString(),anyString(),any(AbstractProfileEvent.class))).thenReturn(Observable.just(rawResponse));

        defaultProfileTransactionService.sendUidChangedEvent(event, null);

        verify(profileClient, never()).sendSlimEvent(anyString(), anyString(), anyString(), any(Order.class));

    }

    @Test
    public void verifyAddressSavedIsSentToProfile() {
        CustomerModel customerModel = mock(CustomerModel.class);
        when(customerModel.getConsentReference()).thenReturn(CONSENT_REFERENCE);
        when(customerModel.getProfileTagDebug()).thenReturn(true);
        User user = mock(User.class);

        when(profileConfigurationService.isProfileTrackingPaused()).thenReturn(false);
        when(profileConfigurationService.isConfigurationPresent()).thenReturn(true);
        when(profileUserEventConverter.convert(any(UserModel.class))).thenReturn(user);
        when(profileClient.sendSlimEvent(anyString(), anyString(),anyString(),any(AbstractProfileEvent.class))).thenReturn(Observable.just(rawResponse));
        when(rawResponse.getStatusCode()).thenReturn(201);
        when(rawResponse.header(HYBRIS_CONTEXT_TRACE_ID_HEADER)).thenReturn(Optional.of("hybris-context-trace-id"));

        defaultProfileTransactionService.sendAddressSavedEvent(customerModel, SITE_ID , CONSENT_REFERENCE);

        verify(profileClient, times(1)).sendSlimEvent(matches("context/core/addressesReplaced"),
                matches(CONSENT_REFERENCE), matches("1"), any(User.class));

    }


    @Test
    public void verifyAddressSavedIsNotSentToProfileWhenProfileTrackingIsPaused() {

        CustomerModel customerModel = mock(CustomerModel.class);
        when(customerModel.getConsentReference()).thenReturn(CONSENT_REFERENCE);
        when(customerModel.getProfileTagDebug()).thenReturn(true);
        User user = mock(User.class);

        when(profileConfigurationService.isProfileTrackingPaused()).thenReturn(true);
        when(profileConfigurationService.isConfigurationPresent()).thenReturn(true);
        when(profileUserEventConverter.convert(any(UserModel.class))).thenReturn(user);
        when(profileClient.sendSlimEvent(anyString(), anyString(),anyString(),any(AbstractProfileEvent.class))).thenReturn(Observable.just(rawResponse));

        defaultProfileTransactionService.sendAddressSavedEvent(customerModel, SITE_ID , CONSENT_REFERENCE);

        verify(profileClient, never()).sendSlimEvent(anyString(), anyString(), anyString(), any(User.class));

    }

    @Test
    public void verifyAddressSavedIsNotSentToProfileWhenYaasConfigurationIsNotPresent() {

        CustomerModel customerModel = mock(CustomerModel.class);
        when(customerModel.getConsentReference()).thenReturn(CONSENT_REFERENCE);
        when(customerModel.getProfileTagDebug()).thenReturn(true);
        User user = mock(User.class);

        when(profileConfigurationService.isProfileTrackingPaused()).thenReturn(false);
        when(profileConfigurationService.isConfigurationPresent()).thenReturn(false);
        when(profileUserEventConverter.convert(any(UserModel.class))).thenReturn(user);
        when(profileClient.sendSlimEvent(anyString(), anyString(),anyString(),any(AbstractProfileEvent.class))).thenReturn(Observable.just(rawResponse));

        defaultProfileTransactionService.sendAddressSavedEvent(customerModel, SITE_ID , CONSENT_REFERENCE);

        verify(profileClient, never()).sendSlimEvent(anyString(), anyString(), anyString(), any(User.class));

    }

    @Test
    public void verifyAddressSavedIsNotSentToProfileWhenConsentReferenceIsNotPresent(){

        CustomerModel customerModel = mock(CustomerModel.class);
        when(customerModel.getConsentReference()).thenReturn(CONSENT_REFERENCE);
        when(customerModel.getProfileTagDebug()).thenReturn(true);
        User user = mock(User.class);

        when(profileConfigurationService.isProfileTrackingPaused()).thenReturn(false);
        when(profileConfigurationService.isConfigurationPresent()).thenReturn(true);
        when(profileUserEventConverter.convert(any(UserModel.class))).thenReturn(user);
        when(profileClient.sendSlimEvent(anyString(), anyString(),anyString(),any(AbstractProfileEvent.class))).thenReturn(Observable.just(rawResponse));

        defaultProfileTransactionService.sendAddressSavedEvent(customerModel, SITE_ID , null);

        verify(profileClient, never()).sendSlimEvent(anyString(), anyString(), anyString(), any(User.class));

    }

    @Test
    public void verifyAddressDeletedIsSentToProfile() {
        CustomerModel customerModel = mock(CustomerModel.class);
        when(customerModel.getConsentReference()).thenReturn(CONSENT_REFERENCE);
        when(customerModel.getProfileTagDebug()).thenReturn(true);
        User user = mock(User.class);

        when(profileConfigurationService.isProfileTrackingPaused()).thenReturn(false);
        when(profileConfigurationService.isConfigurationPresent()).thenReturn(true);
        when(profileUserEventConverter.convert(any(UserModel.class))).thenReturn(user);
        when(profileClient.sendSlimEvent(anyString(), anyString(),anyString(),any(AbstractProfileEvent.class))).thenReturn(Observable.just(rawResponse));
        when(rawResponse.getStatusCode()).thenReturn(201);
        when(rawResponse.header(HYBRIS_CONTEXT_TRACE_ID_HEADER)).thenReturn(Optional.of("hybris-context-trace-id"));

        defaultProfileTransactionService.sendAddressDeletedEvent(customerModel, SITE_ID , CONSENT_REFERENCE);

        verify(profileClient, times(1)).sendSlimEvent(matches("context/core/addressesReplaced"),
                matches(CONSENT_REFERENCE), matches("1"), any(User.class));

    }


    @Test
    public void verifyAddressDeletedIsNotSentToProfileWhenProfileTrackingIsPaused() {

        CustomerModel customerModel = mock(CustomerModel.class);
        when(customerModel.getConsentReference()).thenReturn(CONSENT_REFERENCE);
        when(customerModel.getProfileTagDebug()).thenReturn(true);
        User user = mock(User.class);

        when(profileConfigurationService.isProfileTrackingPaused()).thenReturn(true);
        when(profileConfigurationService.isConfigurationPresent()).thenReturn(true);
        when(profileUserEventConverter.convert(any(UserModel.class))).thenReturn(user);
        when(profileClient.sendSlimEvent(anyString(), anyString(),anyString(),any(AbstractProfileEvent.class))).thenReturn(Observable.just(rawResponse));

        defaultProfileTransactionService.sendAddressDeletedEvent(customerModel, SITE_ID , CONSENT_REFERENCE);

        verify(profileClient, never()).sendSlimEvent(anyString(), anyString(), anyString(), any(User.class));

    }

    @Test
    public void verifyAddressDeletedIsNotSentToProfileWhenYaasConfigurationIsNotPresent() {

        CustomerModel customerModel = mock(CustomerModel.class);
        when(customerModel.getConsentReference()).thenReturn(CONSENT_REFERENCE);
        when(customerModel.getProfileTagDebug()).thenReturn(true);
        User user = mock(User.class);

        when(profileConfigurationService.isProfileTrackingPaused()).thenReturn(false);
        when(profileConfigurationService.isConfigurationPresent()).thenReturn(false);
        when(profileUserEventConverter.convert(any(UserModel.class))).thenReturn(user);
        when(profileClient.sendSlimEvent(anyString(), anyString(),anyString(),any(AbstractProfileEvent.class))).thenReturn(Observable.just(rawResponse));

        defaultProfileTransactionService.sendAddressDeletedEvent(customerModel, SITE_ID , CONSENT_REFERENCE);

        verify(profileClient, never()).sendSlimEvent(anyString(), anyString(), anyString(), any(User.class));

    }

    @Test
    public void verifyAddressDeletedIsNotSentToProfileWhenConsentReferenceIsNotPresent(){

        CustomerModel customerModel = mock(CustomerModel.class);
        when(customerModel.getConsentReference()).thenReturn(CONSENT_REFERENCE);
        when(customerModel.getProfileTagDebug()).thenReturn(true);
        User user = mock(User.class);

        when(profileConfigurationService.isProfileTrackingPaused()).thenReturn(false);
        when(profileConfigurationService.isConfigurationPresent()).thenReturn(true);
        when(profileUserEventConverter.convert(any(UserModel.class))).thenReturn(user);
        when(profileClient.sendSlimEvent(anyString(), anyString(),anyString(),any(AbstractProfileEvent.class))).thenReturn(Observable.just(rawResponse));

        defaultProfileTransactionService.sendAddressDeletedEvent(customerModel, SITE_ID , null);

        verify(profileClient, never()).sendSlimEvent(anyString(), anyString(), anyString(), any(User.class));

    }


    @Test
    public void verifyPersonalDetailsIsSentToProfile() {
        CustomerModel customerModel = mock(CustomerModel.class);
        when(customerModel.getConsentReference()).thenReturn(CONSENT_REFERENCE);
        when(customerModel.getProfileTagDebug()).thenReturn(true);
        User user = mock(User.class);

        when(profileConfigurationService.isProfileTrackingPaused()).thenReturn(false);
        when(profileConfigurationService.isConfigurationPresent()).thenReturn(true);
        when(profileUserEventConverter.convert(any(UserModel.class))).thenReturn(user);
        when(profileClient.sendSlimEvent(anyString(), anyString(),anyString(),any(AbstractProfileEvent.class))).thenReturn(Observable.just(rawResponse));
        when(rawResponse.getStatusCode()).thenReturn(201);
        when(rawResponse.header(HYBRIS_CONTEXT_TRACE_ID_HEADER)).thenReturn(Optional.of("hybris-context-trace-id"));

        defaultProfileTransactionService.sendPersonalDetailsChangedEvent(customerModel, SITE_ID , CONSENT_REFERENCE);

        verify(profileClient, times(1)).sendSlimEvent(matches("context/core/personalDetailsChanged"),
                matches(CONSENT_REFERENCE), matches("1"), any(User.class));

    }


    @Test
    public void verifyPersonalDetailsIsNotSentToProfileWhenProfileTrackingIsPaused() {

        CustomerModel customerModel = mock(CustomerModel.class);
        when(customerModel.getConsentReference()).thenReturn(CONSENT_REFERENCE);
        when(customerModel.getProfileTagDebug()).thenReturn(true);
        User user = mock(User.class);

        when(profileConfigurationService.isProfileTrackingPaused()).thenReturn(true);
        when(profileConfigurationService.isConfigurationPresent()).thenReturn(true);
        when(profileUserEventConverter.convert(any(UserModel.class))).thenReturn(user);
        when(profileClient.sendSlimEvent(anyString(), anyString(),anyString(),any(AbstractProfileEvent.class))).thenReturn(Observable.just(rawResponse));

        defaultProfileTransactionService.sendPersonalDetailsChangedEvent(customerModel, SITE_ID , CONSENT_REFERENCE);

        verify(profileClient, never()).sendSlimEvent(anyString(), anyString(), anyString(), any(User.class));

    }

    @Test
    public void verifyPersonalDetailsIsNotSentToProfileWhenYaasConfigurationIsNotPresent() {

        CustomerModel customerModel = mock(CustomerModel.class);
        when(customerModel.getConsentReference()).thenReturn(CONSENT_REFERENCE);
        when(customerModel.getProfileTagDebug()).thenReturn(true);
        User user = mock(User.class);

        when(profileConfigurationService.isProfileTrackingPaused()).thenReturn(false);
        when(profileConfigurationService.isConfigurationPresent()).thenReturn(false);
        when(profileUserEventConverter.convert(any(UserModel.class))).thenReturn(user);
        when(profileClient.sendSlimEvent(anyString(), anyString(),anyString(),any(AbstractProfileEvent.class))).thenReturn(Observable.just(rawResponse));

        defaultProfileTransactionService.sendPersonalDetailsChangedEvent(customerModel, SITE_ID , CONSENT_REFERENCE);

        verify(profileClient, never()).sendSlimEvent(anyString(), anyString(), anyString(), any(User.class));

    }

    @Test
    public void verifyPersonalDetailsIsNotSentToProfileWhenConsentReferenceIsNotPresent(){

        CustomerModel customerModel = mock(CustomerModel.class);
        when(customerModel.getConsentReference()).thenReturn(CONSENT_REFERENCE);
        when(customerModel.getProfileTagDebug()).thenReturn(true);
        User user = mock(User.class);

        when(profileConfigurationService.isProfileTrackingPaused()).thenReturn(false);
        when(profileConfigurationService.isConfigurationPresent()).thenReturn(true);
        when(profileUserEventConverter.convert(any(UserModel.class))).thenReturn(user);
        when(profileClient.sendSlimEvent(anyString(), anyString(),anyString(),any(AbstractProfileEvent.class))).thenReturn(Observable.just(rawResponse));

        defaultProfileTransactionService.sendPersonalDetailsChangedEvent(customerModel, SITE_ID , null);

        verify(profileClient, never()).sendSlimEvent(anyString(), anyString(), anyString(), any(User.class));

    }

}