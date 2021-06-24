package bg.codexio.customOrmDemo;


import bg.codexio.customOrmDemo.entity.Address;
import bg.codexio.customOrmDemo.entity.Department;
import bg.codexio.customOrmDemo.entity.Employee;
import ormFramework.core.EntityManager;
import ormFramework.core.EntityManagerFactory;

import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 1) Класче (EntityManagerFactory), в което да регистрираме настройките за връзка с базата данни
 * - вид база, потребителско име
 и парола, както и името на базата данни. Това класче в ответ ще върне обект, който ще може да управлява
 на абстрактно ниво базата данни.
 *
 * 2) Преди да се върне такъв обект, искаме да обиколим всички класове в проекта и да намерим тези, които са
 анотирани с анотацията @Entity (това ще е наша анотация)

   3) Ще гледаме дали такава таблица има в базата, ако няма - ще я създадем по предварително описани @Id
 и @Column(име, тип)

   4) Класчето, което ще се върне от т.1. (EntityManager) ще има операции за извличане на обект по ИД
 find(id, Class); записване на нов или редакция на съществуващ обект persist(Object) и изтриване на обект
 по ИД delete(id, Class)
 */
public class ApplicationStarter {
    public static void main(String[] args) throws SQLException, URISyntaxException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        EntityManager entityManager = EntityManagerFactory.create(
                "mysql",
                "localhost",
                3306,
                "root",
                "",
                "test_orm",
                ApplicationStarter.class
        );

        Employee byId = entityManager.findById(25, Employee.class);

        Address softUniAddress = entityManager.findById(1, Address.class);
        Address codexioAddress = entityManager.findById(2, Address.class);
//        Department byId1 = entityManager.findById(30, Department.class);


    }
}
