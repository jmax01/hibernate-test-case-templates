package org.hibernate.bugs;

import static org.junit.Assert.*;

import java.util.UUID;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;

/**
 * Base class for secondary table native query tests.
 */
@SuppressWarnings({ "resource" })
public class BaseSecondaryTableNativeQueryTest {

    /** The Constant CREATE_TABLE. */
    // @formatter:off
    protected static final String CREATE_TEST_ENTITY_TABLE
            = "CREATE TABLE IF NOT EXISTS test_entity ( "
            + "  test_entity_id UUID DEFAULT RANDOM_UUID(), "
            + "  business_key VARCHAR(50) NOT NULL,"
            + "  UNIQUE(business_key),"
            + "  PRIMARY KEY (test_entity_id));";
    // @formatter:on

    /** The Constant CREATE_CHILD_TABLE. */
    // @formatter:off
    protected static final String CREATE_CHILD_TEST_ENTITY_TABLE
            = "CREATE TABLE IF NOT EXISTS child_test_entity ( "
            + "  child_test_entity_id UUID DEFAULT RANDOM_UUID(), "
            + "  test_entity_id UUID NOT NULL, "
            + "  child_business_key VARCHAR(50) NOT NULL, "
            + "  UNIQUE(child_business_key), "
            + "  PRIMARY KEY (child_test_entity_id));";
    // @formatter:on

    /** The Constant CREATE_FOO_BAR_VIEW. */
    protected static final String CREATE_FOO_BAR_VIEW
            = "CREATE VIEW IF NOT EXISTS test_entity_foo_bar_vw AS SELECT test_entity_id, 'foo' AS foo_or_bar FROM test_entity";

    /** The Constant CREATE_TABLE_VIEW. */
    protected static final String CREATE_TABLE_VIEW
            = "CREATE VIEW IF NOT EXISTS test_entity_vw AS SELECT test_entity_id, business_key, 'foo' AS foo_or_bar FROM test_entity";

    /** The Constant INSERT. */
    protected static final String INSERT = "INSERT INTO test_entity (business_key) VALUES ('bk1');";

    /** The entity manager factory. */
    protected EntityManagerFactory entityManagerFactory;

    /** The session. */
    protected Session session;

    /**
     * Inits the.
     */

    @Before
    public void init() {

        this.entityManagerFactory = Persistence.createEntityManagerFactory("templatePU");

        this.session = this.entityManagerFactory.createEntityManager()
                .unwrap(Session.class);

        this.session.getTransaction()
                .begin();

        this.session.createNativeQuery(CREATE_TEST_ENTITY_TABLE, Integer.class)
                .executeUpdate();

        this.session.createNativeQuery(CREATE_CHILD_TEST_ENTITY_TABLE, Integer.class)
                .executeUpdate();

        this.session.createNativeQuery(CREATE_FOO_BAR_VIEW, Integer.class)
                .executeUpdate();

        this.session.createNativeQuery(CREATE_TABLE_VIEW, Integer.class)
                .executeUpdate();

        this.session.getTransaction()
                .commit();

        this.session.getTransaction()
                .begin();

        this.session.createNativeQuery(INSERT, Integer.class)
                .executeUpdate();

        var tableTestEntityId
                = this.session.createNativeQuery("SELECT test_entity.test_entity_id FROM test_entity", UUID.class)
                        .uniqueResult();

        this.session
                .createNativeQuery("INSERT INTO child_test_entity " + "(test_entity_id,child_business_key) VALUES ('"
                        + tableTestEntityId + "','cbk1');",
                    Integer.class)
                .executeUpdate();

        assertNotNull("tableTestEntityId is null", tableTestEntityId);

        var tableBusinessKey
                = this.session.createNativeQuery("SELECT test_entity.business_key FROM test_entity", String.class)
                        .getSingleResult();

        assertEquals("tableBusinessKey is bk1", "bk1", tableBusinessKey);

        var viewTestEntityId = this.session
                .createNativeQuery("SELECT test_entity_foo_bar_vw.test_entity_id FROM test_entity_foo_bar_vw",
                    UUID.class)
                .getSingleResult();

        assertEquals("viewTestEntityId equals tableTestEntityId", tableTestEntityId, viewTestEntityId);

        var viewFooOrBar = this.session
                .createNativeQuery("SELECT test_entity_foo_bar_vw.foo_or_bar FROM test_entity_foo_bar_vw", String.class)
                .getSingleResult();

        assertEquals("viewFooOrBar equals 'foo'", viewFooOrBar, "foo");
        this.session.getTransaction()
                .commit();

    }

    /**
     * Destroy.
     */
    @After
    public void destroy() {

        this.session.getTransaction()
                .begin();

        this.session.createNativeQuery("DELETE FROM test_entity", Integer.class)
                .executeUpdate();

        this.session.createNativeQuery("DELETE FROM child_test_entity", Integer.class)
                .executeUpdate();

        this.session.getTransaction()
                .commit();

        this.session.close();
        this.entityManagerFactory.close();
    }

}
