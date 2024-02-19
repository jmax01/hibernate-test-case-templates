package org.hibernate.bugs;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.NaturalId;

/**
 * Child of {@link TestEntity}
 */
@Entity(name = "child_test_entity")
@Table(name = "child_test_entity")
public class ChildTestEntity {

    @Id
    @GeneratedValue
    @Column(name = "child_test_entity_id", nullable = false, unique = true)
    UUID childTestEntityId;

    @OneToOne(mappedBy = "childTestEntity")
    TestEntity testEntity;

    @NaturalId(mutable = false)
    @Column(name = "child_business_key", length = 50, nullable = false, unique = true)
    String childBusinessKey;

    /**
     * Gets the child test entity id.
     *
     * @return the child test entity id
     */
    public UUID getChildTestEntityId() {
        return this.childTestEntityId;
    }

    /**
     * Sets the child test entity id.
     *
     * @param childTestEntityId the new child test entity id
     */
    public void setChildTestEntityId(final UUID childTestEntityId) {
        this.childTestEntityId = childTestEntityId;
    }

    /**
     * Gets the test entity.
     *
     * @return the test entity
     */
    public TestEntity getTestEntity() {
        return this.testEntity;
    }

    /**
     * Sets the test entity.
     *
     * @param testEntity the new test entity
     */
    public void setTestEntity(final TestEntity testEntity) {
        this.testEntity = testEntity;
    }

    /**
     * Gets the child business key.
     *
     * @return the child business key
     */
    public String getChildBusinessKey() {
        return this.childBusinessKey;
    }

    /**
     * Sets the child business key.
     *
     * @param childBusinessKey the new child business key
     */
    public void setChildBusinessKey(final String childBusinessKey) {
        this.childBusinessKey = childBusinessKey;
    }
}
