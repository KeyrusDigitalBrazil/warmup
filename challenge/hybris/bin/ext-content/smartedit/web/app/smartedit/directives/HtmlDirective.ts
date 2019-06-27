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

import {SeDirective} from "smarteditcommons/services/dependencyInjection/di";

@SeDirective({
	selector: "html"
})
export class HtmlDirective {

	constructor(private $element: JQuery) {
	}

	$postLink() {
		this.$element.addClass('smartedit-html-container');
	}
}