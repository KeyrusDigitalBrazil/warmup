// [y] hybris Platform
//
// Copyright (c) 2017 SAP SE or an SAP affiliate company.  All rights reserved.
//
// This software is the confidential and proprietary information of SAP
// ("Confidential Information"). You shall not disclose such Confidential
// Information and shall use it only in accordance with the terms of the
// license agreement you entered into with SAP.
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery
import de.hybris.y2ysync.services.SyncExecutionService

def customerJob = findJob 'c4cCustomerSyncJob'
syncExecutionService.startSync(customerJob, SyncExecutionService.ExecutionMode.SYNC);

def findJob(code) {
	def fQuery = new FlexibleSearchQuery('SELECT {PK} FROM {Y2YSyncJob} WHERE {code}=?code')
	fQuery.addQueryParameter('code', code)
	flexibleSearchService.searchUnique(fQuery)
}
