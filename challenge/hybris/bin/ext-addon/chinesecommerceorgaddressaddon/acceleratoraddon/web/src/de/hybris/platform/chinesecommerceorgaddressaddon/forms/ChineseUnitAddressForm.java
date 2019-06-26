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
package de.hybris.platform.chinesecommerceorgaddressaddon.forms;


import de.hybris.platform.acceleratorstorefrontcommons.forms.AddressForm;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;


/**
 * This is chinese address form which adds four more fields(cityIso,districtIso,cellphone,fullname)
 */
public class ChineseUnitAddressForm extends AddressForm
{
	private String cityIso;
	private String districtIso;
	private String cellphone;
	private String fullname;

	@NotNull(message = "{address.city.required}")
	public String getCityIso()
	{
		return cityIso;
	}

	public void setCityIso(final String cityIso)
	{
		this.cityIso = cityIso;
	}

	@NotNull(message = "{address.district.required}")
	public String getDistrictIso()
	{
		return districtIso;
	}

	public void setDistrictIso(final String districtIso)
	{
		this.districtIso = districtIso;
	}

	@NotNull(message = "{address.cellphone.invalid}")
	@Pattern(regexp = "^(\\+)?(\\d{2,3})?(\\s)?(\\d{11})$", message = "{address.cellphone.invalid}")
	public String getCellphone()
	{
		return cellphone;
	}

	public void setCellphone(final String cellphone)
	{
		this.cellphone = cellphone;
	}

	@NotNull(message = "{address.fullname.required}")
	@Size(max = 255, message = "{address.maxlength}")
	public String getFullname()
	{
		return fullname;
	}

	public void setFullname(final String fullname)
	{
		this.fullname = fullname;
	}

}
