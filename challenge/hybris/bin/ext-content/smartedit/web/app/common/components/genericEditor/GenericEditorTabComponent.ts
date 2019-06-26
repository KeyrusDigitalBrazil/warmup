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
import {ISeComponent, Payload, SeComponent} from "smarteditcommons";
import {GenericEditorComponent} from "smarteditcommons/components/genericEditor/GenericEditorComponent";
import {GenericEditorField} from "smarteditcommons/components/genericEditor/types";

@SeComponent({
	templateUrl: 'genericEditorTabComponentTemplate.html',
	inputs: [
		'tabId'
	],
	require: {
		ge: '^^genericEditor'
	}
})
export class GenericEditorTabComponent implements ISeComponent {

	public ge: GenericEditorComponent;
	public id: string;
	public fields: GenericEditorField[];
	public tabId: string;
	public component: Payload;

	$onInit(): void {
		this.id = this.ge.editor.id;
		this.fields = this.ge.editor.fieldsMap[this.tabId];
		this.component = this.ge.editor.component;
	}

}
