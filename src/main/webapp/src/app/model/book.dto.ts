import { BookStatus } from "./book-status";

export interface BookDto {
  id: number;
  title: string;
  createdById: number;
  statusId?: BookStatus;
  statusName?: string;
  statusValidTo?: string;
  bookUsedById?: number;
  bookUsedByEmail?: string;
}
