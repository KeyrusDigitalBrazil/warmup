package de.hybris.platform.configurablebundleservices.bundle.impl;

import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.subscriptionservices.subscription.SubscriptionCommerceCartService;
import org.fest.assertions.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * Unit tests for {@link BundleOrderEntryRemoveableChecker}
 */
public class BundleOrderEntryRemoveableCheckerTest
{
	@Mock
	private SubscriptionCommerceCartService subscriptionCommerceCartService;
	@Mock
	private BundleTemplateService bundleTemplateService;
	@InjectMocks
	private final BundleOrderEntryRemoveableChecker bundleOrderEntryRemoveableChecker = new BundleOrderEntryRemoveableChecker();

	private CartEntryModel cartEntry;
	private CartModel cartModel;

	@Before
	public void setUp()
	{
		cartEntry = new CartEntryModel();
		cartModel = new CartModel();
		cartEntry.setOrder(cartModel);

		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldNotRemoveChildCartEntry()
	{
		given(subscriptionCommerceCartService.getMasterCartForCartEntry(cartEntry)).willReturn(new CartModel());

		Assertions.assertThat(bundleOrderEntryRemoveableChecker.canRemove(cartEntry)).isFalse();
		verify(subscriptionCommerceCartService).getMasterCartForCartEntry(cartEntry);
		verifyNoMoreInteractions(subscriptionCommerceCartService);
	}

	@Test
	public void shouldDelegateToParentAndRemoveNoBundleEntry()
	{
		given(bundleTemplateService.getBundleEntryGroup(any(AbstractOrderEntryModel.class))).willReturn(null);
		given(subscriptionCommerceCartService.getMasterCartForCartEntry(cartEntry)).willReturn(cartModel);

		Assertions.assertThat(bundleOrderEntryRemoveableChecker.canRemove(cartEntry)).isTrue();
	}
}
