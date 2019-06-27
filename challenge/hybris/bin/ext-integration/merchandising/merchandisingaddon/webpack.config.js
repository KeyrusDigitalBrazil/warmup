/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ('Confidential Information'). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
const path = require('path');

module.exports = {
	mode: 'production',
	entry: path.resolve(__dirname, './acceleratoraddon/web/features/carouselinitialiser/main.ts'),
	resolve: {
		extensions: ['.tsx', '.ts', '.js']
	},
	output: {
		filename: 'bundle.js',
		path: path.resolve(__dirname, './jsTarget/dest')
	},
	 // Add the loader for .ts files.
	module: {
		rules: [
			{
				test: /\.ts?$/,
				loader: 'awesome-typescript-loader',
				options: { 
					configFileName:path.resolve(__dirname, './acceleratoraddon/web/features/tsconfig.json')
				}
			}
		]
	}
};
