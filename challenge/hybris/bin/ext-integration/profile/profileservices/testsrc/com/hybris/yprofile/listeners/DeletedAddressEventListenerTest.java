package com.hybris.yprofile.listeners;

import com.hybris.yprofile.services.ProfileTransactionService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.event.DeletedAddressEvent;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.store.BaseStoreModel;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.ArrayList;
import static org.mockito.Mockito.*;

@UnitTest
public class DeletedAddressEventListenerTest {

    private static final String CONSENT_REFERENCE = "myConsentReference";
    private static final String BASE_SITE_ID = "1234abc";

    private DeletedAddressEventListener deletedAddressEventListener;

    @Mock
    private ProfileTransactionService profileTransactionService;

    @Mock
    private BaseStoreModel baseStoreModel;

    @Mock
    private CustomerModel customerModel;

    @Mock
    private DeletedAddressEvent deletedAddressEvent;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        deletedAddressEventListener = new DeletedAddressEventListener();
        deletedAddressEventListener.setProfileTransactionService(profileTransactionService);
        when(customerModel.getConsentReference()).thenReturn(DeletedAddressEventListenerTest.CONSENT_REFERENCE);
        when(baseStoreModel.getUid()).thenReturn(DeletedAddressEventListenerTest.BASE_SITE_ID);
        when(deletedAddressEvent.getCustomer()).thenReturn(customerModel);
        when(deletedAddressEvent.getBaseStore()).thenReturn(baseStoreModel);
    }

    @Test
    public void verifyEmptyAddressSent() {
        when(customerModel.getAddresses()).thenReturn(new ArrayList());
        deletedAddressEventListener.onSiteEvent(deletedAddressEvent);
        verify(profileTransactionService, times(1)).sendAddressDeletedEvent(customerModel,
                DeletedAddressEventListenerTest.BASE_SITE_ID, DeletedAddressEventListenerTest.CONSENT_REFERENCE);
    }
}
