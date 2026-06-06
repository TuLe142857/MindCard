package vn.edu.ptithcm.mindcard.repository.specification;

import org.springframework.data.jpa.domain.Specification;

import vn.edu.ptithcm.mindcard.entity.Deck;

public class DeckSpecification {

    /**
     * Finds decks where the keyword matches the deck name, description, or the
     * topic name. Performs a case-insensitive search with wildcards
     * (%keyword%).
     *
     * @param keyword the search term
     * @return a {@link Specification} for the query
     */
    public static Specification<Deck> hasKeyword(String keyword) {
        return (root, query, builder) -> {
            if (keyword == null || keyword.isBlank()) {
                return null;
            }

            // Generate: %keyword% (for LIKE query)
            String pattern = "%" + keyword.toLowerCase().trim() + "%";

            return builder.or(
                    builder.like(builder.lower(root.get("name")), pattern),
                    builder.like(builder.lower(root.get("description")), pattern),
                    builder.like(builder.lower(root.get("topic").get("name")), pattern)
            );
        };
    }

    /**
     * Filters decks by a specific topic ID.
     *
     * @param topicId the ID of the topic
     * @return a {@link Specification} for the query
     */
    public static Specification<Deck> hasTopicId(Integer topicId) {
        return (root, query, builder) -> {
            if (topicId == null) {
                return null;
            }
            return builder.equal(root.get("topic").get("id"), topicId);
        };
    }

    /**
     * Filters decks by their visibility (e.g., PUBLIC or PRIVATE).
     *
     * @param visibility the visibility status
     * @return a {@link Specification} for the query
     */
    public static Specification<Deck> hasVisibility(Deck.DeckVisibility visibility) {
        return (root, query, builder) -> {
            if (visibility == null) {
                return null;
            }
            return builder.equal(root.get("visibility"), visibility);
        };
    }

    /**
     * Filters decks owned by a specific user ID.
     *
     * @param ownerId the user ID of the deck owner
     * @return a {@link Specification} for the query
     */
    public static Specification<Deck> hasOwnerId(Integer ownerId) {
        return (root, query, builder) -> {
            if (ownerId == null) {
                return null;
            }
            return builder.equal(root.get("owner").get("id"), ownerId);
        };
    }

    /**
     * Filters decks owned by a specific username.
     *
     * @param ownerUsername the username of the deck owner
     * @return a {@link Specification} for the query
     */
    public static Specification<Deck> hasOwnerUsername(String ownerUsername) {
        return (root, query, builder) -> {
            if (ownerUsername == null || ownerUsername.isBlank()) {
                return null;
            }
            return builder.equal(root.get("owner").get("username"), ownerUsername);
        };
    }

    /**
     * Filters decks by their deletion status.
     *
     * @param isDeleted true to find deleted decks, false for active decks
     * @return a {@link Specification} for the query
     */
    public static Specification<Deck> isDeleted(Boolean isDeleted) {
        return (root, query, builder) -> {
            if (isDeleted == null) {
                return null;
            }
            return builder.equal(root.get("isDeleted"), isDeleted);
        };
    }
}
