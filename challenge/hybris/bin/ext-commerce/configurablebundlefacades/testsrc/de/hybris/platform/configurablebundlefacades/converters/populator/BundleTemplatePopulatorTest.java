package de.hybris.platform.configurablebundlefacades.converters.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.PromotionResultData;
import de.hybris.platform.configurablebundlefacades.data.BundleTemplateData;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.configurablebundleservices.model.PickExactlyNBundleSelectionCriteriaModel;
import de.hybris.platform.configurablebundleservices.model.PickNToMBundleSelectionCriteriaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.promotions.model.PromotionResultModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;


/**
 * Unit tests for {@link BundleTemplatePopulator}
 */
@UnitTest
public class BundleTemplatePopulatorTest
{
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private BundleTemplateService bundleTemplateService;
	@Mock
	private ModelService modelService;
	@Mock
	private Converter<PromotionResultModel, PromotionResultData> promotionResultConverter;
	@InjectMocks
	private final BundleTemplatePopulator<BundleTemplateModel, BundleTemplateData> bundleTemplatePopulator = new BundleTemplatePopulator<>();

	@Mock
	private BundleTemplateModel root;
	@Mock
	private BundleTemplateModel intermediate;
	@Mock
	private BundleTemplateModel leaf;
	
	private BundleTemplateData target;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		bundleTemplatePopulator.setBundleTemplateService(bundleTemplateService);
		
		when(root.getId()).thenReturn("1");
		when(root.getVersion()).thenReturn("1");
		when(root.getParentTemplate()).thenReturn(null);
		when(root.getName()).thenReturn("Root");
		
		when(intermediate.getId()).thenReturn("2");
		when(intermediate.getVersion()).thenReturn("2");
		when(intermediate.getParentTemplate()).thenReturn(root);
		when(intermediate.getName()).thenReturn("Intermediate");
		
		when(leaf.getId()).thenReturn("3");
		when(leaf.getVersion()).thenReturn("3");
		when(leaf.getParentTemplate()).thenReturn(intermediate);
		when(leaf.getName()).thenReturn("Leaf");
		
		when(bundleTemplateService.getRootBundleTemplate(any(BundleTemplateModel.class))).thenReturn(root);
		
		target = new BundleTemplateData();
	}

	@Test
	public void shouldThrowExceptionWhenSourceIsNull()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Parameter source can not be null");

		bundleTemplatePopulator.populate(null, new BundleTemplateData());
	}

	@Test
	public void shouldThrowExceptionWhenTargetIsNull()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Parameter target can not be null");

		bundleTemplatePopulator.populate(new BundleTemplateModel(), null);
	}

	@Test
	public void testPopulateRootBundleTemplate()
	{
		bundleTemplatePopulator.populate(root, target);
		
		assertThat(target.getId()).isEqualTo(root.getId());
		assertThat(target.getName()).isEqualTo(root.getName());
		assertThat(target.getVersion()).isEqualTo(root.getVersion());
		assertThat(target.getRootBundleTemplateName()).isEqualTo(root.getName());
	}

	@Test
	public void testPopulateIntermediateBundleTemplate()
	{
		bundleTemplatePopulator.populate(intermediate, target);

		assertThat(target.getId()).isEqualTo(intermediate.getId());
		assertThat(target.getName()).isEqualTo(intermediate.getName());
		assertThat(target.getVersion()).isEqualTo(intermediate.getVersion());
		assertThat(target.getRootBundleTemplateName()).isEqualTo(root.getName());
	}

	@Test
	public void testPopulateLeafBundleTemplate()
	{
		bundleTemplatePopulator.populate(leaf, target);

		assertThat(target.getId()).isEqualTo(leaf.getId());
		assertThat(target.getName()).isEqualTo("Intermediate - Leaf");
		assertThat(target.getVersion()).isEqualTo(leaf.getVersion());
		assertThat(target.getRootBundleTemplateName()).isEqualTo(root.getName());
	}

	@Test
	public void testPopulateBundleTemplateHasNoProducts()
	{
		leaf.setProducts(Collections.emptyList());

		bundleTemplatePopulator.populate(leaf, target);

		assertThat(target.getType()).isNull();
	}
	
	@Test
	public void testPopulateBundleTemplateHasOneProduct()
	{
		final ProductModel product = new ProductModel();
		product.setCode("PRODUCT");
		when(leaf.getProducts()).thenReturn(Collections.singletonList(product));
		
		bundleTemplatePopulator.populate(leaf, target);

		assertThat(target.getType()).isEqualTo(product.getClass().getSimpleName());
	}

	@Test
	public void testPopulateBundleTemplateHasTwoProducts()
	{
		final ProductModel product1 = new ProductModel();
		product1.setCode("PRODUCT1");

		final ProductModel product2 = new ProductModel();
		product2.setCode("PRODUCT2");
		when(leaf.getProducts()).thenReturn(Arrays.asList(product1, product2));

		bundleTemplatePopulator.populate(leaf, target);

		assertThat(target.getType()).isEqualTo(product1.getClass().getSimpleName());
	}

	@Test
	public void testPopulateBundleTemplateHasNoSelectionCriteria()
	{
		bundleTemplatePopulator.populate(leaf, target);

		assertThat(target.getMaxItemsAllowed()).isEqualTo(0);
	}
	
	@Test
	public void testPopulateBundleTemplateHasNtoMCriteria()
	{
		PickNToMBundleSelectionCriteriaModel selectionCriteria = new PickNToMBundleSelectionCriteriaModel();
		selectionCriteria.setN(10);
		selectionCriteria.setM(20);

		when(leaf.getBundleSelectionCriteria()).thenReturn(selectionCriteria);
		
		bundleTemplatePopulator.populate(leaf, target);

		assertThat(target.getMaxItemsAllowed()).isEqualTo(selectionCriteria.getM());
	}

	@Test
	public void testPopulateBundleTemplateHasExactlyNCriteria()
	{
		PickExactlyNBundleSelectionCriteriaModel selectionCriteria = new PickExactlyNBundleSelectionCriteriaModel();
		selectionCriteria.setN(10);

		when(leaf.getBundleSelectionCriteria()).thenReturn(selectionCriteria);

		bundleTemplatePopulator.populate(leaf, target);

		assertThat(target.getMaxItemsAllowed()).isEqualTo(selectionCriteria.getN());
	}
}
