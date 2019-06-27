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
	templateUrl: 'subTypeSelectorTemplate.html',
	inputs: ['subTypes', 'onSubTypeSelected']
})
export class SubTypeSelectorComponent implements ISeComponent {
	onSubTypeSelected: (subType: {id: string, label: string}) => void;
	onChange(subType: {id: string, label: string}): void {
		this.onSubTypeSelected(subType);
	}
}
