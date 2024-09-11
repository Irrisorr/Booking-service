CREATE TABLE booking (
                         id SERIAL PRIMARY KEY,
                         user_id BIGINT NOT NULL,
                         provider_id BIGINT NOT NULL,
                         service_id BIGINT NOT NULL,
                         visit_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                         status VARCHAR(20) NOT NULL
);