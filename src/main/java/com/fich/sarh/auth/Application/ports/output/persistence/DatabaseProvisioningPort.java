package com.fich.sarh.auth.Application.ports.output.persistence;

public interface DatabaseProvisioningPort {

    boolean databaseExists(String dbName);

    void createDatabase(String dbName);
}
