package org.hibernate.bugs;

import static org.junit.Assert.*;

import jakarta.persistence.SecondaryTable;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.junit.Test;

/**
 * When a {@link SecondaryTable} is a view eager loading via native query fails
 * <p>
 * This tests when the {@link SecondaryTable} is eagerly loaded
 */
public class SecondaryTableEagerLoadTest extends BaseSecondaryTableNativeQueryTest {

    /**
     * In this case we call a view that has all the test_entity columns and 'bar'
     * <p>
     * <code>
     * org.hibernate.sql.ast.tree.from.UnknownTableReferenceException: Unable to determine TableReference
     * (`test_entity_foo_bar_vw`) for `org.hibernate.bugs.TestEntity(test_entity).bar`
     * </code>
     */
    @SuppressWarnings("resource")
    @Test
    public void viewOfEntityThatAddsTheBarColumnAllPropertiesShorthand() {
        var transaction = this.session.getTransaction();
        transaction.begin();

        try {

            // @formatter:off
            var sqlString =
                    "SELECT "
                    + "   {test_entity.*}, "
                    + "   {child_test_entity.*} "
                    + " FROM "
                    + "   test_entity_vw as test_entity "
                    + " JOIN "
                    + "   child_test_entity child_test_entity ON (test_entity.test_entity_id = child_test_entity.test_entity_id)";
            // @formatter:on

            var query = this.session.unwrap(Session.class)
                    .<TestEntity>createNativeQuery(sqlString, TestEntity.class, "test_entity")
                    .addJoin("child_test_entity", "test_entity", "childTestEntity");

            var testEntities = query.getResultList();

            assertFalse(testEntities.isEmpty());

        }
        catch (Exception e) {
            throw e;
        }
        finally {
            transaction.commit();
        }

    }

    /**
     * In this example we use the all properties shorthand and supply the foo_or_bar value from the
     * test_entity_foo_bar_vw and alias it as the property name
     *
     * <p>
     * <code>
     * org.hibernate.exception.SQLGrammarException: could not prepare statement [Column "TEST_ENTITY.FOO_OR_BAR" not
     * found;
     * SQL statement:
     * SELECT
     * test_entity.test_entity_id test_ent1_1_0_,
     * test_entity.business_key business2_1_0_,
     * test_entity.foo_or_bar foo_or_b1_2_0_,
     * child_test_entity.child_test_entity_id child_te1_0_1_,
     * child_test_entity.child_business_key child_bu2_0_1_,
     * test_entity_foo_bar_vw.foo_or_bar AS fooOrBar
     * FROM
     * test_entity test_entity
     * JOIN
     * test_entity_foo_bar_vw test_entity_foo_bar_vw ON (test_entity.test_entity_id =
     * test_entity_foo_bar_vw.test_entity_id)
     * JOIN
     * child_test_entity child_test_entity ON (test_entity.test_entity_id = child_test_entity.test_entity_id)
     * </code>
     *
     * @see <a
     *      href="https://discourse.hibernate.org/t/how-do-execute-a-native-query-for-an-entity-that-has-a-property-populated-via-secondarytable/9016/4">Discussion</a>
     */
    @SuppressWarnings("resource")
    @Test
    public void fooOrBarAsIndividualColumnAllPropertiesShorthand() {

        var transaction = this.session.getTransaction();
        transaction.begin();

        try {

            // @formatter:off
            var sqlString =
                    "SELECT "
                    + "   {test_entity.*}, "
                    + "   {child_test_entity.*},"
                    + "   test_entity_foo_bar_vw.foo_or_bar AS fooOrBar "
                    + " FROM "
                    + "   test_entity test_entity "
                    + " JOIN "
                    + "   test_entity_foo_bar_vw test_entity_foo_bar_vw ON (test_entity.test_entity_id = test_entity_foo_bar_vw.test_entity_id) "
                    + " JOIN "
                    + "   child_test_entity child_test_entity ON (test_entity.test_entity_id = child_test_entity.test_entity_id) ";
            // @formatter:on

            var query = this.session.unwrap(Session.class)
                    .<TestEntity>createNativeQuery(sqlString, TestEntity.class, "test_entity")
                    .addJoin("child_test_entity", "test_entity", "childTestEntity");

            var testEntities = query.getResultList();

            assertFalse(testEntities.isEmpty());
        }
        catch (Exception e) {
            throw e;
        }
        finally {
            transaction.commit();
        }

    }

