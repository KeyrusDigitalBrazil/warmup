package de.hybris.platform.configurablebundlefacades.converters.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.configurablebundlefacades.data.BundleTemplateData;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.model.BundleSelectionCriteriaModel;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.model.product.ProductModel;
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
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;


/**
 * Unit tests for {@link ProductBundleStarterPopulator}
 */
@UnitTest
public class ProductBundleStarterPopulatorTest
{
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private ModelService modelService;
	@Mock
	private BundleTemplateService bundleTemplateService;
	@Mock
	private Converter<BundleTemplateModel, BundleTemplateData> bundleTemplateConverter;
	@InjectMocks
	private final ProductBundleStarterPopulator<ProductModel, ProductData> bundleStarterPopulator = new ProductBundleStarterPopulator<>();
	
	private BundleTemplateModel starterBundleTemplate;
	private BundleTemplateModel notStarterBundleTemplate;
	private BundleTemplateData starterData;
	private BundleTemplateData notStarterData;
	private BundleSelectionCriteriaModel starterSelectionCriteria;
	private BundleSelectionCriteriaModel notStarterSelectionCriteria;

	private ProductModel product;
	private ProductData target;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		
		starterSelectionCriteria = new BundleSelectionCriteriaModel();
		starterSelectionCriteria.setStarter(true);
		
		starterBundleTemplate = new BundleTemplateModel();
		starterBundleTemplate.setId("Starter");
		starterBundleTemplate.setBundleSelectionCriteria(starterSelectionCriteria);

		notStarterSelectionCriteria = new BundleSelectionCriteriaModel();
		notStarterSelectionCriteria.setStarter(false);

		notStarterBundleTemplate = new BundleTemplateModel();
		notStarterBundleTemplate.setId("Non-starter");
		notStarterBundleTemplate.setBundleSelectionCriteria(notStarterSelectionCriteria);
		
		starterData = new BundleTemplateData();
		starterData.setId("Starter");

		notStarterData = new BundleTemplateData();
		notStarterData.setId("Starter");
		
		when(bundleTemplateConverter.convertAll(eq(Arrays.asList(starterBundleTemplate,notStarterBundleTemplate))))
				.thenReturn(Arrays.asList(starterData, notStarterData));
		when(bundleTemplateConverter.convertAll(eq(Collections.singletonList(starterBundleTemplate)))).thenReturn(
				Collections.singletonList(starterData));
		when(bundleTemplateConverter.convertAll(eq(Collections.singletonList(notStarterBundleTemplate)))).thenReturn(
				Collections.singletonList(notStarterData));
		
		target = new ProductData();
		
		product = new ProductModel();
	}

	@Test
	public void shouldThrowExceptionWhenSourceIsNull()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Parameter source can not be null");

		bundleStarterPopulator.populate(null, new ProductData());
	}

	@Test
	public void shouldThrowExceptionWhenTargetIsNull()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Parameter target can not be null");

		bundleStarterPopulator.populate(new ProductModel(), null);
	}

	@Test
	public void testPopulateProductWithNoBundleTemplates()
	{
		product.setBundleTemplates(Collections.emptyList());
		when(bundleTemplateService.getBundleTemplatesByProduct(any(ProductModel.class))).thenReturn(Collections.emptyList());

		bundleStarterPopulator.populate(product, target);

		assertThat(target.getBundleTemplates()).isNullOrEmpty();
	}

	@Test
	public void testPopulateProductWithStarterBundleTemplate()
	{		
		when(bundleTemplateService.getBundleTemplatesByProduct(any(ProductModel.class))).thenReturn(Collections.singletonList(starterBundleTemplate));
		
		product.setBundleTemplates(Collections.singletonList(starterBundleTemplate));

		bundleStarterPopulator.populate(product, target);

		assertThat(target.getBundleTemplates()).containsExactly(starterData);
	}

	@Test
	public void testPopulateProductWithNonStarterBundleTemplate()
	{
		when(bundleTemplateService.getBundleTemplatesByProduct(any(ProductModel.class))).thenReturn(
				Collections.singletonList(notStarterBundleTemplate));

		product.setBundleTemplates(Collections.singletonList(notStarterBundleTemplate));

		bundleStarterPopulator.populate(product, target);

		assertThat(target.getBundleTemplates()).isNullOrEmpty();
	}

	@Test
	public void testPopulateProductWithMixedBundleTemplates()
	{
		when(bundleTemplateService.getBundleTemplatesByProduct(any(ProductModel.class))).thenReturn(
				Arrays.asList(notStarterBundleTemplate, starterBundleTemplate));

		product.setBundleTemplates(Arrays.asList(notStarterBundleTemplate, starterBundleTemplate));

		bundleStarterPopulator.populate(product, target);

		assertThat(target.getBundleTemplates()).containsExactly(starterData);
	}
}
