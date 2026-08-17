package com.axi.loan.client;

import java.util.List;

public record ClientPageResponse(
        List<ClientResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}