    /**
     * In this example we use the all properties shorthand and supply the foo_or_bar value from the
     * test_entity_foo_bar_vw and assign it as a tenant_entity property
     *
     * <p>
     * <code>
     * could not prepare statement [Column "TEST_ENTITY.FOO_OR_BAR" not found; SQL statement:
     * SELECT
     * test_entity.test_entity_id test_ent1_1_0_,
     * test_entity.business_key business2_1_0_,
     * test_entity.foo_or_bar foo_or_b1_2_0_,
     * child_test_entity.child_test_entity_id child_te1_0_1_,
     * child_test_entity.child_business_key
     * child_bu2_0_1_,
     * test_entity_foo_bar_vw.foo_or_bar AS fooOrBar
     * FROM
     * test_entity test_entity
     * JOIN
     * test_entity_foo_bar_vw test_entity_foo_bar_vw ON (test_entity.test_entity_id =
     * test_entity_foo_bar_vw.test_entity_id)
     * JOIN
     * child_test_entity child_test_entity ON (test_entity.test_entity_id = child_test_entity.test_entity_id)
     * </code>
     *
     * @see <a
     *      href="https://discourse.hibernate.org/t/how-do-execute-a-native-query-for-an-entity-that-has-a-property-populated-via-secondarytable/9016/4">Discussion</a>
     */
    @SuppressWarnings("resource")
    @Test
    public void fooOrBarAsIndividualPropertyAllPropertiesShorthand() {

        var transaction = this.session.getTransaction();
        transaction.begin();

        // @formatter:off
        var sqlString =
                "SELECT "
                + "   {test_entity.*}, "
                + "   {child_test_entity.*}, "
                + "   test_entity_foo_bar_vw.foo_or_bar AS {test_entity.fooOrBar} "
                + " FROM "
                + "   test_entity test_entity "
                + " JOIN "
                + "   test_entity_foo_bar_vw test_entity_foo_bar_vw ON (test_entity.test_entity_id = test_entity_foo_bar_vw.test_entity_id) "
                + " JOIN "
                + "   child_test_entity child_test_entity ON (test_entity.test_entity_id = child_test_entity.test_entity_id) ";
        // @formatter:on

        try {

            var query = this.session.unwrap(Session.class)
                    .<TestEntity>createNativeQuery(sqlString, TestEntity.class, "test_entity")
                    .addJoin("child_test_entity", "test_entity", "childTestEntity");

            var testEntities = query.getResultList();

            assertFalse(testEntities.isEmpty());
        }
        catch (Exception e) {
            throw e;
        }
        finally {
            transaction.commit();
        }

    }

    /**
     * In this case we call a view that has all the test_entity columns and 'bar'
     * and we define each property and use {@link NativeQuery#addJoin(String, String, String)} to join the entities
     * <p>
     * <code>
     * org.hibernate.sql.ast.tree.from.UnknownTableReferenceException: Unable to determine TableReference
     * (`test_entity_foo_bar_vw`) for `org.hibernate.bugs.TestEntity(test_entity).bar`
     * </code>
     */
    @SuppressWarnings("resource")
    @Test
    public void viewOfEntityThatAddsTheBarExplicitPropertiesWithAddJoin() {
        var transaction = this.session.getTransaction();
        transaction.begin();

        try {
            // @formatter:off
            var sqlString =
                    "SELECT "
                    + "   test_entity.test_entity_id AS {test_entity.testEntityId}, "
                    + "   test_entity.business_key AS {test_entity.businessKey}, "
                    + "   test_entity.foo_or_bar AS {test_entity.fooOrBar}, "
                    + "   child_test_entity.child_test_entity_id AS {child_test_entity.childTestEntityId}, "
                    + "   child_test_entity.child_business_key AS {child_test_entity.childBusinessKey} "
                    + " FROM "
                    + "   test_entity_vw test_entity "
                    + " JOIN "
                    + "   child_test_entity child_test_entity ON (test_entity.test_entity_id = child_test_entity.test_entity_id) ";
            // @formatter:on

            var query = this.session.unwrap(Session.class)
                    .<TestEntity>createNativeQuery(sqlString, TestEntity.class, "test_entity")
                    .addJoin("child_test_entity", "test_entity", "childTestEntity");

            var testEntities = query.getResultList();

            assertFalse(testEntities.isEmpty());

        }
        catch (Exception e) {
            throw e;
        }
        finally {
            transaction.commit();
        }

    }

