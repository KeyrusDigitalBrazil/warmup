package de.hybris.platform.configurablebundlefacades.converters.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.PromotionResultData;
import de.hybris.platform.configurablebundlefacades.data.BundleTemplateData;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.model.DisableProductBundleRuleModel;
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
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;


/**
 * Unit tests for {@link ProductBundleDisablePopulator}
 */
@UnitTest
public class ProductBundleDisablePopulatorTest
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
	private final ProductBundleDisablePopulator<ProductModel, ProductData> bundleTemplatePopulator = new ProductBundleDisablePopulator<>();
	
	private BundleTemplateData bundleTemplateData;
	
	private ProductModel productModel;
	private ProductModel ruleProduct;
	private ProductData disabledProductData;
	private ProductData productData;

	private static final String RULE_PRODUCT_CODE = "RULE_PRODUCT_CODE";

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		
		bundleTemplateData = new BundleTemplateData();
		
		productModel = new ProductModel();
		productData = new ProductData();
		
		ruleProduct = new ProductModel();
		ruleProduct.setCode(RULE_PRODUCT_CODE);
		
		disabledProductData = new ProductData();
		disabledProductData.setCode(RULE_PRODUCT_CODE);
		
	}

	@Test
	public void shouldThrowExceptionWhenSourceIsNull()
	{

		bundleTemplatePopulator.populate(null, new ProductData());

		assertThat(productData.isDisabled()).isEqualTo(false);
	}

	@Test
	public void shouldThrowExceptionWhenTargetIsNull()
	{

		bundleTemplatePopulator.populate(new ProductModel(), null);
	}

	@Test
	public void testPopulateNoBundleTemplatesAndRules()
	{
		productModel.setConditionalBundleRules(Collections.emptyList());
		productModel.setTargetBundleRules(Collections.emptyList());
		bundleTemplatePopulator.populate(productModel, productData);
		
		assertThat(productData.isDisabled()).isEqualTo(false);
	}

	@Test
	public void testPopulateNoRules()
	{
		productModel.setConditionalBundleRules(Collections.emptyList());
		productModel.setTargetBundleRules(Collections.emptyList());
		
		productData.setBundleTemplates(Collections.singletonList(bundleTemplateData));
		bundleTemplatePopulator.populate(productModel, productData);

		assertThat(productData.isDisabled()).isEqualTo(false);
	}

	@Test
	public void testPopulateOneTargetDisableRule()
	{
		DisableProductBundleRuleModel disableRule = new DisableProductBundleRuleModel();
		disableRule.setConditionalProducts(Collections.singletonList(ruleProduct));
		disableRule.setTargetProducts(Collections.emptyList());
		
		productModel.setConditionalBundleRules(Collections.emptyList());
		productModel.setTargetBundleRules(Collections.singletonList(disableRule));
		
		bundleTemplateData.setId("BUNDLE_TEMPLATE_DATA");
		bundleTemplateData.setProducts(Arrays.asList(productData, disabledProductData));
		
		productData.setBundleTemplates(Collections.singletonList(bundleTemplateData));
		
		bundleTemplatePopulator.populate(productModel, productData);
		
		assertThat(productData.getBundleTemplates().size()).isEqualTo(1);
		assertThat(productData.getBundleTemplates().get(0).getId()).isEqualTo("BUNDLE_TEMPLATE_DATA");
		final List<ProductData> resultProducts = productData.getBundleTemplates().get(0).getProducts();
		assertThat(resultProducts).isNotNull();
		assertThat(resultProducts.size()).isEqualTo(2);
		assertThat(resultProducts).containsExactly(productData, disabledProductData);
		assertThat(productData.isDisabled()).isEqualTo(false);
		assertThat(disabledProductData.isDisabled()).isEqualTo(true);
		
	}

	@Test
	public void testPopulateOneConditionalDisableRule()
	{
		DisableProductBundleRuleModel disableRule = new DisableProductBundleRuleModel();
		disableRule.setConditionalProducts(Collections.emptyList());
		disableRule.setTargetProducts(Collections.singletonList(ruleProduct));
		
		productModel.setConditionalBundleRules(Collections.singletonList(disableRule));
		productModel.setTargetBundleRules(Collections.emptyList());

		bundleTemplateData.setId("BUNDLE_TEMPLATE_DATA");
		bundleTemplateData.setProducts(Arrays.asList(productData, disabledProductData));

		productData.setBundleTemplates(Collections.singletonList(bundleTemplateData));
		
		bundleTemplatePopulator.populate(productModel, productData);

		assertThat(productData.getBundleTemplates().size()).isEqualTo(1);
		final List<ProductData> resultProducts = productData.getBundleTemplates().get(0).getProducts();
		assertThat(resultProducts).isNotNull();
		assertThat(resultProducts.size()).isEqualTo(2);
		assertThat(resultProducts).containsExactly(productData, disabledProductData);
		assertThat(productData.isDisabled()).isEqualTo(false);
		assertThat(disabledProductData.isDisabled()).isEqualTo(true);
	}

	@Test
	public void testPopulateMixedDisableRules()
	{
		ProductModel ruleProduct2 = new ProductModel();
		ruleProduct2.setCode("ANOTHER_PRODUCT_CODE");
		
		ProductData disabledProductData2 = new ProductData();
		disabledProductData2.setCode(ruleProduct2.getCode());
		
		DisableProductBundleRuleModel disableRule1 = new DisableProductBundleRuleModel();
		disableRule1.setConditionalProducts(Collections.singletonList(ruleProduct));
		disableRule1.setTargetProducts(Collections.emptyList());

		DisableProductBundleRuleModel disableRule2 = new DisableProductBundleRuleModel();
		disableRule2.setConditionalProducts(Collections.emptyList());
		disableRule2.setTargetProducts(Collections.singletonList(ruleProduct2));
		
		productModel.setConditionalBundleRules(Collections.singletonList(disableRule2));
		productModel.setTargetBundleRules(Collections.singletonList(disableRule1));

		bundleTemplateData.setId("BUNDLE_TEMPLATE_DATA");
		bundleTemplateData.setProducts(Arrays.asList(productData, disabledProductData, disabledProductData2));
		productData.setBundleTemplates(Collections.singletonList(bundleTemplateData));
		bundleTemplatePopulator.populate(productModel, productData);

		assertThat(productData.getBundleTemplates().size()).isEqualTo(1);
		final List<ProductData> resultProducts = productData.getBundleTemplates().get(0).getProducts();
		assertThat(resultProducts).isNotNull();
		assertThat(resultProducts.size()).isEqualTo(3);
		assertThat(resultProducts).containsExactly(productData, disabledProductData, disabledProductData2);
		assertThat(productData.isDisabled()).isEqualTo(false);
		assertThat(disabledProductData.isDisabled()).isEqualTo(true);
		assertThat(disabledProductData2.isDisabled()).isEqualTo(true);
	}

	@Test
	public void testPopulateDisableRuleTargetSetToConditional()
	{		
		DisableProductBundleRuleModel disableRule = new DisableProductBundleRuleModel();
		disableRule.setConditionalProducts(Collections.singletonList(ruleProduct));
		disableRule.setTargetProducts(Collections.emptyList());
		
		productModel.setConditionalBundleRules(Collections.singletonList(disableRule));
		productModel.setTargetBundleRules(Collections.emptyList());

		productData.setBundleTemplates(Collections.singletonList(bundleTemplateData));
		bundleTemplateData.setProducts(Arrays.asList(productData, disabledProductData));
		bundleTemplatePopulator.populate(productModel, productData);

		assertThat(productData.getBundleTemplates().size()).isEqualTo(1);
		final List<ProductData> resultProducts = productData.getBundleTemplates().get(0).getProducts();
		assertThat(resultProducts).isNotNull();
		assertThat(resultProducts.size()).isEqualTo(2);
		assertThat(resultProducts).containsExactly(productData, disabledProductData);
		assertThat(productData.isDisabled()).isEqualTo(false);
		assertThat(disabledProductData.isDisabled()).isEqualTo(false);
	}

	@Test
	public void testPopulateOneDisableRuleConditionalSetToTarget()
	{		
		DisableProductBundleRuleModel disableRule = new DisableProductBundleRuleModel();
		disableRule.setConditionalProducts(Collections.emptyList());
		disableRule.setTargetProducts(Collections.singletonList(ruleProduct));
		
		productModel.setConditionalBundleRules(Collections.emptyList());
		productModel.setTargetBundleRules(Collections.singletonList(disableRule));

		productData.setBundleTemplates(Collections.singletonList(bundleTemplateData));
		bundleTemplateData.setProducts(Arrays.asList(productData, disabledProductData));
		bundleTemplatePopulator.populate(productModel, productData);

		assertThat(productData.getBundleTemplates().size()).isEqualTo(1);
		final List<ProductData> resultProducts = productData.getBundleTemplates().get(0).getProducts();
		assertThat(resultProducts).isNotNull();
		assertThat(resultProducts.size()).isEqualTo(2);
		assertThat(resultProducts).containsExactly(productData, disabledProductData);
		assertThat(productData.isDisabled()).isEqualTo(false);
		assertThat(disabledProductData.isDisabled()).isEqualTo(false);
	}
}
