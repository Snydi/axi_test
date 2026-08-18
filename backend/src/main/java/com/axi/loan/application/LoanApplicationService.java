package com.axi.loan.application;

import com.axi.loan.agreement.LoanAgreement;
import com.axi.loan.agreement.LoanAgreementRepository;
import com.axi.loan.client.Client;
import com.axi.loan.client.ClientRepository;
import com.axi.loan.client.Employment;
import com.axi.loan.client.EmploymentRepository;
import com.axi.loan.client.Passport;
import com.axi.loan.client.PassportRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class LoanApplicationService {

    private static final int MAX_PAGE_SIZE = 100;
    private final LoanApplicationRepository repository;
    private final ClientRepository clientRepository;
    private final PassportRepository passportRepository;
    private final EmploymentRepository employmentRepository;
    private final LoanDecisionRepository decisionRepository;
    private final LoanAgreementRepository agreementRepository;

    @Autowired
    public LoanApplicationService(
            LoanApplicationRepository repository,
            ClientRepository clientRepository,
            PassportRepository passportRepository,
            EmploymentRepository employmentRepository,
            LoanDecisionRepository decisionRepository,
            LoanAgreementRepository agreementRepository
    ) {
        this.repository = repository;
        this.clientRepository = clientRepository;
        this.passportRepository = passportRepository;
        this.employmentRepository = employmentRepository;
        this.decisionRepository = decisionRepository;
        this.agreementRepository = agreementRepository;
    }

    @Transactional(readOnly = true)
    public LoanApplicationPageResponse getApplications(int requestedPage, int requestedSize) {
        int page = Math.max(requestedPage, 0);
        int size = Math.clamp(requestedSize, 1, MAX_PAGE_SIZE);
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"));
        var result = repository.findAll(pageable);

        return new LoanApplicationPageResponse(
                result.stream().map(LoanApplicationResponse::from).toList(),
                result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages());
    }

    @Transactional
    public LoanApplicationSubmissionResponse submitApplication(LoanApplicationSubmissionRequest request) {
        String phone = normalizePhone(request.phone());
        if (clientRepository.existsByPhone(phone)) {
            throw new ApplicationSubmissionConflictException("phone", "Клиент с таким телефоном уже существует");
        }
        if (passportRepository.existsBySeriesAndNumber(request.passportSeries(), request.passportNumber())) {
            throw new ApplicationSubmissionConflictException("passportNumber", "Клиент с таким паспортом уже существует");
        }

        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        Client client = clientRepository.save(new Client(
                clean(request.firstName()), clean(request.lastName()), cleanNullable(request.middleName()), phone,
                clean(request.residentialAddress()), clean(request.registrationAddress()),
                request.gender(), request.maritalStatus(), now));

        passportRepository.save(new Passport(
                client, request.passportSeries(), request.passportNumber(), request.passportDepartmentCode(),
                clean(request.passportIssuedBy()), request.passportIssueDate()));
        employmentRepository.save(new Employment(
                client, request.employedFrom(), request.employedTo(), clean(request.position()),
                clean(request.organizationName())));

        LoanApplication application = repository.save(new LoanApplication(
                client, request.requestedAmount(), clean(request.purpose()), now));

        boolean approved = ThreadLocalRandom.current().nextBoolean();
        LoanDecisionStatus status = approved ? LoanDecisionStatus.APPROVED : LoanDecisionStatus.DENIED;
        Short termMonths = approved ? (short) ThreadLocalRandom.current().nextInt(1, 13) : null;
        var approvedAmount = approved ? request.requestedAmount() : null;
        LoanDecision decision = decisionRepository.save(new LoanDecision(
                application, status, approvedAmount, termMonths, now));

        String agreementNumber = null;
        if (approved) {
            agreementNumber = "КД-%d-%08d".formatted(now.getYear(), application.getId());
            agreementRepository.save(new LoanAgreement(application, decision, agreementNumber));
        }

        String message = approved
                ? "Заявка одобрена. Договор подготовлен к подписанию."
                : "По заявке принято решение об отказе.";
        return new LoanApplicationSubmissionResponse(
                application.getId(), status, approvedAmount, termMonths, agreementNumber, message);
    }

    private String normalizePhone(String value) {
        String digits = value.replaceAll("\\D", "");
        if (digits.length() == 11 && digits.startsWith("8")) {
            digits = "7" + digits.substring(1);
        }
        return "+" + digits;
    }

    private String clean(String value) {
        return value.trim().replaceAll("\\s+", " ");
    }

    private String cleanNullable(String value) {
        return value == null || value.isBlank() ? null : clean(value);
    }
}
