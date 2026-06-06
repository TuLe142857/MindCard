package vn.edu.ptithcm.mindcard.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.edu.ptithcm.mindcard.entity.SavedDeck;

public class SavedDeckSpecification {

    public static Specification<SavedDeck> hasKeyword(String keyword){
        return (root, query, builder) -> {
            if (keyword == null || keyword.isBlank()){
                return null;
            }
            String pattern = "%" + keyword + "%";
            return builder.or(
                    builder.like(root.get("name"), pattern),
                    builder.like(root.get("description"), pattern)
            );
        };
    }
}
