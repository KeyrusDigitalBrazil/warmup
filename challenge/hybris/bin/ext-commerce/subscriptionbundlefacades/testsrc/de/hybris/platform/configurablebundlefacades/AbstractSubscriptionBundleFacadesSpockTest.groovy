package de.hybris.platform.configurablebundlefacades

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
;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import de.hybris.platform.commercefacades.order.CartFacade
import de.hybris.platform.commercefacades.order.CommerceEntryGroupUtils
import de.hybris.platform.commercefacades.order.EntryGroupData
import de.hybris.platform.commercefacades.order.data.AbstractOrderData
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData
import de.hybris.platform.commercefacades.product.ProductFacade
import de.hybris.platform.commercefacades.product.ProductOption
import de.hybris.platform.commercefacades.product.data.ProductData
import de.hybris.platform.commerceservices.order.CommerceCartModificationException
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.configurablebundlefacades.converters.BundleXStreamConverter
import de.hybris.platform.configurablebundlefacades.data.BundleTemplateData
import de.hybris.platform.configurablebundlefacades.order.BundleCartFacade
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.dto.converter.Converter
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException
import de.hybris.platform.servicelayer.user.UserService
import de.hybris.platform.servicelayer.util.ServicesUtil
import de.hybris.platform.subscriptionfacades.AbstractSubscriptionFacadesSpockTest

import javax.annotation.Nonnull
import javax.annotation.Resource

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.junit.Assert
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component;


/**
 *
 * Keywords from the old ATDD tests - now called from Spock tests
 *
 */
@Component
public abstract class AbstractSubscriptionBundleFacadesSpockTest extends AbstractSubscriptionFacadesSpockTest {

	private static final Logger LOG = Logger.getLogger(AbstractSubscriptionBundleFacadesSpockTest.class);

	/**This constant value is used to represents default quantity of product added to the bundle. */
	private static final long DEFAULT_QUANTITY =1;

	/**This constant value is used to represents default bundle number (-1=create new bundle) */
	private static final int DEFAULT_BUNDLE_NUMBER =-1;

	/**This constant value is used to signal <code>BundleCartFacade</code> to add the given product to the bundle*/
	private static final boolean ADD_CURRENT_PRODUCT=false;

	@Resource
	BundleCartFacade bundleCartFacade;

	@Resource
	@Qualifier("configurableBundleProductFacade")
	private ProductFacade productFacade;

	//	@Resource
	//	private SubscriptionXStreamAliasConverter xStreamAliasConverter;

	@Resource
	private BundleXStreamConverter bundleXStreamConverter;

	@Resource
	private CartFacade cartFacade;

	@Resource
	private CartService cartService;

	@Resource
	private CommerceCartService commerceCartService;

	@Resource
	private UserService userService;

	@Resource
	private Converter<CartData, CartModel> cartConverter;

	@Resource
	private CommerceEntryGroupUtils commerceEntryGroupUtils;

	/**
	 * Find entry within an order by externalReferenceId.
	 *
	 * @param order order data
	 * @param externalReferenceId reference id
	 * @return entry group data
	 * @throws IllegalArgumentException if entry group was not found
	 */
	public EntryGroupData findEntryGroupByRefInOrder(@Nonnull final AbstractOrderData order, final String externalReferenceId)
	{
		ServicesUtil.validateParameterNotNullStandardMessage("order", order);
		ServicesUtil.validateParameterNotNullStandardMessage("order.rootGroups", order.getRootGroups());
		EntryGroupData result;
		for (rootGroup in order.getRootGroups()) {
			for( group in getCommerceEntryGroupUtils().getNestedGroups(rootGroup)) {
				if (group.getExternalReferenceId().equals(externalReferenceId)) {
					result = group
					break
				}
			}
			if (result != null) {
				break
			}
		}
		if (result == null) {
			throw new IllegalArgumentException("The order does not have a group with ref id '" + externalReferenceId + "'")
		}

		return result
	}

	protected CommerceEntryGroupUtils getCommerceEntryGroupUtils()
	{
		return commerceEntryGroupUtils;
	}


	/**
	 * Java implementation of the robot keyword <br>
	 * <p>
	 * <i>verify product xml</i>
	 * <p>
	 *
	 * @param xpath
	 *           the XPath expression to evaluate
	 *
	 * @param expectedXml
	 *           the expected XML
	 *
	 * @param productCode
	 *           code the code of the product to verify
	 *
	 * @param option
	 *           ProductOption
	 */
	public void verifyProductXmlWithOption(final String xpath, final String expectedXml, final String productCode,
			final String... option) {
		try {
			final ProductData product = productFacade.getProductForCodeAndOptions(productCode, getProductOptions(option));
			final String productXml = xStreamAliasConverter.getXStreamXmlFromSubscriptionProductData(product);

			assertXPathEvaluatesTo("The product XML does not match the expectations:", productXml, xpath, expectedXml,
					"transformation/removeElements.xsl");
		}
		catch (final UnknownIdentifierException e) {
			LOG.error("Product with code " + productCode + " does not exist", e);
			fail("Product with code " + productCode + " does not exist");
		}
		catch (final IllegalArgumentException e) {
			LOG.error("Either the expected XML is malformed or the product code is null", e);
			fail("Either the expected XML is malformed or the product code is null");
		}
	}

