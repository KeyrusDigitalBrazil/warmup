/*
* [y] hybris Platform
*
* Copyright (c) 2018 SAP SE or an SAP affiliate company.
* All rights reserved.
*
* This software is the confidential and proprietary information of SAP
* ("Confidential Information"). You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms of the
* license agreement you entered into with SAP.
*
*/
package de.hybris.platform.yaasconfiguration.client;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import com.hybris.charon.annotations.Control;
import com.hybris.charon.annotations.Http;
import com.hybris.charon.annotations.OAuth;


/**
 * The class of ProductClient.
 */
@Http(value = "product", url = "https://api.yaas.io/hybris/product/v2")
@OAuth
@Control(retries = "${retries}", retriesInterval = "${retriesInterval}", timeout = "${timeout}")
public interface ProductClient
{

	@GET
	@Path("/${tenant}/products")
	@OAuth(scope = "hybris.product_read_unpublished")
	List<Object> getProducts();

	@GET
	@Path("/${tenant}/products/?q=code:{code}")
	@OAuth(scope = "hybris.product_read_unpublished")
	List<Object> getProductByCode(@PathParam("code") String code);


}
