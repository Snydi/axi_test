package com.axi.loan.client;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ClientService {

    private static final int MAX_PAGE_SIZE = 100;
    private static final Sort DEFAULT_ORDER = Sort.by(Sort.Direction.ASC, "id");

    private final ClientRepository clientRepository;
    private final EmploymentRepository employmentRepository;

    public ClientService(ClientRepository clientRepository, EmploymentRepository employmentRepository) {
        this.clientRepository = clientRepository;
        this.employmentRepository = employmentRepository;
    }

    @Transactional(readOnly = true)
    public ClientPageResponse getClients(int requestedPage, int requestedSize) {
        int pageNumber = Math.max(requestedPage, 0);
        int pageSize = Math.clamp(requestedSize, 1, MAX_PAGE_SIZE);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, DEFAULT_ORDER);
        Page<Client> clientPage = clientRepository.findAll(pageable);

        List<Long> clientIds = clientPage.stream().map(Client::getId).toList();
        Map<Long, List<Employment>> employmentsByClientId = clientIds.isEmpty()
                ? Map.of()
                : employmentRepository.findAllByClientIdInOrderByEmployedFromDesc(clientIds).stream()
                        .collect(Collectors.groupingBy(employment -> employment.getClient().getId()));

        List<ClientResponse> clients = clientPage.stream()
                .map(client -> ClientResponse.from(
                        client,
                        employmentsByClientId.getOrDefault(client.getId(), List.of())))
                .toList();

        return new ClientPageResponse(
                clients,
                clientPage.getNumber(),
                clientPage.getSize(),
                clientPage.getTotalElements(),
                clientPage.getTotalPages());
    }
}
