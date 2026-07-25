/**
 * Notification module — manages user notifications (ORDER_UPDATE, PROMO, SYSTEM, etc.).
 *
 * <p>Architecture: Modulithic slice.</p>
 *
 * <ul>
 *   <li>{@code api} — REST controllers</li>
 *   <li>{@code application.service} — business logic</li>
 *   <li>{@code domain.model} — JPA entities</li>
 *   <li>{@code domain.repository} — Spring Data repositories</li>
 * </ul>
 */
package com.kaldar.kaldar.notification;
