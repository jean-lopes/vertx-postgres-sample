create extension if not exists "uuid-ossp";

INSERT INTO search_history(id, cpf, searched_on)
    SELECT uuid_generate_v4(), '1', NOW() + (random() * (NOW()+'90 days' - NOW())) + '30 days'
      FROM generate_series(1, 100);
                                                                                   
INSERT INTO transaction_history(id, cpf, kind, processed_on)
    SELECT uuid_generate_v4(), '1', (ARRAY['CA','DE','CR'])[floor(random()*3)+1], NOW() + (random() * (NOW()+'90 days' - NOW())) + '30 days'
      FROM generate_series(1, 100);
                                                                                                       
INSERT INTO search_history(id, cpf, searched_on)
    SELECT uuid_generate_v4(), left(md5(random()::text), 14), NOW() + (random() * (NOW()+'90 days' - NOW())) + '30 days'
      FROM generate_series(1, 100);

INSERT INTO transaction_history(id, cpf, kind, processed_on)
    SELECT uuid_generate_v4(), left(md5(random()::text), 14), (ARRAY['CA','DE','CR'])[floor(random()*3)+1], NOW() + (random() * (NOW()+'90 days' - NOW())) + '30 days'
      FROM generate_series(1, 100);