    /**
     * In this case we call a view that has all the test_entity columns and 'bar'
     * and we define each property and use {@link NativeQuery#addEntity(String, Class)} to join the entities
     * <p>
     * <code>
     * org.hibernate.sql.ast.tree.from.UnknownTableReferenceException: Unable to determine TableReference
     * (`test_entity_foo_bar_vw`) for `org.hibernate.bugs.TestEntity(test_entity).bar`
     * </code>
     */
    @SuppressWarnings("resource")
    @Test
    public void viewOfEntityThatAddsTheBarExplicitPropertiesWithAddEntity() {
        var transaction = this.session.getTransaction();
        transaction.begin();

        try {

            // @formatter:off
            var sqlString =
                    "SELECT "
                    + "   test_entity.test_entity_id AS {test_entity.testEntityId}, "
                    + "   test_entity.business_key AS {test_entity.businessKey}, "
                    + "   test_entity.foo_or_bar AS {test_entity.fooOrBar}, "
                    + "   child_test_entity.child_test_entity_id AS {child_test_entity.childTestEntityId},  "
                    + "   child_test_entity.child_business_key AS {child_test_entity.childBusinessKey} "
                    + " FROM "
                    + "   test_entity_vw test_entity "
                    + " JOIN "
                    + "   child_test_entity child_test_entity ON (test_entity.test_entity_id = child_test_entity.test_entity_id) ";
            // @formatter:on

            var query = this.session.unwrap(Session.class)
                    .<TestEntity>createNativeQuery(sqlString, TestEntity.class, "test_entity")
                    .addEntity("child_test_entity", ChildTestEntity.class);

            var testEntities = query.getResultList();

            assertFalse(testEntities.isEmpty());

        }
        catch (Exception e) {
            throw e;
        }
        finally {
            transaction.commit();
        }

    }

    /**
     * In this case the source of fooOrBar is test_entity_foo_bar_vw
     * and we define each property and use {@link NativeQuery#addEntity(String, Class)} to join the entities
     * <p>
     * <code>
     * org.hibernate.sql.ast.tree.from.UnknownTableReferenceException: Unable to determine TableReference
     * (`test_entity_foo_bar_vw`) for `org.hibernate.bugs.TestEntity(test_entity).bar`
     * </code>
     */
    @SuppressWarnings("resource")
    @Test
    public void addFooBarViewExplicitPropertiesWithAddEntity() {
        var transaction = this.session.getTransaction();
        transaction.begin();

        try {

            // @formatter:off
            var sqlString =
                    "SELECT "
                    + "   test_entity.test_entity_id AS {test_entity.testEntityId}, "
                    + "   test_entity.business_key AS {test_entity.businessKey}, "
                    + "   child_test_entity.child_test_entity_id AS {child_test_entity.childTestEntityId},  "
                    + "   child_test_entity.child_business_key AS {child_test_entity.childBusinessKey}, "
                    + "   test_entity_foo_bar_vw.foo_or_bar AS {test_entity.fooOrBar} "
                    + " FROM "
                    + "   test_entity_vw test_entity "
                    + " JOIN "
                    + "   child_test_entity child_test_entity ON (test_entity.test_entity_id = child_test_entity.test_entity_id) "
                    + " JOIN "
                    + "  test_entity_foo_bar_vw test_entity_foo_bar_vw ON (test_entity.test_entity_id = test_entity_foo_bar_vw.test_entity_id) ";
            // @formatter:on

            var query = this.session.unwrap(Session.class)
                    .<TestEntity>createNativeQuery(sqlString, TestEntity.class, "test_entity")
                    .addEntity("child_test_entity", ChildTestEntity.class);

            var testEntities = query.getResultList();

            assertFalse(testEntities.isEmpty());

        }
        catch (Exception e) {
            throw e;
        }
        finally {
            transaction.commit();
        }

    }
}
