package com.biblioteca.api.dto.response;

import java.time.Instant;

public record VisitaResponse(ObraResponse obra, Instant visitadoEm) {
}