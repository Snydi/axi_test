package com.axi.loan.client;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

final class ClientSpecifications {

    private ClientSpecifications() {
    }

    static Specification<Client> withFilters(String name, String phone, String passport) {
        return Specification.allOf(
                name.isEmpty() ? Specification.unrestricted() : nameContains(name),
                phone.isEmpty() ? Specification.unrestricted() : phoneContains(phone),
                passport.isEmpty() ? Specification.unrestricted() : passportContains(passport));
    }

    private static Specification<Client> nameContains(String name) {
        return (root, query, builder) -> {
            Expression<String> firstName = root.get("firstName");
            Expression<String> lastName = root.get("lastName");
            Expression<String> middleName = builder.coalesce(root.<String>get("middleName"), "");
            String pattern = "%" + name + "%";

            return builder.or(
                    builder.like(builder.lower(spaced(builder, lastName, firstName, middleName)), pattern),
                    builder.like(builder.lower(spaced(builder, firstName, middleName, lastName)), pattern));
        };
    }

    private static Specification<Client> phoneContains(String phone) {
        return (root, query, builder) ->
                builder.like(normalizedPhone(root, builder), "%" + phone + "%");
    }

    private static Specification<Client> passportContains(String passport) {
        return (root, query, builder) -> {
            var joinedPassport = root.<Client, Passport>join("passport", JoinType.LEFT);
            Expression<String> passportNumber = builder.lower(
                    builder.concat(joinedPassport.get("series"), joinedPassport.get("number")));
            return builder.like(passportNumber, "%" + passport + "%");
        };
    }

    private static Expression<String> normalizedPhone(Root<Client> root, CriteriaBuilder builder) {
        Expression<String> phone = builder.lower(root.get("phone"));
        for (String character : List.of(" ", "(", ")", "-", "+")) {
            phone = builder.function(
                    "replace",
                    String.class,
                    phone,
                    builder.literal(character),
                    builder.literal(""));
        }
        return phone;
    }

    private static Expression<String> spaced(
            CriteriaBuilder builder,
            Expression<String> first,
            Expression<String> second
    ) {
        return builder.concat(builder.concat(first, " "), second);
    }

    private static Expression<String> spaced(
            CriteriaBuilder builder,
            Expression<String> first,
            Expression<String> second,
            Expression<String> third
    ) {
        return spaced(builder, spaced(builder, first, second), third);
    }
}
