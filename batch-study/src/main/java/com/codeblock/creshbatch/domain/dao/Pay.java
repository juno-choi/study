package com.codeblock.creshbatch.domain.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@ToString
@Getter
@Setter
@NoArgsConstructor
public class Pay {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");

    private Long id;
    private Long amount;
    private String txName;
    private String txDateTime;
    private String successState;

    public Pay(Long amount, String txName, String txDateTime) {
        this.amount = amount;
        this.txName = txName;
        this.txDateTime = txDateTime;
    }

    public Pay(Long id, Long amount, String txName, String txDateTime) {
        this.id = id;
        this.amount = amount;
        this.txName = txName;
        this.txDateTime = txDateTime;
    }

    public Pay(Long amount, String txName, String txDateTime, String successState) {
        this.amount = amount;
        this.txName = txName;
        this.txDateTime = txDateTime;
        this.successState = successState;
    }
    public Pay(Long id, Long amount, String txName, String txDateTime, String successState) {
        this.id = id;
        this.amount = amount;
        this.txName = txName;
        this.txDateTime = txDateTime;
        this.successState = successState;
    }
}
