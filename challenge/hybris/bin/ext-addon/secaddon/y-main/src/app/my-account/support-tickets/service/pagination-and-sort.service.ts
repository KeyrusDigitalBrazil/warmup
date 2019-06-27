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

import {Injectable} from "@angular/core";
import {PagingData} from "../models/pagingData";
import {Subject} from "rxjs/Subject";

const DEFAULT_SORT:string = "metadata.modifiedAt:desc";

@Injectable()
export class PaginationAndSortService {

  private pagingData: PagingData = new PagingData(1,5,DEFAULT_SORT);
  private totalPages: number;
  private paginationSource = new Subject<PagingData>();
  private totalItems: number;
  public paginationSource$ = this.paginationSource.asObservable();

  public sortMap: Map<string, string> =
    new Map([["DATE UPDATED DESC", "metadata.modifiedAt:desc"],
      ["DATE UPDATED ASC", "metadata.modifiedAt:asc"],
      ["TICKET ID DESC", "id:desc"],
      ["TICKET ID ASC", "id:asc"]]);

  getSorts():string[] {
    return Array.from(this.sortMap.keys());
  }

  next() {
    if (this.pagingData.pageNumber < this.totalPages) {
      this.pagingData.pageNumber = this.pagingData.pageNumber + 1;
      this.setPagination(this.pagingData);
    }
  }

  previous() {
    if (this.pagingData.pageNumber !== 1) {
      this.pagingData.pageNumber = this.pagingData.pageNumber - 1;
      this.setPagination(this.pagingData);
    }
  }

  goToPage(number: number) {
    this.pagingData.pageNumber = number;
    this.setPagination(this.pagingData);
  }

  sort(selectedSort:string) {
    this.pagingData.sort = this.getSortKey(selectedSort);
    this.setPagination(this.pagingData);
  }

  setTotalItems(totalItems: number) {
    this.totalItems = totalItems;
    this.totalPages = Math.ceil(totalItems / this.pagingData.pageSize);
  }

  setPagination(pageableData: PagingData) {
    this.pagingData = pageableData;
    this.paginationSource.next(this.pagingData);
  }

  getSortKey(value: string): string {
    return this.sortMap.has(value)
      ? this.sortMap.get(value)
      : DEFAULT_SORT;
  }

  getSortValue(): string {
    var val = Array.from(this.sortMap.keys())[0];
    this.sortMap.forEach((key, value) => {
      if (key===this.pagingData.sort) {
        val = value;
        return;
      }
    });
    return val;
  }

  getPagingData():PagingData{
    return this.pagingData;
  }

  getPageNumber():number{
    return this.pagingData.pageNumber;
  }

  getTotalPages():number{
    return this.totalPages;
  }
}
