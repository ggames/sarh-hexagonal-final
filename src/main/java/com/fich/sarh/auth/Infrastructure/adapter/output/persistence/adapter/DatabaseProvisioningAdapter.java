package com.fich.sarh.auth.Infrastructure.adapter.output.persistence.adapter;

import com.fich.sarh.auth.Application.ports.output.persistence.DatabaseProvisioningPort;
import com.fich.sarh.common.WebAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;

@WebAdapter
@RequiredArgsConstructor
public class DatabaseProvisioningAdapter implements DatabaseProvisioningPort {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public boolean databaseExists(String dbName) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = ?",
                Integer.class,
                dbName
        );
        return count != null && count > 0;
    }

    @Override
    public void createDatabase(String dbName) {
        jdbcTemplate.execute("CREATE DATABASE " + dbName);
    }
}
