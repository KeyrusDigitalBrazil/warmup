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

import {ISeComponent, SeComponent} from 'smarteditcommons';

@SeComponent({
	templateUrl: 'duplicatePrimaryNonContentPageTemplate.html',
	inputs: ['model']
})
export class DuplicatePrimaryNonContentPageComponent implements ISeComponent {

	// ------------------------------------------------------------------------
	// Constants
	// ------------------------------------------------------------------------
	public PRODUCT_PAGE = 'ProductPage';

	// ------------------------------------------------------------------------
	// Variables
	// ------------------------------------------------------------------------
	public model: any;
	public ge: any;
	public label: string;

	// ------------------------------------------------------------------------
	// Lifecycle methods
	// ------------------------------------------------------------------------
	$onInit(): void {
		this.model.replace = true;
		this.label = (this.model.typeCode === 'PRODUCT_PAGE') ?
			'se.cms.page.restore.category.duplicate.primaryforvariation.error' :
			'se.cms.page.restore.product.duplicate.primaryforvariation.error';
	}
}