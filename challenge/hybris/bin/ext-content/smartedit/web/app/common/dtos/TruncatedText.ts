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
 * @internal
 * 
 * @name TruncatedText 
 *
 * @description
 * Model containing truncated text properties.
 */
export class TruncatedText {
	constructor(private text: string = "", private truncatedText: string = "", private truncated: boolean, private ellipsis: string = "") {
		// if text/truncatedText is null, then set its value to ""
		this.text = this.text || "";
		this.truncatedText = this.truncatedText || "";
	}

	public getUntruncatedText(): string {
		return this.text;
	}

	public getTruncatedText(): string {
		return this.truncatedText + this.ellipsis;
	}

	public isTruncated(): boolean {
		return this.truncated;
	}
}
