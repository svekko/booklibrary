import { DATE_PIPE_DEFAULT_OPTIONS } from "@angular/common";

export const dateTimeProvider = {
  provide: DATE_PIPE_DEFAULT_OPTIONS,
  useValue: {
    dateFormat: "dd.MM.YYYY HH:mm",
  },
};
