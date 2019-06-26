/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
/**
 * @description
 * Interface for dropdownItem Object for {@link yDropDownMenuModule.directive:yDropDownMenu yDropDownMenu} component.
 */
export interface IYDropdownMenuItem {
	key?: string;
	icon?: string;
	template?: string;
	templateUrl?: boolean;
	callback?(): void;
	condition?(): void;
}
