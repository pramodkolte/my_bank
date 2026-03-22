package com.mybank.audit.domain.port.out;

import com.mybank.audit.domain.model.AuditRecord;

public interface AuditStoragePort {
    void save(AuditRecord record);
}
