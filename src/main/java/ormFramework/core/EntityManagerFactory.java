package ormFramework.core;

import ormFramework.annotation.Column;
import ormFramework.annotation.Entity;
import ormFramework.annotation.Id;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EntityManagerFactory {

    public static EntityManager create(
            String dbType,
            String host,
            int port,
            String user,
            String pass,
            String dbName,
            Class<?> mainClass
    ) throws SQLException, URISyntaxException, ClassNotFoundException {
        Connection connection = createConnection(dbType, host, port, user, pass, dbName);

        List<Class<?>> classes = getEntities(mainClass);

//        createTables(connection, classes);

        return new EntityManagerImpl(connection);
    }

    private static void createTables(Connection connection, List<Class<?>> classes) throws SQLException {
        for (Class classInfo : classes) {
            Entity entityInfo = (Entity) classInfo.getAnnotation(Entity.class);
            String sql = "CREATE TABLE ";

            String tableName = entityInfo.tableName();

            sql += tableName + " (\n";
            String primaryKeyDef = "";

            for (Field field : classInfo.getDeclaredFields()) {
                if (field.isAnnotationPresent(Id.class)) {
                    sql += "  " + field.getName() + " int auto_increment,\n";
                    primaryKeyDef = "constraint " + tableName + "_pk primary key (" + field.getName() + ")";
                } else if (field.isAnnotationPresent(Column.class)) {
                    Column columnInfo = field.getAnnotation(Column.class);
                    sql += "  " + columnInfo.name() + " " + columnInfo.columnDefinition() + ",\n";
                }
            }

            sql += "  " + primaryKeyDef + "\n);";

            System.out.println(sql);

            connection.createStatement().execute(sql);
        }
    }

    private static List<Class<?>> getEntities(Class<?> mainClass) throws URISyntaxException, ClassNotFoundException {
        String path = mainClass.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
        String packageName = mainClass.getPackageName();


        File rootDir = new File(path + packageName.replace(".", "/"));
        List<Class<?>> classes = new ArrayList<>();

        scanEntities(
                rootDir,
                packageName,
                classes
        );
        return classes;
    }

    private static Connection createConnection(String dbType, String host, int port, String user, String pass, String dbName) throws SQLException {
        Connection connection = DriverManager.getConnection(
                "jdbc:" + dbType + "://" + host + ":" + port + "/" + dbName,
                user,
                pass
        );
        return connection;
    }

    private static void scanEntities(File dir, String packageName, List<Class<?>> classes) throws ClassNotFoundException {
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                scanEntities(file, packageName + "." + file.getName(), classes);
            } else if (file.getName().endsWith(".class")) {
                Class<?> classInfo = Class.forName(packageName + "." + file.getName().replace(".class", ""));
                if (classInfo.isAnnotationPresent(Entity.class)) {
                    classes.add(classInfo);
                }
            }
        }
    }
}
