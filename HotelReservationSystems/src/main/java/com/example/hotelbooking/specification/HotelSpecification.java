package com.example.hotelbooking.specification;

import com.example.hotelbooking.entity.Hotel;
import org.springframework.data.jpa.domain.Specification;

public class HotelSpecification {

    public static Specification<Hotel> hasId(Long id) {
        return (root, query, cb) -> id == null ? null : cb.equal(root.get("id"), id);
    }

    public static Specification<Hotel> hasName(String name) {
        return (root, query, cb) -> {
            if (name == null || name.trim().isEmpty()) return null;
            return cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
        };
    }

    public static Specification<Hotel> hasTitle(String title) {
        return (root, query, cb) -> {
            if (title == null || title.trim().isEmpty()) return null;
            return cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
        };
    }

    public static Specification<Hotel> hasCity(String city) {
        return (root, query, cb) -> {
            if (city == null || city.trim().isEmpty()) return null;
            return cb.like(cb.lower(root.get("city")), "%" + city.toLowerCase() + "%");
        };
    }

    public static Specification<Hotel> hasAddress(String address) {
        return (root, query, cb) -> {
            if (address == null || address.trim().isEmpty()) return null;
            return cb.like(cb.lower(root.get("address")), "%" + address.toLowerCase() + "%");
        };
    }

    public static Specification<Hotel> hasDistance(Double distance) {
        return (root, query, cb) -> distance == null ? null : cb.equal(root.get("distanceFromCenter"), distance);
    }

    public static Specification<Hotel> hasRating(Double rating) {
        return (root, query, cb) -> rating == null ? null : cb.equal(root.get("rating"), rating);
    }

    public static Specification<Hotel> hasNumberOfRatings(Integer count) {
        return (root, query, cb) -> count == null ? null : cb.equal(root.get("numberOfRatings"), count);
    }
}
