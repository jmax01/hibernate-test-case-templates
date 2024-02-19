package org.hibernate.bugs;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.SecondaryTable;
import jakarta.persistence.Table;
import org.hibernate.annotations.NaturalId;

/**
 * The test entity with secondary table.
 */

@Entity(name = "test_entity")
@Table(name = "test_entity")
@SecondaryTable(name = "test_entity_foo_bar_vw", pkJoinColumns = @PrimaryKeyJoinColumn(name = "test_entity_id"))
public class TestEntity {

    @Id
    @GeneratedValue
    @Column(name = "test_entity_id", nullable = false, unique = true)
    UUID testEntityId;

    @Column(name = "foo_or_bar", table = "test_entity_foo_bar_vw", nullable = true, unique = false, insertable = false,
        updatable = false)
    String fooOrBar;

    @NaturalId(mutable = false)
    @Column(name = "business_key", length = 50, nullable = false, unique = true)
    String businessKey;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_entity_id", referencedColumnName = "test_entity_id")
    ChildTestEntity childTestEntity;

    /**
     * Gets the test entity id.
     *
     * @return the test entity id
     */
    public UUID getTestEntityId() {
        return this.testEntityId;
    }

    /**
     * Sets the test entity id.
     *
     * @param testEntityId the new test entity id
     */
    public void setTestEntityId(final UUID testEntityId) {
        this.testEntityId = testEntityId;
    }

    /**
     * Gets the foo or bar.
     *
     * @return the foo or bar
     */
    public String getFooOrBar() {
        return this.fooOrBar;
    }

    /**
     * Sets the foo or bar.
     *
     * @param fooOrBar the new foo or bar
     */
    protected void setFooOrBar(final String fooOrBar) {
        this.fooOrBar = fooOrBar;
    }

    /**
     * Gets the business key.
     *
     * @return the business key
     */
    public String getBusinessKey() {
        return this.businessKey;
    }

    /**
     * Sets the business key.
     *
     * @param businessKey the new business key
     */
    public void setBusinessKey(final String businessKey) {
        this.businessKey = businessKey;
    }

    /**
     * Gets the child test entity.
     *
     * @return the child test entity
     */
    public ChildTestEntity getChildTestEntity() {
        return this.childTestEntity;
    }

    /**
     * Sets the child test entity.
     *
     * @param childTestEntity the new child test entity
     */
    public void setChildTestEntity(final ChildTestEntity childTestEntity) {
        this.childTestEntity = childTestEntity;
    }

}
