package me.staek.shop.domain;

import jakarta.persistence.MappedSuperclass;

import java.time.LocalDateTime;

@MappedSuperclass
    public abstract class BaseEntity {

        private String createdBy;
        private LocalDateTime careteDTM;

    private String lastModifiedBy;
    private LocalDateTime lastModifiedDTM;

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCareteDTM() {
        return careteDTM;
    }

    public void setCareteDTM(LocalDateTime careteDTM) {
        this.careteDTM = careteDTM;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public LocalDateTime getLastModifiedDTM() {
        return lastModifiedDTM;
    }

    public void setLastModifiedDTM(LocalDateTime lastModifiedDTM) {
        this.lastModifiedDTM = lastModifiedDTM;
    }
}
