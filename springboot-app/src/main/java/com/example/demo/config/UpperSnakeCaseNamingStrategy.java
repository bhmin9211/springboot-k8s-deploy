package com.example.demo.config;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

/**
 * Hibernate 커스텀 네이밍 전략
 * - 카멜케이스(CamelCase) → 스네이크케이스(SNAKE_CASE)
 * - 테이블/컬럼명을 모두 대문자 + 언더스코어로 변환
 * 예: createdAt → CREATED_AT
 */
public class UpperSnakeCaseNamingStrategy implements PhysicalNamingStrategy {

    @Override
    public Identifier toPhysicalCatalogName(Identifier name, JdbcEnvironment context) {
        return convertToUpperSnakeCase(name);
    }

    @Override
    public Identifier toPhysicalSchemaName(Identifier name, JdbcEnvironment context) {
        return name; // 또는 null 가능
    }

    @Override
    public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment context) {
        return convertToUpperSnakeCase(name);
    }

    @Override
    public Identifier toPhysicalSequenceName(Identifier name, JdbcEnvironment context) {
        return convertToUpperSnakeCase(name);
    }

    @Override
    public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment context) {
        return convertToUpperSnakeCase(name);
    }

    // 필요 없으면 제거 가능 (Hibernate 6에서 optional)
    @Override
    public Identifier toPhysicalTypeName(Identifier name, JdbcEnvironment context) {
        return name;
    }

    private Identifier convertToUpperSnakeCase(Identifier name) {
        if (name == null) return null;

        // createdAt → CREATED_AT
        String snake = name.getText()
                .replaceAll("([a-z])([A-Z])", "$1_$2")
                .toUpperCase();

        return Identifier.toIdentifier(snake);
    }
}
