package com.example.sto_app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateDiagnosticsRequest {

    @NotBlank(message = "Описание диагностики не может быть пустым")
    @Size(max = 2000, message = "Описание диагностики не должно превышать 2000 символов")
    private String diagnosticsDescription;

    public UpdateDiagnosticsRequest() {
    }

    public String getDiagnosticsDescription() {
        return diagnosticsDescription;
    }

    public void setDiagnosticsDescription(String diagnosticsDescription) {
        this.diagnosticsDescription = diagnosticsDescription;
    }
}