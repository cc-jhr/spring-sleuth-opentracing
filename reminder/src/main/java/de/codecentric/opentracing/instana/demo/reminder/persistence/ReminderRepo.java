package de.codecentric.opentracing.instana.demo.reminder.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Benjamin Wilms
 */
public interface ReminderRepo extends JpaRepository<ReminderEntity,Long> {
}
