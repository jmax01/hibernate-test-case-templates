package org.hibernate.bugs;

import static org.junit.Assert.*;

import jakarta.persistence.SecondaryTable;
import org.hibernate.Session;
import org.junit.Test;

/**
 * When a {@link SecondaryTable} is a view eager loading via native query fails
 * <p>
 * This tests when the {@link SecondaryTable} is lazy loaded via byte code enhancement
 */
public class SecondaryTableLazyLoadedTest extends BaseSecondaryTableNativeQueryTest {

    /**
     * This is what we would expect to do with lazy loading
     * <p>
     * <code>
     * org.hibernate.sql.ast.tree.from.UnknownTableReferenceException: Unable to determine TableReference
     * (`test_entity_foo_bar_vw`) for `org.hibernate.bugs.TestEntityLazyLoadBar(test_entity).bar`
     * </code>
     */
    @SuppressWarnings("resource")
    @Test
    public void idiomaticCase() {

        var transaction = this.session.getTransaction();
        transaction.begin();

        try {

            // @formatter:off
            var sqlString =
                    "SELECT"
                    + "  {test_entity.*}, "
                    + "  {child_test_entity.*} "
                    + " FROM "
                    + "  test_entity_vw as test_entity "
                    + " JOIN "
                    + "  child_test_entity child_test_entity ON (test_entity.test_entity_id = child_test_entity.test_entity_id)";
            // @formatter:on

            var query = this.session.unwrap(Session.class)
                    .<TestEntityLazyLoadBar>createNativeQuery(sqlString, TestEntityLazyLoadBar.class, "test_entity")
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
     * In this example we supply the 'bar' as an individual column even though it is lazy loaded
     * <p>
     * <code>
     * org.hibernate.sql.ast.tree.from.UnknownTableReferenceException: Unable to determine TableReference
     * (`test_entity_foo_bar_vw`) for `org.hibernate.bugs.TestEntityLazyLoadBar(test_entity).bar`
     * </code>
     *
     * @see <a
     *      href="https://discourse.hibernate.org/t/how-do-execute-a-native-query-for-an-entity-that-has-a-property-populated-via-secondarytable/9016/4">Discussion</a>
     *
     */
    @SuppressWarnings("resource")
    @Test
    public void fooOrBarAsIndividualColumn() {

        var transaction = this.session.getTransaction();
        transaction.begin();

        try {

            // @formatter:off
            var sqlString =
                    "SELECT"
                    + "   {test_entity.*}, "
                    + "   {child_test_entity.*}, "
                    + "   test_entity_foo_bar_vw.foo_or_bar AS fooOrBar"
                    + " FROM "
                    + "   test_entity as test_entity "
                    + " JOIN "
                    + "   child_test_entity child_test_entity ON (test_entity.test_entity_id = child_test_entity.test_entity_id) "
                    + " JOIN "
                    + "   test_entity_foo_bar_vw test_entity_foo_bar_vw ON (test_entity.test_entity_id = test_entity_foo_bar_vw.test_entity_id)";
            // @formatter:on

            var query = this.session.unwrap(Session.class)
                    .<TestEntityLazyLoadBar>createNativeQuery(sqlString, TestEntityLazyLoadBar.class, "test_entity")
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
     * In this case we call a view that has all the test_entity columns and adds 'bar'
     * <p>
     * <code>
     * org.hibernate.sql.ast.tree.from.UnknownTableReferenceException: Unable to determine TableReference
     * (`test_entity_foo_bar_vw`) for `org.hibernate.bugs.TestEntityLazyLoadBar(test_entity).bar`
     * </code>
     */
    @SuppressWarnings("resource")
    @Test
    public void viewOfEntityThatAddsTheBarColumn() {
        var transaction = this.session.getTransaction();
        transaction.begin();

        try {

            // @formatter:off
            var sqlString =
                    "SELECT"
                    + "  {test_entity.*}, "
                    + "  {child_test_entity.*} "
                    + " FROM "
                    + "  test_entity_vw as test_entity "
                    + " JOIN "
                    + "  child_test_entity child_test_entity ON (test_entity.test_entity_id = child_test_entity.test_entity_id) ";
            // @formatter:on

            var query = this.session.unwrap(Session.class)
                    .<TestEntityLazyLoadBar>createNativeQuery(sqlString, TestEntityLazyLoadBar.class, "test_entity")
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
}