	/**
	 * Java implementation of the robot keyword <br>
	 * <p>
	 * <i>verify Cart xml</i>
	 * <p>
	 *
	 * @param xpath
	 *           the XPath expression to evaluate
	 *
	 * @param expectedXml
	 *           the expected XML
	 *
	 */
	public void verifyCartXml(final String xpath, final String expectedXml) {
		try {
			final CartData cartData = cartFacade.getSessionCartWithEntryOrdering(true);
			buildProductFromReference(cartData);

			final String cartXml = bundleXStreamConverter.getXStreamXmlFromCartData(cartData);

			assertXPathEvaluatesTo("The Cart XML does not match the expectations:", cartXml, xpath, expectedXml,
					"transformation/removeCartElements.xsl");
		}
		catch (final IllegalArgumentException e) {
			LOG.error("Either the expected XML is malformed or the Card is null", e);
			fail("Either the expected XML is malformed or the Card is null");
		}
	}

	/**
	 * Java implementation of the robot keyword <br>
	 * <p>
	 * <i>verify last modified entries is empty</i>
	 * <p>
	 *
	 * @param cartCode
	 *           the card code to verify
	 *
	 */
	public void verifyLastModifiedEntriesIsEmpty(final String cartCode) {
		final List<CartEntryModel> lastModified = (List<CartEntryModel>) commerceCartService
				.getCartForCodeAndUser(cartCode, userService.getCurrentUser()).getLastModifiedEntries();
		Assert.assertTrue(CollectionUtils.isEmpty(lastModified));
	}

	protected void buildProductFromReference(final CartData cartData) {
		if (cartData != null && CollectionUtils.isNotEmpty(cartData.getEntries())) {
			for (final OrderEntryData entryData : cartData.getEntries()) {
				entryData.setProduct(createProductData(entryData.getProduct()));
				entryData.setComponent(createComponentData(entryData.getComponent()));
			}
		}
	}

	/**
	 * Helper method to re-create ProductData
	 *
	 * @param productData
	 * @return
	 */
	protected ProductData createProductData(final ProductData productData) {
		final ProductData newProduct = new ProductData();
		newProduct.setCode(productData.getCode());
		newProduct.setDisabled(productData.isDisabled());
		return newProduct;
	}

	/**
	 * Helper method to re-create BundleTemplateData
	 *
	 * @param bundleTemplateData
	 * @return
	 */
	protected BundleTemplateData createComponentData(final BundleTemplateData bundleTemplateData) {
		final BundleTemplateData bundleData = new BundleTemplateData();

		bundleData.setId(bundleTemplateData.getId());
		bundleData.setName(bundleData.getName());

		return bundleData;
	}

	/**
	 * Helper method to convert from String array to List of ProductOption.
	 *
	 * @param option
	 * @return
	 */
	protected List<ProductOption> getProductOptions(final String[] option) {
		final List<ProductOption> productOptionList = new ArrayList<ProductOption>();

		for (final String name : option) {
			productOptionList.add(ProductOption.valueOf(name));
		}

		return productOptionList;
	}		/**
	 * Java implementation of the robot keyword <br>
	 *
	 * <p>
	 * <i> add product "${productCode1}" for component "${bundleTemplateId1}" </i>
	 * <p>
	 *
	 * @param productCode Product to add as a code
	 * @param bundleTemplateId Component as bundleTemplateId
	 */
	public void addProductToNewBundle(final String productCode, final String bundleTemplateId) {
		try {
			bundleCartFacade.addToCart(productCode, DEFAULT_QUANTITY, DEFAULT_BUNDLE_NUMBER, bundleTemplateId, ADD_CURRENT_PRODUCT);
		}
		catch(CommerceCartModificationException ex) {
			LOG.error("An exception occured while adding a product to a bundle", ex);
			fail(ex.getMessage());
		}
	}

	/**
	 * Java implementation of the robot keyword <br>
	 *
	 * <p>
	 * <i> add product "${productCode1}" for component "${bundleTemplateId1}" to bundle "${bundleNo}" </i>
	 * <p>
	 *
	 * @param productCode Product to add as a code
	 * @param bundleTemplateId Component as bundleTemplateId
	 * @param bundleNo Bundle number (-1=create new bundle; 0=standalone product/no	bundle; >0=number of existing bundle)
	 */
	public void addProductToExistingBundle(final String productCode, final String bundleTemplateId,final int bundleNo) {
		try {
			bundleCartFacade.addToCart(productCode, DEFAULT_QUANTITY, bundleNo, bundleTemplateId, ADD_CURRENT_PRODUCT);
		}
		catch(CommerceCartModificationException ex) {
			LOG.error("An exception occured while adding a product to a existing bundle : "+bundleNo, ex);
			fail(ex.getMessage());
		}
	}

