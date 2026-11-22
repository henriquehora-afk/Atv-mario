package com.biblioteca.api.model;

import java.time.Instant;

public record VisitaHistorico(Obra obra, Instant visitadoEm) {
}