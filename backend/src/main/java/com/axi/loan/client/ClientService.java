package com.axi.loan.client;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class ClientService {

    private static final int MAX_PAGE_SIZE = 100;
    private static final Sort DEFAULT_ORDER = Sort.by(Sort.Direction.ASC, "id");

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Transactional(readOnly = true)
    public ClientPageResponse getClients(
            int requestedPage,
            int requestedSize,
            String requestedName,
            String requestedPhone,
            String requestedPassport
    ) {
        int pageNumber = Math.max(requestedPage, 0);
        int pageSize = Math.clamp(requestedSize, 1, MAX_PAGE_SIZE);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, DEFAULT_ORDER);
        String name = normalizeText(requestedName);
        String phone = normalizeIdentifier(requestedPhone);
        String passport = normalizeIdentifier(requestedPassport);

        Page<Client> clientPage = name.isEmpty() && phone.isEmpty() && passport.isEmpty()
                ? clientRepository.findAll(pageable)
                : clientRepository.findAll(
                        ClientSpecifications.withFilters(name, phone, passport),
                        pageable);

        var clients = clientPage.stream()
                .map(ClientResponse::from)
                .toList();

        return new ClientPageResponse(
                clients,
                clientPage.getNumber(),
                clientPage.getSize(),
                clientPage.getTotalElements(),
                clientPage.getTotalPages());
    }

    String normalizeText(String value) {
        return value == null
                ? ""
                : value.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }

    String normalizeIdentifier(String value) {
        return value == null
                ? ""
                : value.replaceAll("[^\\p{L}\\p{N}]", "").toLowerCase(Locale.ROOT);
    }
}
