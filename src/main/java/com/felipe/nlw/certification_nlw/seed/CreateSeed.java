package com.felipe.nlw.certification_nlw.seed;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @Note Classe criada para gerar inserção de dados no BD sem manipulação direta do SQL no Banco de Dados
 * */
public class CreateSeed {

    private final JdbcTemplate template;

    public CreateSeed(DataSource dataSource){
        this.template = new JdbcTemplate(dataSource);
    }

    public static void main(String[] args) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://localhost:5434/pg_nlw");
        dataSource.setUsername("admin");
        dataSource.setPassword("admin");

        CreateSeed createSeed = new CreateSeed(dataSource);
        createSeed.run(args);
    }

    public void run(String ...args){
        executeSQLFile("src/main/resources/create.sql");
    }

    private void executeSQLFile(String filePath) {
        try {
            String sqlScript = new String(Files.readAllBytes(Paths.get(filePath)));
            template.execute(sqlScript);
            System.out.println("Seed realizado com sucesso.");
        } catch (IOException e) {
            System.out.println("Erro ao executar arquivo: " + e.getMessage());
        }
    }
}


