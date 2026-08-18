package com.axi.loan.application;

import com.axi.loan.client.Gender;
import com.axi.loan.client.MaritalStatus;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LoanApplicationSubmissionRequest(
        @NotBlank(message = "Укажите фамилию")
        @Size(max = 100, message = "Фамилия не должна превышать 100 символов")
        String lastName,
        @NotBlank(message = "Укажите имя")
        @Size(max = 100, message = "Имя не должно превышать 100 символов")
        String firstName,
        @Size(max = 100, message = "Отчество не должно превышать 100 символов")
        String middleName,
        @NotBlank(message = "Укажите серию паспорта")
        @Pattern(regexp = "[0-9]{4}", message = "Серия паспорта должна состоять из 4 цифр")
        String passportSeries,
        @NotBlank(message = "Укажите номер паспорта")
        @Pattern(regexp = "[0-9]{6}", message = "Номер паспорта должен состоять из 6 цифр")
        String passportNumber,
        @NotBlank(message = "Укажите код подразделения")
        @Pattern(regexp = "[0-9]{3}-[0-9]{3}", message = "Код подразделения должен иметь формат 000-000")
        String passportDepartmentCode,
        @NotBlank(message = "Укажите, кем выдан паспорт")
        @Size(max = 255, message = "Наименование органа не должно превышать 255 символов")
        String passportIssuedBy,
        @NotNull(message = "Укажите дату выдачи паспорта")
        @PastOrPresent(message = "Дата выдачи паспорта не может быть в будущем")
        LocalDate passportIssueDate,
        @NotNull(message = "Укажите пол")
        Gender gender,
        @NotNull(message = "Укажите семейное положение")
        MaritalStatus maritalStatus,
        @NotBlank(message = "Укажите адрес проживания")
        String residentialAddress,
        @NotBlank(message = "Укажите адрес регистрации")
        String registrationAddress,
        @NotBlank(message = "Укажите телефон")
        @Pattern(regexp = "\\+?[0-9 ()-]{10,32}", message = "Укажите корректный номер телефона")
        String phone,
        @NotNull(message = "Укажите дату начала работы")
        @PastOrPresent(message = "Дата начала работы не может быть в будущем")
        LocalDate employedFrom,
        @PastOrPresent(message = "Дата окончания работы не может быть в будущем")
        LocalDate employedTo,
        @NotBlank(message = "Укажите должность")
        @Size(max = 255, message = "Должность не должна превышать 255 символов")
        String position,
        @NotBlank(message = "Укажите название организации")
        @Size(max = 255, message = "Название организации не должно превышать 255 символов")
        String organizationName,
        @NotNull(message = "Укажите сумму кредита")
        @DecimalMin(value = "1000", message = "Сумма кредита должна быть не меньше 1 000 рублей")
        @Digits(integer = 13, fraction = 2, message = "Сумма кредита должна содержать не более 13 цифр и 2 знаков после запятой")
        BigDecimal requestedAmount,
        @NotBlank(message = "Укажите цель кредита")
        @Size(max = 500, message = "Цель кредита не должна превышать 500 символов")
        String purpose
) {
    @AssertTrue(message = "Дата окончания работы не может быть раньше даты начала")
    public boolean isEmploymentPeriodValid() {
        return employedFrom == null || employedTo == null || !employedTo.isBefore(employedFrom);
    }
}
