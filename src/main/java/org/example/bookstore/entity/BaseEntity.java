package org.example.bookstore.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@ToString
@Getter
@Setter
@Audited
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    protected ZonedDateTime createdAt;
    @Column(nullable = false)
    @UpdateTimestamp
    protected ZonedDateTime updatedAt;
    @CreatedBy
    @Column(name = "created_by", nullable = false, updatable = false)
    protected String createdBy;
    @LastModifiedBy
    @Column(name = "updated_by", nullable = false)
    protected String updatedBy;

}
