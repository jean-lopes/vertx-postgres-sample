create table search_history (
    id uuid not null primary key,
    cpf char(14) not null,
    searched_on timestamp not null
);

create table transaction_history (
    id uuid not null primary key,
    cpf char(14) not null,
    kind char(2) not null,
    processed_on timestamp not null
);

create index ix_sh_cpf
    on search_history(cpf);
    
create index ix_sh_searched_on
    on search_history(searched_on desc);

create index ix_th_cpf
    on transaction_history(cpf);
    
create index ix_th_cpf_kind
    on transaction_history(cpf, kind);
    
create index ix_th_cpf_processed_on
    on transaction_history(cpf, processed_on desc);