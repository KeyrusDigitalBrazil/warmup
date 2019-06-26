///
/// [y] hybris Platform
///
/// Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
///
/// This software is the confidential and proprietary information of SAP
/// ("Confidential Information"). You shall not disclose such Confidential
/// Information and shall use it only in accordance with the terms of the
/// license agreement you entered into with SAP.
///

import {Component} from '@angular/core';
import {PaginationAndSortService} from "../service/pagination-and-sort.service";

@Component({
  selector: 'sort-page-tickets',
  templateUrl: './pagintion-and-sorting.component.html'
})

export class PaginationAndSortComponent {

  selectedSort: string;

  constructor(private pagingAndSortService: PaginationAndSortService) { }

  ngOnInit(): void {
    this.selectedSort = this.pagingAndSortService.getSortValue();
    this.pagingAndSortService.paginationSource$.subscribe(() => {
      this.selectedSort = this.pagingAndSortService.getSortValue();
    });
  }

  sort(selectedSort:string) {
    this.pagingAndSortService.sort(selectedSort);
  }

  next() {
    this.pagingAndSortService.next();
  }

  previous() {
    this.pagingAndSortService.previous();
  }

  goToPage(number: number) {
    if(number !== this.getPageNumber()){
      this.pagingAndSortService.goToPage(number);
    }
  }

  getTotalPages():number{
    return this.pagingAndSortService.getTotalPages();
  }

  getPageNumber():number{
    return this.pagingAndSortService.getPageNumber();
  }

  getSorts():string[] {
    return this.pagingAndSortService.getSorts();
  }

  hasPagingData():any {
    return this.pagingAndSortService.getPagingData();
  }

  createRange(number: number) {
    var items: number[] = [];
    for(var i = 1; i <= number; i++){
      items.push(i);
    }
    return items;
  }
}
