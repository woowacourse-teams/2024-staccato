package com.staccato.util;

import java.util.List;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DatabaseCleaner {
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void cleanUp() {
        entityManager.flush();
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();
        for (String tableName : getTableNames()) {
            entityManager.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();
            entityManager.createNativeQuery("ALTER TABLE " + tableName + " ALTER COLUMN ID RESTART WITH 1")
                    .executeUpdate();
        }
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
    }

    private List<String> getTableNames() {
        return entityManager.getMetamodel().getEntities().stream()
                .filter(entity -> entity.getJavaType().getAnnotation(Entity.class) != null)
                .map(entityType -> camelToSnake(entityType.getName()))
                .toList();
    }

    private String camelToSnake(String camel) {
        StringBuilder snake = new StringBuilder();
        for (char c : camel.toCharArray()) {
            if (Character.isUpperCase(c)) {
                snake.append("_");
            }
            snake.append(Character.toLowerCase(c));
        }
        if (snake.charAt(0) == '_') {
            snake.deleteCharAt(0);
        }
        return snake.toString();
    }
}
