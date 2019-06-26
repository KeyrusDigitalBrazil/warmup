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
	templateUrl: 'duplicatePrimaryContentPageLabelTemplate.html',
	inputs: ['model']
})
export class DuplicatePrimaryContentPageLabelComponent implements ISeComponent {

	// ------------------------------------------------------------------------
	// Constants
	// ------------------------------------------------------------------------
	public ResolutionOptions = {
		OVERWRITE_PAGE: 1,
		RENAME_PAGE_LABEL: 2
	};

	// ------------------------------------------------------------------------
	// Variables
	// ------------------------------------------------------------------------
	public conflictResolution: number = null;
	private model: any;

	// ------------------------------------------------------------------------
	// Lifecycle Methods
	// ------------------------------------------------------------------------
	$onInit(): void {
		this.conflictResolution = this.ResolutionOptions.OVERWRITE_PAGE;
		this.model.replace = true;
	}

	// ------------------------------------------------------------------------
	// Public API
	// ------------------------------------------------------------------------
	public selectResolution(resolutionSelected: number): void {
		this.model.replace = (resolutionSelected === this.ResolutionOptions.OVERWRITE_PAGE) ? true : false;
	}

}