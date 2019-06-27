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
import {
	IModalService,
	SeInjectable,
	TypedMap
} from 'smarteditcommons';

@SeInjectable()
export class SelectComponentTypeModalService {
	constructor(
		private modalService: IModalService,
	) {}

	open(types: TypedMap<string>): angular.IPromise<string> {
		return this.modalService.open({
			title: 'se.cms.nestedcomponenteditor.select.type',
			templateInline: `<sub-type-selector class="sub-type-selector" data-sub-types="modalController.subTypes"
                data-on-sub-type-selected="modalController.onSubTypeSelected"></sub-type-selector>`,
			controller: function ctrl(modalManager: IModalService) {
				'ngInject';

				this.subTypes = Object.keys(types).map((id: string) => {
					return {
						id,
						label: types[id]
					};
				});

				this.onSubTypeSelected = function(subType: {
					id: string,
					label: string
				}) {
					modalManager.close(subType.id);
				};
			}
		});
	}
}