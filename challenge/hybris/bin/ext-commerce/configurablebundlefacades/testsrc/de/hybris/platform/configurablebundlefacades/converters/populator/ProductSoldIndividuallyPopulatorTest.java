package de.hybris.platform.configurablebundlefacades.converters.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertTrue;

/**
 * JUnit test suite for {@link}
 */
@UnitTest
public class ProductSoldIndividuallyPopulatorTest
{
	private ProductSoldIndividuallyPopulator<ProductModel, ProductData> soldIndividuallyPopulator;

	private ProductModel productModel;
	private ProductData productData;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setUp()
	{
		soldIndividuallyPopulator = new ProductSoldIndividuallyPopulator<>();
		productModel = new ProductModel();
		productModel.setSoldIndividually(true);
		productData = new ProductData();
	}

	@Test
	public void shouldNotAllowNullProductData()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("productData");

		soldIndividuallyPopulator.populate(productModel, null);
	}

	@Test
	public void shouldNotAllowNullProductModel()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("productModel");

		soldIndividuallyPopulator.populate(null, productData);
	}

	@Test
	public void shouldPopulateSoldIndividually()
	{
		soldIndividuallyPopulator.populate(productModel, productData);

		assertTrue(productData.isSoldIndividually());
	}
}
