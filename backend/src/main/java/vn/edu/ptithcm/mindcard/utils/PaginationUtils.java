package vn.edu.ptithcm.mindcard.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Sort;

public class PaginationUtils {

    /**
     * Parses a list of sort parameters into a Sort object. Assumes that the
     * parameters have already been validated by @ValidSort.
     *
     * @param sortParams list of sort parameters (e.g. ["createdAt:desc",
     * "name:asc"])
     * @return Sort object
     */
    public static Sort parseSort(List<String> sortParams) {
        if (sortParams == null || sortParams.isEmpty()) {
            return Sort.unsorted();
        }

        List<Sort.Order> orders = new ArrayList<>();
        for (String param : sortParams) {
            if (param == null || param.isBlank()) {
                continue;
            }

            String[] parts = param.split(":");
            String field = parts[0].trim();
            Sort.Direction direction = Sort.Direction.ASC;

            if (parts.length == 2) {
                if (parts[1].trim().equalsIgnoreCase("desc")) {
                    direction = Sort.Direction.DESC;
                }
            }

            orders.add(new Sort.Order(direction, field));
        }

        return Sort.by(orders);
    }
}
