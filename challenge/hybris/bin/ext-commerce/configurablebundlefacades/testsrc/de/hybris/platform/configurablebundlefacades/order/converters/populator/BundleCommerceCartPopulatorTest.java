package de.hybris.platform.configurablebundlefacades.order.converters.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.PromotionResultData;
import de.hybris.platform.configurablebundlefacades.data.BundleTemplateData;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.promotions.PromotionsService;
import de.hybris.platform.promotions.jalo.PromotionResult;
import de.hybris.platform.promotions.model.PromotionResultModel;
import de.hybris.platform.promotions.result.PromotionOrderResults;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.tenant.MockTenant;
import org.fest.assertions.MapAssert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * Unit tests for {@link BundleCommerceCartPopulator}
 */
@UnitTest
public class BundleCommerceCartPopulatorTest
{
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private PromotionsService promotionsService;
	@Mock
	private ModelService modelService;
	@Mock
	private Converter<PromotionResultModel, PromotionResultData> promotionResultConverter;
	@InjectMocks
	private final BundleCommerceCartPopulator<CartModel, CartData> bundleCartPopulator = new BundleCommerceCartPopulator<>();

	private CartModel source;
	private CartData target;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		bundleCartPopulator.setModelService(modelService);
		bundleCartPopulator.setPromotionResultConverter(promotionResultConverter);

		source = new CartModel();
		target = new CartData();
		target.setEntries(Collections.emptyList());
	}

	@Test
	public void shouldThrowExceptionWhenSourceIsNull()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Parameter source can not be null");

		bundleCartPopulator.populate(null, new CartData());
	}

	@Test
	public void shouldThrowExceptionWhenTargetIsNull()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Parameter target can not be null");

		bundleCartPopulator.populate(new CartModel(), null);
	}

	@Test
	public void testPopulateNoEntries()
	{
		thrown.expect(NullPointerException.class);

		target.setEntries(null);

		bundleCartPopulator.populate(source, target);
	}

	@Test
	public void testPopulateEmptyEntries()
	{
		bundleCartPopulator.populate(source, target);

		assertThat(target.getEntries()).isNullOrEmpty();
		assertThat(target.getFirstIncompleteBundleComponentsMap()).isNullOrEmpty();
		assertThat(target.getPotentialOrderPromotions()).isNullOrEmpty();
		assertThat(target.getPotentialProductPromotions()).isNullOrEmpty();
	}

	@Test
	public void testPopulateWithEntries()
	{
		OrderEntryData orderEntryData1 = new OrderEntryData();
		orderEntryData1.setBundleNo(1);
		OrderEntryData orderEntryData2 = new OrderEntryData();
		orderEntryData2.setBundleNo(0);
		target.setEntries(Arrays.asList(orderEntryData1, orderEntryData2));

		bundleCartPopulator.populate(source, target);

		// Some complex bundleTemplateService interactions are left for OrderComparator test
		assertThat(target.getEntries()).containsExactly(orderEntryData1, orderEntryData2);
	}

	@Test
	public void testPopulateWithIncompleteComponets()
	{
		final OrderEntryData orderEntryData1 = new OrderEntryData();
		orderEntryData1.setBundleNo(1);
		orderEntryData1.setValid(true);
		final BundleTemplateData bundleTemplateData1 = new BundleTemplateData();
		orderEntryData1.setComponent(bundleTemplateData1);

		final OrderEntryData orderEntryData2 = new OrderEntryData();
		orderEntryData2.setBundleNo(2);
		orderEntryData2.setValid(false);
		final BundleTemplateData bundleTemplateData2 = new BundleTemplateData();
		orderEntryData2.setComponent(bundleTemplateData2);

		final OrderEntryData orderEntryData3 = new OrderEntryData();
		orderEntryData3.setBundleNo(3);
		orderEntryData3.setValid(false);
		final BundleTemplateData bundleTemplateData3 = new BundleTemplateData();
		orderEntryData3.setComponent(bundleTemplateData3);

		target.setEntries(Arrays.asList(orderEntryData1, orderEntryData2, orderEntryData3));

		bundleCartPopulator.populate(source, target);

		assertThat(target.getFirstIncompleteBundleComponentsMap()).hasSize(1);
		assertThat(target.getFirstIncompleteBundleComponentsMap()).includes(MapAssert.entry(2, bundleTemplateData2));
	}

	@Test
	public void testPopulateWithPotentialPromotions()
	{
		final List<PromotionResult> potentialOrderPromotions = Arrays.asList(newPromotionResult(1), newPromotionResult(2));
		final List<PromotionResult> potentialProductPromotions = Arrays.asList(newPromotionResult(3), newPromotionResult(4));
		// This wrapping is required since there is a cast to ArrayList in ModelService.addAll()
		// call from AbstractOrderPopulator.getPromotions() method.
		final List<PromotionResultModel> orderPromotionModels = new ArrayList<>(Arrays.asList(new PromotionResultModel(), new PromotionResultModel()));
		final List<PromotionResultModel> productPromotionModels = new ArrayList<>(Arrays.asList(new PromotionResultModel(), new PromotionResultModel()));
		final List<PromotionResultData> convertedOrderPromotions = Arrays.asList(new PromotionResultData(), new PromotionResultData());
		final List<PromotionResultData> convertedProductPromotions = Arrays.asList(new PromotionResultData(), new PromotionResultData());

		final PromotionOrderResults promotionOrderResults = Mockito.mock(PromotionOrderResults.class);
		given(promotionOrderResults.getPotentialOrderPromotions()).willReturn(potentialOrderPromotions);
		given(promotionOrderResults.getPotentialProductPromotions()).willReturn(potentialProductPromotions);

		given(promotionsService.getPromotionResults(any(AbstractOrderModel.class))).willReturn(promotionOrderResults);
		given(modelService.getAll(eq(potentialOrderPromotions), anyCollection())).willReturn(orderPromotionModels);
		given(modelService.getAll(eq(potentialProductPromotions), anyCollection())).willReturn(productPromotionModels);
		given(promotionResultConverter.convertAll(eq(orderPromotionModels))).willReturn(convertedOrderPromotions);
		given(promotionResultConverter.convertAll(eq(productPromotionModels))).willReturn(convertedProductPromotions);

		bundleCartPopulator.populate(source, target);

		assertThat(target.getPotentialOrderPromotions()).isEqualTo(convertedOrderPromotions);
		assertThat(target.getPotentialProductPromotions()).isEqualTo(convertedProductPromotions);

		verify(promotionsService).getPromotionResults(source);
		verify(modelService).getAll(potentialOrderPromotions, new ArrayList());
		verify(modelService).getAll(potentialProductPromotions, new ArrayList());
		verify(promotionResultConverter).convertAll(orderPromotionModels);
		verify(promotionResultConverter).convertAll(productPromotionModels);
		verifyNoMoreInteractions(promotionsService, modelService, promotionResultConverter);
	}

	// This method is needed in order for promotionResult.equals() method to work correctly.
	private PromotionResult newPromotionResult(final long pkValue)
	{
		final PK pk = PK.fromLong(pkValue);
		final MockTenant tenant = new MockTenant("tenantId");
		final PromotionResult promotionResult = mock(PromotionResult.class);

		given(promotionResult.getPK()).willReturn(pk);
		given(promotionResult.getTenant()).willReturn(tenant);

		return promotionResult;
	}
}