	/**
	 * Removes content of bundle item in cart with given product.
	 *
	 * @param componentId component id
	 * @param productCode code of product to replace with
	 * @param bundleNo index of bundle in cart
	 *
	 * @throws CommerceCartModificationException
	 */
	public void replaceBundleProduct(final String componentId, final String productCode, final int bundleNo)
	throws CommerceCartModificationException {
		bundleCartFacade.addToCart(productCode, 1, bundleNo, componentId, true);
	}

	/**
	 * Java implementation of the robot keyword <br>
	 *
	 * <p>
	 * <i> add product "${productCode1}" for component "${bundleTemplateId1}" and product "${productCode2}" for component "${bundleTemplateId2}" </i>
	 * <p>
	 *
	 * @param productCode1 Product one to add as a code
	 * @param bundleTemplateId1 Component one  as bundleTemplateId
	 * @param productCode2 Product two to add as a code
	 * @param bundleTemplateId2 Component two as bundleTemplateId
	 */
	public void addTwoProductsToNewBundle(final String productCode1, final String bundleTemplateId1,final String productCode2, final String bundleTemplateId2) {
		try {
			bundleCartFacade.addToCart(productCode1, DEFAULT_BUNDLE_NUMBER, bundleTemplateId1, productCode2, bundleTemplateId2);
		}
		catch(CommerceCartModificationException ex) {
			LOG.error("An exception occured while adding two products to a bundle", ex);
			fail(ex.getMessage());
		}
	}


	/**
	 * Java implementation of the robot keyword <br>
	 *
	 * <p>
	 * <i> add product "${productCode1}" for component "${bundleTemplateId1}" and product "${productCode2}" for component "${bundleTemplateId2}" to bundle "${bundleNo}" </i>
	 * <p>
	 *
	 * @param productCode1 Product one to add as a code	 *
	 * @param bundleTemplateId1 Component one  as bundleTemplateId
	 * @param productCode2 Product one to add as a code
	 * @param bundleTemplateId2 Component two  as bundleTemplateId
	 * @param bundleNo Bundle number(-1=create new bundle; >0=number of existing	bundle; 0=standalone product/no bundle is not allowed here)
	 */
	public void addTwoProductsToExistingBundle(final String productCode1, final String bundleTemplateId1,final String productCode2, final String bundleTemplateId2, final int bundleNo) {
		try {
			bundleCartFacade.addToCart(productCode1, bundleNo, bundleTemplateId1, productCode2, bundleTemplateId2);
		}
		catch(CommerceCartModificationException ex) {
			LOG.error("An exception occured while adding two products to a existing bundle : "+bundleNo, ex);
			fail(ex.getMessage());
		}
	}

	/**
	 * Java implementation of the robot keyword <br>
	 *
	 * <p>
	 * <i >delete bundle "${bundleNo}" from cart </i>
	 * <p>
	 *
	 * @param bundleNo Bundle number to be deleted from the cart
	 */
	public void deleteCartBundle(final int bundleNo) {
		try {
			bundleCartFacade.deleteCartBundle(bundleNo);
		}
		catch(CommerceCartModificationException ex) {
			LOG.error("An exception occured while deleting a bundle from the cart", ex);
			fail(ex.getMessage());
		}
	}

	/**
	 * Java implementation of the robot keyword <br>
	 *
	 * <p>
	 * <i> check the session cart with added bundle is valid </i>
	 * <p>
	 */
	public void verifySessionCartIsValid() {
		boolean value = bundleCartFacade.isCartValid();

		assertTrue("Current session cart status is not as expected: ", value);
	}

	/**
	 * Java implementation of the robot keyword <br>
	 *
	 * <p>
	 * <i> add "${quantity}" items of product "${productCode}" for component "${bundleTemplateId}" to bundle "${bundleNo}" </i>
	 * <p>
	 *
	 * @param quantity Quantity of product items to add (must be 1)
	 * @param productCode Product one to add as a code	 *
	 * @param bundleNo Bundle number(-1=create new bundle; >0=number of existing	bundle; 0=standalone product/no bundle is not allowed here)
	 */
	public void addQuantityOfProductsToExistingBundle(final long quantity, final String productCode, final String bundleTemplateId, final int bundleNo) {
		try {
			bundleCartFacade.addToCart(productCode, quantity, bundleNo, bundleTemplateId, false);
		}
		catch(CommerceCartModificationException ex) {
			LOG.error("An exception occured while adding a product to a existing bundle : "+bundleNo, ex);
			fail(ex.getMessage());
		}
	}
}
